package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.ValidateBST;

import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestValidateBST {
	
	@Test
	public void test1(){
		ValidateBST vbst = new ValidateBST();
		TreeNode root = TreeNodeUtil.bfsFromString("10,5,15,#,#,6,20");
		assertFalse(vbst.isValidBST(root));
	}
	
	@Test
	public void test2(){
		ValidateBST vbst = new ValidateBST();
		TreeNode root = TreeNodeUtil.bfsFromString("10,5,15,#,#,11,20");
		assertTrue(vbst.isValidBST(root));
	}

}
