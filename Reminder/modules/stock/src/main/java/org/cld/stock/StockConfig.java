package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SellStrategy;
import org.cld.util.FileDataMapper;
import org.cld.util.jdbc.JDBCMapper;

public abstract class StockConfig {
	protected static Logger logger =  LogManager.getLogger(StockConfig.class);
	//
	public static final String RAW_ROOT="/reminder/items/raw";
	public static final String MERGE_ROOT="/reminder/items/merge";
	public static final String CHECK_ROOT="/reminder/items/check";
	public static final String STRATEGY_ROOT="/reminder/sresult";
	
	protected SimpleDateFormat sdf = null;
	
	public abstract String getTestMarketId();
	
	public abstract String stockIdMarket2Cmd(String stockid, String cmd);//from the market-stock-ids to the format each cmd needs
	public abstract String stockIdCmd2DB(String stockid, String cmd);//from stockid used for cmd to stored in db
	
	public abstract Date getMarketStartDate();
	
	public abstract String getStockIdsCmd();
	public abstract String getIPODateCmd();//this is a sync, hbase stored cmd
	public abstract String[] getAllCmds(CrawlCmdGroupType groupType);
	public abstract String[] getSyncCmds();
	public abstract String[] getCurrentDayCmds();
	public abstract String[] getSlowCmds();
	public abstract String[] getFirstStartTimeUseNullCmds();//if the cmd runs first time for a stock, use null as start time instead of ipodates
	public abstract String[] getUpdateAllCmds();//like backward ex-div price quotes
	
	public abstract String getTestMarketChangeDate();//date before this using test_stock_set1, date after this using test_stock_set2
	public abstract String[] getTestStockSet1();
	public abstract String[] getTestStockSet2();
	public abstract Map<LaunchableTask, String[]> getPostProcessMap();
	
	public abstract Map<String, String> getPairedMarket(); //like MarketId_HS_A paired with MarketId_HS_A_ST

	public abstract String getStartDate(String cmdName);
	public abstract TimeZone getTimeZone();
	public abstract Date getLatestOpenMarketDate(Date d);
	public abstract Set<Date> getHolidays();
	public abstract float getDailyLimit();//>0 means has price limit n% up and down, if<=0 no limit
	
	//return the tables this cmd generates, table to file-prefix
	public abstract Map<String, String> getTablesByCmd(String cmd);
	public abstract JDBCMapper getDailyQuoteTableMapper();
	public abstract JDBCMapper getFQDailyQuoteTableMapper();
	public abstract JDBCMapper getSplitTableMapper();//future
	public abstract JDBCMapper getDividendTableMapper();//future
	public abstract JDBCMapper getExDivSplitHistoryTableMapper();//history
	public abstract JDBCMapper getEarnTableMapper();
	public abstract String postImportSql();
	
	//for back testing, BT
	public abstract FileDataMapper getBTFQDailyQuoteMapper();
	public abstract FileDataMapper getBTFQMinuteQuoteMapper();
	
	public static final SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat dsdf = new SimpleDateFormat("yyyy-MM-dd");
	public abstract String getMarketStart();
	public abstract String getMarketEnd();
	
	public Date getNormalTradeStartTime(Date d){
		Date sd = null;
		try{
			sd = msdf.parse(String.format("%s %s", dsdf.format(d), getMarketStart()));
		}catch(Exception e){
			logger.error("", e);
		}
		return sd;
	}
	public Date getNormalTradeEndTime(Date d){
		Date sd = null;
		try{
			sd = msdf.parse(String.format("%s %s", dsdf.format(d), getMarketEnd()));
		}catch(Exception e){
			logger.error("", e);
		}
		return sd;
	}
	public Date getCloseTime(Date d, int holdDuration, int unit){
		if (unit == SellStrategy.HU_DAY){
			Date sd = d;
			if (holdDuration>1){
				sd = StockUtil.getNextOpenDay(d, this.getHolidays(), holdDuration-1);
			}
			//then set the time to market end
			try{
				Date ed = msdf.parse(String.format("%s %s", dsdf.format(sd), getMarketEnd()));
				return ed;
			}catch(Exception e){
				logger.error("", e);
			}
		}else if (unit==SellStrategy.HU_MIN){
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, holdDuration);
			return cal.getTime();
		}else{
			logger.error("unsupported unit:" + unit);
		}
		return null;
	}
	public String[] getAllStrategy(){
		return new String[]{"random", "rally", "closedrop", "closedropavg", "closeraiseavg"};
	}
	
	public String getCrawlByCmd(String cmd){
		return cmd;
	}

	public StockConfig() {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		//sdf.setTimeZone(this.getTimeZone());
	}
	
	//last update date per stock by cmd
	public String getStockLUDateByCmd(String cmd){
		Map<String, String> map = getTablesByCmd(cmd);
		String sql = null;
		if (map!=null){
			Set<String> tables = map.keySet();
			if (tables == null){
				logger.error(String.format("tables for stock last update are not defined for cmd: %s", cmd));
				return null;
			}
			if (tables.size()==1){
				sql = String.format("select stockid, max(dt) from %s group by stockid", tables.iterator().next());
			}else{
				StringBuffer sb = new StringBuffer("select stockid, max(ludt) from (");
				int i=0;
				Iterator<String> it = tables.iterator();
				while (it.hasNext()){
					String table = it.next();
					if (i>0){
						sb.append(" union ");
					}
					sb.append(String.format("select stockid, max(dt) as ludt from %s group by stockid", table));
					i++;
				}
				sb.append(") as stocklu group by stockid");
				sql = sb.toString();
			}
			logger.info(String.format("get lu date sql defined for %s is %s", cmd, sql));
		}
		return sql;
	}
	
	//last update date per stock by cmd
	public String getMarketLUDateByCmd(String cmd){
		Map<String, String> map = getTablesByCmd(cmd);
		String sql = null;
		if (map!=null){
			Set<String> tables = map.keySet();
			if (tables == null){
				logger.error(String.format("tables for market last update are not defined for cmd: %s", cmd));
				return null;
			}
			if (tables.size()==1){
				sql = String.format("select max(dt) from %s", tables.iterator().next());
			}else{
				StringBuffer sb = new StringBuffer("select max(ludt) from (");
				int i=0;
				Iterator<String> it = tables.iterator();
				while (it.hasNext()){
					String table = it.next();
					if (i>0){
						sb.append(" union ");
					}
					sb.append(String.format("select max(dt) as ludt from %s", table));
					i++;
				}
				sb.append(") as marketlu");
				sql = sb.toString();
			}
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
