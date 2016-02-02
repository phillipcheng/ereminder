package org.cld.datacrawl.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.entity.Category;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.TasksType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseCategoryTaskConf")
public class BrowseCategoryTaskConf extends CrawlTaskConf implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(BrowseCategoryTaskConf.class);

	//configurable keys
	public static String startURL_Key="start.url";
	public static String crawlTaskConf_Key="crawl.taskconf";
		
	//configurable values
	private String startURL;
	private int pageNum=0;//number of pages to browse for this task, 0:we need to ask, 1:we execute, >1:we split to BCT
	private String pcatId=null;//this bct's parent catId
	
	//internal transient variables
	@JsonIgnore
	private transient Category newCat;
	@JsonIgnore
	private transient Category oldCat;
	
	//methods
	public BrowseCategoryTaskConf(){
	}
	
	public String toString(){
		return super.toString() +
				", startURL:" + startURL;
	}
	
	@Override
	public BrowseCategoryTaskConf clone(ClassLoader classLoader){
		try {
			Class<?> taskClass = Class.forName(BrowseCategoryTaskConf.class.getName(), true, classLoader);
			BrowseCategoryTaskConf te = (BrowseCategoryTaskConf) taskClass.newInstance();
			copy(te);
			te.setStartURL(this.getStartURL());
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
		this.startURL=(String) TaskUtil.eval(bct.getBaseBrowseTask().getStartUrl(), params);
		this.setStoreId(tasks.getStoreId());
	}
	
	@Override
	public TaskResult runMyself(Map<String, Object> params, boolean addToDB, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		//adding the runtime params
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		
		BrowseCategoryTaskConf taskTemplate = (BrowseCategoryTaskConf) cconf.getTaskMgr().getTask(getName());
		if (taskTemplate!=null){
			//1. after marshal/un-marshal, re-setup
			this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
			List<Task> tl = cconf.getCa().navigateCategory(this, cconf);
			return new TaskResult(tl, null);
		}else{
			logger.error("task is null for:" + getName());
		}
		return null;
	}
	
	//setter and getter
	public String getStartURL() {
		return startURL;
	}
	public void setStartURL(String startURL) {
		this.startURL = startURL;
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
