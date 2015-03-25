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
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class TestRemoteDDB {
	private static Logger logger =  LogManager.getLogger(TestRemoteDDB.class);
	
	/**
	 * Test physical move between nodes
	 * node/threads: s:0, c1:5, c2:5
	 * operation/results: dist s:10 --> s:10 --> start c1 --> s:0, c1:10 --> start c2 --> s:0, c1:5, c2:5 -->
	 * 				stop c2 --> s:0, c1:10
	 */
	@Test
	public void test1PhysicalStartStop(){
		Set<Task> tl =  new LinkedHashSet<Task>();
		List<Task> client1TEList;
		List<Task> client2TEList1, client2TEList2;
		List<Task> serverTEList;
		for (int i=0; i<10; i++){
			tl.add(new Task("a"+ i));
		}
		
		Node serverNode = new Node("tmt.server.properties");
		serverNode.start(true);
		
		ServerNodeImpl server = serverNode.getServer();
		SessionFactory serverSF = (SessionFactory) DBFactory.getDBSF(server.getNC().getNodeId(), TaskMgr.moduleName);
		
		Map<String, Set<String>> dataKeys = server.getPDBMgr().getDataKeys();
		
		logger.info("step1: s1 dist 10, assert s1 get 10");
		TaskPersistMgr.addTasks(serverSF, tl);
		Set<String> tks = TaskUtil.getKeySet(tl);
		server.addTasks(serverNode.getServer().getNC().getNodeId(), tks);
		serverTEList = TaskPersistMgr.getMyTasks(serverSF, server.getNC().getNodeId());
		logger.info("logic server:" + dataKeys.get(server.getNC().getNodeId()).size() + ":" + dataKeys.get(server.getNC().getNodeId()));
		logger.info("physical server:" + serverTEList.size() + ":" + serverTEList);
		assertTrue(dataKeys.get(server.getNC().getNodeId()).size()==10);
		assertTrue(serverTEList.size()==10);
		
		
		logger.info("step2: c1 joins, assert c1 get 10");
		Node clientNode1 = new Node("tmt.client1.properties");
		clientNode1.start(true);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ClientNodeImpl client1 = clientNode1.getClient();
		SessionFactory client1SF = (SessionFactory) DBFactory.getDBSF(client1.getNC().getNodeId(), TaskMgr.moduleName);
		serverTEList = TaskPersistMgr.getMyTasks(serverSF, server.getNC().getNodeId());
		client1TEList = TaskPersistMgr.getMyTasks(client1SF, client1.getNC().getNodeId());
		logger.info("logic server:" + dataKeys.get(server.getNC().getNodeId()).size() + ":" + dataKeys.get(server.getNC().getNodeId()));
		logger.info("logic client1:" + dataKeys.get(client1.getNC().getNodeId()).size() + ":" + dataKeys.get(client1.getNC().getNodeId()));
		
		logger.info("physical server:" + serverTEList.size() + ":" + serverTEList);
		logger.info("physical client1:" + client1TEList.size() + ":" + client1TEList);
		//logger.info("physical client1 queue:" + client1.getQ().size() + ":" + client1.getQ());
		logger.info("physical client1 tasks:" + client1.getTaskInstanceManager().getExe().getTaskCount());
		assertTrue(dataKeys.get(server.getNC().getNodeId()).size()==0);
		assertTrue(dataKeys.get(client1.getNC().getNodeId()).size()==10);
		assertTrue(serverTEList.size()==0);
		assertTrue(client1TEList.size()==10);
		assertTrue(client1.getTaskInstanceManager().getExe().getTaskCount()==10);
		
		logger.info("step3: c2 joins, assert c2 get 5 and c1 keeps 5");
		Node clientNode2 = new Node("tmt.client2.properties");		
		clientNode2.start(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ClientNodeImpl client2 = clientNode2.getClient();
		SessionFactory client2SF = (SessionFactory) DBFactory.getDBSF(client2.getNC().getNodeId(), TaskMgr.moduleName);
		serverTEList = TaskPersistMgr.getMyTasks(serverSF, server.getNC().getNodeId());
		client1TEList = TaskPersistMgr.getMyTasks(client1SF, client1.getNC().getNodeId());
		client2TEList1 = TaskPersistMgr.getMyTasks(client2SF, client2.getNC().getNodeId());
		logger.info("logic server:" + dataKeys.get(server.getNC().getNodeId()).size() + ":" + dataKeys.get(server.getNC().getNodeId()));
		logger.info("logic client1:" + dataKeys.get(client1.getNC().getNodeId()).size() + ":" + dataKeys.get(client1.getNC().getNodeId()));
		logger.info("logic client2:" + dataKeys.get(client2.getNC().getNodeId()).size() + ":" + dataKeys.get(client2.getNC().getNodeId()));
		
		logger.info("physical server:" + serverTEList.size() + ":" + serverTEList);
		logger.info("physical client1:" + client1TEList.size() + ":" + client1TEList);
		logger.info("physical client2:" + client2TEList1.size() + ":" + client2TEList1);
		assertTrue(dataKeys.get(server.getNC().getNodeId()).size()==0);
		assertTrue(dataKeys.get(client1.getNC().getNodeId()).size()==5);
		assertTrue(dataKeys.get(client2.getNC().getNodeId()).size()==5);
		assertTrue(serverTEList.size()==0);
		assertTrue(client1TEList.size()==5);
		//assertTrue(client1.getExe().getTaskCount()==5);
		assertTrue(client2TEList1.size()==5);
		//assertTrue(client2.getExe().getTaskCount()==5);
		
		logger.info("step4: c2 stops, assert all tasks goes to c1");
		client2.stop();
		try {
			//let the keep-alive thread to detect
			Thread.sleep(16000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverTEList = TaskPersistMgr.getMyTasks(serverSF, server.getNC().getNodeId());
		client1TEList = TaskPersistMgr.getMyTasks(client1SF, client1.getNC().getNodeId());
		logger.info("logic server:" + dataKeys.get(server.getNC().getNodeId()).size() + ":" + dataKeys.get(server.getNC().getNodeId()));
		logger.info("logic client1:" + dataKeys.get(client1.getNC().getNodeId()).size() + ":" + dataKeys.get(client1.getNC().getNodeId()));
		logger.info("physical server:" + serverTEList.size() + ":" + serverTEList);
		logger.info("physical client1:" + client1TEList.size() + ":" + client1TEList);
		
		//assertTrue(serverTEList.size()==10);
		assertTrue(client1TEList.size()==10);
		//assertTrue(client1.getExe().getTaskCount()==10);
		Task t = new Task("a1");
		assertTrue(client1TEList.contains(t));
		
		logger.info("step5: c2 starts again, assert getting the same tasks as before");
		clientNode2.start(true);
		try {
			//let the keep-alive thread to detect
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client2 = clientNode2.getClient();
		client2SF = (SessionFactory) DBFactory.getDBSF(client2.getNC().getNodeId(), TaskMgr.moduleName);
		serverTEList = TaskPersistMgr.getMyTasks(serverSF, server.getNC().getNodeId());
		client1TEList = TaskPersistMgr.getMyTasks(client1SF, client1.getNC().getNodeId());
		client2TEList2 = TaskPersistMgr.getMyTasks(client2SF, client2.getNC().getNodeId());
		logger.info("logic server:" + dataKeys.get(server.getNC().getNodeId()).size() + ":" + dataKeys.get(server.getNC().getNodeId()));
		logger.info("logic client1:" + dataKeys.get(client1.getNC().getNodeId()).size() + ":" + dataKeys.get(client1.getNC().getNodeId()));
		logger.info("logic client2:" + dataKeys.get(client2.getNC().getNodeId()).size() + ":" + dataKeys.get(client2.getNC().getNodeId()));
		
		logger.info("physical server:" + serverTEList.size() + ":" + serverTEList);
		logger.info("physical client1:" + client1TEList.size() + ":" + client1TEList);
		logger.info("physical client2:" + client2TEList2.size() + ":" + client2TEList2);
		assertTrue(dataKeys.get(server.getNC().getNodeId()).size()==0);
		assertTrue(dataKeys.get(client1.getNC().getNodeId()).size()==5);
		assertTrue(dataKeys.get(client2.getNC().getNodeId()).size()==5);
		//assertTrue(serverTEList.size()==10);
		assertTrue(client1TEList.size()==5);
		//assertTrue(client1.getExe().getTaskCount()==5);
		assertTrue(client2TEList2.size()==5);
		assertTrue(client2TEList1.equals(client2TEList2));
		assertTrue(client2.getTaskInstanceManager().getExe().getTaskCount()==5);
		
		serverNode.stop();
		clientNode1.stop();
		clientNode2.stop();
	}
	

}
