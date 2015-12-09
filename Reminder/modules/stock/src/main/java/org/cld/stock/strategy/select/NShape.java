package org.cld.stock.strategy.select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.DataMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class NShape extends SelectStrategy {

	public static final String LOOKUP_PERIOD="scs.param.luperiod";
	
	public NShape(){
	}
	
	private int periodNum=0;
	private List<CandleQuote> cqlist = new ArrayList<CandleQuote>();
	

	@Override
	public void init(){
		super.init();
		String lup = (String) this.getParams().get(LOOKUP_PERIOD);
		periodNum = Integer.parseInt(lup);
	}
	
	@Override
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){
		List<CqIndicators> cqilist = (List<CqIndicators>) resultMap.get(this.quoteMapper());
		for (CqIndicators cqi:cqilist){
			cqlist.add(cqi.getCq());
		}
	}
	
	@Override
	public int maxLookupNum() {
		return periodNum;
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{super.quoteMapper()};
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
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<? extends Object>> tableResults) {
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<CandleQuote> lo = null;
		if (super.quoteMapper().oneFetch()){
			lo = cqlist;
		}else{
			lo = new ArrayList<CandleQuote>();
			List<CqIndicators> cqilist = (List<CqIndicators>) tableResults.get(this.quoteMapper());
			for (CqIndicators cqi:cqilist){
				lo.add(cqi.getCq());
			}
		}
		TreeMap<Float, TreeMap<Date, CandleQuote>> dailyFQMap = new TreeMap<Float, TreeMap<Date, CandleQuote>>();
		if (lo.size()>periodNum){
			//init
			for (int i=0; i<periodNum; i++){
				CandleQuote cq = (CandleQuote)lo.get(i);
				addCq(dailyFQMap, cq);
			}
			int idx=periodNum;
			while (idx<lo.size()){
				//calculate the value for date at lo.get(idx) using data in the dailyFQMap which contains data [lo.get(idx-periodDays), lo.get(idx-1)]
				CandleQuote currentCq = (CandleQuote) lo.get(idx-1);
				CandleQuote maxCq = getMaxCq(dailyFQMap);
				CandleQuote minCq = getMinCq(dailyFQMap);
				CandleQuote nextCq = (CandleQuote) lo.get(idx);
				CandleQuote firstCq = (CandleQuote) lo.get(idx-periodNum);
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
