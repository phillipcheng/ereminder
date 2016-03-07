package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.util.StringUtil;
import leet.algo.LargestRectangleInHistogram;

public class TestLargestRectangleInHistogram {
	
	@Test
	public void test1(){
		LargestRectangleInHistogram hist = new LargestRectangleInHistogram();
		int max = hist.largestRectangleArea(new int[]{2,1,5,6,2,3});
		assertTrue(max==10);
	}
	
	@Test
	public void test2(){
		LargestRectangleInHistogram hist = new LargestRectangleInHistogram();
		int max = hist.largestRectangleArea(new int[]{0,0,0,0,0,0,0,0,2147483647});
		assertTrue(max==2147483647);
	}
	
	@Test
	public void test3(){
		LargestRectangleInHistogram hist = new LargestRectangleInHistogram();
		int[] input = StringUtil.readInts("LargestRectangleInHistogram.txt");
		int max = hist.largestRectangleArea(input);
		assertTrue(max==100000000);
	}

}
