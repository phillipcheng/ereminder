package org.cld.taskmgr.test.tc;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBConf;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.test.TaskMgrTestUtil;


public class TestTaskPersist {
	private static Logger logger =  LogManager.getLogger(TestTaskPersist.class);
	
	NodeConf nc;
	SessionFactory taskSF;
	
	public void setup(){
		nc = new NodeConf("tmt.server.properties");
		DBConf dbconf =nc.getDBConf();
		DBFactory.setUp(nc.getNodeId(), TaskMgr.moduleName, dbconf);
		taskSF = (SessionFactory) DBFactory.getDBSF(nc.getNodeId(), TaskMgr.moduleName);
		TaskPersistMgr.removeAllTasks(taskSF);
	}
	
	public void tearDown(){
		
	}
	
	@Test
	public void test1AddTasks(){
		setup();
		List<Task> tkList = TaskMgrTestUtil.getTaskList(10);
		TaskPersistMgr.addTasks(taskSF, tkList);
		List<Task> telist2 = TaskPersistMgr.getAllTask(taskSF);
		assertTrue(tkList.equals(telist2));
		
	}
	
	@Test
	public void test2MoveTasks(){
		setup();
		List<Task> tl = TaskMgrTestUtil.getTaskList(10);
		List<String> tkl = TaskUtil.getKeyList(tl);
		TaskPersistMgr.addTasks(taskSF, tl);
		String toNodeId = "client1";
		assertTrue(TaskPersistMgr.moveTasks(taskSF, tkl, toNodeId));
		List<Task> telist2 = TaskPersistMgr.getMyTasks(taskSF, toNodeId);
		assertTrue(telist2.size()==10);
		List<Task> telist3 = TaskPersistMgr.getMyTasks(taskSF, nc.getNodeId());
		assertTrue(telist3.size()==0);
		
	}	
	
}
