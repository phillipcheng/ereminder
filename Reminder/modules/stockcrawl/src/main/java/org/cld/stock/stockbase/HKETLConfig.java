package org.cld.stock.stockbase;

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
import org.cld.stock.framework.LaunchableTask;
import org.cld.stock.task.nasdaq.FQPostProcessTask;
import org.cld.util.FileDataMapper;
import org.cld.util.ListUtil;
import org.cld.util.jdbc.JDBCMapper;


public class HKETLConfig extends ETLConfig{
	private static Logger logger =  LogManager.getLogger(HKETLConfig.class);
	
	//test
	public static final String Test_SD = "2014-01-10";
	public static final String Test_END_D1 = "2015-05-10";
	public static final String Test_END_D2 = "2015-05-20";//only increase date
	public static final String Test_END_D3 = "2015-06-10";//also increase stock
	public static final String Test_END_D4 = "2015-07-01";//only increase date
	
	public static final String[] Test_D1_Stocks = new String[]{"00111","00113","00114"};
	public static final String[] Test_D3_Stocks = new String[]{"00111","00113","00114", "00119", "00066"};
	
	//
	public static final String MarketId_HK="qbgg_hk";
	public static final String MarketId_HGT="hsgs_hgt_hk";
	public static final String MarketId_TEST="hk-test";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//file name of the xml conf and the store id as well
	public static final String STOCK_IDS ="sina-stock-ids";
	//
	//public static final String STOCK_IPO = "hk-ipo";
	//market
	public static final String QUOTE_FQ_HISTORY="hk-quote-fq-historical";
	
	//issue
	public static final String ISSUE_XDIVSPLIT_HISTORY="hk-issue-xds-history";
	
	//holdings
	
	//finance report

	public static final Map<String, Map<String,String>> cmdTableMap = new HashMap<String, Map<String,String>>();
	static{
		//ids
		Map<String, String> m = new HashMap<String,String>();
		m.put("HKIds","part");
		cmdTableMap.put(STOCK_IDS, m);
		
		//market(quote)
		m = new HashMap<String,String>();
		m.put("HKFqHistory","part");
		cmdTableMap.put(QUOTE_FQ_HISTORY, m);
		
		//stock holder
		
		//issue
		m = new HashMap<String,String>();
		m.put("HKExDivSplit","part");
		cmdTableMap.put(ISSUE_XDIVSPLIT_HISTORY, m);
	}
	
	public static String[] corpConfs = new String[]{
		//STOCK_IPO,
	};
	
	public static String[] quoteConfs = new String[]{
		QUOTE_FQ_HISTORY,//daily
	};
	public static String[] issueConfs = new String[]{
		ISSUE_XDIVSPLIT_HISTORY, //
	};
	
	public static String[] syncConf = new String[]{}; //other cmd need this result
	public static String[] testAllConf = (String[]) ArrayUtils.addAll(new String[]{});
	public static String[] allConf = (String[]) ListUtil.concatAll(corpConfs, quoteConfs, issueConfs);
	public static String[] noneQuoteConf = (String[]) ListUtil.concatAll(corpConfs, issueConfs);
	
	public static final String START_MARKET="1999-01-01";
	public static Date date_START_MARKET=null;
	static{
		try{
			date_START_MARKET = sdf.parse(START_MARKET);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	public HKETLConfig(String baseMarketId) {
		super(baseMarketId);
	}
	
	@Override
	public String getTestMarketId() {
		return MarketId_TEST;
	}
	@Override
	public String getStockIdsCmd() {
		return STOCK_IDS;
	}
	@Override
	public String getIPODateCmd() {
		return null;
	}
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
		return new String[]{};
	}
	@Override
	public Map<String, String> getPairedMarket() {//no pair market
		return null;
	}
	//from market we get 00001, for ISSUE_XDIVSPLIT_HISTORY and QUOTE_FQ_HISTORY we need 0001.HK
	@Override
	public String stockIdMarket2Cmd(String stockid, String cmd) {
		if (cmd==null){
			//get the in db stockid across the cmds, usually fq-history, dividend, split, etc
			return stockid.substring(1) + ".HK";
		}else{
			if (cmd.equals(QUOTE_FQ_HISTORY)||cmd.equals(ISSUE_XDIVSPLIT_HISTORY)){
				return stockid.substring(1) + ".HK";
			}else{
				return stockid;
			}
		}
	}
	@Override
	public String stockIdCmd2DB(String stockid, String cmd) {
		return stockid;
	}
	@Override
	public String getStartDate(String cmdName) {
		return null;
	}
	@Override
	public String[] getCurrentDayCmds() {
		return new String[]{};
	}
	@Override
	public Map<String, String> getTablesByCmd(String cmd) {
		return cmdTableMap.get(cmd);
	}
	
	@Override
	public Map<LaunchableTask, String[]> getPostProcessMap() {
		Map<LaunchableTask, String[]> map = new HashMap<LaunchableTask, String[]>();
		map.put(FQPostProcessTask.getLaunchInstance(), new String[]{QUOTE_FQ_HISTORY});
		return map;
	}
	@Override
	public String[] getFirstStartTimeUseNullCmds() {
		return new String[]{};
	}
	@Override
	public String postImportSql() {
		return null;
	}
	@Override
	public String[] getUpdateAllCmds() {
		return new String[]{QUOTE_FQ_HISTORY};
	}
}