package org.cld.stock.data;

import java.util.Date;
import java.util.Map;

public class StockDataStatus {
	
	public static final String KEY_FR_BalanceSheet="BalanceSheet";//Financial Report
	public static final String KEY_FR_CashFlow="CashFlow";//Financial Report
	public static final String KEY_FR_ProfitStatement="ProfitStatement";//Financial Report
	public static final String KEY_CorpInfo = "CorpInfo"; //
	
	
	String stockid;
	String marketid;
	Map<String, Date> contentLastUpdated;

}
