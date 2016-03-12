package leet.algo.test;

import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import leet.algo.SortedArrayToBST;

public class TestSortedArrayToBST {
	
	@Test
	public void test1(){
		SortedArrayToBST sa = new SortedArrayToBST();
		TreeNode tn = sa.sortedArrayToBST(new int[]{1,3});
		System.err.println(TreeNodeUtil.bfsToString(tn));
	}
	
	@Test
	public void test2(){
		SortedArrayToBST sa = new SortedArrayToBST();
		TreeNode tn = sa.sortedArrayToBST(new int[]{3,5,8});
		System.err.println(TreeNodeUtil.bfsToString(tn));
	}
}
