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
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.taskmgr.entity.CmdStatus;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.DBConnConf;
import org.cld.util.jdbc.ScriptRunner;
import org.cld.util.jdbc.SqlUtil;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.hadooputil.DefaultCopyTextReducer;
import org.cld.hadooputil.DumpHdfsFile;
import org.cld.stock.sina.task.FillEpsTask;
import org.cld.stock.strategy.CountWaveTask;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.SortMapper;
import org.cld.stock.strategy.StrategyValidationTask;
import org.cld.stock.task.LoadDBDataTask;
import org.cld.stock.task.MergeTask;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.TestBase;

public abstract class StockBase extends TestBase{

	protected static Logger logger =  LogManager.getLogger(StockBase.class);
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat mdssdf = new SimpleDateFormat("MMddHHmmss");
	public static final String AllCmdRun_STATUS="AllCmdRun";
	public static final String KEY_IDS = "ids";
	
	public static final String idKeySep = "_";
	public static final String DELTA_NAME="delta";
	
	private String baseMarketId;
	protected String marketId;
	protected String propFile;
	protected Date endDate; //of server timezone, since the static holidays, the dynamic date from hbase are all in server timezone
	protected Date startDate;
	protected String specialParam = null;//for special cmd to use as parameter
	private String marketBaseId;
	
	protected DataStoreManager dsm = null;
	protected DataStoreManager hdfsDsm = null;

	public abstract StockConfig getStockConfig();
	
	public DataStoreManager getDsm(){
		return dsm;
	}
	
	public StockBase(String propFile, String baseMarketId, String marketId, Date sd, Date ed, String marketBaseId){
		super();
		this.setBaseMarketId(baseMarketId);
		this.marketBaseId = marketBaseId;
		this.marketId = marketId;
		super.setProp(propFile);
		this.propFile = propFile;
		this.startDate = sd;
		this.endDate = ed;
		this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
		dsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hbase);
		hdfsDsm = this.cconf.getDsm(CrawlConf.crawlDsManager_Value_Hdfs);
	}
	
	public String toString(){
		return String.format("marketId:%s, propFile:%s, startDate:%s, endDate:%s", marketId, propFile, startDate, endDate);
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
		logger.debug("ci we got:" + ciOrg);
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
	public CmdStatus runCmd(String cmdName, String marketId, String startDate, String endDate) {
		StockConfig sc = getStockConfig();
		if (Arrays.asList(sc.getCurrentDayCmds()).contains(cmdName)){
			Date d = sc.getLatestOpenMarketDate(new Date());
			if (startDate!=null){
				try{
					Date sd = sdf.parse(startDate);
					if (d.before(sd)){
						return null;//skip current day cmd, when the last open market date is before start date
					}
				}catch(Exception e){
					logger.error("", e);
				}
			}else{
				startDate = sdf.format(d);
				endDate = startDate;
			}
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
			cs = CmdStatus.getCmdStatus(dsm, marketId, cmdName, ed, sc.getSdf());
		}
	
		logger.info(String.format("going to run cmd %s with ed:%s, sd:%s, marketId:%s", cmdName, endDate, startDate, marketId));
		if (isStatic){
			cs = new CmdStatus(marketId, cmdName, ed, sd, true, sc.getSdf());
		}else{	
			cs = new CmdStatus(marketId, cmdName, ed, sd, false, sc.getSdf());
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
	
	public static int CMDTYPE_STATIC=1; //not updated with time, static data
	public static int CMDTYPE_DYNAMIC=2; //updated with time
	public static int CMDTYPE_ALL=3;
	
	//return all the cmds run
	private List<CmdStatus> runAllCmd(String marketId, Date sd, Date ed, int type){
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
				CmdStatus cs = null;
				if (marketId.startsWith(sc.getTestMarketId()) && Arrays.asList(sc.getSlowCmds()).contains(cmd) 
						&& (sd!=null && testStartDate.after(sd))){
					cs = runCmd(cmd, marketId, sc.getTestShortStartDate(), strEd);//tradeConf generates too much data, limit it to short_period for test market
				}else{
					cs = runCmd(cmd, marketId, strSd, strEd);
				}
				cmdStatusList.add(cs);
			}
		}
		return cmdStatusList;
	}
	
	//return all the cmds run
	private List<CmdStatus> runFirstTime(StockConfig sc, CrawledItem ciIds, String curMarketId) throws InterruptedException {
		//store the stock-ids for curMarket
		logger.info(String.format("run first time for market:%s", curMarketId));
		ciIds.getId().setId(curMarketId);
		List<String> curIds = (List<String>) ciIds.getParam(KEY_IDS);
		logger.info(String.format("market %s 1st time fetch with size %d.", marketId, curIds.size()));
		dsm.addUpdateCrawledItem(ciIds, null);//hbase persistence for curMarket
		hdfsDsm.addUpdateCrawledItem(ciIds, null);//hdfs persistence for cur market
		
		//store the market-id-crawl status
		CmdStatus mcs = new CmdStatus(marketId, sc.getStockIdsCmd(), endDate, startDate, true, sc.getSdf());
		dsm.addUpdateCrawledItem(mcs, null);
		//do the (null, enDate) for the current market
		return runAllCmd(curMarketId, startDate, endDate, CMDTYPE_ALL);
	}

	public List<CmdStatus> runAllCmd() throws InterruptedException {
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
				cmdStatusList.addAll(runFirstTime(sc, ciIds, curMarketId));
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
					hdfsDsm.addUpdateCrawledItem(ciIds, null);//hdfs persistence for curMarket
					String deltaMarketId = marketId + idKeySep + strEndDate + idKeySep + DELTA_NAME; //delta market
					CrawledItem ciDelta = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
							new CrawledItemId(deltaMarketId, sc.getStockIdsCmd(), endDate));
					ciDelta.addParam(KEY_IDS, deltaIds);
					dsm.addUpdateCrawledItem(ciDelta, null);

					//update the market-command-status
					mcs.getId().setCreateTime(endDate);
					dsm.addUpdateCrawledItem(mcs, null);
					
					//apply static cmds (null, endDate) for delta market
					cmdStatusList.addAll(runAllCmd(deltaMarketId, lastRunDate, endDate, CMDTYPE_STATIC));
				}else{
					logger.info("no delta market, so curMarket is prevMarket");
					curMarketId = prevMarketId;//so that prevMarketId's allCmdRun status can be updated
				}
				/* we do not fill the gap between startDate and lastRunDate now
				if (CompareUtil.ObjectDiffers(startDate, lastRunDate)){//check the (null, lastRunDate) for the pre market
					logger.info(String.format("rerun all cmd for pre-market %s from %s to %s", prevMarketId, startDate, lastRunDate));
					cmdStatusList.addAll(runAllCmd(curMarketId, startDate, lastRunDate, CMDTYPE_ALL));
				}*/
				//if (CompareUtil.ObjectDiffers(lastRunDate, endDate)){//do the [lastRunDate,endDate) for the cur market
					logger.info(String.format("run dynamic cmd for market %s from %s to %s", prevMarketId, lastRunDate, endDate));
					cmdStatusList.addAll(runAllCmd(curMarketId, lastRunDate, endDate, CMDTYPE_DYNAMIC)); //dynamic part
				//}
			}
		}else{
			ciIds = run_browse_idlist(marketId, endDate);
			cmdStatusList.addAll(runFirstTime(sc, ciIds, curMarketId));
		}
		CmdStatus allCmdRunStatus = new CmdStatus(curMarketId, AllCmdRun_STATUS, endDate, startDate, true, sc.getSdf());
		dsm.addUpdateCrawledItem(allCmdRunStatus, null);
		return cmdStatusList;
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
		String cmd = cmdStatus.getCmdName();
		String marketId = cmdStatus.getMarketId();
		Date endDate = cmdStatus.getEndTime();
		boolean finished=true;
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
		JobClient jobClient=null;
		try {
			jobClient = new JobClient(hconf);
		}catch(Exception e){
			logger.error("", e);
		}
		boolean isCmd = false;
		//check whether cmd is cmd or method
		try{
			getClass().getMethod(cmd, String.class);
			isCmd = false;
		}catch(NoSuchMethodException nsme){
			isCmd = true;
		}
		
		CmdStatus cs = null;
		boolean isStatic = false;
		if (isCmd){
			isStatic = ETLUtil.isStatic(cconf, cmd);
		}
		if (isStatic){
			cs = CmdStatus.getCmdStatus(dsm, marketId, cmd);
		}else{
			cs = CmdStatus.getCmdStatus(dsm, marketId, cmd, this.endDate, this.getStockConfig().getSdf());
		}
		if (cs == null){
			logger.error(String.format("CmdStatus not found for marketId:%s, cmd:%s, endDate:%s", marketId, cmd, endDate));
		}else{
			Map<String, Integer> jobStatusMap = cs.getJsMap();
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
						cs.getJsMap().put(jid, newStatusMap.get(jid));
					}
					dsm.addUpdateCrawledItem(cs, null);
					logger.info(String.format("update status for cmd:%s to %s", cmd, newStatusMap));
				}
			}
		}
		return finished;
	}
	
	public static final String STRATEGY_PREFIX="strategy.";
	public static final String STRATEGY_SUFFIX=".properties";
	public void validateAllStrategy(String param){
		try{
			StockConfig sc = this.getStockConfig();
			Map<String, Integer> jobsStatus = new HashMap<String, Integer>();
			for (String simpleStrategyName:sc.getAllStrategy()){
				String strategyName = STRATEGY_PREFIX+simpleStrategyName+STRATEGY_SUFFIX;
				String[] jobIds = validateStrategy(strategyName);
				for (String jobId: jobIds){
					jobsStatus.put(jobId, 1);
				}
			}
			Thread.sleep(10000);
			while (!cmdAllFinished(jobsStatus)){
				Thread.sleep(10000);
			}
			
			String[] inputPaths = new String[sc.getAllStrategy().length];
			for (int i=0; i<inputPaths.length; i++){
				String simpleStrategyName = sc.getAllStrategy()[i];
				String outputDir = String.format("/reminder/sresult/%s_%s_%s/%s/", marketId, sdf.format(startDate), sdf.format(endDate), 
						simpleStrategyName);
				inputPaths[i] = outputDir;
			}
			String outputDir = String.format("/reminder/sresult/%s_%s_%s/all", marketId, sdf.format(startDate), sdf.format(endDate));
			Map<String, String> hadoopParams = new HashMap<String, String>();
			hadoopParams.put(HadoopTaskLauncher.FILTER_REGEXP_KEY, ".*summary-.*");
			HadoopTaskLauncher.executeTasks(this.getCconf().getNodeConf(), hadoopParams, inputPaths, false, outputDir, false, 
					SortMapper.class, DefaultCopyTextReducer.class, false);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	//param the strategy configure file
	public String[] validateStrategy(String param){
		try{
			String strategyName = param;
			String params=null;
			if (param.contains("__")){
				String[] a = param.split("__");
				strategyName = a[0];
				params = a[1];
			}
			PropertiesConfiguration props = new PropertiesConfiguration(strategyName);
			String simpleStrategyName = StringUtil.getStringBetweenFirstPreFirstPost(strategyName, STRATEGY_PREFIX, STRATEGY_SUFFIX);
			if (params!=null){
				Map<String, String> paramMap = StringUtil.parseMapParams(params);
				props.setAutoSave(false);
				for (String key:paramMap.keySet()){
					props.setProperty(key, paramMap.get(key));	
				}
			}
			DBConnConf dbconf = cconf.getSmalldbconf();
	        String outputDir = String.format("/reminder/sresult/%s_%s_%s/%s", marketId, sdf.format(startDate), sdf.format(endDate), simpleStrategyName);
	        String scsType = props.getString(SelectStrategy.KEY_SELECTS_TYPE);
	        SelectStrategy scs = (SelectStrategy) Class.forName(scsType).newInstance();
	        List<SelectStrategy> ssl = scs.gen(props, simpleStrategyName);
	        SellStrategy[] sls = SellStrategy.genSS(props);
	        return StrategyValidationTask.launch(this.propFile, cconf, marketBaseId, marketId, outputDir, ssl, sls, startDate, endDate, dbconf); 
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	private static String KEY_WAVEHEIGHT="wave.height";
	private static String KEY_HDFS_DEFAULTNAME="hdfs.defaultname";
	private static String KEY_OUTPUTDIR="output.dir";
	
	public String[] countWave(String param){
		try{
			PropertiesConfiguration props = new PropertiesConfiguration(param);
	        String outputDirPrefix = props.getString(KEY_OUTPUTDIR);
	        String time = mdssdf.format(new Date());
	        String inputHdfs = props.getString(KEY_HDFS_DEFAULTNAME);
			String outputDir = String.format("%s_%s", outputDirPrefix, time);
	        float waveHeight = props.getFloat(KEY_WAVEHEIGHT);
	        return CountWaveTask.launch(this.propFile, cconf, marketBaseId, marketId, startDate, endDate, inputHdfs, outputDir, waveHeight);
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	//from cmd directly
	public void loadDataFiles(String param){
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
		LoadDBDataTask.launch(marketBaseId, marketId, cconf.getSmalldbconf(), threadNum, localDataDir, includeArry, excludeArr);
	}
	public String[] postprocess(String param){
		List<String> jobIds = new ArrayList<String>();
		String datePart = getStockConfig().getDatePart(marketId, startDate, endDate);
		Map<LaunchableTask, String[]> ppMap = this.getStockConfig().getPostProcessMap();
		for (LaunchableTask t:ppMap.keySet()){
			String[] cmds = ppMap.get(t);
			String[] jobIdsOneCmd = t.launch(this.propFile, cconf, datePart, cmds);
			if (jobIdsOneCmd!=null){
				jobIds.addAll(Arrays.asList(jobIdsOneCmd));
			}
		}
		String[] rt = new String[jobIds.size()];
		return jobIds.toArray(rt);
	}
	public String[] run_merge(String param){
		String datePart = getStockConfig().getDatePart(marketId, startDate, endDate);
		return MergeTask.launch(getStockConfig(), this.propFile, cconf, datePart, param, true);
	}
	
	public static String dumpDir = "/data/cydata/stock/merge/";
	public void dumpFiles(){
		String strEndDate = sdf.format(endDate);
		String localDirRoot = dumpDir + strEndDate;
		DumpHdfsFile.launch(20, cconf.getTaskMgr().getHdfsDefaultName(), "/reminder/items/merge",
				localDirRoot, new String[]{strEndDate}, this.getStockConfig().getSlowCmds());
	}
	public void loadDataFiles(){
		String strEndDate = sdf.format(endDate);
		String localDirRoot = dumpDir + strEndDate;
		LoadDBDataTask.launch(baseMarketId, strEndDate, cconf.getSmalldbconf(), 20, 
				localDirRoot, new String[]{this.baseMarketId}, new String[]{});
	}
	public void setPubDtSql(){
		if (StockUtil.SINA_STOCK_BASE.equals(this.marketBaseId)){
			try{
				InputStreamReader br = new InputStreamReader(new FileInputStream(new File("/data/reminder/sql/postimport.sql")), "UTF-8");
				Connection con = SqlUtil.getConnection(this.getCconf().getSmalldbconf());
				ScriptRunner sr = new ScriptRunner(con, false, false);
				sr.runScript(br);
				SqlUtil.closeResources(con, null);
			}catch(Exception e){
				logger.error("", e);
				return;
			}
		}
	}
	public String[] genFillEpsSql(String param){
		if (StockUtil.SINA_STOCK_BASE.equals(this.marketBaseId)){
			String outputDir = String.format("/reminder/items/sql/%s/%s", FillEpsTask.class.getSimpleName(), sdf.format(endDate));
			return FillEpsTask.launch(propFile, cconf, marketBaseId, marketId, startDate, endDate, cconf.getSmalldbconf(), outputDir);
		}else{
			return null;
		}
	}
	public void exeFillEpsSql(){
		if (StockUtil.SINA_STOCK_BASE.equals(this.marketBaseId)){
			String outputDir = String.format("/reminder/items/sql/%s/%s", FillEpsTask.class.getSimpleName(), sdf.format(endDate));
			String outputFile = outputDir + "/" + "part-r-00000";
			Configuration conf = HadoopTaskLauncher.getHadoopConf(this.getCconf().getNodeConf());
			//generate task list file
			try {
				//generate the task file
				FileSystem fs = FileSystem.get(conf);
				Path fileNamePath = new Path(outputFile);
				FSDataInputStream fin = fs.open(fileNamePath);
				InputStreamReader br = new InputStreamReader(fin, "UTF-8");
				Connection con = SqlUtil.getConnection(this.getCconf().getSmalldbconf());
				ScriptRunner sr = new ScriptRunner(con, false, false);
				sr.runScript(br);
				SqlUtil.closeResources(con, null);
			}catch(Exception e){
				logger.error("", e);
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
					CmdStatus cs = new CmdStatus(marketId, method, endDate, startDate, false, this.getStockConfig().getSdf());
					for (String jobId:jobIds){
						if (jobId!=null){
							cs.getJsMap().put(jobId, 4);//PREPARE
						}
					}
					dsm.addUpdateCrawledItem(cs, null);
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
	private static final int poll_interval=20000;
	public static String[] cmds = new String[]{"runAllCmd", "postprocess", "run_merge", "dumpFiles", "loadDataFiles", 
			"setPubDtSql", "genFillEpsSql", "exeFillEpsSql"};
	public void updateAll(String param) throws InterruptedException{
		int step =0;
		boolean oneStep = false;
		if (param!=null){
			if (!param.contains(".")){//for just 1 step
				step = Integer.parseInt(param);
			}else{
				param = param.substring(0, param.indexOf("."));
				step = Integer.parseInt(param);
				oneStep = true;
			}
		}
		
		for (int i=step; i<cmds.length; i++){
			List<CmdStatus> csl = this.runSpecial(cmds[i], null);
			if (csl!=null){//async
				int allDone=0;
				while(allDone<csl.size()){
					allDone = 0;
					for(CmdStatus cs:csl){
						boolean done = this.cmdFinished(cs);
						if (!done){
							Thread.sleep(poll_interval);
							break;
						}else{
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
