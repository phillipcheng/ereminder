package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static junit.framework.TestCase.assertTrue;

public class SummaryRanges {

	private static Logger logger =  LogManager.getLogger(SummaryRanges.class);

	class Interval {
		int start;
		int end;
		Interval(int s, int e) { start = s; end = e; }
		public String toString(){
			if (end!=start)
				return String.format("%d->%d", start, end);
			else
				return String.format("%d", start);
		}
	}

    public List<String> summaryRanges(int[] nums) {
		List<Interval> li = new ArrayList<>();
		List<String> ret = new ArrayList<>();
		Interval interval = null;
		for (int i=0; i<nums.length; i++){
			int val = nums[i];
			if (interval==null){//init
				interval = new Interval(val, val);
				li.add(interval);
			}else if (val == interval.end+1){//extend
				interval.end = val;
			}else{//create a new interval
				interval = new Interval(val, val);
				li.add(interval);
			}
		}
		for (Interval iv: li){
			ret.add(iv.toString());
		}
		return ret;
	}

	TreeMap<Integer, Interval> tree = new TreeMap<Integer, Interval>();//key is the start of the interval

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

	public void useNum(int[] val) {
		tree.clear();
		for (int i: val){
			addNum(i);
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
    	List<String> output;
    	String ret;

		output = sr.summaryRanges(new int[]{0,1,2,4,5,7});
		logger.info(output);
		ret = output.toString();
		assertTrue("[0->2, 4->5, 7]".equals(ret));

		output = sr.summaryRanges(new int[]{0,2,3,4,6,8,9});
		logger.info(output);
		ret = output.toString();
		assertTrue("[0, 2->4, 6, 8->9]".equals(ret));

		output = sr.summaryRanges(new int[]{});
		logger.info(output);
		ret = output.toString();
		assertTrue("[]".equals(ret));

		output = sr.summaryRanges(new int[]{-1});
		logger.info(output);
		ret = output.toString();
		assertTrue("[-1]".equals(ret));

		output = sr.summaryRanges(new int[]{0});
		logger.info(output);
		ret = output.toString();
		assertTrue("[0]".equals(ret));


		sr.useNum(new int[]{1, 3, 7});
    	li= sr.getIntervals();
    	logger.info(li);
		logger.info(sr.tree);
    	ret = li.toString();
		logger.info(ret);
		assertTrue("[1, 3, 7]".equals(ret));


		sr.useNum(new int[]{1, 2, 3, 7});
    	li= sr.getIntervals();
		logger.info(li);
		logger.info(sr.tree);
		ret = li.toString();
		assertTrue("[1->3, 7]".equals(ret));

		sr.useNum(new int[]{1, 2, 3, 6, 7});
    	li= sr.getIntervals();
		logger.info(li);
		logger.info(sr.tree);
		ret = li.toString();
		assertTrue("[1->3, 6->7]".equals(ret));

		sr.useNum(new int[]{1, 2, 3, 6, 7, 1, 9, 2});
    	li= sr.getIntervals();
		logger.info(li);
		logger.info(sr.tree);
		ret = li.toString();
		assertTrue("[1->3, 6->7, 9]".equals(ret));


	}
}
