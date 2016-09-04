package leet.algo.test;

import java.util.List;

import leet.algo.AdditiveNumber;
import leet.algo.PathSumII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestPathSumII {
	private static Logger logger =  LogManager.getLogger(AdditiveNumber.class);
	@Test
	public void test1(){
		PathSumII ps = new PathSumII();
		TreeNode tn = TreeNodeUtil.bfsFromString("1,#,#");
		List<List<Integer>> ll = ps.pathSum(tn, 1);
		logger.info(ll);
	}
	
	@Test
	public void test2(){
		PathSumII ps = new PathSumII();
		TreeNode tn = TreeNodeUtil.bfsFromString("1,2,#");
		List<List<Integer>> ll = ps.pathSum(tn, 1);
		logger.info(ll);
	}
}
