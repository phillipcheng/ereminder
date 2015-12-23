package org.cld.stock.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

//simple moving average
public class EMA extends Indicator{

	protected static Logger logger =  LogManager.getLogger(EMA.class);
	
	float prevEMA=Indicator.V_NA;
	List<Float> values = new ArrayList<Float>();//of size periods
	
	float multiplier;
	
	public EMA(){
	}
	
	public EMA(int periods, float multiplier){
		super.setPeriods(periods);
		this.multiplier = multiplier;
	}
	
	@Override
	public void init(Map<String, String> params) {
		super.getRmap().put(toKey(), RenderType.line);
	}
	
	public static float calEMA(float prev, List<Float> values, float value, int periods, float multiplier){
		float emaV=Indicator.V_NA;
		if (prev==Indicator.V_NA){
			if (values.size()>periods){
				logger.error("buffer size overflow!");
				return Indicator.V_NA;
			}else if (values.size()<periods-1){
				values.add(value);
				return Indicator.V_NA;
			}else if (values.size()==periods-1){
				values.add(value);
				emaV = SMA.calSMA(values);
				return emaV;
			}else{
				logger.error("when values is full, prevEMA can't be NA.");
				return Indicator.V_NA;
			}
		}else{
			emaV = value*multiplier + (1-multiplier)*prev;
			return emaV;
		}
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		prevEMA = calEMA(prevEMA, values, cqi.getCq().getClose(), super.getPeriods(), multiplier);
		return prevEMA;
	}
	
	public float update(float v){
		prevEMA = calEMA(prevEMA, values, v, super.getPeriods(), multiplier);
		return prevEMA;
	}
	
	@Override
	public String toKey() {
		return String.format("SMA:%d", super.getPeriods());
	}

	public float getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}
}
