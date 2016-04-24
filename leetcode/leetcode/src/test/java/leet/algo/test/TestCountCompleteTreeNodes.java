package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.CountCompleteTreeNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestCountCompleteTreeNodes {
	private static Logger logger =  LogManager.getLogger(TestCountCompleteTreeNodes.class);
	@Test
	public void test1(){
		CountCompleteTreeNodes cctn = new CountCompleteTreeNodes();
		TreeNode r = TreeNodeUtil.bfsFromString("1,2,3,4,5,6,#");
		int cnt = cctn.countNodes(r);
		logger.info(cnt);
		assertTrue(6==cnt);
	}
	
	@Test
	public void test3(){
		CountCompleteTreeNodes cctn = new CountCompleteTreeNodes();
		TreeNode r = TreeNodeUtil.bfsFromString("1,2,3,4,5,#,#");
		int cnt = cctn.countNodes(r);
		logger.info(cnt);
		assertTrue(5==cnt);
	}
	
	@Test
	public void test4(){
		CountCompleteTreeNodes cctn = new CountCompleteTreeNodes();
		TreeNode r = TreeNodeUtil.bfsFromString("1,2,3,4,#,#,#");
		int cnt = cctn.countNodes(r);
		logger.info(cnt);
		assertTrue(4==cnt);
	}
	
	@Test
	public void test5(){
		CountCompleteTreeNodes cctn = new CountCompleteTreeNodes();
		TreeNode r = TreeNodeUtil.bfsFromString("1,2,3");
		int cnt = cctn.countNodes(r);
		logger.info(cnt);
		assertTrue(3==cnt);
	}
	
	@Test
	public void test6(){
		CountCompleteTreeNodes cctn = new CountCompleteTreeNodes();
		TreeNode r = TreeNodeUtil.bfsFromString("1");
		int cnt = cctn.countNodes(r);
		logger.info(cnt);
		assertTrue(1==cnt);
	}
	
	@Test
	public void test2(){
		CountCompleteTreeNodes cctn = new CountCompleteTreeNodes();
		TreeNode r = TreeNodeUtil.bfsFromString("1,2,#");
		int cnt = cctn.countNodes(r);
		logger.info(cnt);
		assertTrue(2==cnt);
	}

}
