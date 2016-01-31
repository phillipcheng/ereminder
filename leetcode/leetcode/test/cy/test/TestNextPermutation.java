package cy.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import cy.NextPermutation;

public class TestNextPermutation {

	@Test
	public void test0() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{1,2};
		np.nextPermutation(a);
		int[] b = new int[]{2,1};
		System.out.println(Arrays.toString(a));
		System.out.println(Arrays.toString(b));
		assertTrue(Arrays.equals(a, b));
	}
	
	@Test
	public void test1() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{1,2,3};
		System.out.println(Arrays.toString(a));
		np.nextPermutation(a);
		int[] b = new int[]{1,3,2};
		assertTrue(Arrays.equals(a, b));
	}

	@Test
	public void test2() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{3,2,1};
		np.nextPermutation(a);
		int[] b = new int[]{1,2,3};
		assertTrue(Arrays.equals(a, b));
	}
	
	@Test
	public void test3() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{1,1,5};
		np.nextPermutation(a);
		int[] b = new int[]{1,5,1};
		assertTrue(Arrays.equals(a, b));
	}
	
	@Test
	public void test4() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{1,3,2};
		np.nextPermutation(a);
		int[] b = new int[]{2,1,3};
		assertTrue(Arrays.equals(a, b));
	}
	
	@Test
	public void test5() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{2,3,1};
		np.nextPermutation(a);
		int[] b = new int[]{3,1,2};
		assertTrue(Arrays.equals(a, b));
	}
	
	@Test
	public void test6() {
		NextPermutation np = new NextPermutation();
		int[] a = new int[]{4,2,0,2,3,2,0};
		np.nextPermutation(a);
		int[] b = new int[]{4,2,0,3,0,2,2};
		assertTrue(Arrays.equals(a, b));
	}
}
