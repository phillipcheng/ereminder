package leet.algo;

import java.util.Arrays;

//Given an unsorted array nums, reorder it such that nums[0] < nums[1] > nums[2] < nums[3]....
//Can you do it in O(n) time and/or in-place with O(1) extra space?
public class WiggleSort {
	public void wiggleSort(int[] nums) {
		//sort it
		Arrays.sort(nums);
		//pick the medium number as the 1st
		int[] out = new int[nums.length];
		int mid = nums.length/2;
		if (nums.length%2==1){
			mid ++;
		}
		for (int i=0; i<nums.length; i++){
			if (i==0){
				out[i]=nums[mid-1];
			}else if (i%2==0){
				out[i] = nums[mid-1-i/2];
			}else{
				out[i]=nums[nums.length -1 -i/2];
			}
		}
		for (int i=0; i<nums.length; i++){
			nums[i] = out[i];
		}
	}
}
