package leet.algo.test;

import java.util.Arrays;

import leet.algo.SearchForRange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSearchForRange {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		SearchForRange sfr = new SearchForRange();
		int[] range = sfr.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 7);
		logger.info(Arrays.toString(range));
	}
	
	@Test
	public void test2(){
		SearchForRange sfr = new SearchForRange();
		int[] range = sfr.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 4);
		logger.info(Arrays.toString(range));
	}

}
