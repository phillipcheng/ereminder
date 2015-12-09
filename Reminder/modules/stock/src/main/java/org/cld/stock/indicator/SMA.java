package org.cld.stock.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

//simple moving average
public class SMA extends Indicator{
	
	public SMA(){
	}
	
	public SMA(int periods){
		super.setPeriods(periods);
	}
	
	@Override
	public void init(Map<String, String> params) {
	}
	
	public static float getSMA(List<Float> vl){
		float sum = 0f;
		for (float v:vl){
			sum+=v;
		}
		return sum/vl.size();
	}
	
	@Override
	public float calculate(CqIndicators prevCqi, List<CqIndicators> cqil, SelectStrategy bs){
		List<Float> vl = new ArrayList<Float>();
		for (CqIndicators cqi:cqil){
			vl.add(cqi.getCq().getClose());
		}
		return getSMA(vl);
	}

	@Override
	public String toKey() {
		return String.format("SMA:%d", super.getPeriods());
	}
}
