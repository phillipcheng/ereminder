package org.cld.stock.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

import org.cld.stock.strategy.IntervalUnit;

public class CandleQuote implements TimedItem{
	String symbol;
	Date startTime;
	//all price are comparable across the dates
	float open;
	float high;
	float close;
	float low;
	double volume;
	double amount;
	float fqIdx;//adjclose/close
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat mdf = new SimpleDateFormat("MM-dd HH:mm");
	
	public String toString(){
		return String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f", symbol, sdf.format(startTime), open, high, close, low, volume);
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
		this.symbol = stockid;
		this.startTime = startTime;
		this.open = open;
		this.high = high;
		this.close = close;
		this.low = low;
		this.volume = volume;
		this.amount = amount;
	}
	
	public CandleQuote(String stockid, Date startTime, float open, float high, float close, float low, double volume){
		this.symbol = stockid;
		this.startTime = startTime;
		this.open = open;
		this.high = high;
		this.close = close;
		this.low = low;
		this.volume = volume;
	}
	
	public CandleQuote clone(){
		CandleQuote cq = new CandleQuote(this.symbol, this.startTime, this.open, this.high, this.close, this.low, this.volume, this.amount);
		return cq;
	}
	
	public CandleQuote(TradeTick tt){
		this.startTime = tt.getDatetime();
		this.open = tt.getLast();
		this.close = tt.getLast();
		this.high = tt.getLast();
		this.low = tt.getLast();
		this.volume = tt.getVl();
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	@Override
	public Date getDatetime() {
		return startTime;
	}

	@Override
	public String toCsv(TimeZone tz) {
		sdf.setTimeZone(tz);
		return String.format("%s,%.3f,%.3f,%.3f,%.3f,%.2f", sdf.format(startTime), open, high, close, low, volume);
	}
}
