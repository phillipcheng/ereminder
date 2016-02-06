package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.AddTwoNumber;
import algo.util.ListNode;

public class TestAddTwoNumber {

	@Test
	public void test1() {
		ListNode l1 = ListNode.getLN("2,4,3");
		ListNode l2 = ListNode.getLN("5,6,4");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("7,0,8,".equals(l.toString()));
	}
	
	
	@Test
	public void test2() {
		ListNode l1 = ListNode.getLN("2,4,5");
		ListNode l2 = ListNode.getLN("5,6,5");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("7,0,1,1,".equals(l.toString()));
	}
	
	@Test
	public void test3() {
		ListNode l1 = ListNode.getLN("2");
		ListNode l2 = ListNode.getLN("8,6,5");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("0,7,5,".equals(l.toString()));
	}
	
	@Test
	public void test4() {
		ListNode l1 = ListNode.getLN("8,6,5");
		ListNode l2 = ListNode.getLN("2");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("0,7,5,".equals(l.toString()));
	}
	
	@Test
	public void test5() {
		ListNode l1 = ListNode.getLN("");
		ListNode l2 = ListNode.getLN("2");
		AddTwoNumber atn = new AddTwoNumber();
		ListNode l = atn.addTwoNumbers(l1, l2);
		assertTrue("2,".equals(l.toString()));
	}

}
