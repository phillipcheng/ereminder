package org.cld.stock;

import java.util.Date;

public class Dividend {
	
	private String stockid;
	private Date dt; //announce date
	private float dividend; //per stock
	private AnnounceTime at = AnnounceTime.beforeMarket;//announce time
	
	public Dividend(String stockid, Date dt, float dividend){
		this.stockid = stockid;
		this.dt = dt;
		this.dividend = dividend;
	}
	
	public Dividend(String stockid, Date dt, float dividend, AnnounceTime at){
		this.stockid = stockid;
		this.dt = dt;
		this.dividend = dividend;
		this.setAt(at);
	}
	
	public String getStockid() {
		return stockid;
	}
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	public Date getDt() {
		return dt;
	}
	public void setDt(Date dt) {
		this.dt = dt;
	}
	public float getDividend() {
		return dividend;
	}
	public void setDividend(float dividend) {
		this.dividend = dividend;
	}
	public AnnounceTime getAt() {
		return at;
	}
	public void setAt(AnnounceTime at) {
		this.at = at;
	}
}
