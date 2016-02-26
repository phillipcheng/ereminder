package stanford.algo2.test.week3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import stanford.algo2.week3.Knapsack;

public class TestKnapsack {
	private static Logger logger =  LogManager.getLogger(TestKnapsack.class);
	@Test
	public void test1(){
		Knapsack ks = Knapsack.createKnapsack(TestKnapsack.class.getResourceAsStream("knapsack1.txt"));
		long m = ks.maxValueDP();
		logger.info(String.format("max value:%d", m));
	}

	@Test
	public void test2(){
		Knapsack ks = Knapsack.createKnapsack(TestKnapsack.class.getResourceAsStream("knapsack1.txt"));
		long m = ks.maxValueRecursion();
		logger.info(String.format("max value:%d", m));
	}
	
	@Test
	public void test3(){
		Knapsack ks = Knapsack.createKnapsack(TestKnapsack.class.getResourceAsStream("knapsack_big.txt"));
		long m = ks.maxValueRecursion();
		logger.info(String.format("max value:%d", m));
	}
}
