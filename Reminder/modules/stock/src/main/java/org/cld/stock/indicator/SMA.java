package org.cld.stock.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;


//simple moving average
public class SMA extends Indicator{

	protected static Logger logger =  LogManager.getLogger(SMA.class);
	//size is periods
	List<Float> values = new ArrayList<Float>();
	
	public SMA(){
	}
	public SMA(int periods){
		super.setPeriods(periods);
	}
	
	@Override
	public void init(Map<String, String> params) {
		super.getRmap().put(toKey(), RenderType.line);
	}
	
	public static float calSMA(List<Float> vl){
		float sum = 0f;
		for (float v:vl){
			sum+=v;
		}
		return sum/vl.size();
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		if (values.size()>super.getPeriods()){
			logger.error("buffer size overflow!");
			return Indicator.V_NA;
		}else if (values.size()<super.getPeriods()-1){
			values.add(cqi.getCq().getClose());
			return Indicator.V_NA;
		}else if (values.size()==super.getPeriods()-1){
			values.add(cqi.getCq().getClose());
			return calSMA(values);
		}else{
			values.remove(0);
			values.add(cqi.getCq().getClose());
			return calSMA(values);
		}
	}

	@Override
	public String toKey() {
		return String.format("SMA:%d", super.getPeriods());
	}
}
