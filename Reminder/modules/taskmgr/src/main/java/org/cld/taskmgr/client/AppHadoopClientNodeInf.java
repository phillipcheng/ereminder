package org.cld.taskmgr.client;

import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.entity.Task;


public interface AppHadoopClientNodeInf {
	
	public void start(AppConf aconf, String crawlProperties);
}
