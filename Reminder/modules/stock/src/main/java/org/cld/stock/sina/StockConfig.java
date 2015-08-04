package org.cld.stock.sina;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class StockConfig {
	public static final String AllCmdRun_STATUS="AllCmdRun";
	
	//file name of the xml conf and the store id as well
	public static final String SINA_STOCK_IDS ="sina-stock-ids";
	//market
	public static final String SINA_STOCK_MARKET_HISTORY="sina-stock-market-history";//历史交易
	public static final String SINA_STOCK_TRADE_DETAIL="sina-stock-market-tradedetail";//成交明细
	public static final String SINA_STOCK_MARKET_RZRQ="sina-stock-market-rzrq";//融资融券
	public static final String SINA_STOCK_MARKET_DZJY="sina-stock-market-dzjy";//大宗交易
	public static final String SINA_STOCK_MARKET_FQ="sina-stock-market-fq"; //复权
	//corp material
	public static final String SINA_STOCK_CORP_INFO="sina-stock-corp-info";//
	public static final String SINA_STOCK_CORP_MANAGER="sina-stock-corp-manager";//
	public static final String SINA_STOCK_CORP_RELATED="sina-stock-corp-related";
	public static final String SINA_STOCK_CORP_RELATED_OTHER="sina-stock-corp-related-other";
	//issue
	public static final String SINA_STOCK_ISSUE_SHAREBONUS="sina-stock-issue-sharebonus";
	//stock holder
	public static final String SINA_STOCK_STOCK_STRUCTURE="sina-stock-stock-structure";
	public static final String SINA_STOCK_STOCK_HOLDER="sina-stock-stock-holder";
	public static final String SINA_STOCK_STOCK_HOLDER_CIRCULATE="sina-stock-stock-holder-circulate";
	public static final String SINA_STOCK_STOCK_HOLDER_FUND="sina-stock-stock-holder-fund";
	//finance report
	public static final String SINA_STOCK_FR_HISTORY="sina-stock-fr-history";
	public static final String SINA_STOCK_FR_HISTORY_OUT="sina-stock-fr-history-out";
	public static final String SINA_STOCK_FR_HISTORY_QUARTER_OUT="sina-stock-fr-history-quarter-out";
	public static final String[] subFR = new String[]{"BalanceSheet", "ProfitStatement", "CashFlow"};
	public static final String SINA_STOCK_FR_QUARTER_BALANCE_SHEET="sina-stock-fr-quarter-BalanceSheet";
	public static final String SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT="sina-stock-fr-quarter-ProfitStatement";
	public static final String SINA_STOCK_FR_QUARTER_CASHFLOW="sina-stock-fr-quarter-CashFlow";
	public static final String SINA_STOCK_FR_FOOTNOTE="sina-stock-fr-footnote";
	public static final String SINA_STOCK_FR_AchieveNotice="sina-stock-fr-achievenotice";
	public static final String SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR="sina-stock-fr-guideline-year";
	public static final String SINA_STOCK_FR_ASSETDEVALUE_YEAR="sina-stock-fr-assetdevalue-year";
	//big event
	
	//idx on the corp-info page
	public static final int IPO_DATE_IDX = 7;
	public static final int FOUND_DATE_IDX=13;
	public static final String SINA_STOCK_DATA="data";
	
	
	public static String[] corpConfs = new String[]{//not related with time
		SINA_STOCK_CORP_INFO, //公司简介
		SINA_STOCK_CORP_MANAGER, //公司高管
		SINA_STOCK_CORP_RELATED, //相关证券 所属概念
		SINA_STOCK_CORP_RELATED_OTHER //所属系别 所属指数
	};
	public static String[] tradeConfs = new String[]{
		SINA_STOCK_MARKET_HISTORY, //历史交易
		SINA_STOCK_TRADE_DETAIL, //成交明细
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
		SINA_STOCK_FR_QUARTER_BALANCE_SHEET, //利润表
		SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, //资产负债表
		SINA_STOCK_FR_QUARTER_CASHFLOW,//现金流量表
		SINA_STOCK_FR_FOOTNOTE, //财务附注
		SINA_STOCK_FR_AchieveNotice, //业绩预告
		SINA_STOCK_FR_ASSETDEVALUE_YEAR, //资产减值准备
		SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, //财务指标
	};
	
	/*
	public static String[] syncConf = new String[]{SINA_STOCK_CORP_INFO}; //other cmd need this result
	public static String[] StaticConf = (String[]) ArrayUtils.addAll(corpConfs); //static
	public static String[] allConf = (String[]) concatAll(corpConfs, tradeConfs, issueConfs, holderConfs, frConfs);
	public static String[] DynamicConf = (String[]) ArrayUtils.removeElements(allConf, TimeLessConf);
	*/
	//for testing
	public static String[] syncConf = new String[]{SINA_STOCK_CORP_INFO}; //other cmd need this result
	public static String[] StaticConf = (String[]) ArrayUtils.addAll(corpConfs); //static
	public static String[] allConf = (String[]) ArrayUtils.addAll(corpConfs, SINA_STOCK_MARKET_DZJY, SINA_STOCK_MARKET_FQ, SINA_STOCK_MARKET_HISTORY);
	public static String[] DynamicConf = (String[])ArrayUtils.removeElements(allConf, StaticConf);
	
	public static <T> T[] concatAll(T[] first, T[]... rest) {
	  int totalLength = first.length;
	  for (T[] array : rest) {
	    totalLength += array.length;
	  }
	  T[] result = Arrays.copyOf(first, totalLength);
	  int offset = first.length;
	  for (T[] array : rest) {
	    System.arraycopy(array, 0, result, offset, array.length);
	    offset += array.length;
	  }
	  return result;
	}
}
