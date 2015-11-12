package org.cld.trade.persist;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.SqlUtil;

public class TradePersistMgr {
	private static Logger logger =  LogManager.getLogger(TradePersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static List<StockPosition> getPosition(DBConnConf dbconf, Date dt){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("select * from %s where dt>=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			List<StockPosition> lo = (List<StockPosition>) SqlUtil.getObjectsByParam(sql, new Object[]{dt}, 
					con, -1, -1, "", mapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void tryPosition(DBConnConf dbconf, StockPosition sp){
		logger.info(String.format("try position with %s", sp));
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("insert into %s (dt, orderqty, orderprice, symbol, isOpenPos, orderid) values(?,?,?,?,0,?)", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{sp.getDt(), sp.getOrderQty(), sp.getOrderPrice(), sp.getSymbol(), sp.getOrderId()};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void openPosition(DBConnConf dbconf, StockPosition sp){
		logger.info(String.format("open position with %s", sp));
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("update %s set isOpenPos=1,orderid=? where dt=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{sp.getOrderId(),sp.getDt()};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void closePosition(DBConnConf dbconf, StockPosition sp){
		logger.info(String.format("close position with %s", sp));
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("update %s set isOpenPos=2,orderid=? where dt=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{sp.getOrderId(),sp.getDt()};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}

}
