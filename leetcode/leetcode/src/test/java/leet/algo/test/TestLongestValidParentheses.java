package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.LongestValidParentheses;

public class TestLongestValidParentheses {

	@Test
	public void test1() {
		LongestValidParentheses lvp = new LongestValidParentheses();
		assertTrue(8==lvp.longestValidParentheses("((()()))("));
	}
	
	@Test
	public void test2() {
		LongestValidParentheses lvp = new LongestValidParentheses();
		assertTrue(2==lvp.longestValidParentheses("(()"));
	}
	
	@Test
	public void test3() {
		LongestValidParentheses lvp = new LongestValidParentheses();
		assertTrue(4==lvp.longestValidParentheses(")()())"));
	}
	
	@Test
	public void test4() {
		LongestValidParentheses lvp = new LongestValidParentheses();
		assertTrue(4==lvp.longestValidParentheses("()()"));
	}

}
