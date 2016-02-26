package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.ValidParentheses;

public class TestValidParentheses {

	@Test
	public void test0() {
		ValidParentheses vp = new ValidParentheses();
		assertTrue(vp.isValid("()"));
		assertTrue(vp.isValid("()[]{}"));
		assertTrue(!vp.isValid("(]"));
		assertTrue(!vp.isValid("([)]"));
	}

}
