package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.FindDuplicateNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.StringUtil;

public class TestFindDuplicateNumber {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		FindDuplicateNumber fdn = new FindDuplicateNumber();
		int dup = fdn.findDuplicate(new int[]{1,2,3,4,1});
		logger.info(dup);
		assertTrue(dup==1);
	}
	
	@Test
	public void test2(){
		FindDuplicateNumber fdn = new FindDuplicateNumber();
		int dup = fdn.findDuplicate(new int[]{1,2,3,4,2});
		logger.info(dup);
		assertTrue(dup==2);
	}
	
	@Test
	public void test3(){
		FindDuplicateNumber fdn = new FindDuplicateNumber();
		int dup = fdn.findDuplicate(new int[]{1,2,3,2,2});
		logger.info(dup);
		assertTrue(dup==2);
	}
	
	@Test
	public void test4(){
		FindDuplicateNumber fdn = new FindDuplicateNumber();
		int dup = fdn.findDuplicate(StringUtil.readInts("leet/algo/test/DupNumbers.txt"));
		logger.info(dup);
	}
	
	

}
