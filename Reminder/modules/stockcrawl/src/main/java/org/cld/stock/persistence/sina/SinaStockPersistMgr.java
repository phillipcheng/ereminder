package org.cld.stock.persistence.sina;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockConfig;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.GeneralJDBCMapper;
import org.cld.util.jdbc.SqlUtil;

public class SinaStockPersistMgr {
	private static Logger logger =  LogManager.getLogger(SinaStockPersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static Date getLatestFilledEps(StockConfig sc, DBConnConf dbconf){
		Connection con = null;
		String sql = String.format("select max(dt) from SinaMarketFQ where obsEps is not null");
		logger.info("sql:" + sql);
		try{
			con = SqlUtil.getConnection(dbconf);
			return SqlUtil.getSingleDateResultSQL(sql, null, con);
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", sql), e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	//pivot at dt, get the latest profitstatment
	public static List<List<Object>> getObservedEpsByDate(StockConfig sc, DBConnConf dbconf, Date dt){
		Connection con = null;
		String sql = String.format("select one.stockid, one.dt, one.dilutedEPS from SinaFrProfitStatement as one, "
				+ "(select stockid, max(dt) as mdt from SinaFrProfitStatement where pubDt<'%s' group by stockid) latest "
				+ "where (one.stockid=latest.stockid and one.dt=latest.mdt)", sdf.format(dt));
		logger.info("sql:" + sql);
		try{
			con = SqlUtil.getConnection(dbconf);
			List<List<Object>> lo = (List<List<Object>>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", 
					GeneralJDBCMapper.getInstance());
			return lo;
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", sql), e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	//pivot at dt, get the latest fqIdx
	public static Map<String, Double> getFQIdx(StockConfig sc, DBConnConf dbconf, Date dt){
		Connection con = null;
		String sql = String.format("select one.stockid, one.fqIdx from SinaMarketFQ one, "
				+ "(select stockid, max(dt) as mdt from SinaMarketFQ where dt<='%s' group by stockid) latest "
				+ "where one.stockid=latest.stockid and one.dt=latest.mdt", sdf.format(dt));
		try{
			con = SqlUtil.getConnection(dbconf);
			List<List<Object>> lol = (List<List<Object>>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", 
					GeneralJDBCMapper.getInstance());
			Map<String, Double> fqIdxMap = new HashMap<String, Double>();
			for (List<Object> lo:lol){
				fqIdxMap.put((String)lo.get(0), (Double)lo.get(1));
			}
			return fqIdxMap;
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", sql), e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
}
