package org.cld.taskmgr.test.tc;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBException;
import org.cld.taskmgr.LocalTaskManager;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.test.MyTestClientImpl;
import org.cld.taskmgr.test.TaskMgrTestUtil;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class TestLocalTaskMgr {
	private static Logger logger =  LogManager.getLogger(TestLocalTaskMgr.class);
	
	NodeConf nc;
	Node clientNode;
	MyTestClientImpl acn;
	LocalTaskManager ltm;
	SessionFactory taskSF;
	
	public void setup(){
		nc = new NodeConf("tmt.client1.properties");
		
		clientNode = new Node("tmt.client1.properties");
		
		clientNode.start(true);
		acn = (MyTestClientImpl) clientNode.getClient().getACN();
		acn.setSleepTime(50);
		
		ltm = new LocalTaskManager(nc);
		ltm.setup(acn, nc);
		taskSF=ltm.getTaskSF();
		
		TaskPersistMgr.removeAllTasks(taskSF);
		TaskPersistMgr.removeAllBS(taskSF);
	}
	
	public void tearDown(){
		clientNode.stop();
	}
	
	
	
	public void assertBSVersion(List<Task> tl, int v){
		try {
			for (int i= 0; i<tl.size(); i++){
				Task t= tl.get(i);
				TaskStat sbs = TaskPersistMgr.getLatestStat(taskSF, t.getId());
				assertTrue(sbs.getRunRound() == v);
			}
		} catch (DBException e) {
			logger.error("dbexception", e);
		}
	}

	
	@Test
	public void testAddTasks(){
		setup();
		int size=1000;
		for (int i=0; i<2; i++){
			List<Task> tl = TaskMgrTestUtil.getTaskList(size);
			List<String> tkl = TaskUtil.getKeyList(tl);
			TaskPersistMgr.addTasks(taskSF, tl);
			ltm.clientAddTasks(tkl);
			
			while (ltm.getExe().getCompletedTaskCount() < size*(i+1) ){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			assertBSVersion(tl,i+1);
		}
		tearDown();
	}
	
	@Test
	public void testRemoveTask(){
		setup();
		List<Task> tl = TaskMgrTestUtil.getTaskList(5);
		List<String> tkl = TaskUtil.getKeyList(tl);
		TaskPersistMgr.addTasks(taskSF, tl);
		ltm.clientAddTasks(tkl);
		ltm.clientRemoveTasks(tkl);
		
		while (ltm.getExe().getCompletedTaskCount() < 5 ){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		tearDown();
	}
}
