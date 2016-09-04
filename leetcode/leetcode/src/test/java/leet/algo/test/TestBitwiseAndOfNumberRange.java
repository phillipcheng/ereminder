package leet.algo.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.BitwiseAndOfNumberRange;

public class TestBitwiseAndOfNumberRange {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		BitwiseAndOfNumberRange b = new BitwiseAndOfNumberRange();
		int n = b.rangeBitwiseAnd(5, 7);
		logger.info(n);
	}
	
	@Test
	public void test2(){
		BitwiseAndOfNumberRange b = new BitwiseAndOfNumberRange();
		int n = b.rangeBitwiseAnd(0, 2147483647);
		logger.info(n);
	}

	@Test
	public void test3(){
		BitwiseAndOfNumberRange b = new BitwiseAndOfNumberRange();
		int n = b.rangeBitwiseAnd(1, 2);
		logger.info(n);
	}
	
	@Test
	public void test4(){
		BitwiseAndOfNumberRange b = new BitwiseAndOfNumberRange();
		int n = b.rangeBitwiseAnd(5, 6);
		logger.info(n);
	}
}
