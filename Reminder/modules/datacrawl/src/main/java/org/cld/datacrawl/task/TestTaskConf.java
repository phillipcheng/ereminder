package org.cld.datacrawl.task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.CrawlTestUtil.browse_cat_type;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.TestTaskConf")
public class TestTaskConf extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(TestTaskConf.class);
	
	public static String datetimeformat="yyyy-MM-dd-hh-mm-ss-SSS";
	public static SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
	
	public static final int TEST_TASK_ONEPATH=1;
	public static final int TEST_TASK_BCT=2;
	public static final int TEST_TASK_BDT=3;
	public static final int TEST_TASK_BDT_TURNPAGEONLY=4;
	public static final int TEST_TASK_ONE_BOOK=5;
	
	//configurable values
	private String startURL;
	private String siteconfid;
	private int taskType;
	private boolean init;
	private String confXml;

	//methods
	private TestTaskConf(){
	}
	
	public TestTaskConf(boolean init, int taskType, String siteconfid, String confXml, String startUrl){
		this();
		this.init = init;
		this.taskType = taskType;
		this.siteconfid = siteconfid;
		this.confXml = confXml;
		this.startURL = startUrl;
		super.setStoreId(siteconfid);
		genId();
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
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{	
		CrawlConf cconf = (CrawlConf) params.get(CrawlClientNode.TASK_RUN_PARAM_CCONF);
		String startUrl = getStartURL();
		ThreadContext.put("taskid", getId());
		try{
			if (!isInit()){
				if (confXml==null){
					cconf.setUpSite(null, cconf.getDsm().getFullSitConf(siteconfid));
				}else{
					cconf.setUpSite(confXml, null);
				}
			}
			int taskType = getTaskType();
			if (taskType==TEST_TASK_ONEPATH){
				CrawlTestUtil.catNavigate(siteconfid, null, cconf, getId(), null, null);
			}else if (taskType == TEST_TASK_BCT){
				CrawlTestUtil.catNavigate(siteconfid, null, startUrl, 
						browse_cat_type.recursive, cconf, getId(), null, null, 0);
			}else if (taskType == TEST_TASK_ONE_BOOK){
				CrawlTestUtil.browsePrd(siteconfid, null, startUrl, cconf, getId());
			}else if (taskType == TEST_TASK_BDT_TURNPAGEONLY){
				CrawlTestUtil.runBDT(siteconfid, null, startUrl, true, cconf, getId());
			}else if (taskType == TEST_TASK_BDT){
				CrawlTestUtil.runBDT(siteconfid, null, startUrl, false, cconf, getId());
			}else{
				logger.error(String.format("taskType: %d not supported.", taskType));
			}
		}catch(Exception e){
			logger.error(String.format("got exception while exe bdt, t: %s",this), e);
		}
		return new ArrayList<Task>();
	}
	
	public String getStartURL() {
		return startURL;
	}

	public void setStartURL(String startURL) {
		this.startURL = startURL;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
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

	public String getConfXml() {
		return confXml;
	}

	public void setConfXml(String confXml) {
		this.confXml = confXml;
	}

}
