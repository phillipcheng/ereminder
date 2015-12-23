package org.cld.stock.etl.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockUtil;
import org.cld.stock.etl.CrawlCmdGroupType;
import org.cld.stock.etl.LaunchableTask;
import org.cld.stock.persistence.StockPersistMgr;

public abstract class ETLConfig {
	protected static Logger logger =  LogManager.getLogger(ETLConfig.class);
	
	private String baseMarketId;
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
	
	public abstract Map<String, String> getPairedMarket(); //like MarketId_HS_A paired with MarketId_HS_A_ST

	public abstract String getStartDate(String cmdName);
	public abstract Map<LaunchableTask, String[]> getPostProcessMap();
	//return the tables this cmd generates, table to file-prefix
	public abstract Map<String, String> getTablesByCmd(String cmd);
	public abstract String postImportSql();
	
	public String getCrawlByCmd(String cmd){
		return cmd;
	}

	public ETLConfig(String baseMarketId) {
		this.setBaseMarketId(baseMarketId);
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

	public String getBaseMarketId() {
		return baseMarketId;
	}

	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
	
	public static ETLConfig getETLConfig(String stockBase){
		if (StockUtil.SINA_STOCK_BASE.equals(stockBase)){
			return new SinaETLConfig(stockBase);
		}else if (StockUtil.NASDAQ_STOCK_BASE.equals(stockBase)){
			return new NasdaqETLConfig(stockBase);
		}else if (StockUtil.HK_STOCK_BASE.equals(stockBase)){
			return new HKETLConfig(stockBase);
		}else{
			logger.error(String.format("stockBase %s not supported.", stockBase));
			return null;
		}
	}
}
