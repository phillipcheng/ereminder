package leet.algo;

import java.util.Arrays;

//Given an unsorted array nums, reorder it such that nums[0] < nums[1] > nums[2] < nums[3]....
//Can you do it in O(n) time and/or in-place with O(1) extra space?
public class WiggleSort {
	private void swap(int[] nums, int i, int j){
		int b = nums[i];
		nums[i] = nums[j];
		nums[j] = b;
	}
	
	public void wiggleSort(int[] nums) {
		Arrays.sort(nums);
		int mid = nums.length/2;
		if (nums.length%2==0){
			mid = nums.length/2-1;
		}
		int idx=0;
		int tmp;//currently smallest
		tmp = nums[idx];
		nums[idx]=nums[mid];
		idx++;
		while(idx<nums.length){
		}
    }
}
