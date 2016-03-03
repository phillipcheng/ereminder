package leet.algo.test;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.Interval;
import leet.algo.InsertInterval;
import leet.algo.MergeIntervals;

public class TestInsertIntervals {
	private static Logger logger =  LogManager.getLogger(TestInsertIntervals.class);
	
	@Test
	public void test1(){
		InsertInterval mi = new InsertInterval();
		List<Interval> li = Interval.fromString("[1,3],[6,9]");
		List<Interval> output = mi.insert(li, new Interval(2,5));
		assertTrue(Interval.toString(output).equals("[1,5],[6,9]"));
	}
	
	@Test
	public void test2(){
		InsertInterval mi = new InsertInterval();
		List<Interval> li = Interval.fromString("[1,2],[3,5],[6,7],[8,10],[12,16]");
		List<Interval> output = mi.insert(li, new Interval(4,9));
		logger.info(output);
		assertTrue(Interval.toString(output).equals("[1,2],[3,10],[12,16]"));
	}

}
