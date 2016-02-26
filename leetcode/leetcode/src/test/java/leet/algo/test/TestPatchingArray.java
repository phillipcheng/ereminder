package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.PatchingArray;

public class TestPatchingArray {
	
	@Test
	public void test1(){
		PatchingArray pa= new PatchingArray();
		assertTrue(1==pa.minPatches(new int[]{1,3}, 6));
	}
	
	@Test
	public void test2(){
		PatchingArray pa= new PatchingArray();
		assertTrue(2==pa.minPatches(new int[]{1,5,10}, 20));
	}
	
	@Test
	public void test3(){
		PatchingArray pa= new PatchingArray();
		assertTrue(0==pa.minPatches(new int[]{1, 2, 2}, 5));
	}
	
	@Test
	public void test4(){
		PatchingArray pa= new PatchingArray();
		assertTrue(3==pa.minPatches(new int[]{}, 7));
	}
	
	@Test
	public void test5(){
		PatchingArray pa= new PatchingArray();
		assertTrue(28==pa.minPatches(new int[]{1,2,31,33},2147483647));
		
	}

}
