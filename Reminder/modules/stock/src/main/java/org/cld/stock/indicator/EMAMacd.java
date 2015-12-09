package org.cld.stock.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

public class EMAMacd extends Indicator{
	public static final String PARAM_PERIODS="periods";
	public static final String upIndName="emaUp";
	public static final String downIndName="emaDown";

	public EMAMacd(){
	}
	
	public EMAMacd(int periods){
		super.setPeriods(periods);
	}
	
	@Override
	public void init(Map<String, String> params) {
	}
	
	@Override
	public float calculate(CqIndicators prevCqi, List<CqIndicators> cqil, SelectStrategy bs){
		String upKey = bs.getIndMap().get(upIndName).toKey();
		String downKey = bs.getIndMap().get(downIndName).toKey();
		String key = toKey();
		float prevEMA = Indicator.V_NA;
		if (prevCqi!=null){
			if (prevCqi.hasIndicator(key)){
				prevEMA = prevCqi.getIndicator(key);
			}
		}
		if (prevEMA == Indicator.V_NA){
			//first EMA is SMA
			List<Float> vl = new ArrayList<Float>();
			for (CqIndicators cqi:cqil){
				if (cqi.hasIndicator(upKey) && cqi.getIndicator(upKey)!=Indicator.V_NA 
						&& cqi.hasIndicator(downKey) && cqi.getIndicator(downKey)!=Indicator.V_NA){
					vl.add(cqi.getIndicator(upKey)-cqi.getIndicator(downKey));
				}else{
					return Indicator.V_NA;
				}
			}
			return SMA.getSMA(vl);
		}else{
			float multiplier = 2f / (getPeriods() + 1);
			CqIndicators lastCqi = cqil.get(cqil.size()-1);
			float v = lastCqi.getIndicator(upKey)-lastCqi.getIndicator(downKey);
			return (v-prevEMA)*multiplier + prevEMA;
		}
	}

	@Override
	public String toKey() {
		return String.format("EMAMacd:%d", getPeriods());
	}
}
