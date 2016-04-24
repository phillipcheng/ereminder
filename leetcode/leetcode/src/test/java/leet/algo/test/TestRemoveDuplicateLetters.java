package leet.algo.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import leet.algo.RemoveDuplicateLetters;

public class TestRemoveDuplicateLetters {
	private static Logger logger =  LogManager.getLogger(TestRemoveDuplicateLetters.class);
	@Test
	public void test1(){
		RemoveDuplicateLetters rdl = new RemoveDuplicateLetters();
		String ret= rdl.removeDuplicateLetters("bcabc");
		logger.info(ret);
	}
	@Test
	public void test2(){
		RemoveDuplicateLetters rdl = new RemoveDuplicateLetters();
		String ret= rdl.removeDuplicateLetters("cbacdcbc");
		logger.info(ret);
	}

}
