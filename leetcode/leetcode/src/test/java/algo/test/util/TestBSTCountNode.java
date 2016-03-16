package algo.test.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.BSTCountNode;

public class TestBSTCountNode {
	private static Logger logger =  LogManager.getLogger(TestBSTCountNode.class);
	@Test
	public void test1(){
		BSTCountNode root = new BSTCountNode(1);
		logger.info(root.getSmaller(1));
		
		root.add(6);

		logger.info(root.getSmaller(6));
		root.add(2);
		

		logger.info(root.getSmaller(2));
		root.add(5);
		

		logger.info(root.getSmaller(5));
	}

}
