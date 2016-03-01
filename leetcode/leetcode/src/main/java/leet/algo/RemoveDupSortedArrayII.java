package leet.algo;

//Given a sorted array, remove the duplicates in place such that each element appear only once and return the new length.
//What if duplicates are allowed at most twice?
public class RemoveDupSortedArrayII {
	
	public int removeDuplicates(int[] nums) {
		if (nums.length==0)
			return 0;
        int idx=0;
        int prev=nums[0];
        int cnt=0;
        for (int i=1; i<nums.length; i++){
        	if (prev!=nums[i]){
        		idx++;
        		prev=nums[i];
        		nums[idx]=prev;
        		cnt=0;
        	}else{
        		if (cnt<1){
        			cnt++;
        			idx++;
        			nums[idx]=prev;
        		}
        	}
        }
        return idx+1;
    }

}
