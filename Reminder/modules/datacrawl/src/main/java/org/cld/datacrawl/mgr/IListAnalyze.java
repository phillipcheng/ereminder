package org.cld.datacrawl.mgr;


import java.util.List;


import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;


public interface IListAnalyze{
	
	public static final String PARAM_SEP="&";

	public void setup(CrawlConf cconf, ListProcessInf lpInf);
	
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
			Task task, int maxPages, int maxItems) throws InterruptedException, SomePageErrorException;
	
	public ListProcessInf getLpInf();
	
	public void setLpInf(ListProcessInf lpInf);

}
