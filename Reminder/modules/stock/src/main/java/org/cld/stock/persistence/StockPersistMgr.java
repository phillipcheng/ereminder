package org.cld.stock.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockConfig;
import org.cld.stock.sina.SinaDailyQuoteCQJDBCMapper;
import org.cld.util.jdbc.SqlUtil;

/**
 * Notes:
 * 1. the date loaded in hbase are treated as is, so if you fetch it from PDT(any) timezone, it is still as is, not converted.
 * @author cheyi
 *
 */
public class StockPersistMgr {
	private static Logger logger =  LogManager.getLogger(StockPersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static Map<String, List<CandleQuote>> getFQDailyQuote(StockConfig sc, CrawlConf cconf, List<String> stockidList, Date sd, Date ed){
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		
		String sql = String.format("select * from %s where stockid in %s and dt>'%s' and dt <='%s' order by stockid, dt", 
				sc.getFQDailyQuoteTableMapper().getTableName(), SqlUtil.generateInParameterValues(stockidList), sdf.format(sd), sdf.format(ed));
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());		
			List<CandleQuote> lo = (List<CandleQuote>) SqlUtil.getObjectsByParam(sql, new Object[]{}, 
					con, -1, -1, "", 
					SinaDailyQuoteCQJDBCMapper.getInstance());
			Map<String, List<CandleQuote>> map = new HashMap<String, List<CandleQuote>>();
			if (lo.size()>1){
				String stid = lo.get(0).getStockid();
				List<CandleQuote> cql = new ArrayList<CandleQuote>();
				for (CandleQuote cq:lo){
					if (!cq.getStockid().equals(stid)){
						map.put(stid, cql);
						cql = new ArrayList<CandleQuote>();
						cql.add(cq);
						stid = cq.getStockid();
					}else{
						cql.add(cq);
					}
				}
				map.put(stid, cql); //the last stid
			}
			return map;
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", sql), e);
			return null;
		}finally{
			if (con!=null){
				try{
					con.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
	}
	
	public static Map<String, List<CandleQuote>> getDailyQuote(StockConfig sc, CrawlConf cconf, List<String> stockidList, Date sd, Date ed){
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		
		String sql = String.format("select * from %s where stockid in %s and dt>'%s' and dt <='%s' order by stockid, dt", 
				sc.getDailyQuoteTableMapper().getTableName(), SqlUtil.generateInParameters(stockidList), sdf.format(sd), sdf.format(ed));
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());		
			List<CandleQuote> lo = (List<CandleQuote>) SqlUtil.getObjectsByParam(sql, new Object[]{stockidList}, 
					con, -1, -1, "", SinaDailyQuoteCQJDBCMapper.getInstance());
			String stid = "";
			Map<String, List<CandleQuote>> map = new HashMap<String, List<CandleQuote>>();
			List<CandleQuote> cql = null;
			for (CandleQuote cq:lo){
				if (!cq.getStockid().equals(stid)){
					if (cql!=null){
						map.put(stid, cql);
					}
					cql = new ArrayList<CandleQuote>();
					cql.add(cq);
					stid = cq.getStockid();
				}else{
					cql.add(cq);
				}
			}
			return map;
		}catch(Exception e){
			logger.error(String.format("exceptin while execute %s", sql), e);
			return null;
		}finally{
			if (con!=null){
				try{
					con.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
	}
	
	public static Map<String, Date> getStockIPOData(StockConfig sc, CrawlConf cconf){
		Map<String, Date> ipoDateMap = new HashMap<String, Date>();
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		Statement stmt = null;
		String query = "";
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());			
			String tableName = sc.getTablesByCmd(sc.getIPODateCmd())[0];
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
			if (stmt!=null){
				try{
					stmt.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
			if (con!=null){
				try{
					con.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
		return ipoDateMap;
	}
	
	public static Map<String, Date> getStockLUDateByCmd(StockConfig sc, String cmd, CrawlConf cconf){
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		Statement stmt = null;
		String query = "";
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());			
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
			if (stmt!=null){
				try{
					stmt.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
			if (con!=null){
				try{
					con.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
		return stockLUMap;
	}
	
	public static Date getMarketLUDateByCmd(StockConfig sc, String cmd, CrawlConf cconf){
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		Statement stmt = null;
		String query = "";
		Date d = null;
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());			
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
			if (stmt!=null){
				try{
					stmt.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
			if (con!=null){
				try{
					con.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
		return d;
	}

}
