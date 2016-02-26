package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.VerifyPreorder;

public class TestVerifyPreorder {
	
	@Test
	public void test1(){
		VerifyPreorder vp = new VerifyPreorder();
		assertTrue(vp.isValidSerialization("9,3,4,#,#,1,#,#,2,#,6,#,#"));
	}
	
	@Test
	public void test2(){
		VerifyPreorder vp = new VerifyPreorder();
		assertFalse(vp.isValidSerialization("1,#"));
	}
	
	@Test
	public void test3(){
		VerifyPreorder vp = new VerifyPreorder();
		assertFalse(vp.isValidSerialization("9,#,#,1"));
	}
	
	@Test
	public void test4(){
		VerifyPreorder vp = new VerifyPreorder();
		assertFalse(vp.isValidSerialization("1"));
	}
	
	@Test
	public void test5(){
		VerifyPreorder vp = new VerifyPreorder();
		assertFalse(vp.isValidSerialization("1,1,1"));
	}

}
