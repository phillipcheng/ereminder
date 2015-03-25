package org.cld.taskmgr.test.tc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

public class TestTask {
	private static Logger logger =  LogManager.getLogger(TestTask.class);
	
	@Test
	public void test1TaskEquals(){
		List<Task> tl1= new ArrayList<Task>();
		List<Task> tl2;
		tl1.add(new Task("1"));
		tl1.add(new Task("2"));
		
		tl2 = new ArrayList<Task>(tl1.subList(0, 2));
		logger.info(tl2);
		boolean res = tl1.removeAll(tl2);
		
		logger.info(res);
		assertTrue(tl1.size()==0);
	}
	
	@Test
	public void testRemoveTask(){
		NodeConf nc = new NodeConf("client.properties");
		Configuration cfg = DBFactory.setUpCfg(nc.getNodeId(), TaskMgr.moduleName, nc.getDBConf());		
		SessionFactory taskMgrSF = DBFactory.setUpSF(nc.getNodeId(), TaskMgr.moduleName, cfg);
		
		Set<String> tkl = new HashSet<String>();
		tkl.add("1");
		boolean ret = TaskPersistMgr.removeTasksById(taskMgrSF, tkl);
		
		assertTrue(ret);
	}

}
