package org.cld.stock.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.JDBCMapper;
import org.cld.util.jdbc.SqlUtil;
import org.cld.util.jdbc.StringJDBCMapper;

/**
 * Notes:
 * 1. the date loaded in hbase are treated as is, so if you fetch it from PDT(any) timezone, it is still as is, not converted.
 * @author cheyi
 *
 */
public class StockPersistMgr {
	private static Logger logger =  LogManager.getLogger(StockPersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static List<String> getStockIds(DBConnConf dbconf, String tableName, String subsector, String country){
		Connection con = null;
		String sql = null;
		sql = String.format("select stockid from %s", tableName);
		String where = " where 1=1 ";
		if (subsector!=null){
			where +=String.format(" and subsector='%s'", subsector);
		}
		if (country!=null){
			where +=String.format(" and country='%s'", country);
		}
		sql +=where;
		try{
			con = SqlUtil.getConnection(dbconf);
			List<String> lo = (List<String>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", StringJDBCMapper.getInstance());
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static void truncateTable(DBConnConf dbconf, String tableName){
		Connection con = null;
		try{
			con = SqlUtil.getConnection(dbconf);
			String sql = String.format("truncate table %s", tableName);
	        logger.info(String.format("start execute update query:%s", sql));
	        SqlUtil.execUpdateSQL(con, sql);
	        logger.info(String.format("finish execute update query:%s", sql));
		}catch (Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	public static void loadData(DBConnConf dbconf, String fileName, String tableName){
		Connection con = null;
		Statement stmt = null;
		try{
			con = SqlUtil.getConnection(dbconf);
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	        String query = String.format(
	        		"LOAD DATA LOCAL INFILE '%s' INTO TABLE %s CHARACTER SET UTF8 COLUMNS TERMINATED BY ',' LINES TERMINATED BY '\n'", 
	        		fileName, tableName);
	        query = query.replace("\\", "\\\\");
	        logger.info(String.format("start execute update query:%s", query));
	        stmt.executeUpdate(query);
	        logger.info(String.format("finish execute update query:%s", query));
		}catch (Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, stmt);
		}
	}
	
	public static List<Object> getDataByDate(DBConnConf dbconf, JDBCMapper tableMapper, Date ed, String dateFieldName){
		Connection con = null;
		String sql = null;
		if (dateFieldName==null){
			sql = String.format("select * from %s where dt='%s'", tableMapper.getTableName(), sdf.format(ed));
		}else{
			sql = String.format("select * from %s where %s='%s'", tableMapper.getTableName(), dateFieldName, sdf.format(ed));
		}
		try{
			con = SqlUtil.getConnection(dbconf);
			List<Object> lo = (List<Object>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", 
					tableMapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static List<Object> getDataByDate(DBConnConf dbconf, JDBCMapper tableMapper, Date ed){
		return getDataByDate(dbconf, tableMapper, ed, null);
	}
	
	//in descending order
	public static List<Object> getDataByStockDateLimit(DBConnConf dbconf, JDBCMapper tableMapper, String stockId, Date ed, int limit){
		Connection con = null;
		String sql = String.format("select * from %s where stockid='%s' and dt<='%s' order by dt desc limit %d", 
				tableMapper.getTableName(), stockId, sdf.format(ed), limit);
		try{
			con = SqlUtil.getConnection(dbconf);
			List<Object> lo = (List<Object>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", 
					tableMapper);
			return lo;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public static Map<String, Date> getStockIPOData(String ipoTableName, DBConnConf dbconf){
		Map<String, Date> ipoDateMap = new HashMap<String, Date>();
		Connection con = null;
		Statement stmt = null;
		String query = "";
		try{
			con = SqlUtil.getConnection(dbconf);
			if (con==null)
				return ipoDateMap;
			query = String.format("select stockid, dt from %s", ipoTableName);
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(query);
			while (res.next()){
				ResultSetMetaData rsmd = res.getMetaData();
				try{
					if (rsmd.getColumnType(2)==Types.DATE){
						ipoDateMap.put(res.getString(1), res.getDate(2));
					}
				}catch(Exception e){
					try{
						logger.error(String.format("error converting for stockid:%s, max dt: %s", res.getString(1), res.getString(2)), e);
					}catch(Exception e1){
						logger.error("", e1);
					}
				}
			}
			res.close();
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", query), e);
		}finally{
			SqlUtil.closeResources(con, stmt);
		}
		
		return ipoDateMap;
	}
	
	//get the get last update date query
	public static String getMarketLUDateQueryByTables(Set<String> tables){
		String sql = null;
		if (tables == null){
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
		return sql;
	}
	
	public static Map<String, Date> getStockLUDateByCmd(String query, DBConnConf dbconf){
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		Connection con = null;
		Statement stmt = null;
		try{
			con = SqlUtil.getConnection(dbconf);
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(query);
			while (res.next()){
				ResultSetMetaData rsmd = res.getMetaData();
				try{
					if (rsmd.getColumnType(2)==Types.TIMESTAMP){
						stockLUMap.put(res.getString(1), res.getTimestamp(2));
					}else if (rsmd.getColumnType(2)==Types.DATE){
						stockLUMap.put(res.getString(1), res.getDate(2));
					}
				}catch(Exception e){
					try{
						logger.error(String.format("error converting for stockid:%s, max dt: %s", res.getString(1), res.getString(2)), e);
					}catch(Exception e1){
						logger.error("", e1);
					}
				}
			}
			res.close();
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", query), e);
		}finally{
			SqlUtil.closeResources(con, stmt);
		}
		return stockLUMap;
	}
	
	public static Date getMarketLUDateByCmd(Set<String> tables, DBConnConf dbconf){
		Connection con = null;
		Statement stmt = null;
		String query = "";
		Date d = null;
		try{
			con = SqlUtil.getConnection(dbconf);		
			query = getMarketLUDateQueryByTables(tables);
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(query);
			if (res.next()){
				d = res.getDate(1);
			}
			res.close();
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", query), e);
		}finally{
			SqlUtil.closeResources(con, stmt);
		}
		return d;
	}
}
