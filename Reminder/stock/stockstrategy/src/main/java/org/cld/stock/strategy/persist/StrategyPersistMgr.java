package org.cld.stock.strategy.persist;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.DivSplit;
import org.cld.stock.mapper.ext.NasdaqDividendJDBCMapper;
import org.cld.stock.mapper.ext.NasdaqSplitJDBCMapper;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.SqlUtil;

public class StrategyPersistMgr {
	private static Logger logger =  LogManager.getLogger(StrategyPersistMgr.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static{
		sdf.setTimeZone(TimeZone.getTimeZone("EST"));
	}
	
	public static List<RangeEntry> getRangeBuyPrice(DBConnConf dbconf, Date dt){
		RangeMapper rmapper = RangeMapper.getInstance();
		Connection con = null;
		String sql = String.format("select org.symbol, org.dt, org.buyPrice from %s as org, "
				+ "(select symbol, max(dt) as mdt from %s group by symbol) as sm "
				+ "where org.symbol=sm.symbol and org.dt=sm.mdt", 
				rmapper.getTableName(), rmapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			List<RangeEntry> lo = (List<RangeEntry>) SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", rmapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void addRangeBuyPrice(DBConnConf dbconf, String symbol, Date dt, float buyPrice){
		RangeMapper rmapper = RangeMapper.getInstance();
		Connection con = null;
		String sql = String.format("insert into %s (symbol, dt, buyPrice) values('%s', '%s', %.3f)", 
				rmapper.getTableName(), symbol, sdf.format(dt), buyPrice);
		try{
			con = SqlUtil.getConnection(dbconf);
			SqlUtil.execUpdateSQL(con, sql);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static List<DivSplit> getTodaySplit(DBConnConf dbconf, Date dt){
		NasdaqSplitJDBCMapper mapper = NasdaqSplitJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("select * from %s where exdt='%s'", 
				mapper.getTableName(), sdf.format(dt));
		try{
			con = SqlUtil.getConnection(dbconf);
			List<DivSplit> lo = (List<DivSplit>) SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", mapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static List<DivSplit> getTodayDiv(DBConnConf dbconf, Date dt){
		NasdaqDividendJDBCMapper mapper = NasdaqDividendJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("select * from %s where EffDate='%s'", 
				mapper.getTableName(), sdf.format(dt));
		try{
			con = SqlUtil.getConnection(dbconf);
			List<DivSplit> lo = (List<DivSplit>) SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", mapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
}
