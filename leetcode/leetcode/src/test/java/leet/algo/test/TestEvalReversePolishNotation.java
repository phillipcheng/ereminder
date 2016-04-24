package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.EvalReversePolishNotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestEvalReversePolishNotation {
	private static Logger logger =  LogManager.getLogger(TestEvalReversePolishNotation.class);
	@Test
	public void test1(){
		EvalReversePolishNotation erpn = new EvalReversePolishNotation();
		int v = erpn.evalRPN(new String[]{"2", "1", "+", "3", "*"});
		logger.info(v);
		assertTrue(9==v);
	}
	
	@Test
	public void test2(){
		EvalReversePolishNotation erpn = new EvalReversePolishNotation();
		int v = erpn.evalRPN(new String[]{"4", "13", "5", "/", "+"});
		logger.info(v);
		assertTrue(6==v);
	}

}
