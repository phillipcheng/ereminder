package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchForRange {
	private static Logger logger =  LogManager.getLogger(SearchForRange.class);
	//return i, nums[i-1]<target, nums[i]==target [from, to]
	public int searchLess(int[] nums, int target, int from, int to, boolean less){
		//logger.info(String.format("from:%d, to:%d", from, to));
		if (from == to){
			if (nums[from]==target){
				return from;
			}else{
				return -1;
			}
		}else if (from +1 == to){
			if (less){
				if (nums[from]==target){
					return from;
				}else if (nums[to]==target){
					return to;
				}else{
					return -1;
				}
			}else{
				if (nums[to]==target){
					return to;
				}else if (nums[from]==target){
					return from;
				}else{
					return -1;
				}
			}
		}else{
			int mid = (from + to)/2;
			if (less){
				if (nums[mid]>=target){
					return searchLess(nums, target, from, mid, less);
				}else{
					return searchLess(nums, target, mid, to, less);
				}
			}else{
				if (nums[mid]<=target){
					return searchLess(nums, target, mid, to, less);
				}else{
					return searchLess(nums, target, from, mid, less);
				}
			}
		}
	}
	
	public int[] searchRange(int[] nums, int target) {
       int less = searchLess(nums, target, 0, nums.length-1, true);
       int more = searchLess(nums, target, 0, nums.length-1, false);
       return new int[]{less, more};
    }
}
