package leet.algo.test;

import leet.algo.ConstructBinaryTreeFromPreorderAndInorderTraversal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestConstructBinaryTreeFromPreorderAndInorderTraversal {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		ConstructBinaryTreeFromPreorderAndInorderTraversal pp = new ConstructBinaryTreeFromPreorderAndInorderTraversal();
		TreeNode tn = pp.buildTree(new int[]{1,2,4,5,3,6}, new int[]{4,2,5,1,3,6});
		logger.info(TreeNodeUtil.bfsToString(tn));
	}

}
