package leet.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermutationsII {
	
	private List<Integer> getUniqueNums(int[] nums){
		List<Integer> pos = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0; i<nums.length; i++){
			if (!set.contains(nums[i])){
				set.add(nums[i]);
				pos.add(i);
			}
		}
		return pos;
	}
	
	private List<List<Integer>> pu(int[] nums){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		if (nums.length>1){
			List<Integer> poss = getUniqueNums(nums);
			for (int pos:poss){
				int first = nums[pos];
				int[] nnum = new int[nums.length-1];
				System.arraycopy(nums, 0, nnum, 0, pos);
				System.arraycopy(nums, pos+1, nnum, pos, nums.length-1-pos);
				List<List<Integer>> ll = pu(nnum);
				for (List<Integer> l:ll){
					l.add(0, first);
					result.add(l);
				}
			}
		}else if (nums.length==1){
			List<Integer> l = new ArrayList<Integer>();
			l.add(nums[0]);
			result.add(l);
		}
		return result;
	}
	
	public List<List<Integer>> permuteUnique(int[] nums) {
        return pu(nums);
    }

}
