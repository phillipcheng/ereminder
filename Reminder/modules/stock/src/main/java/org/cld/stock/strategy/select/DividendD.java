package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cld.stock.AnnounceTime;
import org.cld.stock.CandleQuote;
import org.cld.stock.DivSplit;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import org.cld.util.DateTimeUtil;
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DividendD extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public DividendD(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{sc.getBTFQDailyQuoteMapper(), sc.getDividendTableMapper()};
	}
	
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		float threashold = ((Double)params[0]).floatValue();
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> fqlist = tableResults.get(sc.getBTFQDailyQuoteMapper());
		TreeMap<Date, CandleQuote> cqMap = new TreeMap<Date, CandleQuote>();
		for (Object cqo:fqlist){
			CandleQuote cq = (CandleQuote) cqo;
			cqMap.put(cq.getStartTime(), cq);
		}
		List<Object> dvlist = tableResults.get(sc.getDividendTableMapper());
		for (Object dvo:dvlist){
			DivSplit dv = (DivSplit)dvo;
			Date submitD = cqMap.ceilingKey(dv.getDt());//>=
			if (submitD==dv.getDt()){
				if (dv.getAt()==AnnounceTime.afterMarket){
					submitD = cqMap.ceilingKey(DateTimeUtil.tomorrow(submitD));
				}
			}
			Date beforeSumbitD = cqMap.floorKey(DateTimeUtil.yesterday(submitD));
			if (beforeSumbitD!=null){
				CandleQuote cq = cqMap.get(beforeSumbitD);
				if (checkValid(cq)){
					float yield = dv.getDividend()/(cq.getClose()/cq.getFqIdx());
					if (yield>threashold){
						scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(submitD), yield));
					}
				}
			}
		}
		
		return scrl;
	}
}
