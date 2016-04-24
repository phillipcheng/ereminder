package leet.algo.test;

import leet.algo.PermutationSequence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestPermutationSequence {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		PermutationSequence ps = new PermutationSequence();
		String ret = ps.getPermutation(3, 5);
		logger.info(ret);
	}
	
	@Test
	public void test2(){
		PermutationSequence ps = new PermutationSequence();
		String ret = ps.getPermutation(3, 6);
		logger.info(ret);
	}
	
	@Test
	public void test3(){
		PermutationSequence ps = new PermutationSequence();
		String ret = ps.getPermutation(4, 6);
		logger.info(ret);
	}
}
