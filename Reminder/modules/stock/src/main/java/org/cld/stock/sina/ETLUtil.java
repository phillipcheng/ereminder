package org.cld.stock.sina;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.cld.datastore.entity.CrawledItem;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.xml.mytaskdef.ParsedBrowsePrd;

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	
	//for task name
	public static final String BatchId_Key="BatchId";
	
	//parameters define in the browseTask
	public static final String PK_STOCKID="stockid";
	public static final String PK_DATE="date";
	public static final String PK_START_DATE="startDate";
	public static final String PK_END_DATE="endDate";
	public static final String PK_YEAR="year";
	public static final String PK_QUARTER="quarter";
	public static final String PK_MARKETID="marketId";
	
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat detailsdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	
	
	public static String getTaskName(String calledMethod, Map<String, Object> params){
		StringBuffer sb = new StringBuffer();
		sb.append(calledMethod);
		sb.append("_");
		sb.append(detailsdf.format(new Date()));
		sb.append("_");
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
		return sb.toString();
	}
	
	public static String[] getStockIdByMarketId(String marketId, CrawlConf cconf){
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, StockConfig.SINA_STOCK_IDS, null);
		List<String> ids = (List<String>) ci.getParam("ids");
		String[] idarray = new String[ids.size()];
		idarray = ids.toArray(idarray);
		return idarray;
	}
	
	public static Date getIPODateByStockId(String stockid, CrawlConf cconf){
		//get the IPODate
		CrawledItem corpInfo = cconf.getDsm("hbase").getCrawledItem(stockid, StockConfig.SINA_STOCK_CORP_INFO, null);
		List<String> fnList = (List<String>)corpInfo.getParam(StockConfig.SINA_STOCK_DATA);
		String ipoDateStr = fnList.get(StockConfig.IPO_DATE_IDX).trim();
		String foundDateStr = fnList.get(StockConfig.FOUND_DATE_IDX).trim();
		Date d = DateTimeUtil.getDate(ipoDateStr, sdf);
		if (d==null){
			logger.warn("wrong ipo date found:" + ipoDateStr + ", for stock:" + stockid + ", try found date:" + foundDateStr);
			d = DateTimeUtil.getDate(foundDateStr, sdf);
			if (d==null)
				logger.warn("wrong found date found:" + foundDateStr + ", for stock:" + stockid);	
		}
		return d;
	}
	
	public static int[] getStartYearQuarterByStockId(String stockid, CrawlConf cconf){
		Date d = getIPODateByStockId(stockid, cconf);
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
	//stockid,				  startDate, endDate:	runTaskByStock				filter on the overall page
	//stockid, date:								runTaskByStockDate			filter by url
	//stockid:										runTaskByStock				filter by stockid
	//date:											runTaskByDate				filter by url for all stock
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
			t.initParsedTaskDef(params);
			ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
			if (btt.getParamMap().containsKey(PK_STOCKID)){
				if (btt.getParamMap().containsKey(PK_YEAR) && btt.getParamMap().containsKey(PK_QUARTER)){
					Date sd = getDate(PK_START_DATE, params);
					Date ed = getDate(PK_END_DATE, params);
					jobIds = runTaskByStockYearQuarter(marketId, cconf, propfile, t, sd, ed, confName, params, sync);
				}else if (btt.getParamMap().containsKey(PK_YEAR)){
					Date sd = getDate(PK_START_DATE, params);
					Date ed = getDate(PK_END_DATE, params);
					jobIds = runTaskByStockYear(marketId, cconf, propfile, t, sd, ed, confName, params, sync);
				}else if (btt.getParamMap().containsKey(PK_START_DATE) && btt.getParamMap().containsKey(PK_END_DATE)){
					jobIds = runTaskByStock(marketId, cconf, propfile, t, params, confName, sync);
				}else if (btt.getParamMap().containsKey(PK_DATE)){
					Date sd = getDate(PK_START_DATE, params);
					Date ed = getDate(PK_END_DATE, params);
					jobIds = runTaskByStockDate(marketId, sd, ed, cconf, propfile, t, confName, sync);
				}else{
					jobIds = runTaskByStock(marketId, cconf, propfile, t, params, confName, sync);
				}
			}else if (btt.getParamMap().containsKey(PK_MARKETID)){
				params.put(PK_MARKETID, marketId);
				jobIds = runTaskByMarket(cconf, propfile, t, confName, params, sync);
			}else{
				Date sd = getDate(PK_START_DATE, params);
				Date ed = getDate(PK_END_DATE, params);
				jobIds = runTaskByDate(sd, ed, cconf, propfile, t, params, confName, sync);
			}
			allJobIds = ArrayUtils.addAll(allJobIds, jobIds);
		}
		return allJobIds;
	}
	
	private static String[] runTaskByMarket(CrawlConf cconf, String propfile, Task t, String calledMethod, 
			Map<String, Object> params, boolean sync){
		List<Task> tlist = new ArrayList<Task>();
		t.putAllParams(params);
		tlist.add(t);
		String taskName = ETLUtil.getTaskName(calledMethod, t.getParamMap());
		String jobId = CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync);
		return new String[]{jobId};
	}
	
	private static String[] runTaskByDate(Date startDate, Date endDate, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String calledMethod, boolean sync){
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
		taskParams.put(PK_START_DATE, startDate);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		logger.info("sending out:" + tlist.size() + " tasks.");
		String jobId = CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync);
		return new String[]{jobId};
	}
	
	private static String[] runTaskByStock(String marketId, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String calledMethod, boolean sync){
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
		String jobId = CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync);
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
			CrawlConf cconf, String propfile, Task t, String calledMethod, boolean sync){
		String[] ids = getStockIdByMarketId(marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		int batchId = 0;
		LinkedList<Date> cacheDates = new LinkedList<Date>();
		List<String> jobIdList = new ArrayList<String>();
		for (String id: ids){
			String trimmedId = id.substring(2);
			Date fDate = getIPODateByStockId(trimmedId, cconf);
			if (startDate!=null){
				if (fDate.before(startDate)){
					fDate = startDate;
				}
			}
			logger.debug("ipo date:" + fDate);
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
					if (tlist.size()%100==0){
						logger.info("tlist size:" + tlist.size());
					}
					t1.putParam("stockid", id);
					t1.putParam("date", dstr);
					tlist.add(t1);
					//since tlist can be very large, generate job in between
					if (tlist.size()>=100000){
						Map<String, Object> taskParams = new HashMap<String, Object>();
						taskParams.put(PK_MARKETID, marketId);
						taskParams.put(BatchId_Key, batchId);
						taskParams.put(PK_START_DATE, startDate);
						taskParams.put(PK_END_DATE, endDate);
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
		jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync));
		String[] jobIds = new String[jobIdList.size()];
		return jobIdList.toArray(jobIds);
	}
	
	//if startYear|startQuarter <=0 from IPO year
	//if endYear|endQuarter <=0, from current time
	private static String[] runTaskByStockYearQuarter(String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String calledMethod, Map<String, Object> params, boolean sync) {
		String[] ids = getStockIdByMarketId(marketId, cconf);
		Map<String, List<String>> stockIdByYQ = new HashMap<String, List<String>>();
		for (int i=0; i<ids.length; i++){
			int startYear, startQuarter, endYear, endQuarter;
			String sid = ids[i];
			String stockid = sid.substring(2);
			if (startDate == null){
				int[] yq = getStartYearQuarterByStockId(stockid, cconf);
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
			jobIds.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync));
		}
		String[] strArray = new String[jobIds.size()];
		strArray = jobIds.toArray(strArray);
		return strArray;
	}
	
	//startYear <=0 means from IPO year
	private static String[] runTaskByStockYear(String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String calledMethod, Map<String, Object> params, boolean sync) {
		String[] ids = getStockIdByMarketId(marketId, cconf);
		Map<Integer, List<Task>> taskByYear = new HashMap<Integer, List<Task>>();
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			int startYear, endYear;
			if (startDate==null){
				int[] yq = getStartYearQuarterByStockId(stockid, cconf);
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
			jobIds.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync));
		}
		String[] strArray = new String[jobIds.size()];
		strArray = jobIds.toArray(strArray);
		return strArray;
	}
}
