package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.FirstMissingPositive;

public class TestFirstMissingPositive {

	@Test
	public void test1() {
		FirstMissingPositive fmp = new FirstMissingPositive();
		assertTrue(3==fmp.firstMissingPositive(new int[]{1,2,0}));
		
		assertTrue(2==fmp.firstMissingPositive(new int[]{3,4,-1,1}));
	}
	
	@Test
	public void test2() {
		FirstMissingPositive fmp = new FirstMissingPositive();
		assertTrue(3==fmp.firstMissingPositive(new int[]{2,1}));
	}
	
	@Test
	public void test3() {
		FirstMissingPositive fmp = new FirstMissingPositive();
		assertTrue(1==fmp.firstMissingPositive(new int[]{2}));
	}

}
