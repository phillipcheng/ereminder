package leet.algo;

import java.util.Comparator;
import java.util.PriorityQueue;

public class SlidingWindowMaximum {
	
	class GetMaxComparator implements Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2-o1;
		}
	}
	public int[] maxSlidingWindow(int[] nums, int k) {
		int n = nums.length;
		if (n==0) return new int[]{};
		int[] ret = new int[n+1-k];
		GetMaxComparator comp = new GetMaxComparator();
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(10, comp);
		for (int i=0; i<nums.length; i++){
			pq.add(nums[i]);
			if (i+1>=k){
				ret[i+1-k]=pq.peek();
				pq.remove(nums[i+1-k]);
			}
		}
        return ret;
    }
}
