package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.AtoI;

public class TestAtoI {
	
	@Test
	public void test() {
		int a= Integer.MAX_VALUE;
		int b = 1;
		int c= (int) (((long)a+(long)b)/(long)2);
		System.out.println(c);
	}
	
	@Test
	public void test0() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi(" 010");
		System.out.println(n);
		assertTrue(10==n);
	}
	
	@Test
	public void test1() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   +1");
		System.out.println(n);
		assertTrue(1==n);
	}
	
	@Test
	public void test2() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   -1");
		System.out.println(n);
		assertTrue(-1==n);
	}
	
	@Test
	public void test3() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   -a");
		System.out.println(n);
		assertTrue(0==n);
	}
	
	@Test
	public void test4() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   0a");
		System.out.println(n);
		assertTrue(0==n);
	}
	
	@Test
	public void test5() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   123456789877651");
		System.out.println(n);
		assertTrue(n==Integer.MAX_VALUE);
	}
	
	@Test
	public void test6() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   -123456789877651");
		System.out.println(n);
		assertTrue(n==Integer.MIN_VALUE);
	}
	
	@Test
	public void test7() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   -1234567x1234");
		System.out.println(n);
		assertTrue(n==-1234567);
	}
	
	@Test
	public void test8() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("   -2147483648");
		System.out.println(n);
		assertTrue(n==-2147483648);
	}
	
	@Test
	public void test9() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("2147483647");
		System.out.println(n);
		assertTrue(n==2147483647);
	}
	
	@Test
	public void test10() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("21111");
		System.out.println(n);
		assertTrue(n==21111);
	}
	
	@Test
	public void test11() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("-2147483647");
		System.out.println(n);
		assertTrue(n==-2147483647);
	}
	
	@Test
	public void test12() {
		AtoI atoi = new AtoI();
		int n = atoi.atoi("    +11191657170");
		System.out.println(n);
		assertTrue(n==2147483647);
	}

}
