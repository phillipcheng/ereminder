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
	public static final String TIMESTAMP="timestamp";
	public static final String SHARES_OUTSTANDING="sho";
	public static final String AVG_DAILY_V_90="adv_90";
	public static final String EPS="eps";
	public static final String Indicated_Annual_Dividend="iad";
	public static final String YIELD="yield";
	public static final String ASK="ask";
	public static final String BID="bid";
	
	
	private Date dt;
	private String symbol;
	private float last;
	private long volume;
	private long cumuVol;
	private long sho;
	private long adv90;
	private float eps;
	private float iad;
	private float yield;
	private float bid;
	private float ask;
	
	public static String[] getRegularQuoteFields(){
		return new String[]{LAST, SYMBOL, VL, DATETIME, CVL, TIMESTAMP};
	}
	
	public static String[] getExtendeHourQuoteFields(){
		return new String[]{LAST, SYMBOL, VL, DATETIME, CVL, TIMESTAMP, BID, ASK};
	}
	
	public static String[] getQuoteAndBasicFields(){
		return new String[]{SYMBOL, LAST, BID, ASK, EPS, Indicated_Annual_Dividend, YIELD, SHARES_OUTSTANDING, AVG_DAILY_V_90};
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
	
	public static final String NA="na";
	
	public Quote(Map<String, Object> map, boolean extendedHour){
		setSymbol((String) map.get(SYMBOL));
		try {
			//this.dt = sdf.parse((String) map.get(DATETIME));
			//for pre and after market the datetime is always the pre or after market start time, so use timestamp
			this.dt = new Date(Long.parseLong((String) map.get(TIMESTAMP))*1000);
		}catch(Exception e){
			this.dt = new Date();
		}	
		setLast(Float.parseFloat((String) map.get(LAST)));
		try{
			if (map.containsKey(BID)){
				bid = Float.parseFloat((String) map.get(BID));
			}
			if (map.containsKey(ASK)){
				ask = Float.parseFloat((String) map.get(ASK));
			}
			if (extendedHour){
				if (bid!=0 && ask!=0){
					if ((ask-bid)/(ask+bid)<0.4){
						last = (bid + ask)/2;
					}
				}
			}
			if (map.containsKey(VL)){
				volume = Long.parseLong((String) map.get(VL));
			}
			if (map.containsKey(CVL)){
				setCumuVol(Long.parseLong((String)map.get(CVL)));
			}
			if (map.containsKey(EPS) && !NA.equals(map.get(EPS))){
				this.eps = Float.parseFloat((String) map.get(EPS));
			}
			if (map.containsKey(Indicated_Annual_Dividend) && !NA.equals(map.get(Indicated_Annual_Dividend))){
				this.iad = Float.parseFloat((String) map.get(Indicated_Annual_Dividend));
			}
			if (map.containsKey(YIELD) && !NA.equals(map.get(YIELD))){
				this.yield = Float.parseFloat((String) map.get(YIELD));
			}
			if (map.containsKey(SHARES_OUTSTANDING) && !NA.equals(map.get(SHARES_OUTSTANDING))){
				this.sho = Long.parseLong((String) map.get(SHARES_OUTSTANDING));
			}
			if (map.containsKey(AVG_DAILY_V_90) && !NA.equals(map.get(AVG_DAILY_V_90))){
				this.adv90 = Long.parseLong((String) map.get(AVG_DAILY_V_90));
			}
		}catch(Exception e){
			volume = 0;
			setCumuVol(0);
			logger.debug("", e);
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

	public long getSho() {
		return sho;
	}

	public void setSho(long sho) {
		this.sho = sho;
	}

	public long getAdv90() {
		return adv90;
	}

	public void setAdv90(long adv90) {
		this.adv90 = adv90;
	}

	public float getEps() {
		return eps;
	}

	public void setEps(float eps) {
		this.eps = eps;
	}

	public float getIad() {
		return iad;
	}

	public void setIad(float iad) {
		this.iad = iad;
	}

	public float getYield() {
		return yield;
	}

	public void setYield(float yield) {
		this.yield = yield;
	}

	public float getBid() {
		return bid;
	}

	public void setBid(float bid) {
		this.bid = bid;
	}

	public float getAsk() {
		return ask;
	}

	public void setAsk(float ask) {
		this.ask = ask;
	}	
}
