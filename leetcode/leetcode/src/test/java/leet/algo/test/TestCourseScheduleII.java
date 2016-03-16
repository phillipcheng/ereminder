package leet.algo.test;

import java.util.Arrays;

import leet.algo.CourseScheduleII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestCourseScheduleII {
	private static Logger logger =  LogManager.getLogger(TestCourseScheduleII.class);
	@Test
	public void test0(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(2, new int[][]{});
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test01(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(2, new int[][]{{}});
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test1(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(2, new int[][]{{1,0}});
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test11(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(2, new int[][]{{0,1}});
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test2(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(4, new int[][]{{1,0},{2,0},{3,1},{3,2}});
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test3(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(3, new int[][]{{1,0},{2,1},{0,2}});
		logger.info(Arrays.toString(ret));
	}
	
	@Test
	public void test4(){
		CourseScheduleII cs = new CourseScheduleII();
		int[] ret = cs.findOrder(3, new int[][]{{1,0}});
		logger.info(Arrays.toString(ret));
	}

}
