package leet.algo;

import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainsDuplicateIII {
	private static Logger logger =  LogManager.getLogger(ContainsDuplicateIII.class);
	public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
		TreeSet<Integer> tree = new TreeSet<Integer>();
		for (int i=0; i<nums.length; i++){
			int v = nums[i];
			Integer low = tree.ceiling(v-t);
			Integer high = tree.floor(v+t);
			if (low!=null && low<=v || high!=null && high>=v){
				return true;
			}
			tree.add(v);
			if (i>=k){
				tree.remove(nums[i-k]);
			}
		}
		return false;
    }

}
