package org.cld.stock.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.config.HKStockConfig;
import org.cld.stock.config.NasdaqStockConfig;
import org.cld.stock.config.SinaStockConfig;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.util.DateTimeUtil;

public class StockUtil {
	public static final String SINA_STOCK_BASE="sina";
	public static final String NASDAQ_STOCK_BASE="nasdaq";
	public static final String HK_STOCK_BASE="hk";

	public static final String KEY_BASE_MARKET_ID="baseMarketId";
	
	protected static Logger logger =  LogManager.getLogger(StockUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final long DAY_MILLI = 24*60*60*1000L; 
	
	public static long normalOpenMinute = 9*60+30;
	public static long normalCloseMinute = 16*60;
	
	public static StockConfig getStockConfig(String stockBase){
		if (SINA_STOCK_BASE.equals(stockBase)){
			return new SinaStockConfig();
		}else if (NASDAQ_STOCK_BASE.equals(stockBase)){
			return new NasdaqStockConfig();
		}else if (HK_STOCK_BASE.equals(stockBase)){
			return new HKStockConfig();
		}else{
			logger.error(String.format("stockBase %s not supported.", stockBase));
			return null;
		}
	}
	
	//[fromDate, toDate)
	public static LinkedList<Date> getOpenDayList(Date fromDate, Date toDate, Set<Date> holidays){
		LinkedList<Date> dll = new LinkedList<Date>();
		Date d = fromDate;
		while (d.before(toDate)){
		//while (!toDate.after(d)){
			if (isOpenDay(d, holidays)){
				dll.add(d);
			}
			d = getNextOpenDay(d, holidays);
		}
		return dll;
	}
	//get up to n open days
	public static Date getNextOpenDay(Date d, Set<Date> holidays, int n){
		Date nd = d;
		for(int i=0;i<n;i++){
			nd = getNextOpenDay(nd, holidays);
		}
		return nd;
	}
	//get 1 next open days
	public static Date getNextOpenDay(Date d, Set<Date> holidays){
		Date day = DateTimeUtil.tomorrow(d);
		Calendar c = Calendar.getInstance();
		c.setTime(day);
		while (!isOpenDay(day, holidays)){
			int dow = c.get(Calendar.DAY_OF_WEEK);
			if (dow==Calendar.SUNDAY){
				c.add(Calendar.DATE, +1);
			}else if (dow == Calendar.SATURDAY){
				c.add(Calendar.DATE, +2);
			}
			if (holidays.contains(c.getTime())){
				c.add(Calendar.DATE, +1);
			}
			day = c.getTime();
		}
		return day;
	}
	
	//including today
	public static Date getLastOpenDay(Date d, Set<Date> holidays){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Date day = d;
		while (!isOpenDay(day, holidays)){
			int dow = c.get(Calendar.DAY_OF_WEEK);
			//check weekend
			if (dow==Calendar.SUNDAY){
				c.add(Calendar.DATE, -2);
			}else if (dow == Calendar.SATURDAY){
				c.add(Calendar.DATE, -1);
			}
			if (holidays.contains(c.getTime())){
				c.add(Calendar.DATE, -1);
			}
			day = c.getTime();
		}
		return day;
	}
	
	public static boolean isOpenDay(Date d, Set<Date> holidays){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int dow = c.get(Calendar.DAY_OF_WEEK);
		//check weekend
		if (dow==Calendar.SUNDAY ||
				dow == Calendar.SATURDAY ||
					holidays.contains(c.getTime())){
			return false;
		}else{
			return true;
		}
	}

	public static String getDate(int year, int quarter){
		String dt = null;
		if (quarter == 1){
			dt = "03-31";
		}else if (quarter ==2){
			dt = "06-30";
		}else if (quarter == 3){
			dt = "09-30";
		}else if (quarter == 4){
			dt = "12-31";
		}else{
			logger.error(String.format("wrong quarter %d", quarter));
		}
		return year + "-" + dt;
	}
	
	public static boolean filterByTradeHour(CandleQuote cq, TradeHour th){
		if (th == TradeHour.Normal){
			int minute = cq.getStartTime().getHours() *60 + cq.getStartTime().getMinutes();
			if (minute>=normalOpenMinute && minute<=normalCloseMinute){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	public static Date getPeriodStart(Date dt, IntervalUnit iu, StockConfig sc){
		Calendar cal = Calendar.getInstance(sc.getTimeZone());
		if (iu == IntervalUnit.minute){
			cal.setTime(dt);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTime();
		}else if (iu == IntervalUnit.day){
			cal.setTime(dt);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.HOUR_OF_DAY, 9);//TODO use sc
			cal.set(Calendar.MINUTE, 30);
			return cal.getTime();
		}else{
			logger.error(String.format("unsupported interval unit:%s", iu));
			return null;
		}
	}
	
	//aggregate input to this, if new generated returned
	public static CandleQuote aggregate(CandleQuote curCq, CandleQuote input, IntervalUnit iu, StockConfig sc){
		Date inputPeriodStart = getPeriodStart(input.getStartTime(), iu, sc);
		if (curCq!=null && inputPeriodStart.equals(curCq.getStartTime())){
			if (input.getHigh()>curCq.getHigh()){
				curCq.setHigh(input.getHigh());
			}
			if (input.getLow()<curCq.getLow()){
				curCq.setLow(input.getLow());
			}
			curCq.setClose(input.getClose());
			curCq.setVolume(input.getVolume()+curCq.getVolume());
			return null;
		}else{
			CandleQuote output = input.clone();
			output.setStartTime(inputPeriodStart);
			return output;
		}
	}

	public static List<String> getSymbols(String symbolFile){
		List<String> symbols = new ArrayList<String>();
		try {
			InputStream in = StockUtil.class.getClassLoader().getResourceAsStream(symbolFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line=br.readLine())!=null){
				symbols.add(line.trim());
			}
			br.close();
		}catch(Exception e){
			logger.error("", e);
		}
		return symbols;
	}
	
	public static List<String[]> getTableData(String dataFile){
		List<String[]> data = new ArrayList<String[]>();
		try {
			InputStream in = StockUtil.class.getClassLoader().getResourceAsStream(dataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line=br.readLine())!=null){
				String[] d = line.split(",");
				data.add(d);
			}
			br.close();
		}catch(Exception e){
			logger.error("", e);
		}
		return data;
	}
}
