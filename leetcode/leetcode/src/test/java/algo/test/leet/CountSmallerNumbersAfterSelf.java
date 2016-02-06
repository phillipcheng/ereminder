package algo.test.leet;

import java.util.ArrayList;
import java.util.List;

//You are given an integer array nums and you have to return a new counts array. 
//The counts array has the property where counts[i] is the number of smaller elements to the right of nums[i].
public class CountSmallerNumbersAfterSelf {
	class BSTNode{
		private int value;
		private int count;
		private int leftSize;//number of item on the left, does not equal to number of items smaller than value
		private int rightSize;//number of item on the right, does not equal to number of item bigger than value
		BSTNode left;
		BSTNode right;
		
		public BSTNode(int a){
			value =a;
			count =1;
			leftSize=0;
			rightSize=0;
			left=null;
			right=null;
		}
		
		public void add(int a){
			if (value==a){
				count++;
			}else if (value>a){
				if (left==null){
					left = new BSTNode(a);
				}else{
					left.add(a);
				}
				leftSize++;
			}else{
				if (right==null){
					right = new BSTNode(a);
				}else{
					right.add(a);
				}
				rightSize++;
			}
		}
		
		public BSTNode getNode(int v){
			if (value==v){
				return this;
			}else if (value>v){
				if (left!=null){
					return left.getNode(v);
				}else{
					return null;
				}
			}else{
				if (right!=null){
					return right.getNode(v);
				}else{
					return null;
				}
			}
		}

		public int getSmaller(int v){
			if (v==value){
				return leftSize;
			}else if (v<value){
				if (left==null){
					return 0;
				}else{
					return left.getSmaller(v);
				}
			}else{
				if (right==null){
					return leftSize + count;
				}else{
					return leftSize + count + right.getSmaller(v);
				}
			}
		}
		
		public int getBigger(int v){
			if (v==value){
				return rightSize;
			}else if (v<value){
				if (left==null){
					return rightSize + count;
				}else{
					return rightSize + count + left.getBigger(v);
				}
			}else{
				if (right==null){
					return 0;
				}else{
					return right.getBigger(v);
				}
			}
		}
		//
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public int getLeftSize() {
			return leftSize;
		}
		public void setLeftSize(int leftSize) {
			this.leftSize = leftSize;
		}
		public int getRightSize() {
			return rightSize;
		}
		public void setRightSize(int rightSize) {
			this.rightSize = rightSize;
		}
	}
	
    public List<Integer> countSmaller(int[] nums) {
    	List<Integer> list = new ArrayList<Integer>();
    	BSTNode bst = null;
    	for (int i=nums.length-1; i>=0; i--){
    		if (bst == null){
    			bst = new BSTNode(nums[i]);
    		}else{
    			bst.add(nums[i]);
    		}
    		list.add(0, bst.getSmaller(nums[i]));
    	}
    	return list;
    }
}
