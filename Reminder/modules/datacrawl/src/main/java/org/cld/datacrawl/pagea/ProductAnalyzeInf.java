package org.cld.datacrawl.pagea;

import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.util.Content;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedBrowsePrd;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface ProductAnalyzeInf {
	public String[] getPageVerifyXPaths(Task task);

	public String getTitle(HtmlPage page, Task task, ParsedBrowsePrd taskDef) throws InterruptedException;
	
	//set the product to complete if do not want further browsing
	public void callbackReadDetails(WebClient wc, HtmlPage page, Product product, Task task, ParsedBrowsePrd taskDef) throws InterruptedException;
	
	public void setCConf(CrawlConf cconf);
	
	//
	public List<Content> getPromotions(HtmlPage page);
	
	//-1 for not found, format exception thrown for wrong format	
	public double getOrgPrice(HtmlPage page);
	
	//null for not found
	public String getExternalId(HtmlPage page);
	
	//-1 for not found, format exception thrown for wrong format	
	public double getCurPrice(HtmlPage page);
	
	//-1 for not found, format exception thrown for wrong format
	public double getSecHandPrice(HtmlPage page);
	
	//-1 for not found, format exception thrown for wrong format
	public double getPromPrice(HtmlPage page);
	
}