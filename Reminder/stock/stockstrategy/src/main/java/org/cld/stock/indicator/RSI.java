package org.cld.stock.indicator;

import java.util.Map;

import org.cld.stock.common.CqIndicators;
import org.cld.stock.strategy.SelectStrategy;

//relative strength index
public class RSI extends Indicator{
	
	private EMA gainEMA;
	private EMA lossEMA;
	
	private float prevClose = Indicator.V_NA;
	private float prevAG = Indicator.V_NA;
	private float prevAL = Indicator.V_NA;
	
	public RSI(){
	}
	
	@Override
	public void cleanup(){
		gainEMA.cleanup();
		lossEMA.cleanup();
		prevClose = Indicator.V_NA;
		prevAG = Indicator.V_NA;
		prevAL = Indicator.V_NA;
	}
	
	@Override
	public void init(Map<String, String> params) {
		gainEMA = new EMA(super.getPeriods(), 1f/(super.getPeriods()));
		lossEMA = new EMA(super.getPeriods(), 1f/(super.getPeriods()));
		super.getRmap().put(toKey(), RenderType.line);
	}
	
	@Override
	public Object calculate(CqIndicators cqi, SelectStrategy bs){
		float v = cqi.getCq().getClose();
		if (prevClose==Indicator.V_NA){
			prevClose = v;
			return Indicator.V_NA;
		}else{
			float delta = v-prevClose;
			prevClose = v;
			float avgGain = Indicator.V_NA;
			float avgLoss = Indicator.V_NA;
			if (delta>0){
				avgGain = gainEMA.update(delta);
				avgLoss = prevAL;
				prevAG = avgGain;
			}else{
				avgLoss = lossEMA.update(-1*delta);
				avgGain = prevAG;
				prevAL = avgLoss;
			}
			if (avgGain!=Indicator.V_NA && avgLoss!=Indicator.V_NA){
				if (avgLoss!=0){
					float rs = avgGain/avgLoss;
					float rsi = 100-100/(1+rs);
					return rsi;
				}
			}
		}
		return Indicator.V_NA;
	}

	@Override
	public String toKey() {
		return String.format("RSI:%d", getPeriods());
	}

	public EMA getGainEMA() {
		return gainEMA;
	}

	public void setGainEMA(EMA gainEMA) {
		this.gainEMA = gainEMA;
	}

	public EMA getLossEMA() {
		return lossEMA;
	}

	public void setLossEMA(EMA lossEMA) {
		this.lossEMA = lossEMA;
	}
}
