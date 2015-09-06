package org.cld.stock;

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
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.CompareUtil;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.TestBase;

public abstract class StockBase extends TestBase{
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static final String KEY_IPODate_MAP="data";
	public static final String AllCmdRun_STATUS="AllCmdRun";
	public static final String KEY_IDS = "ids";
	public static final String idKeySep = "_";
	public static final String KEY_IPODates = "ipoDates";
	
	protected String marketId;
	protected String propFile;
	protected Date endDate;
	protected Date startDate;
	protected String specialParam = null;//for special cmd to use as parameter
   
	protected DataStoreManager dsm = null;
	protected DataStoreManager hdfsDsm = null;
	protected JobClient jobClient = null;

	public abstract StockConfig getStockConfig();
	
	public DataStoreManager getDsm(){
		return dsm;
	}
	
	public StockBase(String propFile, String marketId, Date sd, Date ed){
		super();
		this.marketId = marketId;
		super.setProp(propFile);
		this.propFile = propFile;
		this.startDate = sd;
		this.endDate = ed;
		this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
		dsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hbase);
		hdfsDsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hdfs);
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
	
	/**
	 * 
	 * @param stockIdCrawlConfig: SinaStockConfig.SINA_STOCK_IDS, NasdaqStockConfig.STOCK_IDS
	 * @param marketId: 
	 * @param endDate
	 * @return
	 * @throws InterruptedException
	 */
	public CrawledItem run_browse_idlist(String marketId, Date endDate) throws InterruptedException{
		StockConfig sc = getStockConfig();
		if (sc.getTestMarketId().equals(marketId)){
			CrawledItem ci = null;
			Date marketChangeDate = null;
			try {
				marketChangeDate = sdf.parse(sc.getTestMarketChangeDate());
			}catch(Exception e){
				logger.error("", e);
				return null;
			}
			if (endDate.before(marketChangeDate)){
				ci = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
						new CrawledItemId(marketId, sc.getStockIdsCmd(), endDate));
				ci.addParam(KEY_IDS, Arrays.asList(sc.getTestStockSet1()));
			}else{
				ci = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
						new CrawledItemId(marketId, sc.getStockIdsCmd(), endDate));
				ci.addParam(KEY_IDS, Arrays.asList(sc.getTestStockSet2()));
			}
			return ci;
		}
		
		cconf.setUpSite(sc.getStockIdsCmd() + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("marketId", marketId);
		CrawledItem ciOrg = browsePrd(sc.getStockIdsCmd() + ".xml", null, params, endDate, false).get(0);
		//set ipoCache if no seperate ipoDate crawl
		if (sc.getIPODateCmd()==null){
			List<String> ids = (List<String>) ciOrg.getParam(StockBase.KEY_IDS);
			List<String> ipoDates = (List<String>) ciOrg.getParam(StockBase.KEY_IPODates);
			Map<String, String> ipoMap = new HashMap<String, String>();
			for (int i=0; i<ids.size(); i++){
				ipoMap.put(ids.get(i), ipoDates.get(i));
			}
			ciOrg.addParam(KEY_IPODate_MAP, ipoMap);
		}
		logger.info("ci we got:" + ciOrg);
		if (sc.getPairedMarket()!=null && sc.getPairedMarket().containsKey(marketId)){
			String pairMarketId = sc.getPairedMarket().get(marketId);
			//we need to add the st market as well
			params.put("marketId", pairMarketId);
			CrawledItem ciAdd = browsePrd(sc.getStockIdsCmd() + ".xml", null, params, endDate, false).get(0);
			List<String> ids = (List<String>) ciAdd.getParam(KEY_IDS);
			List<String> idsOrg = (List<String>) ciOrg.getParam(KEY_IDS);
			idsOrg.addAll(ids);
			ciOrg.addParam(KEY_IDS, idsOrg);
		}
		return ciOrg;
	}
	
	//cmdName is the fileName of the site-conf without suffix, is the storeid
	public void runCmd(String cmdName, String marketId, String startDate, String endDate) {
		StockConfig sc = getStockConfig();
		String strSD = sc.getStartDate(cmdName);
		if (strSD!=null){
			startDate = strSD;
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
			String[] jobIds = ETLUtil.runTaskByCmd(sc, marketId, cconf, this.getPropFile(), cmdName, params);
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
		StockConfig sc = getStockConfig();
		String strSd = null;
		if (sd!=null){
			strSd = sdf.format(sd);
		}
		String strEd = null;
		if (ed!=null){
			strEd = sdf.format(ed);
		}
		String[] confs = sc.getAllCmds(marketId);
		Date testStartDate = null;
		try{
			testStartDate = sdf.parse(sc.getTestShortStartDate());
		}catch(Exception e){
			logger.error("", e);
		}
		for (String cmd:confs){
			boolean isStatic = ETLUtil.isStatic(cconf, cmd);
			if (
					(type == CMDTYPE_STATIC && isStatic) ||
					(type == CMDTYPE_DYNAMIC && !isStatic) ||
					(type == CMDTYPE_ALL)){
				if (marketId.startsWith(sc.getTestMarketId()) && Arrays.asList(sc.getSlowCmds()).contains(cmd) 
						&& (sd!=null && testStartDate.after(sd))){
					runCmd(cmd, marketId, sc.getTestShortStartDate(), strEd);//tradeConf generates too much data, limit it to short_period for test market
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
		StockConfig sc = getStockConfig();
		Date lastRunDate = null;
		List<String> curIds = null; 
		List<String> deltaIds = null;
		String strEndDate=sdf.format(endDate);
		
		//get the market-ids-crawl-history
		CmdStatus mcs = CmdStatus.getCmdStatus(dsm, marketId, sc.getStockIdsCmd());
		//update the marketId by appending the endDate
		String curMarketId = marketId + idKeySep+ strEndDate; 
		//run browse id to see any updates
		CrawledItem ciIds = null;
		
		if (mcs!=null){
			String prevMarketId = marketId + idKeySep + sdf.format(mcs.getId().getCreateTime());
			if (mcs.getId().getCreateTime().equals(endDate)){//crawl again at the same day, no need to crawl market
				ciIds = dsm.getCrawledItem(prevMarketId, sc.getStockIdsCmd(), CrawledItem.class);
				curIds = (List<String>) ciIds.getParam(KEY_IDS);
			}else{
				//crawl the ids again
				ciIds = run_browse_idlist(marketId, endDate);
				ciIds.getId().setId(curMarketId);
				curIds = (List<String>) ciIds.getParam(KEY_IDS);
			}
			//get latest market-ids-crawl
			CrawledItem ciPreIds = dsm.getCrawledItem(prevMarketId, sc.getStockIdsCmd(), CrawledItem.class);
			List<String> preIds = (List<String>) ciPreIds.getParam(KEY_IDS);
			//get the delta
			deltaIds = new ArrayList<String>();
			deltaIds.addAll(curIds);
			deltaIds.removeAll(preIds);
			
			//get last all cmd run status, last run date
			CmdStatus preAllCmdRunCS = CmdStatus.getCmdStatus(dsm, prevMarketId, AllCmdRun_STATUS);
			if (preAllCmdRunCS==null){//last time AllCmdRunCS failed to generate
				lastRunDate = endDate;
			}else{
				lastRunDate = preAllCmdRunCS.getId().getCreateTime();
			}
			logger.info(String.format("market %s has org size %d.", marketId, preIds.size()));
			if (deltaIds.size()>0){
				logger.info(String.format("market %s has delta size %d.", marketId, deltaIds.size()));
				//has new delta market, let's create another 2 markets
				dsm.addUpdateCrawledItem(ciIds, null);////hbase persistence for curMarket
				hdfsDsm.addUpdateCrawledItem(ciIds, null);//hdfs persistence for cur market
				String deltaMarketId = marketId + idKeySep + strEndDate + "_delta"; //delta market
				CrawledItem ciDelta = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
						new CrawledItemId(deltaMarketId, sc.getStockIdsCmd(), endDate));
				ciDelta.addParam(KEY_IDS, deltaIds);
				dsm.addUpdateCrawledItem(ciDelta, null);
				
				//update ipodate (lastRunDate,endDate] for curMarket
				String ipoCmd = sc.getIPODateCmd();
				if (ipoCmd!=null){
					runCmd(ipoCmd, curMarketId, getDateString(lastRunDate), getDateString(endDate));
					CrawledItem prevIPODate = dsm.getCrawledItem(prevMarketId, ipoCmd, CrawledItem.class);
					CrawledItem curIPODate = dsm.getCrawledItem(curMarketId, ipoCmd, CrawledItem.class);
					Map<String, String> preMap = (Map<String, String>) prevIPODate.getParam(KEY_IPODate_MAP);
					Map<String, String> curMap = (Map<String, String>) curIPODate.getParam(KEY_IPODate_MAP);
					if (curMap==null){
						curMap = new HashMap<String,String>();
					}
					curMap.putAll(preMap);
					curIPODate.addParam(KEY_IPODate_MAP, curMap);
					dsm.addUpdateCrawledItem(curIPODate, null);
				}
				
				//update the market-command-status
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
			//store the stock-ids for curMarket
			ciIds = run_browse_idlist(marketId, endDate);
			ciIds.getId().setId(curMarketId);
			curIds = (List<String>) ciIds.getParam(KEY_IDS);
			logger.info(String.format("market %s 1st time fetch with size %d.", marketId, curIds.size()));
			dsm.addUpdateCrawledItem(ciIds, null);//hbase persistence for curMarket
			hdfsDsm.addUpdateCrawledItem(ciIds, null);//hdfs persistence for cur market
			//store ipodate
			String ipoCmd = sc.getIPODateCmd();
			if (ipoCmd!=null){
				runCmd(ipoCmd, curMarketId, getDateString(startDate), getDateString(endDate));
			}
			//store the market-id-crawl status
			mcs = new CmdStatus(marketId, sc.getStockIdsCmd(), endDate, startDate, true);
			dsm.addUpdateCrawledItem(mcs, null);
			//do the (null, enDate) for the current market
			runAllCmd(curMarketId, startDate, endDate, CMDTYPE_ALL);
		}
		CmdStatus allCmdRunStatus = new CmdStatus(curMarketId, AllCmdRun_STATUS, endDate, startDate, true);
		dsm.addUpdateCrawledItem(allCmdRunStatus, null);
	}

	//update CmdStatus for async commands
	public void updateCmdStatus(String marketId){
		StockConfig sc = getStockConfig();
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
		for (String cmd: sc.getAllCmds(marketId)){
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
