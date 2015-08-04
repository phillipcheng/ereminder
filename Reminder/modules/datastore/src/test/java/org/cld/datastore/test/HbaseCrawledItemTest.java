package org.cld.datastore.test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.impl.HbaseDataStoreManagerImpl;
import org.cld.datastore.impl.HbaseDbAdminImpl;
import org.junit.Test;

public class HbaseCrawledItemTest {
	private static Logger logger = LogManager.getLogger(HbaseCrawledItemTest.class);
	
	@Test
	public void testCreateTable() {
		HbaseDbAdminImpl hbaseAdmin = new HbaseDbAdminImpl();
		hbaseAdmin.createTable(HbaseDataStoreManagerImpl.CRAWLEDITEM_TABLE_NAME, new String[]{HbaseDataStoreManagerImpl.CRAWLEDITEM_CF});
	}
	
	@Test
	public void testAddGetCrawledItem(){//add and get and assert equal
		HbaseDataStoreManagerImpl ds = new HbaseDataStoreManagerImpl(new Configuration());
		CrawledItem ci = new CrawledItem();
		String id = "abc";
		String storeid="store1";
		ci.setFullUrl("http://fullurl1");
		ci.addParam("book", "book1content");
		CrawledItemId ciid = new CrawledItemId(id, storeid, new Date());
		ci.setId(ciid);
		ds.addUpdateCrawledItem(ci, null);
		
		CrawledItem ci2 = ds.getCrawledItem(id, storeid, null);
		logger.info("ci2 get from db:" + ci2);
		
		assertTrue(ci2.contentEquals(ci));
	}
	
	@Test
	public void testAdd2Version(){//add 2 version and get
		HbaseDataStoreManagerImpl ds = new HbaseDataStoreManagerImpl(new Configuration());
		CrawledItem ci = new CrawledItem();
		String id = "abc";
		String storeid="store1";
		ci.setFullUrl("http://fullurl1");
		ci.addParam("book", "book3content");
		CrawledItemId ciid = new CrawledItemId(id, storeid, new Date());
		ci.setId(ciid);
		ds.addUpdateCrawledItem(ci, null);
		
		List<CrawledItem> cil = ds.getCrawledItem(id, storeid, 2, null);
		logger.info("ci list get from db:" + cil);
	}

}
