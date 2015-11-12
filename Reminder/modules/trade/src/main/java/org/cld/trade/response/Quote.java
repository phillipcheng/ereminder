package org.cld.trade.response;

import java.util.Map;

public class Quote {
	public static final String LAST="last";
	public static final String SYMBOL="symbol";
	public static final String ASK="ask";
	public static final String BID="bid";
	public static final String VL="vl";
	public static final String OPEN="opn";
	
	private String symbol;
	private float last;
	private float ask;
	private float bid;
	private double volume;
	private float open;
	
	public static String[] getAllFields(){
		return new String[]{LAST, SYMBOL, ASK, BID, VL, OPEN};
	}
	public Quote(){
	}
	
	public Quote(Map<String, Object> map){
		setSymbol((String) map.get(SYMBOL));
		setLast(Float.parseFloat((String) map.get(LAST)));
		ask = Float.parseFloat((String) map.get(ASK));
		bid = Float.parseFloat((String) map.get(BID));
		volume = Float.parseFloat((String) map.get(VL));	
		setOpen(Float.parseFloat((String) map.get(OPEN)));
	}
	
	public String toString(){
		return String.format("Q:%s,%.3f,%.3f,%.3f,%.3f,%.3f", symbol, open, last, ask, bid, volume);
	}
	//
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float getLast() {
		return last;
	}
	public void setLast(float last) {
		this.last = last;
	}
	public float getAsk() {
		return ask;
	}
	public void setAsk(float ask) {
		this.ask = ask;
	}
	public float getBid() {
		return bid;
	}
	public void setBid(float bid) {
		this.bid = bid;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}	
}
