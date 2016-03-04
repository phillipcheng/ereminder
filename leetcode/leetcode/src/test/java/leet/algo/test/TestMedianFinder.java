package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.MedianFinder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestMedianFinder {
	private static Logger logger =  LogManager.getLogger(TestMedianFinder.class);
	@Test
	public void test1(){
		MedianFinder mf = new MedianFinder();
		mf.addNum(1);
		mf.addNum(2);
		assertTrue(1.5==mf.findMedian());
		mf.addNum(3); 
		assertTrue(2==mf.findMedian());
	}
	
	@Test
	public void test2(){
		MedianFinder mf = new MedianFinder();
		mf.addNum(-1);
		assertTrue(-1==mf.findMedian());
		mf.addNum(-2);
		assertTrue(-1.5==mf.findMedian());
		mf.addNum(-3);
		assertTrue(-2==mf.findMedian());
		mf.addNum(-4);
		assertTrue(-2.5==mf.findMedian());
		mf.addNum(-5);
		assertTrue(-3==mf.findMedian());
	}
	
	@Test
	public void test3(){
		MedianFinder mf = new MedianFinder();
		mf.addNum(12);
		assertTrue(12==mf.findMedian());
		mf.addNum(10);
		assertTrue(11==mf.findMedian());
		mf.addNum(13);
		assertTrue(12==mf.findMedian());
		mf.addNum(11);
		assertTrue(11.5==mf.findMedian());
		mf.addNum(5);
		assertTrue(11==mf.findMedian());
	}
	
	@Test
	public void test4(){
		MedianFinder mf = new MedianFinder();
		mf.addNum(40);
		mf.addNum(12);
		mf.addNum(16);
		mf.addNum(14);
		mf.addNum(35);
		mf.addNum(19);
		mf.addNum(34);
		mf.addNum(35);
		mf.addNum(28);
		mf.addNum(35);
		mf.addNum(26);
		mf.addNum(6);
		mf.addNum(8);
		mf.addNum(2);
		mf.addNum(14);
		mf.addNum(25);
		mf.addNum(25);
		logger.info(mf.findMedian());
		assertTrue(25==mf.findMedian());
	}
}
