package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.StrStr;

public class TestStrStr {

	@Test
	public void test0() {
		StrStr strstr = new StrStr();
		System.out.println(strstr.strStr("1234342345345", "423"));
	}
	
	@Test
	public void test1() {
		StrStr strstr = new StrStr();
		System.out.println(strstr.strStr("aaa", "aaaa"));
	}
	
	@Test
	public void test2() {
		StrStr strstr = new StrStr();
		System.out.println(strstr.strStr("mississippi", "issipi"));
	}

}
