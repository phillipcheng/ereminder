package org.cld.stock.persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.CandleQuote;
import org.cld.stock.HdfsReader;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.TradeHour;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DataMapper;
import org.cld.util.FileDataMapper;
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
	
	public static List<String> getStockIds(DBConnConf dbconf, StockConfig sc){
		return getStockIds(dbconf, sc, 0);
	}
	
	public static List<String> getStockIds(DBConnConf dbconf, StockConfig sc, int limit){
		Connection con = null;
		String tableName = sc.getTablesByCmd(sc.getStockIdsCmd()).keySet().iterator().next();
		String sql = null;
		if (limit>0){
			sql = String.format("select stockid from %s limit %d", tableName, limit);
		}else{
			sql = String.format("select stockid from %s", tableName);
		}
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
	
	//BTD for back testing data, they are ajusted till a certain-day
	public static List<? extends Object> getBTDByStockDate(CrawlConf cconf, FileDataMapper fdMapper, String stockId, Date sd, Date ed, TradeHour th){
		List<Object> lo = new ArrayList<Object>();
		BufferedReader br = null;
		try{
			Configuration conf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
			conf.set("fs.defaultFS", cconf.getTaskMgr().getHdfsDefaultName());
			FileSystem fs = FileSystem.get(conf);
			Path fp = new Path(fdMapper.getFileName(stockId));
			br=new BufferedReader(new InputStreamReader(fs.open(fp)));
			String line;
            line=br.readLine();
            while (line != null){//suppose in time ascending order
				Object o = fdMapper.getObject(line);
				if (o instanceof CandleQuote){
					CandleQuote cq = (CandleQuote)o;
					if (StockUtil.filterByTradeHour(cq, th)){
						cq.setStockid(stockId);
						if (!cq.getStartTime().before(sd)){
							if (cq.getStartTime().after(ed)){
								break;
							}else{
								lo.add(o);
							}
						}
					}
				}
				line=br.readLine();
            }
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (br!=null){
				try{
					br.close();
				}catch(Exception e1){
					logger.error("", e1);
				}
			}
		}
		return lo;
	}
	
	//get for read all
	public static List<? extends Object> getDataByStockDate(CrawlConf cconf, DataMapper dataMapper, String stockId, Date sd, Date ed, TradeHour th){
		if (dataMapper instanceof JDBCMapper){
			JDBCMapper tableMapper = null;
			tableMapper = (JDBCMapper) dataMapper;
			Connection con = null;
			String sql = String.format("select * from %s where stockid='%s' and dt>='%s' and dt<'%s' order by dt asc", 
					tableMapper.getTableName(), stockId, sdf.format(sd), sdf.format(ed));
			try{
				con = SqlUtil.getConnection(cconf.getSmalldbconf());
				return SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", tableMapper);
			}catch(Exception e){
				logger.error("", e);
				return null;
			}finally{
				SqlUtil.closeResources(con, null);
			}
		}else if (dataMapper instanceof FileDataMapper){
			FileDataMapper fdMapper = (FileDataMapper) dataMapper;
			return getBTDByStockDate(cconf, fdMapper, stockId, sd, ed, th);
		}else{
			logger.error(String.format("mapper type not supported. %s", dataMapper));
			return null;
		}
	}
	
	//init for step by step reading
	public static HdfsReader getReader(CrawlConf cconf, FileDataMapper fdMapper, String stockId){
		BufferedReader br = null;
		try{
			Configuration conf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
			FileSystem fs = FileSystem.newInstance(conf);
			Path fp = new Path(fdMapper.getFileName(stockId));
			br=new BufferedReader(new InputStreamReader(fs.open(fp)));
			return new HdfsReader(fs, br, fdMapper);
		}catch(Exception e){
			logger.error("error when get reader.", e);
		}
		return null;
	}
	//get for step by step reading
	public static List<CandleQuote> getBTDDate(BufferedReader reader, FileDataMapper fdMapper, Date sd, Date ed, TradeHour th){
		List<CandleQuote> lo = new ArrayList<CandleQuote>();
		try{
			String line;
            line=reader.readLine();
            while (line != null){//in time ascending order
            	CandleQuote cq = (CandleQuote) fdMapper.getObject(line);
            	if (StockUtil.filterByTradeHour(cq, th)){
					if (sd!=null){
						if (!cq.getStartTime().before(sd)){
							lo.add(cq);
							if (cq.getStartTime().after(ed)){	
								break;
							}
						}
					}else{
						lo.add(cq);
						if (cq.getStartTime().after(ed)){
							break;
						}
					}
            	}
				line=reader.readLine();
            }
    	}catch(Exception e){
			logger.error("error when readline.", e);
		}
		return lo;
	}
	//get for step by step reading
	public static List<? extends Object> getBTDDate(BufferedReader reader, FileDataMapper fdMapper, int numLines, Date endDt, TradeHour th){
		int lines=0;
		List<Object> lo = new ArrayList<Object>();
		try{
			String line;
            line=reader.readLine();
            while (line != null){//in time ascending order
            	CandleQuote cq = (CandleQuote) fdMapper.getObject(line);
            	lines++;
            	if (StockUtil.filterByTradeHour(cq, th)){
            		lo.add(cq);
            	}
				if (lines>numLines || cq.getStartTime().after(endDt)){
					break;
				}
				line=reader.readLine();
            }
    	}catch(Exception e){
			logger.error("error when readline.", e);
		}
		return lo;
	}
	
	public static Map<String, Date> getStockIPOData(StockConfig sc, DBConnConf dbconf){
		Map<String, Date> ipoDateMap = new HashMap<String, Date>();
		if (sc.getIPODateCmd()!=null){
			Connection con = null;
			Statement stmt = null;
			String query = "";
			try{
				con = SqlUtil.getConnection(dbconf);
				if (con==null)
					return ipoDateMap;
				String tableName = sc.getTablesByCmd(sc.getIPODateCmd()).keySet().iterator().next();
				query = String.format("select stockid, dt from %s", tableName);
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
		}
		return ipoDateMap;
	}
	
	public static Map<String, Date> getStockLUDateByCmd(StockConfig sc, String cmd, DBConnConf dbconf){
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		Connection con = null;
		Statement stmt = null;
		String query = "";
		try{
			con = SqlUtil.getConnection(dbconf);
			query = sc.getStockLUDateByCmd(cmd);
			if (query!=null){
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
			}else{
				logger.error(String.format("get stock lu date sql is not defined for cmd: %s", cmd));
			}
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", query), e);
		}finally{
			SqlUtil.closeResources(con, stmt);
		}
		return stockLUMap;
	}
	
	public static Date getMarketLUDateByCmd(StockConfig sc, String cmd, DBConnConf dbconf){
		Connection con = null;
		Statement stmt = null;
		String query = "";
		Date d = null;
		try{
			con = SqlUtil.getConnection(dbconf);		
			query = sc.getMarketLUDateByCmd(cmd);
			if (query!=null){
				stmt = con.createStatement();
				ResultSet res = stmt.executeQuery(query);
				if (res.next()){
					d = res.getDate(1);
				}
				res.close();
			}else{
				logger.error(String.format("get market lu date sql is not defined for cmd: %s", cmd));
			}
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", query), e);
		}finally{
			SqlUtil.closeResources(con, stmt);
		}
		return d;
	}

}
