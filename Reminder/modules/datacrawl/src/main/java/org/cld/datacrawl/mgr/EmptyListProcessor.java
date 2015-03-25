package org.cld.datacrawl.mgr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class EmptyListProcessor implements ListProcessInf{

	@Override
	public List<Task> process(HtmlPage listPage, Date readTime, Category cat, CrawlConf cconf, Task task, int maxItems)
			throws InterruptedException {
		return new ArrayList<Task>();
	}

}
