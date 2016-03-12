package leet.algo.test;

import java.util.List;

import leet.algo.BinaryTreePostorderTraversal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestBinaryTreePostorderTraversal {
	private static Logger logger =  LogManager.getLogger(TestBinaryTreePostorderTraversal.class);
	@Test
	public void test1(){
		BinaryTreePostorderTraversal btpt = new BinaryTreePostorderTraversal();
		TreeNode root = TreeNodeUtil.bfsFromString("1,#,2,3,#,#,#");
		List<Integer> li = btpt.postorderTraversal(root);
		logger.info(li);
	}
	
	@Test
	public void test0(){
		BinaryTreePostorderTraversal btpt = new BinaryTreePostorderTraversal();
		TreeNode root = TreeNodeUtil.bfsFromString("1,#,2,3,#,#,#");
		List<Integer> li = btpt.postorderTraversal(root);
		logger.info(li);
	}

}
