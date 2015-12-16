package org.cld.trade.persist;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.JsonUtil;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.SqlUtil;

public class TradePersistMgr {
	private static Logger logger =  LogManager.getLogger(TradePersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//symbol varchar(50), orderqty decimal(10,2), orderprice decimal(10,2), buySubmitDt DATETIME, buyOrderId varchar(50), 
	//stopSellOrderId varchar(50), limitSellOrderId varchar(50), soMap varchar(500)
	
	public static StockPosition getStockPositionByOrderId(DBConnConf dbconf, String soid){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("select * from %s where buyOrderId=? or stopSellOrderId=? or limitSellOrderId=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			List<StockPosition> lo = (List<StockPosition>) SqlUtil.getObjectsByParam(sql, new Object[]{soid, soid, soid}, 
					con, -1, -1, "", mapper);
			if (lo.size()==1){
				return lo.get(0);
			}else if (lo.size()==0){
				logger.error(String.format("no stockposition found for soid:%s", soid));
			}else if (lo.size()>1){
				logger.error(String.format("multiple stockposition found for soid:%s: %s", soid, lo));
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return null;
	}
	
	public static void createStockPosition(DBConnConf dbconf, StockPosition sp){
		logger.info(String.format("try position with %s", sp));
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("insert into %s (symbol, orderqty, orderprice, buySubmitDt, buyOrderId, soMap) values(?,?,?,?,?,?)", 
				mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			String strJsonSOs = JsonUtil.ObjToJson(sp.getSoMap());
			Object[] params = new Object[]{sp.getSymbol(), sp.getOrderQty(), sp.getOrderPrice(), sp.getBuySubmitDt(), 
					sp.getBuyOrderId(), strJsonSOs};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void updatePosition(DBConnConf dbconf, StockPosition sp){
		logger.info(String.format("update position with %s", sp));
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("update %s set stopSellOrderId=?, limitSellOrderId=? where buyOrderId=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{sp.getStopSellOrderId(), sp.getLimitSellOrderId(), sp.getBuyOrderId()};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
}
