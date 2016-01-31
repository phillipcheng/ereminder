package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.MinDepthBinaryTree;
import cy.util.TreeNode;

public class TestMinDepthBinaryTree {

	@Test
	public void test0() {
		TreeNode t = TreeNode.buildTreeFromLevel("");
		MinDepthBinaryTree mdbt = new MinDepthBinaryTree();
		assertTrue(0==mdbt.minDepth(t));	
	}
	
	@Test
	public void test1() {
		TreeNode t = TreeNode.buildTreeFromLevel("1,2");
		MinDepthBinaryTree mdbt = new MinDepthBinaryTree();
		assertTrue(2==mdbt.minDepth(t));	
	}
	
	@Test
	public void test2() {
		TreeNode t = TreeNode.buildTreeFromLevel("1,2,3,4,#,#,5");
		MinDepthBinaryTree mdbt = new MinDepthBinaryTree();
		assertTrue(3==mdbt.minDepth(t));	
	}

}
