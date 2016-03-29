package careercup.google.test;

import static org.junit.Assert.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import careercup.google.LongestBiggerSubstring;

public class TestLongestBiggerSubstring {
	private static Logger logger =  LogManager.getLogger(TestLongestBiggerSubstring.class);
	@Test
	public void test1(){
		LongestBiggerSubstring lbss = new LongestBiggerSubstring();
		String ret = lbss.getLongestBiggerSubstring("abc");
		assertTrue("bc".equals(ret));
	}
	
	@Test
	public void test2(){
		LongestBiggerSubstring lbss = new LongestBiggerSubstring();
		String ret = lbss.getLongestBiggerSubstring("dbbeabbc");
		logger.info(ret);
		assertTrue("eabbc".equals(ret));
	}
	
	@Test
	public void test3(){
		LongestBiggerSubstring lbss = new LongestBiggerSubstring();
		String ret = lbss.getLongestBiggerSubstring("dbbdbc");
		logger.info(ret);
		assertTrue("dbc".equals(ret));
	}
	@Test
	public void test4(){
		LongestBiggerSubstring lbss = new LongestBiggerSubstring();
		String ret = lbss.getLongestBiggerSubstring("dbbdbadbbdd");
		logger.info(ret);
		assertTrue("dbbdd".equals(ret));
	}
}
