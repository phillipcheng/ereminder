package org.cld.stock.sina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.RunningJob;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.taskmgr.entity.CmdStatus;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.CompareUtil;
import org.cld.etl.csv.CsvReformatMapredLauncher;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.CrawlTaskConf;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.test.TestBase;
import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.StockConfig;
import org.xml.mytaskdef.ParsedBrowsePrd;

public class SinaStockBase extends TestBase{
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	public static final String MarketId_HS_A_ST="shfxjs"; //上证所风险警示板
	
	public static final String HS_A_START_DATE="1989-01-01";
	public static Date date_HS_A_START_DATE=null;
	public static String HS_A_FIRST_DATE_DETAIL_TRADE= "2004-10-01";
	public static String HS_A_FIRST_DATE_RZRQ= "2012-11-12";
	public static String HS_A_FIRST_DATE_DZJY= "2003-01-08";
	static{
		try{
			date_HS_A_START_DATE = sdf.parse(HS_A_START_DATE);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	//for test market
	public static final String MarketId_Test = "test";
	public static final String Test_SD = "2014-01-10";
	public static final String Test_SHORT_SD = "2015-05-01"; //this should before all Test_Dx, since this is used as the start date, and Test_Dx is used as end date.
	public static final String Test_D1 = "2015-05-10";
	public static final String Test_D2 = "2015-05-20";//only increase date
	public static final String Test_D3 = "2015-06-10";//also increase stock
	public static final String Test_D4 = "2015-07-01";//only increase date
	public static Date date_Test_SD = null;
	public static Date date_Test_D1 = null;
	public static Date date_Test_D2 = null;
	public static Date date_Test_D3 = null;
	public static Date date_Test_D4 = null;
	static{
		try{
			date_Test_SD = sdf.parse(Test_SD);
			date_Test_D1 = sdf.parse(Test_D1);
			date_Test_D2 = sdf.parse(Test_D2);
			date_Test_D3 = sdf.parse(Test_D3);
			date_Test_D4 = sdf.parse(Test_D4);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	public static final String[] Test_D1_Stocks = new String[]{"sh600000", "sh601766"};
	public static final String[] Test_D3_Stocks = new String[]{"sh600000", "sh601766", "sz000001"};
	
	public static String KEY_IDS = "ids";
	public static String idKeySep = "_";
	
	private String marketId = MarketId_Test;
	private String propFile = "client1-v2.properties";
	private Date startDate;
	private Date endDate;
	private String itemsFolder;
	private String specialParam = null;//for special cmd to use as parameter
   
	private DataStoreManager dsm = null;
	private JobClient jobClient = null;

	
	public DataStoreManager getDsm(){
		return dsm;
	}
	
	public SinaStockBase(String propFile, String marketId, Date sd, Date ed){
		super();
		this.marketId = marketId;
		super.setProp(propFile);
		this.propFile = propFile;
		this.startDate = sd;
		this.endDate = ed;
		this.itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
		dsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hbase);
		
	}
	
	public CrawlConf getCconf(){
		return this.cconf;
	}
	
	public Map<String, Object> getDateParamMap(String startDate, String endDate){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(AbstractCrawlItemToCSV.FIELD_NAME_STARTDATE, startDate);
		paramMap.put(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE, endDate);
		return paramMap;
	}
	
	public void run_task(String[] taskFileName, Map<String, String> hadoopParams){
		CrawlUtil.hadoopExecuteCrawlTasksByFile(this.propFile, cconf, taskFileName, hadoopParams);
	}
	
	//get #hs_a + #, not added to db yet
	public CrawledItem run_browse_idlist(String marketId, Date endDate) throws InterruptedException{
		if (MarketId_Test.equals(marketId)){
			String d = sdf.format(endDate);
			CrawledItem ci = null;
			Date marketChangeDate = null;
			try {
				marketChangeDate = sdf.parse(Test_D3);
			}catch(Exception e){
				logger.error("", e);
				return null;
			}
			if (endDate.before(marketChangeDate)){
				ci = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
						new CrawledItemId(MarketId_Test, StockConfig.SINA_STOCK_IDS, endDate));
				ci.addParam(KEY_IDS, Arrays.asList(Test_D1_Stocks));
			}else{
				ci = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
						new CrawledItemId(MarketId_Test, StockConfig.SINA_STOCK_IDS, endDate));
				ci.addParam(KEY_IDS, Arrays.asList(Test_D3_Stocks));
			}
			return ci;
		}
		
		cconf.setUpSite(StockConfig.SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("marketId", marketId);
		CrawledItem ciOrg = browsePrd(StockConfig.SINA_STOCK_IDS + ".xml", null, params, endDate, false).get(0);
		
		if (MarketId_HS_A.equals(marketId)){
			//we need to add the st market as well
			params.put("marketId", MarketId_HS_A_ST);
			CrawledItem ciAdd = browsePrd(StockConfig.SINA_STOCK_IDS+".xml", null, params, endDate, false).get(0);
			List<String> ids = (List<String>) ciAdd.getParam(KEY_IDS);
			List<String> idsOrg = (List<String>) ciOrg.getParam(KEY_IDS);
			idsOrg.addAll(ids);
			ciOrg.addParam(KEY_IDS, idsOrg);
		}
		return ciOrg;
	}
	
	//cmdName is the fileName of the site-conf without suffix, is the storeid
	public void runCmd(String cmdName, String marketId, String startDate, String endDate) {
		if (cmdName.contains("rzrq")){
			if (startDate==null){
				startDate = HS_A_FIRST_DATE_RZRQ;
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
		Date ed = null;
		Date sd = null;
		try{
			if (endDate!=null){
				ed = sdf.parse(endDate);
			}
			if (startDate!=null){
				sd = sdf.parse(startDate);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		CmdStatus cs= null;
		boolean isStatic = ETLUtil.isStatic(cconf, cmdName);
		if (isStatic){
			cs = CmdStatus.getCmdStatus(dsm, marketId, cmdName);
		}else{
			cs = CmdStatus.getCmdStatus(dsm, marketId, cmdName, ed);
		}
		boolean needRun = true;
		if (cs != null){//no such command run before
			if (isStatic){
				needRun = false;
			}else if (!CompareUtil.ObjectDiffers(ed, cs.getId().getCreateTime())){//compare only end time
				needRun = false;
			}
		}
		if (needRun){
			logger.info(String.format("going to run cmd %s with ed:%s, sd:%s, marketId:%s", cmdName, endDate, startDate, marketId));
			if (isStatic){
				cs = new CmdStatus(marketId, cmdName, ed, sd, true);
			}else{	
				cs = new CmdStatus(marketId, cmdName, ed, sd, false);
			}
			String[] jobIds = ETLUtil.runTaskByCmd(marketId, cconf, this.getPropFile(), cmdName, params);
			for (String jobId:jobIds){
				cs.getJsMap().put(jobId, 4);//PREPARE
			}
			dsm.addUpdateCrawledItem(cs, null);
		}
	}
	
	public static int CMDTYPE_STATIC=1; //not updated with time, static data
	public static int CMDTYPE_DYNAMIC=2; //updated with time
	public static int CMDTYPE_ALL=3;
	
	private void runAllCmd(String marketId, Date sd, Date ed, int type){
		String strSd = null;
		if (sd!=null){
			strSd = sdf.format(sd);
		}
		String strEd = null;
		if (ed!=null){
			strEd = sdf.format(ed);
		}
		String[] confs = null;
		if (marketId.startsWith(MarketId_Test)){
			confs = StockConfig.testAllConf;
		}else{
			confs = StockConfig.allConf;
		}
		Date testStartDate = null;
		try{
			testStartDate = sdf.parse(Test_SHORT_SD);
		}catch(Exception e){
			logger.error("", e);
		}
		for (String cmd:confs){
			boolean isStatic = ETLUtil.isStatic(cconf, cmd);
			if (
					(type == CMDTYPE_STATIC && isStatic) ||
					(type == CMDTYPE_DYNAMIC && !isStatic) ||
					(type == CMDTYPE_ALL)){
				if (marketId.startsWith(MarketId_Test) && Arrays.asList(StockConfig.tradeConfs).contains(cmd) && (sd!=null && testStartDate.after(sd))){
					runCmd(cmd, marketId, Test_SHORT_SD, strEd);//tradeConf generates too much data, limit it to short_period for test market
				}else{
					runCmd(cmd, marketId, strSd, strEd);
				}
			}
		}
	}
	
	private String getDateString(Date date){
		String str=null;
		if (date!=null){
			str = sdf.format(date);
		}
		return str;
	}
	
	public void runAllCmd(Date startDate, Date endDate) throws InterruptedException {
		Date lastRunDate = null;
		List<String> curIds = null; 
		List<String> deltaIds = null;
		String strEndDate=sdf.format(endDate);
		
		//get the market-ids-crawl-history
		CmdStatus mcs = CmdStatus.getCmdStatus(dsm, marketId, StockConfig.SINA_STOCK_IDS);
		//update the marketId by appending the endDate
		String curMarketId = marketId + idKeySep+ strEndDate; 
		//run browse id to see any updates
		CrawledItem ciIds = null;
		
		if (mcs!=null){
			String prevMarketId = marketId + idKeySep + sdf.format(mcs.getId().getCreateTime());
			if (mcs.getId().getCreateTime().equals(endDate)){//crawl again at the same day, no need to crawl market
				ciIds = dsm.getCrawledItem(prevMarketId, StockConfig.SINA_STOCK_IDS, CrawledItem.class);
				curIds = (List<String>) ciIds.getParam(KEY_IDS);
			}else{
				//crawl the ids again
				ciIds = run_browse_idlist(marketId, endDate);
				ciIds.getId().setId(curMarketId);
				curIds = (List<String>) ciIds.getParam(KEY_IDS);
			}
			//get latest market-ids-crawl
			CrawledItem ciPreIds = dsm.getCrawledItem(prevMarketId, StockConfig.SINA_STOCK_IDS, CrawledItem.class);
			List<String> preIds = (List<String>) ciPreIds.getParam(KEY_IDS);
			//get the delta
			deltaIds = new ArrayList<String>();
			deltaIds.addAll(curIds);
			deltaIds.removeAll(preIds);
			
			//get last all cmd run status, last run date
			CmdStatus preAllCmdRunCS = CmdStatus.getCmdStatus(dsm, prevMarketId, StockConfig.AllCmdRun_STATUS);
			if (preAllCmdRunCS==null){//last time AllCmdRunCS failed to generate
				lastRunDate = endDate;
			}else{
				lastRunDate = preAllCmdRunCS.getId().getCreateTime();
			}
			logger.info(String.format("market %s has org size %d.", marketId, preIds.size()));
			if (deltaIds.size()>0){
				logger.info(String.format("market %s has delta size %d.", marketId, deltaIds.size()));
				//has new delta market, let's create another 2 markets
				dsm.addUpdateCrawledItem(ciIds, null);//cur market
				String deltaMarketId = marketId + idKeySep + strEndDate + "_delta"; //delta market
				CrawledItem ciDelta = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
						new CrawledItemId(deltaMarketId, StockConfig.SINA_STOCK_IDS, endDate));
				ciDelta.addParam(KEY_IDS, deltaIds);
				dsm.addUpdateCrawledItem(ciDelta, null);
				
				//update ipodate (lastRunDate,endDate] for curMarket
				runCmd(StockConfig.SINA_STOCK_IPODate, curMarketId, getDateString(lastRunDate), getDateString(endDate));
				CrawledItem prevIPODate = dsm.getCrawledItem(prevMarketId, StockConfig.SINA_STOCK_IPODate, CrawledItem.class);
				CrawledItem curIPODate = dsm.getCrawledItem(curMarketId, StockConfig.SINA_STOCK_IPODate, CrawledItem.class);
				Map<String, String> preMap = (Map<String, String>) prevIPODate.getParam(StockConfig.SINA_STOCK_DATA);
				Map<String, String> curMap = (Map<String, String>) curIPODate.getParam(StockConfig.SINA_STOCK_DATA);
				if (curMap==null){
					curMap = new HashMap<String,String>();
				}
				curMap.putAll(preMap);
				curIPODate.addParam(StockConfig.SINA_STOCK_DATA, curMap);
				dsm.addUpdateCrawledItem(curIPODate, null);
				
				//update the mcs
				mcs.getId().setCreateTime(endDate);
				dsm.addUpdateCrawledItem(mcs, null);
				
				//check the (null, lastRunDate) for the previous market
				runAllCmd(prevMarketId, startDate, lastRunDate, CMDTYPE_ALL);
				//apply (null, endDate) for delta market
				runAllCmd(deltaMarketId, lastRunDate, endDate, CMDTYPE_STATIC);
				//do the [lastRunDate,endDate) for the current market
				runAllCmd(curMarketId, lastRunDate, endDate, CMDTYPE_DYNAMIC); //
			}else{
				//no new delta market, so the ids is not updated
				//check the (null, lastRunDate) for the pre market
				runAllCmd(prevMarketId, startDate, lastRunDate, CMDTYPE_ALL);
				//do the [lastRunDate,endDate) for the pre market
				if (CompareUtil.ObjectDiffers(lastRunDate, endDate)){
					runAllCmd(prevMarketId, lastRunDate, endDate, CMDTYPE_DYNAMIC); //dynamic part
				}
			}
		}else{
			//store the market-id-crawl for curMarket
			ciIds = run_browse_idlist(marketId, endDate);
			ciIds.getId().setId(curMarketId);
			curIds = (List<String>) ciIds.getParam(KEY_IDS);
			logger.info(String.format("market %s 1st time fetch with size %d.", marketId, curIds.size()));
			dsm.addUpdateCrawledItem(ciIds, null);
			//store ipodate
			runCmd(StockConfig.SINA_STOCK_IPODate, curMarketId, getDateString(startDate), getDateString(endDate));
			//store the market-id-crawl status
			mcs = new CmdStatus(marketId, StockConfig.SINA_STOCK_IDS, endDate, startDate, true);
			dsm.addUpdateCrawledItem(mcs, null);
			//do the (null, enDate) for the current market
			runAllCmd(curMarketId, startDate, endDate, CMDTYPE_ALL);
		}
		CmdStatus allCmdRunStatus = new CmdStatus(curMarketId, StockConfig.AllCmdRun_STATUS, endDate, startDate, true);
		dsm.addUpdateCrawledItem(allCmdRunStatus, null);
	}

	//update CmdStatus for async commands
	public void updateCmdStatus(){
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
		for (String cmd: StockConfig.allConf){
			boolean update=false;
			CmdStatus cs = null;
			boolean isStatic = ETLUtil.isStatic(cconf, cmd);
			if (isStatic){
				cs = CmdStatus.getCmdStatus(dsm, marketId, cmd);
			}else{
				cs = CmdStatus.getCmdStatus(dsm, marketId, cmd, this.endDate);
			}
			Map<String, Integer> jobStatusMap = cs.getJsMap();
			if (jobStatusMap!=null){
				for (String jid: jobStatusMap.keySet()){
					JobID jobId = JobID.forName(jid);
					try{
						RunningJob rjob = jobClient.getJob(jobId);
						int orgStatus = jobStatusMap.get(jid);
						int newStatus = orgStatus;
						if (rjob!=null){
							newStatus = rjob.getJobStatus().getState().getValue();
						}else{
							logger.info(String.format("job %s not found in jobClient.", jid));
							//try history server ?
						}
						if (newStatus!=orgStatus){
							jobStatusMap.put(jid, newStatus);
							update = true;
						}
						logger.info(String.format("job %s got status %d", jid, newStatus));
					}catch(Exception e){
						logger.error("", e);
					}
				}
			}
			if (update){
				dsm.addUpdateCrawledItem(cs, null);
			}
		}
	}

	public void run_tradedetail_checkdownload(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = SinaStockBase.HS_A_FIRST_DATE_DETAIL_TRADE + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		TradeDetailCheckDownload.launch(cconf, datePart);
	}
	
	public void run_tradedetail_postprocess(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = SinaStockBase.HS_A_FIRST_DATE_DETAIL_TRADE + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		TradeDetailPostProcessTask.launch(this.propFile, cconf, datePart);
	}
	
	public void run_merge(){
		String datePart;
		String strEndDate = sdf.format(this.endDate);
		if (this.startDate==null){
			datePart = null + "_" + strEndDate;
		}else{
			datePart = sdf.format(this.startDate) + "_" + strEndDate;
		}
		MergeTask.launch(this.propFile, cconf, datePart, specialParam, true);
	}
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
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, null, false);
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
	
	//getter, setter
	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	public String getSpecialParam() {
		return specialParam;
	}

	public void setSpecialParam(String specialParam) {
		this.specialParam = specialParam;
	}
}
