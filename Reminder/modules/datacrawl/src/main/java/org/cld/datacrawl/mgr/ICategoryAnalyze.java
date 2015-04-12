package org.cld.datacrawl.mgr;

import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;


public interface ICategoryAnalyze {

	public List<Task> navigateCategory(Task bct, TaskStat bs, CrawlConf cconf) throws InterruptedException;
	
	public List<Task> retryCat(CrawlConf cconf, TaskStat bs) throws InterruptedException;
}
