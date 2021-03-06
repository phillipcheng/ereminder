package leet.algo.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;
import leet.algo.AddTwoNumber;

public class TestAddTwoNumber {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1() {
		ListNode l1 = ListNodeUtil.getLN("2,4,3");
		ListNode l2 = ListNodeUtil.getLN("5,6,4");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("7,0,8,".equals(l.toString()));
	}
	
	
	@Test
	public void test2() {
		ListNode l1 = ListNodeUtil.getLN("2,4,5");
		ListNode l2 = ListNodeUtil.getLN("5,6,5");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("7,0,1,1,".equals(l.toString()));
	}
	
	@Test
	public void test3() {
		ListNode l1 = ListNodeUtil.getLN("2");
		ListNode l2 = ListNodeUtil.getLN("8,6,5");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("0,7,5,".equals(l.toString()));
	}
	
	@Test
	public void test4() {
		ListNode l1 = ListNodeUtil.getLN("8,6,5");
		ListNode l2 = ListNodeUtil.getLN("2");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("0,7,5,".equals(l.toString()));
	}
	
	@Test
	public void test5() {
		ListNode l1 = ListNodeUtil.getLN("");
		ListNode l2 = ListNodeUtil.getLN("2");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("2,".equals(l.toString()));
	}

}
