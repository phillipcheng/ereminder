package leet.algo.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import algo.util.ListNode;
import algo.util.ListNodeUtil;
import leet.algo.ConvertSortedListToBST;

public class TestConvertSortedListToBST {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		ListNode ln = ListNodeUtil.getLN("1,2,3");
		ConvertSortedListToBST cs = new ConvertSortedListToBST();
		TreeNode tn = cs.sortedListToBST(ln);
		String ret = TreeNodeUtil.bfsToString(tn);
		logger.info(ret);
	}
	
	@Test
	public void test2(){
		ListNode ln = ListNodeUtil.getLN("1,2,3,4");
		ConvertSortedListToBST cs = new ConvertSortedListToBST();
		TreeNode tn = cs.sortedListToBST(ln);
		String ret = TreeNodeUtil.bfsToString(tn);
		logger.info(ret);
	}
	
	@Test
	public void test3(){
		ListNode ln = ListNodeUtil.getLN("1,2,3,4,5,6");
		ConvertSortedListToBST cs = new ConvertSortedListToBST();
		TreeNode tn = cs.sortedListToBST(ln);
		String ret = TreeNodeUtil.bfsToString(tn);
		logger.info(ret);
	}
	
	

}
