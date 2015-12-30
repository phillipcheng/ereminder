package org.cld.stock.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DivSplit {
	
	private String symbol;
	private Date exDt; //ex-div date
	private Date dt; //announce date
	private float dividend=0f; //per stock, 0 means a split
	private AnnounceTime at = AnnounceTime.beforeMarket;//announce time
	private String info; 
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public DivSplit(String symbol, Date dt, Date exDt, String info){
		this.symbol = symbol;
		this.dt = dt;
		this.exDt = exDt;
		this.info = info;
	}
	
	public DivSplit(String symbol, Date dt, Date exDt, float dividend){
		this.symbol = symbol;
		this.dt = dt;
		this.exDt = exDt;
		this.dividend = dividend;
	}
	
	public DivSplit(String symbol, Date dt, Date exDt, float dividend, AnnounceTime at){
		this(symbol, dt, exDt, dividend);
		this.setAt(at);
	}
	
	public String toString(){
		return String.format("divsplit: %s: xdiv day:%s, dividend:%.3f, splitInfo:%s", symbol, sdf.format(exDt), dividend, info);
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
