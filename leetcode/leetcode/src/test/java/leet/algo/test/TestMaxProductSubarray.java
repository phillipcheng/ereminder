package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.MaxProductSubarray;

public class TestMaxProductSubarray {
	
	@Test
	public void test1(){
		MaxProductSubarray mps = new MaxProductSubarray();
		assertTrue(6==mps.maxProduct(new int[]{2,3,-2,4}));
	}
	
	@Test
	public void test2(){
		MaxProductSubarray mps = new MaxProductSubarray();
		assertTrue(12==mps.maxProduct(new int[]{-4,-3,-2}));
	}

}
