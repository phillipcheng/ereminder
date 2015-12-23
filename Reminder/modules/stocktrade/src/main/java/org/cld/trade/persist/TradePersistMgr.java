package org.cld.trade.persist;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.StockOrder;
import org.cld.trade.StockOrderType;
import org.cld.util.JsonUtil;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.SqlUtil;

public class TradePersistMgr {
	private static Logger logger =  LogManager.getLogger(TradePersistMgr.class);
	
	//symbol varchar(50), orderqty decimal(10,2), orderprice decimal(10,2), buySubmitDt DATETIME, buyOrderId varchar(50), 
	//stopSellOrderId varchar(50), limitSellOrderId varchar(50), bsName varchar(100), soMap varchar(2000)
	
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
		String sql = String.format("insert into %s (symbol, orderqty, orderprice, buySubmitDt, buyOrderId, bsName, soMap) values(?,?,?,?,?,?,?)", 
				mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			String strJsonSOs = JsonUtil.ObjToJson(sp.getSoMap());
			SelectCandidateResult scr = sp.getScr();
			StockOrder buyOrder = sp.getSoMap().get(StockOrderType.buy.name());
			Object[] params = new Object[]{scr.getSymbol(), buyOrder.getQuantity(), scr.getBuyPrice(), scr.getDt(), 
					sp.getBuyOrderId(), sp.getBsName(), strJsonSOs};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void updateStopSellOrderId(DBConnConf dbconf, String buyOrderId, String stopSellOrderId){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("update %s set stopSellOrderId=? where buyOrderId=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{stopSellOrderId, buyOrderId};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void updateLimitSellOrderId(DBConnConf dbconf, String buyOrderId, String limitSellOrderId){
		StockPositionJDBCMapper mapper = StockPositionJDBCMapper.getInstance();
		Connection con = null;
		String sql = String.format("update %s set limitSellOrderId=? where buyOrderId=?", mapper.getTableName());
		try{
			con = SqlUtil.getConnection(dbconf);
			Object[] params = new Object[]{limitSellOrderId, buyOrderId};
			SqlUtil.execUpdateSQLWithParams(con, sql, params);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
}
