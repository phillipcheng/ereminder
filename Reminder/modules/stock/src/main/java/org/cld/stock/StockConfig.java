package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public interface StockConfig {
	//
	public static final String RAW_ROOT="/reminder/items/raw";
	public static final String MERGE_ROOT="/reminder/items/merge";
	public static final String CHECK_ROOT="/reminder/items/check";

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public String getTestMarketId();
	
	public String trimStockId(String stockid);
	
	public String getStockIdsCmd();
	public String getIPODateCmd(); //null, then ipo date info is within StockIds
	public String[] getAllCmds(String marketId);
	public String[] getSyncCmds();
	public Date getMarketStartDate();
	public String[] getCurrentDayCmds();
	
	
	public String getTestMarketChangeDate();//date before this using test_stock_set1, date after this using test_stock_set2
	public String[] getTestStockSet1();
	public String[] getTestStockSet2();
	public String getTestShortStartDate();
	public String[] getSlowCmds();
	public String[] getTablesByCmd(String cmd);
	public String[] getPostProcessCmds();
	
	public Map<String, String> getPairedMarket(); //like MarketId_HS_A paired with MarketId_HS_A_ST

	public String getStartDate(String cmdName);
	
	public String getDatePart(String marketId, Date startDate, Date endDate);
	
	public String getByQuarterSQLByCmd(String cmd, int year, int quarter);
}
