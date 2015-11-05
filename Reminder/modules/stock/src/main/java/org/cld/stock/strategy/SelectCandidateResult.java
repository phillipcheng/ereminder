package org.cld.stock.strategy;

public class SelectCandidateResult {
	
	private String dt;
	private float value;
	private float buyPrice;
	
	public SelectCandidateResult(String dt, float value){
		this.dt = dt;
		this.value = value;
	}
	
	public SelectCandidateResult(String dt, float value, float buyPrice){
		this.dt = dt;
		this.value = value;
		this.setBuyPrice(buyPrice);
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public float getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
	}

}
