package leet.algo.test;


import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import leet.algo.RegExpMatch;

public class TestRegExpMatch {
	
	@Test
	public void test1() {
		RegExpMatch rem = new RegExpMatch();
		assertFalse(rem.isMatch("aa","a"));
		

		assertTrue(rem.isMatch("aa","aa"));
		

		assertFalse(rem.isMatch("aaa","aa"));
		
		
		assertTrue(rem.isMatch("aa","a*"));
		

		assertTrue(rem.isMatch("aa",".*"));
		
	}
	
	@Test
	public void test2() {
		assertTrue(new RegExpMatch().isMatch("ab",".*"));
	}

	@Test
	public void test3() {
		assertTrue(new RegExpMatch().isMatch("aab","c*a*b*"));
	}
	
	@Test
	public void test4() {
		assertTrue(new RegExpMatch().isMatch("a","."));
	}
	
	@Test
	public void test5() {
		assertTrue(new RegExpMatch().isMatch("abc",".*...*"));
		assertFalse(new RegExpMatch().isMatch("ab",".*....*"));
	}
	
	@Test
	public void test6() {
		assertFalse(new RegExpMatch().isMatch("a",".*.a"));
	}
	
	@Test
	public void test7() {
		assertTrue(new RegExpMatch().isMatch("xxxxx",".*.a*"));
	}
	
	@Test
	public void test8() {
		assertTrue(new RegExpMatch().isMatch("xabcyxxxabcd",".abcd*..*....*abcd"));
	}
	
	@Test
	public void test9() {
		assertTrue(new RegExpMatch().isMatch("aaa","a*a"));
	}
	
	@Test
	public void test10() {
		assertTrue(new RegExpMatch().isMatch("aaa",".*a"));
	}
	
	@Test
	public void test11() {
		assertTrue(new RegExpMatch().isMatch("ccc","c*"));
	}
	
	@Test
	public void test12() {
		assertFalse(new RegExpMatch().isMatch("bb","c*"));
	}
	
	@Test
	public void test13() {
		assertFalse(new RegExpMatch().isMatch("cab","c*b"));
	}
	
}
