package leet.algo.test;

import java.util.Arrays;

import org.junit.Test;

import leet.algo.WiggleSort;

public class TestWiggleSort {
	
	@Test
	public void test1(){
		WiggleSort ws = new WiggleSort();
		int[] input = new int[]{4, 5, 5, 6};
		ws.wiggleSort(input);
		System.err.println(String.format("%s", Arrays.toString(input)));
	}
}
