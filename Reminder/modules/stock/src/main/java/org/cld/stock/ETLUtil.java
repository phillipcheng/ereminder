package org.cld.stock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.CrawlTaskConf;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedBrowsePrd;

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	
	//for task name
	public static final String BatchId_Key="BatchId";
	public static final String PK_STOCKID="stockid";
	public static final String PK_DATE="date";
	public static final String PK_LOOKAHEADE="lookahead";//how many time unit (quarter) to look ahead, can be 0, 1, 2, etc
	public static final String PK_MAPPER="mapper";
	public static final String PK_REDUCER="reducer";
	private static final SimpleDateFormat utcsdf = new SimpleDateFormat("yyyy-MM-dd");
	static{
		utcsdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static final int maxBatchSize=1000000;
	//parameter keys define in the browseTask
	
	public static final String DEFAULT_MAPPER="org.cld.datacrawl.hadoop.CrawlTaskMapper";

	public static String getStartUrl(Task t){
		ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
		return btt.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl().getValue();
	}
	
	public static boolean isStatic(Task t){
		ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
		if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_STATIC) && 
				"true".equals(btt.getParamMap().get(AbstractCrawlItemToCSV.FIELD_NAME_STATIC).getValue())){
			return true;
		}
		return false;
	}

	public static boolean isStatic(CrawlConf cconf, String cmdName){
		List<Task> tl = cconf.setUpSite(cmdName+".xml", null);
		if (tl.size()==1 && (tl.get(0) instanceof CrawlTaskConf)){
			CrawlTaskConf ct = (CrawlTaskConf)tl.get(0);
			return isStatic(ct);
		}else{
			logger.error(String.format("isStatic cmd %s does not contain 1 crawlTask.", cmdName));	
		}
		return false;
	}
	
	public static int getLookahead(Task t){
		ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
		if (btt.getParamMap().containsKey(PK_LOOKAHEADE)){
			return Integer.parseInt(btt.getParamMap().get(PK_LOOKAHEADE).getValue());
		}else{
			return 1; //default lookahead 1 quarter
		}
	}
	
	public static boolean needOverwrite(CrawlConf cconf, String cmdName){//for dynamic and no update-date field data
		List<Task> tl = cconf.setUpSite(cmdName+".xml", null);
		if (tl.size()==1 && (tl.get(0) instanceof CrawlTaskConf)){
			CrawlTaskConf ct = (CrawlTaskConf)tl.get(0);
			return isStatic(ct);
		}else{
			logger.error(String.format("needOverwrite cmd %s does not contain 1 crawlTask.", cmdName));	
		}
		return false;
	}
	
	public static String getTaskName(StockConfig sc, String cmd, Map<String, Object> params){
		StringBuffer sb = new StringBuffer();
		sb.append(cmd);
		sb.append("_");
		if (params!=null){
			for (String key: params.keySet()){
				sb.append(key);
				sb.append("_");
				Object val = params.get(key);
				if (val!=null){
					String sval = null;
					if (val instanceof Date){
						sval = sc.getSdf().format(val);
					}else{
						sval = val.toString();
					}
					sb.append(sval);
				}else{
					sb.append("null");
				}
				sb.append("_");
			}
		}
		return sb.toString();
	}
	
	public static String[] getStockIdByMarketId(StockConfig sc, String marketId, CrawlConf cconf, String cmd){
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getStockIdsCmd(), null);
		List<String> ids = (List<String>) ci.getParam(StockBase.KEY_IDS);
		String[] idarray = new String[ids.size()];
		if (Arrays.asList(sc.getUntrimmedStockIdCmds()).contains(cmd)){
			return ids.toArray(idarray);
		}else{
			for (int i=0; i<ids.size(); i++){
				idarray[i] = sc.trimStockId(ids.get(i));
			}
			return idarray;
		}
	}
	
	public static Map<String, Date> getStockLUDateByCmd(StockConfig sc, String cmd, CrawlConf cconf){
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		String confFileName = cmd + ".xml";
		List<Task> tl = cconf.setUpSite(confFileName, null);
		Task t = tl.get(0);
		if (isStatic(t)){
			return stockLUMap;//skip for static tasks
		}else if (Arrays.asList(sc.getCurrentDayCmds()).contains(cmd)){
			return stockLUMap; //skip for current day cmd
		}
		
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
			query = sc.getLUDateByCmd(cmd);
			if (query!=null){
				stmt = con.createStatement();
				ResultSet res = stmt.executeQuery(query);
				while (res.next()){
					stockLUMap.put(res.getString(1), res.getDate(2));
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
	
	private static Map<String, String> ipoCache = null;
	private static SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
	public static Date getIPODateByStockId(StockConfig sc, String marketId, String stockId, CrawlConf cconf, String cmd){
		//get the IPODate
		if (ipoCache==null){
			CrawledItem ipodateCI =null;
			if (sc.getIPODateCmd()!=null){
				ipodateCI = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getIPODateCmd(), null);
			}else{
				ipodateCI = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getStockIdsCmd(), null);
			}
			ipoCache = (Map<String, String>)ipodateCI.getParam(StockBase.KEY_IPODate_MAP);
		}
		//we need trimmed stockid for ipoCache
		if (Arrays.asList(sc.getUntrimmedStockIdCmds()).contains(cmd)){
			stockId = sc.trimStockId(stockId);
		}
		String strDate = ipoCache.get(stockId);
		Date d = sc.getMarketStartDate();
		if (strDate!=null){
			try {
				d = sc.getSdf().parse(strDate);
			} catch (ParseException e) {
				try {
					d = yearSdf.parse(strDate);
				}catch(ParseException e1){
					logger.warn(String.format("start date %s for stock %s is not parsable, using market start date.", strDate, stockId));
				}
			}
		}
		if (d.before(sc.getMarketStartDate())){
			d = sc.getMarketStartDate();
		}
		logger.info(String.format("stock %s ipo date: %s", stockId, sc.getSdf().format(d)));
		return d;
	}
	
	public static int[] getStartYearQuarterByStockId(StockConfig sc, String marketId, String stockid, CrawlConf cconf, String cmd){
		Date d = getIPODateByStockId(sc, marketId, stockid, cconf, cmd);//IPO date UTC timezone
		return DateTimeUtil.getYearQuarter(d, TimeZone.getTimeZone("UTC"));
	}
	
	private static Date getDate(StockConfig sc, String key, Map<String, Object> params){
		try{
			String strVal = (String)params.get(key);
			if (strVal!=null){
				return sc.getSdf().parse((String)params.get(key));
			}else{
				return null;
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	//all will be filtered with startDate, endDate
	//params in the task def:						method                       sample
	//marketid, stockid, year, quarter:	runTaskByStockYearQuarter	nasdaq-fr-quarter-BalanceSheet, sina-stock-fr-quarter-CashFlow 
	//marketid, stockid, date         : runTaskByStockDate			sina-stock-market-tradedetail
	//marketid, stockid,			  :	runTaskByStock				holding-insiders, dividend-history, quote-historical, short-interest, corp-info
	//marketid, date                  : runTaskByDate				sina-stock-market-dzjy, rzrq 
	//marketid                        : runTaskByMarket		        stock-ids
	//marketid, stockid, year, month  : runTaskByMarketYearMonth    sina-stock-ipo
	public static String[] runTaskByCmd(StockConfig sc, String marketId, CrawlConf cconf, String propfile, String cmdName, 
			Map<String, Object> params){
		List<Task> tl = new ArrayList<Task>();
		boolean sync = false;
		if (sc.getSyncCmds()!=null){
			if (Arrays.asList(sc.getSyncCmds()).contains(cmdName)){
				sync = true;
			}
		}
		String confFileName = cmdName + ".xml";
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		String[] jobIds = new String[]{};
		String[] allJobIds = new String[]{};
		for (Task t: tl){
			//get the bpt definition out of the t
			t.initParsedTaskDef();
			String mapperClassName = DEFAULT_MAPPER;
			String reducerClassName = null;
			ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
			if (btt.getParamMap().containsKey(PK_MAPPER)){
				mapperClassName = btt.getParamMap().get(PK_MAPPER).getValue();
			}
			if (btt.getParamMap().containsKey(PK_REDUCER)){
				reducerClassName = btt.getParamMap().get(PK_REDUCER).getValue();
			}
			if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_MARKETID)){
				params.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
				Date sd = getDate(sc, AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
				Date ed = getDate(sc, AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
				if (btt.getParamMap().containsKey(PK_STOCKID)){
					if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_YEAR) && btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_QUARTER)){
						jobIds = runTaskByStockYearQuarter(sc, marketId, cconf, propfile, t, sd, ed, cmdName, params, sync, mapperClassName, reducerClassName);
					}else if (btt.getParamMap().containsKey(PK_DATE)){
						jobIds = runTaskByStockDate(sc, marketId, sd, ed, cconf, propfile, t, params, cmdName, sync, mapperClassName, reducerClassName);
					}else{
						jobIds = runTaskByStock(sc, marketId, cconf, propfile, t, params, cmdName, sync, mapperClassName, reducerClassName);
					}
				}else{
					if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_YEAR) && btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_MONTH)){
						jobIds = runTaskByMarketYearMonth(sc, cconf, propfile, t, cmdName, params, sync, mapperClassName, reducerClassName);
					}else if (btt.getParamMap().containsKey(PK_DATE)){
						jobIds = runTaskByDate(sc, sd, ed, cconf, propfile, t, params, cmdName, sync, mapperClassName, reducerClassName);
					} else{
						jobIds = runTaskByMarket(sc, cconf, propfile, t, cmdName, params, sync, mapperClassName, reducerClassName);
					}
				}
			}else{
				logger.error(String.format("cmd %s has no mandatory field 'marketId'.", cmdName));
			}
			allJobIds = ArrayUtils.addAll(allJobIds, jobIds);
		}
		return allJobIds;
	}
	
	private static Pattern marketIdPattern = Pattern.compile("(.*)_([0-9]+\\-[0-9]+\\-[0-9]+)");
	private static void updateMarketIdParam(Task t){
		String marketId = (String) t.getParamMap().get(AbstractCrawlItemToCSV.FN_MARKETID);
		Matcher m = marketIdPattern.matcher(marketId);
		if (marketId!=null && m.matches()){
			String orgMarketId = m.group(1);
			t.putParam(AbstractCrawlItemToCSV.FN_MARKETID, orgMarketId);
		}
	}
	
	private static String[] runTaskByMarketYearMonth(StockConfig sc, CrawlConf cconf, String propfile, Task t, String cmd, 
			Map<String, Object> params, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByMarketYearMonth");
		List<Task> tlist = new ArrayList<Task>();
		Date sd = getDate(sc, AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
		Date ed = getDate(sc, AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
		//
		Calendar calInst = Calendar.getInstance();
		calInst.setTime(ed);
		int cy = calInst.get(Calendar.YEAR);
		int cm = calInst.get(Calendar.MONTH);
		if (sd==null){
			sd = sc.getMarketStartDate();
		}
		calInst.setTime(sd);
		int fy = calInst.get(Calendar.YEAR);
		int fm = calInst.get(Calendar.MONTH);
		for (int year=fy; year<=cy; year++){
			int sm = 1;
			int tm = 12;
			if (year==fy){
				sm = fm;
			}else if (year==cy){
				tm = cm;
			}
			for (int month=sm; month<=tm; month++){
				if (year==cy && month>cm){
					break;
				}
				String strMonth = month + "";
				if (month<10){
					strMonth = "0" + month;
				}
				Task t1 = t.clone(ETLUtil.class.getClassLoader());
				t1.putParam("year", year);
				t1.putParam("month", strMonth);
				t1.putAllParams(params);
				updateMarketIdParam(t1);
				tlist.add(t1);
			}
		}
		String taskName = ETLUtil.getTaskName(sc, cmd, null);
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put(AbstractCrawlItemToCSV.FN_MARKETID, (String) params.get(AbstractCrawlItemToCSV.FN_MARKETID));
		hadoopJobParams.put(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, sc.getSdf().format(ed));
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync,
				mapperClassName, reducerClassName, hadoopJobParams);
		return new String[]{jobId};
	}
	
	private static String[] runTaskByMarket(StockConfig sc, CrawlConf cconf, String propfile, Task t, String cmd, 
			Map<String, Object> params, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByMarket");
		List<Task> tlist = new ArrayList<Task>();
		t.putAllParams(params);
		updateMarketIdParam(t);
		tlist.add(t);
		String taskName = ETLUtil.getTaskName(sc, cmd, t.getParamMap());
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	
	private static String[] runTaskByDate(StockConfig sc, Date startDate, Date endDate, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String cmd, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByDate");
		Date toDate = new Date();
		if (endDate!=null){
			toDate = endDate;
		}
		LinkedList<Date> dll = StockUtil.getOpenDayList(startDate, toDate, sc.getHolidays(), sc.getTimeZone());
		List<Task> tlist = new ArrayList<Task>();
		Iterator<Date> tryDates = dll.listIterator();
		while(tryDates.hasNext()){
			Date d = tryDates.next();
			String dstr = sc.getSdf().format(d);
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null){
				t1.putAllParams(params);
				updateMarketIdParam(t1);
			}
			t1.putParam("date", dstr);
			tlist.add(t1);
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, startDate);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
		logger.info("sending out:" + tlist.size() + " tasks.");
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	
	/**
	 */
	private static String[] runTaskByStockDate(StockConfig sc, String marketId, Date startDate, Date endDate, 
			CrawlConf cconf, String propfile, Task t, Map<String, Object> params, String cmd, 
			boolean sync, String mapperClassName, String reducerClassName){
		logger.info(String.format("into runTaskByStockDate with marketId:%s", marketId));
		String[] ids = getStockIdByMarketId(sc, marketId, cconf, cmd);
		List<Task> tlist = new ArrayList<Task>();
		int batchId = 0;
		LinkedList<Date> cacheDates = new LinkedList<Date>();
		List<String> jobIdList = new ArrayList<String>();
		for (String id: ids){
			Date fDate = getIPODateByStockId(sc, marketId, id, cconf, cmd);
			if (startDate!=null){
				if (fDate.before(startDate)){
					fDate = startDate;
				}
			}
			logger.info(String.format("%s: ipo date %s", id, sc.getSdf().format(fDate)));
			//update cache if necessary
			Date cacheFirstDate = null;
			if (!cacheDates.isEmpty())
				cacheFirstDate = cacheDates.getFirst();
			else{
				if (endDate==null)
					cacheFirstDate = new Date();
				else{
					cacheFirstDate = endDate;
				}
			}
			LinkedList<Date> dll = StockUtil.getOpenDayList(fDate, cacheFirstDate,sc.getHolidays(), sc.getTimeZone());
			cacheDates.addAll(0, dll);
			//get dates from cache
			Date firstWorkingDay = null;
			if (StockUtil.isOpenDay(fDate, sc.getHolidays(), sc.getTimeZone())){
				firstWorkingDay = fDate;
			}else{
				firstWorkingDay = StockUtil.getNextOpenDay(fDate, sc.getHolidays(), sc.getTimeZone());
			}
			int idx = cacheDates.indexOf(firstWorkingDay);
			if (idx==-1){
				logger.error("cache dates do not contains:" + firstWorkingDay);
			}else{
				Iterator<Date> tryDates = cacheDates.listIterator(idx);
				while(tryDates.hasNext()){
					Date d = tryDates.next();
					String dstr = sc.getSdf().format(d);
					Task t1 = t.clone(ETLUtil.class.getClassLoader());
					t1.putParam("stockid", id);
					t1.putParam("date", dstr);
					t1.putAllParams(params);
					updateMarketIdParam(t1);
					tlist.add(t1);
					//since tlist can be very large, generate job in between
					if (tlist.size()>=maxBatchSize){
						Map<String, Object> taskParams = new HashMap<String, Object>();
						taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
						taskParams.put(BatchId_Key, batchId);
						taskParams.put(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, startDate);
						taskParams.put(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, endDate);
						String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
						logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
						jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync));
						tlist = new ArrayList<Task>(); 
						batchId++;
					}
				}
			}
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		taskParams.put(BatchId_Key, batchId);
		String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null));
		String[] jobIds = new String[jobIdList.size()];
		return jobIdList.toArray(jobIds);
	}
	
	private static String[] runTaskByStock(StockConfig sc, String marketId, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String cmd, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByStock");
		Map<String, Date> stockLUMap = getStockLUDateByCmd(sc, cmd, cconf);
		List<Task> tlist = new ArrayList<Task>();
		List<String> allIds = Arrays.asList(getStockIdByMarketId(sc, marketId, cconf, cmd));
		for (String stockid: allIds){
			String strSd = null;
			if (stockLUMap.containsKey(stockid)){
				Date sd = stockLUMap.get(stockid);//date in the db is UTC
				if (sd!=null) {
					sd = DateTimeUtil.tomorrow(sd);
					strSd = utcsdf.format(sd);
				}
			}
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null){
				t1.putAllParams(params);
				updateMarketIdParam(t1);
				t1.putParam(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, strSd);
			}
			t1.putParam("stockid", stockid);
			tlist.add(t1);
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	
	private static int[] moveAhead(int[] yq, int step){
		int[] retYQ = new int[2];
		if (step==0){
			return yq;
		}else if (step==1){
			if (yq[1]>1){
				retYQ[1] = yq[1]-1;
				retYQ[0] = yq[0];
			}else{
				retYQ[1]= 4;
				retYQ[0]= yq[0]-1;
			}
			return retYQ;
		}else{
			logger.error("lookahead value not supported:" + step);
			return retYQ;
		}
	}
	enum OpenUrlType{
		byYearAndQuarter,
		byYear,
		byIdOnly
	}
	//if startDate is null, means no filter, otherwise set year and quarter parameter as filter
	private static String[] runTaskByStockYearQuarter(StockConfig sc, String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String cmd, Map<String, Object> params, 
			boolean sync, String mapperClassName, String reducerClassName) {
		List<String> jobIds = new ArrayList<String>();
		List<Task> tlist = new ArrayList<Task>();
		Map<String, Date> stockLUMap = getStockLUDateByCmd(sc, cmd, cconf);
		int[] eyq = DateTimeUtil.getYearQuarter(endDate, sc.getTimeZone());
		int endYear = eyq[0];
		int endQuarter = eyq[1];
		int lookahead = getLookahead(t); //how many quarter to look ahead of the current one
		//move one quarter forward
		int[] retYQ = moveAhead(new int[]{endYear, endQuarter}, lookahead);
		endYear = retYQ[0];
		endQuarter = retYQ[1];
		List<String> allIds = Arrays.asList(getStockIdByMarketId(sc, marketId, cconf, cmd));
		
		OpenUrlType out;
		String startUrl = getStartUrl(t);
		if (startUrl.contains(ConfKey.PARAM_PRE+AbstractCrawlItemToCSV.FN_YEAR+ConfKey.PARAM_POST) && 
				startUrl.contains(ConfKey.PARAM_PRE+AbstractCrawlItemToCSV.FN_QUARTER+ConfKey.PARAM_POST)){
			//need to open url for each year and quarter, so we need to use ipo start date.
			out = OpenUrlType.byYearAndQuarter;
		}else if (startUrl.contains(ConfKey.PARAM_PRE+AbstractCrawlItemToCSV.FN_YEAR+ConfKey.PARAM_POST)){
			out = OpenUrlType.byYear;
		}else{
			out = OpenUrlType.byIdOnly;
		}
		
		for (String stockid: allIds){
			Date sd = null;
			if (stockLUMap.containsKey(stockid)){
				sd = stockLUMap.get(stockid);
				if (sd!=null){//since we crawl [startDate, endDate)
					sd = DateTimeUtil.tomorrow(sd);
				}
			}
			if (sd==null){
				//need to open url for each year and quarter, so we need to use ipo start date.
				sd = getIPODateByStockId(sc, marketId, stockid, cconf, cmd);
			}
			int[] yq = DateTimeUtil.getYearQuarter(sd, TimeZone.getTimeZone("UTC"));//sd is either from db or ipo date
			int startYear = yq[0];
			int startQuarter = yq[1];
		
			if (startYear<endYear||
					(startYear==endYear&&startQuarter<=endQuarter)){//we have quarter need to work on
				if (out==OpenUrlType.byYearAndQuarter){
					int year;
					int quarter;
					for (year=startYear; year<=endYear; year++){
						int startQ;
						int endQ;
						if (startYear==endYear){//same year
							startQ=startQuarter;
							endQ =endQuarter;
						}else{
							if (year==startYear){//1st year
								startQ = startQuarter;
								endQ=4;
							}else if (year==endYear){//last year
								startQ=1;
								endQ=endQuarter;
							}else{//for any year between
								startQ=1;
								endQ=4;
							}
						}
						for (quarter=startQ;quarter<=endQ;quarter++){
							Task t1 = t.clone(ETLUtil.class.getClassLoader());
							t1.putParam("stockid", stockid);
							t1.putParam("year", year);
							t1.putParam("quarter", quarter);
							t1.putAllParams(params);
							updateMarketIdParam(t1);
							tlist.add(t1);
						}
					}
				}else if (out==OpenUrlType.byYear){
					int year;
					for (year=startYear; year<=endYear; year++){
						Task t1 = t.clone(ETLUtil.class.getClassLoader());
						t1.putParam("stockid", stockid);
						t1.putParam("year", year);
						if (year==startYear){//for the start year, the there is a startQuarter
							t1.putParam("quarter", startQuarter);
						}else{
							t1.putParam("quarter", 0);//any quarter
						}
						t1.putAllParams(params);
						updateMarketIdParam(t1);
						tlist.add(t1);
						
					}
				}else if (out==OpenUrlType.byIdOnly){//set year and quarter to startYear and startQuarter
					Task t1 = t.clone(ETLUtil.class.getClassLoader());
					t1.putParam("stockid", stockid);
					t1.putParam("year", startYear);
					t1.putParam("quarter", startQuarter);
					t1.putAllParams(params);
					updateMarketIdParam(t1);
					tlist.add(t1);
				}
			}
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		jobIds.add(CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null));
	
		String[] strArray = new String[jobIds.size()];
		strArray = jobIds.toArray(strArray);
		return strArray;
		
	}
}
