package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.SearchInRotatedSortedArray;

public class TestSearchInRotatedSortedArray {

	@Test
	public void test() {
		SearchInRotatedSortedArray sirsa = new SearchInRotatedSortedArray();
		int[] a = new int[]{4,5,6,7,0,1,2};
		
		assertTrue(0==sirsa.search(a, 4));
		assertTrue(1==sirsa.search(a, 5));
		assertTrue(2==sirsa.search(a, 6));
		assertTrue(3==sirsa.search(a, 7));
		assertTrue(4==sirsa.search(a, 0));
		assertTrue(5==sirsa.search(a, 1));
		assertTrue(6==sirsa.search(a, 2));
		assertTrue(-1==sirsa.search(a, 9));
	}

}
