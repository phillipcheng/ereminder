package org.cld.datacrawl.mgr;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.pagea.ProductListAnalyzeInf;
import org.cld.datacrawl.pagea.ListAnalyzeInf;
import org.cld.datacrawl.util.VerifyPage;
import org.cld.taskmgr.entity.Task;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class VerifyPageProductList implements VerifyPage{
	private static Logger logger =  LogManager.getLogger(VerifyPageProductList.class);	

	private CrawlTaskConf ctconf;

	public VerifyPageProductList(CrawlTaskConf ctconf){
		this.ctconf = ctconf;
	}
	
	public boolean verifySuccess(HtmlPage page, Object param) {
		Task task = (Task)param;
		
		ListAnalyzeInf laInf = ctconf.getLaInf();
		String[] xpaths = laInf.getListPageVerifyXPaths(task.getParsedTaskDef());
		if (xpaths!=null){
			for (int i=0; i<xpaths.length; i++){
				if (page.getFirstByXPath(xpaths[i])==null){
					logger.warn(String.format("list analyze: page url:%s does not contain %s", 
							page.getUrl().toString(), xpaths[i]));
					return false;
				}
			}
		}
		
		ProductListAnalyzeInf plaInf = ctconf.getBlaInf();
		xpaths = plaInf.getListPageVerifyXPaths(task);
		if (xpaths!=null){
			for (int i=0; i<xpaths.length; i++){
				if (page.getFirstByXPath(xpaths[i])==null){
					logger.warn(String.format("product list analyze: page url:%s does not contain %s",
							page.getUrl().toString(), xpaths[i]));
					return false;
				}
			}
		}
		
		List<?> lilist = plaInf.getItemList(page, task);
		logger.debug("this page has:" + lilist.size() + " items.");
		for (int i=0; i<lilist.size(); i++){
			HtmlElement item = (HtmlElement) lilist.get(i);
			xpaths = plaInf.getItemVerfiyXPaths(task);
			if (xpaths!=null){
				for (int j=0; j<xpaths.length; j++){
					if (item.getFirstByXPath(xpaths[j]) == null){
						logger.warn("item:" + item.asXml() + "\n does not contain:\n" + xpaths[j]);
						return false;
					}
				}
			}
		}		
		
		return true;
	}
}
