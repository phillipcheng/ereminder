package princeton.algo1.interview.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import princeton.algo1.interview.CountingInversion;

public class TestCountInversion {
	private static Logger logger =  LogManager.getLogger(TestCountInversion.class);
	
	@Test
	public void test1(){
		CountingInversion ci = new CountingInversion();
		int[] input = new int[]{5,7,4,2,1};
		int count = ci.countInversion(input);
		assertTrue(count==9);
		logger.info(Arrays.toString(input));
	}
	
	@Test
	public void test2(){
		CountingInversion ci = new CountingInversion();
		int[] input = new int[]{5,7};
		int count = ci.countInversion(input);
		logger.info(Arrays.toString(input));
	}
	
	@Test
	public void test3(){
		CountingInversion ci = new CountingInversion();
		int[] input = new int[]{2,1};
		int count = ci.countInversion(input);
		logger.info(Arrays.toString(input));
	}

}
