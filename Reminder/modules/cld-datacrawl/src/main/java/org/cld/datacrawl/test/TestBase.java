package org.cld.datacrawl.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.taskmgr.entity.RunType;
import org.cld.util.entity.CrawledItem;


public class TestBase {
	protected static Logger logger =  LogManager.getLogger(TestBase.class);
	protected CrawlConf cconf;

	private String pFile = null;	

	
	public void setProp(String propFile){
		this.pFile = propFile;
		cconf = new CrawlConf(pFile);
	}
	
	public String getPropFile(){
		return pFile;
	}
	
	public CrawlConf getCconf(){
		return cconf;
	}
	
	private static String getConfId(String fileName){
		return fileName.substring(0,fileName.indexOf("."));
	}
	
	private static String testTaskId="testTaskId";
	
	public List<CrawledItem> browsePrd(String confName, String prdUrl, String prdTaskName, RunType bt) throws InterruptedException{
		return CrawlTestUtil.browsePrd(null, confName, prdUrl, prdTaskName, cconf, testTaskId, null, false, bt);
	}
	public List<CrawledItem> browsePrd(String confName, String prdUrl, String prdTaskName, Map<String, Object> params, RunType bt) throws InterruptedException{
		return CrawlTestUtil.browsePrd(null, confName, prdUrl, prdTaskName, cconf, testTaskId, params, false, bt);
	}
	
	public static final String CMD_CRAWL="crawl";
	public static final String CMD_CHECK_ACCOUNT="checkAccount";
	
	public static final String TASK_TYPE_BCT="bct";
	public static final String TASK_TYPE_BDT="bdt";
	public static final String TASK_TYPE_BPT="bpt";
	
	public static final String START_URL_SEP = "::";
	
	//TODO
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
			String[] surls = new String[]{""};//init with empty start url
			if (args.length>4){
				String startUrls = args[4];
				surls = startUrls.split(START_URL_SEP);
			}
			for (String starturl: surls){
				try {
					
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}else if (CMD_CHECK_ACCOUNT.equals(cmd)){
			if (args.length<4){
				logger.error(String.format("usage: TestBase propFile site-conf-file-name %s starturl", cmd));
			}
		}
	}
}
