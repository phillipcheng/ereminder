package leet.algo.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.HouseRobberII;

public class TestHouseRobberII {
	private static Logger logger =  LogManager.getLogger(TestHouseRobberII.class);
	@Test
	public void test1(){
		HouseRobberII hr = new HouseRobberII();
		int m = hr.rob(new int[]{1,3,1});
		logger.info(m);
		assertTrue(m==3);
	}
	
	@Test
	public void test2(){
		HouseRobberII hr = new HouseRobberII();
		int m = hr.rob(new int[]{2,1,1,1});
		logger.info(m);
		assertTrue(m==3);
	}
	
	@Test
	public void test3(){
		HouseRobberII hr = new HouseRobberII();
		int m = hr.rob(new int[]{1,1,1,2});
		logger.info(m);
		assertTrue(m==3);
	}
	
	@Test
	public void test4(){
		HouseRobberII hr = new HouseRobberII();
		int m = hr.rob(new int[]{2,7,9,3,1});
		logger.info(m);
		assertTrue(m==11);
	}
	
	@Test
	public void test5(){
		HouseRobberII hr = new HouseRobberII();
		int m = hr.rob(new int[]{6,6,4,8,4,3,3,10});
		logger.info(m);
		assertTrue(m==27);
	}
	
	@Test
	public void test6(){
		HouseRobberII hr = new HouseRobberII();
		int m = hr.rob(new int[]{1,1,3,6,7,10,7,1,8,5,9,1,4,4,3});
		logger.info(m);
		assertTrue(m==41);
	}

}
