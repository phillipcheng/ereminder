package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.LongestCommonPrefix;

public class TestLongestCommonPrefix {

	@Test
	public void test1() {
		LongestCommonPrefix lcp = new LongestCommonPrefix();
		assertTrue("".equals(lcp.longestCommonPrefix(new String[]{"a","b"})));
		
	}
	
	@Test
	public void test2() {
		LongestCommonPrefix lcp = new LongestCommonPrefix();
		assertTrue("c".equals(lcp.longestCommonPrefix(new String[]{"c","c"})));
		
	}
	
	@Test
	public void test3() {
		LongestCommonPrefix lcp = new LongestCommonPrefix();
		assertTrue("a".equals(lcp.longestCommonPrefix(new String[]{"aa","ab"})));
		
	}

}
