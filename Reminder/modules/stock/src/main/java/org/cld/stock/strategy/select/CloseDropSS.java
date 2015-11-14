package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CloseDropSS extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public CloseDropSS(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public JDBCMapper[] getTableMappers() {
		return new JDBCMapper[]{sc.getFQDailyQuoteTableMapper()};
	}
	
	//use the open of submit day and submit at the open
	@JsonIgnore
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<JDBCMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		float threashold = ((Double)params[0]).floatValue();//drop percentage
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> lo = tableResults.get(sc.getFQDailyQuoteTableMapper());
		for (int i=lo.size()-1; i>=0; i--){
			CandleQuote submitCq = (CandleQuote) lo.get(i);
			CandleQuote thisCq = submitCq;
			CandleQuote prevCq = null;
			float high=0;
			int days=0;//include the submit day
			if (checkValid(submitCq)){
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
					float value = (high-submitCq.getOpen())/high;
					if (value>threashold*0.01){
						scrl.add(new SelectCandidateResult(sdf.format(submitCq.getStartTime()), value));
						logger.info(String.format("%s has consecutive drop %.3f for %d days til %s", submitCq.getStockid(), value, days, 
								sdf.format(submitCq.getStartTime())));
					}
				}
			}
		}
		return scrl;
	}
}
