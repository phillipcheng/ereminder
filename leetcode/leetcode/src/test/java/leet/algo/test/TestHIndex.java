package leet.algo.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.HIndex;

public class TestHIndex {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		HIndex hi = new HIndex();
		int ret = hi.hIndex(new int[]{3,0,6,1,5});
		logger.info(ret);
	}
	
	@Test
	public void test2(){
		HIndex hi = new HIndex();
		int ret = hi.hIndex(new int[]{100});
		logger.info(ret);
	}
	
	@Test
	public void test3(){
		HIndex hi = new HIndex();
		int ret = hi.hIndex(new int[]{1,1});
		logger.info(ret);
	}

}
