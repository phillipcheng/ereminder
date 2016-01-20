package org.cld.stock.common;

import java.util.HashMap;
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
		return cqi;
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
