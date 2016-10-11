package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import leet.algo.test.TestAdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CombinationSumII {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	
	//return the sum list for elements from startIdx
	public Set<List<Integer>> combSum(int[] list, int startIdx, int target){
		//logger.info(String.format("startIdx:%d, target:%d", startIdx, target));
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		if (startIdx == list.length-1){
			if (list[startIdx]==target){
				List<Integer> ret1 = new ArrayList<Integer>();
				ret1.add(list[startIdx]);
				result.add(ret1);
			}else{//failed
			}
		}else{
			if (target>list[startIdx]){
				int nextStart = startIdx+1;
				result.addAll(combSum(list, nextStart, target));
				Set<List<Integer>> res1 = combSum(list, nextStart, target-list[startIdx]);
				if (res1.size()>0){
					for (List<Integer> eachRes:res1){
						eachRes.add(0, list[startIdx]);
					}
					result.addAll(res1);
				}
			}else if (target==list[startIdx]){
				List<Integer> ret1 = new ArrayList<Integer>();
				ret1.add(list[startIdx]);
				result.add(ret1);
			}else{//failed
			}
		}
		return result;
	}
	
	public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        Set<List<Integer>> ret = combSum(candidates, 0 , target);
        List<List<Integer>> ret1 = new ArrayList<List<Integer>>();
        ret1.addAll(ret);
        return ret1;
    }
}
