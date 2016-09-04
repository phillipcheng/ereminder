package leet.algo.test;

import leet.algo.UglyNumberII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestUglyNumberII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		UglyNumberII un = new UglyNumberII();
		for (int i=1; i<20; i++){
			logger.info(un.nthUglyNumber(i));
		}
	}
	
	@Test
	public void test2(){
		UglyNumberII un = new UglyNumberII();
		logger.info(un.nthUglyNumber(1407));
	}

}
