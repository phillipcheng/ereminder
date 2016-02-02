package org.cld.webconf.test;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.webconf.WebConfPersistMgr;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

public class TestDataStoreRSClient {

	public Logger logger = LogManager.getLogger(TestDataStoreRSClient.class);
    
	public String MAIN_REQUEST_URL = "http://localhost:8080/cldwebconf/services/crawlconf";
	
	@Test
	public void testHibernate() throws Exception {
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		WebConfPersistMgr pm = new WebConfPersistMgr(factory);
		pm.saveXmlConf("1", "cy", "<xml>");
		
	}

}
