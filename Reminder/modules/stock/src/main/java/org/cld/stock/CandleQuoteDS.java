package org.cld.stock;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

public class CandleQuoteDS {
	
	private TreeMap<Date, CandleQuote> cqds = new TreeMap<Date, CandleQuote>();
	
	public int size(){
		return cqds.size();
	}
	
	//get max/min before date dt
	public CandleQuote getCq(Date dt, boolean takeMax){
		SortedMap<Date, CandleQuote> m = cqds.headMap(dt);
		float max=0;
		float min=99999999f;
		CandleQuote minCq = null;
		CandleQuote maxCq = null;
		for (Date k:m.keySet()){
			CandleQuote cq = m.get(k);
			if (cq.getClose()<min){
				minCq = cq;
				min = cq.getClose();
			}
			if (cq.getClose()>max){
				maxCq = cq;
				max = cq.getClose();
			}
		}
		if (takeMax){
			return maxCq;
		}else{
			return minCq;
		}
	}
	
	public void removeMin(){
		cqds.remove(cqds.firstKey());
	}
	
	public void add(CandleQuote cq){
		cqds.put(cq.getStartTime(), cq);
	}
	
	public CandleQuote getLast(){
		return cqds.get(cqds.lastKey());
	}
	
	public CandleQuote getPrev(CandleQuote cq){
		return cqds.get(cqds.headMap(cq.getStartTime()).lastKey());
	}
}
