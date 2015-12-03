package org.cld.stock.strategy.select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class WShape extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	public static final String LOOKUP_PERIOD="scs.param.luperiod";
	public static final String AMPLITUDE="scs.param.amplitude";
	public static final String BELOW_LOW="scs.param.belowlow";
	
	public WShape(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{sc.getBTFQDailyQuoteMapper()};
	}
	
	//using the close of current cq, then submit date has to be next trading day
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<Object>> tableResults) {
		String lup = (String) this.getParams().get(LOOKUP_PERIOD);
		int periodDays = Integer.parseInt(lup);
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> lo = tableResults.get(sc.getBTFQDailyQuoteMapper());
		TreeMap<Float, TreeMap<Date, CandleQuote>> dailyFQMap = new TreeMap<Float, TreeMap<Date, CandleQuote>>();
		if (lo.size()>periodDays){
			//init
			for (int i=0; i<periodDays; i++){
				CandleQuote cq = (CandleQuote)lo.get(i);
				CandleQuote.addCq(dailyFQMap, cq);
			}
			int idx=periodDays;
			while (idx<lo.size()){
				//calculate the value for date at lo.get(idx) using data in the dailyFQMap which contains data [lo.get(idx-periodDays), lo.get(idx-1)]
				CandleQuote currentCq = (CandleQuote) lo.get(idx-1);
				CandleQuote maxCq = CandleQuote.getCq(dailyFQMap, true, false);
				CandleQuote minCq = CandleQuote.getCq(dailyFQMap, false, false);
				CandleQuote nextCq = (CandleQuote) lo.get(idx);
				CandleQuote firstCq = (CandleQuote) lo.get(idx-periodDays);
				if (checkValid(currentCq)){
					if (minCq.getStartTime().before(maxCq.getStartTime()) && maxCq.getClose()>minCq.getClose()){
						float value = (currentCq.getClose()-minCq.getClose())/(maxCq.getClose()-minCq.getClose());
						scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(nextCq.getStartTime()), value));
					}
				}
				//
				CandleQuote.addCq(dailyFQMap, nextCq);
				CandleQuote.removeCq(dailyFQMap, firstCq);
				idx++;
			}
			return scrl;
		}else{
			return null;
		}
	}
}
