package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import leet.algo.BinaryTreeMaxPathSum;
import leet.algo.BinaryTreeMaxPathSum;

public class TestBinaryTreeMaxPathSum {

	
	@Test
	public void test3() {
		BinaryTreeMaxPathSum btmps = new BinaryTreeMaxPathSum();
		StringBuffer sb = new StringBuffer();
		for (int i=1; i<(int)Math.pow(2, 20);i++){
			sb.append(i);
			sb.append(",");
		}
		TreeNode t1 = TreeNodeUtil.buildTreeFromLevel(sb.toString());
	
		long start= System.nanoTime();
		int s = btmps.maxPathSum(t1);
		long end= System.nanoTime();
		System.out.println(s + ", time:" + (end-start));
		
	}
	
	@Test
	public void test4() {
		BinaryTreeMaxPathSum btmps = new BinaryTreeMaxPathSum();
		TreeNode t1 = TreeNodeUtil.buildTreeFromLevel("1,-2,3");
	
		long start= System.nanoTime();
		int s = btmps.maxPathSum(t1);
		long end= System.nanoTime();
		System.out.println(s + ", time:" + (end-start));
		
	}
	
	

}
