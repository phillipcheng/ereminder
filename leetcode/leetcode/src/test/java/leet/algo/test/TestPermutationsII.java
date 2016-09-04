package leet.algo.test;

import java.util.List;

import leet.algo.PermutationsII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestPermutationsII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		PermutationsII p = new PermutationsII();
		List<List<Integer>> ll = p.permuteUnique(new int[]{1,1,2});
		logger.info(ll);
	}

}
