package org.cld.stock.strategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.hadooputil.HdfsDownloadUtil;
import org.cld.util.jdbc.GeneralJDBCMapper;
import org.cld.util.jdbc.SqlUtil;
import org.xml.mytaskdef.ScriptEngineUtil;
import org.xml.taskdef.VarType;

public class CompareSelectStrategy extends SelectStrategy{
	
	public CompareSelectStrategy(){
		super();
	}
	
	public CompareSelectStrategy(String name, String outputDir, Object[] scripts) {
		super(name, outputDir, scripts);
	}
	
	public static Logger logger = LogManager.getLogger(CompareSelectStrategy.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	public static String ASC = "asc";
	public static String DESC = "desc";
	
	private String[] toStringArray(Object candidate){
		String[] sqls = null;
		if (candidate instanceof List){
			List<String> l = (List<String>)candidate;
			sqls = new String[l.size()];
			sqls = l.toArray(sqls);
		}else{
			sqls = (String[]) scripts[0];
		}
		return sqls;
	}
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
	public List<String> select(CrawlConf cconf, SelectStrategy ss, Object param){
		String outputFileDir = ss.getOutputDir();
		Object[] scripts = ss.getScripts();
		String[] sqls = toStringArray(scripts[0]);
		String[] resultExps = toStringArray(scripts[1]);
		String filterExp = (String) scripts[2];
		String orderBy = (String) scripts[3];
		String orderDirection = (String) scripts[4];
		boolean isAsc = false;
		if (ASC.equals(orderDirection)){
			isAsc=true;
		}else{
			isAsc=false;
		}
		List<String> retList = new ArrayList<String>();
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());
			List<Map<String, Object>> priceMapArray = new ArrayList<Map<String, Object>>();
			for (int j=0; j<sqls.length; j++){
				String sql = sqls[j];
				if (sql.contains("%s")){
					sql = String.format(sql, param);
				}
				List<? extends Object> lo = SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", GeneralJDBCMapper.getInstance());
				Map<String, Object> aPriceMap = new HashMap<String, Object>();
				for (int i=0; i<lo.size(); i++){
					List<Object> vl = (List<Object>)lo.get(i);
					if (vl.size()>2){
						Object[] vlist = new Object[vl.size()-1];
						for (int k=0; k<vlist.length;k++){
							vlist[k]=vl.get(k+1);
						}
						aPriceMap.put((String)vl.get(0), vlist);
					}else{
						aPriceMap.put((String)vl.get(0), vl.get(1));
					}
				}
				priceMapArray.add(aPriceMap);
			}
			int lineno=0;
			TreeMap<Object, String> orderOutput = new TreeMap<Object, String>();
			Map<String, Object> aPriceMap = priceMapArray.get(0);
			String title = "";
			for (String stockid:aPriceMap.keySet()){
				TreeMap<String, Object> variables = new TreeMap<String, Object>();
				boolean eval=true;
				for (int k=0; k<priceMapArray.size(); k++){
					char varName = (char) ('a' + k);
					Map<String, Object> map = priceMapArray.get(k);
					Object v = map.get(stockid);
					if (v!=null){
						variables.put(varName+"", v);
					}else{
						logger.error(String.format("price for stock:%s on var:%c is null.", stockid, varName));
						eval=false;
						break;
					}
				}
				if (eval){
					for (int k=0; k<resultExps.length; k++){
						String resultExp = resultExps[k];
						Object result = ScriptEngineUtil.eval(resultExp, VarType.OBJECT, variables);
						char varSuffix = (char) ('a' + k);
						String varName = "z" + varSuffix;
						variables.put(varName, result);
					}
					
					boolean ret = (boolean) ScriptEngineUtil.eval(filterExp, VarType.BOOLEAN, variables);
					if (ret){
						if (lineno==0){
							title +="stockid,";
						}
						StringBuffer sb = new StringBuffer(stockid + ",");
						Object orderValue = null;
						for (String key: variables.keySet()){
							Object v = variables.get(key);
							if (key.equals(orderBy)){
								orderValue = variables.get(key);
							}
							if (v instanceof Object[]){
								Object[] vl = (Object[])v;
								for (int i=0; i<vl.length; i++){
									if (lineno==0){
										title+= key+"["+i+"],";
									}
									sb.append(vl[i]);
									sb.append(",");
								}
							}else{
								if (lineno==0){
									title+= key+",";
								}
								sb.append(v.toString());
								sb.append(",");
							}
						}
						if (orderValue==null){
							logger.error("orderby not specified");
						}else{
							orderOutput.put(orderValue, sb.toString());
						}
						lineno++;
					}
				}
			}
			String[] sa = new String[orderOutput.size()+1];
			Set<Object> keySet = null;
			if (isAsc){
				keySet = orderOutput.keySet();
			}else{
				keySet = orderOutput.descendingKeySet();
			}
			sa[0]=title;
			int i=1;
			for (Object key:keySet){
				String line = orderOutput.get(key);
				String stockid = line.substring(0, line.indexOf(","));
				retList.add(stockid);
				sa[i]=line;
				i++;
			}
			String strParam = "";
			java.util.Date d = new java.util.Date();
			if (param instanceof java.util.Date){
				strParam = sdf.format((java.util.Date)param);
			}else{
				strParam = param.toString();
			}
			String fileName = String.format("%s_%s_%s.csv", ss.getName(), strParam, detailSdf.format(d));
			HdfsDownloadUtil.outputToHdfs(sa, outputFileDir+"/"+fileName, cconf.getTaskMgr().getHdfsDefaultName());
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
