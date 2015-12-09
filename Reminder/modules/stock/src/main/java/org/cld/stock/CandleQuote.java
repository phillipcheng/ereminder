package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class CandleQuote {
	String stockid;
	Date startTime;
	//all price are comparable across the dates
	float open;
	float high;
	float close;
	float low;
	double volume;
	double amount;
	float fqIdx;//adjclose/close
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final SimpleDateFormat mdf = new SimpleDateFormat("MM-dd HH:mm");
	
	public String toString(){
		return String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f", stockid, sdf.format(startTime), open, high, close, low, volume);
	}
	
	public static String onlyDate(List<CandleQuote> cql){
		StringBuffer sb = new StringBuffer();
		for (CandleQuote cq: cql){
			sb.append(mdf.format(cq.getStartTime()));
			sb.append(",");
		}
		return sb.toString();
	}

	public CandleQuote(String stockid, Date startTime, float open, float high, float close, float low, double volume, double amount){
		this.stockid = stockid;
		this.startTime = startTime;
		this.open = open;
		this.high = high;
		this.close = close;
		this.low = low;
		this.volume = volume;
		this.amount = amount;
	}
	
	public CandleQuote(String stockid, Date startTime, float open, float high, float close, float low, double volume){
		this.stockid = stockid;
		this.startTime = startTime;
		this.open = open;
		this.high = high;
		this.close = close;
		this.low = low;
		this.volume = volume;
	}
	
	public String getStockid() {
		return stockid;
	}
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public float getFqIdx() {
		return fqIdx;
	}
	public void setFqIdx(float fqIdx) {
		this.fqIdx = fqIdx;
	}
}
