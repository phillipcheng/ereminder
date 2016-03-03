package leet.algo.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import algo.util.Interval;
import leet.algo.MergeIntervals;

public class TestMergeIntervals {
	
	@Test
	public void test1(){
		MergeIntervals mi = new MergeIntervals();
		List<Interval> li = Interval.fromString("[1,3],[2,6],[8,10],[15,18]");
		List<Interval> output = mi.merge(li);
		assertTrue(Interval.toString(output).equals("[1,6],[8,10],[15,18]"));
	}
	
	@Test
	public void test2(){
		MergeIntervals mi = new MergeIntervals();
		List<Interval> li = Interval.fromString("[1,4],[1,4]");
		List<Interval> output = mi.merge(li);
		assertTrue(Interval.toString(output).equals("[1,4]"));
	}

}
