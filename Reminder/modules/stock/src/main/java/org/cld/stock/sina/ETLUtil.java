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

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
	
	/**
	 * 
	 * @param ids
	 * @param cconf
	 * @param propfile
	 * @param confName
	 * @param minStartDate: if specified, min start date
	 */
	public static void getDataFromStartDate(String[] ids, CrawlConf cconf, String propfile, String confName, 
			Date minStartDate){
		List<Task> tlist = new ArrayList<Task>();
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
					logger.debug("try date:" + d);
					String dstr = sdf.format(d);
					TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, confName, confName +".xml");
					ttc.putParam("stockid", id);
					ttc.putParam("date", dstr);
					tlist.add(ttc);
				}
			}
		}
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, null, null);
	}
	
	public static void getDataFromStartYearQuarter(String[] ids, CrawlConf cconf, String propfile, String confName) {
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
						//logger.info("add ttc with param:" + stockid + "," + year + "," + quarter);
					}
				}
			}
		}
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, null, null);
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
	
	public static void getDataFromStartYear(String[] ids, CrawlConf cconf, String propfile, 
			String confName, String outputDirPrefix) {
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
		if (outputDirPrefix!=null){
			for (int year:taskByYear.keySet()){
				List<Task> tlist = taskByYear.get(year);
				logger.info("sending out:" + tlist.size() + " tasks.");
				CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist, 
						null, outputDirPrefix + "/" + year);
			}
		}else{
			List<Task> alllist = new ArrayList<Task>();
			for (int year:taskByYear.keySet()){
				List<Task> tlist = taskByYear.get(year);
				alllist.addAll(tlist);
			}
			logger.info("sending out:" + alllist.size() + " tasks.");
			CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, alllist, 
					null, null);
		}
	}
}
