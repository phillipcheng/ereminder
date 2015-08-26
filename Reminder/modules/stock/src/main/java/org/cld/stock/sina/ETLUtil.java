package org.cld.stock.sina;

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
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.CrawlTaskConf;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.stock.sina.jobs.IPODateMapper;
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
	
	public static boolean hasDateIdx(ParsedBrowsePrd btt){
		if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_ColDateIdx)
				||btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_RowDateIdx)
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
				if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_DATECOMPARE_WTIH)){
					return true;//no exact filtering, need overwrite
				}else{
					return false;//has exact date filtering
				}
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
	
	public static String[] getStockIdByMarketId(String marketId, CrawlConf cconf){
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, StockConfig.SINA_STOCK_IDS, null);
		List<String> ids = (List<String>) ci.getParam("ids");
		String[] idarray = new String[ids.size()];
		idarray = ids.toArray(idarray);
		return idarray;
	}
	
	private static Map<String, String> ipoCache = null;
	public static Date getIPODateByStockId(String marketId, String stockid, CrawlConf cconf){
		if (ipoCache==null){
			//get the IPODate
			CrawledItem ipodateCI = cconf.getDsm("hbase").getCrawledItem(marketId, StockConfig.SINA_STOCK_IPODate, null);
			ipoCache = (Map<String, String>)ipodateCI.getParam(StockConfig.SINA_STOCK_DATA);
		}
		String strDate = ipoCache.get(stockid);
		Date d = null;
		if (strDate==null){
			d = SinaStockBase.date_HS_A_START_DATE;
		}else{
			try {
				d = sdf.parse(strDate);
			} catch (ParseException e) {
				logger.error("", e);
			}
		}
		if (d.before(SinaStockBase.date_HS_A_START_DATE)){
			d = SinaStockBase.date_HS_A_START_DATE;
		}
		logger.info(String.format("stock %s ipo date: %s", stockid, sdf.format(d)));
		return d;
	}
	
	public static int[] getStartYearQuarterByStockId(String marketId, String stockid, CrawlConf cconf){
		Date d = getIPODateByStockId(marketId, stockid, cconf);
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
	//stockid, year, quarter, startDate, endDate:	runTaskByStockYearQuarter	filter on the yar, quarter page
	//stockid, year, 		  startDate, endDate:	runTaskByStockYear			filter on the year page
	//stockid, date:								runTaskByStockDate			
	//stockid,				  startDate, endDate:	runTaskByStock				filter on the overall page
	//date:											runTaskByDate				
	//marketid:										runTaskByMarket				
	//confName is command
	public static String[] runTaskByCmd(String marketId, CrawlConf cconf, String propfile, String confName, 
			Map<String, Object> params){
		List<Task> tl = new ArrayList<Task>();
		boolean sync = false;
		if (Arrays.asList(StockConfig.syncConf).contains(confName)){
			sync = true;
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
					jobIds = runTaskByStockYearQuarter(marketId, cconf, propfile, t, sd, ed, confName, params, sync, mapperClassName, reducerClassName);
				}else if (btt.getParamMap().containsKey(PK_YEAR)){
					Date sd = getDate(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
					Date ed = getDate(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
					jobIds = runTaskByStockYear(marketId, cconf, propfile, t, sd, ed, confName, params, sync, mapperClassName, reducerClassName);
				}else if (btt.getParamMap().containsKey(PK_DATE)){
					Date sd = getDate(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, params);
					Date ed = getDate(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, params);
					jobIds = runTaskByStockDate(marketId, sd, ed, cconf, propfile, t, params, confName, sync, mapperClassName, reducerClassName);
				}else{
					jobIds = runTaskByStock(marketId, cconf, propfile, t, params, confName, sync, mapperClassName, reducerClassName);
				}
			}else if (btt.getParamMap().containsKey(PK_MARKETID)){
				params.put(PK_MARKETID, marketId);
				if (btt.getParamMap().containsKey(PK_YEAR) && btt.getParamMap().containsKey(PK_MONTH)){
					jobIds = runTaskByMarketYearMonth(cconf, propfile, t, confName, params, sync, mapperClassName, reducerClassName);
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
	
	private static String[] runTaskByMarketYearMonth(CrawlConf cconf, String propfile, Task t, String calledMethod, 
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
			sd = SinaStockBase.date_HS_A_START_DATE;
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
				Task t1 = t.clone(IPODateMapper.class.getClassLoader());
				t1.putParam("year", year);
				t1.putParam("month", strMonth);
				t1.putAllParams(params);
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
		tlist.add(t);
		String taskName = ETLUtil.getTaskName(calledMethod, t.getParamMap());
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		return new String[]{jobId};
	}
	
	private static String[] runTaskByDate(Date startDate, Date endDate, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String calledMethod, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByDate");
		Date toDate = new Date();
		if (endDate!=null){
			toDate = endDate;
		}
		LinkedList<Date> dll = DateTimeUtil.getWorkingDayList(startDate, toDate);
		if (DateTimeUtil.isWorkingDay(toDate)){
			dll.add(toDate);
		}
		List<Task> tlist = new ArrayList<Task>();
		Iterator<Date> tryDates = dll.listIterator();
		while(tryDates.hasNext()){
			Date d = tryDates.next();
			String dstr = sdf.format(d);
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null)
				t1.putAllParams(params);
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
		return new String[]{jobId};
	}
	
	private static String[] runTaskByStock(String marketId, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String calledMethod, boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByStock");
		String[] idarray = getStockIdByMarketId(marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<idarray.length; i++){
			String stockid = idarray[i];
			stockid = stockid.substring(2);
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null)
				t1.putAllParams(params);
			t1.putParam("stockid", stockid);
			tlist.add(t1);
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(PK_MARKETID, marketId);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null);
		return new String[]{jobId};
	}
	
	/**
	 * 
	 * @param ids
	 * @param cconf
	 * @param propfile
	 * @param confName
	 * @param minStartDate: if specified, min start date
	 */
	private static String[] runTaskByStockDate(String marketId, Date startDate, Date endDate, 
			CrawlConf cconf, String propfile, Task t, Map<String, Object> params, String calledMethod, 
			boolean sync, String mapperClassName, String reducerClassName){
		logger.info("into runTaskByStockDate");
		String[] ids = getStockIdByMarketId(marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		int batchId = 0;
		LinkedList<Date> cacheDates = new LinkedList<Date>();
		List<String> jobIdList = new ArrayList<String>();
		for (String id: ids){
			String trimmedId = id.substring(2);
			Date fDate = getIPODateByStockId(marketId, trimmedId, cconf);
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
	//if startYear|startQuarter <=0 from IPO year
	//if endYear|endQuarter <=0, from current time
	private static String[] runTaskByStockYearQuarter(String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String calledMethod, Map<String, Object> params, 
			boolean sync, String mapperClassName, String reducerClassName) {
		if (ETLUtil.needOverwrite(t)){
			startDate = adjustStartDate(startDate, endDate);
		}
		logger.info("into runTaskByStockYearQuarter");
		String[] ids = getStockIdByMarketId(marketId, cconf);
		Map<String, List<String>> stockIdByYQ = new TreeMap<String, List<String>>();
		for (int i=0; i<ids.length; i++){
			int startYear, startQuarter, endYear, endQuarter;
			String sid = ids[i];
			String stockid = sid.substring(2);
			if (startDate == null){
				int[] yq = getStartYearQuarterByStockId(marketId, stockid, cconf);
				startYear = yq[0];
				startQuarter = yq[1];
			}else{
				int[] yq = DateTimeUtil.getYearQuarter(startDate);
				startYear = yq[0];
				startQuarter = yq[1];
			}
			if (endDate == null){
				int[] cyq = DateTimeUtil.getYearQuarter(new Date());//TODO set to delist time, if has
				endYear = cyq[0];
				endQuarter = cyq[1];
			}else{
				int[] yq = DateTimeUtil.getYearQuarter(endDate);
				endYear = yq[0];
				endQuarter = yq[1];
			}
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
					String yearQuarter = year + "_" + quarter;
					List<String> sl = stockIdByYQ.get(yearQuarter);
					if (sl==null){
						sl = new ArrayList<String>();
						sl.add(stockid);
						stockIdByYQ.put(yearQuarter, sl);
					}else{
						sl.add(stockid);
					}
				}
			}
		}
		List<String> jobIds = new ArrayList<String>();
		for (String yearQuarter:stockIdByYQ.keySet()){
			int year = Integer.parseInt(yearQuarter.substring(0, 4));
			int quarter = Integer.parseInt(yearQuarter.substring(5));
			List<String> slist = stockIdByYQ.get(yearQuarter);
			List<Task> tlist = new ArrayList<Task>();
			for (String sid:slist){
				Task t1 = t.clone(ETLUtil.class.getClassLoader());
				t1.putParam("stockid", sid);
				t1.putParam("year", year);
				t1.putParam("quarter", quarter);
				t1.putAllParams(params);
				tlist.add(t1);
			}
			Map<String, Object> taskParams = new HashMap<String, Object>();
			taskParams.put(PK_MARKETID, marketId);
			taskParams.put("year", year);
			taskParams.put("quarter", quarter);
			String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
			logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
			jobIds.add(CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null));
		}
		String[] strArray = new String[jobIds.size()];
		strArray = jobIds.toArray(strArray);
		return strArray;
	}
	
	//startYear <=0 means from IPO year
	private static String[] runTaskByStockYear(String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String calledMethod, Map<String, Object> params, 
			boolean sync, String mapperClassName, String reducerClassName) {
		logger.info("into runTaskByStockYear");
		if (ETLUtil.needOverwrite(t)){
			startDate = adjustStartDate(startDate, endDate);
		}
		String[] ids = getStockIdByMarketId(marketId, cconf);
		Map<Integer, List<Task>> taskByYear = new TreeMap<Integer, List<Task>>();
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			int startYear, endYear;
			if (startDate==null){
				int[] yq = getStartYearQuarterByStockId(marketId, stockid, cconf);
				startYear = yq[0];
			}else{
				int[] yq = DateTimeUtil.getYearQuarter(startDate);
				startYear = yq[0];
			}
			
			if (endDate==null){
				int[] cyq = DateTimeUtil.getYearQuarter(new Date());
				endYear = cyq[0];
			}else{
				int[] cyq = DateTimeUtil.getYearQuarter(endDate);
				endYear = cyq[0];
			}
			int year;
			for (year=startYear; year<=endYear; year++){
				Task t1 = t.clone(ETLUtil.class.getClassLoader());
				t1.putParam("stockid", stockid);
				t1.putParam("year", year);
				t1.putAllParams(params);
				List<Task> tl = taskByYear.get(year);
				if (tl==null){
					tl = new ArrayList<Task>();
				}
				tl.add(t1);
				taskByYear.put(year, tl);
			}
		}
		List<String> jobIds = new ArrayList<String>();
		for (int year:taskByYear.keySet()){
			List<Task> tlist = taskByYear.get(year);
			Map<String, Object> taskParams = new HashMap<String, Object>();
			taskParams.put(PK_MARKETID, marketId);
			taskParams.put(PK_YEAR, year);
			String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
			logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
			jobIds.add(CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClassName, reducerClassName, null));
		}
		String[] strArray = new String[jobIds.size()];
		strArray = jobIds.toArray(strArray);
		return strArray;
	}
}
