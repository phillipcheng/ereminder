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
import org.xml.mytaskdef.ParsedBrowsePrd;

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	
	//for task name
	public static final String BatchId_Key="BatchId";
	public static final String PK_STOCKID="stockid";
	public static final String PK_MARKETID="marketId";
	public static final String PK_DATE="date";
	public static final String PK_YEAR="year";
	public static final String PK_QUARTER="quarter";
	public static final String PK_MONTH="month";
	public static final String PK_MAPPER="mapper";
	public static final String PK_REDUCER="reducer";
	
	//parameter keys define in the browseTask
	
	public static final String DEFAULT_MAPPER="org.cld.datacrawl.hadoop.CrawlTaskMapper";
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static boolean isStatic(Task t){
		ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
		if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_STATIC) && 
				"true".equals(btt.getParamMap().get(AbstractCrawlItemToCSV.FIELD_NAME_STATIC).getValue())){
			return true;
		}
		return false;
	}
	
	private static boolean hasDateIdx(ParsedBrowsePrd btt){
		if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_ColDateIdx)
				||btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_RowDateIdx)
				||btt.getParamMap().containsKey(ETLUtil.PK_DATE)
				||btt.getPdtAttrMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_ColDateIdx)
				||btt.getPdtAttrMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_RowDateIdx)){
			return true;
		}else{
			return false;
		}
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
	
	public static boolean needOverwrite(Task t){//for in-accurate date filtering, we need overwrite
		ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
		if (!isStatic(t)){
			//dynamic
			if (hasDateIdx(btt)){
				return false;//has exact date filtering
			}else{
				return true;//no exact filtering, need overwrite
			}
		}
		return false;
	}
	
	public static boolean needOverwrite(CrawlConf cconf, String cmdName){//for dynamic and no update-date field data
		List<Task> tl = cconf.setUpSite(cmdName+".xml", null);
		if (tl.size()==1 && (tl.get(0) instanceof CrawlTaskConf)){
			CrawlTaskConf ct = (CrawlTaskConf)tl.get(0);
			return needOverwrite(ct);
		}else{
			logger.error(String.format("needOverwrite cmd %s does not contain 1 crawlTask.", cmdName));	
		}
		return false;
	}
	
	public static String getTaskName(String calledMethod, Map<String, Object> params){
		StringBuffer sb = new StringBuffer();
		sb.append(calledMethod);
		sb.append("_");
		if (params!=null){
			for (String key: params.keySet()){
				sb.append(key);
				sb.append("_");
				Object val = params.get(key);
				if (val!=null){
					String sval = null;
					if (val instanceof Date){
						sval = sdf.format(val);
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
	
	public static String[] getStockIdByMarketId(StockConfig sc, String marketId, CrawlConf cconf){
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getStockIdsCmd(), null);
		List<String> ids = (List<String>) ci.getParam(StockBase.KEY_IDS);
		String[] idarray = new String[ids.size()];
		idarray = ids.toArray(idarray);
		return idarray;
	}
	
	//get the stocks which has no result for cmd of the given year and quarter
	public static List<String> getStockIdByCmdYearQuarter(StockConfig sc, String marketId, String cmd, int year, int quarter, CrawlConf cconf){
		try{
			Class.forName(cconf.getResultDmDriver());
		}catch (Exception e){
			logger.error("", e);
		}
		Connection con = null;
		Statement stmt = null;
		List<String> stockids = new ArrayList<String>();
		try{
			con = DriverManager.getConnection(cconf.getResultDmUrl(), cconf.getResultDmUser(), cconf.getResultDmPass());			
			String query = sc.getByQuarterSQLByCmd(cmd, year, quarter);
			if (query!=null){
				stmt = con.createStatement();
				ResultSet res = stmt.executeQuery(query);
				while (res.next()){
					stockids.add(res.getString(1));
				}
				res.close();
			}
		}catch(Exception e){
			logger.error("", e);
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
		return stockids;
	}
	
	private static Map<String, String> ipoCache = null;
	public static Date getIPODateByStockId(StockConfig sc, String marketId, String stockid, CrawlConf cconf){
		if (ipoCache==null){
			//get the IPODate
			if (sc.getIPODateCmd()!=null){
				CrawledItem ipodateCI = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getIPODateCmd(), null);
				ipoCache = (Map<String, String>)ipodateCI.getParam(StockBase.KEY_IPODate_MAP);
			}else{
				CrawledItem ipodateCI = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getStockIdsCmd(), null);
				ipoCache = (Map<String, String>)ipodateCI.getParam(StockBase.KEY_IPODate_MAP);
			}
		}
		String strDate = ipoCache.get(stockid);
		Date d = null;
		if (strDate==null){
			d = sc.getMarketStartDate();
		}else{
			try {
				d = sdf.parse(strDate);
			} catch (ParseException e) {
				logger.error("", e);
			}
		}
		if (d.before(sc.getMarketStartDate())){
			d = sc.getMarketStartDate();
		}
		logger.info(String.format("stock %s ipo date: %s", stockid, sdf.format(d)));
		return d;
	}
	
	public static int[] getStartYearQuarterByStockId(StockConfig sc, String marketId, String stockid, CrawlConf cconf){
		Date d = getIPODateByStockId(sc, marketId, stockid, cconf);
		return DateTimeUtil.getYearQuarter(d);
	}
	
	private static Date getDate(String key, Map<String, Object> params){
		try{
			String strVal = (String)params.get(key);
			if (strVal!=null){
				return sdf.parse((String)params.get(key));
			}else{
				return null;
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	//params in the task def:						method
	//stockid, year, quarter, startDate, endDate:	runTaskByStockYearQuarter	filter on the year, quarter page
	//stockid, year, 		  startDate, endDate:	runTaskByStockYear			filter on the year page
	//stockid, date:								runTaskByStockDate			
	//stockid,				  startDate, endDate:	runTaskByStock				filter on the overall page
	//date:											runTaskByDate				
	//marketid:										runTaskByMarket				
	//confName is command
	public static String[] runTaskByCmd(StockConfig sc, String marketId, CrawlConf cconf, String propfile, String confName, 
			Map<String, Object> params){
		List<Task> tl = new ArrayList<Task>();
		boolean sync = false;
		if (sc.getSyncCmds()!=null){
			if (Arrays.asList(sc.getSyncCmds()).contains(confName)){
				sync = true;
			}
		}
		String confFileName = confName + ".xml";
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
			if (btt.getParamMap().containsKey(PK_STOCKID)){
				if (btt.getParamMap().containsKey(PK_YEAR) && btt.getParamMap().containsKey(PK_QUARTER)){
					Date sd = getDate(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
					Date ed = getDate(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
					jobIds = runTaskByStockYearQuarter(sc, marketId, cconf, propfile, t, sd, ed, confName, params, sync, mapperClassName, reducerClassName);
				}else if (btt.getParamMap().containsKey(PK_DATE)){
					Date sd = getDate(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
					Date ed = getDate(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
					jobIds = runTaskByStockDate(sc, marketId, sd, ed, cconf, propfile, t, params, confName, sync, mapperClassName, reducerClassName);
				}else{
					jobIds = runTaskByStock(sc, marketId, cconf, propfile, t, params, confName, sync, mapperClassName, reducerClassName);
				}
			}else if (btt.getParamMap().containsKey(PK_MARKETID)){
				params.put(PK_MARKETID, marketId);
				if (btt.getParamMap().containsKey(PK_YEAR) && btt.getParamMap().containsKey(PK_MONTH)){
					jobIds = runTaskByMarketYearMonth(sc, cconf, propfile, t, confName, params, sync, mapperClassName, reducerClassName);
				}else{
					jobIds = runTaskByMarket(cconf, propfile, t, confName, params, sync, mapperClassName, reducerClassName);
				}
			}else{
				Date sd = getDate(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
				Date ed = getDate(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
				jobIds = runTaskByDate(sd, ed, cconf, propfile, t, params, confName, sync, mapperClassName, reducerClassName);
			}
			allJobIds = ArrayUtils.addAll(allJobIds, jobIds);
		}
		return allJobIds;
	}
	
	private static void updateMarketIdParam(Task t){
		String marketId = (String) t.getParamMap().get(PK_MARKETID);
		if (marketId!=null && marketId.contains("_")){
			String orgMarketId = marketId.substring(0, marketId.indexOf("_"));
			t.putParam(PK_MARKETID, orgMarketId);
		}
	}
	private static String[] runTaskByMarketYearMonth(StockConfig sc, CrawlConf cconf, String propfile, Task t, String calledMethod, 
			Map<String, Object> params, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByMarketYearMonth");
		List<Task> tlist = new ArrayList<Task>();
		Date sd = getDate(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
		Date ed = getDate(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
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
		String taskName = ETLUtil.getTaskName(calledMethod, null);
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put(PK_MARKETID, (String) params.get(PK_MARKETID));
		hadoopJobParams.put(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, sdf.format(ed));
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync,
				mapperClassName, reducerClassName, hadoopJobParams);
		return new String[]{jobId};
	}
	
	private static String[] runTaskByMarket(CrawlConf cconf, String propfile, Task t, String calledMethod, 
			Map<String, Object> params, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByMarket");
		List<Task> tlist = new ArrayList<Task>();
		t.putAllParams(params);
		updateMarketIdParam(t);
		tlist.add(t);
		String taskName = ETLUtil.getTaskName(calledMethod, t.getParamMap());
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	
	private static String[] runTaskByDate(Date startDate, Date endDate, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String calledMethod, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByDate");
		Date toDate = new Date();
		if (endDate!=null){
			toDate = endDate;
		}
		LinkedList<Date> dll = DateTimeUtil.getWorkingDayList(startDate, toDate);
		List<Task> tlist = new ArrayList<Task>();
		Iterator<Date> tryDates = dll.listIterator();
		while(tryDates.hasNext()){
			Date d = tryDates.next();
			String dstr = sdf.format(d);
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
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		logger.info("sending out:" + tlist.size() + " tasks.");
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	
	private static String[] runTaskByStock(StockConfig sc, String marketId, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String calledMethod, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByStock");
		String[] idarray = getStockIdByMarketId(sc, marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<idarray.length; i++){
			String stockid = idarray[i];
			stockid = sc.trimStockId(stockid);
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null){
				t1.putAllParams(params);
				updateMarketIdParam(t1);
			}
			t1.putParam("stockid", stockid);
			tlist.add(t1);
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(PK_MARKETID, marketId);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		if (jobId!=null){
			return new String[]{jobId};
		}else{
			return new String[]{};
		}
	}
	
	/**
	 * 
	 * @param ids
	 * @param cconf
	 * @param propfile
	 * @param confName
	 * @param minStartDate: if specified, min start date
	 */
	private static String[] runTaskByStockDate(StockConfig sc, String marketId, Date startDate, Date endDate, 
			CrawlConf cconf, String propfile, Task t, Map<String, Object> params, String calledMethod, 
			boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByStockDate");
		String[] ids = getStockIdByMarketId(sc, marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		int batchId = 0;
		LinkedList<Date> cacheDates = new LinkedList<Date>();
		List<String> jobIdList = new ArrayList<String>();
		for (String id: ids){
			String trimmedId = sc.trimStockId(id);
			Date fDate = getIPODateByStockId(sc, marketId, trimmedId, cconf);
			if (startDate!=null){
				if (fDate.before(startDate)){
					fDate = startDate;
				}
			}
			logger.info(String.format("%s: ipo date %s", id, sdf.format(fDate)));
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
			LinkedList<Date> dll = DateTimeUtil.getWorkingDayList(fDate, cacheFirstDate);
			logger.info(String.format("gen tasks for stock %s from %s to %s", id, sdf.format(fDate), sdf.format(cacheFirstDate)));
			cacheDates.addAll(0, dll);
			//get dates from cache
			Date firstWorkingDay = null;
			if (DateTimeUtil.isWorkingDay(fDate)){
				firstWorkingDay = fDate;
			}else{
				firstWorkingDay = DateTimeUtil.nextWorkingDay(fDate);
			}
			int idx = cacheDates.indexOf(firstWorkingDay);
			if (idx==-1){
				logger.error("cache dates do not contains:" + firstWorkingDay);
			}else{
				Iterator<Date> tryDates = cacheDates.listIterator(idx);
				while(tryDates.hasNext()){
					Date d = tryDates.next();
					String dstr = sdf.format(d);
					Task t1 = t.clone(ETLUtil.class.getClassLoader());
					t1.putParam("stockid", id);
					t1.putParam("date", dstr);
					t1.putAllParams(params);
					updateMarketIdParam(t1);
					tlist.add(t1);
					//since tlist can be very large, generate job in between
					if (tlist.size()>=100000){
						Map<String, Object> taskParams = new HashMap<String, Object>();
						taskParams.put(PK_MARKETID, marketId);
						taskParams.put(BatchId_Key, batchId);
						taskParams.put(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, startDate);
						taskParams.put(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, endDate);
						String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
						logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
						jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync));
						tlist = new ArrayList<Task>(); 
						batchId++;
					}
				}
			}
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(PK_MARKETID, marketId);
		taskParams.put(BatchId_Key, batchId);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null));
		String[] jobIds = new String[jobIdList.size()];
		return jobIdList.toArray(jobIds);
	}
	
	public static Date adjustStartDate(Date startDate, Date endDate){
		//make startDate at least 6 months before endDate, to give such slow data a buffer to publish
		if (startDate!=null){
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			c.add(Calendar.MONTH, -6);
			Date sd = c.getTime();
			if (startDate.before(sd)){
				logger.info(String.format("start date %s adjusted to %s", sdf.format(startDate), sdf.format(sd)));
				return startDate;
			}else{
				return sd;
			}
		}else{
			return startDate;
		}
	}
	//if startDate is null, means no filter, otherwise set year and quarter parameter as filter
	private static String[] runTaskByStockYearQuarter(StockConfig sc, String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String calledMethod, Map<String, Object> params, 
			boolean sync, String mapperClassName, String reducerClassName) {
		if (startDate==null){
			return runTaskByStock(sc, marketId, cconf, propfile, t, params, calledMethod, sync, mapperClassName, reducerClassName);
		}else{
			List<String> jobIds = new ArrayList<String>();
			int[] yq = DateTimeUtil.getYearQuarter(startDate);
			int startYear, startQuarter, endYear, endQuarter;
			startYear = yq[0];
			startQuarter = yq[1];
			yq = DateTimeUtil.getYearQuarter(endDate);
			endYear = yq[0];
			endQuarter = yq[1];
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
					//move one quarter forward
					int tryQ = quarter;
					int tryY = year;
					if (quarter>1){
						tryQ = quarter-1;
					}else{
						tryQ=4;
						tryY = year -1;
					}
					//get the stocks we need to fetch for the given year and quarter
					String[] allstockids = getStockIdByMarketId(sc, marketId, cconf);
					List<String> doneStockids = getStockIdByCmdYearQuarter(sc, marketId, calledMethod, tryY, tryQ, cconf);
					List<Task> tlist = new ArrayList<Task>();
					for (String sid:allstockids){
						String trimStockId = sc.trimStockId(sid);
						if (!doneStockids.contains(trimStockId)){
							Task t1 = t.clone(ETLUtil.class.getClassLoader());
							t1.putParam("stockid", trimStockId);
							t1.putParam("year", tryY);
							t1.putParam("quarter", tryQ);
							t1.putAllParams(params);
							updateMarketIdParam(t1);
							tlist.add(t1);
						}
					}
					Map<String, Object> taskParams = new HashMap<String, Object>();
					taskParams.put(PK_MARKETID, marketId);
					taskParams.put("year", tryY);
					taskParams.put("quarter", tryQ);
					String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
					logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
					jobIds.add(CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null));
				}
			}
			String[] strArray = new String[jobIds.size()];
			strArray = jobIds.toArray(strArray);
			return strArray;
		}
	}
}
