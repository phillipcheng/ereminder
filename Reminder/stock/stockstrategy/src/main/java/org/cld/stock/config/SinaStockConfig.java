package org.cld.stock.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.mapper.ext.SinaDailyQuoteCQJDBCMapper;
import org.cld.stock.mapper.ext.SinaDividendJDBCMapper;
import org.cld.stock.mapper.ext.SinaEarnJDBCMapper;
import org.cld.stock.mapper.ext.SinaFQDailyQuoteCQJDBCMapper;
import org.cld.util.FileDataMapper;
import org.cld.util.ListUtil;
import org.cld.util.jdbc.JDBCMapper;

public class SinaStockConfig extends StockConfig {
	protected static Logger logger =  LogManager.getLogger(SinaStockConfig.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static Set<Date> CNHolidays = new HashSet<Date>();
	static{
		sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));
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
			CNHolidays.add(sdf.parse("2014-10-06"));
			CNHolidays.add(sdf.parse("2014-10-07"));
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
	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("UTC+8");
	}
	
	@Override
	public Set<Date> getHolidays() {
		return CNHolidays;
	}

	@Override
	public JDBCMapper getDailyQuoteTableMapper() {
		return SinaDailyQuoteCQJDBCMapper.getInstance();
	}

	@Override
	public JDBCMapper getFQDailyQuoteTableMapper() {
		return SinaFQDailyQuoteCQJDBCMapper.getInstance();
	}

	@Override
	public float getDailyLimit() {
		return 10;
	}
	
	@Override
	public String[] getAllStrategy() {
		String[] my = new String[]{};
		return ArrayUtils.addAll(my, super.getAllStrategy());
	}

	@Override
	public JDBCMapper getDividendTableMapper() {
		return SinaDividendJDBCMapper.getInstance();
	}
	@Override
	public JDBCMapper getExDivSplitHistoryTableMapper() {
		return SinaDividendJDBCMapper.getInstance();
	}
	@Override
	public JDBCMapper getSplitTableMapper() {
		return SinaDividendJDBCMapper.getInstance();
	}

	@Override
	public JDBCMapper getEarnTableMapper() {
		return SinaEarnJDBCMapper.getInstance();
	}

	@Override
	public FileDataMapper getBTFQDailyQuoteMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileDataMapper getBTFQMinuteQuoteMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMarketStart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMarketEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
