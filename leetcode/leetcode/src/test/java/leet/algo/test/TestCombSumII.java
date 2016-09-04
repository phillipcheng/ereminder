package leet.algo.test;

import java.util.List;

import leet.algo.CombinationSumII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestCombSumII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	
	@Test
	public void test1(){
		CombinationSumII cs = new CombinationSumII();
		List<List<Integer>> result = cs.combinationSum2(new int[]{10,1,2,7,6,1,5}, 8);
		logger.info(result);
	}

}
