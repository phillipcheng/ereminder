package org.cld.datacrawl.mgr;

import java.util.Date;
import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface IProductListAnalyze {
	
	public void setup(CrawlConf cconf, CrawlTaskConf ctconf);
	
	public void setCTConf(CrawlTaskConf ctconf);//
	
	/**
	 * 
	 * @param internalId
	 * @param storeId
	 * @param summary
	 * @param rt
	 * @return null means no price found
	 */
	public Price readGeneralPriceItem(String internalId, String storeId, DomNode summary, Date rt, 
			Product product, Task t) throws InterruptedException;
	
	/**
	 * either full info or url to fetch info
	 * page + cat + bookSummary = detailedUrl
	 * @param page
	 * @param cat
	 * @param bookSummary: the summary xml get from the list page
	 * @param detailedUrl: the detailed book detailedUrl
	 * @param readTime
	 * @param cconf
	 * @return false, means readItem failed
	 * @throws InterruptedException 
	 */
	public List<Task> readItem(WebClient wc, HtmlPage page, Category category, DomNode bookSummary, 
			String detailedUrl,  Date readTime, CrawlConf cconf, Task task) throws InterruptedException;
	
}
