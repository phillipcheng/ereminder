package org.cld.datacrawl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;

public class CrawlTaskGenerator {
	
	private static Logger logger =  LogManager.getLogger(CrawlTaskGenerator.class);
	
	public static Task getNextTask(Task t, CrawlConf cconf){
		TaskMgr taskMgr = cconf.getTaskMgr();
		String strNextTask = t.getNextTask();
		if (!TaskMgr.isSystemTask(strNextTask)){
			Task nextT = taskMgr.getTask(strNextTask);
			if (nextT!=null){
				return nextT.clone(cconf.getPluginClassLoader());
			}else{
				logger.error("task is null, for:" + strNextTask);
				return null;
			}
		}else{
			return null;
		}
	}
	
}
