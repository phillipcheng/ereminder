package leet.algo;

import algo.tree.TreeNode;

public class RecoverBinarySearchTree {
	TreeNode minNode; //point to the node with the lowest value among the conflicted nodes
	TreeNode maxNode; //point to the node with the highest value among the conflicted nodes
	int min = Integer.MAX_VALUE;
	int max = Integer.MIN_VALUE;
	//
	public void checkBST(TreeNode lowNode, TreeNode highNode, TreeNode root){
		if (root!=null){
			if (lowNode!=null){
				if (lowNode.val>root.val){//conflict
					if (root.val<min){
						min = root.val;
						minNode = root;
					}
					if (lowNode.val>max){
						max = lowNode.val;
						maxNode = lowNode;
					}
				}
			}
			if (highNode!=null){
				if (highNode.val<root.val){
					if (root.val>max){
						max = root.val;
						maxNode = root;
					}
					if (highNode.val<min){
						min = highNode.val;
						minNode = highNode;
					}
				}
			}
			checkBST(lowNode, root, root.left);
			checkBST(root, highNode, root.right);
		}
	}
	
	public void recoverTree(TreeNode root) {
        checkBST(null, root, root.left);
        checkBST(root, null, root.right);
        if (minNode!=null && maxNode!=null){
        	int v = minNode.val;
        	minNode.val = maxNode.val;
        	maxNode.val = v;
        }
    }

}
