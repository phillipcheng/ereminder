package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindMinRotatedSortedArrayII {
	
	private static Logger logger =  LogManager.getLogger(FindMinRotatedSortedArrayII.class);
	//
	private int findMin(int[] nums, int left, int right, boolean asc){
		//logger.info(String.format("%d..%d, asc:%b", left, right, asc));
		if (Math.abs(left-right)<=1){
			return Math.min(nums[left], nums[right]);
		}
		int mid = (left+right)/2;
		if (asc){
			if (nums[mid]>=nums[left]){
				return findMin(nums, mid, right, asc);
			}else{
				return findMin(nums, left, mid, asc);
			}
		}else{
			if (nums[mid]>=nums[left]){
				return findMin(nums, left, mid, asc);
			}else{
				return findMin(nums, mid, right, asc);
			}
		}
	}
	
	public int findMin(int[] nums) {
		int n = nums.length;
		boolean asc=true;
		int start=-1;
		for (int i=0; i<n; i++){
			if (nums[i]!=nums[n-1]){
				if (nums[i]<nums[n-1]){
					asc = false;
				}
				start = i;
				break;
			}
		}
		if (start==-1){
			return nums[0];
		}
		return findMin(nums, start, n-1, asc);
        
    }

}
