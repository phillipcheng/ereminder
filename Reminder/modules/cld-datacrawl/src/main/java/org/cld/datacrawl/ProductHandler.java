package org.cld.datacrawl;

import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.Category;
import org.cld.util.entity.Product;
import org.xml.mytaskdef.ParsedBrowsePrd;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface ProductHandler {
	
	//must have a constructor with CrawlConf as parameter
	
	public void handleProduct(Product product, Task task, ParsedBrowsePrd taskDef);
	
	public void handleCategory(String requestUrl, HtmlPage catlist, Category cat, Task task) 
			throws InterruptedException;
	
}
