package org.cld.taskmgr.server;

import org.cld.taskmgr.AppConf;

public interface AppServerNodeInf{
	
	public void start(ServerNodeImpl serverNode, AppConf aconf);
	
	public void stop(ServerNodeImpl serverNode);
	
	public void reload();
}
