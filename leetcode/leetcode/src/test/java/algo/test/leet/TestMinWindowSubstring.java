package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.MinWindowSubstring;
import algo.util.StringUtil;

public class TestMinWindowSubstring {

	@Test
	public void test1() {
		MinWindowSubstring mwss = new MinWindowSubstring();
		assertTrue("BANC".equals(mwss.minWindow("ADOBECODEBANC", "ABC")));
	}
	
	@Test
	public void test2() {
		MinWindowSubstring mwss = new MinWindowSubstring();
		String s= StringUtil.genRandomString(100000);
		String t= StringUtil.genRandomString(10000);
		long start = System.nanoTime();
		String match = mwss.minWindow(s, t);
		long end = System.nanoTime();
		System.out.println("time elapse:" + (end-start) + ":" + match.length());
	}
	

}
