package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.ReverseNodesInKGroup;
import cy.util.ListNode;

public class TestReverseNodesInKGroup {

	@Test
	public void test() {
		ListNode ln = ListNode.getLN("1,2,3,4,5,6,7,8");
		ReverseNodesInKGroup rnikg = new ReverseNodesInKGroup();
		ListNode ret = rnikg.reverseKGroup(ln, 3);
		System.out.println(ret);
	}

}
