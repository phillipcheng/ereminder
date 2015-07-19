package org.cld.pagea.general;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.datacrawl.mgr.TargetPrdInvoke;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.ScriptEngineUtil;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.ParamValueType;
import org.xml.taskdef.ScopeType;
import org.xml.taskdef.SubListType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;
import org.xml.mytaskdef.IdUrlMapping;
import org.xml.mytaskdef.ParsedBrowsePrd;

public class ProductListAnalyzeUtil {

	private static Logger logger =  LogManager.getLogger(ProductListAnalyzeUtil.class);
	public static final String LIST_PARAM_SEP=",";
	
	
	public static String[] getListPageVerifyXPaths(Task task) {
		BrowseCatType bc = task.getLeafBrowseCatTask();
		String itemListXpath = bc.getSubItemList().getItemList().getValue();
		return new String[]{itemListXpath};
	}

	
	public static List<DomNode> getItemList(HtmlPage listPage, Task task, CrawlConf cconf) {
		BrowseCatType bc = task.getLeafBrowseCatTask();
		ValueType itemListXpathVT = bc.getSubItemList().getItemList();
		if (itemListXpathVT.getToType()==null){
			itemListXpathVT.setToType(VarType.LIST);
		}
		itemListXpathVT.setToEntryType(VarType.HTML_ELEMENT);
		try {
			List<DomNode> itemList;
			itemList = (List<DomNode>) CrawlTaskEval.eval(listPage, itemListXpathVT, cconf, task.getParamMap());
			if (itemList.size()==0){
				logger.error(String.format("item list of xpath %s on page %s not found any items.",
						itemListXpathVT.getValue(), listPage.getUrl().toExternalForm()));
			}
			return itemList;
		} catch (InterruptedException e) {
			logger.error("no interrupted exception is expected.", e);
			return null;
		}
	}

	
	public static List<TargetPrdInvoke> getTargetPrdInovokeList(DomNode itemSummary, HtmlPage page, Task task, Product dummyProduct) {
		List<TargetPrdInvoke> targetPrdInvokeList = new ArrayList<TargetPrdInvoke>();
		
		BrowseCatType bc = task.getLeafBrowseCatTask();
		DomNode fullUrlAnchor = null;
		SubListType slt = bc.getSubItemList();
		
		//using the start url in the prd task with parameters passed
		List<TaskInvokeType> titList = slt.getTaskInvokes();
		if (titList.size()>0){
			for (int i=0; i<titList.size(); i++){
				TaskInvokeType tit = titList.get(i);
				List<ParamValueType> pvlist = tit.getParam();
				String taskName = tit.getToCallTaskName();
				ParsedBrowsePrd pbptTemplate = null;
				if (titList.size()==1){
					pbptTemplate = task.getParsedTaskDef().getDefaultBrowseDetailTask();
				}else{
					pbptTemplate = task.getBrowseDetailTask(taskName);
					if (pbptTemplate==null){
						logger.error(String.format("invoke task name: %s not found in site %s", taskName, task.getTasks().getStoreId()));
						return targetPrdInvokeList;
					}
				}
				if (pvlist!=null && pvlist.size()>0){
					Map<String, Object> paramValueMap = new HashMap<String, Object>();
					for (ParamValueType pv:pvlist){
						if (pv.getType()!=VarType.LIST){
							if (pv.getScope()==ScopeType.CONST){
								paramValueMap.put(pv.getParamName(), pv.getValue());
							}else if (pv.getScope()==ScopeType.ATTRIBUTE){
								paramValueMap.put(pv.getParamName(), dummyProduct.getParam(pv.getValue()));
							}else{
								logger.error(String.format("scope type not supported for: %s", pv));
							}
						}else{
							List<Object> listVal = new ArrayList<Object>();
							String[] sepParams = pv.getValue().split(LIST_PARAM_SEP);
							for (String paramVal:sepParams){
								if (pv.getScope()==ScopeType.CONST){
									listVal.add(paramVal);
								}else if (pv.getScope()==ScopeType.ATTRIBUTE){
									listVal.add(dummyProduct.getParam(paramVal));
								}else{
									logger.error(String.format("scope type not supported for: %s", pv));
								}
							}
							paramValueMap.put(pv.getParamName(), listVal);
						}
					}
					String startUrl = (String) TaskUtil.eval(pbptTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl(), paramValueMap);
					TargetPrdInvoke spo = new TargetPrdInvoke(taskName, startUrl, paramValueMap);
					targetPrdInvokeList.add(spo);
				}
			}
		}else{//
			if (slt.getItemFullUrl()==null){
				fullUrlAnchor = itemSummary;
			}else{
				fullUrlAnchor = itemSummary.getFirstByXPath(slt.getItemFullUrl().getValue()); //TODO
			}
			
			if (fullUrlAnchor !=null){
				if (fullUrlAnchor instanceof HtmlAnchor){
					HtmlAnchor ha = (HtmlAnchor)fullUrlAnchor;
					try {
						String fullUrl = page.getFullyQualifiedUrl(ha.getHrefAttribute()).toExternalForm();
						TargetPrdInvoke spo = new TargetPrdInvoke(fullUrl);
						targetPrdInvokeList.add(spo);
					} catch (MalformedURLException e) {
						logger.error("", e);
					}
				}else{
					logger.error(String.format("itemSummary type:%s is not supported.", fullUrlAnchor.asXml()));
				}
			}
		}
		
		return targetPrdInvokeList;
	}

	
	public static String getInternalId(String fullUrl, Task task, ParsedBrowsePrd pbptTemplate) {
		IdUrlMapping ium = pbptTemplate.getIum();
		if (ium!=null){
			Matcher m = ium.match(fullUrl);
			if (m.matches()){
				int idx = ium.getIdIdx();
				String id = m.group(idx);
				return id.replace('/', '-');
			}else{
				logger.error(String.format("prd id url:%s doesnot match regexp defined:%s", fullUrl, ium));
				return null;
			}
		}else{
			//find the id from user attributes
			for (AttributeType nvt: pbptTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute()){
				if (IdUrlMapping.ID_KEY.equals(nvt.getName())){
					String id=null;
					if (nvt.getValue().getFromScope()==ScopeType.PARAM){
						id = (String) task.getParamMap().get(nvt.getValue().getValue());
						return id;
					}else if (nvt.getValue().getFromType()==VarType.EXPRESSION){
						Object ret = ScriptEngineUtil.eval(nvt.getValue().getValue(), VarType.STRING, task.getParamMap());
						return (String)ret;
					}else{
						logger.error(String.format("from|scope type not supported for id in user attribute: %s|%s", 
								nvt.getValue().getFromType(), nvt.getValue().getFromScope()));
					}
				}
			}
			logger.error(String.format("id not found in user attributes for url: %s", fullUrl));
			return null;
		}
	}
	
	public static void setAttributes(DomNode summary, Product product, Task t, CrawlConf cconf) 
			throws InterruptedException{
		BrowseCatType bc = t.getLeafBrowseCatTask();
		ValueType nameVT = bc.getSubItemList().getName();
		if (nameVT!=null){
			product.setName((String)CrawlTaskEval.eval(summary, nameVT, cconf, t.getParamMap()));
		}
		
		CrawlTaskEval.setUserAttributes(summary, bc.getSubItemList().getUserAttribute(), product.getParamMap(), cconf, t.getParamMap());
	}
}
