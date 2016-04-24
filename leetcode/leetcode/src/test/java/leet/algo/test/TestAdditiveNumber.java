package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.AdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestAdditiveNumber {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		AdditiveNumber an = new AdditiveNumber();
		assertTrue(an.isAdditiveNumber("112358"));
	}
	
	@Test
	public void test2(){
		AdditiveNumber an = new AdditiveNumber();
		assertTrue(an.isAdditiveNumber("199100199"));
	}
	
	@Test
	public void test3(){
		AdditiveNumber an = new AdditiveNumber();
		assertFalse(an.isAdditiveNumber("19910019921"));
	}
	
	@Test
	public void test4(){
		AdditiveNumber an = new AdditiveNumber();
		assertFalse(an.isAdditiveNumber("111"));
	}
	
	@Test
	public void test5(){
		AdditiveNumber an = new AdditiveNumber();
		assertTrue(an.isAdditiveNumber("101"));
	}
	
	@Test
	public void test6(){
		AdditiveNumber an = new AdditiveNumber();
		assertTrue(an.isAdditiveNumber("121474836472147483648"));
	}

}
