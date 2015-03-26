package org.cld.datacrawl.mgr.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.taskmgr.entity.Task;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.entity.Product;
import org.cld.datastore.entity.Price;
import org.cld.datacrawl.mgr.IProductAnalyze;
import org.cld.datacrawl.pagea.ProductAnalyzeInf;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.VerifyPageByXPath;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;

public class ProductAnalyze implements IProductAnalyze{
	
	private static Logger logger =  LogManager.getLogger(ProductAnalyze.class);
	
	private CrawlConf cconf;
	private CrawlTaskConf ctconf;
	private VerifyPageByXPath VPXP; //
	
	public String toString(){
		return System.identityHashCode(this) + "\n" +
				"ctconf:" + System.identityHashCode(ctconf);
	}
	
	public ProductAnalyze(){
		
	}

	
	public ProductAnalyze(CrawlConf cconf, CrawlTaskConf ctconf){
		this.cconf = cconf;
		this.ctconf = ctconf;
		this.VPXP = new VerifyPageByXPath();
	}
	
	@Override
	public void setup(CrawlConf cconf, CrawlTaskConf ctconf){
		this.cconf = cconf;
		this.ctconf = ctconf;
		this.VPXP = new VerifyPageByXPath();
	}

	@Override
	public void setCTConf(CrawlTaskConf ctconf) {
		this.ctconf = ctconf;
	}
	
	@Override
	public Price getPrice(WebClient wc, HtmlPage details, String prdId, String storeId, Price summaryPrice, Task task) 
			throws InterruptedException{
		ProductAnalyzeInf baInf = ctconf.getBaInf();
		Price price = null;
		try {
			double curPrice = baInf.getCurPrice(details);
			if (curPrice == -1){
				//no price found on detailed page, use the price from summary page
				price = summaryPrice;
			}else{
				price = new Price(prdId, new Date(), storeId, curPrice, null);
				price.setPrice(curPrice);
			}
			if (price!=null)
				logger.debug("cur price:" + price.getPrice() + " for url:" + details.getUrl());
		}catch(NumberFormatException nfe){
			logger.error(details.getUrl() + " has wrong cur price format.", nfe);
		}
		
		return price;
	}
	
	@Override
	public void addProduct(WebClient wc, String url, Product product, Product lastProduct, Task task, ParsedBrowsePrd taskDef) 
			throws InterruptedException{
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		boolean monitorPrice = bdt.isMonitorPrice();
		
		product.setRootTaskId(task.getRootTaskId());
		product.setFullUrl(url);
		VPXP.setXPaths(ctconf.getBaInf().getPageVerifyXPaths(task, taskDef));
		ProductAnalyzeInf baInf = ctconf.getBaInf();
		HtmlPageResult detailsResult;
		HtmlPage details = null;

		detailsResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(url), VPXP, task.getParsedTaskDef().getTasks(), 
				cconf.getMaxRetry(), cconf.isCancelable(), task, cconf);	
		details = detailsResult.getPage();
		
		if (detailsResult.getErrorCode() == HtmlPageResult.SUCCSS){			
			//product name
			if (product.getName()==null){
				String title = baInf.getTitle(details, task, taskDef);
				product.setName(title);
			}
			if (monitorPrice){
				//product original price
				try {
					double orgPrice = baInf.getOrgPrice(details);
					if (orgPrice == -1){
						logger.info("no org price found in:" + url);
					}
					product.setOriginalPrice(orgPrice);
					logger.debug("org price:" + product.getOriginalPrice() + " for url:" + url);
				}catch(NumberFormatException nfe){
					logger.error(url + " has wrong org price format.", nfe);
				}
			}
			//product external id
			product.setExternalId(baInf.getExternalId(details));
			
			//call back
			baInf.callbackReadDetails(wc, details, product, task, taskDef);
			product.getId().setCreateTime(new Date());
			logger.debug("product got:" + product);
			cconf.getDsm().addCrawledItem(product, lastProduct);	
		}
	}
	
	/**
	 * 
	 * @param wc
	 * @param url: fullUrl for this product
	 * @param product
	 * @param p, thisPrice, the new price-info got.
	 * @param bs, statistics only for promotion
	 * @return true
	 * 
	 */
	public Price readPrice(WebClient wc, String url, String prdId, String storeId, Price summaryPrice, Task task, ParsedBrowsePrd taskDef)
			throws InterruptedException{
		VPXP.setXPaths(ctconf.getBaInf().getPageVerifyXPaths(task, taskDef));
		HtmlPageResult detailsResult;
		HtmlPage details = null;

		detailsResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(url), VPXP, task.getParsedTaskDef().getTasks(), 
				cconf.getMaxRetry(), cconf.isCancelable(), task, cconf);	
		details = detailsResult.getPage();
		
		if (detailsResult.getErrorCode() == HtmlPageResult.SUCCSS){	
			return getPrice(wc, details, prdId, storeId, summaryPrice, task);
		}else{
			logger.error(String.format("read price page %s failed.", url));
			return null;
		}
	}
}
