package algo.test.leet;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import algo.leet.LongestSubStringWR;

public class TestLongestSubstringWR {

	@Test
	public void test1() {
		LongestSubStringWR lsswr = new LongestSubStringWR();
		int len = lsswr.lengthOfLongestSubstring("abcabcbb");
		System.out.println("len:" + len);
		assertTrue(len==3);
	}
	
	@Test
	public void test2() {
		LongestSubStringWR lsswr = new LongestSubStringWR();
		int len = lsswr.lengthOfLongestSubstring("bbbbbbbbbb");
		System.out.println("len:" + len);
		assertTrue(len==1);
	}
	
	@Test
	public void test3() {
		LongestSubStringWR lsswr = new LongestSubStringWR();
		int len = lsswr.lengthOfLongestSubstring("bbbbbbbbbbaaaaadddddabcadeafdabcdfeghij");
		System.out.println("len:" + len);
		assertTrue(len==10);
	}
	
	@Test
	public void test4() {
		long start = System.nanoTime();
		LongestSubStringWR lsswr = new LongestSubStringWR();
		int len = lsswr.lengthOfLongestSubstring("fpdcztbudxfipowpnamsrfgexjlbjrfoglthewbhtiriznzmolehqnlpwxrfowwwjrd");
		
		long end = System.nanoTime();
		System.out.println("elapse:" + (end-start));
		
		start=System.nanoTime();
		LongestSubStringWR l = new LongestSubStringWR();
		len = l.lengthOfLongestSubstring("fpdcztbudxfipowpnamsrfgexjlbjrfoglthewbhtiriznzmolehqnlpwxrfowwwjrd");
		end = System.nanoTime();
		System.out.println("elapse:" + (end-start));
		
	}
	
	@Test
	public void test5() {
		LongestSubStringWR lsswr = new LongestSubStringWR();
		int len = lsswr.lengthOfLongestSubstring("hnwnkuewhsqmgbbuqcljjivswmdkqtbxixmvtrrbljptnsnfwzqfjmafadrrwsofsbcnuvqhffbsaqxwpqcac");
		System.out.println("len:" + len);
		assertTrue(len==12);
	}
	
	@Test
	public void test6() {
		LongestSubStringWR lsswr = new LongestSubStringWR();
		int len = lsswr.lengthOfLongestSubstring("c");
		System.out.println("len:" + len);
		assertTrue(len==1);
	}
	

}
