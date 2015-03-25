package org.cld.booksites.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;

public class TestHadoopTask {
	
	private static Logger logger =  LogManager.getLogger(TestHadoopTask.class);
	
	private String propFile = "client1-v2.properties";
	
	@Test
	public void testMarshallUnMarshall(){
		CrawlConf cconf = CrawlTestUtil.getCConf(propFile);
		//siteconfid_bct, siteconfid_bdt
		Task t0 = cconf.getTaskMgr().getTask("hksePrd");
		t0.putParam("p1", "v1");
		String s = TaskUtil.taskToJson(t0);
		logger.info(String.format("task t:%s\n json:%s", t0, s));
		
		Object t = TaskUtil.taskFromJson(s);
		assertTrue(t0.equals(t));
	}

	@Test
	public void testMapper1(){
		CrawlConf cconf = CrawlTestUtil.getCConf(propFile);
		Task t0 = cconf.getTaskMgr().getTask("hkse-stock-basic_bct");
		List<Task> tl = new ArrayList<Task>();
		tl.add(t0);
		CrawlUtil.hadoopExecuteCrawlTasks(propFile, cconf, tl);
	}
	
	@Test
	public void testMapper2(){
		CrawlConf cconf = CrawlTestUtil.getCConf(propFile);
		Task t0 = cconf.getTaskMgr().getTask("shse-stock-basic_bct");
		List<Task> tl = new ArrayList<Task>();
		tl.add(t0);
		CrawlUtil.hadoopExecuteCrawlTasks(propFile, cconf, tl);
	}
	
	public static void main(String[] args){
		if (args.length<2){
			logger.error("usage: TestHadoopTask propFile TaskName");
			return;
		}
		
		String prop = args[0];
		String task = args[1];
		
		CrawlConf cconf = CrawlTestUtil.getCConf(prop);
		Task t0 = cconf.getTaskMgr().getTask(task);
		List<Task> tl = new ArrayList<Task>();
		tl.add(t0);
		CrawlUtil.hadoopExecuteCrawlTasks(prop, cconf, tl);
	}
}
