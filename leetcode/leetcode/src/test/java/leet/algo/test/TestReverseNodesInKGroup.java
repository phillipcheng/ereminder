package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;
import leet.algo.ReverseNodesInKGroup;

public class TestReverseNodesInKGroup {

	@Test
	public void test() {
		ListNode ln = ListNodeUtil.getLN("1,2,3,4,5,6,7,8");
		ReverseNodesInKGroup rnikg = new ReverseNodesInKGroup();
		ListNode ret = rnikg.reverseKGroup(ln, 3);
		System.out.println(ret);
	}

}
