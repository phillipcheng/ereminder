package org.cld.datacrawl.task;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.datastore.api.DataStoreManager;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.RunType;
import org.cld.taskmgr.entity.Task;
import org.cld.util.ScriptEngineUtil;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.CrawledItemId;
import org.cld.util.entity.Product;
import org.w3c.dom.Node;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.CsvTransformType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.ParamValueType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;

@Entity
@DiscriminatorValue("org.cld.datacrawl.task.BrowseProductTaskConf")
public class BrowseProductTaskConf extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(BrowseProductTaskConf.class);
	//configurable values
	private String startURL;
	private String productType;
	private RunType rt;//
	private String[] skipUrls;
	private boolean enableJS;
	private String siteconfid;//for cancel task
	
	//methods
	public BrowseProductTaskConf(){
	}
	
	public String toString(){
		return super.toString() + ", startURL:" + startURL;
	}
	
	@Override
	public ParsedTasksDef initParsedTaskDef(){
		ParsedTasksDef ptd = TaskMgr.getParsedTasksDef(storeId);
		if (ptd!=null){
			this.setParsedTaskDef(ptd);
		}else{
			logger.error(String.format("site %s not found in taskMgr.", storeId));
		}
		return ptd;
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
	private static String getDsm(BrowseDetailType bdt, CrawlConf cconf){
		if (bdt.getBaseBrowseTask().getDsm()!=null){
			return bdt.getBaseBrowseTask().getDsm();
		}else{
			return cconf.getCrawlDsManagerValue().get(0);
		}
	}
	private static final SimpleDateFormat ssdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private String paramToString(Map<String, Object> paramMap){
		StringBuffer sb = new StringBuffer();
		for (Object v: paramMap.values()){
			if (!(v instanceof List) &&
					!(v instanceof Node)
					){
				String str=null;
				if (v instanceof Date){
					str = ssdf.format((Date)v);
				}else{
					str = v.toString();
				}
				sb.append(str).append("_");
			}
		}
		String finalStr = sb.toString().replaceAll(".", "_");
		return finalStr;
	}
	
	@Override
	public String getOutputDir(Map<String, Object> paramMap, TaskConf tconf){
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
				if (paramMap.containsKey(AbstractCrawlItemToCSV.FN_MARKETID)){//for stock
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
				}else{//generally, using storeId, followed by the parameters
					od.append(storeId);
					od.append("/");
					String paramStr = paramToString(paramMap);
					od.append(paramStr);
				}
				return od.toString();
			}else{
				return null;
			}
		}else{
			return null;
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
	
	//startUrlList can be a pageList or url list
	private static void addFullUrl(ParsedBrowsePrd pbpTemplate, Map<String, Object> singleValueParams, 
			List<Object> startUrlList, List<String> cachePageList){
		ValueType startUrlVT = pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl();
		Object fullUrl = null;
		if (singleValueParams.containsKey(startUrlVT.getValue())){
			fullUrl = singleValueParams.get(startUrlVT.getValue());
		}else{
			fullUrl = TaskUtil.eval(startUrlVT, singleValueParams);
		}
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
	 * @param storeId: the storeId this product belongs to
	 * @param catId: the category this product belongs to
	 * @param taskName:
	 * @param params
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static TaskResult browseProduct(BrowseProductTaskConf task, CrawlConf cconf, String storeId, 
			String taskName, Map<String, Object> params, Date crawlDateTime, boolean addToDB, 
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
		List<Object> startUrlList = new ArrayList<Object>();//list of startUrl, can be pagelist or urllist
		List<String> cachePageList = new ArrayList<String>(); //if configured to save cache pages
		List<ParamType> plist = pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getParam();
		Map<String, Object> singleValueParams = new HashMap<String, Object>();
		if (plist!=null && plist.size()>0){//expand starturl using parameters, esp list type params
			List<ParamType> listTypeParams = new ArrayList<ParamType>();
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
					logger.warn(String.format("only 1 list param supported. we got: %d, so we only treat %s", listTypeParams.size(), lpvt.getName()));
				}
				Object objVal = params.get(lpvt.getName());
				if (objVal == null){
					logger.warn(String.format("param %s has no value.", lpvt.getName()));
				}else{
					if (objVal instanceof List){
						List<Object> listVal = (List<Object>) objVal;
						for (Object sepParam: listVal){
							singleValueParams.put(lpvt.getName(), sepParam);
							addFullUrl(pbpTemplate, singleValueParams, startUrlList, cachePageList);
						}
					}else{
						logger.error(String.format("list typed param %s has not list value %s.", lpvt.getName(), objVal));
					}
					singleValueParams.remove(lpvt.getName());//remove the added list type
				}
			}else{
				addFullUrl(pbpTemplate, singleValueParams, startUrlList, cachePageList);
			}
		}
		if (startUrlList.size()==0){//if failed to generate any startUrl, using the startUrl in the task instance, then the task template
			if (task.getStartURL()!=null || !"".equals(task.getStartURL())){
				startUrlList.add(task.getStartURL());
			}else{
				startUrlList.add(pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl().getValue());
			}
		}

		//gen bpt if startUrl is multiple (>0) and each is a string (not page)
		if (startUrlList.size()>1){
			if (startUrlList.get(0) instanceof String){
				BrowseProductTaskConf t = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(taskName);
				List<Task> tl = new ArrayList<Task>();
				for (int i=0; i<startUrlList.size(); i++){
					String startUrl = (String) startUrlList.get(i);
					t = t.clone(cconf.getPluginClassLoader());
					t.putAllParams(singleValueParams);
					t.setStartURL(startUrl);
					t.genId();
					tl.add(t);
				}
				return new TaskResult(tl, null);
			}
		}
		
		//cache page if needed
		if (cachePageList.size()>0){
			for (int i=0; i<cachePageList.size(); i++){
				String startUrl = (String) startUrlList.get(i);//cache only start url
				String cachePage = cachePageList.get(i);
				CrawlUtil.downloadPage(cconf, startUrl, cachePage);
			}
		}
		
		TaskResult ftr = new TaskResult();
		if (pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute().size()!=0){
			//start browsing
			for (Object startUrl:startUrlList){
				CrawledItem ci = null;
				boolean needCrawl = true;
				Product lastProduct = null;
				String prdId;
				if (startUrl instanceof String){
					prdId= (String)startUrl;//prd id is url for string typed start url.
				}else{
					AttributeType idAvt = pbpTemplate.getPdtAttrMap().get("id");
					if (idAvt!=null){
						prdId = (String) CrawlTaskEval.eval((DomNode)startUrl, idAvt.getValue(), cconf, params);
					}else{
						logger.error(String.format("for page typed start url, must has id attribute define. %s", startUrl));
						break;
					}
				}
				logger.info(String.format("prd id:%s", prdId));
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
					BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(task.getName());
					CsvTransformType csvtrans = null;
					if (btt!=null){
						csvtrans = btt.getCsvtransform();
					}
					if (startUrl instanceof String){
						WebClient wc = null;
						try {
							wc = CrawlUtil.getWebClient(cconf, taskTemplate.skipUrls, taskTemplate.enableJS);
							ci = cconf.getPa().addProduct(wc, startUrl, thisProduct, null, task, pbpTemplate, cconf, addToDB, 
									csvtrans, hdfsByIdOutputMap, context, mos);
						}catch(Exception e){
							logger.error("", e);
						}finally{
							try {
								CrawlUtil.closeWebClient(wc);
							}catch(Throwable t){
								logger.error("close web client error caught, ignore.", t);
							}
						}
					}else{
						ci = cconf.getPa().addProduct(null, startUrl, thisProduct, null, task, pbpTemplate, cconf, addToDB, 
								csvtrans, hdfsByIdOutputMap, context, mos);
					}
				}
				ftr.addCI(ci);
				if (ci.isGoNext()){
					TaskInvokeType tit = bdt.getBaseBrowseTask().getNextTask().getInvokeTask();
					String callTaskName = tit.getToCallTaskName();
					ParsedBrowsePrd pbptTemplate = task.getBrowseDetailTask(callTaskName);
					if (pbptTemplate==null){
						logger.error(String.format("invoke task name: %s not found in site %s", callTaskName, task.getTasks().getStoreId()));
					}
					//pass ci's attribute specified in the tit's param to the next task's param
					for (ParamValueType pvt:tit.getParam()){
						if (ci.getParam(pvt.getParamName())!=null){
							params.put(pvt.getParamName(), ci.getParam(pvt.getParamName()));
						}
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
					TaskResult tr = browseProduct(t, cconf, storeId, callTaskName, t.getParamMap(), 
							crawlDateTime, addToDB, btt, hdfsByIdOutputMap, context, mos);
					ftr.addTR(tr);
				}
			}
		}else{
			logger.error(String.format("no user attributes defined for task:%s. so no crawling.", 
					pbpTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getTaskName()));
		}
		
		return ftr;
	}
	
	@Override
	public TaskResult runMyself(Map<String, Object> params, boolean addToDB, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		BrowseTaskType btt = null;
		if (getParsedTaskDef()!=null){
			btt = getBrowseTask(getName());
			logger.info("btt:" + btt.getTaskName());
		}
		if (params!=null){
			this.putAllParams(params);
		}
		CrawlConf cconf = (CrawlConf) this.getParamMap().get(TaskMgr.TASK_RUN_PARAM_CCONF);
		BrowseProductTaskConf taskTemplate = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(getName());
		if (taskTemplate!=null){
			Map<String, BufferedWriter> hdfsByIdOutputMap = new HashMap<String, BufferedWriter>();
			this.setParsedTaskDef(taskTemplate.getParsedTaskDef());
			try{
				TaskResult tr = browseProduct(this, cconf, this.getStoreId(), this.getName(), this.getParamMap(), 
						this.getStartDate(), addToDB, btt, hdfsByIdOutputMap, context, mos);
				return tr;
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
		return null;
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
	public RunType getRt() {
		return rt;
	}
	public void setRt(RunType rt) {
		this.rt = rt;
	}
	public String getSiteconfid() {
		return siteconfid;
	}
	public void setSiteconfid(String siteconfid) {
		this.siteconfid = siteconfid;
	}
}
