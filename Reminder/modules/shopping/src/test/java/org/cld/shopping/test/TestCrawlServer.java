package org.cld.shopping.test;


import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlServerNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.DataCrawl;
import org.cld.datacrawl.entity.CrawlPersistMgr;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrsCatStat;
import org.cld.datacrawl.task.BrsDetailStat;
import org.cld.datastore.DBConf;
import org.cld.datastore.DBException;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskOperation;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;

public class TestCrawlServer{
	private static Logger logger =  LogManager.getLogger(TestCrawlServer.class);
	
	NodeConf nc = new NodeConf("server.properties");
	CrawlConf cconf = new CrawlConf("server.properties", nc);
	
	BrsDetailStat bds;
	BrsCatStat bcs;
	BrowseDetailTaskConf bdt;
	BrowseCategoryTaskConf bct;
	
	ICategoryAnalyze ca;
	
	
	ServerNodeImpl sn;
	CrawlServerNode csn;
	
	protected void setUp(){
		
		sn = new ServerNodeImpl(nc);
		csn = (CrawlServerNode)sn.getASN();
		sn.start(cconf, false);
	}

	@Test
	public void testStartTask(){
		setUp();
		
		
	}
	

}
