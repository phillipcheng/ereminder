package org.cld.stock.strategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.hadooputil.HdfsDownloadUtil;
import org.cld.util.DownloadUtil;
import org.cld.util.jdbc.GeneralJDBCMapper;
import org.cld.util.jdbc.SqlUtil;
import org.xml.mytaskdef.ScriptEngineUtil;
import org.xml.taskdef.VarType;

public class CompareSelect implements SelectStock{
	public static Logger logger = LogManager.getLogger(CompareSelect.class);
	
	public static String VAR_A_PRICE = "a";
	public static String VAR_B_PRICE = "b";
	
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	/**
	 * 
	 * @param thisDay
	 * @param cconf
	 * @param outputFileDir
	 * @param algName
	 * @param refASQL
	 * @param refBSQL: has param thisDay
	 * @param filterExp:  we can have a>b, a>(0.9*b)
	 */
	@Override
	public List<String> select(String name, CrawlConf cconf, String outputFileDir, Object[] params){
		String refASQL = (String) params[0];
		String refBSQL = (String) params[1];
		String filterExp = (String) params[2];
		List<String> retList = new ArrayList<String>();
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());			
			//get stockid,refPrice Map
			List<? extends Object> lo = SqlUtil.getObjectsByParam(refASQL, new Object[]{}, con, -1, -1, "", GeneralJDBCMapper.getInstance());
			Map<String, Double> aPriceMap = new HashMap<String, Double>();
			for (int i=0; i<lo.size(); i++){
				List<Object> vl = (List<Object>)lo.get(i);
				aPriceMap.put((String)vl.get(0), (Double)vl.get(1));
			}
			
			//get the XD close price for thisDay
			lo = SqlUtil.getObjectsByParam(refBSQL, new Object[]{}, con, -1, -1, "", GeneralJDBCMapper.getInstance());
			Map<String, Double> bPriceMap = new HashMap<String, Double>();
			for (int i=0; i<lo.size(); i++){
				List<Object> vl = (List<Object>)lo.get(i);
				bPriceMap.put((String)vl.get(0), (Double)vl.get(1));
			}
			List<String> sl = new ArrayList<String>();
			sl.add(String.format("//a sql: %s", refASQL));
			sl.add(String.format("//b sql: %s", refBSQL));
			sl.add(String.format("//filter exp: %s", filterExp));
			for (String stockid:aPriceMap.keySet()){
				Double aPrice = aPriceMap.get(stockid);
				Double bPrice = bPriceMap.get(stockid);
				if (aPrice!=null && bPrice!=null){
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put(VAR_A_PRICE, aPrice);
					variables.put(VAR_B_PRICE, bPrice);
					boolean ret = (boolean) ScriptEngineUtil.eval(filterExp, VarType.BOOLEAN, variables);
					if (ret){
						sl.add(String.format("stock:%s, refPrice:%f, thisDayFQPrice:%f", stockid, aPrice.doubleValue(), bPrice.doubleValue()));
						retList.add(stockid);
					}
				}else{
					if (aPrice==null){
						logger.error(String.format("aPrice for stock:%s is null.", stockid));
					}else{
						logger.error(String.format("bPrice for stock %s is null.", stockid));
					}
				}
			}
			String[] sa = new String[sl.size()];
			sl.toArray(sa);
			java.util.Date d = new java.util.Date();
			String fileName = name + "_" + detailSdf.format(d);
			if (outputFileDir.startsWith("hdfs:")){
				HdfsDownloadUtil.outputToHdfs(sa, outputFileDir+"/"+fileName, cconf.getTaskMgr().getHdfsDefaultName());
			}else{
				DownloadUtil.outputToFile(sa, outputFileDir, fileName);
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (con!=null){
				try{
					con.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
		return retList;
	}
}
