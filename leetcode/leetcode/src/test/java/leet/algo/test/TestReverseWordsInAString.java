package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.ReverseWordsInAString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestReverseWordsInAString {
	private static Logger logger =  LogManager.getLogger(TestReverseWordsInAString.class);
	@Test
	public void test1(){
		ReverseWordsInAString rw = new ReverseWordsInAString();
		String ret = rw.reverseWords(" the sky is blue ");
		logger.info(String.format("|%s|", ret));
		assertTrue("blue is sky the".equals(ret));
	}
	
	@Test
	public void test2(){
		ReverseWordsInAString rw = new ReverseWordsInAString();
		String ret = rw.reverseWords("the sky is blue ");
		logger.info(String.format("|%s|", ret));
		assertTrue("blue is sky the".equals(ret));
	}
	
	@Test
	public void test3(){
		ReverseWordsInAString rw = new ReverseWordsInAString();
		String ret = rw.reverseWords("   a   ");
		logger.info(String.format("|%s|", ret));
		assertTrue("a".equals(ret));
	}
	
	
	@Test
	public void test4(){
		ReverseWordsInAString rw = new ReverseWordsInAString();
		String ret = rw.reverseWords(" 1");
		logger.info(String.format("|%s|", ret));
	}
	
	

}
