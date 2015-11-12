package org.cld.trade.persist;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.SqlUtil;

public class TradePersistMgr {
	private static Logger logger =  LogManager.getLogger(TradePersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static List<StockPosition> getOpenPosition(DBConnConf dbconf){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("select * from %s where isOpenPos=1", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			List<StockPosition> lo = (List<StockPosition>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", mapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void openPosition(DBConnConf dbconf, StockPosition sos){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("insert into %s (dt, orderqty, orderprice, symbol, isOpenPos) values(?,?,?,?,1)", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{sos.getDt(), sos.getOrderQty(), sos.getOrderPrice(), sos.getSymbol()};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void closePosition(DBConnConf dbconf, StockPosition sos){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("update %s set isOpenPos=0 where dt=? and symbol=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{sos.getDt(), sos.getSymbol()};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}

}
