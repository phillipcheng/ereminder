package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.DecodeWays;

public class TestDecodeWays {

	@Test
	public void test0() {
		DecodeWays dw = new DecodeWays();
		assertTrue(2==dw.numDecodings("12"));
	}
	
	@Test
	public void test1() {
		DecodeWays dw = new DecodeWays();
		assertTrue(3==dw.numDecodings("123"));
	}
	
	
	@Test
	public void test2() {
		DecodeWays dw = new DecodeWays();
		long start = System.nanoTime();
		dw.numDecodings("8435098221275814436356964965276761914618252012127634945951156398377952112951644342731746644843122125");
		long end = System.nanoTime();
		System.out.println("elapse:" + (end-start));
	}
	
	@Test
	public void test3() {
		DecodeWays dw = new DecodeWays();
		assertTrue(0==dw.numDecodings(""));
	}
	
	@Test
	public void test4() {
		DecodeWays dw = new DecodeWays();
		assertTrue(0==dw.numDecodings("0"));
	}
	
	@Test
	public void test5() {
		DecodeWays dw = new DecodeWays();
		assertTrue(1==dw.numDecodings("210"));
	}
	
	@Test
	public void test6() {
		DecodeWays dw = new DecodeWays();
		assertTrue(0==dw.numDecodings("01"));
	}
	
	@Test
	public void test7() {
		DecodeWays dw = new DecodeWays();
		assertTrue(0==dw.numDecodings("12212001"));
	}
	

}
