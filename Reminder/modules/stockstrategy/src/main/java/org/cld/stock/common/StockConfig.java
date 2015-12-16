package org.cld.stock.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.StrategyConst;
import org.cld.util.FileDataMapper;
import org.cld.util.jdbc.JDBCMapper;

public abstract class StockConfig {
	protected static Logger logger =  LogManager.getLogger(StockConfig.class);
	
	protected SimpleDateFormat sdf = null;
		
	public abstract TimeZone getTimeZone();
	public abstract Set<Date> getHolidays();
	public abstract float getDailyLimit();//>0 means has price limit n% up and down, if<=0 no limit
	
	public abstract JDBCMapper getDailyQuoteTableMapper();
	public abstract JDBCMapper getFQDailyQuoteTableMapper();
	public abstract JDBCMapper getSplitTableMapper();//future
	public abstract JDBCMapper getDividendTableMapper();//future
	public abstract JDBCMapper getExDivSplitHistoryTableMapper();//history
	public abstract JDBCMapper getEarnTableMapper();
	
	//for back testing, BT
	public abstract FileDataMapper getBTFQDailyQuoteMapper();
	public abstract FileDataMapper getBTFQMinuteQuoteMapper();
	
	public static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat dsdf = new SimpleDateFormat("yyyy-MM-dd");
	public abstract String getMarketStart();
	public abstract String getMarketEnd();
	
	public Date getLatestOpenMarketDate(Date d) {
		while (!StockUtil.isOpenDay(d, getHolidays())){
			d = StockUtil.getLastOpenDay(d, getHolidays());
		}
		return d;
	}
	
	public Date getNormalTradeStartTime(Date d){
		Date sd = null;
		try{
			sd = msdf.parse(String.format("%s %s", dsdf.format(d), getMarketStart()));
		}catch(Exception e){
			logger.error("", e);
		}
		return sd;
	}
	public Date getNormalTradeEndTime(Date d){
		Date sd = null;
		try{
			sd = msdf.parse(String.format("%s %s", dsdf.format(d), getMarketEnd()));
		}catch(Exception e){
			logger.error("", e);
		}
		return sd;
	}
	public Date getCloseTime(Date d, int holdDuration, String unit){
		if (unit.equals(StrategyConst.V_UNIT_DAY)){
			Date sd = d;
			if (holdDuration>1){
				sd = StockUtil.getNextOpenDay(d, this.getHolidays(), holdDuration-1);
			}
			//then set the time to market end
			try{
				Date ed = msdf.parse(String.format("%s %s", dsdf.format(sd), getMarketEnd()));
				return ed;
			}catch(Exception e){
				logger.error("", e);
			}
		}else if (unit.equals(StrategyConst.V_UNIT_MINUTE)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, holdDuration);
			return cal.getTime();
		}else{
			logger.error("unsupported unit:" + unit);
		}
		return null;
	}
	
	public String[] getAllStrategy(){
		return new String[]{"random", "rally", "closedrop", "closedropavg", "closeraiseavg"};
	}

	public StockConfig() {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}
	
	public SimpleDateFormat getSdf(){
		return sdf;
	}
}
