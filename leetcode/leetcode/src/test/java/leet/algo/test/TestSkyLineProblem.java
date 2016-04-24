package leet.algo.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import leet.algo.SkyLineProblem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSkyLineProblem {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	
	@Test
	public void test0(){
		SkyLineProblem slp = new SkyLineProblem();
		List<int[]> output = slp.getSkyline(new int[][]{{15, 20, 10}, {19, 24, 8}});
		for (int[] xy: output){
			logger.info(Arrays.toString(xy));
		}
		//[15 10], [20 8], [24, 0]
	}
	
	@Test
	public void test1(){
		SkyLineProblem slp = new SkyLineProblem();
		List<int[]> output = slp.getSkyline(new int[][]{{2, 9, 10}, {3, 7, 15}, {5, 12, 12}, {15, 20, 10}, {19, 24, 8}});
		for (int[] xy: output){
			logger.info(Arrays.toString(xy));
		}
		//[2 10], [3 15], [7 12], [12 0], [15 10], [20 8], [24, 0]
	}
	
	@Test
	public void test2(){
		SkyLineProblem slp = new SkyLineProblem();
		List<int[]> output = slp.getSkyline(new int[][]{{1, 2, 1}, {1, 2, 2}, {1, 2, 3}});
		for (int[] xy: output){
			logger.info(Arrays.toString(xy));
		}
		//[1 3], [2, 0]
	}
	
	@Test
	public void test3(){
		SkyLineProblem slp = new SkyLineProblem();
		List<int[]> output = slp.getSkyline(new int[][]{{3, 7, 8}, {3, 8, 7}, {3, 9, 6}, {3, 10, 5}, {3, 11, 4}});
		for (int[] xy: output){
			logger.info(Arrays.toString(xy));
		}
		//[3 8], [7 7], [8 6], [9 5], [10 4], [11 0]
	}
	
	@Test
	public void test4(){
		SkyLineProblem slp = new SkyLineProblem();
		List<int[]> output = slp.getSkyline(new int[][]{{0, 5, 7}, {5, 10, 7}, {5, 10, 12}, {10, 15, 7}, 
				{15, 20, 7}, {15, 20, 12}, {20,25,7}});
		for (int[] xy: output){
			logger.info(Arrays.toString(xy));
		}
		//[0 7], [5 12], [10 7], [15 12], [20 7], [25 0]
	}

}
