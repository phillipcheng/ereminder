package leet.algo.test;

import leet.algo.LowestCommonAncestorOfBinaryTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestLowestCommonAncestorOfBinaryTree {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		LowestCommonAncestorOfBinaryTree lca = new LowestCommonAncestorOfBinaryTree();
		TreeNode root = TreeNodeUtil.bfsFromString("3,5,1,6,2,0,8,#,#,7,4,#,#,#,#");
		TreeNode p = root.left;//5
		TreeNode q = root.left.right.right;//4
		TreeNode l = lca.lowestCommonAncestor(root, p, q);
		logger.info(l.val);
	}

	@Test
	public void test2(){
		LowestCommonAncestorOfBinaryTree lca = new LowestCommonAncestorOfBinaryTree();
		TreeNode root = TreeNodeUtil.bfsFromString("3,5,1,6,2,0,8,#,#,7,4,#,#,#,#");
		TreeNode p = root.left;
		TreeNode q = root.right.right;
		TreeNode l = lca.lowestCommonAncestor(root, p, q);
		logger.info(l.val);
	}
}
