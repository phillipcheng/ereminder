package org.cld.stock.strategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datastore.DBConnConf;
import org.cld.hadooputil.HdfsDownloadUtil;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.util.jdbc.GeneralJDBCMapper;
import org.cld.util.jdbc.SqlUtil;
import org.xml.mytaskdef.ScriptEngineUtil;
import org.xml.taskdef.VarType;

public class CompareSelectStrategy extends SelectStrategy{
	
	public static Logger logger = LogManager.getLogger(CompareSelectStrategy.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	public static String ASC = "asc";
	public static String DESC = "desc";

	private String[] sqls;
	private String[] exps;
	private String filter;
	private String orderBy;
	private String orderDirection;
	private String[] params;
	
	public CompareSelectStrategy(){
		super();
	}
	
	public static final String KEY_SQL="scs.sql";
	public static final String KEY_EXP="scs.exp";
	public static final String KEY_PARAM="scs.param";
	public static final String KEY_FILTER="scs.filter";
	public static final String KEY_ORDERBY="scs.orderBy";
	public static final String KEY_ORDERDIR="scs.orderDirection";
	
	@Override
	public void init(PropertiesConfiguration props){
		super.init(props);
		//sqls
		List<String> sqlList = new ArrayList<String>();
		for (char k='a';k<'z';k++){
			String sqlName = KEY_SQL+"."+k;
			if (props.containsKey(sqlName)){
				sqlList.add(props.getString(sqlName));
			}else{
				break;
			}
		}
		sqls = new String[sqlList.size()];
		sqls = sqlList.toArray(sqls);
		//exps
		List<String> expList = new ArrayList<String>();
		for (char k='a';k<'z';k++){
			String expName = KEY_EXP+".z"+k;
			if (props.containsKey(expName)){
				expList.add(props.getString(expName));
			}else{
				break;
			}
		}
		exps = new String[expList.size()];
		exps = expList.toArray(exps);
		//params
		List<String> paramList = new ArrayList<String>();
		for (int k=1;k<10;k++){
			String paramName = KEY_PARAM+"."+k;
			if (props.containsKey(paramName)){
				paramList.add(props.getString(paramName));
			}else{
				break;
			}
		}
		params = new String[paramList.size()];
		params = paramList.toArray(params);
		
		filter = props.getString(KEY_FILTER);
		orderBy = props.getString(KEY_ORDERBY);
		orderDirection = props.getString(KEY_ORDERDIR);
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
	public static final String SQL_KEY_TODAY="%date";
	public static final String SQL_KEY_THIS_TDATE="%tdate";
	public static final String SQL_KEY_LAST_TDATE="%lasttdate";
	@Override
	public List<String> select(DBConnConf dbconf, Date dt, StockConfig sc){
		Date today = dt;
		Date thisTradingDay;
		Date lastTradingDay;
		if (this.getDayType()==byDayType.byTradingDay){
			thisTradingDay = dt;
			lastTradingDay = StockUtil.getLastOpenDay(dt, sc.getHolidays());
		}else{//calendayDay
			thisTradingDay = StockUtil.getNextOpenDay(dt, sc.getHolidays());
			lastTradingDay = StockUtil.getLastOpenDay(dt, sc.getHolidays());
		}
		String outputFileDir = getOutputDir();
		boolean isAsc = false;
		if (ASC.equals(orderDirection)){
			isAsc=true;
		}else{
			isAsc=false;
		}
		List<String> retList = new ArrayList<String>();
		try{
			Class.forName(dbconf.getDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		try{
			con = DriverManager.getConnection(dbconf.getUrl(), dbconf.getUser(), dbconf.getPass());
			List<Map<String, Object>> priceMapArray = new ArrayList<Map<String, Object>>();
			/*
			 * replace %date with today
			 * replace %tdate with thisTradingDay
			 * replace %lasttdate with lastTradingDay
			 */
			for (int j=0; j<sqls.length; j++){
				String sql = sqls[j];
				sql = sql.replace(SQL_KEY_TODAY, sdf.format(today));
				sql = sql.replace(SQL_KEY_THIS_TDATE, sdf.format(thisTradingDay));
				sql = sql.replace(SQL_KEY_LAST_TDATE, sdf.format(lastTradingDay));
				for (int k=1; k<=params.length;k++){
					if (sql.contains("%"+k)){
						sql = sql.replace("%"+k, params[k-1]);
					}
				}
				List<? extends Object> lo = SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", GeneralJDBCMapper.getInstance());
				if (j==0 && lo.size()==0){
					//master sql no result
					return new ArrayList<String>();
				}
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
			TreeMap<Object, String> orderOutput = new TreeMap<Object, String>();//aMap is the lead
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
					for (int k=0; k<exps.length; k++){
						String resultExp = exps[k];
						Object result = ScriptEngineUtil.eval(resultExp, VarType.OBJECT, variables);
						char varSuffix = (char) ('a' + k);
						String varName = "z" + varSuffix;
						variables.put(varName, result);
					}
					
					boolean ret = (boolean) ScriptEngineUtil.eval(filter, VarType.BOOLEAN, variables);
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
				sa[i]=sdf.format(dt) + ',' + line;
				i++;
			}
			String allStrParam = "";
			java.util.Date d = new java.util.Date();
			for (i=0; i<params.length;i++){
				if (i>0){
					allStrParam+="_";
				}
				allStrParam+=params[i];
			}
			String fileName = String.format("%s_%s_%s.csv", getName(), allStrParam, detailSdf.format(d));
			HdfsDownloadUtil.outputToHdfs(sa, outputFileDir+"/"+fileName, this.getFsDefaultName());
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
	
	public String[] getSqls() {
		return sqls;
	}
	public void setSqls(String[] sqls) {
		this.sqls = sqls;
	}
	public String[] getExps() {
		return exps;
	}
	public void setExps(String[] exps) {
		this.exps = exps;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderDirection() {
		return orderDirection;
	}
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
}
