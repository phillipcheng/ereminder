package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.Candy;

public class TestCandy {

	@Test
	public void test1() {
		Candy c = new Candy();
		int[] ratings = new int[]{1,1};
		assertTrue(2==c.candy(ratings));
	}
	
	@Test
	public void test2() {
		Candy c = new Candy();
		int[] ratings = new int[]{1,2};
		assertTrue(3==c.candy(ratings));
	}
	
	@Test
	public void test3() {
		Candy c = new Candy();
		int[] ratings = new int[]{2,1};
		assertTrue(3==c.candy(ratings));
	}
	

	@Test
	public void test4() {
		Candy c = new Candy();
		int[] ratings = new int[]{1001};
		assertTrue(1==c.candy(ratings));
	}
	
	@Test
	public void test5() {
		Candy c = new Candy();
		int[] ratings = new int[]{};
		assertTrue(0==c.candy(ratings));
	}
	
	@Test
	public void test6() {
		Candy c = new Candy();
		int[] ratings = new int[]{1,2,3};
		assertTrue(6==c.candy(ratings));
	}
	
	@Test
	public void test7() {
		Candy c = new Candy();
		int[] ratings = new int[]{1,2,1};
		assertTrue(4==c.candy(ratings));
	}
	
	@Test
	public void test8() {
		Candy c = new Candy();
		int[] ratings = new int[]{1,2,2};
		assertTrue(4==c.candy(ratings));
	}



}
