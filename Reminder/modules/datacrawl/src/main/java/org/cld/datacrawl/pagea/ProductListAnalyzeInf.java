package org.cld.datacrawl.pagea;

import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.mgr.TargetPrdInvoke;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BrowseDetailType;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/*
 * process the result book list of the list page
 */
public interface ProductListAnalyzeInf{
	
	public void setCConf(CrawlConf cconf);
	
	//to verify the whole page
	public String[] getListPageVerifyXPaths(Task task);
	
	//to verify item by item
	public String[] getItemVerfiyXPaths(Task task);
	
	//can be html element or html page
	public List<DomNode> getItemList(HtmlPage listPage, Task task);
	
	//get the invoke browse product task and parameters
	public List<TargetPrdInvoke> getTargetPrdInovokeList(DomNode itemSummary, HtmlPage page, Task task, Product dummyProduct);
	
	/*
	 * return the unique book Id with the store
	 */
	public String getInternalId(String fullUrl, Task task, ParsedBrowsePrd pbptTemplate);
	
	/*
	 * return -1 for no price, price is like the key of history item
	 */
	public double getCurPrice(DomNode bookSummary);
	
	public String getPromId(DomNode summary);
	
	public void setAttributes(DomNode summary, Product product, Task task) throws InterruptedException;
}
