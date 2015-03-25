package org.cld.shopping.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.entity.TSKey;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.DataCrawl;
import org.cld.datacrawl.entity.CrawlPersistMgr;
import org.cld.datastore.DBConf;
import org.cld.datastore.DBException;
import org.cld.datastore.DBFactory;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class TestPersistence {
	private static Logger logger = LogManager.getLogger(TestPersistence.class);
	
	NodeConf nc = new NodeConf("server.properties");
	CrawlConf cconf = new CrawlConf("server.properties", nc);
	
	protected void setUp(){
	}
	
//	@Test
//	public void testGetBook() {
//		setUp();
//		
//		try {			
//			Product b = CrawlPersistMgr.getProduct(sf, "22932249");
//			logger.info(b.toString());
//		} catch (DBException e) {
//			logger.error(e);
//		}
//	}

}
