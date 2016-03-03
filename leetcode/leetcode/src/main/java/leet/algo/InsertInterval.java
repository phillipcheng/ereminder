package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import algo.util.Interval;

//Given a set of non-overlapping intervals, insert a new interval into the intervals (merge if necessary).
public class InsertInterval {
	
	public class IntervalComparator implements Comparator<Interval>{
		public int compare(Interval o1, Interval o2) {
			//o2 is the key
			int v = o2.start; //o2.start == o2.end
			if (v<=o1.end && v>=o1.start){
				return 0;
			}
			if (v>o1.end){
				return -1;
			}else{
				return 1;
			}
		}
	}
	
	public List<Interval> insert(List<Interval> intervals, Interval newInterval) {
		Interval si = new Interval(newInterval.start, newInterval.start);
		Interval ei = new Interval(newInterval.end, newInterval.end);
		IntervalComparator ic = new IntervalComparator();
		int sp = Collections.binarySearch(intervals, si, ic);
		int ep = Collections.binarySearch(intervals, ei, ic);
		
		
    }

}
