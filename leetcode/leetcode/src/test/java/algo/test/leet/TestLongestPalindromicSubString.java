package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.LongestPalindromicSubString;

public class TestLongestPalindromicSubString {
	@Test
	public void test0() {
		LongestPalindromicSubString lpss = new LongestPalindromicSubString();
		assertTrue("a".equals(lpss.longestPalindrome("a")));
	}
	
	@Test
	public void test1() {
		LongestPalindromicSubString lpss = new LongestPalindromicSubString();
		assertTrue("bb".equals(lpss.longestPalindrome("abb")));
	}
	
	@Test
	public void test2() {
		LongestPalindromicSubString lpss = new LongestPalindromicSubString();
		assertTrue("xxxxxxxxxx".equals(lpss.longestPalindrome("xxxxxxxxxxabbac")));
	}
	
}
