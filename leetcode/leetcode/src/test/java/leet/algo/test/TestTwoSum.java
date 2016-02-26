package leet.algo.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import leet.algo.TwoSum;

public class TestTwoSum {

	@Test
	public void test() {
		int[] numbers= new int[]{0,2,4,0};
        int target = 0;
        int[] ret = TwoSum.twoSum(numbers,target);
        assertTrue(ret[0]==1);
        assertTrue(ret[1]==4);

	}

}
