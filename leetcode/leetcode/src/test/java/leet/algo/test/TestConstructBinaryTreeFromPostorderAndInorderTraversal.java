package leet.algo.test;

import leet.algo.ConstructBinaryTreeFromPostorderAndInorderTraversal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestConstructBinaryTreeFromPostorderAndInorderTraversal {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		ConstructBinaryTreeFromPostorderAndInorderTraversal pp = new ConstructBinaryTreeFromPostorderAndInorderTraversal();
		TreeNode tn = pp.buildTree(new int[]{4,2,5,1,3,6}, new int[]{4,5,2,6,3,1});
		logger.info(TreeNodeUtil.bfsToString(tn));
	}

}
