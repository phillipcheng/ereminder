package org.cld.stock.sina;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil.browse_type;
import org.cld.datastore.entity.CrawledItem;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.cld.util.DateTimeUtil;
import org.json.JSONArray;

public class ETLUtil {
	
	private static Logger logger =  LogManager.getLogger(ETLUtil.class);
	
	public static void getMarketHistory(CrawlConf cconf, String propfile, String marketId) 
			throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i=0; i<jsarry.length(); i++){
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			//get the IPODate
			CrawledItem corpInfo = cconf.getDsm("hbase").getCrawledItem(stockid, StockConfig.SINA_STOCK_CORP_INFO, null);
			JSONArray jsa = (JSONArray)corpInfo.getParam(CorpInfoToCSV.FIELD_NAME_ATTR);
			String ipoDateStr = jsa.getString(StockConfig.IPO_DATE_IDX).trim();
			String foundDateStr = jsa.getString(StockConfig.FOUND_DATE_IDX).trim();
			String dateUsed = ipoDateStr;
			int[] yq = DateTimeUtil.getYearQuarter(ipoDateStr, sdf);
			if (yq==null){
				logger.warn("wrong ipo date found:" + ipoDateStr + ", for stock:" + stockid + ", try found date:" + foundDateStr);
				yq = DateTimeUtil.getYearQuarter(foundDateStr, sdf);
				dateUsed = foundDateStr;
			}
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
								StockConfig.SINA_STOCK_MARKET_HISTORY, StockConfig.SINA_STOCK_MARKET_HISTORY+".xml");
						ttc.putParam("stockid", stockid);
						ttc.putParam("year", year+"");
						ttc.putParam("quarter", quarter+"");
						tlist.add(ttc);
						//logger.info("add ttc with param:" + stockid + "," + year + "," + quarter);
					}
				}
			}else{
				logger.error("stock:" + stockid + ", with wrong date:" + dateUsed);
			}
		}
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tlist);
	}
	
	public static void mergeMarketHistoryByQuarter(CrawlConf cconf, int fromYear, 
			int fromQuarter, int toYear, int toQuarter) throws Exception {
		int year;
		int quarter;
		Configuration conf = HadoopTaskUtil.getHadoopConf(cconf.getNodeConf());
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
