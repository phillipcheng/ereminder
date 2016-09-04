package leet.algo.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.UniquePathsII;

public class TestUniquePathsII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		UniquePathsII up = new UniquePathsII();
		int np = up.uniquePathsWithObstacles(new int[][]{{0,0,0},{0,1,0},{0,0,0}});
		logger.info(np);
	}

}
