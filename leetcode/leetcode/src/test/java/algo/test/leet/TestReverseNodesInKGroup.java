package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.ReverseNodesInKGroup;
import algo.util.ListNode;

public class TestReverseNodesInKGroup {

	@Test
	public void test() {
		ListNode ln = ListNode.getLN("1,2,3,4,5,6,7,8");
		ReverseNodesInKGroup rnikg = new ReverseNodesInKGroup();
		ListNode ret = rnikg.reverseKGroup(ln, 3);
		System.out.println(ret);
	}

}
