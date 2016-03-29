package princeton.algo1.interview.test;

import leet.algo.test.TestAddTwoNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import princeton.algo1.interview.SelectionIn2SortedArray;

public class TestSelectionIn2SortedArray {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		SelectionIn2SortedArray sa = new SelectionIn2SortedArray();
		int ret = sa.find(new int[]{1,3,4,5}, new int[]{2,6,7}, 5);
		assert(ret==5);
	}
	
	@Test
	public void test2(){
		SelectionIn2SortedArray sa = new SelectionIn2SortedArray();
		int ret = sa.find(new int[]{1,3,4,5}, new int[]{2,6,7}, 2);
		assert(ret==2);
	}

}
