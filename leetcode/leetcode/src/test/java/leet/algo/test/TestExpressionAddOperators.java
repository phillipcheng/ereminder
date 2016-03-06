package leet.algo.test;

import java.util.List;

import leet.algo.ExpressionAddOperator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestExpressionAddOperators {
	
	private static Logger logger =  LogManager.getLogger(TestExpressionAddOperators.class);
	
	@Test
	public void test0(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("", 5);
		logger.info(sl);
	}
	
	@Test
	public void test1(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("123", 6);
		logger.info(sl);
	}
	
	@Test
	public void test2(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("232", 8);
		logger.info(sl);
	}

	
	@Test
	public void test3(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("105", 5);
		logger.info(sl);
	}
	
	@Test
	public void test4(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("00", 0);
		logger.info(sl);
	}
	
	@Test
	public void test5(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("3456237490", 9191);
		logger.info(sl);
	}
	
	@Test
	public void test6(){
		ExpressionAddOperator eao = new ExpressionAddOperator();
		List<String> sl = eao.addOperators("11213", 2);
		logger.info(sl);
	}

}
