package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.ScrambleStringss;

import org.junit.Test;

public class TestScrambleStringss {
	
	@Test
	public void test1(){
		ScrambleStringss ss = new ScrambleStringss();
		assertFalse(ss.isScramble("abcd", "bdac"));
	}
	
	@Test
	public void test2(){
		ScrambleStringss ss = new ScrambleStringss();
		assertTrue(ss.isScramble("abcd", "bdca"));
	}
	
	@Test
	public void test3(){
		ScrambleStringss ss = new ScrambleStringss();
		assertFalse(ss.isScramble("abcdefjhijkl", "efjhijklbdac"));
	}

}
