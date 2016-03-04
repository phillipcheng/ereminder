package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.InterleavingString;

import org.junit.Test;

public class TestInterleavingString {
	
	@Test
	public void test0(){
		InterleavingString is = new InterleavingString();
		assertTrue(is.isInterleave("ab", "db", "adbb"));
	}
	
	@Test
	public void test1(){
		InterleavingString is = new InterleavingString();
		assertTrue(is.isInterleave("aabcc", "dbbca", "aadbbcbcac"));
	}
	
	@Test
	public void test2(){
		InterleavingString is = new InterleavingString();
		assertFalse(is.isInterleave("aabcc", "dbbca", "aadbbbaccc"));
	}

}
