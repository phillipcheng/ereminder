package org.cld.datacrawl.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil.browse_type;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.taskmgr.entity.Task;


public class TestBase {
	protected static Logger logger =  LogManager.getLogger(TestBase.class);
	protected CrawlConf cconf;

	private String pFile = null;	

	
	public void setProp(String propFile){
		this.pFile = propFile;
		cconf = CrawlTestUtil.getCConf(pFile);
	}
	
	public String getPropFile(){
		return pFile;
	}
	
	private static String getConfId(String fileName){
		return fileName.substring(0,fileName.indexOf("."));
	}
	
	private static String testTaskId="testTaskId";
	
	public void catNavigate(String confFileName) throws Exception{
		CrawlTestUtil.catNavigate(getConfId(confFileName), confFileName, cconf, testTaskId, pFile);
	}
	
	public void catNavigate(String confFileName, String starturl, browse_type type) throws Exception{
		CrawlTestUtil.catNavigate(getConfId(confFileName), confFileName, starturl, type, cconf, testTaskId, pFile, 0);
	}
	
	public void catNavigate(String confFileName, String starturl, browse_type type, int pageNum) throws Exception{
		CrawlTestUtil.catNavigate(getConfId(confFileName), confFileName, starturl, type, cconf, testTaskId, pFile, pageNum);
	}
	
	public void runBDT(String confFileName, String startUrl, boolean turnPagesOnly) throws Exception{
		CrawlTestUtil.runBDT(getConfId(confFileName), confFileName, startUrl, turnPagesOnly, cconf, testTaskId);
	}
	public void browsePrd(String confName, String prdUrl) throws InterruptedException{
		CrawlTestUtil.browsePrd(getConfId(confName), confName, prdUrl, cconf, testTaskId);
	}
	public void browsePrd(String confName, String prdUrl, Map<String, Object> params) throws InterruptedException{
		CrawlTestUtil.browsePrd(getConfId(confName), confName, prdUrl, cconf, testTaskId, params);
	}
	public void browsePrd(String confName, String prdUrl, String prdTaskName, Map<String, Object> params) throws InterruptedException{
		CrawlTestUtil.browsePrd(getConfId(confName), confName, prdUrl, prdTaskName, cconf, testTaskId, params);
	}
	//sequential
	public void regressionAll(String[] allConf) throws Exception{
		for (String conf: allConf){
			catNavigate(conf);
		}
	}
	
	//parallel
	public void regressionTaskAll(String[] allConf) throws Exception {
		List<String> selectedtaskids = new ArrayList<String>();
		List<Task> tl = new ArrayList<Task>();
		for (String confXml: allConf){
			TestTaskConf tbt = new TestTaskConf(false, browse_type.one_path, getConfId(confXml), confXml, null);
			selectedtaskids.add(tbt.getId());
			tl.add(tbt);
		}
		CrawlTestUtil.executeTasks(tl, cconf, pFile);
	}
	
	public int getUnlockedAccounts(String landingUrl, String confName){
		SiteRuntime srt = CrawlTestUtil.getSRT(getConfId(confName), cconf, null);
		try {
			return HtmlUnitUtil.checkLockedCrendentials(landingUrl, srt.getSiteDef(), cconf);
		} catch (InterruptedException e) {
			logger.error("", e);
			return -1;
		}
	}
	
	public static final String CMD_CRAWL="crawl";
	public static final String CMD_CHECK_ACCOUNT="checkAccount";
	
	public static final String TASK_TYPE_BCT="bct";
	public static final String TASK_TYPE_BDT="bdt";
	public static final String TASK_TYPE_BPT="bpt";
	
	public static final String START_URL_SEP = "::";
	
	//starturls comma separated string
	public static void main(String[] args) throws Exception {
		if (args.length<3){
			logger.error("usage: TestBase propFile site-conf-file-name cmd ...");
			return;
		}
		
		String prop = args[0];
		TestBase tb = new TestBase();
		tb.setProp(prop);
		String siteconfName = args[1];
		
		String cmd = args[2];
		if (CMD_CRAWL.equals(cmd)){
			if (args.length<5){
				logger.error(String.format("usage: TestBase propFile site-conf-file-name %s tasktype starturls", cmd));
			}
			String taskType = args[3];
			String startUrls = args[4];
			
			String[] surls = startUrls.split(START_URL_SEP);
			for (String starturl: surls){
				try {
					if (TASK_TYPE_BCT.equals(taskType)){
						tb.catNavigate(siteconfName, starturl, browse_type.recursive);
					}else if (TASK_TYPE_BDT.equals(taskType)){
						tb.runBDT(siteconfName, starturl, false);
					}else if (TASK_TYPE_BPT.equals(taskType)){
						tb.browsePrd(siteconfName, starturl);
					}else{
						logger.error(String.format("task type:%s not supported.", taskType));
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}else if (CMD_CHECK_ACCOUNT.equals(cmd)){
			if (args.length<4){
				logger.error(String.format("usage: TestBase propFile site-conf-file-name %s starturl", cmd));
			}
			String startUrl = args[3];
			int i = tb.getUnlockedAccounts(startUrl, siteconfName);
			logger.info(String.format("%d unlocked accounts for %s on url %s", i, siteconfName, startUrl));
		}
	}
}
