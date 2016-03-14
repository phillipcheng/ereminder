package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.NumArray;

import org.junit.Test;

public class TestNumArray {
	
	@Test
	public void test1(){
		NumArray numArray = new NumArray(new int[]{1,2,3,4,5,6});
	}
	
	@Test
	public void test2(){
		NumArray numArray = new NumArray(new int[]{-28,-39,53,65,11,-56,-65,-39,-43,97});
		numArray.sumRange(5,6);
		numArray.update(9,27);
		numArray.sumRange(2,3);
		numArray.sumRange(6,7);
		numArray.update(1,-82);
		numArray.update(3,-72);
		numArray.sumRange(3,7);
		numArray.sumRange(1,8);
		numArray.update(5,13);
		numArray.update(4,-67);
	}

}
