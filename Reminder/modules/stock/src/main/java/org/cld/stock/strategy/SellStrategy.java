package org.cld.stock.strategy;

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
