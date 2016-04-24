package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.BasicCalculatorII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestBasicCalculatorII {
	private static Logger logger =  LogManager.getLogger(TestBasicCalculatorII.class);
	@Test
	public void test1(){
		BasicCalculatorII bc = new BasicCalculatorII();
		int val = bc.calculate("3+2*2");
		logger.info(val);
		assertTrue(val == 7);
	}
	
	@Test
	public void test2(){
		BasicCalculatorII bc = new BasicCalculatorII();
		int val = bc.calculate(" 3/2 ");
		logger.info(val);
		assertTrue(val == 1);
	}
	
	@Test
	public void test3(){
		BasicCalculatorII bc = new BasicCalculatorII();
		int val = bc.calculate(" 3+5 / 2 ");
		logger.info(val);
		assertTrue(val == 5);
	}
	
	

}
