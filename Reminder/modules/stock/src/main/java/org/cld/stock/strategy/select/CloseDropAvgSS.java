package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cld.stock.CandleQuote;
import org.cld.stock.DivSplit;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DateTimeUtil;
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CloseDropAvgSS extends SelectStrategy {

	public static final int LOOKUP_DAYS=20;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public CloseDropAvgSS(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public JDBCMapper[] getTableMappers() {
		return new JDBCMapper[]{sc.getFQDailyQuoteTableMapper(), sc.getExDivSplitHistoryTableMapper()};
	}
	
	//use the open of submit day and submit at the open, table results are per stock
	@JsonIgnore
	@Override
	public List<SelectCandidateResult> getSelectCandidate(Map<JDBCMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		float threashold = ((Double)params[0]).floatValue();//drop percentage 5 mean 5%
		float openUpRatio = ((Double)params[1]).floatValue();//the limit price ratio submit to open price -1 mean -1%
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> lxd = tableResults.get(sc.getExDivSplitHistoryTableMapper());
		Set<Date> lxdds = new HashSet<Date>();
		for (Object o:lxd){
			DivSplit ds = (DivSplit)o;
			lxdds.add(ds.getDt());
		}
		List<Object> lo = tableResults.get(sc.getFQDailyQuoteTableMapper());
		for (int i=lo.size()-1; i>=0; i--){
			CandleQuote submitCq = (CandleQuote) lo.get(i);
			if (lxdds.contains(submitCq.getStartTime())){//skip ex days
				continue;
			}
			CandleQuote thisCq = submitCq;
			CandleQuote prevCq = null;
			CandleQuote oneDayBeforeSumbitCq = null;
			if (i-1>=0){
				oneDayBeforeSumbitCq = (CandleQuote) lo.get(i-1);
				float high=0;
				int days=0;//include the submit day
				if (checkValid(oneDayBeforeSumbitCq)){
					for (int j=i-1; j>0; j--){
						prevCq = (CandleQuote)lo.get(j);
						if (j==i-1){
							//for the day before submit day, i need to make sure the open of submit day is lower then the close of prev day
							if (thisCq.getOpen()>=prevCq.getClose()){
								break;
							}
						}else{
							if (thisCq.getClose()>=prevCq.getClose()){
								break;
							}
						}
						days++;
						high = prevCq.getClose();
						thisCq = prevCq;
					}
					if (high>0){
						if (DateTimeUtil.DateDiff(oneDayBeforeSumbitCq.getStartTime().getTime(),thisCq.getStartTime().getTime())<=LOOKUP_DAYS){
							float value = (high-submitCq.getOpen())/(high*days);
							if (value>threashold*0.01){
								float price = submitCq.getOpen()*(1+openUpRatio*0.01f);
								scrl.add(new SelectCandidateResult(sdf.format(submitCq.getStartTime()), value, price));
								logger.info(String.format("%s has consecutive drop %.3f for %d days til %s", submitCq.getStockid(), value, days, 
										sdf.format(submitCq.getStartTime())));
							}
						}else{
							logger.info(String.format("%s has used data %d days before. ignored.", submitCq.getStockid(), LOOKUP_DAYS));
						}
					}
				}
			}
		}
		return scrl;
	}
}
