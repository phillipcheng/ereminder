package leet.algo;


//Given an array nums containing n + 1 integers where each integer is between 1 and n (inclusive), 
//Assume that there is only one duplicate number, find the duplicate one.

/*
You must not modify the array (assume the array is read only).
You must use only constant, O(1) extra space.
Your runtime complexity should be less than O(n2).
There is only one duplicate number in the array, but it could be repeated more than once.
 */
public class FindDuplicateNumber {//1+1/2+1/3+1/4...+1/n = ln(n)
	public int findDuplicate(int[] nums) {
		for (int i=1; i<nums.length; i++){//i is the gap
			for (int j=0; j+i<nums.length; j++){
				if (nums[j]==nums[j+i]){
					return nums[j];
				}
			}
		}
		return -1;
    }
}
