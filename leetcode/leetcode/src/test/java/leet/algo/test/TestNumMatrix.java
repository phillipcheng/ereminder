package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.NumMatrix;

public class TestNumMatrix {
	
	@Test
	public void test1(){
		NumMatrix nm = new NumMatrix(new int[][]{{3, 0, 1, 4, 2},{5, 6, 3, 2, 1},{1, 2, 0, 1, 5},
				{4, 1, 0, 1, 7},{1, 0, 3, 0, 5}});
		assertTrue(8==nm.sumRegion(2, 1, 4, 3));
		assertTrue(11==nm.sumRegion(1, 1, 2, 2));
		assertTrue(12==nm.sumRegion(1, 2, 2, 4));
	}

}
