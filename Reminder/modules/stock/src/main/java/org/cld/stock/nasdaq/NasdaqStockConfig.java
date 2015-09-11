package org.cld.stock.nasdaq;

import java.util.Date;
import java.util.Map;

import org.cld.stock.StockConfig;
import org.cld.util.ListUtil;

public class NasdaqStockConfig implements StockConfig{
	
	public static final String MarketId_NASDAQ="NASDAQ";
	public static final String MarketId_NYSE="NYSE";
	
	//file name of the xml conf and the store id as well
	public static final String STOCK_IDS ="nasdaq-ids";
	//market
	public static final String QUOTE_HISTORY="nasdaq-quote-historical";//历史交易
	public static final String QUOTE_PREMARKET="nasdaq-quote-premarket";//
	public static final String QUOTE_AFTERHOURS="nasdaq-quote-afterhours";//
	public static final String QUOTE_TICK="nasdaq-quote-tick";//
	public static final String QUOTE_ONEMINUTE="nasdaq-quote-oneminute";//
	
	//corp material
	public static final String CORP_INFO="nasdaq-corp-info";//
	
	//issue
	public static final String DIVIDEND_HISTORY="nasdaq-dividend-history";
	
	//holdings
	public static final String HOLDINGS_OWNERSHIP="nasdaq-holdings_ownership";
	public static final String HOLDINGS_INSTITUTIONAL="nasdaq-holdings_institutional";
	public static final String HOLDINGS_INSIDER="nasdaq-holdings_insider";
	
	//finance report
	public static final String BALANCE_SHEET="nasdaq-fr-balance-sheet";
	public static final String INCOME_STATEMENT="nasdaq-fr-income-statement";
	public static final String CASH_FLOW="nasdaq-fr-cash-flow";
	
	public static final String STOCK_DATA="data";
	
	//
	public static final String RAW_ROOT="/reminder/items/raw";
	public static final String MERGE_ROOT="/reminder/items/merge";
	public static final String CHECK_ROOT="/reminder/items/check";
	
	
	public static String[] corpConfs = new String[]{
		CORP_INFO, //公司简介
	};
	public static String[] quoteConfs = new String[]{
		QUOTE_HISTORY, // daily
		QUOTE_PREMARKET, // 8:00AM ET - 9:30AM ET, will be posted from 4:15 a.m. ET to 7:30 a.m. ET of the following day.
		QUOTE_AFTERHOURS, //4:00PM ET - 8:00PM ET, will be posted 4:15 p.m. ET to 3:30 p.m. ET of the following day
		QUOTE_TICK, // 9:30AM ET - 4:00PM ET
		//QUOTE_ONEMINUTE // trading + extended hours, 8:00AM-8:00PM, max get the past 5 days
	};
	public static String[] issueConfs = new String[]{
		DIVIDEND_HISTORY, //
	};
	public static String[] holderConfs = new String[]{
		HOLDINGS_OWNERSHIP,
		HOLDINGS_INSTITUTIONAL,
		HOLDINGS_INSIDER,
	};	
	public static String[] frConfs = new String[]{
		BALANCE_SHEET, //
		INCOME_STATEMENT, //
		CASH_FLOW, //
	};
	
	public static String[] syncConf = new String[]{}; //other cmd need this result
	//public static String[] allConf = (String[]) ListUtil.concatAll(corpConfs, quoteConfs, issueConfs, holderConfs, frConfs);
	public static String[] allConf = new String[]{};
	@Override
	public String getTestMarketId() {
		return NasdaqTestStockConfig.MarketId_NASDAQ_Test;
	}
	@Override
	public String getStockIdsCmd() {
		return STOCK_IDS;
	}
	@Override
	public String getIPODateCmd() {//get with stock_ids
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}
	@Override
	public Map<String, String> getPairedMarket() {//no pair market
		return null;
	}
	@Override
	public String getStartDate(String cmdName) {
		return null;
	}
	@Override
	public String trimStockId(String stockid) {
		return stockid;//untrimmed
	}
	@Override
	public String[] getCurrentDayCmds() {
		return new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS};
	}
}
