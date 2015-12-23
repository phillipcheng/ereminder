package org.cld.stock.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.mapper.ext.NasdaqDailyQuoteMapper;
import org.cld.stock.mapper.ext.NasdaqDividendJDBCMapper;
import org.cld.stock.mapper.ext.NasdaqEarnJDBCMapper;
import org.cld.stock.mapper.ext.NasdaqExDivSplitMapper;
import org.cld.stock.mapper.ext.NasdaqFQDailyQuoteMapper;
import org.cld.stock.mapper.ext.NasdaqFileFQDailyMapper;
import org.cld.stock.mapper.ext.NasdaqFileFQMinuteMapper;
import org.cld.stock.mapper.ext.NasdaqSplitJDBCMapper;
import org.cld.util.FileDataMapper;
import org.cld.util.jdbc.JDBCMapper;


public class NasdaqStockConfig extends StockConfig{
	private static Logger logger =  LogManager.getLogger(StockConfig.class);
	public static final String MarketId_NASDAQ="NASDAQ";
	public static final String MarketId_NYSE="NYSE";
	public static final String MarketId_AMEX="AMEX";
	public static final String MarketId_ALL="ALL";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static String NASDAQ_FIRST_DATE_EARN_ANNOUNCE= "2010-01-04";
	//http://markets.on.nytimes.com/research/markets/holidays/holidays.asp?display=market&exchange=SHH
	public static Set<Date> USHolidays = new HashSet<Date>();
	static{
		sdf.setTimeZone(TimeZone.getTimeZone("UTC-5"));
		try{
			//
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
	
	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("GMT-5:00");
	}
	
	@Override
	public Set<Date> getHolidays() {
		return USHolidays;
	}
	@Override
	public JDBCMapper getDailyQuoteTableMapper() {
		return NasdaqDailyQuoteMapper.getInstance();
	}
	@Override
	public JDBCMapper getFQDailyQuoteTableMapper() {
		return NasdaqFQDailyQuoteMapper.getInstance();
	}
	@Override
	public float getDailyLimit() {
		return 0;
	}
	@Override
	public String[] getAllStrategy() {
		String[] my = new String[]{};
		return ArrayUtils.addAll(my, super.getAllStrategy());
	}
	@Override
	public JDBCMapper getDividendTableMapper() {
		return NasdaqDividendJDBCMapper.getInstance();
	}
	@Override
	public JDBCMapper getExDivSplitHistoryTableMapper() {
		return NasdaqExDivSplitMapper.getInstance();
	}
	@Override
	public JDBCMapper getSplitTableMapper() {
		return NasdaqSplitJDBCMapper.getInstance();
	}
	@Override
	public JDBCMapper getEarnTableMapper() {
		return NasdaqEarnJDBCMapper.getInstance();
	}

	@Override
	public FileDataMapper getBTFQDailyQuoteMapper() {
		return NasdaqFileFQDailyMapper.getInstance();
	}

	@Override
	public FileDataMapper getBTFQMinuteQuoteMapper() {
		return NasdaqFileFQMinuteMapper.getInstance();
	}

	@Override
	public String getMarketStart() {
		return "9:30";
	}

	@Override
	public String getMarketEnd() {
		return "16:00";
	}
}
