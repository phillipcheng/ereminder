package org.cld.stock.strategy;

public class SelectCandidateResult {
	private String stockId;
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
	
	public SelectCandidateResult(String stockid, String dt, float value, float buyPrice){
		this.setStockId(stockid);
		this.dt = dt;
		this.value = value;
		this.setBuyPrice(buyPrice);
	}
	public String toString(){
		return String.format("%s,%s,%.4f,%.3f", stockId, dt, value, buyPrice);
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

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

}
