package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.TrappingRainWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestTrappingRainWater {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		
		TrappingRainWater trw = new TrappingRainWater();
		int hold = trw.trap(new int[]{0,1,0,2,1,0,1,3,2,1,2,1});
		logger.info(hold);
		assertTrue(hold==6);
	}

}
