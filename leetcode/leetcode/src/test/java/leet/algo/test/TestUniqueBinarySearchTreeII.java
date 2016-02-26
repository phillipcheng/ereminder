package leet.algo.test;

import java.util.List;

import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import leet.algo.UniqueBinarySearchTrees;
import leet.algo.UniqueBinarySearchTreesII;

public class TestUniqueBinarySearchTreeII {
	
	@Test
	public void test1(){
		UniqueBinarySearchTreesII ubst = new UniqueBinarySearchTreesII();
		List<TreeNode> ltn = ubst.generateTrees(0);
		System.err.println(String.format("total:%d", ltn.size()));
		/*
		for (TreeNode tn:ltn){
			System.err.println(TreeNodeUtil.preOrderToString(tn));
		}*/
	}
	
	@Test
	public void test2(){
		UniqueBinarySearchTrees ubst = new UniqueBinarySearchTrees();
		for (int i=0; i<20;i++){
			int size = ubst.numTrees(i);
			System.err.println(String.format("total:%d", size));
		}
		/*
		for (TreeNode tn:ltn){
			System.err.println(TreeNodeUtil.preOrderToString(tn));
		}*/
	}

}
