package leet.algo;

import algo.tree.TreeNode;

public class BinaryTreeMaxPathSum {
	//return an array: A[0] is max root, A[1] is max tree
	public int[] maxTree(TreeNode root){
		if (root==null)
			return new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
		
		if (root.left==null && root.right==null){
			//case 1: left==null, right==null
			return new int[]{root.val, root.val};
		}else{
			//the max path of the root-tree is max among following 3 candidates
			//1. max path of left-tree
			int[] maxL = maxTree(root.left);
			//2. max path of right-tree
			int[] maxR = maxTree(root.right);
			//3. max path to left-root + root + max path to right-root
			int maxLeftFrom = maxL[0];
			int maxRightFrom = maxR[0];
			if (maxLeftFrom<0) maxLeftFrom=0;
			if (maxRightFrom<0) maxRightFrom=0;
			int maxRootThrough=root.val + Math.max(maxLeftFrom, maxRightFrom);
			int maxRootAll = root.val + maxLeftFrom + maxRightFrom;
			return new int[]{maxRootThrough, Math.max(Math.max(maxL[1], maxR[1]), maxRootAll)};
		}
	}
	
	public int maxPathSum(TreeNode root) {
		return maxTree(root)[1];
    }
}
