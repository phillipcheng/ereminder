package leet.algo.test;

import static org.junit.Assert.*;

import java.util.List;

import leet.algo.RemoveInvalidParentheses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestRemoveInvalidParentheses {
	private static Logger logger =  LogManager.getLogger(TestRemoveInvalidParentheses.class);
	@Test
	public void test1(){
		RemoveInvalidParentheses rip = new RemoveInvalidParentheses();
		List<String> ret = rip.removeInvalidParentheses("()())()");
		logger.info(ret);
		assertTrue(ret.size()==2);
		assertTrue(ret.contains("()()()"));
		assertTrue(ret.contains("(())()"));
	}
	
	@Test
	public void test2(){
		RemoveInvalidParentheses rip = new RemoveInvalidParentheses();
		List<String> ret = rip.removeInvalidParentheses("(a)())()");
		logger.info(ret);
		assertTrue(ret.size()==2);
		assertTrue(ret.contains("(a)()()"));
		assertTrue(ret.contains("(a())()"));
	}
	
	@Test
	public void test3(){
		RemoveInvalidParentheses rip = new RemoveInvalidParentheses();
		List<String> ret = rip.removeInvalidParentheses(")(");
		logger.info(ret);
		assertTrue(ret.size()==1);
		assertTrue(ret.contains(""));
	}
	
	@Test
	public void test4(){
		RemoveInvalidParentheses rip = new RemoveInvalidParentheses();
		List<String> ret = rip.removeInvalidParentheses(")(((()(y((u()(z()()");
		logger.info(ret);
	}
	
	@Test
	public void test5(){
		RemoveInvalidParentheses rip = new RemoveInvalidParentheses();
		List<String> ret = rip.removeInvalidParentheses(")((())))))()(((l((((");
		logger.info(ret);
	}
	
	

}
