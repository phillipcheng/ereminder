package org.cld.stock.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

//simple moving average
public class EMA extends Indicator{

	public EMA(){
	}
	
	public EMA(int periods){
		super.setPeriods(periods);
	}
	
	@Override
	public void init(Map<String, String> params) {
	}
	
	@Override
	public float calculate(CqIndicators prevCqi, List<CqIndicators> cqil, SelectStrategy bs){
		float prevEMA = Indicator.V_NA;
		String key = toKey();
		if (prevCqi!=null){
			if (prevCqi.hasIndicator(key)){
				prevEMA = prevCqi.getIndicator(key);
			}
		}
		if (prevEMA == Indicator.V_NA){
			//first EMA is SMA
			if (cqil.size()<super.getPeriods()){
				return Indicator.V_NA;
			}else{
				List<Float> vl = new ArrayList<Float>();
				for (CqIndicators cqi:cqil){
					vl.add(cqi.getCq().getClose());
				}
				return SMA.getSMA(vl);
			}
		}else{
			float multiplier = 2f / (super.getPeriods() + 1);
			CandleQuote lastCq = cqil.get(cqil.size()-1).getCq();
			return (lastCq.getClose()-prevEMA)*multiplier + prevEMA;
		}
	}

	@Override
	public String toKey() {
		return String.format("SMA:%d", super.getPeriods());
	}
}
