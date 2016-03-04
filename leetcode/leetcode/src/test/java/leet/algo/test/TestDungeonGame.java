package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.DungeonGame;

public class TestDungeonGame {
	
	@Test
	public void test1(){
		DungeonGame dg = new DungeonGame();
		int[][] map = new int[][]{{-2,-3,3},{-5,-10,1},{10,30,-5}};
		int v = dg.calculateMinimumHP(map);
		assertTrue(7==v);
	}
	
	@Test
	public void test2(){
		DungeonGame dg = new DungeonGame();
		int[][] map = new int[][]{{100}};
		int v = dg.calculateMinimumHP(map);
		assertTrue(1==v);
	}
	
	@Test
	public void test3(){
		DungeonGame dg = new DungeonGame();
		int[][] map = new int[][]{{-3,5}};
		int v = dg.calculateMinimumHP(map);
		assertTrue(4==v);
	}

}
