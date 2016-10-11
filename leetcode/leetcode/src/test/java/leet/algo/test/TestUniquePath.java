package leet.algo.test;

import leet.algo.UniquePaths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestUniquePath {
	
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	
	@Test
	public void test1(){
		UniquePaths up = new UniquePaths();
		int tp = up.uniquePaths(2, 2);
		logger.info(tp);
	}

}
