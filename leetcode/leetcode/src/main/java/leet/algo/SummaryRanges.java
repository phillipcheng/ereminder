package leet.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

class Interval {
	int start;
	int end;
	Interval() { start = 0; end = 0; }
	Interval(int s, int e) { start = s; end = e; }
	public String toString(){
		return String.format("%d-%d", start, end);
	}
}

public class SummaryRanges {
	TreeMap<Integer, Interval> tree = new TreeMap<Integer, Interval>();
	
	public SummaryRanges() {
    }
	
	public void addNum(int val) {
		if (tree.containsKey(val)) return;
		Integer l = tree.lowerKey(val);//the highest key less than val
		Integer h = tree.higherKey(val);//the lowest key higher than val
		if (l!=null && h!=null && tree.get(l).end==val-1 && tree.get(h).start==val+1){//merge with l and h
			tree.get(l).end = tree.get(h).end;
			tree.remove(h);
		}else if (l!=null && tree.get(l).end>=val-1){//merge with l
			tree.get(l).end = Math.max(tree.get(l).end, val);
		}else if (h!=null && tree.get(h).start<=val+1){//merge with h
			tree.get(h).start=Math.min(tree.get(h).start, val);
		}else{
			tree.put(val, new Interval(val, val));
		}
	}
    
    public List<Interval> getIntervals() {
    	List<Interval> li = new ArrayList<Interval>();
    	li.addAll(tree.values());
    	return li;
    }
    
    public static void main(String[] args){
    	SummaryRanges sr = new SummaryRanges();
    	List<Interval> li;
    	/*
    	sr.addNum(1);
    	sr.addNum(3);
    	sr.addNum(7);
    	li= sr.getIntervals(); System.out.println(li);
    	sr.addNum(2);
    	li= sr.getIntervals(); System.out.println(li);
    	sr.addNum(6);
    	li= sr.getIntervals(); System.out.println(li);*/
    	sr.addNum(1);
    	sr.addNum(9);
    	sr.addNum(2);
    	li= sr.getIntervals(); System.out.println(li);
    }
}
