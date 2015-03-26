package org.cld.sites.test;

import static org.junit.Assert.*;

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
	public void testAddGetCrawledItem(){
		HbaseDataStoreManagerImpl ds = new HbaseDataStoreManagerImpl();
		CrawledItem ci = new CrawledItem();
		String id = "abc";
		String storeid="store1";
		ci.setFullUrl("http://fullurl1");
		ci.addParam("book", "book1content");
		CrawledItemId ciid = new CrawledItemId();
		ciid.setId(id);
		ciid.setStoreId(storeid);
		ci.setId(ciid);
		ds.addCrawledItem(ci, null);
		
		CrawledItem ci2 = ds.getCrawledItem(id, storeid, null);
		logger.info("ci2 get from db:" + ci2);
		
		assertTrue(ci2.contentEquals(ci));
	}
	
	@Test
	public void testGetCrawledItem(){
		HbaseDataStoreManagerImpl ds = new HbaseDataStoreManagerImpl();
		String id = "abc";
		String storeid="store1";
		CrawledItem ci2 = ds.getCrawledItem(id, storeid, null);
		logger.info("ci2 get from db:" + ci2);
	}

}
