package org.cld.sites.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.TestBase;
import org.junit.Before;
import org.junit.Test;

public class TestMiscSites extends TestBase {
	private static Logger logger =  LogManager.getLogger(TestMiscSites.class);
	
	private String propFile = "client1-v2.properties";
	
	@Before
	public void setUp(){
		super.setProp(propFile);
	}
	
	
	public static final String YAHOO_FINANCE="yahoo.finance.xml";
	public static final String USCIS_CONF = "uscis.xml";
	public static final String INVOKE_YAHOO_FINANCE_CONF1="invoke.yahoo.finance.xml";
	
	@Test
	public void testOnePrd_USCIS() throws Exception{
		browsePrd(USCIS_CONF, null);
	}
	
	@Test
	public void invokeTask_YahooFinance() throws Exception{
		cconf.setUpSite(YAHOO_FINANCE, null);
		CrawlTestUtil.invokeTask(INVOKE_YAHOO_FINANCE_CONF1, cconf);
	}
	
	//invoke task via API
	@Test
	public void run_YahooFinance() throws InterruptedException{
		cconf.setUpSite(YAHOO_FINANCE, null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbal", "HPQ");
		browsePrd(YAHOO_FINANCE, null, params);
	}
}
