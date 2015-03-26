package org.cld.datacrawl.mgr;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedBrowsePrd;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface IProductAnalyze {
	
	public void setup(CrawlConf cconf, CrawlTaskConf ctconf);
	
	public void setCTConf(CrawlTaskConf ctconf);//
	
	public Price getPrice(WebClient wc, HtmlPage details, String prdId, String storeId, Price summaryPrice, Task task) 
			throws InterruptedException;
	
	/**
	 * 
	 * @param wc
	 * @param url
	 * @throws InterruptedException 
	 * 
	 */
	public void addProduct(WebClient wc, String url, Product thisProduct, Product lastProduct, Task task, ParsedBrowsePrd taskDef) 
			throws InterruptedException;
	
	/**
	 * 
	 * @param wc
	 * @param url
	 * @param storeId
	 * @param price, price got from the summary page, can be null.
	 * @param taskstat, statistics
	 * @return price, price get from the detailed page, can be the same as latest price value, or null
	 * @throws InterruptedException
	 */
	public Price readPrice(WebClient wc, String url, String prdId, String storeId, Price summaryPrice,
			Task task, ParsedBrowsePrd taskDef) throws InterruptedException;
}
