package org.cld.datacrawl.mgr;


import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.entity.ProductList;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.mytaskdef.ParsedTasksDef;


public interface IListAnalyze{
	
	public static final String PARAM_SEP="&";

	public void setup(CrawlConf cconf, CrawlTaskConf ctconf, ListProcessInf lpInf);

	public void setCTConf(CrawlTaskConf ctconf);//
	
	/**
	 * 
	 * @param cat: contains stype, fullUrl, catId
	 * @param fromPage
	 * @param toPage
	 * @param task: contains tasks definition and task instance data (like parameters)
	 * @param maxPages
	 * @param maxItems
	 */
	public List<Task> readTopLink(Category category, int fromPage, int toPage, 
			Task task, int maxPages, int maxItems) throws InterruptedException;
	
	public VerifyPageProductList getVPBL();
	
	public void setVPBL(VerifyPageProductList vPBL);
	
	public ListProcessInf getLpInf();
	
	public void setLpInf(ListProcessInf lpInf);

}
