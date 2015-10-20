package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.JDBCMapper;

public abstract class StockConfig {
	protected static Logger logger =  LogManager.getLogger(StockConfig.class);
	//
	public static final String RAW_ROOT="/reminder/items/raw";
	public static final String MERGE_ROOT="/reminder/items/merge";
	public static final String CHECK_ROOT="/reminder/items/check";

	protected SimpleDateFormat sdf = null;
	
	public abstract String getTestMarketId();
	
	public abstract String trimStockId(String stockid);
	
	public abstract String getStockIdsCmd();
	public abstract String getIPODateCmd(); //null, then ipo date info is within StockIds
	public abstract String[] getAllCmds(String marketId);
	public abstract String[] getSyncCmds();
	public abstract Date getMarketStartDate();
	public abstract String[] getCurrentDayCmds();
	public abstract String[] getUntrimmedStockIdCmds();
	
	public abstract String getTestMarketChangeDate();//date before this using test_stock_set1, date after this using test_stock_set2
	public abstract String[] getTestStockSet1();
	public abstract String[] getTestStockSet2();
	public abstract String getTestShortStartDate();
	public abstract String[] getSlowCmds();
	public abstract Map<LaunchableTask, String[]> getPostProcessMap();
	
	public abstract Map<String, String> getPairedMarket(); //like MarketId_HS_A paired with MarketId_HS_A_ST

	public abstract String getStartDate(String cmdName);
	public abstract TimeZone getTimeZone();
	public abstract Date getLatestOpenMarketDate(Date d);
	public abstract Set<Date> getHolidays();
	public abstract float getDailyLimit();//>0 means has price limit n% up and down, if<=0 no limit
	
	//db
	public abstract String[] getTablesByCmd(String cmd);
	public abstract JDBCMapper getDailyQuoteTableMapper();
	public abstract JDBCMapper getFQDailyQuoteTableMapper();
	
	public StockConfig() {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		//sdf.setTimeZone(this.getTimeZone());
	}
	
	//last update date per stock by cmd
	public String getStockLUDateByCmd(String cmd){
		String[] tables = getTablesByCmd(cmd);
		if (tables == null){
			logger.error(String.format("tables for stock last update are not defined for cmd: %s", cmd));
			return null;
		}
		String sql = null;
		if (tables.length==1){
			sql = String.format("select stockid, max(dt) from %s group by stockid", tables[0]);
		}else{
			StringBuffer sb = new StringBuffer("select stockid, max(ludt) from (");
			for (int i=0; i<tables.length; i++){
				String table = tables[i];
				if (i>0){
					sb.append(" union ");
				}
				sb.append(String.format("select stockid, max(dt) as ludt from %s group by stockid", table));
			}
			sb.append(") as stocklu group by stockid");
			sql = sb.toString();
		}
		logger.info(String.format("get lu date sql defined for %s is %s", cmd, sql));
		return sql;
	}
	
	//last update date per stock by cmd
	public String getMarketLUDateByCmd(String cmd){
		String[] tables = getTablesByCmd(cmd);
		if (tables == null){
			logger.error(String.format("tables for market last update are not defined for cmd: %s", cmd));
			return null;
		}
		String sql = null;
		if (tables.length==1){
			sql = String.format("select max(dt) from %s", tables[0]);
		}else{
			StringBuffer sb = new StringBuffer("select max(ludt) from (");
			for (int i=0; i<tables.length; i++){
				String table = tables[i];
				if (i>0){
					sb.append(" union ");
				}
				sb.append(String.format("select max(dt) as ludt from %s", table));
			}
			sb.append(") as marketlu");
			sql = sb.toString();
		}
		logger.info(String.format("get market lu date sql defined for %s is %s", cmd, sql));
		return sql;
	}

	public SimpleDateFormat getSdf(){
		return sdf;
	}
	
	public String getDatePart(String marketId, Date startDate, Date endDate) {
		return marketId + "_" + sdf.format(endDate);
	}
	
	public String[] getPostProcessCmds(){
		List<String> ls = new ArrayList<String>();
		Map<LaunchableTask, String[]> map = this.getPostProcessMap();
		for (String[] cmds:map.values()){
			ls.addAll(Arrays.asList(cmds));
		}
		String[] ret = new String[ls.size()];
		return ls.toArray(ret);
	}
}
