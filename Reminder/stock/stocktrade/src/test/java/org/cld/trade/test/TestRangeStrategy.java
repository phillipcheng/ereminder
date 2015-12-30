package org.cld.trade.test;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.trade.AutoTrader;
import org.junit.Test;

public class TestRangeStrategy {
	private static Logger logger =  LogManager.getLogger(TestRangeStrategy.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static{
		sdf.setTimeZone(TimeZone.getTimeZone("EST"));
	}
	
	@Test
	public void test1() throws Exception {
		AutoTrader at = new AutoTrader();
		at.applySplitDiv(sdf.parse("2015-12-31"));//check A for dividend 0.115
		at.applySplitDiv(sdf.parse("2015-12-16"));//check CRMZ 13:10
	}
}
