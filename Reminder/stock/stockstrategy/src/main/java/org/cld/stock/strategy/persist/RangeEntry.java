package org.cld.stock.strategy.persist;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RangeEntry {
	
	private String symbol;
	private Date dt;
	private float buyPrice;
	
	public RangeEntry(String symbol, Date dt, float buyPrice){
		this.symbol = symbol;
		this.dt = dt;
		this.buyPrice = buyPrice;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Date getDt() {
		return dt;
	}
	public void setDt(Date dt) {
		this.dt = dt;
	}
	public float getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof RangeEntry){
			RangeEntry re = (RangeEntry) o;
			return (this.symbol.equals(re.getSymbol()) && 
					//this.dt.equals(re.getDt()) && 
					this.buyPrice==re.getBuyPrice());
		}else{
			return false;
		}
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public String toString(){
		return String.format("%s,%s,%.2f", symbol, sdf.format(dt), buyPrice);
	}

}
