package org.cld.datacrawl.task;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BrowseTaskType;


public class CrawlTaskConf extends Task {

	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(CrawlTaskConf.class);
	
	@Override
	public ParsedTasksDef initParsedTaskDef(){
		ParsedTasksDef ptd = TaskMgr.getParsedTasksDef(storeId);
		if (ptd!=null){
			this.setParsedTaskDef(ptd);
		}else{
			logger.error(String.format("site %s not found in taskMgr.", storeId));
		}
		return ptd;
	}
}
