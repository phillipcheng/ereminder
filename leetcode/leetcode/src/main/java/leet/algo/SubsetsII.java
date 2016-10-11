package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubsetsII {
	
	private List<List<Integer>> gen(List<List<Integer>> input){
		if (input.size()>=1){
			List<Integer> in = input.remove(0);
			List<List<Integer>> out = gen(input);
			List<List<Integer>> output = new ArrayList<List<Integer>>();
			for (int i=0;i<=in.size();i++){
				List<Integer> in1 = in.subList(0, i);
				if (out.size()>0){
					for (List<Integer> o1: out){
						List<Integer> a = new ArrayList<Integer>();
						a.addAll(in1);
						a.addAll(o1);
						output.add(a);
					}
				}else{
					List<Integer> a = new ArrayList<Integer>();
					a.addAll(in1);
					output.add(a);
				}
			}
			return output;
		}else{
			return new ArrayList<List<Integer>>();
		}
	}
	
	public List<List<Integer>> subsetsWithDup(int[] nums) {
		Arrays.sort(nums);
		List<List<Integer>> groups = new ArrayList<List<Integer>>();
		List<Integer> g1 = new ArrayList<Integer>();
		int pre = nums[0];
		g1.add(pre);
		for (int i=1; i<nums.length; i++){
			if (nums[i]!=pre){
				groups.add(g1);
				g1 = new ArrayList<Integer>();
			}
			g1.add(nums[i]);
			pre = nums[i];
		}
		groups.add(g1);
		return gen(groups);
    }
}
