package leet.algo.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import leet.algo.SlidingWindowMaximum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSlidingWindowMax {
	private static Logger logger =  LogManager.getLogger(TestSlidingWindowMax.class);
	@Test
	public void test1(){
		SlidingWindowMaximum sw = new SlidingWindowMaximum();
		int[] ret = sw.maxSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3);
		assertTrue(Arrays.equals(ret, new int[]{3,3,5,5,6,7}));
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test2(){
		SlidingWindowMaximum sw = new SlidingWindowMaximum();
		int[] ret = sw.maxSlidingWindow(new int[]{}, 0);
		assertTrue(Arrays.equals(ret, new int[]{}));
		logger.info(Arrays.toString(ret));
	}

}
