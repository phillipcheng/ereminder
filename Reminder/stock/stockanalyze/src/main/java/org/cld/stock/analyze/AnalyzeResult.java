package org.cld.stock.analyze;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyResult;


class RateValueComparator implements Comparator<String>{
	private Map<String, StrategyResult> baseMap;
	public RateValueComparator(Map<String, StrategyResult> baseMap){
		this.baseMap = baseMap;
	}
	
	@Override
	public int compare(String a, String b) {
		if (baseMap.get(a).getAvgRate() >= baseMap.get(b).getAvgRate()) {
            return -1;
        } else {
            return 1;
        }// returning 0 would merge keys
	}
}
public class AnalyzeResult {
	public static Logger logger = LogManager.getLogger(AnalyzeResult.class);
	private Map<String, Map<String, StrategyResult>> resultBySymbol = new ConcurrentHashMap<String, Map<String, StrategyResult>>();
	private Map<String, Map<String, StrategyResult>> resultByStrategy = new ConcurrentHashMap<String, Map<String, StrategyResult>>();
	
	public void addResult(String symbol, SelectStrategy bs, SellStrategy ss, StrategyResult sr){
		String ts = bs.toString() + "|" + ss.toString();
		Map<String, StrategyResult> tsMap = resultBySymbol.get(symbol);
		if (tsMap == null){
			tsMap = new HashMap<String, StrategyResult>();
			resultBySymbol.put(symbol, tsMap);
		}
		tsMap.put(ts, sr);
		
		Map<String, StrategyResult> ssMap = resultByStrategy.get(ts);
		if (ssMap == null){
			ssMap = new HashMap<String, StrategyResult>();
			resultByStrategy.put(ts, ssMap);
		}
		ssMap.put(symbol, sr);
	}
	
	public Map<String, StrategyResult> getOrderedResultBySymbol(){
		Map<String, StrategyResult> map = new HashMap<String, StrategyResult>();
		for (String symbol:resultBySymbol.keySet()){
			Map<String, StrategyResult> rbs = resultBySymbol.get(symbol);
			float totalRate = 0f;
			int total = 0;
			for (String ts: rbs.keySet()){
				StrategyResult sr = rbs.get(ts);
				totalRate += sr.getAvgRate() * sr.getNumRecords();
				total += sr.getNumRecords();
			}
			StrategyResult tsr = new StrategyResult(total, totalRate/total);
			map.put(symbol, tsr);
		}
		Map<String, StrategyResult> tmap = new TreeMap<String, StrategyResult>(new RateValueComparator(map));
		tmap.putAll(map);
		return tmap;
	}
	
	public Map<String, StrategyResult> getOrderedResultByStrategy(){
		Map<String, StrategyResult> map = new HashMap<String, StrategyResult>();
		for (String ts:resultByStrategy.keySet()){
			Map<String, StrategyResult> rbs = resultByStrategy.get(ts);
			float totalRate = 0f;
			int total = 0;
			for (String symbol: rbs.keySet()){
				StrategyResult sr = rbs.get(symbol);
				totalRate += sr.getAvgRate() * sr.getNumRecords();
				total += sr.getNumRecords();
			}
			StrategyResult ssr = new StrategyResult(total, totalRate/total);
			map.put(ts, ssr);
		}
		Map<String, StrategyResult> tmap = new TreeMap<String, StrategyResult>(new RateValueComparator(map));
		tmap.putAll(map);
		return tmap;
	}
	
	public StrategyResult getAggregResult(){
		float totalRate = 0f;
		int total = 0;
		for (String symbol:resultBySymbol.keySet()){
			Map<String, StrategyResult> rbs = resultBySymbol.get(symbol);
			for (String ts: rbs.keySet()){
				StrategyResult sr = rbs.get(ts);
				logger.info(String.format("symbol:%s, sr:%s found for ts:%s", symbol, sr, ts));
				totalRate += sr.getAvgRate() * sr.getNumRecords();
				total += sr.getNumRecords();
			}
		}
		float totalAvgRate = 0f;
		if (total!=0){
			totalAvgRate = totalRate/total;
		}
		StrategyResult tsr = new StrategyResult(total, totalAvgRate);
		
		return tsr;
	}
}
