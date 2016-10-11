package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.NumberOfIslands;

import org.junit.Test;

public class TestNumberOfIslands {
	
	@Test
	public void test1(){
		NumberOfIslands noi = new NumberOfIslands();
		assertTrue(1==noi.numIslands(new char[][]{
				{'1','1','1','1','0'},
				{'1','1','0','1','0'},
				{'1','1','0','0','0'},
				{'0','0','0','0','0'}}));
	}
	
	@Test
	public void test2(){
		NumberOfIslands noi = new NumberOfIslands();
		assertTrue(3==noi.numIslands(new char[][]{
				{'1','1','0','0','0'},
				{'1','1','0','0','0'},
				{'0','0','1','0','0'},
				{'0','0','0','1','1'}}));
	}
	
	@Test
	public void test3(){
		NumberOfIslands noi = new NumberOfIslands();
		assertTrue(2==noi.numIslands(new char[][]{
				{'1','1','0','0','0'},
				{'0','1','1','0','0'},
				{'0','0','1','0','0'},
				{'0','0','0','1','1'}}));
	}

}
