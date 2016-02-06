package algo.leet;

import algo.util.TreeNode;

public class BalancedBinaryTree {
	
	public int max(int a, int b){
		if (a>=b){
			return a;
		}else
			return b;
	}
	
	public int depth(TreeNode t){
		if (t==null){
			return 0;
		}
		
		return max(depth(t.left), depth(t.right)) + 1;
		
	}
	
	public boolean isBalanced(TreeNode root) {
		if (root!=null){
			int leftDepth = depth(root.left);
			int rightDepth = depth(root.right);
			return (Math.abs(rightDepth-leftDepth)<=1 && 
					isBalanced(root.left) && 
					isBalanced(root.right));
		}else{
			return true;
		}
    }
}
