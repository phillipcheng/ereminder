package leet.algo.test;

import leet.algo.RotateList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.ListNode;
import algo.util.ListNodeUtil;

public class TestRotateList {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		RotateList rl = new RotateList();
		ListNode ln = ListNodeUtil.getLN("1,2,3,4,5");
		ListNode ret = rl.rotateRight(ln, 2);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test2(){
		RotateList rl = new RotateList();
		ListNode ln = ListNodeUtil.getLN("1");
		ListNode ret = rl.rotateRight(ln, 0);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test3(){
		RotateList rl = new RotateList();
		ListNode ln = ListNodeUtil.getLN("1");
		ListNode ret = rl.rotateRight(ln, 1);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test4(){
		RotateList rl = new RotateList();
		ListNode ln = ListNodeUtil.getLN("1,2");
		ListNode ret = rl.rotateRight(ln, 2);
		logger.info(ListNodeUtil.toString(ret));
	}
	
	@Test
	public void test5(){
		RotateList rl = new RotateList();
		ListNode ln = ListNodeUtil.getLN("1,2");
		ListNode ret = rl.rotateRight(ln, 1);
		logger.info(ListNodeUtil.toString(ret));
	}

}
