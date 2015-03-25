package org.cld.taskmgr.test.tc;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class TestTaskPriority {

	private static Logger logger =  LogManager.getLogger(TestTaskPriority.class);
	
	/**
	 * 2 threads
	 * long task run 10 s
	 * short task run 2 s
	 * 
	 * 1. each task for 2 rounds
	 * 2. thread pool size is 3 (set thread size to 2, max is thread.size + 1, so becomes 3)
	 * 3. add (s1, l1, l2)
	 * 4. In the 1st round, after s1 finishes and before l1 finishes, add l3
	 * 
	 * to check s1 round 2 is started after l3 round 1, because round 1 has higher priority
	 * 
	 * task adding order expected:
	 * s1, l1, l2, s1, l3, l1, l2, l3
	 * 
	 * task execution order expected:
	 * s1, l1, l2, l3, s1, l1, l2, l3
	 */
	@Test
	public void test1TaskPriority(){
		int runRound = 4;
		int tasksPerRound = 4;
		
		
		Set<Task> tl =  new LinkedHashSet<Task>();
		List<Task> client1TL;
		List<Task> serverTL;

		tl.add(new Task("short1"));
		tl.add(new Task("long1"));
		tl.add(new Task("long2"));
		
		
		Node serverNode = new Node("tcn.server.properties");
		serverNode.start(true);
		ServerNodeImpl server = serverNode.getServer();
		SessionFactory serverSF = (SessionFactory) DBFactory.getDBSF(server.getNC().getNodeId(), TaskMgr.moduleName);
		Map<String, Set<String>> dataKeys = server.getPDBMgr().getDataKeys();
		
		TaskPersistMgr.removeAllTasks(serverSF);
		TaskPersistMgr.removeAllBS(serverSF);
		
		Node clientNode1 = new Node("tcn.client1.properties");
		clientNode1.start(true);
		ClientNodeImpl client1 = clientNode1.getClient();
		client1.getTaskInstanceManager().setRunRound(runRound);
		
		SessionFactory client1SF = (SessionFactory) DBFactory.getDBSF(client1.getNC().getNodeId(), TaskMgr.moduleName);
		TaskPersistMgr.addTasks(client1SF, tl);
		Set<String> tks = TaskUtil.getKeySet(tl);
		server.addTasks(server.getNC().getNodeId(), tks);
		
		//make sure s1 is added for the 2nd time
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("InterruptedException", e);
		}
		
		//add l3
		Set<Task> tl2 =  new LinkedHashSet<Task>();
		tl2.add(new Task("long3"));
		TaskPersistMgr.addTasks(client1SF, tl2);
		tks = TaskUtil.getKeySet(tl2);
		server.addTasks(server.getNC().getNodeId(), tks);
		
		logger.info("logic server:" + dataKeys.get(server.getNC().getNodeId()).size() + ":" + dataKeys.get(server.getNC().getNodeId()));
		logger.info("logic client1:" + dataKeys.get(client1.getNC().getNodeId()).size() + ":" + dataKeys.get(client1.getNC().getNodeId()));
		
		serverTL = TaskPersistMgr.getMyTasks(serverSF, server.getNC().getNodeId());
		client1TL = TaskPersistMgr.getMyTasks(client1SF, client1.getNC().getNodeId());
		
		logger.info("physical server:" + serverTL.size() + ":" + serverTL);
		logger.info("physical client1:" + client1TL.size() + ":" + client1TL);

		
		logger.info("physical client1 tasks:" + client1.getTaskInstanceManager().getExe().getTaskCount());
		//assertTrue(client1.getExe().getTaskCount()==10);
		
		while (client1.getTaskInstanceManager().getExe().getCompletedTaskCount() < tasksPerRound*runRound){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		serverNode.stop();
		clientNode1.stop();
	}
	
	

}
