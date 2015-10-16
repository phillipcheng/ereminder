package org.cld.stock.sina;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.LaunchableTask;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.sina.task.TradeDetailPostProcessTask;
import org.cld.util.ListUtil;
import org.cld.util.jdbc.JDBCMapper;

public class SinaStockConfig extends StockConfig {
	protected static Logger logger =  LogManager.getLogger(SinaStockBase.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static Set<Date> CNHolidays = new HashSet<Date>();
	static{
		//sdf.setTimeZone(TimeZone.getTimeZone("CTT"));
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
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	public static final String MarketId_HS_A_ST="shfxjs"; //上证所风险警示板
	public static final String MarketId_HS_Test = "hs_a_test";
	
	public static final String HS_A_START_DATE="1989-01-01";
	public static String HS_A_FIRST_DATE_DETAIL_TRADE= "2004-10-01";
	public static String HS_A_FIRST_DATE_RZRQ= "2012-11-12";
	public static String HS_A_FIRST_DATE_DZJY= "2003-01-08";

	public static Date date_HS_A_START_DATE=null;
	static{
		try{
			date_HS_A_START_DATE = sdf.parse(HS_A_START_DATE);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	//file name of the xml conf and the store id as well
	public static final String SINA_STOCK_IDS ="sina-stock-ids";
	//corp material
	public static final String SINA_STOCK_IPO="sina-stock-ipo";
	public static final String SINA_STOCK_CORP_INFO="sina-stock-corp-info";//
	public static final String SINA_STOCK_CORP_MANAGER="sina-stock-corp-manager";//
	public static final String SINA_STOCK_CORP_RELATED="sina-stock-corp-related";
	public static final String SINA_STOCK_CORP_RELATED_OTHER="sina-stock-corp-related-other";
	//finance report
	public static final String SINA_STOCK_FR_QUARTER_BALANCE_SHEET="sina-stock-fr-quarter-BalanceSheet";//quarterly update
	public static final String SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT="sina-stock-fr-quarter-ProfitStatement";//quarterly update
	public static final String SINA_STOCK_FR_QUARTER_CASHFLOW="sina-stock-fr-quarter-CashFlow";//quarterly update
	public static final String SINA_STOCK_FR_FOOTNOTE="sina-stock-fr-footnote";//quarterly update
	public static final String SINA_STOCK_FR_AchieveNotice="sina-stock-fr-achievenotice";//daily update
	public static final String SINA_STOCK_FR_GUIDELINE_YEAR="sina-stock-fr-guideline-year";//quarterly update
	public static final String SINA_STOCK_FR_ASSETDEVALUE_YEAR="sina-stock-fr-assetdevalue-year";//quarterly update
	//market daily update
	public static final String SINA_STOCK_MARKET_HISTORY="sina-stock-market-history";//历史交易
	public static final String SINA_STOCK_TRADE_DETAIL="sina-stock-market-tradedetail";//成交明细
	public static final String SINA_STOCK_MARKET_RZRQ="sina-stock-market-rzrq";//融资融券
	public static final String SINA_STOCK_MARKET_DZJY="sina-stock-market-dzjy";//大宗交易
	public static final String SINA_STOCK_MARKET_FQ="sina-stock-market-fq"; //复权
	//issue daily update
	public static final String SINA_STOCK_ISSUE_SHAREBONUS="sina-stock-issue-sharebonus";
	//stock holder daily update
	public static final String SINA_STOCK_STOCK_STRUCTURE="sina-stock-stock-structure";
	public static final String SINA_STOCK_STOCK_HOLDER="sina-stock-stock-holder";
	public static final String SINA_STOCK_STOCK_HOLDER_CIRCULATE="sina-stock-stock-holder-circulate";
	public static final String SINA_STOCK_STOCK_HOLDER_FUND="sina-stock-stock-holder-fund";
	
	
	//idx on the corp-info page
	public static final int IPO_DATE_IDX = 7;
	public static final int FOUND_DATE_IDX=13;
	public static final int NAME_CHANGE_HISTORY=41;

	public static final Map<String, String[]> cmdTableMap = new HashMap<String, String[]>();
	static{
		//corp
		cmdTableMap.put(SINA_STOCK_CORP_INFO, new String[]{"sinacorpinfo"});
		cmdTableMap.put(SINA_STOCK_IPO, new String[]{"SinaCorpIPO"});
		cmdTableMap.put(SINA_STOCK_CORP_MANAGER, new String[]{"sinacorpmanager"});
		cmdTableMap.put(SINA_STOCK_CORP_RELATED, new String[]{
				"sinacorprelatedindices", 
				"sinacorprelatedsecurities", 
				"sinacorprelatedxis"});
		cmdTableMap.put(SINA_STOCK_CORP_RELATED_OTHER, new String[]{
				"sinacorprelatedconcepts",
				"sinacorprelatedindustries"});
		//fr
		cmdTableMap.put(SINA_STOCK_FR_QUARTER_BALANCE_SHEET, new String[]{"sinafrbalancesheet"}); //quarterly update
		cmdTableMap.put(SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, new String[]{"sinafrprofitstatement"});//quarterly update
		cmdTableMap.put(SINA_STOCK_FR_QUARTER_CASHFLOW, new String[]{"sinafrcashflow"});//quarterly update
		cmdTableMap.put(SINA_STOCK_FR_FOOTNOTE, new String[]{//quarterly update
				"sinafrfootnoteaccount",
				"sinafrfootnoteinventory",
				"sinafrfootnoterecievableaging",
				"sinafrfootnotetax", 
				"sinafrfootnoteincomeindustry",
				"sinafrfootnoteincomeproduct",
				"sinafrfootnoteincomeregion"});
		cmdTableMap.put(SINA_STOCK_FR_AchieveNotice, new String[]{"sinafrachievenotice"});//daily update
		cmdTableMap.put(SINA_STOCK_FR_GUIDELINE_YEAR, new String[]{"sinafrguideline"});//quarterly update
		cmdTableMap.put(SINA_STOCK_FR_ASSETDEVALUE_YEAR, new String[]{"sinafrassetdevalue"});//quarterly update
		//market-daily update
		cmdTableMap.put(SINA_STOCK_MARKET_HISTORY, new String[]{"sinamarketdaily"});
		cmdTableMap.put(SINA_STOCK_TRADE_DETAIL, new String[]{"sinamarkettradedetail"});
		cmdTableMap.put(SINA_STOCK_MARKET_RZRQ, new String[]{
				"sinamarketrzrqdetail",
				"sinamarketrzrqsummary"});
		cmdTableMap.put(SINA_STOCK_MARKET_DZJY, new String[]{"sinamarketdzjy"});
		cmdTableMap.put(SINA_STOCK_MARKET_FQ, new String[]{"sinamarketfq"});
		//stock holder
		cmdTableMap.put(SINA_STOCK_STOCK_HOLDER_FUND, new String[]{"sinastockfundholder"});
		cmdTableMap.put(SINA_STOCK_STOCK_STRUCTURE, new String[]{"sinastockstructure"});
		cmdTableMap.put(SINA_STOCK_STOCK_HOLDER, new String[]{
				"sinastocktopholder", 
				"sinastocktopholdersummary"});
		cmdTableMap.put(SINA_STOCK_STOCK_HOLDER_CIRCULATE, new String[]{"sinastocktopholdercirculate"});
		//issue
		cmdTableMap.put(SINA_STOCK_ISSUE_SHAREBONUS, new String[]{
				"sinasharebonusalloted", 
				"sinasharebonusdividend"});
	}
	
	public static String[] corpConfs = new String[]{//not related with time
		SINA_STOCK_IPO,
		SINA_STOCK_CORP_INFO, //公司简介
		SINA_STOCK_CORP_MANAGER, //公司高管
		SINA_STOCK_CORP_RELATED, //相关证券 所属指数 所属系
		SINA_STOCK_CORP_RELATED_OTHER //所属行业板块  所属概念板块
	};
	public static String[] tradeConfs = new String[]{
		SINA_STOCK_TRADE_DETAIL, //成交明细
		SINA_STOCK_MARKET_HISTORY, //历史交易
		SINA_STOCK_MARKET_RZRQ, //融资融券
		SINA_STOCK_MARKET_DZJY, //大宗交易		//Marketless
		SINA_STOCK_MARKET_FQ //复权交易 //		//Marketless
	};
	public static String[] issueConfs = new String[]{
		SINA_STOCK_ISSUE_SHAREBONUS, //分红送配
	};
	public static String[] holderConfs = new String[]{
		SINA_STOCK_STOCK_STRUCTURE, //股本结构
		SINA_STOCK_STOCK_HOLDER, //主要股东
		SINA_STOCK_STOCK_HOLDER_CIRCULATE, //流通股东
		SINA_STOCK_STOCK_HOLDER_FUND //基金持股
	};	
	public static String[] frConfs = new String[]{
		SINA_STOCK_FR_AchieveNotice, //业绩预告
		SINA_STOCK_FR_ASSETDEVALUE_YEAR, //资产减值准备
		SINA_STOCK_FR_FOOTNOTE, //财务附注
		SINA_STOCK_FR_GUIDELINE_YEAR, //财务指标
		SINA_STOCK_FR_QUARTER_BALANCE_SHEET, //利润表
		SINA_STOCK_FR_QUARTER_CASHFLOW,//现金流量表
		SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, //资产负债表
	};
	
	public static String[] syncConf = new String[]{SINA_STOCK_IPO}; //other cmd need this result
	public static String[] allConf = (String[]) ListUtil.concatAll(corpConfs, tradeConfs, issueConfs, holderConfs, frConfs);

	@Override
	public String getTestMarketId() {
		return MarketId_HS_Test;
	}

	@Override
	public String getStockIdsCmd() {
		return SINA_STOCK_IDS;
	}

	@Override
	public String getIPODateCmd() {
		return SINA_STOCK_IPO;
	}

	@Override
	public String getTestMarketChangeDate() {
		return SinaTestStockConfig.Test_D3;
	}

	@Override
	public String[] getTestStockSet1() {
		return SinaTestStockConfig.Test_D1_Stocks;
	}

	@Override
	public String[] getTestStockSet2() {
		return SinaTestStockConfig.Test_D3_Stocks;
	}

	static Map<String, String> pairedMarket = new HashMap<String, String>();
	static{
		pairedMarket.put(MarketId_HS_A, MarketId_HS_A_ST);
	}
	
	@Override
	public Map<String, String> getPairedMarket() {
		return pairedMarket;
	}

	@Override
	public String getStartDate(String cmdName) {
		String startDate = null;
		if (cmdName.contains("rzrq")){
			startDate = SinaStockConfig.HS_A_FIRST_DATE_RZRQ;
		}else if (cmdName.contains("dzjy")){
			startDate = SinaStockConfig.HS_A_FIRST_DATE_DZJY;
		}else if (cmdName.contains("tradedetail")){
			startDate = SinaStockConfig.HS_A_FIRST_DATE_DETAIL_TRADE;
		}
		return startDate;
	}

	@Override
	public String[] getAllCmds(String marketId) {
		if (marketId.startsWith(SinaStockConfig.MarketId_HS_Test)){
			return SinaTestStockConfig.testAllConf;
		}else{
			return SinaStockConfig.allConf;
		}
	}

	@Override
	public String getTestShortStartDate() {
		return SinaTestStockConfig.Test_SHORT_SD;
	}

	@Override
	public String[] getSlowCmds() {
		return new String[]{SINA_STOCK_TRADE_DETAIL};
	}

	@Override
	public String[] getSyncCmds() {
		return new String[]{SINA_STOCK_IPO};
	}

	@Override
	public Date getMarketStartDate() {
		return date_HS_A_START_DATE;
	}
	
	@Override
	public String trimStockId(String stockid) {
		return stockid.substring(2); //remove the sz, sh prefix
	}
	@Override
	public String[] getCurrentDayCmds() {
		return new String[]{};
	}

	@Override
	public String[] getTablesByCmd(String cmd) {
		return cmdTableMap.get(cmd);
	}

	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("CTT");
	}
	
	@Override
	public Date getLatestOpenMarketDate(Date d) {
		while (!StockUtil.isOpenDay(d, CNHolidays)){
			d = StockUtil.getLastOpenDay(d, CNHolidays);
		}
		return d;
	}
	@Override
	public String[] getUntrimmedStockIdCmds() {
		return new String[]{SINA_STOCK_TRADE_DETAIL};
	}
	
	
	@Override
	public Set<Date> getHolidays() {
		return CNHolidays;
	}

	@Override
	public Map<LaunchableTask, String[]> getPostProcessMap() {
		Map<LaunchableTask, String[]> map = new HashMap<LaunchableTask, String[]>();
		map.put(TradeDetailPostProcessTask.getLaunchInstance(), new String[]{SINA_STOCK_TRADE_DETAIL});
		return map;
	}

	@Override
	public JDBCMapper getDailyQuoteTableMapper() {
		return SinaDailyQuoteCQJDBCMapper.getInstance();
	}

	@Override
	public JDBCMapper getFQDailyQuoteTableMapper() {
		return SinaFQDailyQuoteCQJDBCMapper.getInstance();
	}
}
