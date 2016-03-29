package leet.algo.test;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.RestoreIPAddress;

public class TestRestoreIPAddress {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	
	@Test
	public void test1(){
		RestoreIPAddress rip = new RestoreIPAddress();
		List<String> rl = rip.restoreIpAddresses("25525511135");
		logger.info(rl);
	}

}
