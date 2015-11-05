package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.cld.stock.AnnounceTime;
import org.cld.stock.CandleQuote;
import org.cld.stock.Dividend;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategyMapperByStock;
import org.cld.util.DateTimeUtil;
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DividendSS extends SelectStrategyMapperByStock {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public DividendSS(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public JDBCMapper[] getTableMappers() {
		return new JDBCMapper[]{sc.getFQDailyQuoteTableMapper(), sc.getDividendTableMapper()};
	}
	
	@JsonIgnore
	@Override
	public List<SelectCandidateResult> getSelectCandidate(Map<JDBCMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		float threashold = ((Double)params[0]).floatValue();
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> fqlist = tableResults.get(sc.getFQDailyQuoteTableMapper());
		TreeMap<Date, CandleQuote> cqMap = new TreeMap<Date, CandleQuote>();
		for (Object cqo:fqlist){
			CandleQuote cq = (CandleQuote) cqo;
			cqMap.put(cq.getStartTime(), cq);
		}
		List<Object> dvlist = tableResults.get(sc.getDividendTableMapper());
		for (Object dvo:dvlist){
			Dividend dv = (Dividend)dvo;
			Date submitD = cqMap.ceilingKey(dv.getDt());//>=
			if (submitD==dv.getDt()){
				if (dv.getAt()==AnnounceTime.afterMarket){
					submitD = cqMap.ceilingKey(DateTimeUtil.tomorrow(submitD));
				}
			}
			Date beforeSumbitD = cqMap.floorKey(DateTimeUtil.yesterday(submitD));
			if (beforeSumbitD!=null){
				CandleQuote cq = cqMap.get(beforeSumbitD);
				float yield = dv.getDividend()/(cq.getClose()/cq.getFqIdx());
				if (yield>threashold){
					scrl.add(new SelectCandidateResult(sdf.format(submitD), yield));
				}
			}
		}
		
		return scrl;
	}
}
