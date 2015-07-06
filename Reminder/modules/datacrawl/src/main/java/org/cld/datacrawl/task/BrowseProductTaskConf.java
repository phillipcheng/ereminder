package org.cld.datacrawl.task;

import java.io.Serializable;
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
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.pagea.general.ProductListAnalyzeUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.StringUtil;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.WebClient;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseProductTaskConf")
public class BrowseProductTaskConf extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(BrowseProductTaskConf.class);
	
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
		//since the start url is get from xml file, so un-escape is needed
		this.startURL=StringEscapeUtils.unescapeXml(bdt.getBaseBrowseTask().getStartUrl());
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
				this.putParam(pt.getName(), pt.getValue());
			}
		}
	}
	
	private static void addFullUrl(ParsedBrowsePrd pbpTemplate, Map<String, Object> singleValueParams, 
			List<String> startUrlList, List<String> cachePageList){
		String fullUrl = StringUtil.fillParams(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl(), 
				singleValueParams, "[", "]");
		startUrlList.add(fullUrl);
		if (pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getCachePage()!=null){
			String fullCachePage = StringUtil.fillParams(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getCachePage(), 
					singleValueParams, "[", "]");
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
	 * @param
	 * @return
	 * @throws InterruptedException
	 */
	public static void browseProduct(BrowseProductTaskConf task, CrawlConf cconf, WebClient wc, String storeId, 
			String catId, String taskName, Map<String, Object> params) 
			throws InterruptedException{
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
					if (val!=null){
						singleValueParams.put(pv.getName(), params.get(pv.getName()));
					}else{
						logger.error(String.format("param %s has no value.", pv.getName()));
					}
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
			startUrlList.add(task.getStartURL());
		}
		
		//cache page if needed
		if (cachePageList.size()>0){
			for (int i=0; i<cachePageList.size(); i++){
				String startUrl = startUrlList.get(i);
				String cachePage = cachePageList.get(i);
				CrawlUtil.downloadPage(cconf, startUrl, cachePage, storeId);
			}
		}
		//start browsing
		for (String startUrl:startUrlList){
			String internalId = ProductListAnalyzeUtil.getInternalId(startUrl, task, pbpTemplate);
			if (internalId!=null && !"".equals(internalId)){			
				//lastProduct = CrawlPersistMgr.getProduct(cconf.getCrawlSF(), internalId);
				if (dsManager!=null){
					lastProduct = (Product) dsManager.getCrawledItem(internalId, storeId, Product.class);
				}
				if (lastProduct != null){
					//belong to more categories
					if (catId != null)
						lastProduct.addCat(catId);
					
					if (!lastProduct.isCompleted()){//re-crawl product details if not complete
						thisProduct = cconf.getProductInstance(task.getTasks().getProductType());
						CrawledItemId pid = new CrawledItemId(internalId, storeId, new Date());
						thisProduct.setId(pid);
						cconf.getPa().addProduct(wc, startUrl, thisProduct, lastProduct, task, pbpTemplate, cconf);
					}
					//get 1st browse prd task's monitor price definition
					if (task.getBrowseDetailTask(null).getBrowsePrdTaskType().isMonitorPrice()){
						//
					}
				}else{
					//add new product and price
					thisProduct = cconf.getProductInstance(task.getTasks().getProductType());
					CrawledItemId pid = new CrawledItemId(internalId, storeId, new Date());
					thisProduct.setId(pid);
					thisProduct.addCat(catId);
					cconf.getPa().addProduct(wc, startUrl, thisProduct, null, task, pbpTemplate, cconf);
					//get 1st browse prd task's monitor price definition
					if (task.getBrowseDetailTask(null).getBrowsePrdTaskType().isMonitorPrice()){
						//
					}
				}
			}
		}
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		//adding the runtime params
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(CrawlClientNode.TASK_RUN_PARAM_CCONF);
		
		BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(getName());
		this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
		WebClient wc = CrawlUtil.getWebClient(cconf, taskTemplate.skipUrls, taskTemplate.enableJS);
		
		browseProduct(this, cconf, wc, this.getStoreId(), null, this.getName(), this.getParamMap());
	
		return new ArrayList<Task>();
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
