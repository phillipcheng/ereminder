package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.SortList;

public class TestSortList {

	@Test
	public void testSortList1() {
		SortList sl = new SortList();
		assertTrue("1,".equals(sl.sort("1")));
	}
	
	@Test
	public void testSortList2() {
		SortList sl = new SortList();
		assertTrue("1,2,".equals(sl.sort("2,1")));
	}
	
	@Test
	public void testSortList3() {
		SortList sl = new SortList();
		assertTrue("1,2,3,4,5,6,7,8,".equals(sl.sort("1,2,3,4,5,6,7,8")));
	}
	
	@Test
	public void testSortList4() {
		SortList sl = new SortList();
		assertTrue("1,2,3,".equals(sl.sort("3,2,1")));
	}
	
	@Test
	public void testSortList5() {
		SortList sl = new SortList();
		assertTrue("1,2,3,4,5,6,7,8,9,".equals(sl.sort("9,8,7,6,5,4,3,2,1")));
	}
	
	@Test
	public void testSortList6() {
		SortList sl = new SortList();
		assertTrue("1,2,3,4,5,6,7,8,9,".equals(sl.sort("4,3,2,1,9,8,7,6,5")));
	}
	
	@Test
	public void testSortList7() {
		String str = "1,3,3,1,3,1,3,3,2,3,2,2,1,1,1,3,2,2,1,1,2,2,2,3,3,1,1,2,2,2,1,2,1,1,2,3,3,2,2,3,2,3,2,2,2,1,1,3,2,3,3,1,1,1,2,2,1,2,2,2,2,3,1,3,1,1,1,2,1,2,2,2,1,3,2,2,2,3,3,2,3,3,1,1,2,2,1,2,1,3,2,1,3,3,1,2,1,1,1,1,1,2,1,2,2,2,2,3,3,3,1,1,3,2,1,1,2,1,3,3,2,2,1,3,1,3,1,3,2,2,3,2,3,2,2,1,2,3,1,3,1,2,3,3,2,3,3,3,1,1,2,3,1,2,3,2,1,1,2,3,1,1,3,1,2,2,3,2,1,3,1,2,1,3,2,1,1,2,2,2,1,3,1,3,2,3,3,1,1,3,1,2,1,2,3,1,2,1,1,3,1,3,3,1,1,1,2,2,1,3,1,2,2,3,2,1,3,2,1,3,2,2,3,3,2,2,1,3,2,2,2,2,2,3,2,2,3,1,3,2,1,3,2,1,2,3,3,3,1,2,2,3,1,1,2,2,3,2,1,1,1,1,1,3,2,2,2,1,3,2,1,2,3,2,1,1,2,1,3,3,1,3,1,2,2,1,2,3,2,3,3,1,2,3,2,2,3,3,2,1,3,2,2,2,3,3,3,1,1,2,1,1,2,3,3,3,1,3,2,2,1,2,2,1,2,3,1,3,2,2,3,3,3,1,2,3,2,1,3,1,1,2,2,1,1,1,2,2,3,1,3,1,2,3,3,3,2,2,3,1,1,1,3,2,1,1,3,1,2,3,3,3,2,1,2,3,2,3,2,1,3,2,2,2,2,1,1,3,1,1,1,3,2,2,2,1,2,3,2,3,2,2,1,2,3,2,1,1,3,1,3,3,1,1,1,1,1,2,3,3,3,1,3,2,2,3,1,1,3,1,1,1,3,1,1,2,2,2,1,1,1,1,2,1,3,3,3,1,2,2,2,2,3,3,1,2,2,3,1,3,1,2,1,2,2,3,3,1,3,3,2,1,3,1,1,3,1,2,3,3,3,3,1,1,3,3,3,3,2,2,2,1,1,3,2,2,2,3,1,3,3,3,1,1,3,1,3,2,3,1,2,3,2,2,3,3,3,1,2,1,2,1,2,3,1,2,2,2,1,1,1,2,2,1,2,1,1,1,3,2,1,2,3,2,2,2,1,2,3,2,2,1,3,3,3,1,2,3,3,1,1,3,3,1,1,2,1,2,3,1,2,3,2,2,3,2,1,3,1,3,1,2,2,2,2,1,2,3,3,2,2,2,3,2,2,1,2,2,3,1,3,1,1,1,2,3,3,2,2,3,3,2,3,1,1,2,2,2,3,2,2,1,1,3,2,2,3,3,3,3,1,2,3,3,1,3,3,1,2,2,1,3,2,3,3,2,3,2,1,2,1,2,2,3,3,2,3,3,1,1,2,1,3,2,2,3,1,2,1,3,1,1,3,3,3,3,2,3,3,3,1,3,2,2,2,3,3,1,2,1,2,3,2,2,2,2,3,3,1,1,3,3,2,1,3,2,2,3,2,3,2,2,2,3,1,2,1,3,2,2,1,2,2,3,2,2,2,2,2,1,1,2,1,3,3,2,2,2,1,3,3,3,3,2,3,3,2,3,3,1,3,3,1,3,2,2,2,2,2,1,2,2,3,3,3,1,2,3,1,3,2,2,2,2,3,1,1,1,3,2,3,3,2,3,1,2,1,2,2,1,2,2,3,3,1,2,3,2,2,3,3,1,1,1,2,1,2,3,3,2,2,2,2,3,1,1,1,3,3,1,1,1,3,3,3,2,3,3,1,1,1,2,3,2,2,2,2,1,2,2,3,1,3,1,2,3,1,3,3,1,2,3,2,2,3,3,1,1,2,1,2,3,3,3,2,1,2,1,2,3,1,2,2,1,2,2,2,1,2,3,3,3,3,1,2,1,3,1,1,2,1,3,1,3,2,3,2,3,3,1,2,2,2,3,3,2,1,1,3,1,2,1,3,1,2,1,2,2,2,1,3,1,1,2,2,1,2,1,2,3,3,1,1,3,1,1,1,2,2,3,1,3,3,3,3,2,2,1,3,2,3,2,2,1,3,3,2,1,2,1,2,2,3,1,2,2,1,2,2,3,1,3,3,2,3,1,1,1,3,3,3,3,3,3,1,1,1,3,3,2,2,1,1,3,2,2,2,3,3,3,1,2,2,1,1,3,3,3,2,2,2,2,3,1,2,1,2,2,3,3,3,2,2,2,1,1,1,3,1,1,1,1,1,1,1,2,3,1,3,1,1,3,1,2,1,3,2,2,3,1,2,3,3,2,3,1,1,2,2,3,3,2,2,1,2,2,1,2,2,1,2,1,3,2,1,2,3,1,1,2,3,2,2,2,3,2,3,3,1,1,1,3,3,1,1,2,1,1,1,2,3,3,2,3,3,3,1,2,3,2,2,2,2,2,2,2,1,1,2,2,1,3,1,1,2,3,1,2,3,2,1,2,2,1,3,3,2,2,1,2,1,3,1,3,2,1,1,3,2,3,1,1,2,3,1,1,1,3,2,2,3,2,3,1,2,2,3,1,3,2,1,1,3,2,2,1,3,2,1,2,3,3,1,3,3,3,1,1,2,1,1,2,3,3,2,2,3,2,1,1,2,3,1,1,3,2,3,2,1,2,3,2,1,1,1,1,3,2,3,2,3,1,3,2,1,3,1,3,3,2,2,3,2,3,1,3,2,1,2,2,2,3,3,2,1,2,3,1,1,3,1,2,2,2,3,2,3,1,1,2,1,1,3,1,3,2,1,1,1,3,1,1,3,3,3,3,1,2,3,2,3,2,1,2,1,3,1,3,1,2,2,3,2,3,2,3,3,3,3,1,1,2,2,3,1,1,3,2,1,1,2,1,2,1,1,1,1,1,1,2,3,3,3,3,2,3,1,2,3,3,1,1,3,1,1,1,2,1,1,2,2,2,2,2,1,2,2,2,2,2,2,1,3,3,1,2,2,1,2,1,1,1,1,2,2,3,2,2,2,3,1,3,1,2,2,2,3,3,3,2,1,2,1,1,3,3,2,3,1,2,1,2,2,3,2,3,3,3,3,1,1,1,1,1,1,2,3,1,1,3,1,3,2,3,1,1,1,2,1,1,2,2,2,3,2,2,2,1,3,1,1,1,1,2,3,2,3,2,2,1,3,1,2,1,2,1,2,2,3,1,2,3,3,2,1,1,3,2,3,1,3,1,1,1,2,3,2,1,3,3,1,3,3,3,3,2,2,3,3,1,3,2,2,3,3,2,3,3,3,1,1,2,2,2,2,1,3,3,1,3,2,2,3,1,2,1,1,3,1,1,2,1,1,3,1,1,3,2,2,2,2,2,3,2,1,3,3,2,1,1,2,2,3,2,1,2,1,2,1,2,1,1,1,3,2,2,2,1,3,3,2,3,2,1,1,3,3,1,3,1,3,3,3,3,3,3,3,3,3,2,3,2,2,2,2,2,2,1,1,1,2,3,1,2,2,2,3,1,2,2,1,2,2,1,1,2,3,1,2,2,2,3,3,1,1,3,2,3,2,2,3,1,2,1,1,1,2,3,3,1,1,2,1,2,3,3,2,2,3,1,3,3,3,3,1,1,3,2,2,3,2,1,3,1,3,2,1,2,1,2,1,3,2,3,1,3,2,2,3,3,3,1,2,3,3,1,1,2,1,2,1,3,2,1,1,1,2,2,2,2,2,1,2,1,3,2,1,2,2,1,1,2,3,3,1,3,2,2,3,3,2,1,3,2,1,3,3,2,2,1,3,2,1,3,2,3,2,2,1,2,1,1,3,1,1,3,3,3,3,1,2,3,3,3,3,2,3,2,3,3,3,3,1,1,2,2,1,2,2,2,3,2,1,2,1,2,3,1,1,1,3,1,2,3,2,3,2,2,2,3,1,2,3,2,1,1,1,1,1,2,3,1,3,2,2,3,3,3,2,1,2,2,1,3,2,2,1,2,3,1,3,3,1,3,3,2,2,1,1,2,3,1,1,3,3,1,3,3,3,3,2,3,1,2,2,1,1,1,1,1,3,1,3,2,3,2,1,2,2,1,2,3,2,1,1,3,2,3,2,3,3,1,2,1,2,3,3,2,3,3,2,3,2,3,3,3,1,2,3,2,1,2,3,2,2,1,3,3,3,1,2,3,3,1,2,1,1,2,1,2,1,2,2,3,3,2,1,3,1,1,1,2,2,1,3,3,1,1,2,2,3,2,3,2,3,3,1,3,1,2,3,3,1,2,2,1,1,2,1,1,3,3,2,3,3,1,2,3,3,2,2,3,1,2,3,3,3,3,1,2,3,1,2,2,1,1,3,1,2,3,3,1,2,2,1,3,2,3,2,1,2,1,3,2,3,1,1,1,2,3,1,3,2,2,2,2,2,3,1,2,2,1,2,2,3,2,2,1,3,1,2,2,2,1,2,3,1,1,3,2,3,2,3,3,2,1,3,2,1,3,2,2,3,1,1,3,3,1,2,3,2,1,3,3,3,3,1,1,3,1,3,2,2,3,3,1,3,1,";
		str+="1,2,1,1,3,1,1,2,3,3,1,1,3,2,2,3,3,3,3,2,2,2,2,3,2,1,3,1,2,1,3,3,1,3,3,2,2,2,3,1,3,1,2,2,2,3,1,3,3,1,3,3,2,3,1,1,2,2,2,2,3,1,1,2,3,1,1,3,1,3,1,1,3,3,2,2,1,3,2,2,2,2,3,1,1,1,2,1,1,1,2,2,3,2,1,2,1,1,2,3,2,1,1,1,2,2,3,3,1,3,1,2,2,1,2,2,1,1,1,1,2,1,1,1,1,3,2,3,1,3,3,1,3,1,2,1,1,1,2,3,3,2,1,1,3,2,3,2,3,3,2,3,3,2,2,1,3,2,2,2,3,2,1,1,3,2,1,2,2,1,1,2,1,2,3,2,1,3,2,2,2,3,1,3,2,1,3,2,1,1,1,3,1,1,1,2,2,2,2,2,3,2,3,1,3,1,3,3,2,3,3,2,3,3,3,2,2,3,3,3,1,3,2,1,1,3,2,3,3,2,2,3,1,1,1,2,3,1,3,3,3,3,1,1,3,3,3,1,1,2,1,2,3,2,1,3,1,1,3,3,3,2,3,1,2,2,2,2,1,2,3,3,3,2,1,3,2,1,3,3,3,1,2,3,3,2,1,1,3,1,1,3,1,3,2,3,1,3,2,2,3,2,3,3,2,1,2,3,1,1,1,3,3,3,3,1,2,2,1,3,2,1,3,3,1,1,2,2,1,1,1,2,1,3,2,1,2,3,2,1,1,3,2,2,3,1,1,1,2,2,2,2,3,2,1,3,3,2,3,2,3,2,3,1,1,3,2,1,2,3,1,2,2,1,3,3,2,2,1,3,3,1,3,1,1,3,2,3,2,2,1,2,2,3,2,2,1,3,1,2,1,3,2,1,3,1,3,2,3,2,2,2,1,3,1,1,2,2,3,2,1,2,1,2,3,1,1,3,1,3,2,3,1,1,1,3,1,2,1,3,1,1,2,2,1,2,1,1,2,1,3,1,2,2,2,2,2,1,2,3,2,1,3,1,2,1,2,2,2,1,1,1,2,3,1,1,3,2,3,2,2,2,2,2,2,2,1,2,2,3,3,3,2,1,1,1,3,2,2,3,2,2,1,2,2,2,3,3,3,1,3,3,2,3,1,2,3,1,3,2,1,1,2,1,1,2,1,1,3,3,2,2,3,2,1,2,3,2,1,3,1,2,3,1,3,3,3,2,1,2,2,1,1,1,3,3,2,1,1,1,3,1,2,1,2,2,3,2,3,3,3,2,3,3,1,2,3,2,1,1,1,2,3,1,2,3,1,2,3,1,3,1,3,1,2,2,1,1,3,1,1,3,1,2,3,2,1,3,3,2,2,2,2,2,3,3,2,1,1,1,2,2,3,2,3,3,2,3,1,3,2,2,2,1,2,2,2,2,3,2,1,1,2,2,3,3,2,1,2,3,2,2,3,2,3,2,3,3,1,3,3,3,2,3,1,2,3,3,2,3,1,2,2,1,1,3,3,2,3,2,1,3,2,3,3,1,3,1,2,3,1,2,2,2,3,3,2,1,3,1,3,2,2,3,2,2,2,2,2,2,2,3,3,1,3,2,3,2,1,1,2,1,2,1,1,2,2,3,3,2,1,1,1,3,3,2,1,1,3,1,3,1,3,3,2,2,2,3,1,1,2,3,3,1,2,2,2,1,3,3,1,3,3,1,2,3,3,2,3,3,2,1,3,1,2,1,3,1,1,1,3,3,2,1,1,1,2,2,3,3,2,3,3,1,3,2,3,3,3,2,2,1,2,3,2,3,3,2,2,2,1,2,3,3,3,2,3,3,1,1,3,3,1,2,1,3,3,3,1,3,3,3,2,1,3,3,1,3,2,3,1,2,1,3,3,3,3,1,3,3,1,1,3,2,3,2,1,3,1,1,2,2,1,1,3,1,3,1,3,1,2,2,2,2,3,2,1,3,3,1,2,3,2,3,2,3,1,2,2,2,2,3,1,1,1,3,2,2,3,1,2,2,3,3,2,1,2,1,3,2,1,3,2,1,3,1,1,2,1,2,3,2,3,1,1,2,3,1,1,2,2,2,2,3,2,2,1,2,1,2,1,1,1,1,1,3,1,1,3,3,3,1,3,2,3,3,1,2,1,2,2,3,1,3,3,1,3,3,3,1,1,2,2,3,2,2,3,2,1,3,3,3,3,3,2,1,2,2,3,1,1,1,1,1,3,3,2,3,3,3,2,3,2,1,2,3,2,2,1,2,2,1,2,3,3,1,3,3,2,1,1,1,2,3,2,3,1,3,2,2,3,1,1,2,1,3,2,2,2,2,1,2,2,1,1,2,1,1,2,1,1,2,2,3,2,2,2,1,2,3,2,3,2,3,2,1,2,3,3,3,1,1,2,1,3,1,2,1,1,3,3,1,1,3,2,2,1,3,2,2,2,2,1,3,2,2,2,2,2,3,2,3,2,3,3,3,1,3,3,1,1,3,1,2,2,1,1,2,3,2,3,2,1,3,1,2,3,2,3,3,3,2,1,2,1,3,2,2,1,2,3,3,1,2,2,1,3,3,3,3,1,3,3,2,2,3,2,1,3,2,2,1,3,1,3,2,1,2,3,1,3,3,1,2,2,3,1,1,3,3,1,3,3,3,1,1,1,2,3,3,2,3,3,1,2,1,2,3,3,2,2,3,3,3,3,2,2,3,3,3,1,1,1,1,1,1,2,3,2,2,1,2,1,3,1,2,3,3,1,1,3,1,2,2,1,1,1,2,1,3,3,1,1,1,3,2,1,3,2,2,3,3,2,2,1,1,3,1,2,2,2,1,2,1,2,2,1,3,3,1,2,3,3,3,1,3,3,1,3,3,2,2,2,3,2,2,3,1,3,2,1,3,2,2,1,1,1,2,2,2,1,1,3,1,3,3,2,2,1,2,2,3,3,2,2,2,2,1,1,2,3,1,3,3,2,3,2,3,1,2,2,2,2,2,3,3,2,1,2,2,3,2,3,2,2,1,1,1,2,2,3,1,3,1,2,3,2,1,3,3,1,1,2,3,3,2,1,3,1,2,2,3,1,1,1,3,2,3,1,3,1,3,2,2,3,3,3,2,3,3,3,2,3,3,2,2,1,2,1,3,3,1,1,3,3,2,2,3,2,1,2,3,1,1,2,3,2,1,2,3,1,1,3,1,3,1,3,2,2,1,2,1,2,2,1,2,3,3,1,3,2,2,1,2,2,3,3,3,1,2,1,2,2,3,1,3,1,2,1,3,1,2,1,2,2,1,2,3,3,1,3,3,1,3,2,1,2,3,3,3,3,3,1,2,1,2,2,3,1,2,3,1,3,3,1,1,2,1,1,1,3,2,1,3,3,1,3,2,1,2,1,1,1,1,2,1,3,1,2,1,1,3,2,1,1,2,3,1,1,3,3,3,1,1,2,2,3,1,2,1,3,2,3,3,3,3,1,1,3,3,3,1,2,3,3,3,3,3,1,3,2,2,1,3,1,3,1,3,2,1,1,1,1,1,3,1,1,2,1,2,2,2,3,3,2,3,2,3,2,3,3,1,2,2,3,2,2,1,3,3,3,2,3,1,3,3,2,2,1,2,3,2,3,3,2,1,1,3,1,2,2,2,1,2,1,1,2,2,2,1,2,1,3,1,3,3,1,2,2,3,1,3,1,2,1,2,1,3,3,1,3,2,3,2,1,1,3,2,1,1,2,2,2,1,3,3,3,1,3,3,2,3,3,1,2,3,2,3,2,2,1,2,2,3,1,3,2,3,1,2,2,1,3,3,3,2,2,3,2,2,2,1,3,3,1,1,1,2,3,1,2,3,1,1,1,1,3,3,1,1,3,1,1,3,1,1,1,2,2,1,1,1,1,2,1,1,2,3,1,2,1,2,2,2,3,2,1,1,1,1,2,2,3,2,3,1,1,3,3,2,2,1,2,1,1,1,2,2,2,2,3,2,3,3,2,2,1,1,3,1,3,2,1,2,2,2,3,1,2,1,3,1,3,3,3,1,2,1,1,2,3,2,3,2,3,3,1,3,3,3,2,2,3,3,3,3,3,3,3,2,1,1,3,1,1,3,2,1,2,3,2,3,2,3,3,1,2,3,2,3,2,1,2,3,3,2,3,1,2,1,2,1,2,1,2,3,2,2,2,3,1,2,3,3,1,1,1,3,2,2,3,1,3,2,3,1,2,1,1,2,3,3,3,1,1,1,3,2,1,2,1,2,3,3,3,3,1,1,1,3,2,2,2,3,1,2,1,2,3,1,2,1,2,1,3,1,1,1,2,1,1,3,2,2,3,1,3,1,1,3,3,1,3,1,3,2,2,3,1,2,3,3,3,3,2,2,1,1,3,2,1,3,1,1,2,3,2,2,2,2,3,1,2,2,1,1,2,2,2,1,1,2,3,1,2,2,1,2,3,3,2,1,2,2,1,3,2,1,3,2,1,3,2,3,2,3,1,2,2,2,1,2,3,1,2,2,1,1,1,3,2,3,2,3,3,1,3,3,3,1,3,1,1,1,2,1,3,2,3,1,1,1,1,2,2,3,3,1,1,3,1,3,2,1,2,2,2,1,";
		str+="2,3,1,1,2,3,2,3,3,1,3,2,1,2,2,3,3,1,3,2,2,2,1,1,2,2,2,3,1,3,2,1,3,3,2,3,3,3,2,2,3,2,1,3,2,2,2,3,2,1,2,2,3,2,2,2,3,2,3,1,2,1,1,1,3,1,2,2,2,1,3,1,2,3,1,2,1,1,1,1,3,3,2,2,1,3,2,2,2,3,1,3,3,2,2,2,3,2,3,1,2,2,2,3,2,3,2,2,3,2,3,1,3,1,2,2,3,2,1,1,1,2,3,1,2,3,1,3,1,1,3,1,2,1,1,3,1,2,3,2,1,3,3,3,1,1,2,3,3,1,2,1,2,3,3,3,1,1,2,1,3,2,3,3,3,3,2,3,3,1,1,2,2,3,1,3,2,3,1,2,3,3,2,2,1,2,3,2,1,2,2,2,2,3,2,2,2,1,2,3,3,1,1,2,3,2,2,3,3,3,1,3,2,2,2,1,1,3,2,1,1,1,3,2,1,2,1,2,2,1,2,3,1,2,2,1,1,3,1,2,3,3,3,1,1,3,1,3,3,1,3,2,2,3,1,3,3,3,3,3,3,1,3,2,1,2,3,2,2,2,2,3,3,1,2,3,1,3,3,3,2,2,3,1,3,1,2,2,1,1,2,1,2,1,3,3,2,1,3,1,1,2,1,2,3,1,2,2,2,3,3,3,1,1,1,1,3,2,1,2,2,3,1,3,3,2,2,2,2,1,2,3,1,3,3,2,2,3,1,2,3,2,3,2,1,2,3,2,3,3,1,3,1,2,3,2,3,3,1,1,3,3,3,1,3,1,2,2,3,1,3,1,1,3,1,1,3,3,3,2,2,1,1,3,2,1,2,3,2,3,3,2,1,2,2,1,3,3,2,3,2,3,2,2,3,2,1,1,2,2,1,3,3,3,3,2,3,1,3,1,1,1,3,3,2,1,3,1,3,2,1,3,2,1,3,3,1,3,1,3,3,1,2,2,2,3,1,3,2,2,1,2,1,3,2,2,2,1,2,3,3,3,1,1,2,3,3,1,3,1,2,3,1,1,1,3,1,3,2,3,2,3,1,3,3,1,2,2,1,1,2,1,2,3,3,1,2,2,1,2,2,1,1,1,1,2,2,1,2,3,3,2,1,3,2,1,2,1,1,1,1,2,1,2,3,1,2,3,2,1,3,2,1,3,3,3,1,3,1,2,2,3,1,3,2,1,1,1,2,2,2,3,2,2,3,3,2,2,2,1,2,2,2,1,3,3,2,1,3,2,3,2,2,1,3,1,3,2,3,1,3,3,1,1,3,3,3,1,2,3,1,2,3,1,3,1,2,2,3,1,3,3,3,2,3,3,3,2,3,2,3,1,3,2,3,3,3,3,2,1,1,1,1,2,2,1,1,3,2,3,2,3,3,2,3,3,2,2,3,2,3,3,1,3,3,2,2,2,3,3,1,1,2,1,1,3,3,3,2,3,3,1,1,2,3,2,2,2,2,1,1,1,3,3,1,3,2,1,1,1,1,3,3,1,3,1,2,2,3,2,1,1,2,1,3,2,3,2,1,3,3,3,2,3,2,3,3,2,2,3,1,3,2,2,2,3,3,1,1,2,2,3,1,2,2,2,3,3,3,1,2,3,3,1,3,2,2,1,2,1,2,3,1,3,3,1,2,2,2,2,2,1,3,2,2,3,2,1,1,1,3,3,2,1,1,2,2,3,3,3,1,2,1,3,2,2,1,3,1,2,3,1,3,1,2,1,3,3,1,2,2,3,2,1,1,1,2,3,3,3,3,1,1,1,2,2,2,2,1,1,1,3,2,3,2,2,3,2,1,1,1,2,2,3,2,2,3,3,2,3,2,1,1,1,3,1,1,1,2,1,2,1,1,3,2,2,1,2,2,3,3,1,3,1,3,2,1,2,3,1,1,1,3,2,1,2,2,1,1,1,1,2,1,3,3,1,1,1,1,2,2,1,1,1,2,1,3,2,2,3,1,1,1,1,2,3,2,2,1,2,3,1,1,2,3,3,1,1,3,3,2,1,2,3,2,3,3,2,3,3,1,1,2,1,2,2,3,2,3,2,3,1,1,2,2,1,1,1,1,2,3,1,2,2,1,2,1,3,3,3,3,2,3,3,2,1,2,1,2,3,2,1,2,2,3,1,1,2,2,2,3,3,2,3,1,3,2,2,2,1,1,2,1,2,1,1,1,1,2,2,1,1,1,2,1,3,2,2,1,1,2,1,1,1,2,2,2,2,3,3,2,1,1,3,3,2,3,2,2,1,3,1,1,2,1,1,3,3,2,2,1,2,2,1,1,3,2,3,1,2,2,1,3,3,2,1,1,2,2,2,2,2,1,3,1,3,3,3,3,1,3,2,3,2,3,1,3,1,3,2,2,3,1,1,1,1,3,2,2,2,1,3,1,2,1,3,2,3,1,2,2,3,3,2,2,1,3,1,3,3,3,3,2,3,1,2,1,3,3,3,2,1,3,2,2,3,1,1,2,3,3,1,1,2,2,3,3,3,3,3,3,2,3,1,3,1,1,1,2,1,3,1,3,1,1,3,1,3,1,1,2,1,2,2,3,1,2,1,3,1,2,3,2,2,2,2,2,3,1,1,2,2,3,3,1,1,2,2,3,3,3,1,3,2,3,1,3,1,3,1,3,1,1,3,3,3,1,1,3,3,3,2,2,1,2,2,3,1,2,2,3,3,1,2,1,2,3,3,1,2,2,3,2,2,2,1,1,2,2,2,2,3,3,1,2,2,2,1,2,1,1,3,1,1,1,1,2,2,2,1,1,1,2,3,1,2,3,1,3,3,3,2,1,1,1,1,2,3,3,1,3,1,1,2,2,2,3,2,2,2,3,1,1,1,3,2,2,1,2,3,1,2,1,3,3,3,1,1,3,3,1,1,3,3,1,2,3,1,3,2,2,2,2,3,3,2,3,3,2,2,3,2,2,1,3,1,1,1,3,3,2,2,1,1,1,3,2,1,3,1,3,2,3,2,1,3,3,3,2,2,3,2,2,1,3,1,1,3,2,2,3,2,1,3,3,1,1,3,1,3,1,1,3,1,2,1,2,3,1,3,3,3,3,2,2,3,3,3,3,2,3,1,1,3,1,1,3,3,1,3,3,3,3,1,1,2,2,1,3,3,2,2,3,3,3,3,1,1,1,2,2,3,2,3,1,1,2,3,1,3,3,2,2,2,1,3,1,1,3,3,1,1,2,2,2,3,1,3,2,2,2,3,1,1,3,3,2,2,1,2,1,2,2,1,3,1,3,1,3,2,1,3,3,3,3,2,2,2,3,3,2,2,1,1,2,3,2,1,3,3,1,2,1,2,3,1,1,2,3,3,1,3,2,2,1,3,3,1,1,1,2,1,2,2,1,1,1,2,2,3,1,2,1,3,2,3,1,1,3,1,2,1,3,3,2,2,1,1,1,3,1,2,2,2,3,2,3,2,2,3,1,2,2,1,1,3,3,3,1,3,1,2,2,2,3,1,3,2,1,1,1,3,1,3,2,3,3,3,1,1,2,2,2,3,2,2,2,2,1,1,2,1,1,2,2,2,1,1,1,2,1,3,3,1,3,2,1,2,2,1,2,3,1,1,2,2,3,3,2,2,1,3,3,1,3,2,2,3,2,1,1,3,2,3,1,1,2,3,2,2,2,1,2,2,3,3,2,2,1,1,2,1,1,1,2,1,1,2,3,2,2,2,3,2,2,2,2,2,1,3,1,3,3,3,3,2,3,3,3,2,1,1,1,3,2,3,3,1,1,1,3,1,3,3,2,1,1,2,3,2,3,2,3,2,1,3,3,1,1,2,2,2,1,1,2,3,2,1,1,3,1,3,1,1,3,2,3,2,3,1,1,3,1,3,3,2,3,2,3,1,1,3,1,3,2,3,1,3,2,1,2,1,1,2,2,3,2,1,2,1,1,2,1,2,1,1,2,3,3,3,3,2,2,3,3,2,1,1,1,3,3,3,2,1,2,2,1,2,2,1,3,2,1,2,1,1,1,3,1,2,1,3,3,3,2,3,1,3,2,1,1,2,3,1,3,2,3,3,1,2,1,3,3,1,1,1,2,2,3,2,1,3,2,2,1,2,2,1,3,3,1,2,2,3,3,2,2,1,3,3,2,2,1,1,3,2,3,2,2,1,2,1,3,3,2,1,3,2,3,3,1,3,2,2,3,2,3,3,2,3,1,2,3,3,1,2,1,1,2,3,3,2,2,2,3,1,2,1,3,2,1,3,3,2,1,1,3,3,1,3,3,1,2,1,2,2,3,2,3,2,1,1,3,1,3,3,1,2,2,2,3,2,3,2,3,3,1,2,3,3,2,2,3,1,1,2,3,1,2,3,2,1,1,2,1,1,1,3,3,3,3,1,2,2,2,3,3,2,1,3,1,1,2,3,3,3,3,1,3,2,2,1,2,3,3,3,3,1,3,3,3,1,2,1,2,1,1,1,3,3,1,1,1,2,2,1,3,3,2,3,1,3,2,1,2,1,1,1,1,1,1,2,3,2,1,3,3,2,2,1,1,3,2,1,1,3,2,1,2,1,2,3,1,2,2,3,3,2,";
		str+="3,3,1,2,2,1,3,1,2,3,3,3,2,2,2,3,3,2,2,3,3,2,1,3,3,3,1,3,1,2,3,2,1,2,1,2,2,3,1,2,3,2,3,3,3,1,3,3,3,1,2,3,2,3,3,2,1,2,1,3,1,3,3,2,3,3,2,1,3,3,1,1,3,1,2,3,3,2,3,3,1,2,1,2,1,3,3,3,1,3,3,2,1,2,1,1,1,2,3,1,1,2,3,3,1,1,1,3,2,1,1,1,2,1,2,3,2,2,1,1,3,3,1,3,3,3,1,2,2,2,1,1,1,2,1,1,2,1,1,2,1,1,1,3,3,3,1,3,1,2,2,2,2,1,2,2,1,1,3,3,1,2,2,1,1,3,1,1,3,1,2,2,3,1,3,1,2,3,2,1,3,3,3,1,2,2,1,2,1,1,3,2,1,1,1,3,1,2,1,2,2,3,1,1,3,3,3,3,3,3,1,3,3,2,1,3,2,2,2,1,3,2,1,3,3,2,2,2,1,1,3,1,3,1,2,2,3,2,2,3,3,1,3,3,2,1,3,1,3,3,1,3,2,2,3,3,1,3,3,3,3,3,1,2,3,3,3,3,3,3,1,3,1,3,1,3,2,1,2,1,1,1,1,3,3,2,3,2,3,2,2,2,1,3,2,3,2,2,3,3,2,1,2,2,2,3,2,1,3,1,3,1,2,1,2,3,2,3,1,1,3,1,1,1,1,1,1,2,3,1,2,3,2,2,2,1,3,1,1,1,1,2,1,2,3,2,2,2,1,2,3,1,1,1,3,2,1,1,3,2,1,2,1,2,2,3,3,1,3,1,3,1,1,1,3,2,2,3,2,2,2,3,2,2,3,3,3,1,2,2,2,3,1,3,3,3,3,1,1,1,2,1,2,2,1,3,2,2,2,2,1,2,3,3,1,1,1,1,1,3,3,3,3,1,1,1,3,3,2,3,2,1,2,2,3,2,2,3,1,2,3,1,3,1,3,2,3,1,3,1,1,2,2,1,2,3,3,3,2,3,3,2,2,3,1,2,3,2,2,3,3,3,1,2,1,1,2,3,1,3,1,3,3,1,1,2,2,3,3,2,2,1,2,1,1,3,2,2,1,1,2,3,2,2,1,1,3,2,3,3,3,2,3,1,1,1,2,1,2,3,2,3,2,3,1,2,3,2,3,1,1,2,2,3,1,1,2,1,3,3,1,3,2,2,2,3,1,1,1,1,1,2,2,2,3,2,1,2,2,2,1,2,1,3,1,3,1,3,2,1,2,3,1,3,1,2,3,2,3,1,2,3,2,1,2,2,3,3,3,1,3,3,3,2,2,3,1,3,1,2,3,2,3,2,3,3,2,3,3,2,2,2,1,1,2,2,2,3,3,1,2,1,1,3,1,1,2,1,2,1,3,2,1,1,2,2,1,3,1,3,2,1,1,3,1,2,3,3,2,3,3,1,1,3,3,1,1,1,2,1,2,1,2,2,1,3,2,1,2,2,1,1,2,2,2,2,3,1,3,1,2,3,1,2,1,2,3,3,1,1,3,1,1,2,1,3,3,1,1,1,1,3,2,2,3,2,2,1,2,1,1,3,1,1,1,1,1,1,2,3,3,1,3,2,2,2,3,2,2,3,3,3,2,2,3,2,3,2,2,1,3,3,3,3,3,3,3,1,1,3,1,2,2,1,2,2,1,2,2,3,3,3,2,1,3,2,1,3,3,1,3,2,3,3,3,2,1,1,3,3,3,2,2,3,1,3,1,3,3,1,3,3,3,3,2,1,1,1,2,3,3,2,2,2,2,3,2,2,3,1,1,3,2,2,2,1,2,2,2,1,3,1,2,1,1,1,1,3,2,3,3,2,1,3,1,2,3,3,2,3,1,3,2,3,1,1,1,3,3,1,2,1,1,2,1,3,1,3,3,1,1,1,2,2,2,2,1,3,2,3,3,3,2,3,1,3,2,2,1,1,3,1,3,2,2,1,3,1,3,3,1,2,2,2,1,1,2,2,3,2,2,2,2,1,3,1,1,3,3,1,1,1,1,2,3,1,2,3,1,1,2,2,1,2,1,3,3,2,2,2,2,2,3,2,2,3,2,2,2,3,1,2,2,2,1,1,1,1,3,3,1,1,3,3,2,2,2,3,3,3,1,3,3,2,1,1,2,3,2,1,3,1,1,2,1,2,1,1,2,3,1,1,1,1,3,3,1,2,1,2,1,1,2,3,1,1,1,1,2,1,3,1,3,2,2,3,1,1,3,3,1,1,1,2,2,3,1,2,1,1,2,3,2,3,1,1,3,1,3,3,1,1,1,3,3,3,3,1,3,2,2,1,1,2,1,2,2,3,1,3,1,1,1,3,2,3,2,1,1,3,1,2,2,3,3,3,3,2,3,1,2,2,3,2,3,2,1,3,1,2,2,3,2,3,3,3,3,3,1,3,2,1,3,2,3,1,1,2,1,3,2,1,1,1,1,1,3,1,1,2,2,2,3,3,2,2,1,1,1,1,3,1,3,3,3,2,3,1,2,1,1,3,3,3,2,1,3,2,2,2,2,1,2,2,1,3,1,2,3,2,1,3,1,3,3,1,1,3,2,1,1,3,2,3,2,3,2,3,1,2,2,1,2,2,2,3,1,2,2,3,3,2,3,3,2,3,2,3,2,3,3,1,3,1,1,1,3,3,3,2,2,2,1,2,1,3,1,2,1,3,2,2,3,1,1,3,1,1,2,3,2,3,2,3,1,2,1,2,1,3,1,3,2,1,2,3,3,2,2,1,1,3,3,3,1,3,2,2,2,2,1,2,2,3,2,1,1,3,2,1,2,3,2,1,3,1,3,3,2,1,1,3,3,1,3,3,3,1,2,1,2,2,3,2,1,3,1,2,2,3,1,1,3,3,1,3,1,3,3,1,1,1,3,3,1,1,2,3,3,2,1,1,3,3,1,1,1,2,2,1,3,1,3,3,3,2,2,2,2,3,1,1,3,1,3,1,3,2,3,3,2,2,1,1,1,2,3,2,3,2,3,2,2,2,1,3,3,2,3,1,2,3,3,3,3,1,1,1,3,2,1,3,2,2,3,3,2,1,1,2,1,1,2,3,1,2,1,2,3,1,2,3,3,1,2,2,1,1,2,3,3,3,3,3,1,2,2,2,2,1,1,2,3,2,2,1,3,2,3,1,3,3,2,3,3,2,3,2,3,2,3,3,1,2,1,3,3,3,3,1,2,1,3,2,1,3,2,1,1,3,2,3,1,3,1,2,2,1,3,1,3,2,2,3,2,3,1,2,1,2,1,1,3,2,3,3,1,2,2,2,1,1,1,1,1,3,2,2,1,2,2,2,3,3,2,1,3,1,2,2,3,2,1,3,2,1,1,2,2,1,3,3,1,2,2,2,2,1,2,3,2,2,2,3,3,1,2,1,1,3,3,2,2,2,3,3,2,1,3,2,2,3,3,3,3,3,1,1,3,2,1,2,1,2,2,1,3,3,1,3,2,1,1,3,2,3,3,3,2,2,3,1,1,3,1,3,3,1,3,2,3,1,2,1,3,2,3,3,3,3,1,1,1,1,3,1,3,1,3,1,2,2,3,3,2,2,1,3,1,3,1,3,1,1,2,3,2,3,2,2,1,2,3,2,1,2,3,3,3,3,2,2,2,1,3,2,1,1,1,3,2,2,2,1,1,1,2,2,1,1,1,1,1,1,2,3,2,2,1,3,3,1,3,3,3,1,3,3,1,3,3,3,3,1,2,2,3,1,3,1,3,1,2,3,1,2,2,2,2,3,1,3,2,1,3,2,1,1,1,3,3,3,3,2,3,3,3,1,1,2,1,2,1,3,3,1,3,3,1,2,3,3,2,1,1,2,2,3,3,3,1,1,1,1,1,3,3,3,1,2,3,1,2,1,2,3,2,1,3,2,3,1,1,2,2,1,1,3,2,2,1,1,1,3,3,2,2,1,3,2,1,2,3,2,3,2,1,3,1,3,3,3,3,2,2,3,1,2,3,2,2,2,1,2,1,2,2,2,3,1,2,2,2,2,2,1,2,2,2,2,1,3,3,1,2,2,1,2,2,2,3,3,3,2,2,1,3,3,3,3,1,3,2,3,3,2,2,3,1,1,1,2,2,2,3,3,3,2,2,1,3,2,1,2,3,2,1,3,1,3,2,2,1,1,1,3,1,2,1,2,2,3,3,2,3,3,3,3,2,1,1,1,1,1,2,1,1,3,3,2,3,3,3,3,1,1,3,3,2,3,3,2,1,1,1,2,3,2,2,2,2,2,2,1,1,3,3,1,3,3,2,1,1,3,1,3,1,3,2,2,2,3,2,2,3,3,1,1,2,1,1,2,1,3,2,3,1,3,2,1,1,2,1,3,3,1,1,1,2,3,2,3,2,3,3,2,2,3,3,2,1,3,2,2,2,2,1,2,2,2,2,2,2,3,2,1,2,1,2,3,3,2,2,2,3,1,3,1,3,2,2,1,3,2,3,2,2,2,1,2,2,1,3,3,3,3,3,2,3,2,1,1,3,1,1,1,3,3,1,3,3,3,3,2,2,3,1,2,3,3,2,2,1,2,3,3,1,1,";
		str+="2,2,1,2,1,1,1,2,2,3,1,1,3,1,1,2,2,3,2,1,1,2,2,2,3,2,2,2,3,1,3,2,1,1,2,2,1,1,1,1,1,1,1,3,3,3,2,1,1,1,2,2,2,3,2,2,3,1,2,1,3,3,2,3,1,1,3,3,3,1,2,3,2,1,1,3,3,2,3,3,1,2,2,1,2,1,1,2,1,1,2,2,3,1,1,2,2,1,1,1,3,1,2,3,2,2,2,1,1,1,3,2,3,3,1,2,2,2,3,1,3,2,2,3,2,1,2,3,2,3,2,3,2,1,1,3,3,1,1,2,3,3,2,2,3,3,2,2,2,1,3,3,3,3,1,2,1,2,3,3,2,3,1,1,2,3,1,3,1,1,3,1,1,3,2,3,3,2,3,3,1,1,1,1,1,2,1,1,2,2,2,2,2,1,1,3,2,1,3,2,1,2,2,2,3,3,2,2,2,1,1,3,1,1,2,3,2,2,3,2,1,2,1,3,1,1,1,2,1,1,3,1,1,1,2,2,2,3,2,1,2,3,1,2,3,1,2,1,2,2,2,1,1,3,1,3,3,3,1,3,3,2,3,3,3,1,3,3,1,1,2,1,1,3,1,2,3,1,3,3,1,1,2,2,2,1,2,2,2,2,2,3,1,2,3,3,1,1,2,1,1,1,2,2,1,1,1,3,1,2,2,1,1,3,2,1,1,2,2,3,1,3,1,2,3,1,3,3,2,3,3,1,3,2,3,1,3,2,3,2,1,1,1,1,3,1,1,3,3,1,1,2,2,2,3,3,1,3,2,1,2,3,1,3,3,2,3,2,1,2,1,1,1,3,3,2,1,1,2,1,1,2,2,3,3,2,1,2,2,3,3,1,1,1,3,2,1,2,2,2,2,3,2,2,3,2,3,3,1,2,2,1,3,3,1,1,2,2,1,3,2,2,1,1,1,1,2,1,1,1,3,3,2,3,1,2,1,1,2,1,1,1,2,2,1,3,1,2,2,2,1,1,1,1,3,1,2,2,3,1,1,2,1,2,2,1,2,2,2,2,2,2,3,2,1,2,1,1,2,2,1,3,3,3,2,1,1,3,2,2,1,2,1,2,1,2,3,1,2,3,2,1,3,2,1,2,3,2,3,2,1,1,3,1,1,1,2,1,1,3,1,3,1,3,2,3,2,2,2,2,1,2,1,1,1,3,3,3,3,3,1,2,3,1,2,3,3,2,1,3,3,3,3,3,2,3,3,3,2,2,1,3,1,1,2,2,3,1,1,2,3,1,3,1,2,1,1,2,2,3,2,3,1,3,2,1,1,2,1,2,3,2,2,1,2,3,3,1,1,2,2,3,3,3,3,1,2,1,3,1,2,3,2,3,2,3,3,3,2,1,1,3,1,2,2,2,1,2,1,3,2,2,3,2,1,2,2,2,2,3,3,2,3,2,2,1,1,2,1,2,2,2,3,3,3,3,1,1,2,1,3,1,2,1,3,2,3,2,1,1,3,2,1,1,1,2,1,3,3,1,2,3,1,2,2,3,2,2,1,3,1,2,3,2,3,3,3,1,1,3,1,3,2,1,1,2,1,1,2,1,1,1,1,2,3,2,3,2,2,1,1,1,1,2,1,1,3,1,3,1,1,2,1,3,3,3,2,3,2,2,1,3,2,1,1,3,2,1,3,1,1,2,3,1,3,1,2,2,3,3,1,3,3,2,1,3,3,1,1,3,1,2,2,3,1,2,2,3,3,2,1,2,2,1,1,2,2,2,3,3,1,1,2,1,3,3,3,1,2,1,2,1,3,1,1,1,1,1,2,1,2,3,3,3,1,2,3,1,1,2,2,2,1,3,2,2,3,3,1,2,2,2,3,1,1,2,2,2,1,2,1,1,2,2,2,1,3,1,1,3,1,3,2,1,1,1,1,1,3,3,2,1,1,1,1,2,1,3,3,1,2,1,3,1,1,2,2,3,2,2,2,1,1,1,3,2,3,2,1,3,1,3,3,2,2,2,2,1,1,3,1,1,2,2,1,2,1,1,3,1,2,1,3,3,1,2,1,1,1,3,3,2,3,1,2,1,3,2,1,2,3,2,3,1,1,2,1,2,1,1,2,2,3,2,3,3,3,1,3,2,3,1,3,2,2,3,2,2,1,3,2,3,2,2,1,1,1,1,1,3,3,1,2,1,2,1,3,2,2,2,1,1,1,2,1,2,2,1,3,3,3,3,2,1,3,3,1,2,2,3,1,1,2,2,3,1,1,1,1,1,2,3,3,2,3,3,2,2,3,1,3,2,2,2,1,1,2,2,3,2,2,2,3,3,2,2,2,2,2,3,1,1,2,3,2,3,2,2,1,3,1,3,2,2,2,3,1,2,3,2,3,1,2,3,2,2,1,3,2,2,3,3,1,2,3,1,2,3,3,2,2,3,1,2,3,3,3,1,1,1,3,3,1,2,1,1,3,3,2,3,3,1,2,2,2,1,3,1,3,3,3,3,2,1,2,2,2,2,1,1,2,3,1,1,2,1,1,1,2,1,3,1,1,1,1,1,2,2,3,2,3,3,2,2,3,3,3,2,3,1,1,1,1,1,2,1,1,2,1,3,3,1,3,2,2,1,3,2,2,3,2,3,2,2,1,1,3,2,2,3,1,1,3,2,2,2,3,1,3,3,1,2,2,3,2,2,1,3,2,1,1,2,3,1,3,1,2,2,1,1,2,3,3,1,1,2,3,2,3,1,1,1,2,2,3,3,2,3,2,2,1,1,3,3,3,2,1,1,2,1,1,2,2,2,1,3,2,3,3,2,2,3,3,1,3,2,1,3,2,1,1,2,1,3,2,2,2,1,3,3,3,3,2,1,2,3,3,3,2,3,3,1,2,1,2,2,2,3,1,2,3,1,2,3,1,2,2,1,1,1,2,2,1,1,3,1,2,2,2,3,3,1,3,3,2,2,1,2,3,1,1,2,2,2,3,3,2,2,2,1,2,2,2,1,3,1,1,3,3,2,1,1,3,2,1,2,3,3,3,2,3,2,1,1,2,1,3,3,2,2,2,3,3,3,3,1,3,2,1,1,3,1,2,3,1,1,1,2,3,3,3,3,1,1,3,3,3,3,1,1,1,3,3,2,3,3,3,2,1,2,3,3,2,2,2,1,2,1,1,1,1,3,3,2,3,1,3,2,2,2,3,1,2,1,2,2,2,3,1,3,1,1,1,1,1,2,2,1,1,2,1,2,2,2,2,1,1,3,1,3,1,3,1,1,1,2,2,2,2,2,1,3,1,2,1,3,2,2,1,2,3,2,2,1,3,1,3,1,1,1,2,2,3,2,3,1,3,3,1,2,1,2,3,3,2,2,3,1,3,2,3,1,3,1,3,1,2,3,2,2,1,2,2,2,3,1,2,3,3,3,2,2,2,2,2,2,3,1,2,3,1,3,2,2,1,3,3,2,3,3,2,1,3,1,2,2,3,1,3,3,3,3,2,1,3,2,3,3,3,2,3,2,3,3,1,1,1,1,3,3,2,2,3,1,2,2,1,1,3,3,3,3,2,3,2,3,3,1,2,3,2,2,3,1,3,2,3,2,3,3,2,3,1,3,1,1,3,2,3,2,3,2,1,1,3,3,3,2,2,3,2,3,3,1,3,2,1,1,3,3,1,3,3,1,3,1,3,3,2,3,3,2,2,1,2,1,1,2,1,2,2,1,2,2,1,1,1,2,1,1,2,2,1,3,1,1,1,3,3,3,2,3,1,2,3,1,3,1,3,1,3,2,1,1,3,2,3,2,1,2,1,2,2,3,1,3,3,2,2,2,3,2,2,2,1,3,2,1,1,3,3,3,2,3,1,1,1,2,2,3,2,2,3,2,1,3,1,3,3,1,1,2,1,3,3,2,2,3,2,3,2,2,1,2,1,2,2,2,3,1,3,2,1,1,3,3,1,2,1,2,3,3,1,2,1,2,1,2,3,2,3,2,2,2,1,2,1,2,2,3,2,1,3,2,1,3,3,1,2,2,3,1,2,1,1,2,2,1,1,2,3,3,3,3,1,1,1,3,1,3,3,3,1,3,2,3,1,3,1,1,1,3,2,2,2,3,2,3,3,2,1,1,2,3,1,3,1,3,3,2,1,2,3,1,3,1,2,3,3,1,2,2,2,3,2,3,2,3,1,2,2,2,2,3,1,2,2,1,3,2,3,3,1,3,1,2,2,1,2,1,1,1,2,1,1,3,1,3,3,2,3,3,1,3,1,2,3,3,3,1,2,2,1,2,1,2,3,2,3,2,1,2,1,1,3,3,2,1,1,2,3,3,1,1,3,2,2,3,1,2,3,2,2,1,2,3,1,1,1,2,2,3,2,3,3,3,2,1,1,3,1,3,2,3,1,3,3,1,2,1,1,2,3,3,2,1,2,3,2,2,1,1,1,3,3,3,3,2,1,3,3,1,1,2,2,1,1,3,1,2,3,2,1,3,3,3,1,1,2,2,3,3,3,3,1,2,2,2,2,1,1,3,1,2,1,2,2,2,3,3,1,3,2,1,2,1,2,1,1,1,3,3,2,3,3,3,3,1,3,1,3,2,3,1,3,3,1,3,";
		str+=str;
		str+=str;
		System.out.println(str.length());
		long startTime = System.nanoTime();
		SortList sl = new SortList();
		sl.sort(str);
		long endTime = System.nanoTime();
		System.out.println("elapsed:" + (endTime-startTime));
		
	}

}
