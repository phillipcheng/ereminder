package org.cld.stock.etl.base;

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
import org.cld.stock.etl.CrawlCmdGroupType;
import org.cld.stock.etl.LaunchableTask;
import org.cld.stock.etl.task.nasdaq.FQPostProcessTask;
import org.cld.stock.etl.task.nasdaq.QuotePostProcessTask;
import org.cld.util.FileDataMapper;
import org.cld.util.ListUtil;
import org.cld.util.jdbc.JDBCMapper;


public class NasdaqETLConfig extends ETLConfig{
	private static Logger logger =  LogManager.getLogger(NasdaqETLConfig.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//test
	public static final String MarketId_NASDAQ_Test="NASDAQ_Test";
	public static final String Test_SD = "2014-01-10";
	public static final String Test_SHORT_SD = "2015-05-01"; //this should before all Test_Dx, since this is used as the start date, and Test_Dx is used as end date.
	public static final String Test_END_D1 = "2015-05-10";
	public static final String Test_END_D2 = "2015-05-20";//only increase date
	public static final String Test_END_D3 = "2015-06-10";//also increase stock
	public static final String Test_END_D4 = "2015-07-01";//only increase date
	public static Date date_Test_SD = null;
	public static Date date_Test_END_D1 = null;
	public static Date date_Test_END_D2 = null;
	public static Date date_Test_END_D3 = null;
	public static Date date_Test_END_D4 = null;
	static{
		try{
			date_Test_SD = sdf.parse(Test_SD);
			date_Test_END_D1 = sdf.parse(Test_END_D1);
			date_Test_END_D2 = sdf.parse(Test_END_D2);
			date_Test_END_D3 = sdf.parse(Test_END_D3);
			date_Test_END_D4 = sdf.parse(Test_END_D4);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	//public static final String[] Test_D1_Stocks = new String[]{"baba", "goog"};
	//public static final String[] Test_D3_Stocks = new String[]{"baba", "goog", "bidu"};

	public static final String[] Test_D1_Stocks = new String[]{"AAPL"};//,"GOOG","TUES","XGTI"
	public static final String[] Test_D3_Stocks = Test_D1_Stocks;
	
	public static final String MarketId_NASDAQ="NASDAQ";
	public static final String MarketId_NYSE="NYSE";
	public static final String MarketId_AMEX="AMEX";
	public static final String MarketId_ALL="ALL";
	public static String NASDAQ_FIRST_DATE_EARN_ANNOUNCE= "2010-01-04";
	
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
	public static final String QUOTE_FQ_HISTORY="nasdaq-quote-fq-historical";
	public static final String QUOTE_HISTORY="nasdaq-quote-historical";
	
	//issue
	public static final String ISSUE_UPCOMING_SPLIT="nasdaq-issue-upcoming-split";
	public static final String ISSUE_XDIVSPLIT_HISTORY="nasdaq-issue-xds-history";
	public static final String ISSUE_DIVIDEND_HISTORY="nasdaq-issue-dividend-history";
	public static final String ISSUE_UPCOMING_DIVIDEND="nasdaq-issue-upcoming-dividend";
	
	
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
		
		m = new HashMap<String,String>();
		m.put("NasdaqHistory","part");
		cmdTableMap.put(QUOTE_HISTORY, m);
		
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
		cmdTableMap.put(ISSUE_DIVIDEND_HISTORY, m);
		
		m = new HashMap<String,String>();
		m.put("NasdaqSplit","part");
		cmdTableMap.put(ISSUE_UPCOMING_SPLIT, m);
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
		QUOTE_HISTORY,
	};
	public static String[] issueConfs = new String[]{
		ISSUE_XDIVSPLIT_HISTORY, //
		ISSUE_DIVIDEND_HISTORY,
		ISSUE_UPCOMING_SPLIT,
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
	public static String[] testAllConf = (String[]) ArrayUtils.addAll(new String[]{});
	public static String[] allConf = (String[]) ListUtil.concatAll(corpConfs, quoteConfs, issueConfs, holderConfs, frConfs);
	public static String[] noneQuoteConf = (String[]) ListUtil.concatAll(corpConfs, issueConfs, holderConfs, frConfs);
	
	public static final String START_MARKET="1999-01-01";
	public static Date date_START_MARKET=null;
	static{
		try{
			date_START_MARKET = sdf.parse(START_MARKET);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public NasdaqETLConfig(String baseMarketId) {
		super(baseMarketId);
	}
	
	public String getCrawlByCmd(String cmd){
		if (cmd.equals(QUOTE_FQ_HISTORY)){
			return "yahoo-quote-fq-historical";
		}else if (cmd.equals(ISSUE_XDIVSPLIT_HISTORY)){
			return "yahoo-issue-xds-history";
		}else if (cmd.equals(QUOTE_HISTORY)){ 
			return "google-quote-historical";
		}else{
			return cmd;
		}
	}
	
	@Override
	public String getTestMarketId() {
		return MarketId_NASDAQ_Test;
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
	public String[] getAllCmds(CrawlCmdGroupType groupType) {
		if (groupType == CrawlCmdGroupType.test){
			return testAllConf;
		}else if (groupType == CrawlCmdGroupType.all){
			return allConf;
		}else if (groupType == CrawlCmdGroupType.nonequote){
			return noneQuoteConf;
		}else{
			logger.error(String.format("group type:%s not supported.", groupType));
			return new String[]{};
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
		return Test_END_D3;
	}
	@Override
	public String[] getTestStockSet1() {
		return Test_D1_Stocks;
	}
	@Override
	public String[] getTestStockSet2() {
		return Test_D3_Stocks;
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
		if (EARN_ANNOUNCE.equals(cmdName) || EARN_ANNOUNCE_TIME.equals(cmdName)){
			startDate = NASDAQ_FIRST_DATE_EARN_ANNOUNCE;
		}
		return startDate;
	}
	//from market we get sz000001, only trade_detail need this untrimmed version
	@Override
	public String stockIdMarket2Cmd(String stockid, String cmd) {
		return stockid;
	}
	@Override
	public String stockIdCmd2DB(String stockid, String cmd) {
		return stockid;
	}
	@Override
	public String[] getCurrentDayCmds() {
		return new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS, HOLDING_SUMMARY, HOLDING_TOP5};
	}
	@Override
	public Map<String, String> getTablesByCmd(String cmd) {
		return cmdTableMap.get(cmd);
	}
	
	@Override
	public Map<LaunchableTask, String[]> getPostProcessMap() {
		Map<LaunchableTask, String[]> map = new HashMap<LaunchableTask, String[]>();
		map.put(QuotePostProcessTask.getLaunchInstance(), new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS});
		map.put(FQPostProcessTask.getLaunchInstance(), new String[]{"nasdaq-quote-fq-historical"});//TODO
		return map;
	}
	
	@Override
	public String[] getFirstStartTimeUseNullCmds() {
		return new String[]{};
	}
	
	@Override
	public String postImportSql() {
		return "nasdaqpostimport.sql";
	}
	@Override
	public String[] getUpdateAllCmds() {
		return new String[]{QUOTE_FQ_HISTORY};
	}
}