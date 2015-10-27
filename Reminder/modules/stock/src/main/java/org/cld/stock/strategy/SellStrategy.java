package org.cld.stock.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.cld.util.StringUtil;

//for abstract class json mapping
public class SellStrategy {
	public static String KEY_SELLS_DURATION="sls.duration";
	public static String KEY_SELLS_LIMIT_PERCENTAGE="sls.limitPercentage";
	public static String KEY_SELLS_TRAIL_PERCENTAGE="sls.stopTrailingPercentage";

	private int holdDuration=0;
	private float limitPercentage=0;//% unit
	private float stopTrailingPercentage=0;//% unit
	
	public SellStrategy(){
	}
	
	public SellStrategy(int holdDuration, float limitPercentage, float stopTrailingPercentage){
		this.holdDuration = holdDuration;
		this.limitPercentage = limitPercentage;
		this.stopTrailingPercentage = stopTrailingPercentage;
	}

	@Override
	public int hashCode(){
		return (int) (holdDuration + limitPercentage + stopTrailingPercentage);
	}
	
	@Override
	public boolean equals(Object o){
		if (o!=null){
			SellStrategy os = (SellStrategy) o;
			if (this.holdDuration==os.holdDuration &&
					this.limitPercentage == os.limitPercentage &&
					this.stopTrailingPercentage == os.stopTrailingPercentage){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String toString(){
		return String.format("%d_%.2f_%.2f", holdDuration, limitPercentage, stopTrailingPercentage);
	}
	
	public static SellStrategy[] genSS(PropertiesConfiguration props){
		float[] durations = new float[]{0};
		float[] limitPercentages = new float[]{0};
		float[] stopTrailingPercentages = new float[]{0};
		
		String strDurations = props.getString(SellStrategy.KEY_SELLS_DURATION);
		if (strDurations!=null){
			durations = StringUtil.parseSteps(strDurations);
		}
		String strLimitPercents = props.getString(SellStrategy.KEY_SELLS_LIMIT_PERCENTAGE);
		if (strLimitPercents!=null){
			limitPercentages = StringUtil.parseSteps(strLimitPercents);
		}
		String strStopTrailingPercents = props.getString(SellStrategy.KEY_SELLS_TRAIL_PERCENTAGE);
		if (strStopTrailingPercents!=null){
			stopTrailingPercentages = StringUtil.parseSteps(strStopTrailingPercents);
		}
		List<SellStrategy> ssl = new ArrayList<SellStrategy>();
		for (float duration:durations){
			for (float lp:limitPercentages){
				for (float stp:stopTrailingPercentages){
					SellStrategy sls = new SellStrategy();
			        sls.setHoldDuration((int) duration);
			        sls.setLimitPercentage(lp);
			        sls.setStopTrailingPercentage(stp);
			        ssl.add(sls);
				}
			}
		}
		SellStrategy[] ssa = new SellStrategy[ssl.size()];
		return ssl.toArray(ssa);
        
	}
	public int getHoldDuration() {
		return holdDuration;
	}

	public void setHoldDuration(int holdDuration) {
		this.holdDuration = holdDuration;
	}

	public float getLimitPercentage() {
		return limitPercentage;
	}

	public void setLimitPercentage(float limitPercentage) {
		this.limitPercentage = limitPercentage;
	}

	public float getStopTrailingPercentage() {
		return stopTrailingPercentage;
	}

	public void setStopTrailingPercentage(float stopTrailingPercentage) {
		this.stopTrailingPercentage = stopTrailingPercentage;
	}
}
