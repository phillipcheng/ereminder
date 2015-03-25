package org.cld.taskmgr.test.tc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.cld.taskmgr.test.MyTestClientImpl;
import org.junit.Test;

public class TestHotDeployment {
	
	private static Logger logger =  LogManager.getLogger(TestHotDeployment.class);
	
	private ClientNodeImpl cn;
	private MyTestClientImpl acn;
	private NodeConf nc;
	String cfgProp = "client.properties";
	String cfgV1Prop = "client.v1.properties";
	String cfgV2Prop = "client.v2.properties";
	File cfgPropFile;
	File cfgV1PropFile;
	File cfgV2PropFile;
	Node clientNode;
	
	String logProp = "log4j.properties";
	String logV1Prop = "log4j.v1.properties";
	String logV2Prop = "log4j.v2.properties";
	File logPropFile;
	File logV1PropFile;
	File logV2PropFile;
	
	public void setup(){
		NodeConf nc = new NodeConf("client.properties");
		String propDir = nc.getPropDir();
		cfgPropFile = new File(propDir, cfgProp);
		cfgV1PropFile = new File(propDir, cfgV1Prop);
		cfgV2PropFile = new File(propDir, cfgV2Prop);
		
		logPropFile = new File(propDir, logProp);
		logV1PropFile = new File(propDir, logV1Prop);
		logV2PropFile = new File(propDir, logV2Prop);
		
		clientNode = new Node("client.properties");
		clientNode.start(true);
		this.cn = clientNode.getClient();
		this.nc = cn.getNC();
		this.acn = (MyTestClientImpl)cn.getACN();
	}
	
	public void tearDown(){
		clientNode.stop();
	}
	
	@Test
	public void test1ThreadsChanged(){
		setup();
		
		try {
			FileUtils.copyFile(cfgV2PropFile, cfgPropFile);
			//FileUtils.touch(cfgPropFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg:" + cfgV2PropFile, e1);
		}
		
		try {
			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//assert 5 threads
		
		logger.info("thread size:" + cn.getTaskInstanceManager().getExe().getCorePoolSize());
		assertTrue(cn.getTaskInstanceManager().getExe().getCorePoolSize()==5);
		
		try {
			FileUtils.copyFile(cfgV1PropFile, cfgPropFile);
			//FileUtils.touch(cfgPropFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg-v1 to cfg.", e1);
		}
		
		try {
			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//assert 2 threads
		logger.info("thread size:" + cn.getTaskInstanceManager().getExe().getCorePoolSize());
		assertTrue(cn.getTaskInstanceManager().getExe().getCorePoolSize()==2);
		
		
		try {
			FileUtils.copyFile(cfgV2PropFile, cfgPropFile);
			//FileUtils.touch(cfgPropFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg-v2 to cfg.", e1);
		}
		
		try {
			Thread.sleep(NodeConf.CONF_WATCH_PERIOD + 3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//assert 5 threads
		logger.info("thread size:" + cn.getTaskInstanceManager().getExe().getCorePoolSize());
		assertTrue(cn.getTaskInstanceManager().getExe().getCorePoolSize()==5);
		
		//
		tearDown();
	}
	
	@Test
	public void test2Log4JChanged(){
		setup();
		
		//silent
		try {
			FileUtils.copyFile(logV2PropFile, logPropFile);
			FileUtils.touch(logPropFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg:" + logV2PropFile, e1);
		}
		
		try {
			Thread.sleep(2* NodeConf.CONF_WATCH_PERIOD);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertFalse(logger.isDebugEnabled());
		logger.debug("debug message.");
		logger.info("info message.");
		logger.error("error message.");
		//verbose
		try {
			FileUtils.copyFile(logV1PropFile, logPropFile);
			FileUtils.touch(logPropFile);
		} catch (IOException e1) {
			logger.error("exception while copy cfg:" + logV1PropFile, e1);
		}
		
		try {
			Thread.sleep(2 * NodeConf.CONF_WATCH_PERIOD);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(logger.isDebugEnabled());
		logger.debug("debug message.");
		logger.info("info message.");
		logger.error("error message.");
		
		
		tearDown();
	}
	
}
