package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import algo.util.Interval;

//Given a collection of intervals, merge all overlapping intervals.
public class MergeIntervals {

	public class IntervalComparator implements Comparator<Interval>{
		public int compare(Interval o1, Interval o2) {
			return o1.start-o2.start;
		}
	}
	
	public boolean overlap(Interval a, Interval b){
		return a.end>=b.start;
	}
	
	public Interval merge(Interval a, Interval b){
		return new Interval(Math.min(a.start, b.start), Math.max(a.end, b.end));
	}
	
	public List<Interval> merge(List<Interval> intervals) {
		if (intervals.size()<=1) return intervals;
		List<Interval> output = new ArrayList<Interval>();
		Interval[] ia = new Interval[intervals.size()];
		intervals.toArray(ia);
		Arrays.sort(ia, new IntervalComparator());
		Interval a = ia[0];
		for (int i=1; i<ia.length; i++){
			Interval b = ia[i];
			if (overlap(a, b)){
				a = merge(a, b);
			}else{
				output.add(a);
				a = b;
			}
			if (i==ia.length-1){
				output.add(a);//add the last
			}
		}
        return output;
    }
}
