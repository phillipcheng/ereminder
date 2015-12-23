package org.cld.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateTimeUtil {
	public static final Logger logger = LogManager.getLogger(DateTimeUtil.class);
	
	public static final String CH_TO="到";
	public static final String CH_YEAR="年";
	public static final String CH_MONTH="月";
	public static final String CH_DAY="日";
	
	public static SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd");
	public static String dateformat="MM/dd/yyyy";
	public static String sortableDateFormat="yyyy/MM/dd";
	public static String fileNameDateFormat="yyyyMMdd";
	public static String detailedDateformat="MM/dd/yyyy/HH/mm/ss/SSS";
	public static String hmDataFormat="yyyy/MM/dd/HH/mm";
	public static SimpleDateFormat sdf = new SimpleDateFormat(dateformat);	
	public static SimpleDateFormat sddf = new SimpleDateFormat(detailedDateformat);
	public static SimpleDateFormat ssdf = new SimpleDateFormat(sortableDateFormat);
	public static SimpleDateFormat sfdf = new SimpleDateFormat(fileNameDateFormat);
	public static SimpleDateFormat shmdf = new SimpleDateFormat(hmDataFormat);
	
	public static long MILL_SEC_DAY = 1000 * 60 * 60 * 24;	
	public static long MILL_SEC_MARKET_START_MORNINING = 1000 * 60 * 6 * 95; //9:30 in the morning
	public static long MILL_SEC_MARKET_STOP_MORNINING = 1000 * 60 * 6 * 115; //9:30 in the morning
	public static long MILL_SEC_MARKET_START_AFTERNOON = 1000 * 60 * 6 * 130; //9:30 in the morning
	public static long MILL_SEC_MARKET_STOP_AFTERNOON = 1000 * 60 * 6 * 150; //9:30 in the morning
	public static long DIFF_CURRENT_GMT = TimeZone.getDefault().getRawOffset(); //difference between current timezone and GMT
		//between Beijing and GMT is 1000 * 60 * 60 * 8; 
	
	public static final Locale chinaLocale =  new Locale("zh", "CN");
	//2013年1月21日上午09:30
	public static final String yMdahm_DateFormat="yyyy" + CH_YEAR+ "M" + CH_MONTH + "dd" + CH_DAY + "aaahh:mm";
	public static final String yMd_DateFormat = "yyyy" + CH_YEAR+ "MM" + CH_MONTH + "dd" + CH_DAY;
	public static final String yMdHm_DateFormat="yyyy" + CH_YEAR+ "MM" + CH_MONTH + "dd" + CH_DAY + "HH:mm";
	public static final String MdHm_DateFormat="MM" + CH_MONTH + "dd" + CH_DAY + "HH:mm";
	public static final String MdHms_DateFormat="MM" + CH_MONTH + "dd" + CH_DAY + "HH:mm:ss";


	public static final SimpleDateFormat yMdahm_DF= new SimpleDateFormat(yMdahm_DateFormat, chinaLocale);
	public static final SimpleDateFormat yMd_DF= new SimpleDateFormat(yMd_DateFormat, chinaLocale);
	public static final SimpleDateFormat yMdHm_DF= new SimpleDateFormat(yMdHm_DateFormat, chinaLocale);
	public static final SimpleDateFormat MdHm_DF= new SimpleDateFormat(MdHm_DateFormat, chinaLocale);
	public static final SimpleDateFormat MdHms_DF= new SimpleDateFormat(MdHms_DateFormat, chinaLocale);

	
	public static Date getDate(String date, SimpleDateFormat sdf){
		try {
			Date d = sdf.parse(date);
			return d;
		}catch(Exception e){
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public static int[] getYearQuarter(Date d){
		Calendar calInst = Calendar.getInstance();
		calInst.setTime(d);
		int year = calInst.get(Calendar.YEAR);
		int month = calInst.get(Calendar.MONTH);
		int quarter = (month)/3+1; //map 1-12 to 1-4
		return new int[]{year, quarter};
	}
	
	public static int[] lastYearQuarter(int year, int quarter){
		int y=year;
		int q = quarter;
		if (quarter==1){
			q = 4;
			y--;
		}else{
			q = quarter-1;
		}
		return new int[]{y,q};
	}
	
	public static String lastDayOfYearQuarter(int year, int quarter){
		String[] days = new String[]{"03-31","06-30", "09-30", "12-31"};
		return String.format("%d-%s", year, days[quarter-1]);
	}
	
	public static Date getDate(String value){
		try {
			return yMd_DF.parse(value);
		} catch (ParseException e) {
			logger.error("parse date error:", e);
		}
		return null;
	}
	/*
	 * TODO adding MdHms_DF support
	 * 2013年1月14日10:00到2013年1月21日09:59:59
	 * 2013年01月12日00:00到01月18日00:00
	 * 2013年1月21日上午09:30
	 */
	public static DateTimeRange getDTRange(String value){
		if (value.indexOf(CH_TO)!=-1){
			String fromStr = value.substring(0,value.indexOf(CH_TO));
			String toStr = value.substring(value.indexOf(CH_TO)+CH_TO.length());
			try {
				Date fromDate = yMdHm_DF.parse(fromStr);		
				Date toDate = null;
				if (!toStr.contains(CH_YEAR)){
					toDate = MdHm_DF.parse(toStr);
					toDate.setYear(fromDate.getYear());
				}else{
					toDate = yMdHm_DF.parse(toStr);
				}
				return new DateTimeRange(fromDate, toDate);
			} catch (ParseException e) {
				logger.error("parse date time range error:" + value, e);
				return null;
			}
		}else{
			Date fromDate;
			try {
				fromDate = yMdHm_DF.parse(value);
				return new DateTimeRange(fromDate, null);
			} catch (ParseException e) {
				try {
					fromDate = yMdahm_DF.parse(value);
					return new DateTimeRange(fromDate, null);
				} catch (ParseException e1) {
					logger.error("failed to parse:" + value);
					return null;
				}
			}
		}
	}
	
	public static int DateDiff(long d1, long d2){
		long diff = d2 - d1;
	    return (int) (Math.abs(diff / MILL_SEC_DAY )+ 1); 
	}
	/**
	 * return number of days between d1 and d2
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int DateDiff(Date d1, Date d2){
	    long diff = d2.getTime() - d1.getTime();
	    return (int) (Math.abs(diff / MILL_SEC_DAY )+ 1); 
	}
	
	/*
	 * return number of delta days after the from date (when delta < 0, means before the from date
	 */
	public static Date getDay(Date from, int delta){
		long newDay = from.getTime() + delta * MILL_SEC_DAY;
		return new Date(newDay);
	}

	public static Date yesterday(Date day){
		return new Date(day.getTime() - MILL_SEC_DAY);
	}
	
	public static Date tomorrow(Date day){
		return new Date(day.getTime() + MILL_SEC_DAY);
	}
	
	public static Date getDayBegin(Date day){
		return getDayBegin(day.getTime());
		
	}
	
	public static Date getDayBegin(long time){
		return new Date( (time + DIFF_CURRENT_GMT)/ DateTimeUtil.MILL_SEC_DAY * DateTimeUtil.MILL_SEC_DAY - DIFF_CURRENT_GMT);
	}
	
	//return the time part of the day
	public static long getDayTime(long time){
		return (time + DIFF_CURRENT_GMT) % DateTimeUtil.MILL_SEC_DAY;
	}
	
	public static long getMS(int numDays) {
		return numDays * MILL_SEC_DAY;
	}
	
	public static Date thisMonday(Date d){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return c.getTime();		
	}
	
	public static Date getFirstDay(int year, int month){
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month-1, 1);
		return c.getTime();
	}
	
	public static Date getLastDay(int year, int month){
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		int lastDate = c.getActualMaximum(Calendar.DATE);
		c.set(Calendar.DATE, lastDate);
		return c.getTime();
	}
	
	//orgFromUTC - serverFromUTC = org.date - server.date
	public static Date convertToServerTZFromTZ(Date d, TimeZone originTimeZone){
		long serverFromUTC = TimeZone.getDefault().getOffset(d.getTime());
		long orgFromUTC = originTimeZone.getOffset(d.getTime());
		Date d1 = new Date(d.getTime() + serverFromUTC - orgFromUTC);
		return d1;
	}
	
	//need to remove hours, minutes, etc
	public static Date convertDateToServerTZFromTZ(Date d, TimeZone originTimeZone){
		Date outd = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(originTimeZone);
		String date = sdf.format(d);
		sdf.setTimeZone(TimeZone.getDefault());
		try {
			outd = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("", e);
		}
		return outd;
	}
	

}
