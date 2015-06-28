package org.cld.datacrawl.mgr.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.pagea.general.ProductAnalyzeUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.entity.Product;
import org.cld.datastore.entity.Price;
import org.cld.datacrawl.mgr.IProductAnalyze;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.VerifyPageByXPath;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;

public class ProductAnalyze implements IProductAnalyze{
	
	private static Logger logger =  LogManager.getLogger(ProductAnalyze.class);
	
	private VerifyPageByXPath VPXP; //
	
	public String toString(){
		return System.identityHashCode(this) + "\n";
	}
	
	public ProductAnalyze(){
		this.VPXP = new VerifyPageByXPath();
	}

	
	@Override
	public void addProduct(WebClient wc, String url, Product product, Product lastProduct, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf) 
			throws InterruptedException{
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		boolean monitorPrice = bdt.isMonitorPrice();
		
		product.setRootTaskId(task.getRootTaskId());
		product.setFullUrl(url);

		if (taskDef.getBrowsePrdTaskType().getBaseBrowseTask().getUserAttribute().size()!=0){
			HtmlPageResult detailsResult;
			HtmlPage details = null;
			VPXP.setXPaths(ProductAnalyzeUtil.getPageVerifyXPaths(task, taskDef));
			detailsResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(url), VPXP, null, task.getParsedTaskDef(), cconf);	
			details = detailsResult.getPage();
			
			if (detailsResult.getErrorCode() == HtmlPageResult.SUCCSS){			
				//product name
				if (product.getName()==null){
					String title = ProductAnalyzeUtil.getTitle(details, task, taskDef, cconf);
					product.setName(title);
				}
				if (monitorPrice){
					//
				}
				//product external id
				
				//call back
				ProductAnalyzeUtil.callbackReadDetails(wc, details, product, task, taskDef, cconf);
				product.getId().setCreateTime(new Date());
				logger.debug("product got:" + product);
				if (cconf.getDsm()!=null)
					cconf.getDsm().addCrawledItem(product, lastProduct);	
			}
		}else{
			//do not need to browse the page
		}
	}
}
