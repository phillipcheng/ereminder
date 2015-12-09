package org.cld.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.impl.HdfsDataStoreManagerImpl;
import org.cld.taskmgr.entity.CmdStatus;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DateTimeUtil;
import org.cld.util.ListUtil;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.ScriptRunner;
import org.cld.util.jdbc.SqlUtil;
import org.xml.taskdef.BrowseDetailType;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.hadooputil.TransferHdfsFile;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.test.TestBase;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.task.LoadDBDataTask;
import org.cld.stock.task.MergeTask;

public abstract class StockBase extends TestBase{
	protected static Logger logger =  LogManager.getLogger(StockBase.class);
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String AllCmdRun_STATUS="AllCmdRun";
	public static final String KEY_IDS = "ids";
	public static final String idKeySep = "_";
	public static final String DELTA_NAME="delta";
	public static final char EXCLUDE_MARK='|';
	public static final char INCLUDE_MARK='+';
	
	private String baseMarketId;
	protected String marketId;
	protected String propFile;
	protected Date endDate; //of server timezone, since the static holidays, the dynamic date from hbase are all in server timezone
	protected Date startDate;
	protected String specialParam = null;//for special cmd to use as parameter
	protected DataStoreManager dsm = null;
	protected HdfsDataStoreManagerImpl hdfsDsm = null;

	public abstract StockConfig getStockConfig();
	public abstract boolean fqReady(Date today);

	public StockBase(String propFile, String baseMarketId, String marketId, Date sd, Date ed, String marketBaseId){
		super();
		this.setBaseMarketId(baseMarketId);
		this.marketId = marketId;
		super.setProp(propFile);
		this.propFile = propFile;
		this.startDate = sd;
		this.endDate = ed;
		dsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hbase);
		hdfsDsm = (HdfsDataStoreManagerImpl) this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hdfs);
	}
	
	public String toString(){
		return String.format("marketId:%s, propFile:%s, startDate:%s, endDate:%s", marketId, propFile, startDate, endDate);
	}
	
	public Map<String, Object> getDateParamMap(String startDate, String endDate){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(AbstractCrawlItemToCSV.FN_STARTDATE, startDate);
		paramMap.put(AbstractCrawlItemToCSV.FN_ENDDATE, endDate);
		return paramMap;
	}
	
	public void run_task(String[] taskFileName, Map<String, String> hadoopParams){
		CrawlUtil.hadoopExecuteCrawlTasksByFile(this.propFile, cconf, taskFileName, hadoopParams);
	}
	
	private void addCsvValue(CrawledItem ciDelta){
		List<String> deltaIds = (List<String>) ciDelta.getParam(KEY_IDS);
		String[][] csvValue = new String[deltaIds.size()][];
		for (int i=0; i<deltaIds.size(); i++){
			csvValue[i] = new String[]{"", deltaIds.get(i)};
		}
		ciDelta.setCsvValue(csvValue);
	}
	
	public CrawledItem run_browse_idlist(String marketId, Date endDate) throws InterruptedException{
		StockConfig sc = getStockConfig();
		CrawledItem ci = null;
		List<Task> tl = cconf.setUpSite(sc.getStockIdsCmd() + ".xml", null);
		Task t = tl.get(0);
		t.initParsedTaskDef();
		BrowseDetailType bdt = t.getBrowseDetailTask(t.getName()).getBrowsePrdTaskType();
		t.putParam(AbstractCrawlItemToCSV.FN_BASEMARKETID, this.baseMarketId);
		BrowseProductTaskConf.evalParams(t, bdt);
		String storeId = (String) t.getParamMap().get(AbstractCrawlItemToCSV.FN_STOREID);
		if (sc.getTestMarketId().equals(marketId)){
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
			addCsvValue(ci);
		}else{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("marketId", marketId);
			ci = browsePrd(sc.getStockIdsCmd() + ".xml", null, params, endDate, false).get(0);
			logger.debug("ci we got:" + ci);
			if (sc.getPairedMarket()!=null && sc.getPairedMarket().containsKey(marketId)){
				String pairMarketId = sc.getPairedMarket().get(marketId);
				//we need to add the st market as well
				params.put("marketId", pairMarketId);
				CrawledItem ciAdd = browsePrd(sc.getStockIdsCmd() + ".xml", null, params, endDate, false).get(0);
				List<String> ids = (List<String>) ciAdd.getParam(KEY_IDS);
				List<String> idsOrg = (List<String>) ci.getParam(KEY_IDS);
				idsOrg.addAll(ids);
				ci.addParam(KEY_IDS, idsOrg);
			}
		}
		ci.addParam(AbstractCrawlItemToCSV.FN_STOREID, storeId);
		return ci;
	}
	
	//cmdName is the fileName of the site-conf without suffix, is the storeid
	public CmdStatus runCmd(String cmdName, String marketId, String startDate, String endDate) {
		logger.info(String.format("runCmd cmd:%s, market %s, start %s, end %s.", cmdName, marketId, startDate, endDate));
		StockConfig sc = getStockConfig();
		if (Arrays.asList(sc.getCurrentDayCmds()).contains(cmdName)){
			//do not need start date for current day cmd, only end date needed for output folder name
		}else{
			if (startDate==null){
				startDate = sc.getStartDate(cmdName);
				if (startDate==null){
					startDate = sdf.format(sc.getMarketStartDate());
				}
			}
		}
		Map<String, Object> params = getDateParamMap(startDate, endDate);
		params.put(AbstractCrawlItemToCSV.FN_MARKETID, marketId);
		params.put(AbstractCrawlItemToCSV.FN_BASEMARKETID, baseMarketId);
		Date ed = null;
		try{
			if (endDate!=null){
				ed = sdf.parse(endDate);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		CmdStatus cs= null;
		boolean isStatic = ETLUtil.isStatic(sc, cconf, cmdName);
		logger.info(String.format("going to run cmd %s with ed:%s, sd:%s, marketId:%s", cmdName, endDate, startDate, marketId));
		if (isStatic){
			cs = new CmdStatus(marketId, cmdName, ed, true, sc.getSdf());
		}else{	
			cs = new CmdStatus(marketId, cmdName, ed, false, sc.getSdf());
		}
		String[] jobIds = ETLUtil.runTaskByCmd(sc, marketId, cconf, this.getPropFile(), cmdName, params);
		for (String jobId:jobIds){
			if (jobId!=null){
				cs.getJsMap().put(jobId, 4);//PREPARE
			}
		}
		dsm.addUpdateCrawledItem(cs, null);
		return cs;
	}
	
	private List<CmdStatus> runCmdGroup(String marketId, Date sd, Date ed, CrawlCmdGroupType groupType, 
			CrawlCmdType cmdType, String[] includeCmds, String[] excludeCmds){
		ETLUtil.cleanCaches();
		List<CmdStatus> cmdStatusList = new ArrayList<CmdStatus>();
		StockConfig sc = getStockConfig();
		String strSd = null;
		if (sd!=null){
			strSd = sdf.format(sd);
		}
		String strEd = null;
		if (ed!=null){
			strEd = sdf.format(ed);
		}
		String[] cmds = sc.getAllCmds(groupType);
		for (String cmd:cmds){
			if (includeCmds!=null){
				if (!Arrays.asList(includeCmds).contains(cmd)){
					continue;
				}
			}
			if (excludeCmds!=null){
				if (Arrays.asList(excludeCmds).contains(cmd)){
					continue;
				}
			}
			boolean isStatic = ETLUtil.isStatic(sc, cconf, cmd);
			if ((cmdType == CrawlCmdType.nondynamic && isStatic) ||
					(cmdType == CrawlCmdType.dynamic && !isStatic) ||
					(cmdType == CrawlCmdType.any)){
				CmdStatus cs = runCmd(cmd, marketId, strSd, strEd);
				if (cs==null){
					logger.error(String.format("cmdstatus for cmd:%s, market %s, start %s, end %s is null.", cmd, marketId, strSd, strEd));
				}
				cmdStatusList.add(cs);
			}
		}
		return cmdStatusList;
	}
	
	private void recordCiIds(CrawledItem ciIds){
		StockConfig sc = getStockConfig();
		List<String> curIds = (List<String>) ciIds.getParam(KEY_IDS);
		String storeId = (String) ciIds.getParam(AbstractCrawlItemToCSV.FN_STOREID);
		logger.info(String.format("market %s 1st time fetch with size %d.", marketId, curIds.size()));
		dsm.addUpdateCrawledItem(ciIds, null);//hbase persistence for curMarket
		//
		String fileName = String.format("raw/%s_%s/%s/data", this.getMarketId(), sdf.format(this.getEndDate()), storeId);
		hdfsDsm.addCrawledItem(ciIds, null, fileName);//hdfs persistence for curMarket
		
		//store the market-id-crawl status
		CmdStatus mcs = new CmdStatus(marketId, sc.getStockIdsCmd(), endDate, true, sc.getSdf());
		dsm.addUpdateCrawledItem(mcs, null);
	}
	
	//return all the cmds run
	private List<CmdStatus> runFirstTime(StockConfig sc, CrawledItem ciIds, String curMarketId, CrawlCmdGroupType groupType, 
			String[] includeCmds, String[] excludeCmds) throws InterruptedException {
		//store the stock-ids for curMarket
		logger.info(String.format("run first time for market:%s", curMarketId));
		ciIds.getId().setId(curMarketId);
		recordCiIds(ciIds);
		//do the (null, enDate) for the current market
		return runCmdGroup(curMarketId, startDate, endDate, groupType, CrawlCmdType.any, includeCmds, excludeCmds);
	}
	
	public void runIdsCmd() throws InterruptedException {
		String strEndDate=sdf.format(endDate);
		String curMarketId = marketId + idKeySep+ strEndDate;
		CrawledItem ciIds = null;
		ciIds = run_browse_idlist(marketId, endDate);
		ciIds.getId().setId(curMarketId);
		recordCiIds(ciIds);
	}
	private String getCurMarketId(){
		StockConfig sc = getStockConfig();
		String strEndDate=sdf.format(endDate);
		CmdStatus mcs = CmdStatus.getCmdStatus(dsm, marketId, sc.getStockIdsCmd());
		String curMarketId = marketId + idKeySep+ strEndDate; 
		if (mcs!=null){
			curMarketId = marketId + idKeySep + sdf.format(mcs.getId().getCreateTime());
		}
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(curMarketId, sc.getStockIdsCmd(), null);
		if (ci==null){
			logger.error(String.format("cur market not found."));
			return null;
		}else{
			return curMarketId;
		}
	}

	public boolean cmdFinished(String jid){
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		JobClient jobClient=null;
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
		boolean finished=true;
		JobID jobId = JobID.forName(jid);
		try{
			RunningJob rjob = jobClient.getJob(jobId);
			int newStatus=1;
			if (rjob!=null){
				newStatus = rjob.getJobStatus().getState().getValue();
				logger.debug(String.format("job %s got status %d", jid, newStatus));
			}else{
				logger.info(String.format("job %s not found in jobClient.", jid));
			}
			if (newStatus==1 || newStatus==4){//1 preparation, 4 running
				finished=false;
			}
		}catch(Exception e){
			logger.error("", e);
			finished=false;
		}
		return finished;
	}
	
	public boolean cmdAllFinished(Map<String, Integer> jobStatusMap){
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		JobClient jobClient=null;
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
		boolean finished=true;
		if (jobStatusMap!=null){
			for (String jid: jobStatusMap.keySet()){
				JobID jobId = JobID.forName(jid);
				try{
					RunningJob rjob = jobClient.getJob(jobId);
					int orgStatus = jobStatusMap.get(jid);
					int newStatus = orgStatus;
					if (rjob!=null){
						newStatus = rjob.getJobStatus().getState().getValue();
						logger.debug(String.format("job %s got status %d", jid, newStatus));
					}else{
						logger.info(String.format("job %s not found in jobClient.", jid));
					}
					if (newStatus!=orgStatus){
						logger.info(String.format("job %s changed from status %d to status %s.", jid, orgStatus, newStatus));
						jobStatusMap.put(jid, newStatus);
					}
					if (newStatus==1 || newStatus==4){//1 preparation, 4 running
						finished=false;
					}
				}catch(Exception e){
					logger.error("", e);
					finished=false;
				}
			}
		}
		return finished;
	}
	//update CmdStatus for async commands
	//return all the cmd who is not yet finished
	public boolean cmdFinished(CmdStatus cmdStatus){
		boolean finished=true;
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		JobClient jobClient=null;
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
		
		Map<String, Integer> jobStatusMap = cmdStatus.getJsMap();
		Map<String, Integer> newStatusMap = new HashMap<String, Integer>(); //job id to status
		if (jobStatusMap!=null){
			for (String jid: jobStatusMap.keySet()){
				JobID jobId = JobID.forName(jid);
				try{
					RunningJob rjob = jobClient.getJob(jobId);
					int orgStatus = jobStatusMap.get(jid);
					int newStatus = orgStatus;
					if (rjob!=null){
						newStatus = rjob.getJobStatus().getState().getValue();
						logger.debug(String.format("job %s got status %d", jid, newStatus));
					}else{
						logger.info(String.format("job %s not found in jobClient.", jid));
						//try history server ?
					}
					if (newStatus!=orgStatus){
						newStatusMap.put(jid, newStatus);
					}
					if (newStatus==1 || newStatus==4){//1 preparation, 4 running
						finished=false;
					}
				}catch(Exception e){
					logger.error("error get job status", e.getMessage());
				}
			}
			if (newStatusMap.size()>0){
				for (String jid:newStatusMap.keySet()){
					cmdStatus.getJsMap().put(jid, newStatusMap.get(jid));
				}
				//dsm.addUpdateCrawledItem(cs, null);
				logger.info(String.format("update status for cmd:%s to %s", cmdStatus.getCmdName(), newStatusMap));
			}
		}
		return finished;
	}
	
	public static final String STRATEGY_NAMES="sn";
	public static final String STEP_NAMES="step";
	public void validateAllStrategyByStock(String inparam){
		Map<String,String> params = StringUtil.parseMapParams(inparam);
		String snParam = params.get(STRATEGY_NAMES);
		String stepParam = params.get(STEP_NAMES);
		StockUtil.validateAllStrategyByStock(this.propFile, cconf, baseMarketId, marketId, startDate, endDate, snParam, stepParam);
	}

	//from cmd directly
	public void loadDataFilesDirectly(String param){
		Map<String,String> paramMap = StringUtil.parseMapParams(param);
		int threadNum=10;
		String localDataDir="";
		String include="-";
		String exclude="-";
		if (paramMap.containsKey(LoadDBDataTask.KEY_THREAD_NUM)){
			threadNum = Integer.parseInt(paramMap.get(LoadDBDataTask.KEY_THREAD_NUM));
		}
		if (paramMap.containsKey(LoadDBDataTask.KEY_ROOT_DIR)){
			localDataDir = paramMap.get(LoadDBDataTask.KEY_ROOT_DIR);
		}else{
			logger.error("must has " + LoadDBDataTask.KEY_ROOT_DIR);
			return;
		}
		if (paramMap.containsKey(LoadDBDataTask.KEY_INCLUDE)){
			include = paramMap.get(LoadDBDataTask.KEY_INCLUDE);
		}
		if (paramMap.containsKey(LoadDBDataTask.KEY_EXCLUDE)){
			exclude = paramMap.get(LoadDBDataTask.KEY_EXCLUDE);
		}
		String[] includeArry = new String[]{};
		String[] excludeArr = new String[]{};
		if (!include.equals("") && !include.equals("-")){
			includeArry = include.split(",");
		}
		if (!exclude.equals("") && !exclude.equals("-")){
			excludeArr = exclude.split(",");
		}
		LoadDBDataTask.launch(baseMarketId, marketId, cconf.getSmalldbconf(), threadNum, localDataDir, includeArry, excludeArr);
	}
	
	//param is null:all, startwith -:exclude these cmd, startwith +:only include these cmd, else group name
	public List<CmdStatus> runAllCmd(String param) throws InterruptedException {
		logger.info(String.format("runAllCmd with param:%s", param));
		CrawlCmdGroupType groupType = CrawlCmdGroupType.all;
		String[] excludeCmds = null;
		String[] includeCmds = null;
		if (param==null){
			groupType = CrawlCmdGroupType.all;
		}else if (param.equals(CrawlCmdGroupType.nonequote.toString())){
			groupType = CrawlCmdGroupType.nonequote;
		}else if (param.startsWith(EXCLUDE_MARK+"")){
			//means exclude which cmd
			param = param.substring(1);
			excludeCmds = StringUtils.split(param,EXCLUDE_MARK);
			logger.info(String.format("excludeCmds: %s", Arrays.asList(excludeCmds)));
		}else if (param.startsWith(INCLUDE_MARK+"")){
			//means exclude which cmd
			param = param.substring(1);
			includeCmds = StringUtils.split(param,INCLUDE_MARK);
		}
		
		List<CmdStatus> cmdStatusList = new ArrayList<CmdStatus>();
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
			logger.info(String.format("prevMarketId is %s", prevMarketId));
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
			if (ciPreIds==null){
				logger.error(String.format("can't find crawledItem for cmd %s on market %s, so run as first time", sc.getStockIdsCmd(), prevMarketId));
				cmdStatusList.addAll(runFirstTime(sc, ciIds, curMarketId, groupType, includeCmds, excludeCmds));
			}else{
				List<String> preIds = (List<String>) ciPreIds.getParam(KEY_IDS);
				//get the delta
				deltaIds = new ArrayList<String>();
				deltaIds.addAll(curIds);
				deltaIds.removeAll(preIds);
				
				if (startDate!=null){
					lastRunDate = startDate;
				}else{
					//get last all cmd run status, last run date
					CmdStatus preAllCmdRunCS = CmdStatus.getCmdStatus(dsm, prevMarketId, AllCmdRun_STATUS);
					if (preAllCmdRunCS==null){//last time AllCmdRun-CmdStatus failed to generate, from last Id-CmdStatus
						logger.warn("System corrupted, AllCmdStatus is not generated for that market. Starting from last stock-id CmdStatus time!" + sdf.format(mcs.getId().getCreateTime()));
						lastRunDate = mcs.getId().getCreateTime();
					}else{
						lastRunDate = preAllCmdRunCS.getId().getCreateTime();
					}
				}
				logger.info(String.format("market %s has org size: %d, lastRunDate: %s", marketId, preIds.size(), lastRunDate));
				if (deltaIds.size()>0){
					logger.info(String.format("market %s has delta size %d.", marketId, deltaIds.size()));
					//has new delta market, let's create another 2 markets
					dsm.addUpdateCrawledItem(ciIds, null);////hbase persistence for curMarket
					
					String deltaMarketId = marketId + idKeySep + strEndDate + idKeySep + DELTA_NAME; //delta market
					CrawledItem ciDelta = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
							new CrawledItemId(deltaMarketId, sc.getStockIdsCmd(), endDate));
					ciDelta.addParam(KEY_IDS, deltaIds);
					dsm.addUpdateCrawledItem(ciDelta, null);

					//generate hdfs file delta market: raw/[marketId]_[endDate]/storeid/data
					String fileName = String.format("raw/%s_%s/%s/data", this.getMarketId(), sdf.format(this.getEndDate()), sc.getStockIdsCmd());
					addCsvValue(ciDelta);
					hdfsDsm.addCrawledItem(ciDelta, null, fileName);//hdfs persistence for curMarket
					
					//update the market-command-status
					mcs.getId().setCreateTime(endDate);
					dsm.addUpdateCrawledItem(mcs, null);
					
					//apply static cmds (null, endDate) for delta market
					cmdStatusList.addAll(runCmdGroup(deltaMarketId, lastRunDate, endDate, groupType, 
							CrawlCmdType.nondynamic, includeCmds, excludeCmds));
				}else{
					logger.info("no delta market, so curMarket is prevMarket");
					curMarketId = prevMarketId;//so that prevMarketId's allCmdRun status can be updated
				}
				logger.info(String.format("run dynamic cmd for market %s from %s to %s", prevMarketId, lastRunDate, endDate));
				cmdStatusList.addAll(runCmdGroup(curMarketId, lastRunDate, endDate, groupType, 
						CrawlCmdType.dynamic, includeCmds, excludeCmds)); //dynamic part
			}
		}else{
			ciIds = run_browse_idlist(marketId, endDate);
			cmdStatusList.addAll(runFirstTime(sc, ciIds, curMarketId, groupType, includeCmds, excludeCmds));
		}
		CmdStatus allCmdRunStatus = new CmdStatus(curMarketId, AllCmdRun_STATUS, endDate, true, sc.getSdf());
		dsm.addUpdateCrawledItem(allCmdRunStatus, null);
		return cmdStatusList;
	}
	
	//param is null:all, startwith -:exclude these cmd, startwith +:only include these cmd
	public String[] postprocess(String param){
		String[] excludeCmds = null;
		String[] includeCmds = null;
		if (param.startsWith(EXCLUDE_MARK+"")){
			param = param.substring(1);
			excludeCmds = StringUtils.split(param,EXCLUDE_MARK);
		}else if (param.startsWith(INCLUDE_MARK+"")){
			param = param.substring(1);
			includeCmds = StringUtils.split(param,INCLUDE_MARK);
		}
		List<String> jobIds = new ArrayList<String>();
		String datePart = getStockConfig().getDatePart(marketId, startDate, endDate);
		Map<LaunchableTask, String[]> ppMap = this.getStockConfig().getPostProcessMap();
		for (LaunchableTask t:ppMap.keySet()){
			String[] cmds = ppMap.get(t);
			for (String cmd:cmds){
				if (includeCmds!=null){
					if (!Arrays.asList(includeCmds).contains(cmd)){
						continue;
					}
				}
				if (excludeCmds!=null){
					if (Arrays.asList(excludeCmds).contains(cmd)){
						continue;
					}
				}
				String[] jobIdsOneCmd = t.launch(this.propFile, baseMarketId, cconf, datePart, new String[]{cmd});
				if (jobIdsOneCmd!=null){
					jobIds.addAll(Arrays.asList(jobIdsOneCmd));
				}
			}
		}
		String[] rt = new String[jobIds.size()];
		return jobIds.toArray(rt);
	}
	
	//param is null:all, startwith -:exclude these cmd, startwith +:only include these cmd
	public String[] run_merge(String param){
		String datePart = getStockConfig().getDatePart(marketId, startDate, endDate);
		return MergeTask.launch(getStockConfig(), this.propFile, this.baseMarketId, cconf, datePart, param, true);
	}
	
	public static String dumpDir = "/data/cydata/stock/merge/";
	//param is null:all, startwith -:exclude these cmd, startwith +:only include these cmd
	public void dumpFiles(String param){
		String[] excludeCmds = new String[]{};
		String[] includeCmds = null;
		if (param.startsWith(EXCLUDE_MARK+"")){
			param = param.substring(1);
			excludeCmds = StringUtils.split(param,EXCLUDE_MARK);
		}else if (param.startsWith(INCLUDE_MARK+"")){
			param = param.substring(1);
			includeCmds = StringUtils.split(param,INCLUDE_MARK);
		}
		String strEndDate = sdf.format(endDate);
		String localDirRoot = dumpDir + strEndDate;
		String[] includeFolders = null;
		if (includeCmds!=null){
			includeFolders = new String[includeCmds.length];
			for (int i=0;i<includeFolders.length;i++){
				includeFolders[i] = String.format("%s/%s_%s", includeCmds[i], marketId, strEndDate);
			}
		}else{
			includeFolders = new String[]{String.format("%s_%s", marketId, strEndDate)};
		}
		TransferHdfsFile.launch(20, cconf.getTaskMgr().getHdfsDefaultName(), "/reminder/items/merge",
				localDirRoot, includeFolders, ListUtil.concatAll(this.getStockConfig().getSlowCmds(), excludeCmds), false);
	}
	
	//param is null:all, startwith -:exclude these cmd, startwith +:only include these cmd
	public void loadDataFiles(String param){
		String[] excludeCmds = new String[]{};
		String[] includeCmds = null;
		if (param.startsWith(EXCLUDE_MARK+"")){
			param = param.substring(1);
			excludeCmds = StringUtils.split(param,EXCLUDE_MARK);
		}else if (param.startsWith(INCLUDE_MARK+"")){
			param = param.substring(1);
			includeCmds = StringUtils.split(param,INCLUDE_MARK);
		}
		String strEndDate = sdf.format(endDate);
		String localDirRoot = dumpDir + strEndDate;
		String[] includeFolders = new String[]{this.baseMarketId};
		if (includeCmds!=null){
			includeFolders = includeCmds;
		}
		LoadDBDataTask.launch(baseMarketId, strEndDate, cconf.getSmalldbconf(), 20, localDirRoot, includeFolders, excludeCmds);
	}
	
	public void postImport(){
		String postImportFileName = getStockConfig().postImportSql();
		try{
			if (postImportFileName!=null){
				InputStreamReader br = new InputStreamReader(new FileInputStream(new File(String.format("/data/reminder/sql/%s", postImportFileName))), "UTF-8");
				Connection con = SqlUtil.getConnection(this.getCconf().getSmalldbconf());
				ScriptRunner sr = new ScriptRunner(con, false, false);
				sr.runScript(br);
				SqlUtil.closeResources(con, null);
			}
		}catch(Exception e){
			logger.error("", e);
			return;
		}
	}
	
	public String[] prepareAllData(String sn){
		try {
			String strategyName = StockUtil.STRATEGY_PREFIX+sn+StockUtil.STRATEGY_SUFFIX;
			PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
			List<SelectStrategy> bsl = SelectStrategy.gen(props, sn, this.getBaseMarketId());
			if (bsl.size()>0){
				SelectStrategy bs = bsl.get(0);
				return bs.prepareData(this.getBaseMarketId(), this.getMarketId(), cconf, this.getPropFile(), this.startDate, this.endDate);
			}else{
				return null;
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public String[] prepareOneDayData(String sn){
		try {
			String strategyName = StockUtil.STRATEGY_PREFIX+sn+StockUtil.STRATEGY_SUFFIX;
			PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
			List<SelectStrategy> bsl = SelectStrategy.gen(props, sn, this.getBaseMarketId());
			if (bsl.size()>0){
				String curMarketId = getCurMarketId();
				SelectStrategy bs = bsl.get(0);
				//set startDate to be one day before the day, and endDate to be one day after the day
				Date theDay = StockUtil.getLastOpenDay(DateTimeUtil.yesterday(this.endDate), this.getStockConfig().getHolidays());
				Date sd = DateTimeUtil.yesterday(theDay);
				Date ed = DateTimeUtil.tomorrow(theDay);
				return bs.prepareData(this.getBaseMarketId(), curMarketId, cconf, this.getPropFile(), sd, ed);
			}else{
				return null;
			}
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	//remove the raw files for this cmd under folders from startDate to endDate
	public void removeRaw(String cmd){
		if (startDate == null || endDate==null){
			logger.error("for removeRaw startDate and endDate can't be null.");
		}
		Date d = startDate;
		try {
			FileSystem fs = FileSystem.get(HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf()));
			while (d.before(endDate)){
				String fn = String.format("/reminder/items/raw/%s_%s/%s", this.marketId, sdf.format(d), cmd);
				Path p = new Path(fn);
				if (fs.exists(p)){
					fs.delete(p, true);
					logger.info(String.format("delete path %s", fn));
				}
				d = DateTimeUtil.tomorrow(d);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static final int poll_interval=20000;
	public static final String CMD_NAME="cmd";
	public static String[] cmds = new String[]{"runAllCmd", "postprocess", "run_merge", "dumpFiles", "loadDataFiles", "postImport", "prepareOneDayData"};
	public void updateAll(String inparam) throws InterruptedException{
		Map<String,String> params = StringUtil.parseMapParams(inparam);
		String cmdParam = params.get(CMD_NAME);
		String stepParam = params.get(STEP_NAMES);
		logger.info(String.format("cmd:%s,step:%s", cmdParam, stepParam));
		String[] cmdParams = new String[]{cmdParam, cmdParam, cmdParam, cmdParam, cmdParam, null, "closedropavg"};
		if (inparam !=null){
			if (CrawlCmdGroupType.nonequote.toString().equals(cmdParam)){//for none trading days
				cmds = new String[]{"runAllCmd", "run_merge", "dumpFiles", "loadDataFiles", "postImport"};
				cmdParams = new String[]{cmdParam, null, null, null, null};
			}
		}
		int step =0;
		boolean oneStep = false;
		if (stepParam!=null){
			if (!stepParam.contains(".")){//for just 1 step
				step = Integer.parseInt(stepParam);
			}else{
				stepParam = stepParam.substring(0, stepParam.indexOf("."));
				step = Integer.parseInt(stepParam);
				oneStep = true;
			}
		}
		for (int i=step; i<cmds.length; i++){
			List<CmdStatus> csl = this.runSpecial(cmds[i], cmdParams[i]);
			if (csl!=null){//async
				if (csl.size()==0){
					//cmd failed, stop all
					logger.error(String.format("cmd %s with param %s failed, stop all.", cmds[i], cmdParams[i]));
					return;
				}
				int allDone=0;
				while(allDone<csl.size()){
					allDone = 0;
					for(CmdStatus cs:csl){
						if (cs!=null){
							boolean done = this.cmdFinished(cs);
							if (!done){
								Thread.sleep(poll_interval);
								break;
							}else{
								allDone++;
							}
						}else{
							logger.error(String.format("cmd %s with param %s return cmd status null.", cmds[i], cmdParams[i]));
							allDone++;
						}
					}
				}
			}
			if (oneStep){
				return;
			}
		}
	}

	public List<CmdStatus> runSpecial(String method, String param){
		Method m;
		try {
			boolean noParam=true;
			if (param==null){
				//try method without param 1st
				try{
					m = getClass().getMethod(method);
				}catch(Exception e){
					m = getClass().getMethod(method, String.class);
					noParam=false;
				}
			}else{
				m = getClass().getMethod(method, String.class);
				noParam=false;
			}
			logger.info(String.format("going to run cmd %s with ed:%s, sd:%s, marketId:%s", method, endDate, startDate, marketId));
			if (m.getReturnType() == String[].class){
				String[] jobIds = null;
				if (noParam){
					jobIds = (String[]) m.invoke(this);
				}else{
					jobIds = (String[]) m.invoke(this, param);
				}
				if (jobIds!=null){
					CmdStatus cs = new CmdStatus(marketId, method, endDate, false, this.getStockConfig().getSdf());
					for (String jobId:jobIds){
						if (jobId!=null){
							cs.getJsMap().put(jobId, 4);//PREPARE
						}
					}
					//dsm.addUpdateCrawledItem(cs, null);
					List<CmdStatus> cmsl = new ArrayList<CmdStatus>();
					cmsl.add(cs);
					return cmsl;
				}
			}else if (m.getReturnType() == List.class){//List<CmdStatus>
				if (noParam){
					return (List<CmdStatus>)m.invoke(this);
				}else{
					return (List<CmdStatus>)m.invoke(this, param);
				}
			}else{
				if (noParam){
					m.invoke(this);
				}else{
					m.invoke(this, param);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	//getter, setter
	public CrawlConf getCconf(){
		return this.cconf;
	}
	public DataStoreManager getDsm(){
		return dsm;
	}
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
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getBaseMarketId() {
		return baseMarketId;
	}
	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
}
