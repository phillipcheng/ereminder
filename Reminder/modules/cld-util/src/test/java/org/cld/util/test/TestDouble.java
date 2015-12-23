package org.cld.util.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestDouble {
	
	public static final Logger logger = LogManager.getLogger(TestDouble.class);
	
	@Test
	public void testDouble(){
		double mid = 1.505;
		mid = (double) Math.round(mid * 100) / 100; //decimal 2 digits
		assertTrue(mid == 1.51);
		mid = 1.5;
		mid = (double) Math.round(mid * 100) / 100; //decimal 2 digits
		assertTrue(mid == 1.5);
		mid = 61.88/2;
		mid = mid + 0.01;
		mid = (double) Math.round(mid * 100) / 100; //decimal 2 digits
		logger.info(mid);
		
	}
	
	@Test
	public void test1(){
		System.out.println("hello world");
		for (int i=0; i<100; i++){
			System.out.println(i);
		}
	}

}
