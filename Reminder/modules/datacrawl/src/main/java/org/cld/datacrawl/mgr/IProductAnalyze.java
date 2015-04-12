package org.cld.datacrawl.mgr;

import org.cld.datacrawl.CrawlConf;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedBrowsePrd;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface IProductAnalyze {
	
	/**
	 * 
	 * @param wc
	 * @param url
	 * @throws InterruptedException 
	 * 
	 */
	public void addProduct(WebClient wc, String url, Product thisProduct, Product lastProduct, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf) 
			throws InterruptedException;
}
