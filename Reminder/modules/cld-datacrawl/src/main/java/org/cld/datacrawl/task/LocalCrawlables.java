package org.cld.datacrawl.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.TaskExeMgr;

public class LocalCrawlables implements Runnable{
	
	private static Logger logger =  LogManager.getLogger(LocalCrawlables.class);
	public static final String TASK_ID_KEY="taskid";
	private BrowseProductTaskConf task;
	private String taskId;
	private boolean addToDB;
	private CrawlConf cconf;
	private TaskExeMgr tem;
	
	public LocalCrawlables(BrowseProductTaskConf task, String taskId, boolean addToDB, CrawlConf cconf, TaskExeMgr tem){
		this.task = task;
		this.taskId = taskId;
		this.addToDB = addToDB;
		this.tem = tem;
		this.cconf = cconf;
	}
	
	@Override
	public void run() {
		try {
			ThreadContext.put(TASK_ID_KEY, taskId);
			CrawlTestUtil.browsePrd(task, cconf, taskId, addToDB);
			//normal end
			tem.cancel(task.getSiteconfid(), taskId);
		}catch(InterruptedException ie){
			logger.error("", ie);
		}catch(Throwable t){
			logger.error("", t);
		}finally{
			ThreadContext.clearMap();
		}
	}
}
