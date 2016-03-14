package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.FractionToRecurringDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestFractionToRecurringDecimal {
	private static Logger logger =  LogManager.getLogger(TestFractionToRecurringDecimal.class);
	@Test
	public void test1(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(1, 2);
		logger.info(ret);
		assertTrue("0.5".equals(ret));
	}
	
	@Test
	public void test2(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(2, 1);
		logger.info(ret);
		assertTrue("2".equals(ret));
	}
	
	@Test
	public void test3(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(2, 3);
		logger.info(ret);
		assertTrue("0.(6)".equals(ret));
	}
	
	@Test
	public void test4(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(230, 99);
		logger.info(ret);
		assertTrue("2.(32)".equals(ret));
	}
	
	@Test
	public void test5(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(0, 99);
		logger.info(ret);
		assertTrue("0".equals(ret));
	}
	
	@Test
	public void test6(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(-50, 8);
		logger.info(ret);
		assertTrue("-6.25".equals(ret));
	}
	
	@Test
	public void test7(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(-1, -2147483648);
		logger.info(ret);
		assertTrue("0.0000000004656612873077392578125".equals(ret));
	}
	
	@Test
	public void test8(){
		FractionToRecurringDecimal fr = new FractionToRecurringDecimal();
		String ret = fr.fractionToDecimal(-2147483648,1);
		logger.info(ret);
		assertTrue("-2147483648".equals(ret));
	}

}
