package org.cld.stock;

import java.util.Date;

public class DivSplit {
	
	private String stockid;
	private Date exDt; //ex-div date
	private Date dt; //announce date
	private float dividend; //per stock, 0 means a split
	private AnnounceTime at = AnnounceTime.beforeMarket;//announce time
	private String info; 
	
	public DivSplit(String stockid, Date dt, Date exDt, String info){
		this.stockid = stockid;
		this.dt = dt;
		this.exDt = exDt;
		this.info = info;
	}
	
	public DivSplit(String stockid, Date dt, Date exDt, float dividend){
		this.stockid = stockid;
		this.dt = dt;
		this.exDt = exDt;
		this.dividend = dividend;
	}
	
	public DivSplit(String stockid, Date dt, Date exDt, float dividend, AnnounceTime at){
		this(stockid, dt, exDt, dividend);
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getExDt() {
		return exDt;
	}

	public void setExDt(Date exDt) {
		this.exDt = exDt;
	}
}
