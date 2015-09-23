package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StockUtil {
	/**
	 * US 
	 * TimeZone: ET
	 * 2014	Holiday	Status
		January 01, 2014	New Year's Day (Observed)	Closed
		January 20, 2014	Martin Luther King, Jr. Day	Closed
		February 17, 2014	President's Day - U.S.	Closed
		April 18, 2014	Good Friday	Closed
		May 26, 2014	Memorial Day - U.S.	Closed
					July 03, 2014	Early Close - U.S.	1:00 p.m.
		July 04, 2014	Independence Day - U.S.	Closed
		September 01, 2014	Labor Day - U.S.	Closed
		November 27, 2014	Thanksgiving Day - U.S.	Closed
					November 28, 2014	Early Close - U.S.	1:00 p.m.
					December 24, 2014	Early Close - U.S.	1:00 p.m.
		December 25, 2014	Christmas Day	Closed

	 * 2015	Holiday	Status
		January 01, 2015	New Year's Day (Observed)	Closed
		January 19, 2015	Martin Luther King, Jr. Day	Closed
		February 16, 2015	President's Day - U.S.	Closed
		April 3, 2015	Good Friday	Closed
		May 25, 2015	Memorial Day - U.S.	Closed
		July 03, 2015	Independence Day - U.S. (Observed)	Closed
		September 07, 2015	Labor Day - U.S.	Closed
		November 26, 2015	Thanksgiving Day - U.S.	Closed
				November 27, 2015	Early Close - U.S.	1:00 p.m.
				December 24, 2015	Christmas Eve	1:00 p.m.
		December 25, 2015	Christmas Day	Closed
	 */

	protected static Logger logger =  LogManager.getLogger(StockUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//http://markets.on.nytimes.com/research/markets/holidays/holidays.asp?display=market&exchange=SHH
	public static List<Date> USHolidays = new ArrayList<Date>();
	static{
		try{
			USHolidays.add(sdf.parse("2014-01-01"));
			USHolidays.add(sdf.parse("2014-01-20"));
			USHolidays.add(sdf.parse("2014-02-17"));
			USHolidays.add(sdf.parse("2014-04-18"));
			USHolidays.add(sdf.parse("2014-05-26"));
			USHolidays.add(sdf.parse("2014-07-04"));
			USHolidays.add(sdf.parse("2014-09-01"));
			USHolidays.add(sdf.parse("2014-11-27"));
			USHolidays.add(sdf.parse("2014-12-25"));
			//
			USHolidays.add(sdf.parse("2015-01-01"));
			USHolidays.add(sdf.parse("2015-01-19"));
			USHolidays.add(sdf.parse("2015-02-16"));
			USHolidays.add(sdf.parse("2015-04-03"));
			USHolidays.add(sdf.parse("2015-05-25"));
			USHolidays.add(sdf.parse("2015-07-03"));
			USHolidays.add(sdf.parse("2015-09-07"));
			USHolidays.add(sdf.parse("2015-11-26"));
			USHolidays.add(sdf.parse("2015-12-25"));
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static List<Date> CNHolidays = new ArrayList<Date>();
	static{
		try{
			//
			CNHolidays.add(sdf.parse("2015-01-01"));
			CNHolidays.add(sdf.parse("2015-01-02"));
			CNHolidays.add(sdf.parse("2015-02-18"));
			CNHolidays.add(sdf.parse("2015-02-19"));
			CNHolidays.add(sdf.parse("2015-02-20"));
			CNHolidays.add(sdf.parse("2015-02-23"));
			CNHolidays.add(sdf.parse("2015-02-24"));
			CNHolidays.add(sdf.parse("2015-04-06"));
			CNHolidays.add(sdf.parse("2015-05-01"));
			CNHolidays.add(sdf.parse("2015-06-22"));
			CNHolidays.add(sdf.parse("2015-09-03"));
			CNHolidays.add(sdf.parse("2015-09-04"));
			CNHolidays.add(sdf.parse("2015-10-01"));
			CNHolidays.add(sdf.parse("2015-10-02"));
			CNHolidays.add(sdf.parse("2015-10-05"));
			CNHolidays.add(sdf.parse("2015-10-06"));
			CNHolidays.add(sdf.parse("2015-10-07"));
			//
			CNHolidays.add(sdf.parse("2016-01-01"));
			CNHolidays.add(sdf.parse("2016-02-08"));
			CNHolidays.add(sdf.parse("2016-02-09"));
			CNHolidays.add(sdf.parse("2016-02-10"));
			CNHolidays.add(sdf.parse("2016-02-11"));
			CNHolidays.add(sdf.parse("2016-02-12"));
			CNHolidays.add(sdf.parse("2016-04-04"));
			CNHolidays.add(sdf.parse("2016-05-02"));
			CNHolidays.add(sdf.parse("2016-06-09"));
			CNHolidays.add(sdf.parse("2016-06-10"));
			CNHolidays.add(sdf.parse("2016-09-15"));
			CNHolidays.add(sdf.parse("2016-09-16"));
			CNHolidays.add(sdf.parse("2016-10-03"));
			CNHolidays.add(sdf.parse("2016-10-04"));
			CNHolidays.add(sdf.parse("2016-10-05"));
			CNHolidays.add(sdf.parse("2016-10-06"));
			CNHolidays.add(sdf.parse("2016-10-07"));
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static boolean daysContainDay(List<Date> dl, Date d){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("EST"));
		for (Date d1:dl){
			c.setTime(d1);
			int day1 = c.get(Calendar.DAY_OF_YEAR);
			int year1 = c.get(Calendar.YEAR);
			
			c.setTime(d);
			int day2 = c.get(Calendar.DAY_OF_YEAR);
			int year2 = c.get(Calendar.YEAR);
			if (day1==day2 && year1==year2){
				return true;
			}
		}
		return false;
	}
	
	//d without time
	public static Date getLastOpenDay(Date d, List<Date> holidays){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
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
		return c.getTime();
	}
	
	public static boolean isOpenDay(Date d, List<Date> holidays){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int dow = c.get(Calendar.DAY_OF_WEEK);
		//check weekend
		if (dow==Calendar.SUNDAY ||
				dow == Calendar.SATURDAY ||
					daysContainDay(holidays, c.getTime())){
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
