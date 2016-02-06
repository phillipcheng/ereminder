package org.cld.stock.etl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.etl.base.ETLConfig;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.TaskMapper;
import org.cld.util.DateTimeUtil;
import org.cld.util.entity.CrawledItem;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	
	//for task name
	public static final String BatchId_Key="BatchId";
	public static final String PK_STOCKID="stockid";
	public static final String PK_DATE="date";
	public static final String PK_LOOKAHEADE="lookahead";//how many time unit (quarter) to look ahead, can be 0, 1, 2, etc
	public static final String PK_MAPPER="mapper";
	public static final String PK_REDUCER="reducer";

	public static final int maxBatchSize=1000000;
	//parameter keys define in the browseTask
	
	public static final Class DEFAULT_MAPPER=TaskMapper.class;
	private static Map<String, Date> ipoCache = null;//stockid to ipodate string cache
	
	public static void cleanCaches(){
		ipoCache=null;
	}
	
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

	public static boolean isStatic(ETLConfig sc, CrawlConf cconf, String cmdName){
		String confName = sc.getCrawlByCmd(cmdName);
		List<Task> tl = cconf.setUpSite(confName+".xml", null);
		if (tl.size()==1){
			return isStatic(tl.get(0));
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
	
	public static boolean needOverwrite(CrawlConf cconf, String cmdName){
		return false;
	}
	
	public static String getTaskName(ETLConfig sc, String cmd, Map<String, Object> params){
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
	
	/**
	 * @param: cmd used to decide whether returned trimmed id or not, default no trimmed, can be null
	 */
	public static String[] getStockIdByMarketId(ETLConfig sc, String marketId, CrawlConf cconf, String cmd){
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, sc.getStockIdsCmd(), null);
		List<String> ids = (List<String>) ci.getParam(StockBase.KEY_IDS);
		String[] idarray = new String[ids.size()];
		for (int i=0; i<ids.size(); i++){
			idarray[i] = sc.stockIdMarket2Cmd(ids.get(i), cmd);
		}
		return idarray;
	}
	
	public static Date getIPODateByStockId(ETLConfig sc, String marketId, String stockId, CrawlConf cconf, String cmd){
		//get the IPODate
		Map<String, String> tableMap = sc.getTablesByCmd(sc.getIPODateCmd());
		String ipoTableName = tableMap.keySet().iterator().next();//get first table
		if (ipoCache==null){
			ipoCache = StockPersistMgr.getStockIPOData(ipoTableName, cconf.getBigdbconf());
		}
		stockId = sc.stockIdCmd2DB(stockId, cmd);
		Date d = ipoCache.get(stockId);
		if (d==null){
			d = sc.getMarketStartDate();
		}
		logger.debug(String.format("stock %s ipo date: %s", stockId, sc.getSdf().format(d)));
		return d;
	}
	
	public static int[] getStartYearQuarterByStockId(ETLConfig sc, String marketId, String stockid, CrawlConf cconf, String cmd){
		Date d = getIPODateByStockId(sc, marketId, stockid, cconf, cmd);//IPO date UTC timezone
		return DateTimeUtil.getYearQuarter(d);
	}
	
	private static Date getDate(ETLConfig sc, String key, Map<String, Object> params){
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
	//marketid, stockid, year, quarter:	runTaskByStockYearQuarter	BalanceSheet, CashFlow, sina-fq, sina-historical
	//marketid, stockid, date         : runTaskByStockDate			sina-stock-market-tradedetail
	//marketid, stockid,			  :	runTaskByStock				holding-insiders, dividend-history, quote-historical, short-interest, corp-info, nasdaq-fq
	//marketid, date                  : runTaskByDate				sina-stock-market-dzjy, rzrq, nasdaq-upcoming-dividend
	//marketid                        : runTaskByMarket		        stock-ids
	//marketid, stockid, year, month  : runTaskByMarketYearMonth    sina-stock-ipo
	public static String[] runTaskByCmd(ETLConfig sc, String marketId, CrawlConf cconf, String propfile, String cmdName, 
			Map<String, Object> params, boolean useHadoop){
		List<Task> tl = new ArrayList<Task>();
		boolean sync = false;
		if (sc.getSyncCmds()!=null){
			if (Arrays.asList(sc.getSyncCmds()).contains(cmdName)){
				sync = true;
			}
		}
		
		String confFileName = sc.getCrawlByCmd(cmdName) + ".xml";
		tl = cconf.setUpSite(confFileName, null);
		
		String[] jobIds = new String[]{};
		String[] allJobIds = new String[]{};
		for (Task t: tl){
			//get the bpt definition out of the t
			t.initParsedTaskDef();
			t.putAllParams(params);
			BrowseDetailType bdt = t.getBrowseDetailTask(t.getName()).getBrowsePrdTaskType();
			BrowseProductTaskConf.evalParams(t, bdt);
			Class mapperClass = DEFAULT_MAPPER;
			Class reducerClass = null;
			ParsedBrowsePrd btt = t.getBrowseDetailTask(t.getName());
			try{
				if (btt.getParamMap().containsKey(PK_MAPPER)){
					mapperClass = Class.forName(btt.getParamMap().get(PK_MAPPER).getValue());
				}
				if (btt.getParamMap().containsKey(PK_REDUCER)){
					reducerClass = Class.forName(btt.getParamMap().get(PK_REDUCER).getValue());
				}
			}catch(Exception e){
				logger.error("", e);
			}
			if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_MARKETID)){
				params.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
				Date sd = getDate(sc, AbstractCrawlItemToCSV.FN_STARTDATE, params);
				Date ed = getDate(sc, AbstractCrawlItemToCSV.FN_ENDDATE, params);
				if (btt.getParamMap().containsKey(PK_STOCKID)){
					if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_YEAR) && btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_QUARTER)){
						jobIds = runTaskByStockYearQuarter(sc, marketId, cconf, propfile, t, sd, ed, cmdName, params, sync, mapperClass, reducerClass);
					}else if (btt.getParamMap().containsKey(PK_DATE)){
						jobIds = runTaskByStockDate(sc, marketId, sd, ed, cconf, propfile, t, params, cmdName, sync, mapperClass, reducerClass);
					}else{
						jobIds = runTaskByStock(sc, marketId, cconf, propfile, t, params, cmdName, sync, mapperClass, reducerClass);
					}
				}else{
					if (btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_YEAR) && btt.getParamMap().containsKey(AbstractCrawlItemToCSV.FN_MONTH)){
						jobIds = runTaskByMarketYearMonth(sc, cconf, propfile, t, cmdName, params, sync, mapperClass, reducerClass);
					}else if (btt.getParamMap().containsKey(PK_DATE)){
						jobIds = runTaskByDate(sc, sd, ed, cconf, propfile, t, params, cmdName, sync, mapperClass, reducerClass, useHadoop);
					} else{
						jobIds = runTaskByMarket(sc, cconf, propfile, t, cmdName, params, sync, mapperClass, reducerClass, useHadoop);
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
	
	private static boolean needCheckDB(ETLConfig sc, CrawlConf cconf, String cmd){
		if (cconf.getBigdbconf().getUrl()==null)
			return false;
		if (isStatic(sc, cconf, cmd)){
			return false;
		}else if (Arrays.asList(sc.getCurrentDayCmds()).contains(cmd)){
			return false; //skip for current day cmd
		}
		return true;
	}
	
	//sina-ipo, nasdaq-ipo
	private static String[] runTaskByMarketYearMonth(ETLConfig sc, CrawlConf cconf, String propfile, Task t, String cmd, 
			Map<String, Object> params, boolean sync, Class mapperClass, Class reducerClass){
		logger.info("into runTaskByMarketYearMonth");
		List<Task> tlist = new ArrayList<Task>();
		Date sd = getDate(sc, AbstractCrawlItemToCSV.FN_STARTDATE, params);
		Date ed = getDate(sc, AbstractCrawlItemToCSV.FN_ENDDATE, params);
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
		hadoopJobParams.put(AbstractCrawlItemToCSV.FN_ENDDATE, sc.getSdf().format(ed));
		String jobId = TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync,
				mapperClass, reducerClass, hadoopJobParams);
		return new String[]{jobId};
	}
	
	private static List<String> getCsvList(List<CrawledItem> cil){
		List<String> outputList = new ArrayList<String>();
		for (CrawledItem ci:cil){
			if (ci!=null && ci.getCsvValue()!=null){
				//key,value,filename
				for (String[] kv : ci.getCsvValue()){
					if (AbstractCrawlItemToCSV.KEY_VALUE_UNDEFINED.equals(kv[0])){
						outputList.add(kv[1]);
					}else{
						String str = String.format("%s,%s", kv[0], kv[1]);
						outputList.add(str);
					}
				}
			}
		}
		return outputList;
	}
	//return job ids for hadoop (async call), return csv array if not using hadoop
	private static String[] runTaskByMarket(ETLConfig sc, CrawlConf cconf, String propfile, Task t, String cmd, 
			Map<String, Object> params, boolean sync, Class mapperClass, Class reducerClass, boolean useHadoop){
		logger.info("into runTaskByMarket");
		List<Task> tlist = new ArrayList<Task>();
		t.putAllParams(params);
		updateMarketIdParam(t);
		tlist.add(t);
		String taskName = ETLUtil.getTaskName(sc, cmd, t.getParamMap());
		if (useHadoop){
			String jobId = TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClass, reducerClass, null);
			if (jobId!=null){
				return new String[]{jobId};
			}else{
				return new String[]{};
			}
		}else{
			try {
				params.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
				TaskResult tr = t.runMyself(params, false, null, null);
				if (tr!=null){
					List<String> sl = getCsvList(tr.getCIs());
					String[] sa = new String[sl.size()];
					return sl.toArray(sa);
				}
			}catch(Exception e){
				logger.error("", e);
			}
			return new String[]{};
		}
	}
	//return job ids for hadoop (async call), return csv array if not using hadoop
	private static String[] runTaskByDate(ETLConfig etlConfig, Date startDate, Date endDate, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String cmd, boolean sync, Class mapperClass, Class reducerClass, boolean useHadoop){
		logger.info("into runTaskByDate");
		StockConfig sc = StockUtil.getStockConfig(etlConfig.getBaseMarketId());
		Map<String, String> tables = etlConfig.getTablesByCmd(cmd);
		Date luDate = null;
		if (tables!=null)
			luDate = StockPersistMgr.getMarketLUDateByCmd(tables.keySet(), cconf.getBigdbconf());
		Date sd = null;
		if (luDate!=null){
			sd = DateTimeUtil.tomorrow(luDate);
		}else if (startDate!=null){
			sd = startDate;
		}else{
			String strSd = etlConfig.getStartDate(cmd);
			if (strSd!=null){
				try {
					sd = etlConfig.getSdf().parse(strSd);
				} catch (ParseException e) {
					logger.error("", e);
				}
			}else{
				sd = etlConfig.getMarketStartDate();
			}
		}
		LinkedList<Date> dll = StockUtil.getOpenDayList(sd, endDate, sc.getHolidays());
		List<Task> tlist = new ArrayList<Task>();
		Iterator<Date> tryDates = dll.listIterator();
		while(tryDates.hasNext()){
			Date d = tryDates.next();
			String dstr = etlConfig.getSdf().format(d);
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null){
				t1.putAllParams(params);
				updateMarketIdParam(t1);
			}
			t1.putParam("date", dstr);
			tlist.add(t1);
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_STARTDATE, startDate);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(etlConfig, cmd, taskParams);
		logger.info("sending out:" + tlist.size() + " tasks.");
		if (useHadoop){
			String jobId = TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClass, reducerClass, null);
			if (jobId!=null){
				return new String[]{jobId};
			}else{
				return new String[]{};
			}
		}else{
			List<String> output = new ArrayList<String>();
			for (Task at:tlist){
				try{
					taskParams.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
					TaskResult tr = at.runMyself(taskParams, false, null, null);
					if (tr!=null){
						List<String> sl = getCsvList(tr.getCIs());
						output.addAll(sl);
					}
				}catch(Exception e){
					logger.error("", e);
				}
			}
			String[] sa = new String[output.size()];
			return output.toArray(sa);
		}
	}
	
	//sina-stock-market-tradedetail
	private static String[] runTaskByStockDate(ETLConfig etlConfig, String marketId, Date startDate, Date endDate, 
			CrawlConf cconf, String propfile, Task t, Map<String, Object> params, String cmd, 
			boolean sync, Class mapperClass, Class reducerClass){
		logger.info(String.format("into runTaskByStockDate with marketId:%s", marketId));
		String[] ids = getStockIdByMarketId(etlConfig, marketId, cconf, cmd);
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		if (needCheckDB(etlConfig, cconf, cmd)){
			String query = etlConfig.getStockLUDateByCmd(cmd);
			stockLUMap = StockPersistMgr.getStockLUDateByCmd(query, cconf.getBigdbconf());
		}
		List<Task> tlist = new ArrayList<Task>();
		int batchId = 0;
		LinkedList<Date> cacheDates = new LinkedList<Date>();
		List<String> jobIdList = new ArrayList<String>();
		StockConfig sc = StockUtil.getStockConfig(etlConfig.getBaseMarketId());
		for (String id: ids){
			Date sd = null;
			String idForDB = etlConfig.stockIdCmd2DB(id, cmd);
			if (stockLUMap.containsKey(idForDB)){
				sd = stockLUMap.get(idForDB);
				if (sd!=null){//since we crawl [startDate, endDate)
					sd = DateTimeUtil.tomorrow(sd);
				}
			}
			if (sd==null){
				//need to open url for each year and quarter, so we need to use ipo start date.
				sd = getIPODateByStockId(etlConfig, marketId, id, cconf, cmd);
			}
			logger.info(String.format("%s: start date for cmd %s is %s", id, cmd, etlConfig.getSdf().format(sd)));
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
			LinkedList<Date> dll = StockUtil.getOpenDayList(sd, cacheFirstDate, sc.getHolidays());
			cacheDates.addAll(0, dll);
			//get dates from cache
			Date firstWorkingDay = null;
			if (StockUtil.isOpenDay(sd, sc.getHolidays())){
				firstWorkingDay = sd;
			}else{
				firstWorkingDay = StockUtil.getNextOpenDay(sd, sc.getHolidays());
			}
			int idx = cacheDates.indexOf(firstWorkingDay);
			if (idx==-1){
				logger.error("cache dates do not contains:" + firstWorkingDay);
			}else{
				Iterator<Date> tryDates = cacheDates.listIterator(idx);
				while(tryDates.hasNext()){
					Date d = tryDates.next();
					String dstr = etlConfig.getSdf().format(d);
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
						taskParams.put(AbstractCrawlItemToCSV.FN_STARTDATE, startDate);
						taskParams.put(AbstractCrawlItemToCSV.FN_ENDDATE, endDate);
						String taskName = ETLUtil.getTaskName(etlConfig, cmd, taskParams);
						logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
						jobIdList.add(TaskUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName, sync));
						tlist = new ArrayList<Task>(); 
						batchId++;
					}
				}
			}
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		taskParams.put(BatchId_Key, batchId);
		String taskName = ETLUtil.getTaskName(etlConfig, cmd, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		jobIdList.add(TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClass, reducerClass, null));
		String[] jobIds = new String[jobIdList.size()];
		return jobIdList.toArray(jobIds);
	}
	//holding-insiders, dividend-history, quote-historical, short-interest, corp-info, nasdaq-fq, sina-bulletin
	private static String[] runTaskByStock(ETLConfig sc, String marketId, CrawlConf cconf, String propfile, Task t, 
			Map<String, Object> params, String cmd, boolean sync, Class mapperClass, Class reducerClass){
		logger.info("into runTaskByStock");
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		if (!Arrays.asList(sc.getUpdateAllCmds()).contains(cmd)){
			if (needCheckDB(sc, cconf, cmd)){
				String query = sc.getStockLUDateByCmd(cmd);
				stockLUMap = StockPersistMgr.getStockLUDateByCmd(query, cconf.getBigdbconf());
			}
		}
		List<Task> tlist = new ArrayList<Task>();
		List<String> allIds = Arrays.asList(getStockIdByMarketId(sc, marketId, cconf, cmd));
		for (String stockid: allIds){
			Date sd = null;
			if (stockLUMap.containsKey(stockid)){
				sd = stockLUMap.get(stockid);//date in the db is loaded from text files which are displayed on webpage which is in each market's tz
				if (sd!=null) {
					sd = DateTimeUtil.tomorrow(sd);
				}
			}
			if (!Arrays.asList(sc.getFirstStartTimeUseNullCmds()).contains(cmd)){
				if (sd==null){
					//need to open url for each year and quarter, so we need to use ipo start date.
					sd = getIPODateByStockId(sc, marketId, stockid, cconf, cmd);
				}
				if (sd==null){
					sd = sc.getMarketStartDate();
				}
			}else{
				//keep sd null
			}
			Task t1 = t.clone(ETLUtil.class.getClassLoader());
			if (params!=null){
				t1.putAllParams(params);
				updateMarketIdParam(t1);
				if (sd!=null){
					t1.putParam(AbstractCrawlItemToCSV.FN_STARTDATE, sc.getSdf().format(sd));
				}else{//set sd null
					t1.putParam(AbstractCrawlItemToCSV.FN_STARTDATE, null);
				}
			}
			t1.putParam("stockid", stockid);
			tlist.add(t1);
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
		String jobId = TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClass, reducerClass, null);
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
	private static String[] runTaskByStockYearQuarter(ETLConfig sc, String marketId, CrawlConf cconf, String propfile, Task t, 
			Date startDate, Date endDate, String cmd, Map<String, Object> params, 
			boolean sync, Class mapperClass, Class reducerClass) {
		List<String> jobIds = new ArrayList<String>();
		List<Task> tlist = new ArrayList<Task>();
		Map<String, Date> stockLUMap = new HashMap<String, Date>();
		if (needCheckDB(sc, cconf, cmd)){
			String query = sc.getStockLUDateByCmd(cmd);
			stockLUMap = StockPersistMgr.getStockLUDateByCmd(query, cconf.getBigdbconf());
		}
		int[] eyq = DateTimeUtil.getYearQuarter(endDate);
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
			int[] yq = DateTimeUtil.getYearQuarter(sd);
			int startYear = yq[0];
			int startQuarter = yq[1];
			String strSd = sc.getSdf().format(sd);
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
							t1.putParam(AbstractCrawlItemToCSV.FN_STARTDATE, strSd);//overwrite startDate
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
						t1.putParam(AbstractCrawlItemToCSV.FN_STARTDATE, strSd);//overwrite startDate
						updateMarketIdParam(t1);
						tlist.add(t1);
						
					}
				}else if (out==OpenUrlType.byIdOnly){//set year and quarter to startYear and startQuarter
					Task t1 = t.clone(ETLUtil.class.getClassLoader());
					t1.putParam("stockid", stockid);
					t1.putParam("year", startYear);
					t1.putParam("quarter", startQuarter);
					t1.putAllParams(params);
					t1.putParam(AbstractCrawlItemToCSV.FN_STARTDATE, strSd);//overwrite startDate
					updateMarketIdParam(t1);
					tlist.add(t1);
				}
			}
		}
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		String taskName = ETLUtil.getTaskName(sc, cmd, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		jobIds.add(TaskUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tlist, taskName, sync, mapperClass, reducerClass, null));
	
		String[] strArray = new String[jobIds.size()];
		strArray = jobIds.toArray(strArray);
		return strArray;
		
	}
}
