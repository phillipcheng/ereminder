package org.cld.stock.nasdaq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.util.ListUtil;


public class NasdaqStockConfig extends StockConfig{
	private static Logger logger =  LogManager.getLogger(StockConfig.class);
	public static final String MarketId_NASDAQ="NASDAQ";
	public static final String MarketId_NYSE="NYSE";
	public static final String MarketId_AMEX="AMEX";
	public static final String MarketId_ALL="ALL";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//file name of the xml conf and the store id as well
	public static final String STOCK_IDS ="nasdaq-ids";
	//market
	public static final String QUOTE_HISTORY="nasdaq-quote-historical";//start-end
	public static final String QUOTE_PREMARKET="nasdaq-quote-premarket";//current day
	public static final String QUOTE_AFTERHOURS="nasdaq-quote-afterhours";//current day
	public static final String QUOTE_TICK="nasdaq-quote-tick";//current day
	public static final String QUOTE_SHORT_INTEREST="nasdaq-quote-short-interest";
	
	//issue
	public static final String DIVIDEND_HISTORY="nasdaq-issue-dividend-history";
	
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
	
	public static final String STOCK_DATA="data";
	
	//
	public static final String RAW_ROOT="/reminder/items/raw";
	public static final String MERGE_ROOT="/reminder/items/merge";
	public static final String CHECK_ROOT="/reminder/items/check";
	
	public static final Map<String, String[]> cmdTableMap = new HashMap<String, String[]>();
	static{
		//fr
		cmdTableMap.put(BALANCE_SHEET, new String[]{"NasdaqFrQuarterBalanceSheet"});
		cmdTableMap.put(INCOME_STATEMENT, new String[]{"NasdaqFrQuarterIncomeStatement"});
		cmdTableMap.put(CASH_FLOW, new String[]{"NasdaqFrQuarterCashFlow"});
		cmdTableMap.put(REVENUE, new String[]{"NasdaqFrQuarterRevenue"});
		//market(quote)
		cmdTableMap.put(QUOTE_HISTORY, new String[]{"NasdaqQuoteHistory"});
		cmdTableMap.put(QUOTE_PREMARKET, new String[]{"NasdaqPremarket"});
		cmdTableMap.put(QUOTE_AFTERHOURS, new String[]{"NasdaqAfterhours"});
		cmdTableMap.put(QUOTE_TICK, new String[]{"NasdaqTick"});
		cmdTableMap.put(QUOTE_SHORT_INTEREST, new String[]{"NasdaqShortInterest"});
		//stock holder
		cmdTableMap.put(HOLDING_INSTITUTIONAL, new String[]{"NasdaqHoldingInstitutional"});
		cmdTableMap.put(HOLDING_INSIDERS, new String[]{"NasdaqHoldingInsiders"});
		//issue
		cmdTableMap.put(DIVIDEND_HISTORY, new String[]{"NasdaqDividendHistory"});
	}
	
	public static String[] corpConfs = new String[]{
	};
	
	public static String[] quoteConfs = new String[]{
		QUOTE_HISTORY, // daily
		QUOTE_PREMARKET, // 8:00AM ET - 9:30AM ET, will be posted from 4:15 a.m. ET to 7:30 a.m. ET of the following day.
		QUOTE_AFTERHOURS, //4:00PM ET - 8:00PM ET, will be posted 4:15 p.m. ET to 3:30 p.m. ET of the following day
		QUOTE_TICK, // 9:30AM ET - 4:00PM ET
		QUOTE_SHORT_INTEREST,
		//QUOTE_ONEMINUTE // trading + extended hours, 8:00AM-8:00PM, max get the past 5 days
	};
	public static String[] issueConfs = new String[]{
		DIVIDEND_HISTORY, //
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
	};
	
	public static String[] syncConf = new String[]{}; //other cmd need this result
	public static String[] allConf = (String[]) ListUtil.concatAll(corpConfs, quoteConfs, issueConfs, holderConfs, frConfs);
	
	public static final String START_MARKET="1989-01-01";
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
		return new String[]{QUOTE_TICK, QUOTE_PREMARKET, QUOTE_AFTERHOURS, HOLDING_SUMMARY, HOLDING_TOP5};
	}
	@Override
	public String[] getTablesByCmd(String cmd) {
		return cmdTableMap.get(cmd);
	}
	@Override
	public String[] getPostProcessCmds() {
		return new String[]{};
	}
	@Override
	public String getDatePart(String marketId, Date startDate, Date endDate) {
		String strStartDate = null;
		if (startDate == null){
			strStartDate = "null";
		}else{
			strStartDate = sdf.format(startDate);
		}
		return marketId + "_" + strStartDate + "_" + sdf.format(endDate);
	}

	@Override
	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone("EST");
	}
	
	@Override
	public Date getLatestOpenMarketDate(Date d) {
		while (!StockUtil.isOpenDay(d, StockUtil.USHolidays)){
			d = StockUtil.getLastOpenDay(d, StockUtil.USHolidays);
		}
		return d;
	}
	@Override
	public String[] getUntrimmedStockIdCmds() {
		return new String[]{};
	}
	@Override
	public Set<Date> getHolidays() {
		return StockUtil.USHolidays;
	}
}
