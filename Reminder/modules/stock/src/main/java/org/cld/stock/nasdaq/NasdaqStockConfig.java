package org.cld.stock.nasdaq;

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
import org.cld.stock.LaunchableTask;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.nasdaq.persistence.NasdaqDailyQuoteCQJDBCMapper;
import org.cld.stock.nasdaq.persistence.NasdaqDividendJDBCMapper;
import org.cld.stock.nasdaq.persistence.NasdaqEarnJDBCMapper;
import org.cld.stock.nasdaq.persistence.NasdaqExDivSplitMapper;
import org.cld.stock.nasdaq.persistence.NasdaqFQDailyQuoteCQJDBCMapper;
import org.cld.stock.nasdaq.persistence.NasdaqSplitJDBCMapper;
import org.cld.stock.nasdaq.task.FQPostProcessTask;
import org.cld.stock.nasdaq.task.QuotePostProcessTask;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.util.ListUtil;
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
		//sdf.setTimeZone(TimeZone.getTimeZone("EST"));
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
	//file name of the xml conf and the store id as well
	public static final String STOCK_IDS ="nasdaq-ids";
	//
	public static final String STOCK_IPO = "nasdaq-ipo";
	//market
	//public static final String QUOTE_HISTORY="nasdaq-quote-historical";//start-end, not needed since FQ contains all the info
	public static final String QUOTE_PREMARKET="nasdaq-quote-premarket";//current day
	public static final String QUOTE_AFTERHOURS="nasdaq-quote-afterhours";//current day
	public static final String QUOTE_TICK="nasdaq-quote-tick";//current day
	public static final String QUOTE_SHORT_INTEREST="nasdaq-quote-short-interest";
	public static final String QUOTE_FQ_HISTORY="nasdaq-quote-fq-historical";//from yahoo finance
	
	//issue
	public static final String ISSUE_SPLIT="nasdaq-issue-split";//upcoming split
	public static final String ISSUE_XDIVSPLIT_HISTORY="nasdaq-issue-xds-history";
	public static final String ISSUE_DIVIDEND="nasdaq-issue-dividend";
	
	//holdings
	public static final String HOLDING_SUMMARY="nasdaq-holding-summary";
	public static final String HOLDING_TOP5="nasdaq-holding-top5";
	public static final String HOLDING_INSTITUTIONAL="nasdaq-holding-institutional";
	public static final String HOLDING_INSIDERS="nasdaq-holding-insiders";
	
	//finance report
	public static final String BALANCE_SHEET="nasdaq-fr-quarter-BalanceSheet";
	public static final String INCOME_STATEMENT="nasdaq-fr-quarter-IncomeStatement";
	public static final String CASH_FLOW="nasdaq-fr-quarter-CashFlow";
	public static final String REVENUE="nasdaq-fr-quarter-revenue";
	public static final String EARN_ANNOUNCE="nasdaq-earn-announce";
	public static final String EARN_ANNOUNCE_TIME="nasdaq-earn-announce-time";
	
	public static final String STOCK_DATA="data";
	
	//
	public static final String RAW_ROOT="/reminder/items/raw";
	public static final String MERGE_ROOT="/reminder/items/merge";
	public static final String CHECK_ROOT="/reminder/items/check";
	
	public static final Map<String, Map<String,String>> cmdTableMap = new HashMap<String, Map<String,String>>();
	static{
		//ids
		Map<String, String> m = new HashMap<String,String>();
		m.put("NasdaqIds","part");
		cmdTableMap.put(STOCK_IDS, m);
		
		//corp
		m = new HashMap<String,String>();
		m.put("NasdaqIPO","part");
		cmdTableMap.put(STOCK_IPO, m);
		
		//fr
		m = new HashMap<String,String>();
		m.put("NasdaqFrQuarterBalanceSheet","part");
		cmdTableMap.put(BALANCE_SHEET, m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqFrQuarterIncomeStatement","part");
		cmdTableMap.put(INCOME_STATEMENT, m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqFrQuarterCashFlow","part");
		cmdTableMap.put(CASH_FLOW, m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqFrQuarterRevenue","part");
		cmdTableMap.put(REVENUE,m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqEarnAnnounce","part");
		cmdTableMap.put(EARN_ANNOUNCE,m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqEarnAnnounceTime","part");
		cmdTableMap.put(EARN_ANNOUNCE_TIME,m);
		
		//market(quote)
		m = new HashMap<String,String>();
		m.put("NasdaqPremarket","part");
		cmdTableMap.put(QUOTE_PREMARKET,m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqAfterhours","part");
		cmdTableMap.put(QUOTE_AFTERHOURS,m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqTick","part");
		cmdTableMap.put(QUOTE_TICK,m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqShortInterest","part");
		cmdTableMap.put(QUOTE_SHORT_INTEREST,m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqFqHistory","part");
		cmdTableMap.put(QUOTE_FQ_HISTORY, m);
		
		//stock holder
		m = new HashMap<String,String>();
		m.put("NasdaqHoldingInstitutional","part");
		cmdTableMap.put(HOLDING_INSTITUTIONAL, m);
		m = new HashMap<String,String>();
		m.put("NasdaqHoldingInsiders","part");
		cmdTableMap.put(HOLDING_INSIDERS, m);
		
		//issue
		m = new HashMap<String,String>();
		m.put("NasdaqExDivSplit","part");
		cmdTableMap.put(ISSUE_XDIVSPLIT_HISTORY, m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqDividend","part");
		cmdTableMap.put(ISSUE_DIVIDEND, m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqSplit","part");
		cmdTableMap.put(ISSUE_SPLIT, m);
	}
	
	public static String[] corpConfs = new String[]{
		STOCK_IPO,
	};
	
	public static String[] quoteConfs = new String[]{
		QUOTE_PREMARKET, // 8:00AM ET - 9:30AM ET, will be posted from 4:15 a.m. ET to 7:30 a.m. ET of the following day.
		QUOTE_AFTERHOURS, //4:00PM ET - 8:00PM ET, will be posted 4:15 p.m. ET to 3:30 p.m. ET of the following day
		QUOTE_TICK, // 9:30AM ET - 4:00PM ET
		QUOTE_SHORT_INTEREST,
		QUOTE_FQ_HISTORY,//daily
	};
	public static String[] issueConfs = new String[]{
		ISSUE_XDIVSPLIT_HISTORY, //
		ISSUE_DIVIDEND,
		ISSUE_SPLIT,
	};
	public static String[] holderConfs = new String[]{
		HOLDING_INSTITUTIONAL,
		HOLDING_INSIDERS,
		HOLDING_SUMMARY,
		HOLDING_TOP5
	};	
	public static String[] frConfs = new String[]{
		BALANCE_SHEET, //
		INCOME_STATEMENT, //
		CASH_FLOW, //
		REVENUE,
		EARN_ANNOUNCE,
		EARN_ANNOUNCE_TIME,
	};
	
	public static String[] syncConf = new String[]{STOCK_IPO}; //other cmd need this result
	public static String[] allConf = (String[]) ListUtil.concatAll(new String[]{STOCK_IDS}, 
			corpConfs, quoteConfs, issueConfs, holderConfs, frConfs);
	
	public static final String START_MARKET="1999-01-01";
	public static Date date_START_MARKET=null;
	static{
		try{
			date_START_MARKET = sdf.parse(START_MARKET);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	@Override
	public String getTestMarketId() {
		return NasdaqTestStockConfig.MarketId_NASDAQ_Test;
	}
	@Override
	public String getStockIdsCmd() {
		return STOCK_IDS;
	}
	@Override
	public String getIPODateCmd() {
		return STOCK_IPO;
	}
	@Override
	public String[] getAllCmds(String marketId) {
		if (marketId.startsWith(NasdaqTestStockConfig.MarketId_NASDAQ_Test)){
			return NasdaqTestStockConfig.testAllConf;
		}else{
			return NasdaqStockConfig.allConf;
		}
	}
	@Override
	public String[] getSyncCmds() {
		return null;
	}
	@Override
	public Date getMarketStartDate() {
		return date_START_MARKET;
	}
	@Override
	public String getTestMarketChangeDate() {
		return NasdaqTestStockConfig.Test_END_D3;
	}
	@Override
	public String[] getTestStockSet1() {
		return NasdaqTestStockConfig.Test_D1_Stocks;
	}
	@Override
	public String[] getTestStockSet2() {
		return NasdaqTestStockConfig.Test_D3_Stocks;
	}
	@Override
	public String getTestShortStartDate() {
		return NasdaqTestStockConfig.Test_SHORT_SD;
	}
	@Override
	public String[] getSlowCmds() {
		return new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS};
	}
	@Override
	public Map<String, String> getPairedMarket() {//no pair market
		return null;
	}

	@Override
	public String getStartDate(String cmdName) {
		String startDate = null;
		if (NasdaqStockConfig.EARN_ANNOUNCE.equals(cmdName) || NasdaqStockConfig.EARN_ANNOUNCE_TIME.equals(cmdName)){
			startDate = NasdaqStockConfig.NASDAQ_FIRST_DATE_EARN_ANNOUNCE;
		}
		return startDate;
	}
	@Override
	public String trimStockId(String stockid) {
		return stockid;//untrimmed
	}
	@Override
	public String[] getCurrentDayCmds() {
		return new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS, HOLDING_SUMMARY, HOLDING_TOP5, ISSUE_SPLIT};
	}
	@Override
	public Map<String, String> getTablesByCmd(String cmd) {
		return cmdTableMap.get(cmd);
	}
	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("EST");
	}
	
	@Override
	public Date getLatestOpenMarketDate(Date d) {
		while (!StockUtil.isOpenDay(d, USHolidays)){
			d = StockUtil.getLastOpenDay(d, USHolidays);
		}
		return d;
	}
	@Override
	public String[] getUntrimmedStockIdCmds() {
		return new String[]{};
	}
	@Override
	public Set<Date> getHolidays() {
		return USHolidays;
	}
	@Override
	public Map<LaunchableTask, String[]> getPostProcessMap() {
		Map<LaunchableTask, String[]> map = new HashMap<LaunchableTask, String[]>();
		map.put(QuotePostProcessTask.getLaunchInstance(), new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS});
		map.put(FQPostProcessTask.getLaunchInstance(), new String[]{QUOTE_FQ_HISTORY});
		return map;
	}
	@Override
	public JDBCMapper getDailyQuoteTableMapper() {
		return NasdaqDailyQuoteCQJDBCMapper.getInstance();
	}
	@Override
	public JDBCMapper getFQDailyQuoteTableMapper() {
		return NasdaqFQDailyQuoteCQJDBCMapper.getInstance();
	}
	@Override
	public float getDailyLimit() {
		return 0;
	}
	@Override
	public String[] getFirstStartTimeUseNullCmds() {
		return new String[]{};
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
	public String postImportSql() {
		return "nasdaqpostimport.sql";
	}
	@Override
	public JDBCMapper getEarnTableMapper() {
		return NasdaqEarnJDBCMapper.getInstance();
	}
	@Override
	public String[] getUpdateAllCmds() {
		return new String[]{QUOTE_FQ_HISTORY};
	}
}
