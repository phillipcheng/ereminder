package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.LetterCombPhoneNum;

public class TestLetterCombPhoneNum {

	@Test
	public void test() {
		LetterCombPhoneNum lcpn = new LetterCombPhoneNum();
		System.out.println(lcpn.letterCombinations("23"));
	}
	
	@Test
	public void test1() {
		LetterCombPhoneNum lcpn = new LetterCombPhoneNum();
		System.out.println(lcpn.letterCombinations("123"));
	}
	
	@Test
	public void test2() {
		LetterCombPhoneNum lcpn = new LetterCombPhoneNum();
		System.out.println(lcpn.letterCombinations("232"));
	}

}
