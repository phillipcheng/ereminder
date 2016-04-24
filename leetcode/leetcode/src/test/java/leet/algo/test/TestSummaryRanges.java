package leet.algo.test;

import java.util.List;

import leet.algo.SummaryRanges;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSummaryRanges {
	private static Logger logger =  LogManager.getLogger(TestSummaryRanges.class);
	@Test
	public void test1(){
		SummaryRanges sr = new SummaryRanges();
		List<String> sl = sr.summaryRanges(new int[]{0,1,2,4,5,7});
		logger.info(sl);
	}
	
	@Test
	public void test2(){
		SummaryRanges sr = new SummaryRanges();
		List<String> sl = sr.summaryRanges(new int[]{-1});
		logger.info(sl);
	}
	
	@Test
	public void test3(){
		SummaryRanges sr = new SummaryRanges();
		List<String> sl = sr.summaryRanges(new int[]{0,2,4,6});
		logger.info(sl);
	}

}
