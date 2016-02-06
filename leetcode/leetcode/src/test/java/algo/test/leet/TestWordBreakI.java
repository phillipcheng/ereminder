package algo.test.leet;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import algo.leet.WordBreakI;

public class TestWordBreakI {

	@Test
	public void test() {
		WordBreakI wbi = new WordBreakI();
		HashSet<String> dict = new HashSet<String>();
		dict.add("leet");
		dict.add("code");
		assertTrue(wbi.wordBreak("leetcode", dict));
		assertFalse(wbi.wordBreak("leettcode", dict));
	}
	
	@Test
	public void test1() {
		WordBreakI wbi = new WordBreakI();
		HashSet<String> dict = new HashSet<String>();
		dict.add("leet");
		dict.add("code");
		assertTrue(wbi.wordBreak("codeleetcode", dict));
	}
	
	@Test
	public void test2() {
		WordBreakI wbi = new WordBreakI();
		HashSet<String> dict = new HashSet<String>();
		dict.add("leet");
		dict.add("code");
		assertFalse(wbi.wordBreak("", dict));
		assertFalse(wbi.wordBreak("l", dict));
	}
	
	@Test
	public void test3() {
		WordBreakI wbi = new WordBreakI();
		HashSet<String> dict = new HashSet<String>();
		dict.add("a");
		dict.add("aa");
		dict.add("aaa");
		dict.add("aaaa");
		dict.add("aaaaa");
		dict.add("aaaaaa");
		dict.add("aaaaaaa");
		dict.add("aaaaaaaa");
		dict.add("aaaaaaaaa");
		dict.add("aaaaaaaaaa");
		assertFalse(wbi.wordBreak("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab", dict));
	}

}
