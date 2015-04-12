package org.cld.datacrawl.mgr;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.util.VerifyPage;
import org.cld.pagea.general.ListAnalyzeUtil;
import org.cld.pagea.general.ProductListAnalyzeUtil;
import org.cld.taskmgr.entity.Task;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class VerifyPageProductList implements VerifyPage{
	private static Logger logger =  LogManager.getLogger(VerifyPageProductList.class);	

	private CrawlConf cconf;
	
	public VerifyPageProductList(CrawlConf cconf){
		this.cconf = cconf;
	}
	
	public boolean verifySuccess(HtmlPage page, Object param) {
		Task task = (Task)param;
		
		String[] xpaths = ProductListAnalyzeUtil.getListPageVerifyXPaths(task);
		if (xpaths!=null){
			for (int i=0; i<xpaths.length; i++){
				if (page.getFirstByXPath(xpaths[i])==null){
					logger.warn(String.format("product list analyze: page url:%s does not contain %s",
							page.getUrl().toString(), xpaths[i]));
					return false;
				}
			}
		}
		
		List<?> lilist = ProductListAnalyzeUtil.getItemList(page, task, cconf);
		logger.debug("this page has:" + lilist.size() + " items.");
		for (int i=0; i<lilist.size(); i++){
			HtmlElement item = (HtmlElement) lilist.get(i);
			//TODO 
		}		
		
		return true;
	}
}
