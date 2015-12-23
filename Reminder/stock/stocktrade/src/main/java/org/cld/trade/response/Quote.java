package org.cld.trade.response;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.TradeTick;
import org.cld.util.SafeSimpleDateFormat;

public class Quote {
	private static Logger logger =  LogManager.getLogger(Quote.class);
	private static final SafeSimpleDateFormat sdf = new SafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	public static final String LAST="last";
	public static final String SYMBOL="symbol";
	public static final String VL="incr_vl";//volume of last trade
	public static final String CVL="vl";//Cumulative volume
	public static final String DATETIME="datetime";
	
	private Date dt;
	private String symbol;
	private float last;
	private long volume;
	private long cumuVol;
	
	public static String[] getAllFields(){
		return new String[]{LAST, SYMBOL, VL, DATETIME, CVL};
	}
	public Quote(){
	}
	
	public TradeTick toTradeTick(){
		return new TradeTick(dt, last, volume);
	}
	
	public Quote(String symbol, TradeTick tt){
		this.dt = tt.getDatetime();
		this.symbol = symbol;
		this.last = tt.getLast();
		this.volume = tt.getVl();
	}
	
	public Quote(Map<String, Object> map){
		setSymbol((String) map.get(SYMBOL));
		try {
			this.dt = sdf.parse((String) map.get(DATETIME));
		}catch(Exception e){
			this.dt = new Date();
		}	
		setLast(Float.parseFloat((String) map.get(LAST)));
		try{
			volume = Long.parseLong((String) map.get(VL));
			setCumuVol(Long.parseLong((String)map.get(CVL)));
		}catch(Exception e){
			volume = 0;
			setCumuVol(0);
		}
	}
	
	public String toString(){
		return String.format("Q:%s,%s,%.3f,%d", symbol, sdf.format(dt), last, volume);
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
	public double getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public Date getDt() {
		return dt;
	}
	public void setDt(Date dt) {
		this.dt = dt;
	}
	public long getCumuVol() {
		return cumuVol;
	}
	public void setCumuVol(long cumuVol) {
		this.cumuVol = cumuVol;
	}	
}
