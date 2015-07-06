package org.cld.datacrawl.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlServerNode;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.server.AppServerNodeInf;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.junit.Test;

public class TestRedeploy {
	private static Logger logger =  LogManager.getLogger(TestRedeploy.class);

	private NodeConf nc;	
	
	private ClientNodeImpl cni;
	private CrawlClientNode ccn;
	String cfgClientProp = "client1.properties";
	String v1ClientProp = "client1.v1.properties";
	String v2ClientProp = "client1.v2.properties";
	File cfgClientFile;
	File cfgClientV1;
	File cfgClientV2;
	
	private ServerNodeImpl sni;
	private CrawlServerNode csn;
	String cfgServerProp = "dc.server.properties";
	String v1ServerProp = "dc.server.v1.properties";
	String v2ServerProp = "dc.server.v2.properties";
	File cfgServerFile;
	File cfgServerV1;
	File cfgServerV2;
	
	private void setupClientConf(){
		NodeConf nc = new NodeConf(cfgClientProp);
		String propDir = nc.getPropDir();
		cfgClientFile = new File(propDir, cfgClientProp);
		cfgClientV1 = new File(propDir, v1ClientProp);
		cfgClientV2 = new File(propDir, v2ClientProp);
	}
	
	private void startClient(){
		Node clientNode = new Node(cfgClientProp);
		clientNode.start(true);
		cni = clientNode.getClient();
		ccn = (CrawlClientNode)clientNode.getClient().getACN();
	}
	
	public void setupServer(){
		NodeConf nc = new NodeConf(cfgServerProp);
		String propDir = nc.getPropDir();
		cfgServerFile = new File(propDir, cfgServerProp);
		cfgServerV1 = new File(propDir, v1ServerProp);
		cfgServerV2 = new File(propDir, v2ServerProp);
		
		Node serverNode = new Node(cfgServerProp);
		serverNode.start(true);
		sni = serverNode.getServer();
		csn = (CrawlServerNode)serverNode.getServer().getASN();
	}
	
	@Test
	public void test1ChangeThread(){
		setupClientConf();
		//start with v2 conf
		try {
			FileUtils.copyFile(cfgClientV2, cfgClientFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg:" + cfgClientV2, e1);
		}
		startClient();
		//assert thread number is 2
		logger.info("active thread count:" + this.cni.getTaskInstanceManager().getExe().getCorePoolSize());
		assertTrue(cni.getTaskInstanceManager().getExe().getCorePoolSize()==2);
		//change conf to v1
		try {
			FileUtils.copyFile(cfgClientV1, cfgClientFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg-v1 to cfg.", e1);
		}
		
		try {
			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//assert thread number is 1
		logger.info("active thread count:" + this.cni.getTaskInstanceManager().getExe().getCorePoolSize());
		assertTrue(cni.getTaskInstanceManager().getExe().getCorePoolSize()==1);
		
		//change conf to v2
		try {
			FileUtils.copyFile(cfgClientV2, cfgClientFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg-v2 to cfg.", e1);
		}
		
		try {
			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//assert thread number is 2
		logger.info("active thread count:" + this.cni.getTaskInstanceManager().getExe().getCorePoolSize());
		assertTrue(cni.getTaskInstanceManager().getExe().getCorePoolSize()==2);
	}
	
	@Test
	public void test1AddTaskType(){
		//now server does not start any task, all will be dispatched to clients
//		setupServer();
//		
//		try {
//			FileUtil.copy(cfgServerV2, cfgServerFile);
//		} catch (IOException e1) {
//			logger.error("exception while copy cfg:" + cfgServerV2, e1);
//		}
//		
//		try {
//			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//assert 2 tasks, siteA, siteB
//		logger.info("active thread count:" + this.sni.getExe().getActiveCount());
//		assertTrue(sni.getExe().getActiveCount()==2);
//		
//		try {
//			FileUtil.copy(cfgServerV1, cfgServerFile);
//		} catch (IOException e1) {
//			logger.error("exception while copy cfg-v1 to cfg.", e1);
//		}
//		
//		try {
//			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//assert 1 task, siteA
//		logger.info("active thread count:" + this.sni.getExe().getActiveCount());
//		assertTrue(sni.getExe().getActiveCount()==1);
//		
//		try {
//			FileUtil.copy(cfgServerV2, cfgServerFile);
//		} catch (IOException e1) {
//			logger.error("exception while copy cfg-v2 to cfg.", e1);
//		}
//		
//		try {
//			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		//assert 2 tasks, site A, siteB
//		logger.info("active thread count:" + this.sni.getExe().getActiveCount());
//		assertTrue(sni.getExe().getActiveCount()==2);
	}
}
