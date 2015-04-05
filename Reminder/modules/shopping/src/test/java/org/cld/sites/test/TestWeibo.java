package org.cld.sites.test;

import org.junit.Test;

public class TestWeibo extends TestBase{
	public static final String SITE_CONF_FILE ="weibo.xml";
	@Test
	public void checkUnlockedAccounts(){
		int i = getUnlockedAccounts(SITE_CONF_FILE);
		logger.info(String.format("%d unlocked accounts for %s", i, SITE_CONF_FILE));
	}
	
}
