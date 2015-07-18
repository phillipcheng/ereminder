package org.cld.stock.sina;

public class StockConfig {
	//file name of the xml conf and the store id as well
	public static final String SINA_STOCK_IDS ="sina-stock-ids";
	//market
	public static final String SINA_STOCK_MARKET_HISTORY="sina-stock-market-history";
	public static final String SINA_STOCK_TRADE_DETAIL="sina-stock-market-tradedetail";
	public static final String SINA_STOCK_MARKET_RZRQ="sina-stock-market-rzrq";
	//corp material
	public static final String SINA_STOCK_CORP_INFO="sina-stock-corp-info";
	public static final String SINA_STOCK_CORP_MANAGER="sina-stock-corp-manager";
	public static final String SINA_STOCK_CORP_RELATED="sina-stock-corp-related";
	public static final String SINA_STOCK_CORP_RELATED_OTHER="sina-stock-corp-related-other";
	public static final String SINA_STOCK_DATA="data";
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
	public static final String SINA_STOCK_FR_QUARTER="sina-stock-fr-quarter";
	public static final String[] subFR = new String[]{"BalanceSheet", "ProfitStatement", "CashFlow"}; 
	
	public static final String SINA_STOCK_FR_FOOTNOTE="sina-stock-fr-footnote";
	public static final String SINA_STOCK_FR_AchieveNotice="sina-stock-fr-achievenotice";
	public static final String SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR="sina-stock-fr-guideline-year";
	public static final String SINA_STOCK_FR_ASSETDEVALUE_YEAR="sina-stock-fr-assetdevalue-year";
	//big event
	
	//idx on the corp-info page
	public static final int IPO_DATE_IDX = 7;
	public static final int FOUND_DATE_IDX=13;
}
