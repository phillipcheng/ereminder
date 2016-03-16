package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.LargestNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestLargestNumber {
	private static Logger logger =  LogManager.getLogger(TestLargestNumber.class);
	@Test
	public void test1(){
		LargestNumber ln = new LargestNumber();
		String ret = ln.largestNumber(new int[]{3, 30, 34, 5, 9});
		logger.info(ret);
		assertTrue(ret.equals("9534330"));
	}
	
	@Test
	public void test2(){
		LargestNumber ln = new LargestNumber();
		String ret = ln.largestNumber(new int[]{12,121});
		logger.info(ret);
		assertTrue(ret.equals("12121"));
	}
	
	@Test
	public void test3(){
		LargestNumber ln = new LargestNumber();
		String ret = ln.largestNumber(new int[]{0,0,0,0});
		logger.info(ret);
		assertTrue(ret.equals("0"));
	}
	
	@Test
	public void test4(){
		LargestNumber ln = new LargestNumber();
		String ret = ln.largestNumber(new int[]{1,2,3,4,5,6,7,8,9,0});
		logger.info(ret);
		assertTrue(ret.equals("9876543210"));
	}
	
	@Test
	public void test5(){
		LargestNumber ln = new LargestNumber();
		String ret = ln.largestNumber(new int[]{1,0});
		logger.info(ret);
		assertTrue(ret.equals("10"));
	}

}
