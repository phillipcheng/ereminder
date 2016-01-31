package cy;

import cy.util.TreeNode;

public class BinaryTreeMaxPathSum {
	
	public static int max(int m1, int m2){		
		if (m1 >m2 ){
			return m1;
		}else
			return m2;
		
	}
	
	//return the max path in the tree
	public int maxTree(TreeNode root){
		if (root==null)
			return Integer.MIN_VALUE;
		
		if (root.left==null && root.right==null){
			//case 1: left==null, right==null
			return root.val;		
		}else{
			//the max path of the root-tree is max among following 4 candidates
			//1. max path of left-tree
			int maxLeftTree = maxTree(root.left);
			//2. max path of right-tree
			int maxRightTree = maxTree(root.right);
			//3. max path of from-root
			int maxRootFrom = maxFrom(root);
			//4. max path from left-tree + root + max path from right-tree
			int maxLeftFrom = maxFrom(root.left);
			int maxRightFrom = maxFrom(root.right);
			int maxRootThrough;
			if (maxLeftFrom==Integer.MIN_VALUE && maxRightFrom==Integer.MIN_VALUE){
				maxRootThrough = root.val;
			}else if (maxLeftFrom==Integer.MIN_VALUE && maxRightFrom!=Integer.MIN_VALUE){
				maxRootThrough = maxRightFrom;
				maxRootThrough += root.val;
			}else if (maxLeftFrom!=Integer.MIN_VALUE && maxRightFrom == Integer.MIN_VALUE){
				maxRootThrough = maxLeftFrom;
				maxRootThrough += root.val;
			}else{
				maxRootThrough = maxLeftFrom;
				maxRootThrough +=root.val;
				maxRootThrough +=maxRightFrom;
			}
			
			int m1 = max(maxLeftTree, maxRightTree);
			int m2 = max(maxRootFrom, maxRootThrough);
			
			return max(m1, m2);
			
		}
	}
	
	//return the max path from the root
	public int maxFrom(TreeNode root){
		if (root == null)
			return Integer.MIN_VALUE;
		if (root.left==null && root.right==null){
			//case 1: left==null, right==null
			return root.val;
		}else{
			int left = maxFrom(root.left);
			int right = maxFrom(root.right);
			if (left!=Integer.MIN_VALUE && right!=Integer.MIN_VALUE){
				//case 2: left!=null, right!=null
				if (left > right){
					left +=root.val;
					//left.path.add(root);
					return left;
				}else{
					right +=root.val;
					return right;
				}
			}else if (left==Integer.MIN_VALUE){
				//case 3: left==null, right!=null
				if (right >0){
					right +=root.val;
					//right.path.add(root);
					return right;
				}else{
					return root.val;
				}
			}else{
				//case 4: left!=null, right==null
				if (left >0){
					left +=root.val;
					//left.path.add(root);
					return left;
				}else{
					return root.val;
				}
			}
		}
	}
	
	public int maxPathSum(TreeNode root) {
		return maxTree(root);
    }
}
