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
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class TestDDB {
	private static Logger logger =  LogManager.getLogger(TestDDB.class);
	
	private NodeConf nc;
	private ServerNodeImpl serverNode;
	private Set<Task> emptyTL = new LinkedHashSet<Task>(); 
	
	public void setupLocalServer(Set<Task> tl){		
		nc = new NodeConf();
		nc.setNodeId("server");
		serverNode = new ServerNodeImpl(nc, true);
		serverNode.localRegister(nc.getNodeId(), nc, tl);
	}
	
	public void setupLocalServerWithEmptyTasks(){
		Set<Task> emptyTL = new LinkedHashSet<Task>();
		setupLocalServer(emptyTL);
	}
	
	/**
	 * Test Join and Remove (Local)
	 * node/threads: c1:1000, c2:500, c3:500
	 * tasks: c1:1000, c2:500 --> join c3:0 --> c1:750, c2:375, c3:375 -->remove c3 --> c1:1000, c2:500
	 */
	@Test
	public void test1JoinRemove(){
		setupLocalServerWithEmptyTasks();
		
		NodeConf nc1 = new NodeConf();
		NodeConf nc2 = new NodeConf();
		Set<Task> kl1 = new LinkedHashSet<Task>();
		Set<Task> kl2 = new LinkedHashSet<Task>();
		
		nc1.setNodeId("client1");
		nc2.setNodeId("client2");
		nc1.setThreadSize(1000);
		nc2.setThreadSize(500);
		int i=0;
		for (i=0; i<nc1.getThreadSize(); i++){
			kl1.add(new Task(i+""));
		}
		for (i=nc1.getThreadSize(); i< nc1.getThreadSize() + nc2.getThreadSize(); i++){
			kl2.add(new Task(i+""));
		}
		
		serverNode.localRegister(nc1.getNodeId(), nc1, kl1);
		serverNode.localRegister(nc2.getNodeId(), nc2, kl2);
		
		NodeConf nc3 = new NodeConf();
		Set<Task> kl3 = new LinkedHashSet<Task>();
		nc3.setNodeId("client3");
		nc3.setThreadSize(500);
		
		serverNode.localRegister(nc3.getNodeId(), nc3, kl3);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 750);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 375);
		assertTrue(dataKeys.get(nc3.getNodeId()).size() == 375);
		
		serverNode.localUnregister(nc3.getNodeId(), nc3.getNodeId());
		
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 1000);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 500);
		
	}
	
	/**
	 * Test Distribute (Local)
	 * node/threads: c1:100, c2:50
	 * tasks: c1:649, c2:325 --> dist s:51 --> c1:683, c2:342
	 */
	@Test
	public void test2Distribute(){
		setupLocalServerWithEmptyTasks();
		
		NodeConf nc1 = new NodeConf();
		NodeConf nc2 = new NodeConf();
		Set<Task> kl1 = new LinkedHashSet<Task>();
		Set<Task> kl2 = new LinkedHashSet<Task>();
		
		nc1.setNodeId("client1");
		nc2.setNodeId("client2");
		nc1.setThreadSize(100);
		nc2.setThreadSize(50);
		
		int i=0;
		for (i=0; i<649; i++){
			kl1.add(new Task("a"+ i));
		}
		assertTrue(kl1.size() == 649);
		kl1.add(new Task("a20"));
		assertTrue(kl1.size() == 649);
		
		for (i=0; i<325; i++){
			kl2.add(new Task("b"+ i));
		}
		
		//initialize, only for testing
		serverNode.localRegisterNoDistribute(nc1.getNodeId(), nc1, kl1);
		serverNode.localRegisterNoDistribute(nc2.getNodeId(), nc2, kl2);
		
		Set<Task> kl3 = new LinkedHashSet<Task>();
		for (i=0; i<51; i++){
			kl3.add(new Task("c"+ i));
		}
		serverNode.localDistributeKeyList(kl3);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		logger.info(dataKeys.get(nc1.getNodeId()).size());
		logger.info(dataKeys.get(nc2.getNodeId()).size());
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 683);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 342);
	}

	/**
	 * Test local register and unregister
	 * node/threads: s:0, c1:5, c2:5, c3:5
	 * tasks: s:0, c1:0, c2:0 --> dist s:1 --> s:0, c1:0, c2:1 --> dist s:1 --> s:0, c1:1, c2:1 
	 * 		--> join c3:0 --> s:0, c1:0, c2:1, c3:1 --> local-unregist c3 --> s:0, c1:1, c2:1 --> 
	 * 		local-unregist c2 --> s:0, c1:2 --> local-unregist c1 --> s:2 --> local-regist c1 --> s:0, c1:2 -->
	 * 		local-regist c2 --> s:0, c1:1, c2:1
	 */
	@Test
	public void test3NodesLocal(){
		setupLocalServerWithEmptyTasks();
		
		NodeConf nc1 = new NodeConf();
		NodeConf nc2 = new NodeConf();
		NodeConf nc3 = new NodeConf();
		
		nc1.setNodeId("client1");
		nc2.setNodeId("client2");
		nc3.setNodeId("client3");
		
		nc1.setThreadSize(5);
		nc2.setThreadSize(5);
		nc3.setThreadSize(5);
		
		serverNode.localRegister(nc1.getNodeId(), nc1, emptyTL);
		serverNode.localRegister(nc2.getNodeId(), nc2, emptyTL);
		
		
		Set<Task> tl = new LinkedHashSet<Task>();
		tl.add(new Task("1"));
		
		
		serverNode.localDistributeKeyList(tl);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 1);
		
		tl.clear();
		tl.add(new Task("2"));
		
		serverNode.localDistributeKeyList(tl);
		
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 1);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 1);
		

		serverNode.localRegister(nc3.getNodeId(), nc3, emptyTL);
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 1);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 1);
		assertTrue(dataKeys.get(nc3.getNodeId()).size() == 0);
		
		serverNode.localUnregister(nc3.getNodeId(), nc3.getNodeId());
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 1);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 1);
		logger.info("unregistered:" + nc3);
		
		serverNode.localUnregister(nc2.getNodeId(), nc2.getNodeId());
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 2);
		logger.info("unregistered:" + nc2);
		
		serverNode.localUnregister(nc1.getNodeId(), nc1.getNodeId());
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 2);
		logger.info("unregistered:" + nc1);
		
		serverNode.localRegister(nc1.getNodeId(), nc1, emptyTL);
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 2);
		
		serverNode.localRegister(nc2.getNodeId(), nc2, emptyTL);
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 1);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 1);
		
	}
	
	/**
	 * Test First Distribute (Local)
	 * node/threads: s:0, c1:25
	 * tasks: s:0 --> dist s:25 --> s:25 -->join c1 --> s:0, c1:25
	 */
	@Test
	public void test4FirstDistribute(){
		setupLocalServerWithEmptyTasks();
		
		Set<Task> kl1 = new LinkedHashSet<Task>();
		Set<Task> emptyKL = new LinkedHashSet<Task>();
		
		int i=0;
		for (i=0; i<25; i++){
			kl1.add(new Task("a"+ i));
		}
		serverNode.localDistributeKeyList(kl1);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 25);

		
		NodeConf nc1 = new NodeConf();
		nc1.setNodeId("client1");
		nc1.setThreadSize(25);
		
		serverNode.localRegister(nc1.getNodeId(), nc1, emptyKL);
		
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		logger.info(nc1.getNodeId() + ":" + dataKeys.get(nc1.getNodeId()).size());
		logger.info(nc.getNodeId() + ":" + dataKeys.get(nc.getNodeId()).size());
		
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 25);
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
	}
	
	
	/**
	 * Test multiple distribution results
	 * threads: s:0, c1:10, c2:15
	 * node/threads: s:0 --> local-regist-no-dist c1:2823 --> local-regist-no-dist c2:4169 --> dist s:57
	 * 					--> s:0, c1:2819, c2:4230 
	 * Notice: There is distribution from c1 to c2, as well as from s to c2, so called multiple-DR
	 */
	@Test
	public void test5MultipleDR(){
		setupLocalServerWithEmptyTasks();
		
		Set<Task> kl1 = new LinkedHashSet<Task>();		
		int i=0;
		for (i=0; i<2823; i++){
			kl1.add(new Task("a"+ i));
		}
		NodeConf nc1 = new NodeConf();
		nc1.setNodeId("client1");
		nc1.setThreadSize(10);
		
		serverNode.localRegisterNoDistribute(nc1.getNodeId(), nc1, kl1);
		
		Set<Task> kl2 = new LinkedHashSet<Task>();		
		for (i=0; i<4169; i++){
			kl2.add(new Task("b"+ i));
		}
		NodeConf nc2 = new NodeConf();
		nc2.setNodeId("client2");
		nc2.setThreadSize(15);
		
		serverNode.localRegisterNoDistribute(nc2.getNodeId(), nc2, kl2);
		
		
		Set<Task> kl3 = new LinkedHashSet<Task>();
		for (i=0; i<57; i++){
			kl3.add(new Task("c"+ i));
		}
		
		serverNode.localDistributeKeyList(kl3);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		dataKeys = serverNode.getPDBMgr().getDataKeys();
		
		logger.info(nc1.getNodeId() + ":" + dataKeys.get(nc1.getNodeId()).size());
		logger.info(nc2.getNodeId() + ":" + dataKeys.get(nc2.getNodeId()).size());
		logger.info(nc.getNodeId() + ":" + dataKeys.get(nc.getNodeId()).size());
		
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 2819);
		assertTrue(dataKeys.get(nc2.getNodeId()).size() == 4230);
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
	}
	
	/**
	 * Test initialized with "register with tasks"
	 * node/threads: s:0
	 * operation/results: local-regist s:57 --> s:57
	 */
	@Test
	public void test6LocalFirstRegisterWithTasks(){
		
		Set<Task> tl =  new LinkedHashSet<Task>();
		for (int i=0; i<57; i++){
			tl.add(new Task("a"+ i));
		}
		setupLocalServer(tl);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		dataKeys = serverNode.getPDBMgr().getDataKeys();		
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 57);
	}
	
	/**
	 * Test Distribute
	 * node/threads: c1:100, c2:50
	 * tasks: c1:649, c2:325 --> dist s:51 --> c1:683, c2:342
	 */
	@Test
	public void test7DistributeServerTS2(){
		Set<Task> emptyTL = new LinkedHashSet<Task>();
		NodeConf nc = new NodeConf();
		nc.setNodeId("server");
		nc.setThreadSize(2);
		ServerNodeImpl serverNode = new ServerNodeImpl(nc, true);
		serverNode.localRegister(nc.getNodeId(), nc, emptyTL);
		
		NodeConf nc1 = new NodeConf();
		nc1.setNodeId("client1");
		nc1.setThreadSize(1);
		serverNode.localRegister(nc1.getNodeId(), nc1, emptyTL);
		
		Set<Task> kl1 = new LinkedHashSet<Task>();
		kl1.add(new Task("a1"));
		serverNode.localDistributeKeyList(kl1);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();
		
		assertTrue(dataKeys.get(nc1.getNodeId()).size() == 1);
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 0);
		
		assertTrue(nc.getThreadSize()==2);
		
	}
	
	/**
	 * Test distribute empty task list
	 * node/threads: s:0
	 * operation/results: local-regist s:57 --> dist s:0 --> s:57
	 */
	@Test
	public void test8DistributeEmptyTask(){
		Set<Task> emptyTL =  new LinkedHashSet<Task>();
		
		Set<Task> tl =  new LinkedHashSet<Task>();
		for (int i=0; i<57; i++){
			tl.add(new Task("a"+ i));
		}
		setupLocalServer(tl);
		
		Map<String, Set<String>> dataKeys = serverNode.getPDBMgr().getDataKeys();	
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 57);
		
		serverNode.localDistributeKeyList(emptyTL);
		dataKeys = serverNode.getPDBMgr().getDataKeys();		
		assertTrue(dataKeys.get(nc.getNodeId()).size() == 57);
	}
	

}
