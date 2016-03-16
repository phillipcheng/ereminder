package leet.algo;

import algo.tree.TreeNode;

public class ValidateBST {
	class BSTResult{
		boolean valid;
		int min;
		int max;
		public BSTResult(boolean valid, int min, int max){
			this.valid = valid;
			this.min = min;
			this.max = max;
		}
	}
	
	private BSTResult checkBST(TreeNode root){
		if (root == null){
        	return new BSTResult(true, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }else if (root.left==null && root.right==null){
        	return new BSTResult(true, root.val, root.val);
        }
		BSTResult leftValid;
		BSTResult rightValid;
		boolean leftRes = true;
		boolean rightRes = true;
		int min = root.val;
		int max = root.val;
        if (root.left!=null){
        	leftValid = checkBST(root.left);
        	leftRes = leftValid.valid && root.val > leftValid.max;
        	min = leftValid.min;
        }
        if (root.right!=null){
        	rightValid = checkBST(root.right);
        	rightRes = rightValid.valid && root.val < rightValid.min;
        	max = rightValid.max;
        }
        return new BSTResult(leftRes && rightRes, min, max);
	}
	
	public boolean isValidBST(TreeNode root) {
        BSTResult res = checkBST(root);
        return res.valid;
    }

}
