package leet.algo.test;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.SubsetsII;

public class TestSubsetII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		SubsetsII ss = new SubsetsII();
		List<List<Integer>> ret = ss.subsetsWithDup(new int[]{1,2,2});
		logger.info(ret);
	}

}
