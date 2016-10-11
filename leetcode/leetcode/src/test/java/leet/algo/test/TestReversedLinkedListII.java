package leet.algo.test;

import leet.algo.ReversedLinkedListII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;

public class TestReversedLinkedListII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	//1 ≤ m ≤ n ≤ length of list.
	@Test
	public void test1(){//1<m<n==length
		ListNode ln = ListNodeUtil.getLN("[1,2,3,4]");
		ReversedLinkedListII rll = new ReversedLinkedListII();
		ListNode lnn = rll.reverseBetween(ln, 3, 4);
		logger.info(ListNodeUtil.toString(lnn));
	}
	
	@Test
	public void test2(){//1=m<n==length
		ListNode ln = ListNodeUtil.getLN("[1,2,3]");
		ReversedLinkedListII rll = new ReversedLinkedListII();
		ListNode lnn = rll.reverseBetween(ln, 1, 3);
		logger.info(ListNodeUtil.toString(lnn));
	}
	
	@Test
	public void test3(){//1=m=n==length
		ListNode ln = ListNodeUtil.getLN("[1]");
		ReversedLinkedListII rll = new ReversedLinkedListII();
		ListNode lnn = rll.reverseBetween(ln, 1, 1);
		logger.info(ListNodeUtil.toString(lnn));
	}
}
