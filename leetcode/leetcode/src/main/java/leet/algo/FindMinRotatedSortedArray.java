package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindMinRotatedSortedArray {
	private static Logger logger =  LogManager.getLogger(FindMinRotatedSortedArray.class);
	//
	private int findMin(int[] nums, int left, int right, boolean asc){
		//logger.info(String.format("%d..%d, asc:%b", left, right, asc));
		if (Math.abs(left-right)<=1){
			return Math.min(nums[left], nums[right]);
		}
		int mid = (left+right)/2;
		if (asc){
			if (nums[mid]>nums[left]){
				return findMin(nums, mid, right, asc);
			}else{
				return findMin(nums, left, mid, asc);
			}
		}else{
			if (nums[mid]>nums[left]){
				return findMin(nums, left, mid, asc);
			}else{
				return findMin(nums, mid, right, asc);
			}
		}
	}
	
	public int findMin(int[] nums) {
		if (nums.length==1) return nums[0];
		boolean asc=true;
		if (nums[nums.length-1]>nums[0]) asc=false;
        return findMin(nums, 0, nums.length-1, asc);
    }

}
