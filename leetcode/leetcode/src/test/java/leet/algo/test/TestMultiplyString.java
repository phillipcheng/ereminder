package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.MultiplyString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestMultiplyString {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		MultiplyString ms = new MultiplyString();
		String ret = ms.multiply("15", "327");
		logger.info(ret);
		assertTrue("4905".equals(ret));
	}
	
	@Test
	public void test2(){
		MultiplyString ms = new MultiplyString();
		String ret = ms.multiply("666", "0");
		logger.info(ret);
		assertTrue("0".equals(ret));
	}

}
