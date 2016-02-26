package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.ZigZagConversation;

public class TestZigZagConversation {

	
	@Test
	public void test0() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("", 1);
		assertTrue("".equals(s));
	}
	
	@Test
	public void test1() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIA", 4);
		assertTrue("INAEGAMHYICI".equals(s));
	}
	
	@Test
	public void test2() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIAM", 4);
		assertTrue("INMAEGAMHYICI".equals(s));
	}
	
	@Test
	public void test3() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIAMC", 4);
		assertTrue("INMAEGACMHYICI".equals(s));
	}
	
	@Test
	public void test4() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIAMCH", 4);
		assertTrue("INMAEGACMHYIHCI".equals(s));
	}
	
	@Test
	public void test5() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIAMCHE", 4);
		assertTrue("INMAEGACMHYIHCIE".equals(s));
	}
	
	@Test
	public void test6() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIAMCHEN", 4);
		System.out.println(s);
		assertTrue("INMAEGACMHYIHNCIE".equals(s));
	}
	
	@Test
	public void test7() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("IAMCHENGYIIAMCHENG", 4);
		assertTrue("INMAEGACGMHYIHNCIE".equals(s));
	}
	

	@Test
	public void test8() {
		ZigZagConversation zz = new ZigZagConversation();
		String s = zz.convert("ABCDEF", 5);
		assertTrue("ABCDFE".equals(s));
	}

}
