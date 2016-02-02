package org.cld.datacrawl.task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.BrowseType;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.entity.SiteConf;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.TestTaskConf")
public class TestTaskConf extends CrawlTaskConf implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(TestTaskConf.class);
	
	public static String datetimeformat="yyyy-MM-dd-hh-mm-ss-SSS";
	public static SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
	
	
	//configurable values
	private String startURL;
	private String siteconfid;
	private BrowseType taskType;
	
	private boolean init;
	private String confXmlFileName;
	private SiteConf siteconf;//contain xml content

	//methods
	private TestTaskConf(){
	}
	
	//called from local with file access
	public TestTaskConf(boolean init, BrowseType taskType, String siteconfid, String confXmlFileName, String startUrl){
		this();
		this.init = init;
		this.taskType = taskType;
		this.siteconfid = siteconfid;
		this.confXmlFileName = confXmlFileName;
		this.startURL = startUrl;
		super.setStoreId(siteconfid);
		genId();
	}
	
	//called from web server with siteconf passed no file access
	public TestTaskConf(boolean init, BrowseType taskType, SiteConf siteconf, String startUrl){
		this(init, taskType, siteconf.getId(), null, startUrl);
		this.siteconf = siteconf;
	}
	
	public String toString(){
		return super.toString() +
				", startURL:" + startURL + 
				", siteconfid:" + siteconfid + 
				", taskType:" + taskType;
	}
	
	@Override
	public String genId(){
		//since the id with be used as the parameter to a javascript function, we need to escape
		String inputId = taskType+"-" + siteconfid + "-" + startURL + "-" + sdf.format(new Date());
		inputId = inputId.replace(":", "-");
		inputId = inputId.replace("/", "-");
		inputId = inputId.replace(".", "-");
		this.setId(inputId);
		return this.getId();
	}

	@Override
	public String getOutputDir(Map<String, Object> paramMap, TaskConf tconf){
		CrawlConf cconf = (CrawlConf) tconf;
		if (taskType == BrowseType.product){
			List<Task> tl = cconf.setUpSite(confXmlFileName, siteconf);
			for (Task t: tl){
				if (t instanceof BrowseProductTaskConf){
					return ((BrowseProductTaskConf)t).getOutputDir(paramMap, tconf);
				}
			}
		}
		return null;
	}
	
	@Override
	public TaskResult runMyself(Map<String, Object> params, boolean addToDB, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{	
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		String startUrl = getStartURL();
		ThreadContext.put("taskid", getId());
		try{
			if (!isInit()){
				cconf.setUpSite(confXmlFileName, siteconf);
			}
			BrowseType taskType = getTaskType();
			if (taskType==BrowseType.onePath){
				CrawlTestUtil.catNavigate(siteconf, confXmlFileName, cconf, getId(), null);
			}else if (taskType == BrowseType.category){
				CrawlTestUtil.catNavigate(siteconf, confXmlFileName, startUrl, BrowseType.recursive, cconf, getId(), null, null, 0);
			}else if (taskType == BrowseType.product){
				CrawlTestUtil.browsePrd(siteconf, confXmlFileName, startUrl, cconf, getId(), this.getParamMap(), this.getStartDate(), false, context, mos);
			}else if (taskType == BrowseType.detailsTurnPageOnly){
				CrawlTestUtil.runBDT(siteconf, confXmlFileName, startUrl, true, cconf, getId());
			}else if (taskType == BrowseType.details){
				CrawlTestUtil.runBDT(siteconf, confXmlFileName, startUrl, false, cconf, getId());
			}else{
				logger.error(String.format("taskType: %d not supported.", taskType));
			}
		}catch(Exception e){
			logger.error(String.format("got exception while exe bdt, t: %s",this), e);
		}
		return null;
	}
	
	//getter and setter
	public String getStartURL() {
		return startURL;
	}
	public void setStartURL(String startURL) {
		this.startURL = startURL;
	}
	public boolean isInit() {
		return init;
	}
	public void setInit(boolean init) {
		this.init = init;
	}
	public String getSiteconfid() {
		return siteconfid;
	}
	public void setSiteconfid(String siteconfid) {
		this.siteconfid = siteconfid;
	}
	public BrowseType getTaskType() {
		return taskType;
	}
	public void setTaskType(BrowseType taskType) {
		this.taskType = taskType;
	}
	public String getConfXmlFileName() {
		return confXmlFileName;
	}
	public void setConfXmlFileName(String confXmlFileName) {
		this.confXmlFileName = confXmlFileName;
	}
	public SiteConf getSiteconf() {
		return siteconf;
	}
	public void setSiteconf(SiteConf siteconf) {
		this.siteconf = siteconf;
	}
}
