package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.BasicCalculator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestBasicCalculator {
	private static Logger logger =  LogManager.getLogger(TestBasicCalculator.class);
	@Test
	public void test1(){
		BasicCalculator bc = new BasicCalculator();
		int ret=bc.calculate("1 + 1");
		logger.info(ret);
		assertTrue(ret==2);
	}
	
	@Test
	public void test2(){
		BasicCalculator bc = new BasicCalculator();
		int ret=bc.calculate(" 2-1 + 2 ");
		logger.info(ret);
		assertTrue(ret==3);
	}
	
	@Test
	public void test3(){
		BasicCalculator bc = new BasicCalculator();
		int ret=bc.calculate("(1+(4+5+2)-3)+(6+8)");
		logger.info(ret);
		assertTrue(ret==23);
	}

}
