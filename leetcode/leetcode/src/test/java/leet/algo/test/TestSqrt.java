package leet.algo.test;

import leet.algo.Sqrt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSqrt {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		Sqrt sqrt = new Sqrt();
		int a = sqrt.mySqrt(2147395599);
		logger.info(a);
	}
	
	@Test
	public void test2(){
		Sqrt sqrt = new Sqrt();
		int a = sqrt.mySqrt(345);
		logger.info(a);
	}

}
