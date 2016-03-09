package leet.algo;

public class JumpGame {
	//Given an array of non-negative integers, you are initially positioned at the first index of the array.
	//Each element in the array represents your maximum jump length at that position.
	//Determine if you are able to reach the last index.
	
	//return the idx of the next zero item, starting from startIdx
	public int getNextZeroIdx(int[] nums, int startIdx){
		for (int i=startIdx; i<nums.length; i++){
			if (nums[i]==0){
				return i;
			}
		}
		return nums.length;
	}
	
	//return the idx of the next non zero item, starting from startIdx
	public int getNextNoneZeroIdx(int[] nums, int startIdx){
		for (int i=startIdx; i<nums.length; i++){
			if (nums[i]>0){
				return i;
			}
		}
		return nums.length;
	}
	
	public boolean canJump(int[] nums) {
		if (nums.length<=0) return false;
		int start = 0;
		int end = getNextZeroIdx(nums, start);
		int maxleap = 0;//index of the max leap
		for (int i=start; i<end; i++){
			maxleap = Math.max(maxleap, nums[i]+i);
		}
		start = getNextNoneZeroIdx(nums, end);
		end = getNextZeroIdx(nums, maxleap);
		while (maxleap < (nums.length-1) && maxleap>=start){
			for (int i=start; i<end; i++){
				maxleap = Math.max(maxleap, nums[i]+i);
			}
			start = getNextNoneZeroIdx(nums, end);
			end = getNextZeroIdx(nums, maxleap);
		}
		if (maxleap>=(nums.length-1)){
			return true;
		}else {//if (maxleap<nextStart)
			return false;
		}
    }
	
	public int jump(int[] nums) {
		int start = 0;
		int end = 0;
		int ml = 0;
		int ln = 0;
		while (ml<nums.length-1){
			for (int i=start; i<=end; i++){
				ml = Math.max(ml, nums[i]+i);
			}
			start = end+1;
			end = ml;
			ln++;
		}
		return ln;
    }

}
