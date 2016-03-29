package leet.algo.test;

import leet.algo.ReorderList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;

public class TestReorderList {

	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		ReorderList rl = new ReorderList();
		ListNode head = ListNodeUtil.getLN("1,2,3,4");
		rl.reorderList(head);
		logger.info(ListNodeUtil.toString(head));
	}

}
