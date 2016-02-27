package leet.algo.test;

import org.junit.Test;

import leet.algo.CountRangeSum;

public class TestCountRangeSum {
	
	@Test
	public void test1(){
		CountRangeSum crs = new CountRangeSum();
		int c = crs.countRangeSum(new int[]{-2, 5, -1}, -2, 2);
		System.out.println(c);
	}
	
	@Test
	public void test2(){
		CountRangeSum crs = new CountRangeSum();
		int c = crs.countRangeSum(new int[]{2147483647,-2147483648,-1,0}, -1, 0);
		System.out.println(c);
	}

}
