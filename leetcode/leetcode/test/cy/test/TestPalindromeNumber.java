package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.PalindromeNumber;

public class TestPalindromeNumber {

	@Test
	public void test1() {
		PalindromeNumber p = new PalindromeNumber();
		assertTrue(p.isPalindrome(121));
	}
	
	
	@Test
	public void test3() {
		PalindromeNumber p = new PalindromeNumber();
		assertTrue(!p.isPalindrome(1222231));
	}
	
	@Test
	public void test4() {
		PalindromeNumber p = new PalindromeNumber();
		assertTrue(!p.isPalindrome(-2147447412));
	}
	
	@Test
	public void test5() {
		PalindromeNumber p = new PalindromeNumber();
		assertTrue(p.isPalindrome(2147447412));
	}


}
