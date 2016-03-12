package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.BurstBalloons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestBurstBalloons {
	private static Logger logger =  LogManager.getLogger(TestBurstBalloons.class);
	
	@Test
	public void test1(){
		BurstBalloons bb = new BurstBalloons();
		int ret = bb.maxCoins(new int[]{3, 1, 5, 8});
		logger.info(ret);
		assertTrue(ret==167);
	}
	
	@Test
	public void test2(){
		BurstBalloons bb = new BurstBalloons();
		int ret = bb.maxCoins(new int[]{3, 1, 5});
		logger.info(ret);
		assertTrue(ret==35);
	}
	
	@Test
	public void test3(){
		BurstBalloons bb = new BurstBalloons();
		int ret = bb.maxCoins(new int[]{3, 5});
		logger.info(ret);
		assertTrue(ret==20);
	}
	
	@Test
	public void test4(){
		BurstBalloons bb = new BurstBalloons();
		int ret = bb.maxCoins(new int[]{3});
		logger.info(ret);
		assertTrue(ret==3);
	}

}
