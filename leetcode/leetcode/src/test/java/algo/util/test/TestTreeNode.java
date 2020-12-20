package algo.util.test;

import static org.junit.Assert.*;
import leet.algo.test.TestAddTwoNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;

public class TestTreeNode {
	private static Logger logger =  LogManager.getLogger(TestTreeNode.class);
	@Test
	public void test1() {
		String input;
		TreeNode r;
		String output;
		input="1,2,#";
		r = TreeNodeUtil.bfsFromString(input);
		output = TreeNodeUtil.bfsToString(r);
		System.out.println(input);
		System.out.println(output);
		
	}
	
	@Test
	public void test2() {
		TreeNode r;
		String output;
		
		String input="-2,1,#,8,#";
		r = TreeNodeUtil.bfsFromString(input);
		output = TreeNodeUtil.bfsToString(r);
		System.out.println(input);
		System.out.println(output);
		
	}
	
	@Test
	public void test3() {
		TreeNode r;
		String output;
		
		String input="-2,1,-1,8,5,7,4,9,#,3,#,0,6,5,2,#,#,#,#,3,#,7,#";
		r = TreeNodeUtil.bfsFromString(input);
		output = TreeNodeUtil.bfsToString(r);
		System.out.println(input);
		System.out.println(output);
		
	}
	
	@Test
	public void test4() {
		String input = "";//read properties from algo.util.test.TestTreeNode.properties
		TreeNode r;
		String output;
		
		r = TreeNodeUtil.bfsFromString(input);
		output = TreeNodeUtil.bfsToString(r);
		System.out.println(input);
		System.out.println(output);
		
	}
	
	@Test
	public void preOrderTest1(){
		String input = "1,2,3,#,#,4,#,#,5,#,6,#,#,";
		TreeNode tn = TreeNodeUtil.preOrderFromString(input);
		String output = TreeNodeUtil.preOrderToString(tn);
		logger.info(output);
		assertTrue(output.equals(input));
	}

}
