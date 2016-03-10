package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import leet.algo.MinDepthBinaryTree;

public class TestMinDepthBinaryTree {

	@Test
	public void test0() {
		TreeNode t = TreeNodeUtil.fromLevelString("");
		MinDepthBinaryTree mdbt = new MinDepthBinaryTree();
		assertTrue(0==mdbt.minDepth(t));	
	}
	
	@Test
	public void test1() {
		TreeNode t = TreeNodeUtil.fromLevelString("1,2");
		MinDepthBinaryTree mdbt = new MinDepthBinaryTree();
		assertTrue(2==mdbt.minDepth(t));	
	}
	
	@Test
	public void test2() {
		TreeNode t = TreeNodeUtil.fromLevelString("1,2,3,4,#,#,5");
		MinDepthBinaryTree mdbt = new MinDepthBinaryTree();
		assertTrue(3==mdbt.minDepth(t));	
	}

}
