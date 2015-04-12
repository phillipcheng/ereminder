package org.cld.datacrawl.mgr;

import java.util.Date;
import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface IProductListAnalyze extends ListProcessInf{

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
