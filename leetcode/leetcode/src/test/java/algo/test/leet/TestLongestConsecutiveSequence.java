package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.LongestConsecutiveSequence;

public class TestLongestConsecutiveSequence {

	@Test
	public void test0() {
		LongestConsecutiveSequence lcs = new LongestConsecutiveSequence();
		assertTrue(1==lcs.longestConsecutive(new int[]{0}));
	}
	
	@Test
	public void test1() {
		LongestConsecutiveSequence lcs = new LongestConsecutiveSequence();
		assertTrue(2==lcs.longestConsecutive(new int[]{0, -1}));
	}
	
	@Test
	public void test2() {
		LongestConsecutiveSequence lcs = new LongestConsecutiveSequence();
		assertTrue(5==lcs.longestConsecutive(new int[]{1,3,5,2,4}));
	}
	
	@Test
	public void test3() {
		LongestConsecutiveSequence lcs = new LongestConsecutiveSequence();
		assertTrue(4==lcs.longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
	}
	

	@Test
	public void test4() {
		LongestConsecutiveSequence lcs = new LongestConsecutiveSequence();
		assertTrue(3==lcs.longestConsecutive(new int[]{2147483646,-2147483647,0,2,2147483644,-2147483645,2147483645}));
	}
}
