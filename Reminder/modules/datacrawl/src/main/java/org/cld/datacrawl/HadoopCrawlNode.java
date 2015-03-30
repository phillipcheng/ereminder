package org.cld.datacrawl;

import java.util.List;

import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.client.AppHadoopClientNodeInf;
import org.cld.taskmgr.entity.Task;

public class HadoopCrawlNode implements AppHadoopClientNodeInf {

	private CrawlConf cconf;
	
	@Override
	public void start(AppConf aconf, String crawlProperties) {
		this.cconf = (CrawlConf)aconf;
		List<Task> telist = cconf.getTaskMgr().getStartableTasks();
		//use the node Id as the source name
		CrawlUtil.hadoopExecuteCrawlTasks(crawlProperties, cconf, telist, cconf.getNodeConf().getNodeId());
	}
}
