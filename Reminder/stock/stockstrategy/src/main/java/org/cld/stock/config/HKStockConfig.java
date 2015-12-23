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
import org.cld.stock.mapper.ext.HKExDivSplitMapper;
import org.cld.stock.mapper.ext.HKFQDailyQuoteMapper;
import org.cld.util.FileDataMapper;
import org.cld.util.jdbc.JDBCMapper;


public class HKStockConfig extends StockConfig{
	private static Logger logger =  LogManager.getLogger(StockConfig.class);
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//http://markets.on.nytimes.com/research/markets/holidays/holidays.asp?display=market&exchange=SHH
	public static Set<Date> holidays = new HashSet<Date>();
	static{
		//sdf.setTimeZone(TimeZone.getTimeZone("EST"));
		try{
			//
			holidays.add(sdf.parse("2014-01-01"));
			holidays.add(sdf.parse("2014-01-31"));
			holidays.add(sdf.parse("2014-02-03"));
			holidays.add(sdf.parse("2014-04-18"));
			holidays.add(sdf.parse("2014-04-21"));
			holidays.add(sdf.parse("2014-05-01"));
			holidays.add(sdf.parse("2014-05-06"));
			holidays.add(sdf.parse("2014-06-02"));
			holidays.add(sdf.parse("2014-07-01"));
			holidays.add(sdf.parse("2014-09-09"));
			holidays.add(sdf.parse("2014-10-01"));
			holidays.add(sdf.parse("2014-10-02"));
			holidays.add(sdf.parse("2014-12-25"));
			holidays.add(sdf.parse("2014-12-26"));
			//
			holidays.add(sdf.parse("2015-01-01"));
			holidays.add(sdf.parse("2015-02-19"));
			holidays.add(sdf.parse("2015-02-20"));
			holidays.add(sdf.parse("2015-04-03"));
			holidays.add(sdf.parse("2015-04-06"));
			holidays.add(sdf.parse("2015-04-07"));
			holidays.add(sdf.parse("2015-05-01"));
			holidays.add(sdf.parse("2015-05-25"));
			holidays.add(sdf.parse("2015-07-01"));
			holidays.add(sdf.parse("2015-09-03"));
			holidays.add(sdf.parse("2015-09-28"));
			holidays.add(sdf.parse("2015-10-01"));
			holidays.add(sdf.parse("2015-10-21"));
			holidays.add(sdf.parse("2015-12-25"));
			//
			holidays.add(sdf.parse("2016-01-01"));
			holidays.add(sdf.parse("2016-02-08"));
			holidays.add(sdf.parse("2016-02-09"));
			holidays.add(sdf.parse("2016-02-10"));
			holidays.add(sdf.parse("2016-03-25"));
			holidays.add(sdf.parse("2016-03-28"));
			holidays.add(sdf.parse("2016-04-04"));
			holidays.add(sdf.parse("2016-05-02"));
			holidays.add(sdf.parse("2016-06-09"));
			holidays.add(sdf.parse("2016-07-01"));
			holidays.add(sdf.parse("2016-09-16"));
			holidays.add(sdf.parse("2016-10-10"));
			holidays.add(sdf.parse("2016-12-26"));
			holidays.add(sdf.parse("2016-12-27"));
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	
	
	//file name of the xml conf and the store id as well
	public static final String STOCK_IDS ="sina-stock-ids";
	//
	//public static final String STOCK_IPO = "hk-ipo";
	//market
	public static final String QUOTE_FQ_HISTORY="hk-quote-fq-historical";
	
	//issue
	public static final String ISSUE_XDIVSPLIT_HISTORY="hk-issue-xds-history";
	
	//holdings
	
	public String getCrawlByCmd(String cmd){
		if (cmd.equals(QUOTE_FQ_HISTORY)){
			return "yahoo-quote-fq-historical";
		}else if (cmd.equals(ISSUE_XDIVSPLIT_HISTORY)){
			return "yahoo-issue-xds-history";
		}else{
			return cmd;
		}
	}
	
	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("EST");
	}
	
	@Override
	public Set<Date> getHolidays() {
		return holidays;
	}
	@Override
	public JDBCMapper getDailyQuoteTableMapper() {
		return null;
	}
	@Override
	public JDBCMapper getFQDailyQuoteTableMapper() {
		return HKFQDailyQuoteMapper.getInstance();
	}
	@Override
	public JDBCMapper getExDivSplitHistoryTableMapper() {
		return HKExDivSplitMapper.getInstance();
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
		return null;
	}

	@Override
	public JDBCMapper getSplitTableMapper() {
		return null;
	}
	@Override
	public JDBCMapper getEarnTableMapper() {
		return null;
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
