package org.cld.stock;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
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
import org.cld.util.CompareUtil;
import org.cld.util.StringUtil;
import org.cld.util.jdbc.DBConnConf;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.hadooputil.DumpHdfsFile;
import org.cld.stock.sina.task.FillEpsTask;
import org.cld.stock.strategy.CountWaveTask;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyValidationTask;
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
	protected JobClient jobClient = null;

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
		//sdf.setTimeZone(this.getStockConfig().getTimeZone());
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
		boolean needRun = true;
		if (cs != null){
			cs.setMarketId(marketId);//some previously store CmdStatus has these field missing
			cs.setCmdName(cmdName);
			if (isStatic){
				needRun = false;
			}else if (!CompareUtil.ObjectDiffers(ed, cs.getId().getCreateTime())){//compare only end time
				//needRun = false;  //always run for dynamic
			}
		}
		if (needRun){
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
		}
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
	
	private String getDateString(Date date){
		String str=null;
		if (date!=null){
			str = sdf.format(date);
		}
		return str;
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
		//run ipo cmd
		String ipoCmd = sc.getIPODateCmd();
		if (ipoCmd!=null){
			runCmd(ipoCmd, curMarketId, getDateString(startDate), getDateString(endDate));//sync cmd do not need to track
		}
		//store the market-id-crawl status
		CmdStatus mcs = new CmdStatus(marketId, sc.getStockIdsCmd(), endDate, startDate, true, sc.getSdf());
		dsm.addUpdateCrawledItem(mcs, null);
		//do the (null, enDate) for the current market
		return runAllCmd(curMarketId, startDate, endDate, CMDTYPE_ALL);
	}

	public List<CmdStatus> runAllCmd(Date startDate, Date endDate) throws InterruptedException {
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
					if (preAllCmdRunCS==null){//last time AllCmdRunCS failed to generate, from beginning
						logger.warn("Starting from null!");
						lastRunDate = null;
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
					
					//update ipodate (lastRunDate,endDate] for curMarket
					String ipoCmd = sc.getIPODateCmd();
					if (ipoCmd!=null){
						runCmd(ipoCmd, curMarketId, getDateString(lastRunDate), getDateString(endDate));
					}
					
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

	//update CmdStatus for async commands
	//return all the cmd who is not yet finished
	public boolean cmdFinished(CmdStatus cmdStatus){
		logger.info("call cmdFinished with cmdStatus:" + cmdStatus.toString());
		String cmd = cmdStatus.getCmdName();
		String marketId = cmdStatus.getMarketId();
		Date endDate = cmdStatus.getEndTime();
		boolean finished=true;
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(this.cconf.getNodeConf());
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
	
	private static String KEY_HDFS_DEFAULTNAME="hdfs.defaultname";
	private static String KEY_OUTPUTDIR="output.dir";
	
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
			if (params!=null){
				Map<String, String> paramMap = StringUtil.parseMapParams(params);
				props.setAutoSave(false);
				for (String key:paramMap.keySet()){
					props.setProperty(key, paramMap.get(key));	
				}
			}
			DBConnConf dbconf = cconf.getSmalldbconf();
	        String outputDirPrefix = props.getString(KEY_OUTPUTDIR);
	        String fsDefaultName = props.getString(KEY_HDFS_DEFAULTNAME);
	        String time = mdssdf.format(new Date());
			String outputDir = String.format("%s/%s", outputDirPrefix, time);
			if (params!=null){
				String strParams = params.replace(".", "_").replace(":","_").replace(",", "_");
				outputDir = String.format("%s/%s_%s", outputDirPrefix, strParams, time);
			}
	        String scsType = props.getString(SelectStrategy.KEY_SELECTS_TYPE);
	        SelectStrategy scs = (SelectStrategy) Class.forName(scsType).newInstance();
	        scs.init(props);
	        scs.setOutputDir(outputDir);
	        if (fsDefaultName!=null){
	        	scs.setFsDefaultName(fsDefaultName);
	        }else{
	        	scs.setFsDefaultName(cconf.getTaskMgr().getHdfsDefaultName());
	        }
	        
	        SellStrategy[] sls = SellStrategy.genSS(props);
	        
	        return StrategyValidationTask.launch(this.propFile, cconf, marketBaseId, scs, sls, startDate, endDate, dbconf);
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}
	
	private static String KEY_WAVEHEIGHT="wave.height";
	
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
	
	public void genFillEpsSql(String param){
		FillEpsTask.launch(propFile, cconf, marketBaseId, marketId, startDate, endDate, cconf.getSmalldbconf());
	}
	
	public CmdStatus runSpecial(String method, String param){
		Method m;
		try {
			m = getClass().getMethod(method, String.class);
			logger.info(String.format("going to run cmd %s with ed:%s, sd:%s, marketId:%s", method, endDate, startDate, marketId));
			if (m.getReturnType() == String[].class){
				String[] jobIds = (String[]) m.invoke(this, param);
				if (jobIds!=null){
					CmdStatus cs = new CmdStatus(marketId, method, endDate, startDate, false, this.getStockConfig().getSdf());
					for (String jobId:jobIds){
						if (jobId!=null){
							cs.getJsMap().put(jobId, 4);//PREPARE
						}
					}
					dsm.addUpdateCrawledItem(cs, null);
					return cs;
				}
			}else{
				m.invoke(this, param);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	private static final int poll_interval=20000;
	
	//this will do all the crawling, postprocessing and merge
	public void updateAll(String param) throws InterruptedException{
		int step =1;
		if (param!=null){
			step = Integer.parseInt(param);
		}
		if (step==1){//crawl
			List<CmdStatus> csl = this.runAllCmd(startDate, endDate);
			int allDone=0;
			while(allDone<csl.size()){
				allDone = 0;
				for(int i=0; i<csl.size(); i++){
					boolean done = this.cmdFinished(csl.get(i));
					if (!done){
						Thread.sleep(poll_interval);
						break;
					}else{
						allDone++;
					}
				}
			}
			step++;
		}
		if (step==2){//file postprocess
			CmdStatus cs = this.runSpecial("postprocess", null);
			if (cs!=null){
				boolean done=false;
				while(!done){
					done = this.cmdFinished(cs);
					if (!done)
						Thread.sleep(poll_interval);
				}
			}
			step++;
		}
		if (step==3){//merge
			CmdStatus cs = this.runSpecial("run_merge", null);
			if (cs!=null){
				boolean done=false;
				while(!done){
					done = this.cmdFinished(cs);
					if (!done)
						Thread.sleep(poll_interval);
				}
			}
			step++;
		}
		String strEndDate = sdf.format(endDate);
		String localDirRoot = "/data/cydata/stock/merge/"+strEndDate;
		if (step==4){//export file from hdfs
			DumpHdfsFile.launch(20, cconf.getTaskMgr().getHdfsDefaultName(), "/reminder/items/merge",
					localDirRoot, new String[]{strEndDate}, this.getStockConfig().getSlowCmds());
			step++;
		}
		if (step==5){//import file to dbms
			LoadDBData.launch(baseMarketId, strEndDate, cconf.getSmalldbconf(), 20, 
					localDirRoot, new String[]{this.baseMarketId}, new String[]{});
			step++;
		}
	}

	public void loadDataFiles(String param){
		Map<String,String> paramMap = StringUtil.parseMapParams(param);
		int threadNum=10;
		String localDataDir="";
		String include="-";
		String exclude="-";
		if (paramMap.containsKey(LoadDBData.KEY_THREAD_NUM)){
			threadNum = Integer.parseInt(paramMap.get(LoadDBData.KEY_THREAD_NUM));
		}
		if (paramMap.containsKey(LoadDBData.KEY_ROOT_DIR)){
			localDataDir = paramMap.get(LoadDBData.KEY_ROOT_DIR);
		}else{
			logger.error("must has " + LoadDBData.KEY_ROOT_DIR);
			return;
		}
		if (paramMap.containsKey(LoadDBData.KEY_INCLUDE)){
			include = paramMap.get(LoadDBData.KEY_INCLUDE);
		}
		if (paramMap.containsKey(LoadDBData.KEY_EXCLUDE)){
			exclude = paramMap.get(LoadDBData.KEY_EXCLUDE);
		}
		String[] includeArry = new String[]{};
		String[] excludeArr = new String[]{};
		if (!include.equals("") && !include.equals("-")){
			includeArry = include.split(",");
		}
		if (!exclude.equals("") && !exclude.equals("-")){
			excludeArr = exclude.split(",");
		}
		LoadDBData.launch(marketBaseId, marketId, cconf.getSmalldbconf(), threadNum, localDataDir, includeArry, excludeArr);
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
