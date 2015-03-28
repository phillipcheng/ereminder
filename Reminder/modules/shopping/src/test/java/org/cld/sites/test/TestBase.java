package org.cld.sites.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.SiteRuntime;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.junit.Test;
import org.xml.taskdef.LoginType;

public class TestBase {
	protected static Logger logger =  LogManager.getLogger(TestBase.class);
	
	CrawlClientNode ccnode;
	CrawlConf cconf;
	public static final String propFile = "client1-v2.properties";
	
	private TestBase(String clientProperties) {
		ccnode = CrawlTestUtil.getCCNode(clientProperties);
		cconf = ccnode.getCConf();
	}
	
	public TestBase(){
		this(propFile);
	}
	
	private static String getConfId(String fileName){
		return fileName.substring(0,fileName.indexOf("."));
	}
	
	private static String testTaskId="testTaskId";
	
	public void catNavigate(String confFileName) throws Exception{
		CrawlTestUtil.catNavigate(getConfId(confFileName), confFileName, cconf, testTaskId, ccnode, propFile);
	}
	
	public void catNavigate(String confFileName, String starturl, int type) throws Exception{
		CrawlTestUtil.catNavigate(getConfId(confFileName), confFileName, starturl, type, cconf, testTaskId, ccnode, propFile);
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
			TestTaskConf tbt = new TestTaskConf(false, TestTaskConf.TEST_TASK_ONEPATH, getConfId(confXml), confXml, null);
			selectedtaskids.add(tbt.getId());
			tl.add(tbt);
		}
		TaskUtil.executeTasks(ccnode.getTaskNode(), tl);
		while(!ccnode.getTaskNode().getTaskInstanceManager().getRunningTasks().isEmpty()){
			Thread.sleep(2000);
		}
	}
	
	public int getUnlockedAccounts(String confName){
		SiteRuntime srt = CrawlTestUtil.getSRT(getConfId(confName), cconf, null);
		LoginType loginInfo = srt.getBdt().getTasks().getLoginInfo();
		try {
			return HtmlUnitUtil.checkLockedCrendentials(loginInfo, cconf);
		} catch (InterruptedException e) {
			logger.error("", e);
			return -1;
		}
	}
}
