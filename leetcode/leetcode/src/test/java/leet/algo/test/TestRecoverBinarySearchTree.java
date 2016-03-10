package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.RecoverBinarySearchTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestRecoverBinarySearchTree {
	private static Logger logger =  LogManager.getLogger(TestRecoverBinarySearchTree.class);
	
	private static String orgString1 = "5,3,7,1,4,6,8,#,#,#,#,#,#,#,#,";
	@Test
	public void test1(){
		TreeNode tn = TreeNodeUtil.fromLevelString("5,3,7,1,4,6,8,#,#,#,#,#,#,#,#");
		RecoverBinarySearchTree rbst = new RecoverBinarySearchTree();
		rbst.recoverTree(tn);
		String str = TreeNodeUtil.toLevelString(tn);
		logger.info(str);
		assertTrue(orgString1.equals(str));
	}
	
	@Test
	public void test2(){
		TreeNode tn = TreeNodeUtil.fromLevelString("5,7,3,1,4,6,8,#,#,#,#,#,#,#,#");
		RecoverBinarySearchTree rbst = new RecoverBinarySearchTree();
		rbst.recoverTree(tn);
		String str = TreeNodeUtil.toLevelString(tn);
		logger.info(str);
		assertTrue(orgString1.equals(str));
	}
	
	@Test
	public void test3(){
		TreeNode tn = TreeNodeUtil.fromLevelString("5,3,8,1,4,6,7,#,#,#,#,#,#,#,#");
		RecoverBinarySearchTree rbst = new RecoverBinarySearchTree();
		rbst.recoverTree(tn);
		String str = TreeNodeUtil.toLevelString(tn);
		logger.info(str);
		assertTrue(orgString1.equals(str));
	}
}
