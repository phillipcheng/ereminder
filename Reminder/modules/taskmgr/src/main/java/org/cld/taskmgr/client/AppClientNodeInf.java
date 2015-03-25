package org.cld.taskmgr.client;

import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.entity.Task;


public interface AppClientNodeInf {
	
	public void start(ClientNodeImpl node, AppConf aconf);
	
	public void stop(ClientNodeImpl node);	
	
	public Runnable getRunnableTask(Task t, int runNum);

	public void reload();
}
