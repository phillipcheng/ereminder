package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.ValidParentheses;

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
