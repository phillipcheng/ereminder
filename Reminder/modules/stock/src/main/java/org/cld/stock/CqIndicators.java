package org.cld.stock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.strategy.SelectStrategy;

public class CqIndicators {
	private static Logger logger =  LogManager.getLogger(CqIndicators.class);
	private CandleQuote cq;
	private Map<String, Float> pv = new HashMap<String, Float>(); //name+parameter to value map
	
	public CqIndicators(CandleQuote cq){
		this.cq = cq;
	}
	
	public String toString(){
		return String.format("cq:%s, pv:%s", cq.toString(), pv.toString());
	}
	
	private static CqIndicators convert(CandleQuote cq, Map<Date, CqIndicators> map){
		if (map.containsKey(cq.getStartTime())){
			return map.get(cq.getStartTime());
		}else{
			return new CqIndicators(cq);
		}
	}
	
	private static List<CqIndicators> convert(List<CandleQuote> cql, Map<Date,CqIndicators> map){
		List<CqIndicators> cqiList = new ArrayList<CqIndicators>();
		for (CandleQuote cq: cql){
			cqiList.add(convert(cq, map));
		}
		return cqiList;
	}
	
	//calculate and add indicators to newCqlist and output a cqindicator list of the same size using prevList if not null
	public static List<CqIndicators> addIndicators(CqIndicators prevCqi, List<CandleQuote> prevList, List<CandleQuote> newCqlist, SelectStrategy bs){
		TreeMap<Date, CqIndicators> cqiTree = new TreeMap<Date, CqIndicators>();
		for (Indicator ind:bs.getIndList()){
			CqIndicators myPreCqi = prevCqi;
			int periods = ind.getPeriods();
			List<CqIndicators> cqil = new ArrayList<CqIndicators>();
			if (prevList!=null){//add the last periods item from prevList into cql
				if (prevList.size()>periods){
					cqil = convert(prevList.subList(prevList.size()-periods, prevList.size()), cqiTree);
				}else{
					cqil = convert(prevList, cqiTree);
				}
			}
			for (int i=0; i<newCqlist.size(); i++){
				CandleQuote cq = newCqlist.get(i);
				CqIndicators cqi = convert(cq, cqiTree);
				float v = Indicator.V_NA;
				cqil.add(cqi);
				if (cqil.size()>periods){
					cqil.remove(0);
				}
				v = ind.calculate(myPreCqi, cqil, bs);
				cqi.putIndicator(ind.toKey(), v);
				cqiTree.put(cqi.getCq().getStartTime(), cqi);
				myPreCqi = cqi;
			}
		}
		List<CqIndicators> cqil = new ArrayList<CqIndicators>();
		for (CqIndicators cqi:cqiTree.values()){
			cqil.add(cqi);
			logger.debug(String.format("cqi:%s", cqi));
		}
		return cqil;
	}
	
	public CandleQuote getCq() {
		return cq;
	}
	public void setCq(CandleQuote cq) {
		this.cq = cq;
	}
	public Map<String, Float> getPv() {
		return pv;
	}
	public void setPv(Map<String, Float> pv) {
		this.pv = pv;
	}
	public float getIndicator(String name){
		return pv.get(name);
	}
	public boolean hasIndicator(String name){
		return pv.containsKey(name);
	}
	public void putIndicator(String name, float value){
		pv.put(name, value);
	}
	
}
