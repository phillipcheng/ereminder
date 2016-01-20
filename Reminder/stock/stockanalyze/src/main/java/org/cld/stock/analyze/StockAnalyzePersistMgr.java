package org.cld.stock.analyze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cld.util.DataMapper;
import org.cld.util.FileDataMapper;
import org.cld.util.FsType;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.JDBCMapper;
import org.cld.util.jdbc.SqlUtil;

import org.cld.taskmgr.hadoop.HadoopTaskLauncher;

import org.cld.stock.common.CandleQuote;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockDataConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeHour;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectStrategy;


/**
 * Notes:
 * 1. the date loaded in hbase are treated as is, so if you fetch it from PDT(any) timezone, it is still as is, not converted.
 * @author cheyi
 *
 */
public class StockAnalyzePersistMgr {
	private static Logger logger =  LogManager.getLogger(StockAnalyzePersistMgr.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//get for read all
	public static List<? extends Object> getDataByStockDate(AnalyzeConf aconf, DataMapper dataMapper, String stockId, Date sd, Date ed, TradeHour th){
		if (dataMapper instanceof JDBCMapper){
			JDBCMapper tableMapper = null;
			tableMapper = (JDBCMapper) dataMapper;
			Connection con = null;
			String sql = String.format("select * from %s where stockid='%s' and dt>='%s' and dt<'%s' order by dt asc", 
					tableMapper.getTableName(), stockId, sdf.format(sd), sdf.format(ed));
			try{
				con = SqlUtil.getConnection(aconf.getDbconf());
				return SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", tableMapper);
			}catch(Exception e){
				logger.error("", e);
				return null;
			}finally{
				SqlUtil.closeResources(con, null);
			}
		}else if (dataMapper instanceof FileDataMapper){
			FileDataMapper fdMapper = (FileDataMapper) dataMapper;
			return getBTDByStockDate(aconf, fdMapper, stockId, sd, ed, th);
		}else{
			logger.error(String.format("mapper type not supported. %s", dataMapper));
			return null;
		}
	}
	
	//init for step by step reading
	public static CqCachedReader getReader(AnalyzeConf aconf, FileDataMapper fdMapper, String stockId){
		BufferedReader br = null;
		try{
			fdMapper.setRootFolder(aconf.getBtDataFolder());
			if (aconf.getBtFs()==FsType.hdfs){
				Configuration conf = HadoopTaskLauncher.getHadoopConf(aconf);
				FileSystem fs = FileSystem.get(conf);
				Path fp = new Path(fdMapper.getFileName(stockId, FsType.hdfs));
				br=new BufferedReader(new InputStreamReader(fs.open(fp)));
			}else{
				File f = new File(fdMapper.getFileName(stockId, FsType.local));
				br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			}
			return new CqCachedReader(br, fdMapper);
		}catch(Exception e){
			logger.error("error when get reader.", e);
		}
		return null;
	}
	
	//BTD for back testing data, they are ajusted till a certain-day
	private static List<? extends Object> getBTDByStockDate(AnalyzeConf aconf, FileDataMapper fdMapper, String stockId, Date sd, Date ed, TradeHour th){
		List<Object> lo = new ArrayList<Object>();
		CqCachedReader ccreader = getReader(aconf, fdMapper, stockId);
		BufferedReader br = ccreader.getBr();
		try{
			String line;
            line=br.readLine();
            while (line != null){//suppose in time ascending order
				Object o = fdMapper.getObject(line);
				if (o instanceof CandleQuote){
					CandleQuote cq = (CandleQuote)o;
					if (StockUtil.filterByTradeHour(cq, th)){
						cq.setSymbol(stockId);
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
	
	//used by chart
	public static List<CqIndicators> getData(AnalyzeConf aconf, StockDataConfig sdcfg, SelectStrategy bs, TradeHour th){
		FileDataMapper fdMapper = null;
		StockConfig sc = StockUtil.getStockConfig(sdcfg.getBaseMarketId());
		if (sdcfg.getUnit()==IntervalUnit.day){
			fdMapper = sc.getBTFQDailyQuoteMapper();
		}else if (sdcfg.getUnit()==IntervalUnit.minute){
			fdMapper = sc.getBTFQMinuteQuoteMapper();
		}else{
			logger.error(String.format("unit not supported: %s", sdcfg.getUnit()));
		}
		List<CandleQuote> cql = (List<CandleQuote>) StockAnalyzePersistMgr.getBTDByStockDate(aconf, fdMapper, sdcfg.getStockId(), 
				sdcfg.getStartDt(), sdcfg.getEndDt(), th);
		List<CqIndicators> cqilist = new ArrayList<CqIndicators>();
		CandleQuote preCq = null;
		for (CandleQuote cq: cql){
			if (bs.getLookupUnit()!=IntervalUnit.unspecified && bs.getLookupUnit()!=IntervalUnit.day){
				if (StockUtil.crossMarketStart(preCq, cq)){
					logger.debug(String.format("cleanup needed. preCq:%s, cq:%s", preCq, cq));
					bs.cleanup();
				}
			}
			CqIndicators cqi = CqIndicators.addIndicators(cq, bs);
			cqilist.add(cqi);
			preCq = cq;
		}
		return cqilist;
	}

}
