package cy.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.util.ListNode;

public class TestListNode {

	@Test
	public void test1() {
		ListNode ln = ListNode.getLN("1,2,3");
		assertTrue("1,2,3,".equals(ln.toString()));
	}

}
