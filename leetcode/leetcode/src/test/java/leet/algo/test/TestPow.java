package leet.algo.test;

import leet.algo.Pow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestPow {
	private static Logger logger =  LogManager.getLogger(TestPow.class);
	
	@Test
	public void test1(){
		Pow p = new Pow();
		double myV = p.myPow(1.5d, 7);
		double v = Math.pow(1.5, 7);
		logger.info(String.format("myV:%s, v:%s", myV, v));
	}
	
	@Test
	public void test2(){
		Pow p = new Pow();
		double myV = p.myPow(1.5d, -7);
		double v = Math.pow(1.5, -7);
		logger.info(String.format("myV:%s, v:%s", myV, v));
	}
	
	@Test
	public void test3(){
		Pow p = new Pow();
		double myV = p.myPow(1.5d, 0);
		double v = Math.pow(1.5, 0);
		logger.info(String.format("myV:%s, v:%s", myV, v));
	}
	
	@Test
	public void test4(){
		Pow p = new Pow();
		double myV = p.myPow(3.89707d, 2);
		double v = Math.pow(3.89707d, 2);
		logger.info(String.format("myV:%s, v:%s", myV, v));
	}@Test
	
	public void test5(){
		Pow p = new Pow();
		double myV = p.myPow(2d, -2147483648);
		double v = Math.pow(2d, -2147483648);
		logger.info(String.format("myV:%s, v:%s", myV, v));
	}

}
