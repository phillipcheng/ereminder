package leet.algo.test;

import java.util.List;

import leet.algo.MinimumHeightTrees;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestMinimumHeightTrees {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	@Test
	public void test1(){
		MinimumHeightTrees mht = new MinimumHeightTrees();
		List<Integer> li = mht.findMinHeightTrees(4, new int[][]{{1,0},{1,2},{1,3}});
		logger.info(li);
	}
	
	@Test
	public void test2(){
		MinimumHeightTrees mht = new MinimumHeightTrees();
		List<Integer> li = mht.findMinHeightTrees(6, new int[][]{{0,3},{1,3},{2,3},{4,3},{5,4}});
		logger.info(li);
	}
	
	@Test
	public void test3(){
		MinimumHeightTrees mht = new MinimumHeightTrees();
		List<Integer> li = mht.findMinHeightTrees(3, new int[][]{{0,1},{0,2}});
		logger.info(li);
	}

}
