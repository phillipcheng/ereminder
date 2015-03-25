package org.cld.datacrawl.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.TasksType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseCategoryTaskConf")
public class BrowseCategoryTaskConf extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(BrowseCategoryTaskConf.class);

	//configurable keys
	public static String startURL_Key="start.url";
	public static String crawlTaskConf_Key="crawl.taskconf";
		
	//configurable values
	private String startURL;
	private String crawlTaskConf;
	private int pageNum=0;//number of pages to browse for this task, 0:we need to ask, 1:we execute, >1:we split to BCT
	private String pcatId=null;//this bct's parent catId
	
	//internal transient variables
	@JsonIgnore
	private transient CrawlTaskConf ctconf;
	@JsonIgnore
	private transient Category newCat;
	@JsonIgnore
	private transient Category oldCat;
	
	//methods
	public BrowseCategoryTaskConf(){
	}
	
	public String toString(){
		return super.toString() +
				", startURL:" + startURL + 
				", crawlTaskConf:" + crawlTaskConf + 
				", ctconf:" + ctconf;
	}
	
	@Override
	public BrowseCategoryTaskConf clone(ClassLoader classLoader){
		try {
			Class<?> taskClass = Class.forName(BrowseCategoryTaskConf.class.getName(), true, classLoader);
			BrowseCategoryTaskConf te = (BrowseCategoryTaskConf) taskClass.newInstance();
			copy(te);
			te.setStartURL(this.getStartURL());
			te.setCrawlTaskConf(this.getCrawlTaskConf());
			te.setCtconf(this.getCtconf());
			te.setPcatId(this.getPcatId());
			te.setNewCat(this.getNewCat());
			te.setOldCat(this.getOldCat());
			te.setRootTaskId(this.getRootTaskId());
			return te;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
	
	public String genId(){
		this.setId(super.genId() + "|" + getStartURL() + "|" + getPageNum());
		return this.getId();
	}

	@Override
	public void setUp(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params) {
		super.setUp(tasks, pluginClassLoader, params);
		BrowseCatType bct = tasks.getCatTask().get(0);
		//since the start url is get from xml file, so un-escape is needed
		this.startURL=StringEscapeUtils.unescapeXml(bct.getBaseBrowseTask().getStartUrl());
		this.crawlTaskConf = "general";
		this.setStoreId(tasks.getStoreId());
		CrawlConf cconf = (CrawlConf) params.get(CrawlConf.taskParamCConf_Key);
		ctconf = cconf.getCCTConf(crawlTaskConf);//if no changes to this ctconf, just use the template
		if (ctconf == null){
			logger.error("ctconf not found:" + crawlTaskConf);
		}
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		//adding the runtime params
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(CrawlClientNode.TASK_RUN_PARAM_CCONF);
		
		BrowseCategoryTaskConf taskTemplate = (BrowseCategoryTaskConf) cconf.getTaskMgr().getTask(getName());
		if (taskTemplate!=null){
			//1. after marshal/un-marshal, re-setup
			CrawlTaskConf ctconf = taskTemplate.getCtconf();
			setCtconf(ctconf);
			this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
			ICategoryAnalyze ca= ctconf.getCa();
			return ca.navigateCategory(this, ts);
		}else{
			logger.error("task is null for:" + getName());
		}
		return new ArrayList<Task>();
	}
	
	//setter and getter
	public String getStartURL() {
		return startURL;
	}
	public void setStartURL(String startURL) {
		this.startURL = startURL;
	}
	public String getCrawlTaskConf() {
		return crawlTaskConf;
	}
	public void setCrawlTaskConf(String crawlTaskConf) {
		this.crawlTaskConf = crawlTaskConf;
	}
	
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getPcatId() {
		return pcatId;
	}

	public void setPcatId(String pcatId) {
		this.pcatId = pcatId;
	}
	
	@JsonIgnore
	public CrawlTaskConf getCtconf() {
		return ctconf;
	}
	public void setCtconf(CrawlTaskConf ctconf) {
		this.ctconf = ctconf;
	}@JsonIgnore
	public Category getNewCat() {
		return newCat;
	}
	public void setNewCat(Category cat) {
		this.newCat = cat;
	}
	@JsonIgnore
	public Category getOldCat() {
		return oldCat;
	}
	public void setOldCat(Category oldCat) {
		this.oldCat = oldCat;
	}
}
