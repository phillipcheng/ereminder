package leet.algo;

import algo.tree.TreeNode;

//The minimum depth is the number of nodes along 
//the shortest path from the root node down to the nearest leaf node.
public class MinDepthBinaryTree {
	
	public boolean isLeaf(TreeNode n){
		return (n.left==null && n.right==null);
	}
	
	public int min(int a, int b){
		if (a<b){
			return a;
		}else{
			return b;
		}
	}
	
	
	public int minDepth(TreeNode root) {
		if (root==null){
			return 0;
		}
        if (isLeaf(root)){
        	return 1;
        }else if (root.right==null && root.left!=null){
        	return minDepth(root.left)+1;
        }else if (root.right!=null && root.left==null){
        	return minDepth(root.right)+1;
        }else{
        	return min (minDepth(root.left), minDepth(root.right))+1;
        }
        
    }
}
