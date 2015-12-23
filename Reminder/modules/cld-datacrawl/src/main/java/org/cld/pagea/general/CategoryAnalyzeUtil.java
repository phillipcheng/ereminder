package org.cld.pagea.general;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.ProductConf;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.Category;
import org.cld.util.entity.CrawledItemId;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.SubListType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.xml.mytaskdef.BrowseCatInst;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.IdUrlMapping;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.mytaskdef.XPathType;

public class CategoryAnalyzeUtil {
	
	private static Logger logger =  LogManager.getLogger(CategoryAnalyzeUtil.class);
	
	public static XPathType[] getCatPageVerifyXPaths(Category cat, BrowseCategoryTaskConf task) {
		List<XPathType> xpaths = new ArrayList<XPathType>();
		BrowseCatInst bci = task.getBCI(task.getStartURL());
		if (bci!=null){
			ValueType totalItemNumVT = bci.getBc().getTotalItemNum();
			if (totalItemNumVT!=null){
				if (totalItemNumVT.getFromType()==VarType.XPATH || totalItemNumVT.getValue().contains("//")){
					xpaths.add(new XPathType(totalItemNumVT.getValue(), totalItemNumVT.getFrameId()));
				}
			}
			ValueType totalPageNumVT = bci.getBc().getTotalPageNum();
			if (totalPageNumVT!=null){
				if (totalPageNumVT.getFromType()==VarType.XPATH || totalPageNumVT.getValue().contains("//")){
					xpaths.add(new XPathType(totalPageNumVT.getValue(), totalPageNumVT.getFrameId()));
				}
			}
			ValueType itemListVT = bci.getBc().getSubItemList().getItemList();
			if (itemListVT!=null){
				if (itemListVT.getFromType()==VarType.XPATH || itemListVT.getValue().contains("/")){
					xpaths.add(new XPathType(itemListVT.getValue(), itemListVT.getFrameId()));
				}
			}
		}
		logger.info("category page verify xpaths:" + xpaths);
		return xpaths.toArray(new XPathType[xpaths.size()]);
	}

	
	public static String getCatId(String url, ParsedTasksDef tasksDef) {
		BrowseCatInst bci = tasksDef.getBCI(url);
		if (bci!=null){
			String id = bci.getId();
			return id;
		}
		return null;
	}

	
	public static String getCatURL(Category cat, int pageNum, ParsedTasksDef tasksDef) {
		BrowseCatInst bci = tasksDef.getBCI(cat.getFullUrl());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IdUrlMapping.ID_KEY, cat.getId().getId());
		map.put(IdUrlMapping.ID2_KEY, cat.getId2());
		map.put(IdUrlMapping.PAGENUM_KEY, pageNum+"");
		if (pageNum==1 && bci.getPBC().getIumFirstPage()!=null){
			return bci.getPBC().getIumFirstPage().getUrl(map);
		}else{
			return bci.getPBC().getIum().getUrl(map);
		}
	}
	
	/*
	 * Pagination will be done by getCatPages
	 * @param requestUrl
	 * @param catlist page is the htmlpage after clicking the startCat, page's url maybe different then request url
	 * @param startCat: the parent category
	 * @return list of sub category including myself, for leaf node, only myself is returned
	 */
	public static List<Category> getSubCategoyList(String requestUrl, HtmlPage catlist, Category startCat, 
			Task task, CrawlConf cconf) 
			throws InterruptedException {
		String url = requestUrl;
		BrowseCatInst bci = task.getBCI(url);
		BrowseCatType bc = bci.getBc();
		
		//evaluate the sublist of html elements
		SubListType slt = bc.getSubItemList();
		ValueType listXPathVT = slt.getItemList();
		if (listXPathVT.getToType()==null){
			//default to list, set to list page if needed
			listXPathVT.setToType(VarType.LIST);
		}
		listXPathVT.setToEntryType(VarType.HTML_ELEMENT);
		List<DomNode> hel = (List<DomNode>) CrawlTaskEval.eval(catlist, listXPathVT, cconf, task.getParamMap());
		List<Category> lc = new ArrayList<Category>();
		if (hel.size()>0){
			for (DomNode he:hel){
				//evaluate the attributes
				Map<String, Object> userAttributes = new HashMap<String, Object>();
				CrawlTaskEval.setUserAttributes(he, slt.getUserAttribute(), userAttributes, cconf, task.getParamMap());
				//evaluate itemFullUrlClicks if have
				ClickStreamType itemFullUrlClicks = slt.getItemFullUrlClicks();
				HtmlPage newlistPage = null;
				if (itemFullUrlClicks!=null){
					//apply the itemFullUrlClicks
					Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
					List<DomNode> pagelist= new ArrayList<DomNode>();
					pagelist.add(he);
					pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
					HtmlUnitUtil.clickClickStream(itemFullUrlClicks, pageMap, userAttributes, cconf, new NextPage(requestUrl));
					pagelist = (List<DomNode>) pageMap.get(ConfKey.CURRENT_PAGE);
					newlistPage = (HtmlPage) pagelist.get(0);
				}
				ValueType itemFullUrlXpath = slt.getItemFullUrl();
				String fullUrl = null;
				if (itemFullUrlXpath!=null){
					if (ConfKey.LIST_PAGE.equals(itemFullUrlXpath.getBasePage())){
						itemFullUrlXpath.setBasePage(null);//TODO, here list page just indicate using list page not item object
						fullUrl = (String) CrawlTaskEval.eval(newlistPage, itemFullUrlXpath, cconf, userAttributes);
					}else{
						fullUrl = (String) CrawlTaskEval.eval(he, itemFullUrlXpath, cconf, userAttributes);
					}
				}else{
					fullUrl = CrawlTaskEval.getURLStringValue(he, catlist);
				}
				if (fullUrl!=null){
					String catId = getCatId(fullUrl, task.getParsedTaskDef());
					if (catId!=null){
						DomNamespaceNode dsn;
						Category cat = new Category(new CrawledItemId(catId, startCat.getId().getStoreId(), new Date()), 
								task.getTasks().getProductType());
						cat.setFullUrl(fullUrl);
						cat.setLeaf(slt.isIsLeaf());
						ValueType nameVT = slt.getName();
						if (nameVT!=null){
							cat.setName((String)CrawlTaskEval.eval(he, nameVT, cconf, task.getParamMap()));
						}
						if (slt.getLastItem()!=null){
							dsn = he.getFirstByXPath(slt.getLastItem());
							cat.setLastItem(dsn.getTextContent());
						}	
						//set user attr
						cat.getParamMap().putAll(userAttributes);
						lc.add(cat);
						logger.debug(String.format("cat got: %s", cat));
					}else{
						logger.error(String.format("cat returned in sublist with url: %s is not a valid url on page:%s", 
								fullUrl, requestUrl));
					}
				}else{
					logger.error(String.format("unsupported html element for itemFullUrl: %s, xpath:%s from element %s on page:%s", 
							fullUrl, slt.getItemFullUrl(), he, requestUrl));
				}
			}
		}else{
			logger.error(String.format("sub list xpath:%s not found on page:%s, page content:%s", 
					listXPathVT.getValue(), catlist.getUrl().toExternalForm(), catlist.asXml()));
		}
		return lc;
	}
	
	private static void setCatSysAttributes(String requestUrl, HtmlPage page, Category cat, Task task, CrawlConf cconf) throws InterruptedException {
		String url = requestUrl;
		BrowseCatInst bci = task.getBCI(url);
		if (bci!=null){
			BrowseCatType bc = bci.getBc();
			//pageNum
			ValueType tpVT = bc.getTotalPageNum();
			if (tpVT!=null){
				Object value = CrawlTaskEval.eval(page, tpVT, cconf, task.getParamMap());
				if (value!=null){
					cat.setPageNum(CrawlTaskEval.getIntValue(value, tpVT.getValue()));
				}else{
					cat.setPageNum(1);//configured but not found, means single page, not configured means browse all
				}
			}else{
				//try to use the totalItemNum and itemPerPage to calculate the totalPageNum
				ValueType tinVT = bc.getTotalItemNum();
				ValueType ippVT = bc.getItemPerPage();
				if (tinVT!=null && ippVT!=null){
					Object value = CrawlTaskEval.eval(page, tinVT, cconf, task.getParamMap());
					int tin = CrawlTaskEval.getIntValue(value, tinVT.getValue());
					value = CrawlTaskEval.eval(page, ippVT, cconf, task.getParamMap());
					int ipp = CrawlTaskEval.getIntValue(value, ippVT.getValue());
					int left = tin % ipp;
					int pageNum=-1;
					if (left>0){
						pageNum = tin / ipp;
					}else{
						pageNum = tin / ipp + 1;
					}
					logger.info(String.format("totalItemNum:%d, itemPerPage:%d, pageNum:%d", tin, ipp, pageNum));
					cat.setPageNum(pageNum);
				}
			}
			
			ValueType mpnVT = bc.getMaxPageNum();
			if (mpnVT!=null){
				//page number will be limited and some items will be missing
				Object value = CrawlTaskEval.eval(page, mpnVT, cconf, task.getParamMap());
				int mpn = CrawlTaskEval.getIntValue(value, mpnVT.getValue());
				if (cat.getPageNum()>mpn){
					cat.setPageNum(mpn);
				}
			}

			//itemName
			ValueType nameVT = bc.getBaseBrowseTask().getItemName();
			if (nameVT!=null){
				Object value = CrawlTaskEval.eval(page, nameVT, cconf, task.getParamMap());
				cat.setName((String)value);
			}
			
			//other attributes
			CrawlTaskEval.setUserAttributes(page, bc.getBaseBrowseTask().getUserAttribute(), cat.getParamMap(), cconf, task.getParamMap());
		}else{
			logger.error(String.format("url %s is not a valid category url for task: %s", url, task.getName()));
		}
	}
	
	/**
	 * 1) set pageNum for the cat to enable performance improvement by having multiple sub-task
	 * 2) when page is limited, set total item, page size, page limit to enable split cat to multiple sub-cat to crawl more
	 * 3) if nothing set, this cat will be browsed without functional and performance improvement but still works
	 * @param catlist page is the htmlpage after clicking cat
	 * @param cat (in/out)
	 */
	public static void setCatItemNum(String requestUrl, HtmlPage catlist, Category cat, Task task, CrawlConf cconf) 
			throws InterruptedException {
		setCatSysAttributes(requestUrl, catlist, cat, task, cconf);
		ProductConf pconf = cconf.getPrdConfMap().get(cat.getItemType());
		if (pconf!=null){
			if (pconf.getPrdHandler()!=null){
				pconf.getPrdHandler().handleCategory(requestUrl, catlist, cat, task);
			}else{
				logger.error(String.format("prd handler for %s is null.", cat.getItemType()));
			}
		}else{
			logger.error(String.format("prd conf for %s not found.", cat.getItemType()));
		}
	}
	
	
	public static boolean needJS(String url, ParsedTasksDef taskDef) {
		BrowseCatInst bci = taskDef.getBCI(url);
		BrowseCatType bc = bci.getBc();
		return bc.getBaseBrowseTask().isEnableJS();
	}

	
}
