package org.cld.taskmgr.test.tc;

import static org.junit.Assert.assertTrue;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.client.ClientKeepAliveThread;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.junit.Test;

public class TestKeepAlive {
	private static Logger logger =  LogManager.getLogger(TestKeepAlive.class);
	
	private Node serverNode;
	private Node clientNode1;
	
	public void setUp(){
		serverNode = new Node("tmt.server.properties");
		serverNode.start(true);
		
		clientNode1 = new Node("tmt.client1.properties");
		clientNode1.start(true);
	}
	
	public void tearDown(){
		serverNode.stop();
		clientNode1.stop();
	}
	
	@Test
	public void test1Normal(){
		setUp();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("interrupted exception.", e);
		}
		
		assertTrue(clientNode1.getClient().getConnState()==ClientKeepAliveThread.CN_STATE_CONNECTED);
		
		tearDown();
	}
	
	@Test
	public void test2ClientStopRevive(){
		setUp();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		ClientNodeImpl client1 = clientNode1.getClient();
		client1.stopC();
		
		//server kicked it out
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//client2 re-register by keep-alive-thread
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(clientNode1.getClient().getConnState()==ClientKeepAliveThread.CN_STATE_CONNECTED);
		
		serverNode.stop();
	}
	
	@Test
	public void test3ClientUnregisterRevive(){
		setUp();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		ClientNodeImpl client1 = clientNode1.getClient();
		ServerNodeImpl server = serverNode.getServer();
		
		server.unregister(client1.getNC().getNodeId(), client1.getNC().getNodeId());
		
		//client2 re-register by keep-alive-thread
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(clientNode1.getClient().getConnState()==ClientKeepAliveThread.CN_STATE_CONNECTED);
		
		tearDown();
	}

}
