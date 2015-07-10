package org.cld.pagea.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.ProductHandler;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.BrowseCatInst;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseDetailType;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DefaultHandler implements ProductHandler{
	
	private static Logger logger =  LogManager.getLogger(DefaultHandler.class);
	
	private CrawlConf cconf;
	//must have a constructor with CrawlConf as parameter
	public DefaultHandler(CrawlConf cconf){
		this.cconf = cconf;
	}
	
	@Override
	public void handleProduct(Product product, Task task, ParsedBrowsePrd taskDef) {
	}

	@Override
	public void handleCategory(String requestUrl, HtmlPage catlist,
			Category cat, Task task) throws InterruptedException {
	}
}
