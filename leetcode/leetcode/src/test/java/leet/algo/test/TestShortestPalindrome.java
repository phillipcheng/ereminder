package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.ShortestPalindrome;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.StringUtil;


public class TestShortestPalindrome {
	private static Logger logger =  LogManager.getLogger(TestShortestPalindrome.class);
	@Test
	public void test1(){
		ShortestPalindrome sp = new ShortestPalindrome();
		String str = sp.shortestPalindrome("aacecaaa");
		logger.info(str);
		assertTrue(str.equals("aaacecaaa"));
	}
	
	@Test
	public void test2(){
		ShortestPalindrome sp = new ShortestPalindrome();
		String str = sp.shortestPalindrome("abcd");
		logger.info(str);
		assertTrue(str.equals("dcbabcd"));
	}
	
	@Test
	public void test3(){
		ShortestPalindrome sp = new ShortestPalindrome();
		String input = StringUtil.readString("shortestpalindrome.txt");
		String str = sp.shortestPalindrome(input);
		logger.info(str);
	}
	
	@Test
	public void test4(){
		ShortestPalindrome sp = new ShortestPalindrome();
		String str = sp.shortestPalindrome("abbacd");
		logger.info(str);
		assertTrue(str.equals("dcabbacd"));
	}
}
