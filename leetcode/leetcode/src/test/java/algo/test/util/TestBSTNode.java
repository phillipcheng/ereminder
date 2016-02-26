package algo.test.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.BSTNode;

public class TestBSTNode {
	private static Logger logger =  LogManager.getLogger(TestBSTNode.class);
	@Test
	public void test1(){
		BSTNode root = new BSTNode(1);
		logger.info(root.getSmaller(1));
		
		root.add(6);

		logger.info(root.getSmaller(6));
		root.add(2);
		

		logger.info(root.getSmaller(2));
		root.add(5);
		

		logger.info(root.getSmaller(5));
	}

}
