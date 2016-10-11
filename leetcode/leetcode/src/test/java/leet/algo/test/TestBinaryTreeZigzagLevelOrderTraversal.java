package leet.algo.test;

import java.util.List;

import leet.algo.BinaryTreeZigzagLevelOrderTraversal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestBinaryTreeZigzagLevelOrderTraversal {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		BinaryTreeZigzagLevelOrderTraversal btz = new BinaryTreeZigzagLevelOrderTraversal();
		TreeNode tn = TreeNodeUtil.bfsFromString("3,9,20,#,#,15,7");
		List<List<Integer>> out = btz.zigzagLevelOrder(tn);
		logger.info(out);
	}

}
