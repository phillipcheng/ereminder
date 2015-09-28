package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.DateTimeUtil;


public class StockUtil {

	protected static Logger logger =  LogManager.getLogger(StockUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//http://markets.on.nytimes.com/research/markets/holidays/holidays.asp?display=market&exchange=SHH
	public static Set<Date> USHolidays = new HashSet<Date>();
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
	
	public static Set<Date> CNHolidays = new HashSet<Date>();
	static{
		try{
			//
			CNHolidays.add(sdf.parse("2005-01-03"));
			CNHolidays.add(sdf.parse("2005-02-07"));
			CNHolidays.add(sdf.parse("2005-02-08"));
			CNHolidays.add(sdf.parse("2005-02-09"));
			CNHolidays.add(sdf.parse("2005-02-10"));
			CNHolidays.add(sdf.parse("2005-02-11"));
			CNHolidays.add(sdf.parse("2005-02-12"));
			CNHolidays.add(sdf.parse("2005-02-13"));
			CNHolidays.add(sdf.parse("2005-02-14"));
			CNHolidays.add(sdf.parse("2005-02-15"));
			CNHolidays.add(sdf.parse("2005-05-01"));
			CNHolidays.add(sdf.parse("2005-05-02"));
			CNHolidays.add(sdf.parse("2005-05-03"));
			CNHolidays.add(sdf.parse("2005-05-04"));
			CNHolidays.add(sdf.parse("2005-05-05"));
			CNHolidays.add(sdf.parse("2005-05-06"));
			CNHolidays.add(sdf.parse("2005-05-07"));
			CNHolidays.add(sdf.parse("2005-10-01"));
			CNHolidays.add(sdf.parse("2005-10-02"));
			CNHolidays.add(sdf.parse("2005-10-03"));
			CNHolidays.add(sdf.parse("2005-10-04"));
			CNHolidays.add(sdf.parse("2005-10-05"));
			CNHolidays.add(sdf.parse("2005-10-06"));
			CNHolidays.add(sdf.parse("2005-10-07"));
			CNHolidays.add(sdf.parse("2005-10-08"));
			CNHolidays.add(sdf.parse("2005-10-09"));
			//
			CNHolidays.add(sdf.parse("2006-01-02"));
			CNHolidays.add(sdf.parse("2006-01-03"));
			CNHolidays.add(sdf.parse("2006-01-26"));
			CNHolidays.add(sdf.parse("2006-01-27"));
			CNHolidays.add(sdf.parse("2006-01-28"));
			CNHolidays.add(sdf.parse("2006-01-29"));
			CNHolidays.add(sdf.parse("2006-01-30"));
			CNHolidays.add(sdf.parse("2006-01-31"));
			CNHolidays.add(sdf.parse("2006-02-01"));
			CNHolidays.add(sdf.parse("2006-02-02"));
			CNHolidays.add(sdf.parse("2006-02-03"));
			CNHolidays.add(sdf.parse("2006-05-01"));
			CNHolidays.add(sdf.parse("2006-05-02"));
			CNHolidays.add(sdf.parse("2006-05-03"));
			CNHolidays.add(sdf.parse("2006-05-04"));
			CNHolidays.add(sdf.parse("2006-05-05"));
			CNHolidays.add(sdf.parse("2006-10-02"));
			CNHolidays.add(sdf.parse("2006-10-03"));
			CNHolidays.add(sdf.parse("2006-10-04"));
			CNHolidays.add(sdf.parse("2006-10-05"));
			CNHolidays.add(sdf.parse("2006-10-06"));
			//
			CNHolidays.add(sdf.parse("2007-01-01"));
			CNHolidays.add(sdf.parse("2007-01-02"));
			CNHolidays.add(sdf.parse("2007-01-03"));
			CNHolidays.add(sdf.parse("2007-02-19"));
			CNHolidays.add(sdf.parse("2007-02-20"));
			CNHolidays.add(sdf.parse("2007-02-21"));
			CNHolidays.add(sdf.parse("2007-02-22"));
			CNHolidays.add(sdf.parse("2007-02-23"));
			CNHolidays.add(sdf.parse("2007-05-01"));
			CNHolidays.add(sdf.parse("2007-05-02"));
			CNHolidays.add(sdf.parse("2007-05-03"));
			CNHolidays.add(sdf.parse("2007-05-04"));
			CNHolidays.add(sdf.parse("2007-05-05"));
			CNHolidays.add(sdf.parse("2007-05-06"));
			CNHolidays.add(sdf.parse("2007-05-07"));
			CNHolidays.add(sdf.parse("2007-10-01"));
			CNHolidays.add(sdf.parse("2007-10-02"));
			CNHolidays.add(sdf.parse("2007-10-03"));
			CNHolidays.add(sdf.parse("2007-10-04"));
			CNHolidays.add(sdf.parse("2007-10-05"));
			CNHolidays.add(sdf.parse("2007-10-06"));
			CNHolidays.add(sdf.parse("2007-10-07"));
			CNHolidays.add(sdf.parse("2007-12-31"));
			//
			CNHolidays.add(sdf.parse("2008-01-01"));
			CNHolidays.add(sdf.parse("2008-02-06"));
			CNHolidays.add(sdf.parse("2008-02-07"));
			CNHolidays.add(sdf.parse("2008-02-08"));
			CNHolidays.add(sdf.parse("2008-02-09"));
			CNHolidays.add(sdf.parse("2008-02-10"));
			CNHolidays.add(sdf.parse("2008-02-11"));
			CNHolidays.add(sdf.parse("2008-02-12"));
			CNHolidays.add(sdf.parse("2008-04-04"));
			CNHolidays.add(sdf.parse("2008-05-01"));
			CNHolidays.add(sdf.parse("2008-05-02"));
			CNHolidays.add(sdf.parse("2008-06-09"));
			CNHolidays.add(sdf.parse("2008-09-15"));
			CNHolidays.add(sdf.parse("2008-09-29"));
			CNHolidays.add(sdf.parse("2008-09-30"));
			CNHolidays.add(sdf.parse("2008-10-01"));
			CNHolidays.add(sdf.parse("2008-10-02"));
			CNHolidays.add(sdf.parse("2008-10-03"));
			//
			CNHolidays.add(sdf.parse("2009-01-01"));
			CNHolidays.add(sdf.parse("2009-01-02"));
			CNHolidays.add(sdf.parse("2009-01-25"));
			CNHolidays.add(sdf.parse("2009-01-26"));
			CNHolidays.add(sdf.parse("2009-01-27"));
			CNHolidays.add(sdf.parse("2009-01-28"));
			CNHolidays.add(sdf.parse("2009-01-29"));
			CNHolidays.add(sdf.parse("2009-01-30"));
			CNHolidays.add(sdf.parse("2009-01-31"));
			CNHolidays.add(sdf.parse("2009-04-06"));
			CNHolidays.add(sdf.parse("2009-05-01"));
			CNHolidays.add(sdf.parse("2009-05-02"));
			CNHolidays.add(sdf.parse("2009-05-28"));
			CNHolidays.add(sdf.parse("2009-05-29"));
			CNHolidays.add(sdf.parse("2009-10-01"));
			CNHolidays.add(sdf.parse("2009-10-02"));
			CNHolidays.add(sdf.parse("2009-10-03"));
			CNHolidays.add(sdf.parse("2009-10-04"));
			CNHolidays.add(sdf.parse("2009-10-05"));
			CNHolidays.add(sdf.parse("2009-10-06"));
			CNHolidays.add(sdf.parse("2009-10-07"));
			CNHolidays.add(sdf.parse("2009-10-08"));
			//
			CNHolidays.add(sdf.parse("2010-01-01"));
			CNHolidays.add(sdf.parse("2010-01-02"));
			CNHolidays.add(sdf.parse("2010-01-03"));
			CNHolidays.add(sdf.parse("2010-02-13"));
			CNHolidays.add(sdf.parse("2010-02-14"));
			CNHolidays.add(sdf.parse("2010-02-15"));
			CNHolidays.add(sdf.parse("2010-02-16"));
			CNHolidays.add(sdf.parse("2010-02-17"));
			CNHolidays.add(sdf.parse("2010-02-18"));
			CNHolidays.add(sdf.parse("2010-02-19"));
			CNHolidays.add(sdf.parse("2010-02-20"));
			CNHolidays.add(sdf.parse("2010-02-21"));
			CNHolidays.add(sdf.parse("2010-04-03"));
			CNHolidays.add(sdf.parse("2010-04-04"));
			CNHolidays.add(sdf.parse("2010-04-05"));
			CNHolidays.add(sdf.parse("2010-05-01"));
			CNHolidays.add(sdf.parse("2010-05-02"));
			CNHolidays.add(sdf.parse("2010-05-03"));
			CNHolidays.add(sdf.parse("2010-06-14"));
			CNHolidays.add(sdf.parse("2010-06-15"));
			CNHolidays.add(sdf.parse("2010-06-16"));
			CNHolidays.add(sdf.parse("2010-09-22"));
			CNHolidays.add(sdf.parse("2010-09-23"));
			CNHolidays.add(sdf.parse("2010-09-24"));
			CNHolidays.add(sdf.parse("2010-10-01"));
			CNHolidays.add(sdf.parse("2010-10-02"));
			CNHolidays.add(sdf.parse("2010-10-03"));
			CNHolidays.add(sdf.parse("2010-10-04"));
			CNHolidays.add(sdf.parse("2010-10-05"));
			CNHolidays.add(sdf.parse("2010-10-06"));
			CNHolidays.add(sdf.parse("2010-10-07"));
			//
			CNHolidays.add(sdf.parse("2011-01-03"));
			CNHolidays.add(sdf.parse("2011-02-02"));
			CNHolidays.add(sdf.parse("2011-02-03"));
			CNHolidays.add(sdf.parse("2011-02-04"));
			CNHolidays.add(sdf.parse("2011-02-07"));
			CNHolidays.add(sdf.parse("2011-02-08"));
			CNHolidays.add(sdf.parse("2011-04-04"));
			CNHolidays.add(sdf.parse("2011-04-05"));
			CNHolidays.add(sdf.parse("2011-05-02"));
			CNHolidays.add(sdf.parse("2011-06-06"));
			CNHolidays.add(sdf.parse("2011-09-12"));
			CNHolidays.add(sdf.parse("2011-10-03"));
			CNHolidays.add(sdf.parse("2011-10-04"));
			CNHolidays.add(sdf.parse("2011-10-05"));
			CNHolidays.add(sdf.parse("2011-10-06"));
			CNHolidays.add(sdf.parse("2011-10-07"));
			//
			CNHolidays.add(sdf.parse("2012-01-02"));
			CNHolidays.add(sdf.parse("2012-01-03"));
			CNHolidays.add(sdf.parse("2012-01-23"));
			CNHolidays.add(sdf.parse("2012-01-24"));
			CNHolidays.add(sdf.parse("2012-01-25"));
			CNHolidays.add(sdf.parse("2012-01-26"));
			CNHolidays.add(sdf.parse("2012-01-27"));
			CNHolidays.add(sdf.parse("2012-04-02"));
			CNHolidays.add(sdf.parse("2012-04-03"));
			CNHolidays.add(sdf.parse("2012-04-04"));
			CNHolidays.add(sdf.parse("2012-04-30"));
			CNHolidays.add(sdf.parse("2012-06-22"));
			CNHolidays.add(sdf.parse("2012-10-01"));
			CNHolidays.add(sdf.parse("2012-10-02"));
			CNHolidays.add(sdf.parse("2012-10-03"));
			CNHolidays.add(sdf.parse("2012-10-04"));
			CNHolidays.add(sdf.parse("2012-10-05"));
			//
			CNHolidays.add(sdf.parse("2013-01-01"));
			CNHolidays.add(sdf.parse("2013-01-02"));
			CNHolidays.add(sdf.parse("2013-01-03"));
			CNHolidays.add(sdf.parse("2013-02-11"));
			CNHolidays.add(sdf.parse("2013-02-12"));
			CNHolidays.add(sdf.parse("2013-02-13"));
			CNHolidays.add(sdf.parse("2013-02-14"));
			CNHolidays.add(sdf.parse("2013-02-15"));
			CNHolidays.add(sdf.parse("2013-04-04"));
			CNHolidays.add(sdf.parse("2013-04-05"));
			CNHolidays.add(sdf.parse("2013-04-29"));
			CNHolidays.add(sdf.parse("2013-04-30"));
			CNHolidays.add(sdf.parse("2013-05-01"));
			CNHolidays.add(sdf.parse("2013-06-10"));
			CNHolidays.add(sdf.parse("2013-06-11"));
			CNHolidays.add(sdf.parse("2013-06-12"));
			CNHolidays.add(sdf.parse("2013-09-19"));
			CNHolidays.add(sdf.parse("2013-09-20"));
			CNHolidays.add(sdf.parse("2013-10-01"));
			CNHolidays.add(sdf.parse("2013-10-02"));
			CNHolidays.add(sdf.parse("2013-10-03"));
			CNHolidays.add(sdf.parse("2013-10-04"));
			CNHolidays.add(sdf.parse("2013-10-07"));
			//
			CNHolidays.add(sdf.parse("2014-01-01"));
			CNHolidays.add(sdf.parse("2014-01-31"));
			CNHolidays.add(sdf.parse("2014-02-03"));
			CNHolidays.add(sdf.parse("2014-02-04"));
			CNHolidays.add(sdf.parse("2014-02-05"));
			CNHolidays.add(sdf.parse("2014-02-06"));
			CNHolidays.add(sdf.parse("2014-04-07"));
			CNHolidays.add(sdf.parse("2014-05-01"));
			CNHolidays.add(sdf.parse("2014-05-02"));
			CNHolidays.add(sdf.parse("2014-06-02"));
			CNHolidays.add(sdf.parse("2014-09-08"));
			CNHolidays.add(sdf.parse("2014-10-01"));
			CNHolidays.add(sdf.parse("2014-10-02"));
			CNHolidays.add(sdf.parse("2014-10-03"));
			CNHolidays.add(sdf.parse("2013-10-06"));
			CNHolidays.add(sdf.parse("2013-10-07"));
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
	
	//[fromDate, toDate)
	public static LinkedList<Date> getOpenDayList(Date fromDate, Date toDate, Set<Date> holidays){
		LinkedList<Date> dll = new LinkedList<Date>();
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		Date d = fromDate;
		while (d.before(toDate)){
			if (isOpenDay(d, holidays)){
				dll.add(d);
			}
			d = getNextOpenDay(d, holidays);
		}
		return dll;
	}
	
	public static Date getNextOpenDay(Date d, Set<Date> holidays){
		Calendar c = Calendar.getInstance();
		Date day = DateTimeUtil.tomorrow(d);
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
