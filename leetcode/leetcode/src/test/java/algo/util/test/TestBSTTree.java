package algo.util.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.tree.BSTTree;

public class TestBSTTree {
	private static Logger logger =  LogManager.getLogger(TestBSTTree.class);
	@Test
	public void test1(){
		BSTTree tree = new BSTTree();
		tree.add(1);
		tree.add(2);
		tree.add(3);
		String str = tree.inOrderToString();
		logger.info(str);
	}
	
	@Test
	public void test2(){
		BSTTree tree = new BSTTree();
		tree.add(3);
		tree.add(4);
		tree.add(1);
		tree.add(2);
		String str = tree.inOrderToString();
		logger.info(str);
		tree.delete(3);
		str = tree.inOrderToString();
		logger.info(str);
	}
	
	@Test
	public void test3(){
		BSTTree tree = new BSTTree();
		tree.add(8);
		tree.add(3);
		tree.add(4);
		tree.add(1);
		tree.add(2);
		tree.add(6);
		tree.add(9);
		tree.add(11);
		tree.add(6);
		tree.add(12);
		String str = tree.inOrderToString();
		logger.info(str);
		tree.delete(9);
		str = tree.inOrderToString();
		logger.info(str);
		tree.delete(8);
		str = tree.inOrderToString();
		logger.info(str);
		tree.delete(3);
		str = tree.inOrderToString();
		logger.info(str);
	}

}
