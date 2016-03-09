package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.ScrambleString;

public class TestScrambleString {
	
	@Test
	public void test1(){
		ScrambleString ss = new ScrambleString();
		assertFalse(ss.isScramble("abcd", "bdac"));
	}
	
	@Test
	public void test2(){
		ScrambleString ss = new ScrambleString();
		assertTrue(ss.isScramble("great", "rgtae"));
	}
	
	@Test
	public void test3(){
		ScrambleString ss = new ScrambleString();
		assertFalse(ss.isScramble("abcdefghij", "efghijcadb"));
	}

}
