package org.cld.datacrawl.mgr;

import java.util.Date;
import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.hibernate.SessionFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface ListProcessInf {
	public List<Task> process(HtmlPage listPage, Date readTime, 
			Category cat, CrawlConf cconf, Task task, int maxItems) throws InterruptedException;
}
