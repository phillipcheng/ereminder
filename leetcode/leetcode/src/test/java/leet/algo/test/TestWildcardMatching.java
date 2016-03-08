package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.WildcardMatching;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestWildcardMatching {
	private static Logger logger =  LogManager.getLogger(TestWildcardMatching.class);
	
	@Test
	public void test0(){
		WildcardMatching wm = new WildcardMatching();
		assertTrue(wm.isMatch("", ""));
	}
	
	@Test
	public void test1(){
		WildcardMatching wm = new WildcardMatching();
		String p = wm.mergeP("?*?**");
		logger.info(p);
		assertTrue("?*?*".equals(p));
	}
	
	@Test
	public void test2(){
		WildcardMatching wm = new WildcardMatching();
		assertFalse(wm.isMatch("aa", "a"));
	}
	
	@Test
	public void test3(){
		WildcardMatching wm = new WildcardMatching();
		assertTrue(wm.isMatch("aa", "aa"));
	}
	
	@Test
	public void test4(){
		WildcardMatching wm = new WildcardMatching();
		assertFalse(wm.isMatch("aaa", "aa"));
	}
	
	@Test
	public void test5(){
		WildcardMatching wm = new WildcardMatching();
		assertTrue(wm.isMatch("aa", "*"));
	}
	
	@Test
	public void test6(){
		WildcardMatching wm = new WildcardMatching();
		assertTrue(wm.isMatch("aa", "a*"));
	}
	
	@Test
	public void test7(){
		WildcardMatching wm = new WildcardMatching();
		assertTrue(wm.isMatch("ab", "?*"));
	}
	
	@Test
	public void test8(){
		WildcardMatching wm = new WildcardMatching();
		assertFalse(wm.isMatch("aab", "c*a*b"));
	}

}
