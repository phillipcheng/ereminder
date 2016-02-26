import static org.junit.Assert.*;

import org.junit.Test;


public class DimaTwoSequencesSuite {

	@Test
	public void test1() {
		long v = DimaTwoSequences.getNumbers(1, "1", "2", 7);
		System.out.println(v);
		assertTrue(v==1);
	}
	
	@Test
	public void test2() {
		long v = DimaTwoSequences.getNumbers(2, "1 2", "2 3", 11);
		System.out.println(v);
		assertTrue(v==2);
	}
	
	@Test
	public void test3() {
		long v = DimaTwoSequences.getNumbers(5, "1 1 2 2 3", "1 2 2 2 3", 11);
		System.out.println(v);
		assertTrue(v==2);
	}
	
	@Test
	public void test4() {
		long v = DimaTwoSequences.getNumbers(5, "1 1 2 2 2", "1 2 2 3 3", 11);
		System.out.println(v);
		assertTrue(v==8);
	}
	
	@Test
	public void test5(){
		long v = DimaTwoSequences.getNumbers(100, 
		"1 8 10 6 5 3 2 3 4 2 3 7 1 1 5 1 4 1 8 1 5 5 6 5 3 7 4 5 5 3 8 7 8 6 8 9 10 7 8 5 8 9 1 3 7 2 6 1 7 7 2 8 1 5 4 2 10 4 9 8 1 10 1 5 9 8 1 9 5 1 5 7 1 6 7 8 8 2 2 3 3 7 2 10 6 3 6 3 5 3 10 4 4 6 9 9 3 2 6 6",
		"4 3 8 4 4 2 4 6 6 3 3 5 8 4 1 6 2 7 6 1 6 10 7 9 2 9 2 9 10 1 1 1 1 7 4 5 3 6 8 6 10 4 3 4 8 6 5 3 1 2 2 4 1 9 1 3 1 9 6 8 9 4 8 8 4 2 1 4 6 2 6 3 4 7 7 7 8 10 7 8 8 6 4 10 10 7 4 5 5 8 3 8 2 8 6 4 5 2 10 2",
		29056621);
		System.out.println(v);
		assertTrue(v==5236748);
	}
	
	@Test
	public void test6(){
		long v = DimaTwoSequences.getNumbers(100, 
		"2 2 10 3 5 6 4 7 9 8 2 7 5 5 1 7 5 9 2 2 10 3 6 10 9 9 10 7 3 9 7 8 8 3 9 3 9 3 3 6 3 7 9 9 7 10 9 1 1 3 6 2 9 5 9 9 6 2 6 5 6 8 2 10 1 1 6 8 8 4 5 2 6 8 8 5 9 2 3 3 7 7 10 5 4 2 10 6 7 6 5 4 10 6 10 3 9 9 1 5", 
		"3 5 6 4 2 3 2 9 3 8 3 1 10 7 4 3 6 9 3 5 9 5 3 10 4 7 9 7 4 3 3 6 9 8 1 1 10 9 1 6 8 8 8 2 1 6 10 1 8 6 3 5 7 7 10 4 6 6 9 1 5 3 5 10 4 4 1 7 9 7 5 10 6 5 4 1 9 6 4 5 7 3 1 10 2 10 6 6 1 10 7 5 1 4 2 9 2 7 3 10",
		727992321);
		System.out.println(v);
		assertTrue(v==340960284);
	}
	
	@Test
	public void f1() {
		long v = DimaTwoSequences.factorial(4, 2, 5);
		assertTrue(v==1);
	}
	
	@Test
	public void f2() {
		long v = DimaTwoSequences.factorial(5, 2, 11);
		assertTrue(v==8);
	}
	
	@Test
	public void f3() {
		long v = DimaTwoSequences.factorial(17, 0, 727992321);
		System.out.println(v);
	}
}
