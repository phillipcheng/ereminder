package leet.algo;

import algo.tree.TreeNode;

public class SortedArrayToBST {
	
	public TreeNode makeNode(int nums[], int start, int end){
		if (end-start>1){
			int mid= start+(end-start)/2;
			TreeNode left = makeNode(nums, start, mid-1);
			TreeNode right = makeNode(nums, mid+1, end);
			TreeNode t = new TreeNode(nums[mid]);
			t.left = left;
			t.right = right;
			return t;
		}else if (end-start==1){
			TreeNode tn1 = new TreeNode(nums[end]);
			TreeNode tn2 = new TreeNode(nums[start]);
			tn1.left = tn2;
			return tn1;
		}else if (end==start){
			return new TreeNode(nums[start]);
		}else{
			System.err.println(String.format("end %d can't be less then start %d", end, start));
			return null;
		}
	}
	
	public TreeNode sortedArrayToBST(int[] nums) {
		return makeNode(nums, 0, nums.length-1);
    }
}
