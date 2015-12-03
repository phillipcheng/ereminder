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
import org.cld.util.jdbc.JDBCMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class NShape extends SelectStrategy {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	public static final String LOOKUP_PERIOD="luperiod";
	public NShape(){
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

	//max and latest
	private CandleQuote getMaxCq(TreeMap<Float, TreeMap<Date, CandleQuote>> dailyFQMap){
		TreeMap<Date, CandleQuote> map = dailyFQMap.get(dailyFQMap.descendingKeySet().first());
		return map.get(map.descendingKeySet().first());
	}
	//min and earliest
	private CandleQuote getMinCq(TreeMap<Float, TreeMap<Date, CandleQuote>> dailyFQMap){
		TreeMap<Date, CandleQuote> map = dailyFQMap.get(dailyFQMap.firstKey());
		return map.get(map.firstKey());
	}
	private boolean removeCq(TreeMap<Float, TreeMap<Date, CandleQuote>> dailyFQMap, CandleQuote cq){
		if (dailyFQMap.containsKey(cq.getClose())){
			TreeMap<Date, CandleQuote> cqTree = dailyFQMap.get(cq.getClose());
			cqTree.remove(cq.getStartTime());
			if (cqTree.size()==0){
				dailyFQMap.remove(cq.getClose());
			}
			return true;
		}else{
			return false;
		}
	}
	private void addCq(TreeMap<Float, TreeMap<Date, CandleQuote>> dailyFQMap, CandleQuote cq){
		if (dailyFQMap.containsKey(cq.getClose())){
			TreeMap<Date, CandleQuote> cqTree = dailyFQMap.get(cq.getClose());
			cqTree.put(cq.getStartTime(), cq);
		}else{
			TreeMap<Date, CandleQuote> cqTree = new TreeMap<Date, CandleQuote>();
			cqTree.put(cq.getStartTime(), cq);
			dailyFQMap.put(cq.getClose(), cqTree);
		}
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
				addCq(dailyFQMap, cq);
			}
			int idx=periodDays;
			while (idx<lo.size()){
				//calculate the value for date at lo.get(idx) using data in the dailyFQMap which contains data [lo.get(idx-periodDays), lo.get(idx-1)]
				CandleQuote currentCq = (CandleQuote) lo.get(idx-1);
				CandleQuote maxCq = getMaxCq(dailyFQMap);
				CandleQuote minCq = getMinCq(dailyFQMap);
				CandleQuote nextCq = (CandleQuote) lo.get(idx);
				CandleQuote firstCq = (CandleQuote) lo.get(idx-periodDays);
				if (checkValid(currentCq)){
					if (minCq.getStartTime().before(maxCq.getStartTime()) && maxCq.getClose()>minCq.getClose()){
						float value = (currentCq.getClose()-minCq.getClose())/(maxCq.getClose()-minCq.getClose());
						scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(nextCq.getStartTime()), value));
					}
				}
				//
				addCq(dailyFQMap, nextCq);
				removeCq(dailyFQMap, firstCq);
				idx++;
			}
			return scrl;
		}else{
			return null;
		}
	}
}
