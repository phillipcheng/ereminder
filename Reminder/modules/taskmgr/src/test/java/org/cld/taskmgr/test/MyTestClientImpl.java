package org.cld.taskmgr.test;

import java.util.HashMap;
import java.util.List;

import org.cld.datastore.DBConf;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.RerunableTask;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.client.AppClientNodeInf;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.hibernate.SessionFactory;

public class MyTestClientImpl implements AppClientNodeInf {

	private ClientNodeImpl node;
	private SessionFactory taskSF;
	private NodeConf nc;
	private int sleepTime;
	
	private MyTestAppConf appConf;
	
	@Override
	public void start(ClientNodeImpl node, AppConf aconf) {
		this.node = node;
		this.nc = node.getNC();
		
		DBConf dbconf = nc.getDBConf();
		DBFactory.setUp(nc.getNodeId(), TaskMgr.moduleName, dbconf);
		taskSF = (SessionFactory) DBFactory.getDBSF(nc.getNodeId(), TaskMgr.moduleName);
		
		this.appConf = (MyTestAppConf)aconf;
	}

	@Override
	public void stop(ClientNodeImpl node) {
	}
	
	@Override
	public Runnable getRunnableTask(Task t, int runNum) {
		return new RerunableTask(node, appConf.getTaskMgr()
				, t, runNum, taskSF, node.getTaskInstanceManager(), appConf.isRerun(), new HashMap<String, Object>());
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
