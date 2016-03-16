package leet.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContainsDuplicateII {
	
	public boolean containsNearbyDuplicate(int[] nums, int k) {
		Map<Integer, Integer> s = new HashMap<Integer, Integer>();//value and latest idx
        for (int i=0; i<nums.length; i++){
        	if (s.containsKey(nums[i])){
        		int preIdx = s.get(nums[i]);
        		if (i-preIdx<=k)
        			return true;
        		else{
        			s.put(nums[i], i);
        		}
        	}else{
        		s.put(nums[i], i);
        	}
        }
        return false;
    }

}
