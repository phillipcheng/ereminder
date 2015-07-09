package org.cld.stock.sina;

public class StockConfig {
	//file name of the xml conf and the store id as well
	public static final String SINA_STOCK_IDS ="sina-stock-ids";
	public static final String SINA_STOCK_FR_HISTORY="sina-stock-fr-history";
	public static final String SINA_STOCK_FR_QUARTER="sina-stock-fr-quarter";
	public static final String SINA_STOCK_MARKET_HISTORY="sina-stock-market-history";
	public static final String SINA_STOCK_CORP_INFO="sina-stock-corp-info";
	//
	public static final String[] subFR = new String[]{"BalanceSheet", "ProfitStatement", "CashFlow"}; 
	//idx on the corp-info page
	public static final int IPO_DATE_IDX = 7;
	public static final int FOUND_DATE_IDX=13;
}
