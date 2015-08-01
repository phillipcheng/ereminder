package org.cld.stock.sina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapreduce.JobStatus;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.etl.csv.CsvReformatMapredLauncher;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.test.TestBase;


import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.StockConfig;

public class SinaStockBase extends TestBase{
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	
	public static final int HS_A_START_YEAR=1990;
	public static String HS_A_FIRST_DATE_DETAIL_TRADE= "2004-10-1";
	public static String HS_A_FIRST_DATE_RZRQ= "2012-11-12";
	public static String HS_A_FIRST_DATE_DZJY= "2003-01-08";
	
	private String marketId = ETLUtil.MarketId_Test;
	private String propFile = "client1-v2.properties";
	private String itemsFolder;
    
	/*
	RUNNING(1),
    SUCCEEDED(2),
    FAILED(3),
    PREP(4),
    KILLED(5);
	*/
	
	private DataStoreManager dsm = null;
	private JobClient jobClient = null;
	
	public SinaStockBase(String propFile, String marketId){
		super();
		this.marketId = marketId;
		super.setProp(propFile);
		this.propFile = propFile;
		this.itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
		String statusId = marketId + "_status";
		dsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hbase);
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public CrawlConf getCconf(){
		return this.cconf;
	}
	
	public Map<String, Object> getDateParamMap(String startDate, String endDate){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (startDate!=null){
			paramMap.put(ETLUtil.PK_START_DATE, startDate);
		}
		if (endDate!=null){
			paramMap.put(ETLUtil.PK_END_DATE, endDate);
		}
		return paramMap;
	}
	/***
	 * Stock ids
	 * */
	//get all stockids by market
	public void run_browse_idlist(String marketId) throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("marketId", marketId);
		browsePrd(StockConfig.SINA_STOCK_IDS + ".xml", null, params);
	}
	
	public void run_task(String taskFileName){
		CrawlUtil.hadoopExecuteCrawlTasksByFile(this.propFile, cconf, taskFileName);
	}
	
	//cmdName is the fileName of the site-conf without suffix, is the storeid
	public void run_cmd(String cmdName, String marketId, String startDate, String endDate) {
		if (cmdName.contains("rzrq")){
			if (startDate==null){
				startDate = HS_A_FIRST_DATE_DZJY;
			}
		}else if (cmdName.contains("dzjy")){
			if (startDate == null){
				startDate = HS_A_FIRST_DATE_DZJY;
			}
		}else if (cmdName.contains("tradedetail")){
			if (startDate == null){
				startDate = HS_A_FIRST_DATE_DETAIL_TRADE;
			}
		}
		Map<String, Object> params = getDateParamMap(startDate, endDate);
		String[] jobIds = ETLUtil.runTaskByCmd(marketId, cconf, this.getPropFile(), cmdName, params);
		
	}
	
	//
	public void initFetch(String ed){
		
	}
	//
	public void updateFetch(String sd, String ed){
		
	}
	//
	public void updateTaskStatus(){
		/*
		for (String confName:taskStatus.keySet()){
			Map<String, Map<String, Object>> allJobStatus = taskStatus.get(confName);
			for (Map<String, Object> mjts: allJobStatus.values()){
				Map<String, Integer> jobStatusMap = (Map<String, Integer>) mjts.get(KEY_JSM);
				if (jobStatusMap!=null){
					for (String jid: jobStatusMap.keySet()){
						JobID jobId = JobID.forName(jid);
						try{
							RunningJob rjob = jobClient.getJob(jobId);
							if (rjob!=null){
								jobStatusMap.put(jid, rjob.getJobStatus().getState().getValue());
							}else{
								logger.info(String.format("job %s not found in jobClient.", jid));
								//try history server
							}
						}catch(Exception e){
							logger.error("", e);
						}
					}
				}
			}
		}
		dsm.addUpdateCrawledItem(crawlStatus, null);
		*/
	}
	
	
	/****
	 * 财务数据
	 * @throws ParseException 
	 */
	//利润表
	//资产负债表
	//现金流量表
	//crawl financial report history by market to hdfs
	public void run_browse_fr_history() throws ParseException{//till running time
		//set output to null, needs follow up etl
		ETLUtil.runTaskByCmd(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_FR_HISTORY, null);
	}
	//fr history convert to csv/hive
	public void run_convert_fr_history_tabular_to_csv() throws Exception {
		String[] ids = ETLUtil.getStockIdByMarketId(marketId, cconf);
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			for (String subFR: StockConfig.subFR){
				TabularCSVConvertTask ct = new TabularCSVConvertTask(stockid, 
						StockConfig.SINA_STOCK_FR_HISTORY + "/" + subFR, 
						StockConfig.SINA_STOCK_FR_HISTORY_OUT+ "/" + subFR);
				tlist.add(ct);
			}
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, null);
	}
	//fr history reformat from split by stockid to split by quarter
	public void run_fr_reformat() throws Exception{
		for (String subFR: StockConfig.subFR){
			CsvReformatMapredLauncher.format(this.getPropFile(), 
					itemsFolder + "/" + StockConfig.SINA_STOCK_FR_HISTORY_OUT + "/" + subFR, 
					1, 
					itemsFolder  + "/" + StockConfig.SINA_STOCK_FR_HISTORY_QUARTER_OUT + "/" + subFR);
		}
	}
}
