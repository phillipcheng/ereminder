package leet.algo.test;

import leet.algo.RemoveDuplicatesFromSortedListII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;

public class TestRemoveDuplicatesFromSortedListII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		RemoveDuplicatesFromSortedListII rd = new RemoveDuplicatesFromSortedListII();
		ListNode ln = ListNodeUtil.getLN("[1,2,3]");
		ListNode ret = rd.deleteDuplicates(ln);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test2(){
		RemoveDuplicatesFromSortedListII rd = new RemoveDuplicatesFromSortedListII();
		ListNode ln = ListNodeUtil.getLN("[1]");
		ListNode ret = rd.deleteDuplicates(ln);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test3(){
		RemoveDuplicatesFromSortedListII rd = new RemoveDuplicatesFromSortedListII();
		ListNode ln = ListNodeUtil.getLN("[1,2,2]");
		ListNode ret = rd.deleteDuplicates(ln);
		logger.info(ListNodeUtil.toString(ret));
	}

}
