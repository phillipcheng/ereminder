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
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.CrawlTestUtil.browse_type;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

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
	private browse_type taskType;
	
	private boolean init;
	private String confXml;

	//methods
	private TestTaskConf(){
	}
	
	public TestTaskConf(boolean init, browse_type taskType, String siteconfid, String confXml, String startUrl){
		this();
		this.init = init;
		this.taskType = taskType;
		this.siteconfid = siteconfid;
		this.confXml = confXml;
		this.startURL = startUrl;
		super.setStoreId(siteconfid);
		genId();
	}
	
	public TestTaskConf(boolean init, browse_type taskType, String siteconfid, String confXml){
		this(init, taskType, siteconfid, confXml, null);
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
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		String startUrl = getStartURL();
		ThreadContext.put("taskid", getId());
		try{
			if (!isInit()){
				if (confXml==null){
					cconf.setUpSite(null, cconf.getDefaultDsm().getFullSitConf(siteconfid));
				}else{
					cconf.setUpSite(confXml, null);
				}
			}
			browse_type taskType = getTaskType();
			if (taskType==browse_type.one_path){
				CrawlTestUtil.catNavigate(siteconfid, null, cconf, getId(), null);
			}else if (taskType == browse_type.bct){
				CrawlTestUtil.catNavigate(siteconfid, null, startUrl, 
						browse_type.recursive, cconf, getId(), null, null, 0);
			}else if (taskType == browse_type.bpt){
				CrawlTestUtil.browsePrd(siteconfid, confXml, startUrl, cconf, getId(), this.getParamMap(), this.getStartDate(), true);
			}else if (taskType == browse_type.bdt_turnpage_only){
				CrawlTestUtil.runBDT(siteconfid, null, startUrl, true, cconf, getId());
			}else if (taskType == browse_type.bdt){
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

	public browse_type getTaskType() {
		return taskType;
	}

	public void setTaskType(browse_type taskType) {
		this.taskType = taskType;
	}
}
