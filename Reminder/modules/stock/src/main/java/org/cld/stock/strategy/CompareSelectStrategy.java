package org.cld.stock.strategy;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.util.CombPermUtil;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.DBConnConf;
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
	private String filter = "true";
	private String orderBy;
	private String orderDirection;
	private Boolean[] acceptNull;
	
	public CompareSelectStrategy(){
		super();
	}
	
	public static String KEY_SELECTS_ACCEPTNULL=".acceptnull";//per sql
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
		List<Boolean> acceptNullList = new ArrayList<Boolean>();
		for (char k='a';k<'z';k++){
			String sqlName = KEY_SQL+"."+k;
			if (props.containsKey(sqlName)){
				sqlList.add(props.getString(sqlName));
				acceptNullList.add(props.getBoolean(sqlName+KEY_SELECTS_ACCEPTNULL, false));
			}else{
				break;
			}
		}
		sqls = new String[sqlList.size()];
		sqls = sqlList.toArray(sqls);
		//
		acceptNull = new Boolean[sqlList.size()];
		acceptNull = acceptNullList.toArray(acceptNull);
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
		
		if (props.containsKey(KEY_FILTER)){
			filter = props.getString(KEY_FILTER);
		}
		orderBy = props.getString(KEY_ORDERBY);
		orderDirection = props.getString(KEY_ORDERDIR);
	}
	
	public void evalExp(){
		for (int i=0; i<exps.length; i++){
			String exp = exps[i];
			for (int j=1; j<=params.length;j++){
				if (exp.contains("%"+j)){
					exp = exp.replace("%"+j, params[j-1].toString());
				}
			}
			exps[i] = exp;
		}
		for (int j=1; j<=params.length;j++){
			if (filter.contains("%"+j)){
				filter = filter.replace("%"+j, params[j-1].toString());
			}
		}
	}
	
	public List<SelectStrategy> gen(PropertiesConfiguration props, String simpleStrategyName){
		List<SelectStrategy> lss =new ArrayList<SelectStrategy>();
		List<Object[]> paramList = new ArrayList<Object[]>();
		for (int k=1;k<10;k++){
			String paramName = KEY_PARAM+"."+k;
			if (props.containsKey(paramName)){
				paramList.add(StringUtil.parseSteps(props.getString(paramName)));
			}else{
				break;
			}
		}
		List<List<Object>> paramsList = CombPermUtil.eachOne(paramList);
		if (paramsList.size()>0){
			for (List<Object> pl:paramsList){
				CompareSelectStrategy css = new CompareSelectStrategy();
				css.init(props);
				Object[] params = new Object[pl.size()];
				params =  pl.toArray(params);
				css.setParams(params);
				css.evalExp();
				css.setName(simpleStrategyName);
				lss.add(css);
			}
		}else{
			CompareSelectStrategy css = new CompareSelectStrategy();
			css.setName(simpleStrategyName);
			css.init(props);
			lss.add(css);
		}
		return lss;
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
	public static final String SQL_KEY_THIS_QUARTER="%quarter";//2015-12-31
	public static final String SQL_KEY_LAST_QUARTER="%lastquarter";//2015-09-30
	public static final String SQL_KEY_LAST_TWO_QUARTER="%lasttwoquarter";//2015-06-30
	
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
		boolean isAsc = false;
		if (ASC.equals(orderDirection)){
			isAsc=true;
		}else{
			isAsc=false;
		}
		/*
		int[] yq = DateTimeUtil.getYearQuarter(dt);
		String lastDayThisQuarter = DateTimeUtil.lastDayOfYearQuarter(yq[0], yq[1]);
		yq = DateTimeUtil.lastYearQuarter(yq[0], yq[1]);
		String lastDayLastQuarter = DateTimeUtil.lastDayOfYearQuarter(yq[0], yq[1]);
		yq = DateTimeUtil.lastYearQuarter(yq[0], yq[1]);
		String lastDayLastTwoQuarter = DateTimeUtil.lastDayOfYearQuarter(yq[0], yq[1]);
		*/
		
		List<String> retList = new ArrayList<String>();
		Connection con = null;
		List<Map<String, Object>> priceMapArray = new ArrayList<Map<String, Object>>();
		try{
			con = SqlUtil.getConnection(dbconf);
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
				//sql = sql.replace(SQL_KEY_LAST_QUARTER, lastDayLastQuarter);
				//sql = sql.replace(SQL_KEY_LAST_TWO_QUARTER, lastDayLastTwoQuarter);
				for (int k=1; k<=params.length;k++){
					if (sql.contains("%"+k)){
						sql = sql.replace("%"+k, params[k-1].toString());
					}
				}
				List<? extends Object> lo = SqlUtil.getObjectsByParam(sql, new Object[]{}, con, -1, -1, "", GeneralJDBCMapper.getInstance());
				if (j==0 && lo.size()==0){
					//master sql no result
					return new ArrayList<String>();
				}
				Map<String, Object> aPriceMap = new LinkedHashMap<String, Object>();//preserve insertion order
				for (int i=0; i<lo.size(); i++){
					List<Object> vl = (List<Object>)lo.get(i);
					if (vl.size()>2){
						Object[] vlist = new Object[vl.size()-1];
						for (int k=0; k<vlist.length;k++){
							vlist[k]=vl.get(k+1);
						}
						aPriceMap.put((String)vl.get(0), vlist);
					}else if (vl.size()==2){
						aPriceMap.put((String)vl.get(0), vl.get(1));
					}else if (vl.size()==1){
						aPriceMap.put((String)vl.get(0), vl.get(0));
					}
				}
				priceMapArray.add(aPriceMap);
			}		
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
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
					if (this.acceptNull[k]){
						variables.put(varName+"", null);
					}else{
						logger.info(String.format("price for stock:%s on var:%c is null.", stockid, varName));
						eval=false;
						break;
					}
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
							if (v!=null){
								sb.append(v.toString());
							}else{
								sb.append("null");
							}
							sb.append(",");
						}
					}
					if (orderValue==null){//no order specified
						orderOutput.put(lineno, sb.toString());
					}else{
						orderOutput.put(orderValue, sb.toString());
					}
					lineno++;
				}
			}
		}
		String[] sa = new String[orderOutput.size()+1];
		Set<Object> keySet = null;
		if (orderBy==null){
			keySet = orderOutput.keySet();
		}else{
			if (isAsc){
				keySet = orderOutput.keySet();
			}else{
				keySet = orderOutput.descendingKeySet();
			}
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
		//String fileName = String.format("%s_%s_%s_%s.csv", getName(), sdf.format(dt), allStrParam, detailSdf.format(d));
		//HdfsDownloadUtil.outputToHdfs(sa, outputFileDir+"/"+fileName, this.getFsDefaultName());
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

	public Boolean[] getAcceptNull() {
		return acceptNull;
	}

	public void setAcceptNull(Boolean[] acceptNull) {
		this.acceptNull = acceptNull;
	}
}
