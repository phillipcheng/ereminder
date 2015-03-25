package org.cld.webconf.test;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreRSClient;
import org.cld.datastore.entity.SiteConf;
import org.junit.Test;

public class TestDataStoreRSClient {

	public Logger logger = LogManager.getLogger(TestDataStoreRSClient.class);
    
	public String MAIN_REQUEST_URL = "http://localhost:8080/cldwebconf/services/crawlconf";
	
	@Test
	public void test1() throws Exception {
		DataStoreRSClient wcc = new DataStoreRSClient(MAIN_REQUEST_URL, 1000);
		List<SiteConf> scl = wcc.getDeployedSiteConf();
		logger.info(scl);
	}

}
