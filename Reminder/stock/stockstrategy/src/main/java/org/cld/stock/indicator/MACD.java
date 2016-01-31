package org.cld.stock.indicator;

import java.util.HashMap;
import java.util.Map;

import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

public class MACD extends Indicator{
	public static final String PARAM_SMALL_PERIODS="upperiods";
	public static final String PARAM_BIGGER_PERIODS="downperiods";
	public static final String PARAM_SIGNALPERIODS="signalperiods";
	
	public static final String DIFF="diff";//smallPeriod - bigPeriod
	public static final String DEA="dea";//signal
	public static final String MACD="macd";//DIFF-DEA
	
	private int smallPeriods;
	private int bigPeriods;
	private int signalperiods;
	
	private EMA smallPeriodEMA;
	private EMA bigPeriodEMA;
	private EMA signalEMA;
	
	public MACD(){
	}
	
	@Override
	public void cleanup(){
		smallPeriodEMA.cleanup();
		bigPeriodEMA.cleanup();
		signalEMA.cleanup();
	}
	
	@Override
	public void init(Map<String, String> params) {
		smallPeriods = (int) Float.parseFloat(params.get(PARAM_SMALL_PERIODS));
		bigPeriods = (int) Float.parseFloat(params.get(PARAM_BIGGER_PERIODS));
		signalperiods = (int) Float.parseFloat(params.get(PARAM_SIGNALPERIODS));
		smallPeriodEMA = new EMA(smallPeriods, 2f/(smallPeriods+1));
		bigPeriodEMA = new EMA(bigPeriods, 2f/(bigPeriods+1));
		signalEMA = new EMA(signalperiods, 2f/(signalperiods+1));
		super.setPeriods(bigPeriods);//biggest one
		super.getRmap().put(DIFF, RenderType.line);
		super.getRmap().put(DEA, RenderType.line);
		super.getRmap().put(MACD, RenderType.bar);
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		float v = cqi.getCq().getClose();
		float up = smallPeriodEMA.update(v);
		float down = bigPeriodEMA.update(v);
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
		return smallPeriods;
	}
	public void setUpperiods(int upperiods) {
		this.smallPeriods = upperiods;
	}
	public int getDownperiods() {
		return bigPeriods;
	}
	public void setDownperiods(int downperiods) {
		this.bigPeriods = downperiods;
	}
	public int getSignalperiods() {
		return signalperiods;
	}
	public void setSignalperiods(int signalperiods) {
		this.signalperiods = signalperiods;
	}
	public EMA getUpEMA() {
		return smallPeriodEMA;
	}
	public void setUpEMA(EMA upEMA) {
		this.smallPeriodEMA = upEMA;
	}
	public EMA getDownEMA() {
		return bigPeriodEMA;
	}
	public void setDownEMA(EMA downEMA) {
		this.bigPeriodEMA = downEMA;
	}
	public EMA getSignalEMA() {
		return signalEMA;
	}
	public void setSignalEMA(EMA signalEMA) {
		this.signalEMA = signalEMA;
	}
}
