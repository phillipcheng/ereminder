package cy.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cy.BinaryTreeMaxPathSum;
import cy.util.TreeNode;

public class TestBinaryTreeMaxPathSum {

	@Test
	public void test1() {
		BinaryTreeMaxPathSum btmps = new BinaryTreeMaxPathSum();
		TreeNode t1 = new TreeNode(1);
		TreeNode t2 = new TreeNode(2);
		
		t1.left = t2;
		int s = btmps.maxPathSum(t1);
		
		System.out.println(s);
		
	}
	
	@Test
	public void test2() {
		BinaryTreeMaxPathSum btmps = new BinaryTreeMaxPathSum();
		StringBuffer sb = new StringBuffer();
		for (int i=1; i<(int)Math.pow(2, 20);i++){
			sb.append(i);
			sb.append(",");
		}
		TreeNode t1 = TreeNode.buildTreeFromLevel(sb.toString());
	
		long start= System.nanoTime();
		int s = btmps.maxPathSum(t1);
		long end= System.nanoTime();
		System.out.println(s + ", time:" + (end-start));
		
	}

}
