package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.MaxSquare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestMaxArea {
	private static Logger logger =  LogManager.getLogger(TestMaxArea.class);
	@Test
	public void test1(){
		MaxSquare mx = new MaxSquare();
		char[][] input = new char[][]{{'1','1'},{'1','1'}};
		int ma = mx.maximalSquare(input);
		logger.info(ma);
		assertTrue(ma==4);
	}
	
	@Test
	public void test2(){
		MaxSquare mx = new MaxSquare();
		char[][] input = new char[][]{{}};
		int ma = mx.maximalSquare(input);
		logger.info(ma);
		assertTrue(ma==0);
	}
	
	@Test
	public void test3(){
		MaxSquare mx = new MaxSquare();
		char[][] input = new char[][]{};
		int ma = mx.maximalSquare(input);
		logger.info(ma);
		assertTrue(ma==0);
	}
	
	@Test
	public void test4(){
		MaxSquare mx = new MaxSquare();
		char[][] input = new char[][]{{'1','1','1','1'},{'0','1','0','0'},{'0','1','1','1'},{'1','1','1','0'},{'0','1','1','0'}};
		int ma = mx.maximalSquare(input);
		logger.info(ma);
		assertTrue(ma==4);
	}
	
	@Test
	public void test5(){
		MaxSquare mx = new MaxSquare();
		char[][] input = new char[][]{{'1','0','1','0','0'},{'1','0','1','1','1'},{'1','1','1','1','1'},{'1','0','0','1','0'}};
		int ma = mx.maximalSquare(input);
		logger.info(ma);
		assertTrue(ma==4);
	}

}
