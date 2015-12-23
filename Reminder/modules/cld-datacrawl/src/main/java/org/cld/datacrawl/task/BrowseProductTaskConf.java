package org.cld.datacrawl.task;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.mgr.BinaryBoolOpEval;
import org.cld.datastore.api.DataStoreManager;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.pagea.general.ProductListAnalyzeUtil;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.CompareUtil;
import org.cld.util.ScriptEngineUtil;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.CrawledItemId;
import org.cld.util.entity.Product;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.CsvOutputType;
import org.xml.taskdef.CsvTransformType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.WebClient;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseProductTaskConf")
public class BrowseProductTaskConf extends CrawlTaskConf implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(BrowseProductTaskConf.class);
	//configurable values
	private String startURL;
	private String productType;
	private String catId;
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

	//using the task param map to evaluate expression type params
	public static void evalParams(Task t, BrowseDetailType bdt){
		for (ParamType pt: bdt.getBaseBrowseTask().getParam()){
			if (pt.getValue()!=null){
				if (pt.getType()==VarType.EXPRESSION){
					String ret = (String)ScriptEngineUtil.eval(pt.getValue(), VarType.STRING, t.getParamMap());
					t.putParam(pt.getName(), ret);
					logger.info(String.format("exp: %s eval to %s", pt.getValue(), ret));
				}
			}
		}
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
		if (bdt.getBaseBrowseTask().getStartUrl()!=null){
			this.startURL = bdt.getBaseBrowseTask().getStartUrl().getValue();
		}
		this.productType = tasks.getProductType();
		if (tasks.getSkipUrl()!=null){
			int size = tasks.getSkipUrl().size();
			this.skipUrls = new String[size];
			this.skipUrls = tasks.getSkipUrl().toArray(this.skipUrls);
		}
		this.enableJS = bdt.getBaseBrowseTask().isEnableJS();
		for (ParamType pt: bdt.getBaseBrowseTask().getParam()){
			if (pt.getValue()!=null){
				Object value = null;
				if (pt.getType()==VarType.EXPRESSION){
					value = ScriptEngineUtil.eval(pt.getValue(), VarType.STRING, this.getParamMap());
				}else{
					value = TaskUtil.getValue(null, pt.getValue(), pt.getType());
				}
				putParam(pt.getName(), value);
			}else{//value is null, set default value for different types
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
	
	private static String getDsm(BrowseDetailType bdt, CrawlConf cconf){
		if (bdt.getBaseBrowseTask().getDsm()!=null){
			return bdt.getBaseBrowseTask().getDsm();
		}else{
			return cconf.getCrawlDsManagerValue().get(0);
		}
	}
	public static List<CrawledItem> browseProduct(BrowseProductTaskConf task, CrawlConf cconf, String storeId, 
			String catId, String taskName, Map<String, Object> params, boolean retcsv, Date crawlDateTime, boolean addToDB, 
			BrowseTaskType btt, Map<String, BufferedWriter> hdfsByIdOutputMap, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) 
			throws InterruptedException{
		Product thisProduct = null;
		ParsedBrowsePrd pbpTemplate = task.getBrowseDetailTask(taskName);
		BrowseDetailType bdt = pbpTemplate.getBrowsePrdTaskType();
		DataStoreManager dsManager = cconf.getDsm(getDsm(bdt, cconf));
		//re-evaluate params
		task.putAllParams(params);
		BrowseProductTaskConf.evalParams(task, bdt);
		//startUrl may contains parameters needs to be converted to startUrlWithValue (maybe multiple) by filling the ParamValueMap
		List<String> startUrlList = new ArrayList<String>();
		List<String> cachePageList = new ArrayList<String>(); //if configured to save cache pages
		List<ParamType> plist = pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getParam();
		if (plist!=null && plist.size()>0){//expand starturl using parameters, esp list type params
			List<ParamType> listTypeParams = new ArrayList<ParamType>();
			Map<String, Object> singleValueParams = new HashMap<String, Object>();
			for (ParamType pv:plist){
				if (pv.getType()!=VarType.LIST){
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
			if (task.getStartURL()!=null || !"".equals(task.getStartURL())){
				startUrlList.add(task.getStartURL());
			}else{
				startUrlList.add(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl().getValue());
			}
		}
		
		//cache page if needed
		if (cachePageList.size()>0){
			for (int i=0; i<cachePageList.size(); i++){
				String startUrl = startUrlList.get(i);
				String cachePage = cachePageList.get(i);
				CrawlUtil.downloadPage(cconf, startUrl, cachePage);
			}
		}
		
		List<CrawledItem> retcilist = new ArrayList<CrawledItem>();
		if (pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute().size()!=0){
			//start browsing
			for (String startUrl:startUrlList){
				CrawledItem ci = null;
				boolean needCrawl = true;
				Product lastProduct = null;
				String prdId = ProductListAnalyzeUtil.getInternalId(startUrl, task, pbpTemplate);
				if (CrawlConf.crawlDsManager_Value_Hbase.equals(bdt.getBaseBrowseTask().getDsm())){
					//check any update
					if (prdId!=null && !"".equals(prdId)){			
						if (dsManager!=null){
							lastProduct = (Product) dsManager.getCrawledItem(prdId, storeId, Product.class);
							ci = lastProduct;
						}
						if (lastProduct != null && lastProduct.isCompleted()){
							//TODO check last update time
							//!CompareUtil.ObjectDiffers(crawlDateTime, lastProduct.getId().getCreateTime()) 
							needCrawl= false;
							logger.info("last product is complete, skip browsing.");
						}
					}
				}
				logger.debug(String.format("needCrawl:%b", needCrawl));
				if (needCrawl){
					//add new product and price
					thisProduct = cconf.getProductInstance(task.getTasks().getProductType());
					CrawledItemId pid = new CrawledItemId(prdId, storeId, crawlDateTime); //Id might need to be filled back
					thisProduct.setId(pid);
					thisProduct.addCat(catId);
					BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(task.getName());
					WebClient wc = null;
					try {
						wc = CrawlUtil.getWebClient(cconf, taskTemplate.skipUrls, taskTemplate.enableJS);
						CsvTransformType csvtrans = null;
						if (btt!=null){
							csvtrans = btt.getCsvtransform();
						}
						ci = cconf.getPa().addProduct(wc, startUrl, thisProduct, null, task, pbpTemplate, cconf, retcsv, addToDB, 
								csvtrans, hdfsByIdOutputMap, context, mos);
						logger.debug(String.format("after add product, ci:%s", ci));
					}catch(Exception e){
						logger.error("", e);
					}finally{
						try {
							CrawlUtil.closeWebClient(wc);
						}catch(Throwable t){
							logger.error("close web client error caught, ignore.", t);
						}
					}
				}
				if (ci.isGoNext()){
					TaskInvokeType tit = bdt.getBaseBrowseTask().getNextTask().getInvokeTask();
					String callTaskName = tit.getToCallTaskName();
					ParsedBrowsePrd pbptTemplate = task.getBrowseDetailTask(callTaskName);
					if (pbptTemplate==null){
						logger.error(String.format("invoke task name: %s not found in site %s", callTaskName, task.getTasks().getStoreId()));
					}
					//check tit's param only pass the value to the param which has no init value
					List<ParamType> ptl = pbptTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getParam();
					for (ParamType pt:ptl){
						if (pt.getValue()!=null){
							params.remove(pt.getName());
						}
					}
					BrowseProductTaskConf t = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(callTaskName);
					t = t.clone(cconf.getPluginClassLoader());
					t.putAllParams(params);
					t.genId();
					List<CrawledItem> cilist1 = browseProduct(t, cconf, storeId, catId, callTaskName, t.getParamMap(), 
							retcsv, crawlDateTime, addToDB, btt, hdfsByIdOutputMap, context, mos);
					if (btt==null)
						retcilist.addAll(cilist1);
				}else{
					if (btt==null)
						retcilist.add(ci);
				}
			}
		}
		
		return retcilist;
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		//adding the runtime params
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(getName());
		this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
		browseProduct(this, cconf, this.getStoreId(), this.catId, this.getName(), this.getParamMap(), false, this.getStartDate(), true, null, null, null, null);
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
			return browseProduct(this, cconf, getStoreId(), this.catId, getName(), getParamMap(), true, getStartDate(), addToDB, null, null, null, null);
		}else{
			logger.error(String.format("task %s not found in config.", getName()));
			return null;
		}
	}

	@Override
	public boolean hasOutput(){
		return true;
	}
	@Override
	public void runMyselfAndOutput(Map<String, Object> params, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		BrowseTaskType btt = null;
		if (getParsedTaskDef()!=null){
			btt = getBrowseTask(getName());
			logger.info("btt:" + btt.getTaskName());
		}
		this.putAllParams(params);
		CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(getName());
		if (taskTemplate!=null){
			Map<String, BufferedWriter> hdfsByIdOutputMap = new HashMap<String, BufferedWriter>();
			try{
				this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
				browseProduct(this, cconf, getStoreId(), this.catId, getName(), getParamMap(), true, getStartDate(), true, 
						btt, hdfsByIdOutputMap, context, mos);
			}catch(Throwable t){
				logger.error("", t);
			}finally{
				for (BufferedWriter br: hdfsByIdOutputMap.values()){
					try{
						br.close();
					}catch(Exception e){
						logger.error("", e);
					}
				}
			}
		}else{
			logger.error(String.format("task %s not found in config.", getName()));
		}
	}
	
	@Override
	public String getOutputDir(Map<String, Object> paramMap){
		if (paramMap==null){
			paramMap = this.getParamMap();
		}
		if (getParsedTaskDef()==null){//not a browse task
			return null;
		}
		BrowseTaskType btt = getBrowseTask(getName());
		if (btt!=null){
			CsvTransformType csvtrans = btt.getCsvtransform();
			if (csvtrans!=null){//using the default
				//raw/[marketId]_[endDate]/storeid/[year]_[quarter]
				StringBuffer od = new StringBuffer("raw/");
				String marketId = (String) paramMap.get(AbstractCrawlItemToCSV.FN_MARKETID);
				String endDate = (String) paramMap.get(AbstractCrawlItemToCSV.FN_ENDDATE);
				//remove the ending endDate_delta
				String deltaSuffix = "_" + endDate + "_delta";
				if (marketId.endsWith(deltaSuffix)){
					marketId = marketId.substring(0, marketId.indexOf(deltaSuffix));
				}
				od.append(marketId);
				od.append("_");
				od.append(endDate);
				od.append("/");
				String storeId = (String) paramMap.get(AbstractCrawlItemToCSV.FN_STOREID);
				if (storeId==null){
					od.append(this.getStoreId());
				}else{
					od.append(storeId);
				}
				od.append("/");
				logger.info(String.format("output dir is %s.", od.toString()));
				if (paramMap.containsKey(AbstractCrawlItemToCSV.FN_YEAR)){
					int year = (int) paramMap.get(AbstractCrawlItemToCSV.FN_YEAR);
					od.append(year);
				}
				if (paramMap.containsKey(AbstractCrawlItemToCSV.FN_QUARTER)){
					int quarter = (int) paramMap.get(AbstractCrawlItemToCSV.FN_QUARTER);
					od.append("_");
					od.append(quarter);
				}
				return od.toString();
			}else{
				return null;
			}
		}else{
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
	public String getCatId() {
		return catId;
	}
	public void setCatId(String catId) {
		this.catId = catId;
	}
}
