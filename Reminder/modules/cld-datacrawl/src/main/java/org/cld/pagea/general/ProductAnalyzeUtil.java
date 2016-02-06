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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.ProductConf;
import org.cld.datacrawl.mgr.BinaryBoolOpEval;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.datacrawl.mgr.ProductAnalyze;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.VerifyPageByXPath;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.Product;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.mytaskdef.TasksTypeUtil;
import org.xml.mytaskdef.XPathType;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.ValueType;

public class ProductAnalyzeUtil {
	
	private static Logger logger =  LogManager.getLogger(ProductAnalyzeUtil.class);
	
	//
	public static XPathType[] getPageVerifyXPaths(Task task, ParsedBrowsePrd taskDef) {
		if (taskDef.getBrowsePrdTaskType().getFirstPageClickStream()==null){
			List<XPathType> xpathList = new ArrayList<XPathType>();
			List<AttributeType> attrlist = taskDef.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute();
			//page verifications
			for (AttributeType attr: attrlist){
				if (!attr.getValue().isOptional()){
					XPathType xpath = TasksTypeUtil.getXPath(attr.getValue(), task.getParamMap());
					if (xpath!=null)
						xpathList.add(xpath);
				}
			}
			ValueType tpVT = taskDef.getBrowsePrdTaskType().getTotalPage();
			if (tpVT!=null && !tpVT.isOptional() && tpVT.getValue().contains("/")){
				XPathType xpath = TasksTypeUtil.getXPath(tpVT, task.getParamMap());
				if (xpath!=null)
					xpathList.add(xpath);
			}
			ValueType np = taskDef.getBrowsePrdTaskType().getNextPage();
			if (np!=null && !np.isOptional() && np.getValue().contains("/")){
				xpathList.add(new XPathType(np.getValue(), np.getFrameId()));
			}
			if (xpathList.size()>0)
				return xpathList.toArray(new XPathType[xpathList.size()]);
			else
				return null;
		}else{
			//when there is firstpage click, the xpath validation are needed after first page clicks
			return null;
		}
	}
	
	public static BinaryBoolOp[] getPageVerifyBoolOp(Task task, ParsedBrowsePrd taskDef){
		if (taskDef.getBrowsePrdTaskType().getFirstPageClickStream()==null){
			//page verifications
			List<BinaryBoolOp> bboplist = taskDef.getBrowsePrdTaskType().getBaseBrowseTask().getPageVerify();
			if (bboplist!=null){
				return bboplist.toArray(new BinaryBoolOp[bboplist.size()]);
			}else{
				return null;
			}
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
			List<HtmlPage> pagelist= new ArrayList<HtmlPage>();
			pagelist.add(prdPage);
			pageMap.put(ConfKey.START_PAGE, pagelist);
			pageMap.put(ConfKey.CURRENT_PAGE, pagelist);//set current page
			boolean ret = HtmlUnitUtil.clickClickStream(firstPageClicks, pageMap, task.getParamMap(), cconf, 
					new NextPage(prdPage.getUrl().toExternalForm(), null, null));
			if (!ret){
				pageMap.put(ConfKey.CURRENT_PAGE, null);
			}
		}
		return pageMap;
	}
	
	private static final String LocationHref="location.href=";
	private static HtmlPage getNextPage(WebClient wc, HtmlPage curPage, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf, Product product) throws InterruptedException{
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		if (bdt.getNextPage()!=null){
			HtmlPage frame = (HtmlPage) HtmlUnitUtil.getFramePage(curPage, bdt.getNextPage().getFrameId());
			DomNamespaceNode dnsn = frame.getFirstByXPath(bdt.getNextPage().getValue());
			product.addParam(ConfKey.PRD_NEXTPAGE, dnsn);
			logger.info(String.format("get next page element: %s from page:%s", dnsn, frame.getUrl().toExternalForm()));
			if (dnsn==null){
				logger.info(String.format("next page xpath %s not found on page %s.", bdt.getNextPage(), frame.getUrl().toExternalForm()));
				return null;
			}
			HtmlElement nextPageEle = null;
			String url = null;
			if (dnsn instanceof HtmlAnchor){
				HtmlAnchor ha = (HtmlAnchor)dnsn;
				if (ha.getHrefAttribute().contains("javascript")){
					nextPageEle = (HtmlElement) dnsn;
				}else{
					try {
						url = frame.getFullyQualifiedUrl(((HtmlAnchor)dnsn).getHrefAttribute()).toExternalForm();
					} catch (MalformedURLException e) {
						logger.error("", e);
					}
				}
			}else if (dnsn instanceof DomAttr){
				DomAttr da = (DomAttr)dnsn;
				//location.href='3.html#pic'
				String val = da.getValue();
				if (val.contains(LocationHref)){
					val = val.substring(LocationHref.length()+1, val.length()-1);
					try {
						url = frame.getFullyQualifiedUrl(val).toExternalForm();
					} catch (MalformedURLException e) {
						logger.error("", e);
					}
				}else{
					logger.error("DomAttr %s as next page item has no %s", da.toString(), LocationHref);
				}
			}else{
				nextPageEle = (HtmlElement) dnsn;
			}
			
			NextPage np = null;
			if (url!=null){
				np = new NextPage(url, curPage, bdt.getNextPage().getFrameId());
			}else{
				np = new NextPage(nextPageEle, curPage, bdt.getNextPage().getFrameId());
			}
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
	}
	
	private static boolean taskNeedFilter(ParsedBrowsePrd taskDef, CrawledItem ci){
		if ((taskDef.getPdtAttrMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_ColDateIdx)
				|| taskDef.getPdtAttrMap().containsKey(AbstractCrawlItemToCSV.FIELD_NAME_RowDateIdx)) 
			&& ci.getParam(AbstractCrawlItemToCSV.FN_STARTDATE)!=null)
			return true;
		return false;
	}
	
	public static void callbackReadDetails(WebClient wc, HtmlPage inpage, Product product, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf) throws InterruptedException {	
		//set the user attributes in case there is default values
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		CrawlTaskEval.setInitAttributes(bdt.getBaseBrowseTask().getUserAttribute(), product.getParamMap(), task.getParamMap());	
		Map<String, List<? extends DomNode>> pageMap = null;
		int totalPage = -1;
		boolean firstPageSuccess = true;
		if (product.getLastUrl()==null){
			//go to first page if there is click stream
			pageMap = getFirstPage(wc, inpage, bdt.getFirstPageClickStream(), task, cconf);
			if (pageMap.get(ConfKey.CURRENT_PAGE)==null){
				//failed to go to first page
				firstPageSuccess = false;
			}
			totalPage = getTotalPage(pageMap, task, taskDef, cconf);
			product.setTotalPage(totalPage);
		}else{
			pageMap = new HashMap<String, List<? extends DomNode>>();
			HtmlPageResult hpResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(product.getLastUrl(), null, null), 
					 new VerifyPageByXPath(getPageVerifyXPaths(task, taskDef)), null, task.getParsedTaskDef(), cconf);
			if (hpResult.getErrorCode()==HtmlPageResult.SUCCSS){
				inpage = hpResult.getPage();
				List<HtmlPage> pagelist = new ArrayList<HtmlPage>();
				pagelist.add(inpage);
				pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
			}else{
				logger.error(String.format("product %s try to start from lastUrl %s, but failed.", product.getId(), product.getLastUrl()));
				//as nothing happened, restart from the same place
			}
		}
		if (firstPageSuccess){
			boolean tryPattern= false;
			if (product.getTotalPage()>0){
				tryPattern = bdt.isTryPattern();
			}
			HtmlPage curPage = (HtmlPage) pageMap.get(ConfKey.CURRENT_PAGE).get(0);
			boolean finalPage=false;
			//set variable nextPage, later it will be set while doing the getNextPage
			if (bdt.getNextPage()!=null){
				HtmlPage frame = (HtmlPage) HtmlUnitUtil.getFramePage(curPage, bdt.getNextPage().getFrameId());
				DomNamespaceNode dnsn = frame.getFirstByXPath(bdt.getNextPage().getValue());
				product.addParam(ConfKey.PRD_NEXTPAGE, dnsn);
				logger.info(String.format("get next page element: %s from page:%s", dnsn, frame.getUrl().toExternalForm()));
			}
			if (bdt.getLastPageCondition()!=null){
				finalPage= BinaryBoolOpEval.eval(bdt.getLastPageCondition(), product.getParamMap(), curPage, cconf);
			}
			int curPageNum = 1;
			//(totalPage not set or curPage less than totalPage) & curPage not null & not final & not finished
			boolean useTotalPage=false;
			boolean externalistFinished=false; //finished by detecting the pattern instead of browsing till the final page (final condition satisfied) or page num
			while (curPage!=null && !externalistFinished){
				boolean goon=false;
				if (curPageNum<=totalPage){//count page 1st priority
					goon= true;
					useTotalPage=true;
				} else if (!useTotalPage && !finalPage){//use final page condition
					goon = true;
				}
				if (goon){
					//eval on curPage again
					List<HtmlPage> pagelist = new ArrayList<HtmlPage>();
					pagelist.add(curPage);
					pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
					externalistFinished = CrawlTaskEval.setUserAttributes(pageMap, bdt.getBaseBrowseTask().getUserAttribute(), 
							product.getParamMap(), cconf, task.getParamMap(), tryPattern, curPageNum==1);
					if (ProductAnalyze.postCrawlProcess(task, bdt.getBaseBrowseTask(), product)){//suppose the data are listed in the desc order
						externalistFinished = true; //if there is any data filtered, we donot need go further
						logger.info("filter condition meet do not crawl further.");
					}
					
					logger.info("curPageNum:" + curPageNum + ", totalPage:" + totalPage);
					logger.debug(product.getParamMap());
					if (externalistFinished)
						break;
					curPage=getNextPage(wc, curPage, task, taskDef, cconf, product);
					curPageNum ++;
					if (curPage==null){
						finalPage = true;
					}else if (bdt.getLastPageCondition()!=null){
						finalPage = BinaryBoolOpEval.eval(bdt.getLastPageCondition(), product.getParamMap(), curPage, cconf);
					}
				}else{
					break;
				}
			}
			
			if (!externalistFinished && finalPage && curPage!=null && 
					(curPageNum<=totalPage||totalPage==-1)){//totalPage not set or totalPage set and curPage<=totalPage
				//operate on the final page
				List<HtmlPage> pagelist = new ArrayList<HtmlPage>();
				pagelist.add(curPage);
				pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
				CrawlTaskEval.setUserAttributes(pageMap, bdt.getBaseBrowseTask().getUserAttribute(), 
						product.getParamMap(), cconf, task.getParamMap(), tryPattern, curPageNum==1);
				logger.info("curPageNum:" + curPageNum + ", totalPage:" + totalPage);
				logger.debug(product.getParamMap());
				product.setCompleted(true);
			}else if(curPageNum>totalPage && totalPage!=-1){
				//we have totalPage, and we browse passed totalPage meaning we get all pages, and we do not need to process the final page
				product.setCompleted(true);
			}else if (externalistFinished){
				product.setCompleted(true);
			}else{
				product.setCompleted(false);
			}
		}else{
			product.setCompleted(false);
		}

		//clean up the next page in product
		product.addParam(ConfKey.PRD_NEXTPAGE, null);

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
