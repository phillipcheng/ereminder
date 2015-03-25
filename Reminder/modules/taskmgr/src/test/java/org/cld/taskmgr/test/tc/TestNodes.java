package org.cld.taskmgr.test.tc;

import static org.junit.Assert.*;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.junit.Test;

public class TestNodes {
	private static Logger logger =  LogManager.getLogger(TestNodes.class);
	
	private Node server;
	private Node client1;
	
	public void setUp(){
		server = new Node("tcn.server.properties");
		client1 = new Node("tcn.client1.properties");
		
		server.start(true);
		client1.start(true);
	}
	
	public void tearDown(){
		server.stop();
		client1.stop();
	}
	
	@Test
	public void testNodesRunRound(){
		setUp();
		
		ServerNodeImpl serverImpl = server.getServer();
		ClientNodeImpl client1Impl = client1.getClient();
		
		ThreadPoolExecutor clientExe = client1Impl.getTaskInstanceManager().getExe();
		clientExe.shutdown();
		try {
			boolean res = clientExe.awaitTermination(1000, TimeUnit.SECONDS);
			assertTrue(res); //not terminated by timeout, but actually all the tasks completed
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tearDown();
	}

}
