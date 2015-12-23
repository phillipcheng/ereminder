package org.cld.stock.strategy;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectCandidateResult {
	private String symbol;
	private Date dt;//the submit time
	private float value;
	private float buyPrice;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public SelectCandidateResult(String symbol, Date dt, float value, float buyPrice){
		this.setSymbol(symbol);
		this.dt = dt;
		this.value = value;
		this.setBuyPrice(buyPrice);
	}
	public String toString(){
		return String.format("%s,%s,%.4f,%.3f", symbol, sdf.format(dt), value, buyPrice);
	}
	
	public Date getDt() {
		return dt;
	}
	public void setDt(Date dt) {
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
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
