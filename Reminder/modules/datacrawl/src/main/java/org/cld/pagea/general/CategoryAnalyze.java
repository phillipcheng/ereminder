package org.cld.pagea.general;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.ProductConf;
import org.cld.datacrawl.mgr.impl.CrawlTaskEval;
import org.cld.datacrawl.pagea.CategoryAnalyzeInf;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.taskmgr.entity.Task;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.SubListType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.xml.mytaskdef.BrowseCatInst;
import org.xml.mytaskdef.IdUrlMapping;
import org.xml.mytaskdef.ParsedTasksDef;

public class CategoryAnalyze implements CategoryAnalyzeInf {
	private static Logger logger =  LogManager.getLogger(CategoryAnalyze.class);
	
	public CrawlConf cconf;
	public CrawlTaskConf ctconf;
	
	@Override
	public void setCTConf(CrawlConf cconf, CrawlTaskConf ctconf) {
		this.cconf = cconf;
		this.ctconf = ctconf;
	}
	
	@Override
	public String[] getCatPageVerifyXPaths(Category cat, Task task) {
		return null;
	}

	@Override
	public String getCatId(String url, Task task) {
		BrowseCatInst bci = task.getBCI(url);
		if (bci!=null){
			String id = bci.getId();
			return id;
		}
		return null;
	}

	@Override
	public String getCatURL(Category cat, int pageNum, ParsedTasksDef tasksDef) {
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
	
	@Override
	public List<Category> getSubCategoyList(String requestUrl, HtmlPage catlist, Category startCat, Task task) 
			throws InterruptedException {
		String url = requestUrl;
		BrowseCatInst bci = task.getBCI(url);
		BrowseCatType bc = bci.getBc();
		SubListType slt = bc.getSubItemList();
		ValueType listXPathVT = slt.getItemList();
		if (listXPathVT.getToType()==null){
			//default to list, set to list page if needed
			listXPathVT.setToType(VarType.LIST);
		}
		List<DomNode> hel = (List<DomNode>) CrawlTaskEval.eval(catlist, listXPathVT, cconf, task.getParamMap());
		List<Category> lc = new ArrayList<Category>();
		if (hel.size()>0){
			for (DomNode he:hel){
				String itemFullUrlXpath = slt.getItemFullUrl();
				DomNode fullUrlAnchor = null;
				if (itemFullUrlXpath!=null){
					fullUrlAnchor = he.getFirstByXPath(slt.getItemFullUrl());
				}else{
					fullUrlAnchor = he;
				}
				if (fullUrlAnchor instanceof HtmlAnchor){
					String fullUrl=null;
					HtmlAnchor ha = (HtmlAnchor)fullUrlAnchor;
					try {
						fullUrl = catlist.getFullyQualifiedUrl(ha.getHrefAttribute()).toExternalForm();
					} catch (MalformedURLException e) {
						logger.error("", e);
					}
					String catId = getCatId(fullUrl, task);
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
						CrawlTaskEval.setUserAttributes(he, slt.getUserAttribute(), cat.getParamMap(), cconf, task.getParamMap());
						lc.add(cat);
					}else{
						logger.error(String.format("cat returned in sublist with url: %s is not a valid url on page:%s", 
								fullUrl, requestUrl));
					}
				}else{
					logger.error(String.format("unsupported html element: %s, xpath:%s from element %s on page:%s", 
							fullUrlAnchor, slt.getItemFullUrl(), he, requestUrl));
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
	
	@Override
	public void setCatItemNum(String requestUrl, HtmlPage catlist, Category cat, Task task) 
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
	
	@Override
	public boolean needJS(String url, Task task) {
		BrowseCatInst bci = task.getBCI(url);
		BrowseCatType bc = bci.getBc();
		return bc.getBaseBrowseTask().isEnableJS();
	}

	
}
