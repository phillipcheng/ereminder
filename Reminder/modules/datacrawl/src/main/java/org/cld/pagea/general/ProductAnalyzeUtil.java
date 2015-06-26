package org.cld.pagea.general;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.ProductConf;
import org.cld.datacrawl.mgr.impl.CrawlTaskEval;
import org.cld.datacrawl.util.Content;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.VerifyPageByXPath;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.ScriptEngineUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.util.StringUtil;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.ClickType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

public class ProductAnalyzeUtil {
	
	private static Logger logger =  LogManager.getLogger(ProductAnalyzeUtil.class);
	
	
	public static String[] getPageVerifyXPaths(Task task, ParsedBrowsePrd taskDef) {
		if (taskDef.getBrowsePrdTaskType().getFirstPageClickStream()==null){
			List<String> xpathList = new ArrayList<String>();
			List<AttributeType> attrlist = taskDef.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute();
			for (AttributeType attr: attrlist){
				if (VarType.XPATH == attr.getValue().getFromType() && !attr.isOptional()){
					xpathList.add(attr.getValue().getValue());
				}
			}
			String[] array = xpathList.toArray(new String[xpathList.size()]);
			return array;
		}else{
			//when there is firstpage click, the xpath validation are needed after first page clicks
			return null;
		}
	}

	
	public static String getTitle(HtmlPage page, Task task, ParsedBrowsePrd taskDef, CrawlConf cconf) 
			throws InterruptedException {
		BrowseDetailType bd = taskDef.getBrowsePrdTaskType();
		ValueType nameVT = bd.getBaseBrowseTask().getItemName();
		if (nameVT!=null){
			Object value = CrawlTaskEval.eval(page, nameVT, cconf, task.getParamMap());
			return (String)value;
		}
		return null;	
	}
	
	//get the total page number from the 1st page
	private static int getTotalPage(Map<String, List<? extends DomNode>> pageMap, Task task, ParsedBrowsePrd taskDef, CrawlConf cconf) 
			throws InterruptedException {
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		ValueType tpVT = bdt.getTotalPage();
		if (tpVT!=null){
			Object value = CrawlTaskEval.eval(pageMap, tpVT, cconf, task.getParamMap());
			return CrawlTaskEval.getIntValue(value, tpVT.getValue());
		}else{
			//using page returned, override me if not the case, for example there is explicit page number there
			return -1;
		}
	}
	
	//if first page is different then start page, please override this
	private static Map<String,List<? extends DomNode>> getFirstPage(WebClient wc, HtmlPage prdPage, 
			ClickStreamType firstPageClicks, Task task, CrawlConf cconf) 
			throws InterruptedException{
		Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
		if (firstPageClicks==null || "".equals(firstPageClicks)){
			List<HtmlPage> pagelist= new ArrayList<HtmlPage>();
			pagelist.add(prdPage);
			pageMap.put(ConfKey.START_PAGE, pagelist);
			pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
		}else{//open this url via firstPageClicks
			if (firstPageClicks.getFinishCondition()==null){
				List<HtmlPage> pagelist= new ArrayList<HtmlPage>();
				pagelist.add(prdPage);
				pageMap.put(ConfKey.START_PAGE, pagelist);
				pageMap.put(ConfKey.CURRENT_PAGE, pagelist);//set current page
				HtmlUnitUtil.clickClickStream(firstPageClicks, pageMap, task.getParamMap(), cconf, new NextPage(prdPage.getUrl().toExternalForm()));
			}else{
				logger.error("click stream does not support finish condition now.");
			}
		}
		return pageMap;
	}
	
	private static boolean checkFinalPage(String finalExp, Map<String, Object> variables){
		if (finalExp==null)
			return false;
		else{
			Boolean bool = (Boolean)ScriptEngineUtil.eval(finalExp, VarType.BOOLEAN, variables);
			return bool.booleanValue();
		}
	}
	
	private static HtmlPage getNextPage(WebClient wc, HtmlPage curPage, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf) throws InterruptedException{
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		if (bdt.getNextPage()!=null){
			HtmlElement nextPageEle = null ;
			String url = null;
			
			DomNamespaceNode dnsn = curPage.getFirstByXPath(bdt.getNextPage());
			if (dnsn==null){
				logger.info(String.format("next page xpath %s not found on page %s.", bdt.getNextPage(), curPage.getUrl().toExternalForm()));
				return null;
			}
			if (dnsn instanceof HtmlAnchor){
				try {
					url = curPage.getFullyQualifiedUrl(((HtmlAnchor)dnsn).getHrefAttribute()).toExternalForm();
				} catch (MalformedURLException e) {
					logger.error("", e);
				}
			}else if (dnsn instanceof HtmlInput || dnsn instanceof HtmlSpan){
				//clickable
				nextPageEle = (HtmlElement) dnsn;
			}else{
				//TODO special, should be in config, for example: location.href='1.html#pic'
				url = dnsn.getTextContent();
				url = StringUtil.getStringBetweenFirstPreLastPost(url, "'", "'");
				try {
					url = curPage.getFullyQualifiedUrl(url).toExternalForm();
				} catch (MalformedURLException e) {
					logger.error("",e);
				}
			}
			
			if (nextPageEle!=null || url!=null){
				NextPage np = new NextPage(url, nextPageEle);
				HtmlPageResult hpResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, np, 
						new VerifyPageByXPath(getPageVerifyXPaths(task, taskDef)), null, task.getParsedTaskDef(), cconf);
				if (hpResult.getErrorCode()==HtmlPageResult.SUCCSS){
					return hpResult.getPage();
				}else{
					return null;
				}
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	public static void callbackReadDetails(WebClient wc, HtmlPage inpage, Product product, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf) throws InterruptedException {	
		//set the user attributes in case there is default values
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		CrawlTaskEval.setInitAttributes(bdt.getBaseBrowseTask().getUserAttribute(), product.getParamMap(), task.getParamMap());	
		
		Map<String, List<? extends DomNode>> pageMap = null;
		if (product.getLastUrl()==null){
			//go to first page if there is click stream
			pageMap = getFirstPage(wc, inpage, bdt.getFirstPageClickStream(), task, cconf);
			int totalPage = getTotalPage(pageMap, task, taskDef, cconf);
			product.setTotalPage(totalPage);
		}else{
			pageMap = new HashMap<String, List<? extends DomNode>>();
			
			HtmlPageResult hpResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(product.getLastUrl()), 
					 new VerifyPageByXPath(getPageVerifyXPaths(task, taskDef)), null, task.getParsedTaskDef(), cconf);
			if (hpResult.getErrorCode()==HtmlPageResult.SUCCSS){
				inpage = hpResult.getPage();
				List<HtmlPage> pagelist = new ArrayList<HtmlPage>();
				pagelist.add(inpage);
				pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
			}else{
				//as nothing happened, restart from the same place
			}
		}
		boolean tryPattern= false;
		if (product.getTotalPage()>0){
			tryPattern = true;
		}
		boolean externalistFinished=false;
		boolean finalPage=checkFinalPage(bdt.getLastPageCondition(), product.getParamMap());
		HtmlPage curPage = (HtmlPage) pageMap.get(ConfKey.CURRENT_PAGE).get(0);
		while (curPage!=null && !finalPage && !externalistFinished){
			//eval on curPage again
			List<HtmlPage> pagelist = new ArrayList<HtmlPage>();
			pagelist.add(curPage);
			pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
			externalistFinished = CrawlTaskEval.setUserAttributes(pageMap, bdt.getBaseBrowseTask().getUserAttribute(), 
					product.getParamMap(), cconf, task.getParamMap(), tryPattern);
			if (externalistFinished)
				break;
			finalPage = checkFinalPage(bdt.getLastPageCondition(), product.getParamMap());
			if (finalPage)
				break;
			String lastPageUrl = curPage.getUrl().toExternalForm();
			curPage=getNextPage(wc, curPage, task, taskDef, cconf);
		}
		
		if (finalPage || externalistFinished){
			product.setCompleted(true);
		}
		
		ProductConf pconf = cconf.getPrdConfMap().get(product.getItemType());
		if (pconf!=null){
			if (pconf.getPrdHandler()!=null){
				pconf.getPrdHandler().handleProduct(product, task, taskDef);
			}else{
				logger.error(String.format("product handler for %s is null.", product.getItemType()));
			}
		}else{
			logger.error(String.format("product conf for %s not found.", product.getItemType()));
		}
	}
}
