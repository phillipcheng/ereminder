package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.NumberOfDigitOne;

import org.junit.Test;

public class TestNumberOfDigitOne {
	
	@Test
	public void test0(){
		NumberOfDigitOne nod=new NumberOfDigitOne();
		assertTrue(2==nod.countDigitOne(10));
	}
	
	@Test
	public void test1(){
		NumberOfDigitOne nod=new NumberOfDigitOne();
		assertTrue(6==nod.countDigitOne(13));
	}
	
	@Test
	public void test4(){
		NumberOfDigitOne nod=new NumberOfDigitOne();
		assertTrue(13==nod.countDigitOne(30));
	}
	
	@Test
	public void test5(){
		NumberOfDigitOne nod=new NumberOfDigitOne();
		assertTrue(21==nod.countDigitOne(100));
	}
	
}
