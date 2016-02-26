package leet.algo.test;

import java.util.Arrays;

import org.junit.Test;

import leet.algo.WiggleSort;

public class TestWiggleSort {
	
	@Test
	public void test1(){
		WiggleSort ws = new WiggleSort();
		int[] input = new int[]{1, 5, 1, 1, 6, 4};
		ws.wiggleSort(input);
		System.err.println(String.format("%s", Arrays.toString(input)));
	}
	
	@Test
	public void test2(){
		WiggleSort ws = new WiggleSort();
		int[] input = new int[]{1,1,2,2,3};
		ws.wiggleSort(input);
		System.err.println(String.format("%s", Arrays.toString(input)));
	}
	
	@Test
	public void test3(){
		WiggleSort ws = new WiggleSort();
		int[] input = new int[]{1, 3, 2, 2, 3, 1};
		ws.wiggleSort(input);
		System.err.println(String.format("%s", Arrays.toString(input)));
	}
	
	@Test
	public void test4(){
		WiggleSort ws = new WiggleSort();
		int[] input = new int[]{4,5,5,6};
		ws.wiggleSort(input);
		System.err.println(String.format("%s", Arrays.toString(input)));
	}
	
	@Test
	public void test5(){
		WiggleSort ws = new WiggleSort();
		int[] input = new int[]{4,5,5,5,5,6,6,6};
		ws.wiggleSort(input);
		System.err.println(String.format("%s", Arrays.toString(input)));
	}
}
