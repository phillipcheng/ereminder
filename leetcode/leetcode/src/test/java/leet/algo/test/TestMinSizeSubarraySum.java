package leet.algo.test;

import leet.algo.MinimumSizeSubarraySum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestMinSizeSubarraySum {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		MinimumSizeSubarraySum msss = new MinimumSizeSubarraySum();
		int a = msss.minSubArrayLen(4, new int[]{1,4,4});
		logger.info(a);
	}
	
	@Test
	public void test2(){
		MinimumSizeSubarraySum msss = new MinimumSizeSubarraySum();
		int a = msss.minSubArrayLen(3, new int[]{1,1});
		logger.info(a);
	}

}
