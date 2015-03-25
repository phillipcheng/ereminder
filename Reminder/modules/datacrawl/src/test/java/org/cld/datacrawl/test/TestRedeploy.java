package org.cld.datacrawl.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlServerNode;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.server.AppServerNodeInf;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.junit.Test;

public class TestRedeploy {

	private static Logger logger =  LogManager.getLogger(TestRedeploy.class);
	
	private ServerNodeImpl sn;
	private CrawlServerNode csn;
	private NodeConf nc;
	String cfgProp = "dc.server.properties";
	String v1Prop = "dc.server.v1.properties";
	String v2Prop = "dc.server.v2.properties";
	File cfgFile;
	File cfgV1;
	File cfgV2;
	
	
	public void setup(){
		NodeConf nc = new NodeConf(cfgProp);
		String propDir = nc.getPropDir();
		cfgFile = new File(propDir, cfgProp);
		cfgV1 = new File(propDir, v1Prop);
		cfgV2 = new File(propDir, v2Prop);
		
		
		
		Node serverNode = new Node(cfgProp);
		serverNode.start(true);
		this.sn = serverNode.getServer();
		this.csn = (CrawlServerNode)serverNode.getServer().getASN();
	}
	
	@Test
	public void test0(){
		setup();
	}
	
//	@Test
//	public void test1AddTaskType(){
//		
//		setup();
//		
//		try {
//			FileUtil.copy(cfgV2, cfgFile);
//		} catch (IOException e1) {
//			logger.error("exception while copy cfg:" + cfgV2, e1);
//		}
//		
//		try {
//			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//assert 2 tasks, siteA, siteB
//		logger.info("active thread count:" + this.sn.getExe().getActiveCount());
//		assertTrue(sn.getExe().getActiveCount()==2);
//		
//		try {
//			FileUtil.copy(cfgV1, cfgFile);
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
//		logger.info("active thread count:" + this.sn.getExe().getActiveCount());
//		assertTrue(sn.getExe().getActiveCount()==1);
//		
//		try {
//			FileUtil.copy(cfgV2, cfgFile);
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
//		logger.info("active thread count:" + this.sn.getExe().getActiveCount());
//		assertTrue(sn.getExe().getActiveCount()==2);
//		
//	}
	
	
}
