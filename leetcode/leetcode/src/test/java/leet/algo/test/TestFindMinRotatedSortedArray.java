package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.FindMinRotatedSortedArray;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestFindMinRotatedSortedArray {
	private static Logger logger =  LogManager.getLogger(TestFindMinRotatedSortedArray.class);
	@Test
	public void test1(){
		FindMinRotatedSortedArray fmrsa = new FindMinRotatedSortedArray();
		int min = fmrsa.findMin(new int[]{4,5,6,7,0,1,2});
		logger.info(min);
		assertTrue(min==0);
	}

}
