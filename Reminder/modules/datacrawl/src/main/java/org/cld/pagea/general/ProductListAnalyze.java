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
import org.cld.datacrawl.mgr.TargetPrdInvoke;
import org.cld.datacrawl.mgr.impl.CrawlTaskEval;
import org.cld.datacrawl.pagea.ProductListAnalyzeInf;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.cld.util.StringUtil;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamValueType;
import org.xml.taskdef.ScopeType;
import org.xml.taskdef.SubListType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;
import org.xml.mytaskdef.IdUrlMapping;
import org.xml.mytaskdef.ParsedBrowsePrd;

public class ProductListAnalyze implements ProductListAnalyzeInf {

	private static Logger logger =  LogManager.getLogger(ProductListAnalyze.class);
	private CrawlConf cconf;
	public static final String LIST_PARAM_SEP=",";
	
	@Override
	public String[] getListPageVerifyXPaths(Task task) {
		BrowseCatType bc = task.getLeafBrowseCatTask();
		String itemListXpath = bc.getSubItemList().getItemList().getValue();
		return new String[]{itemListXpath};
	}

	@Override
	public void setCConf(CrawlConf cconf){
		this.cconf = cconf;
	}
	@Override
	public String[] getItemVerfiyXPaths(Task task) {
		return null;
	}
	
	@Override
	public List<DomNode> getItemList(HtmlPage listPage, Task task) {
		BrowseCatType bc = task.getLeafBrowseCatTask();
		ValueType itemListXpathVT = bc.getSubItemList().getItemList();
		if (itemListXpathVT.getToType()==null){
			itemListXpathVT.setToType(VarType.LIST);
		}
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

	@Override
	public List<TargetPrdInvoke> getTargetPrdInovokeList(DomNode itemSummary, HtmlPage page, Task task, Product dummyProduct) {
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
					TargetPrdInvoke spo = new TargetPrdInvoke(taskName, 
							pbptTemplate.getBrowsePrdTaskType().getBaseBrowseTask().getStartUrl(), paramValueMap);
					targetPrdInvokeList.add(spo);
				}
			}
		}else{//
			if (slt.getItemFullUrl()==null){
				fullUrlAnchor = itemSummary;
			}else{
				fullUrlAnchor = itemSummary.getFirstByXPath(slt.getItemFullUrl());
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

	@Override
	public String getInternalId(String fullUrl, Task task, ParsedBrowsePrd pbptTemplate) {
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
					}else{
						logger.error(String.format("from type not supported for id in user attribute: %s", nvt.getValue().getFromType()));
					}
				}
			}
			logger.error(String.format("id not found in user attributes for url: %s", fullUrl));
			return null;
		}
	}
	
	@Override
	public double getCurPrice(DomNode bookSummary) {
		//means no price found on the list page
		return -1;
	}

	@Override
	public String getPromId(DomNode summary) {
		return null;
	}

	@Override
	public void setAttributes(DomNode summary, Product product, Task t) 
			throws InterruptedException{
		BrowseCatType bc = t.getLeafBrowseCatTask();
		ValueType nameVT = bc.getSubItemList().getName();
		if (nameVT!=null){
			product.setName((String)CrawlTaskEval.eval(summary, nameVT, cconf, t.getParamMap()));
		}
		
		CrawlTaskEval.setUserAttributes(summary, bc.getSubItemList().getUserAttribute(), product.getParamMap(), cconf, t.getParamMap());
	}
}
