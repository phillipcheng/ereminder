package org.cld.stock.sina;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil.browse_type;
import org.cld.datastore.entity.CrawledItem;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DateTimeUtil;
import org.cld.util.ReflectUtil;

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	
	public static final String BatchId_Key="BatchId";
	public static final String MarketId_Key="MarketId";
	public static final String StartDate_Key="StartDate";
	public static final String StartYear_Key="StartYear";
	public static final String StartQuarter_Key="StartQuarter";
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat detailsdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	public static final String MarketId_Test = "test";
	
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
		}
		return sb.toString();
	}
	
	public static String[] getStockIdByMarketId(String marketId, CrawlConf cconf){
		if (MarketId_Test.equals(marketId)){
			return new String[]{"sh600000", "sh601766", "sz000001"};
		}else{
			CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, StockConfig.SINA_STOCK_IDS, null);
			ci.fromParamData();
			List<String> ids = (List<String>) ci.getParam("ids");
			String[] idarray = new String[ids.size()];
			idarray = ids.toArray(idarray);
			return idarray;
		}
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
	
	public static void runTaskByStartDate(Date startDate, CrawlConf cconf, String propfile, String confName, 
			Map<String, Object> params){
		List<Task> tl = new ArrayList<Task>();
		String confFileName = confName + ".xml";
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		Date toDate = new Date();
		LinkedList<Date> dll = DateTimeUtil.getWorkingDayList(startDate, toDate);
		if (DateTimeUtil.isWorkingDay(toDate)){
			dll.add(toDate);
		}
		List<Task> tlist = new ArrayList<Task>();
		Iterator<Date> tryDates = dll.listIterator();
		while(tryDates.hasNext()){
			Date d = tryDates.next();
			logger.debug("try date:" + d);
			String dstr = sdf.format(d);
			for (Task t: tl){
				Task t1 = t.clone(ETLUtil.class.getClassLoader());
				if (params!=null)
					t1.putAllParams(params);
				t1.putParam("date", dstr);
				tlist.add(t1);
			}
		}
		String calledMethod = ReflectUtil.getMethodName(1);
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(StartDate_Key, startDate);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName);
	}
	
	public static void runTaskByMarket(String marketId, CrawlConf cconf, String propfile, String confName, 
			Map<String, Object> params){
		String[] idarray = ETLUtil.getStockIdByMarketId(marketId, cconf);
		String confFileName = confName + ".xml";
		List<Task> tl = new ArrayList<Task>();
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<idarray.length; i++){
			String stockid = idarray[i];
			stockid = stockid.substring(2);
			
			for (Task t: tl){
				Task t1 = t.clone(ETLUtil.class.getClassLoader());
				if (params!=null)
					t1.putAllParams(params);
				t1.putParam("stockid", stockid);
				tlist.add(t1);
			}
		}
		String calledMethod = ReflectUtil.getMethodName(1);
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(MarketId_Key, marketId);
		if (params!=null)
			taskParams.putAll(params);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName);
	}
	
	/**
	 * 
	 * @param ids
	 * @param cconf
	 * @param propfile
	 * @param confName
	 * @param minStartDate: if specified, min start date
	 */
	public static void runTaskByMarketIdStartDate(String marketId, CrawlConf cconf, String propfile, String confName, 
			Date minStartDate){
		String[] ids = getStockIdByMarketId(marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		int batchId = 0;
		LinkedList<Date> cacheDates = new LinkedList<Date>();
		for (String id: ids){
			String trimmedId = id.substring(2);
			Date fDate = getIPODateByStockId(trimmedId, cconf);
			if (minStartDate!=null){
				if (fDate.before(minStartDate)){
					fDate = minStartDate;
				}
			}
			logger.debug("ipo date:" + fDate);
			//update cache if necessary
			Date cacheFirstDate = null;
			if (!cacheDates.isEmpty())
				cacheFirstDate = cacheDates.getFirst();
			else
				cacheFirstDate = new Date();
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
					TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, confName, confName +".xml");
					ttc.putParam("stockid", id);
					ttc.putParam("date", dstr);
					tlist.add(ttc);
					//since tlist can be very large, generate job in between
					if (tlist.size()>100000){
						String calledMethod = ReflectUtil.getMethodName(1);
						Map<String, Object> taskParams = new HashMap<String, Object>();
						taskParams.put(MarketId_Key, marketId);
						taskParams.put(BatchId_Key, batchId);
						String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
						logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
						CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName);
						
						tlist = new ArrayList<Task>(); 
						batchId++;
					}
				}
			}
		}
		String calledMethod = ReflectUtil.getMethodName(1);
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(MarketId_Key, marketId);
		taskParams.put(BatchId_Key, batchId);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName);
	}
	
	public static void runTaskByMarketIdStartQuarter(String marketId, CrawlConf cconf, String propfile, String confName) {
		String[] ids = getStockIdByMarketId(marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			int[] yq = getStartYearQuarterByStockId(stockid, cconf);
			int[] cyq = DateTimeUtil.getYearQuarter(new Date());
			if (yq!=null){
				int year;
				int quarter;
				for (year=yq[0]; year<=cyq[0]; year++){
					int startQ;
					int endQ;
					if (cyq[0]==yq[0]){//IPODate and CurrentDate same year
						startQ=yq[1];
						endQ =cyq[1];
					}else{
						if (year==yq[0]){//for the IPO year
							startQ = yq[1];
							endQ=4;
						}else if (year==cyq[0]){//for the current year
							startQ=1;
							endQ=cyq[1];
						}else{//for any year between
							startQ=1;
							endQ=4;
						}
					}
					for (quarter=startQ;quarter<=endQ;quarter++){
						TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, 
								confName, confName +".xml");
						ttc.putParam("stockid", stockid);
						ttc.putParam("year", year+"");
						ttc.putParam("quarter", quarter+"");
						tlist.add(ttc);
					}
				}
			}
		}
		String calledMethod = ReflectUtil.getMethodName(1);
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(MarketId_Key, marketId);
		String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
		logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName);
	}
	
	public static void runTaskByMarketIdStartYear(String marketId, CrawlConf cconf, String propfile, 
			String confName) {
		String[] ids = getStockIdByMarketId(marketId, cconf);
		Map<Integer, List<Task>> taskByYear = new HashMap<Integer, List<Task>>();
		String confFileName = confName + ".xml";
		List<Task> ttList = new ArrayList<Task>();
		if (confFileName!=null){
			ttList = cconf.setUpSite(confFileName, null);
		}
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			int[] yq = getStartYearQuarterByStockId(stockid, cconf);
			int[] cyq = DateTimeUtil.getYearQuarter(new Date());
			if (yq!=null){
				int year;
				for (year=yq[0]; year<=cyq[0]; year++){
					for (Task t:ttList){
						Task t1 = t.clone(ETLUtil.class.getClassLoader());
						t1.putParam("stockid", stockid);
						t1.putParam("year", year);
						List<Task> tl = taskByYear.get(year);
						if (tl==null){
							tl = new ArrayList<Task>();
						}
						tl.add(t1);
						taskByYear.put(year, tl);
					}
				}
			}
		}
		
		for (int year:taskByYear.keySet()){
			List<Task> tlist = taskByYear.get(year);
			String calledMethod = ReflectUtil.getMethodName(1);
			Map<String, Object> taskParams = new HashMap<String, Object>();
			taskParams.put(MarketId_Key, marketId);
			taskParams.put(StartYear_Key, year);
			String taskName = ETLUtil.getTaskName(calledMethod, taskParams);
			logger.info(String.format("sending out:%d tasks for hadoop task %s.", tlist.size(), taskName));
			CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, taskName);
		}
	}
	
	public static void mergeMarketHistoryByQuarter(CrawlConf cconf, int fromYear, 
			int fromQuarter, int toYear, int toQuarter) throws Exception {
		int year;
		int quarter;
		Configuration conf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
		FileSystem fs = FileSystem.get(conf);
		for (year=toYear; year>=fromYear; year--){
			int startQ=1;
			int endQ=4;
			if (year==fromYear){//from Year
				startQ = fromQuarter;
			}else if (year==toYear){//to Year
				endQ=toQuarter;
			}
			
			for (quarter=startQ; quarter<=endQ; quarter++){
				String itemFolder = cconf.getTaskMgr().getHadoopCrawledItemFolder()+"/";
				Path ppat = new Path(itemFolder + StockConfig.SINA_STOCK_MARKET_HISTORY+"/"+"*_phtml_year_"+year+"_jidu_"+quarter);
				FileStatus[] files = fs.globStatus(ppat);
				if (files.length>1){
					Path[] paths = new Path[files.length];
					for (int i=0; i<files.length; i++){
						paths[i]=files[i].getPath();
					}
					Path tmp = new Path(itemFolder + StockConfig.SINA_STOCK_MARKET_HISTORY +"-tmp/"+year+"-"+quarter);
					if (fs.exists(tmp)){
						fs.delete(tmp, true);
					}
					fs.mkdirs(tmp);
					FileUtil.copy(fs, paths, fs, tmp, false, true, conf);
					Path output = new Path(itemFolder + StockConfig.SINA_STOCK_MARKET_HISTORY+"-output/"+year+"-"+quarter);
					if (fs.exists(output)){
						fs.delete(output, true);
					}
					FileUtil.copyMerge(fs, tmp, fs, output, false, conf, "");
				}
			}
		}
	}
}
