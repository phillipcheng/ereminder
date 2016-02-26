package leet.algo.test;


import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import leet.algo.SubStringWithConcatenationAllWords;

public class TestSubStringWithConcatenationAllWords {
	@Test
	public void test0() {
		SubStringWithConcatenationAllWords ssw = new SubStringWithConcatenationAllWords();
		ArrayList<Integer> ali = ssw.findSubstring("barfoothefoobarman", new String[]{"foo", "bar"});
		System.out.println(ali);
		assertTrue(ali.size()==2);
		assertTrue(ali.contains(0));
		assertTrue(ali.contains(9));
		
		
	}
	
	@Test
	public void test1() {
		SubStringWithConcatenationAllWords ssw = new SubStringWithConcatenationAllWords();
		ArrayList<Integer> ali = ssw.findSubstring(
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab" +
				"ababababababababababababababababababababababababababababababababababababababababababababababab"+
				"ababababababababababababababababababababababababababababababababababababababababababababababab"+
				"ababababababababababababababababababababababababababababababababababababababababababababababab"+
				"ababababababababababababababababababababababababababababababababababababababababababababababab"+
				"ababababababababababababababababababababababababababababababababababababababababababababababab"+
				"ababababababababababababababababababababababababababababababababababababababababababababababab"+
				"ababababababababababababababababababababababababababababababababababababababababababababababab", 
				new String[]{"ab", "ba", "ab", "ba", "ab", "ba","ab", "ba", "ab", "ba", "ab", "ba"});
		System.out.println(ali);		
		
	}
	
	@Test
	public void test2() {
		SubStringWithConcatenationAllWords ssw = new SubStringWithConcatenationAllWords();
		ArrayList<Integer> ali = ssw.findSubstring(
				"abaababbaba", 
				new String[]{"ab", "ba", "ab", "ba"});
		System.out.println(ali);		
		
	}
}
