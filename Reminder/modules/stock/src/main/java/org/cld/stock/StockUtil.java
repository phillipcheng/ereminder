package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.util.DateTimeUtil;


public class StockUtil {
	public static final String SINA_STOCK_BASE="sina";
	public static final String NASDAQ_STOCK_BASE="nasdaq";
	
	protected static Logger logger =  LogManager.getLogger(StockUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static StockConfig getStockConfig(String stockBase){
		if (SINA_STOCK_BASE.equals(stockBase)){
			return new SinaStockConfig();
		}else if (NASDAQ_STOCK_BASE.equals(stockBase)){
			return new NasdaqStockConfig();
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

}
