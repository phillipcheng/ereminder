package algo.test.util;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;

public class TestListNode {

	@Test
	public void test1() {
		ListNode ln = ListNodeUtil.getLN("1,2,3");
		assertTrue("1,2,3,".equals(ListNodeUtil.toString(ln)));
	}

}
