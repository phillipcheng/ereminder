package org.cld.datacrawl.task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.pagea.general.ProductListAnalyzeUtil;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.CompareUtil;
import org.cld.util.StringUtil;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.WebClient;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseProductTaskConf")
public class BrowseProductTaskConf extends CrawlTaskConf implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(BrowseProductTaskConf.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	//configurable values
	private String startURL;
	private String productType;
	//following transient attributes exists on the taskMgr's taskConf cache, but not in the db
	transient String[] skipUrls;
	transient boolean enableJS;
	
	//methods
	public BrowseProductTaskConf(){
	}
	
	public String toString(){
		return super.toString() + ", startURL:" + startURL;
	}
	
	@Override
	public BrowseProductTaskConf clone(ClassLoader classLoader){
		try {
			Class<?> taskClass = Class.forName(BrowseProductTaskConf.class.getName(), true, classLoader);
			BrowseProductTaskConf te = (BrowseProductTaskConf) taskClass.newInstance();
			copy(te);
			te.setStartURL(this.getStartURL());
			te.setProductType(this.getProductType());
			te.skipUrls = this.skipUrls;
			te.enableJS = this.enableJS;
			return te;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
	
	public String genId(){
		this.setId(super.genId() + "|" + getStartURL());
		return this.getId();
	}

	@Override
	public void setUp(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params) {
		super.setUp(tasks, pluginClassLoader, params);
		
		BrowseDetailType bdt = getBrowseDetailTask(this.getName()).getBrowsePrdTaskType();
		bdt.isMonitorPrice();
		//start url only used by product list analyze generate a list of product analyze by API
		//since the start url is get from xml file, so un-escape is needed,
		//Just for display and id purpose, since for list type parameters, the value is wrong, it should be generated into a list of start url
		//this.startURL= (String) CrawlTaskEval.eval(bdt.getBaseBrowseTask().getStartUrl(), params);
		this.startURL = bdt.getBaseBrowseTask().getStartUrl().getValue();
		this.productType = tasks.getProductType();
		if (tasks.getSkipUrl()!=null){
			int size = tasks.getSkipUrl().size();
			this.skipUrls = new String[size];
			this.skipUrls = tasks.getSkipUrl().toArray(this.skipUrls);
		}
		this.enableJS = bdt.getBaseBrowseTask().isEnableJS();
		for (ParamType pt: bdt.getBaseBrowseTask().getParam()){
			if (pt.getValue()!=null){
				//has default value, put in the paramsMap
				if (VarType.STRING==pt.getType()){
					putParam(pt.getName(), pt.getValue());
				}else if (VarType.INT == pt.getType()){
					putParam(pt.getName(), Integer.parseInt(pt.getValue()));
				}else if (VarType.BOOLEAN == pt.getType()){
					putParam(pt.getName(), Boolean.parseBoolean(pt.getValue()));
				}else if (VarType.DATE == pt.getType()){
					try{
						putParam(pt.getName(), sdf.parse(pt.getValue()));
					}catch(Exception e){
						logger.error("", e);
					}
				}else{
					logger.error(String.format("default value type not support for param : %s", pt.getName()));
				}
			}else{
				if (VarType.INT == pt.getType()){
					putParam(pt.getName(), -1);
				}else if (VarType.BOOLEAN == pt.getType()){
					putParam(pt.getName(), true);
				}else {
					putParam(pt.getName(), null);
				}
			}
		}
	}
	
	private static void addFullUrl(ParsedBrowsePrd pbpTemplate, Map<String, Object> singleValueParams, 
			List<String> startUrlList, List<String> cachePageList){
		String fullUrl = (String) TaskUtil.eval(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl(), singleValueParams);
		startUrlList.add(fullUrl);
		if (pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getCachePage()!=null){
			String fullCachePage = (String) TaskUtil.eval(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getCachePage(), singleValueParams);
			cachePageList.add(fullCachePage);
		}
	}
	/**
	 * reusable browse detailed product and add to store if updated
	 * @param task: task instance
	 * @param cconf: the whole crawl conf
	 * @param ctconf: the crawl task conf, defines which is the category, list analysis implementation
	 * @param wc: the web client
	 * @param storeId: the storeId this product belongs to
	 * @param catId: the category this product belongs to
	 * @param startUrl
	 * @param taskName:
	 * @return
	 * @throws InterruptedException
	 */
	public static List<CrawledItem> browseProduct(BrowseProductTaskConf task, CrawlConf cconf, WebClient wc, String storeId, 
			String catId, String taskName, Map<String, Object> params, boolean retcsv, Date crawlDateTime, boolean addToDB) 
			throws InterruptedException{
		CrawledItem ci = null;
		Product lastProduct = null;
		Product thisProduct = null;
		ParsedBrowsePrd pbpTemplate = task.getBrowseDetailTask(taskName);
		BrowseDetailType bdt = pbpTemplate.getBrowsePrdTaskType();
		DataStoreManager dsManager = null;
		if (bdt.getBaseBrowseTask().getDsm()!=null){
			dsManager = cconf.getDsm(bdt.getBaseBrowseTask().getDsm());
		}else{
			dsManager = cconf.getDefaultDsm();
		}
		//startUrl may contains parameters needs to be converted to startUrlWithValue (maybe multiple) by filling the ParamValueMap
		List<String> startUrlList = new ArrayList<String>();
		List<String> cachePageList = new ArrayList<String>(); //if configured to save cache pages
		List<ParamType> plist = pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getParam();
		if (plist!=null && plist.size()>0){
			List<ParamType> listTypeParams = new ArrayList<ParamType>();
			Map<String, Object> singleValueParams = new HashMap<String, Object>();
			for (ParamType pv:plist){
				if (pv.getType()!=VarType.LIST){
					Object val = params.get(pv.getName());
					singleValueParams.put(pv.getName(), params.get(pv.getName()));
				}else{
					listTypeParams.add(pv);
				}
			}
			if (listTypeParams.size()>=1){
				//for list type parameter, it will end up in list of start url (each url maps to a cachepage if configured)
				ParamType lpvt = listTypeParams.get(0);
				if (listTypeParams.size()>1){
					logger.warn(String.format("only 1 list param supported. we got: %d, so we only treat %s", 
						listTypeParams.size(), lpvt.getName()));
				}
				Object objVal = params.get(lpvt.getName());
				if (objVal == null){
					logger.error(String.format("param %s has no value.", lpvt.getName()));
				}
				if (objVal instanceof List){
					List<Object> listVal = (List<Object>) objVal;
					for (Object sepParam: listVal){
						singleValueParams.put(lpvt.getName(), sepParam);
						addFullUrl(pbpTemplate, singleValueParams, startUrlList, cachePageList);
					}
				}else{
					logger.error(String.format("list typed param %s has not list value %s.", lpvt.getName(), objVal));
				}
			}else{
				addFullUrl(pbpTemplate, singleValueParams, startUrlList, cachePageList);
			}
		}else{
			//no parameter, just a string
			startUrlList.add(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl().getValue());
		}
		
		//cache page if needed
		if (cachePageList.size()>0){
			for (int i=0; i<cachePageList.size(); i++){
				String startUrl = startUrlList.get(i);
				String cachePage = cachePageList.get(i);
				CrawlUtil.downloadPage(cconf, startUrl, cachePage);
			}
		}
		
		List<CrawledItem> cilist = new ArrayList<CrawledItem>();
		if (pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute().size()!=0){
			//start browsing
			for (String startUrl:startUrlList){
				String prdId = ProductListAnalyzeUtil.getInternalId(startUrl, task, pbpTemplate);
				if (prdId!=null && !"".equals(prdId)){			
					if (dsManager!=null){
						lastProduct = (Product) dsManager.getCrawledItem(prdId, storeId, Product.class);
						ci = lastProduct;
					}
					if (lastProduct != null && !CompareUtil.ObjectDiffers(crawlDateTime, lastProduct.getId().getCreateTime()) && lastProduct.isCompleted()){
						logger.info("last product has the same crawl time and is complete, skip browsing.");
					}else{
						//add new product and price
						thisProduct = cconf.getProductInstance(task.getTasks().getProductType());
						CrawledItemId pid = new CrawledItemId(prdId, storeId, crawlDateTime);
						thisProduct.setId(pid);
						thisProduct.addCat(catId);
						ci = cconf.getPa().addProduct(wc, startUrl, thisProduct, null, task, pbpTemplate, cconf, retcsv, addToDB);
					}
					cilist.add(ci);
				}else{
					logger.error(String.format("prdId is null for task: %s, startUrl: %s", task.getName(), startUrl));
				}
			}
		}
		return cilist;
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		//adding the runtime params
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(getName());
		this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
		WebClient wc = CrawlUtil.getWebClient(cconf, taskTemplate.skipUrls, taskTemplate.enableJS);
		
		browseProduct(this, cconf, wc, this.getStoreId(), null, this.getName(), this.getParamMap(), false, this.getStartDate(), true);
	
		return new ArrayList<Task>();
	}
	
	@Override
	public List<CrawledItem> runMyselfWithOutput(Map<String, Object> params, boolean addToDB) throws InterruptedException{
		//adding the runtime params
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(getName());
		if (taskTemplate!=null){
			this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
			WebClient wc = CrawlUtil.getWebClient(cconf, taskTemplate.skipUrls, taskTemplate.enableJS);
			return browseProduct(this, cconf, wc, this.getStoreId(), null, this.getName(), this.getParamMap(), true, this.getStartDate(), addToDB);
		}else{
			logger.error(String.format("task %s not found in config.", getName()));
			return null;
		}
	}
	
	//setter and getter
	public String getStartURL() {
		return startURL;
	}
	public void setStartURL(String startURL) {
		this.startURL = startURL;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
}
