package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.MaxRectangle;

import org.junit.Test;

public class TestMaxRectangle {
	
	@Test
	public void test00(){
		MaxRectangle mr = new MaxRectangle();
		int res = mr.maximalRectangle(new char[][]{{}});
		assertTrue(res==0);
	}
	
	@Test
	public void test01(){
		MaxRectangle mr = new MaxRectangle();
		int res = mr.maximalRectangle(new char[][]{});
		assertTrue(res==0);
	}
	
	@Test
	public void test1(){
		MaxRectangle mr = new MaxRectangle();
		int res = mr.maximalRectangle(new char[][]{{'1','0','0'},{'0','1','1'}, {'0','1','1'}});
		assertTrue(res==4);
	}
	
	@Test
	public void test2(){
		MaxRectangle mr = new MaxRectangle();
		int res = mr.maximalRectangle(new char[][]{{'1','1','0', '0'},{'0','1','1','1'}, {'0','0','1','1'}, {'0','0','1','1'}});
		assertTrue(res==6);
	}
}
