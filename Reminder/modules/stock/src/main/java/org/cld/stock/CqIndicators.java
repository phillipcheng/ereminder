package org.cld.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.indicator.Indicator;
import org.cld.stock.strategy.SelectStrategy;

public class CqIndicators {
	private static Logger logger =  LogManager.getLogger(CqIndicators.class);
	private CandleQuote cq;
	private Map<String, Object> pv = new HashMap<String, Object>(); //indicator instance to value map
	
	public CqIndicators(CandleQuote cq){
		this.cq = cq;
	}
	
	public String toString(){
		return String.format("cq:%s, pv:%s", cq.toString(), pv.toString());
	}

	public static CqIndicators addIndicators(CandleQuote cq, SelectStrategy bs){
		CqIndicators cqi = new CqIndicators(cq);
		for (Indicator ind:bs.getIndMap().values()){
			Object v = Indicator.V_NA;
			v = ind.calculate(cqi, bs);
			cqi.putIndicator(ind.toKey(), v);
		}
		logger.debug(String.format("cqi:%s", cqi));
		return cqi;
	}
	
	//calculate and add indicators to newCqlist and output a cqindicator list of the same size using prevList if not null
	public static List<CqIndicators> addIndicators(List<CandleQuote> newCqlist, SelectStrategy bs){
		List<CqIndicators> cqil = new ArrayList<CqIndicators>();
		for (CandleQuote cq:newCqlist){
			cqil.add(addIndicators(cq, bs));
		}
		return cqil;
	}
	
	public CandleQuote getCq() {
		return cq;
	}
	public void setCq(CandleQuote cq) {
		this.cq = cq;
	}
	public Map<String, Object> getPv() {
		return pv;
	}
	public void setPv(Map<String, Object> pv) {
		this.pv = pv;
	}
	public Object getIndicator(String name){
		return pv.get(name);
	}
	public boolean hasIndicator(String name){
		return pv.containsKey(name);
	}
	public void putIndicator(String name, Object value){
		pv.put(name, value);
	}
	
}
