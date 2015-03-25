package org.cld.taskmgr.test;

import java.util.HashSet;

import org.cld.datastore.DBConf;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.server.AppServerNodeInf;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;

public class MyTestServerImpl implements AppServerNodeInf{
	
	private ServerNodeImpl node;
	private SessionFactory taskSF;
	private NodeConf nc;
	private int sleepTime;

	MyTestAppConf appConf;

	@Override
	public void start(ServerNodeImpl serverNode, AppConf aconf) {
		this.node = serverNode;
		this.nc = node.getNC();
		
		DBConf dbconf = nc.getDBConf();
		DBFactory.setUp(nc.getNodeId(), TaskMgr.moduleName, dbconf);
		taskSF = (SessionFactory) DBFactory.getDBSF(nc.getNodeId(), TaskMgr.moduleName);		
		
		this.appConf = (MyTestAppConf)aconf;
		
		node.localRegister(serverNode.getNC().getNodeId(), serverNode.getNC(), new HashSet<Task>());
	}

	@Override
	public void stop(ServerNodeImpl serverNode) {
		
	}
	

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}
}
