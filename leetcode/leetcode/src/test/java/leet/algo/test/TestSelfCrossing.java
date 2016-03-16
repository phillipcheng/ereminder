package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.SelfCrossing;

import org.junit.Test;

public class TestSelfCrossing {
	
	@Test
	public void test1(){
		SelfCrossing sc = new SelfCrossing();
		assertTrue(sc.isSelfCrossing(new int[]{2,1,1,2}));
	}
	
	@Test
	public void test2(){
		SelfCrossing sc = new SelfCrossing();
		assertFalse(sc.isSelfCrossing(new int[]{1,2,3,4}));
	}
	
	@Test
	public void test3(){
		SelfCrossing sc = new SelfCrossing();
		assertTrue(sc.isSelfCrossing(new int[]{1,1,1,1}));
	}
	
	@Test
	public void test4(){
		SelfCrossing sc = new SelfCrossing();
		assertTrue(sc.isSelfCrossing(new int[]{1,1,3,2, 1, 4}));
	}
	
	@Test
	public void test5(){
		SelfCrossing sc = new SelfCrossing();
		assertTrue(sc.isSelfCrossing(new int[]{1,1,2,1,1}));
	}
	
	@Test
	public void test6(){
		SelfCrossing sc = new SelfCrossing();
		assertFalse(sc.isSelfCrossing(new int[]{3,3,3,2,1,1}));
	}
	
	@Test
	public void test7(){
		SelfCrossing sc = new SelfCrossing();
		assertFalse(sc.isSelfCrossing(new int[]{1,1,2,2,3,3,4,4,10,4,4,3,3,2,2,1,1}));
	}

}
