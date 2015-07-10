package org.cld.datacrawl.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.Category;
import org.cld.pagea.general.CategoryAnalyzeUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.TasksType;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseDetailTaskConf")
public class BrowseDetailTaskConf extends Task implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static Logger logger =  LogManager.getLogger(BrowseDetailTaskConf.class);

	public static final String TASK_RUN_PARAM_MAX_PAGE="maxpage";
	public static final String TASK_RUN_PARAM_MAX_ITEM="maxitem";
	
	private String catId="";
	private String productType;//
	private int fromPage;
	private int toPage=-1;//-1 means to the end
	
	
	public BrowseDetailTaskConf(){
	}
	
	public String toString(){
		return super.toString() +
				", catId:" + catId + 
				", fromPage:" + fromPage + 
				", toPage:" + toPage;
	}
	
	@Override
	public BrowseDetailTaskConf clone(ClassLoader classLoader){
		try {
			Class<?> taskClass = Class.forName(BrowseDetailTaskConf.class.getName(), true, classLoader);			
			BrowseDetailTaskConf te = (BrowseDetailTaskConf) taskClass.newInstance();
			copy(te);	
			te.catId = this.getCatId();
			te.fromPage = this.fromPage;
			te.toPage = this.toPage;
			te.productType = this.productType;
			return te;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
	
	public String genId(){
		return super.genId() + this.getCatId() + this.getFromPage(); 
	}
	
	@Override
	public void setUp(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params) {
		super.setUp(tasks, pluginClassLoader, params);
		this.setStoreId(tasks.getStoreId());
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		try {
			//adding the runtime params
			this.putAllParams(params);
			CrawlConf cconf = (CrawlConf) params.get(CrawlClientNode.TASK_RUN_PARAM_CCONF);
			
			BrowseDetailTaskConf taskTemplate = (BrowseDetailTaskConf) cconf.getTaskMgr().getTask(getName());
			ParsedBrowsePrd pbpTemplate = taskTemplate.getBrowseDetailTask(this.getName());
			BrowseDetailType bdt = pbpTemplate.getBrowsePrdTaskType();
			DataStoreManager dsManager = null;
			if (bdt.getBaseBrowseTask().getDsm()!=null){
				dsManager = cconf.getDsm(bdt.getBaseBrowseTask().getDsm());
			}else{
				dsManager = cconf.getDefaultDsm();
			}
			if (taskTemplate != null){		
				//1. re-setup
				this.setParsedTaskDef(taskTemplate.getParsedTaskDef());		
				//2. build category from TaskEntry
				Category category = null;
				if (dsManager!=null){
					category = (Category) dsManager.getCrawledItem(getCatId(), taskTemplate.getParsedTaskDef().getTasks().getStoreId(),
						Category.class);
				}else{
					category = new Category();
				}
				String catUrl = CategoryAnalyzeUtil.getCatURL(category, this.fromPage, taskTemplate.getParsedTaskDef());
				logger.info("start browsing category:" + catUrl);
				category.setFullUrl(catUrl);
				//3. 
				int maxPages=-1;
				int maxItems = -1;
				if (params.containsKey(TASK_RUN_PARAM_MAX_PAGE)){
					maxPages = ((Integer)params.get(TASK_RUN_PARAM_MAX_PAGE)).intValue();
				}
				if (params.containsKey(TASK_RUN_PARAM_MAX_ITEM)){
					maxItems = ((Integer)params.get(TASK_RUN_PARAM_MAX_ITEM)).intValue();
				}
				return cconf.getLa().readTopLink(category, getFromPage(), getToPage(), this, maxPages, maxItems);
			}
		}catch(SomePageErrorException e){
			throw new RuntimeException(e);
		}
		return new ArrayList<Task>();
	}
	
	public String getCatId() {
		return catId;
	}
	public void setCatId(String key) {
		this.catId = key;
	}
	public int getFromPage() {
		return fromPage;
	}
	public void setFromPage(int fromPage) {
		this.fromPage = fromPage;
	}
	public int getToPage() {
		return toPage;
	}
	public void setToPage(int toPage) {
		this.toPage = toPage;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
}
