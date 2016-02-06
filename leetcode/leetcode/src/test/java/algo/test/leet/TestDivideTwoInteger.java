package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.DevideTwoInteger;

public class TestDivideTwoInteger {

	@Test
	public void test1() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a=1234;
		int b = 11;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test2() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a=-1234;
		int b = 11;
		System.out.println(a/b);
		System.out.println(dti.divide(a, b));
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test3() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a=1234;
		int b = -11;
		System.out.println(a/b);
		System.out.println(dti.divide(a, b));
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test4() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a=-2147483648;
		int b = 1;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test6() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a=-2147483648;
		int b = -3;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test5() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a=-1;
		int b = 1;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test7() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a = -1010369383;
		int b = -2147483648;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test8() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a = 102137742;
		int b = 1817624734;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test9() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a = -2147483648;
		int b = -1017100424;
		assertTrue(a/b==dti.divide(a, b));
	}
	
	@Test
	public void test10() {
		DevideTwoInteger dti = new DevideTwoInteger();
		int a = -1384904293;
		int b = -153821790;
		assertTrue(a/b==dti.divide(a, b));
	}

}
