package org.cld.stock.indicator;

import java.util.HashMap;
import java.util.Map;

import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

public class MACD extends Indicator{
	public static final String PARAM_UPPERIODS="upperiods";
	public static final String PARAM_DOWNPERIODS="downperiods";
	public static final String PARAM_SIGNALPERIODS="signalperiods";
	
	public static final String DIFF="diff";//up-down
	public static final String DEA="dea";//signal
	public static final String MACD="macd";//DIFF-DEA
	
	private int upperiods;
	private int downperiods;
	private int signalperiods;
	
	private EMA upEMA;
	private EMA downEMA;
	private EMA signalEMA;
	
	public MACD(){
	}
	
	@Override
	public void cleanup(){
		upEMA.cleanup();
		downEMA.cleanup();
		signalEMA.cleanup();
	}
	
	@Override
	public void init(Map<String, String> params) {
		upperiods = (int) Float.parseFloat(params.get(PARAM_UPPERIODS));
		downperiods = (int) Float.parseFloat(params.get(PARAM_DOWNPERIODS));
		signalperiods = (int) Float.parseFloat(params.get(PARAM_SIGNALPERIODS));
		upEMA = new EMA(upperiods, 2f/(upperiods+1));
		downEMA = new EMA(downperiods, 2f/(downperiods+1));
		signalEMA = new EMA(signalperiods, 2f/(signalperiods+1));
		super.setPeriods(downperiods);//biggest one
		super.getRmap().put(DIFF, RenderType.line);
		super.getRmap().put(DEA, RenderType.line);
		super.getRmap().put(MACD, RenderType.bar);
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		float v = cqi.getCq().getClose();
		float up = upEMA.update(v);
		float down = downEMA.update(v);
		float signal = Indicator.V_NA;
		if (up!=Indicator.V_NA && down!=Indicator.V_NA){
			signal = signalEMA.update(up-down);
			if (signal!=Indicator.V_NA){
				Map<String, Float> map = new HashMap<String, Float>();
				map.put(DIFF, up-down);
				map.put(DEA, signal);
				map.put(MACD, up-down-signal);
				return map;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	@Override
	public String toKey() {
		return String.format("EMAMacd:%d", getPeriods());
	}

	//
	public int getUpperiods() {
		return upperiods;
	}
	public void setUpperiods(int upperiods) {
		this.upperiods = upperiods;
	}
	public int getDownperiods() {
		return downperiods;
	}
	public void setDownperiods(int downperiods) {
		this.downperiods = downperiods;
	}
	public int getSignalperiods() {
		return signalperiods;
	}
	public void setSignalperiods(int signalperiods) {
		this.signalperiods = signalperiods;
	}
	public EMA getUpEMA() {
		return upEMA;
	}
	public void setUpEMA(EMA upEMA) {
		this.upEMA = upEMA;
	}
	public EMA getDownEMA() {
		return downEMA;
	}
	public void setDownEMA(EMA downEMA) {
		this.downEMA = downEMA;
	}
	public EMA getSignalEMA() {
		return signalEMA;
	}
	public void setSignalEMA(EMA signalEMA) {
		this.signalEMA = signalEMA;
	}
}
