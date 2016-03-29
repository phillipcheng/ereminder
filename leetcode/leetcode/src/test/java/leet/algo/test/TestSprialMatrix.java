package leet.algo.test;

import java.util.List;

import leet.algo.SpiralMatrix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestSprialMatrix {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	@Test
	public void test1(){
		SpiralMatrix sm = new SpiralMatrix();
		List<Integer> li = sm.spiralOrder(new int[][]{{1,2,3},{4,5,6},{7,8,9}});
		logger.info(li);
	}
	
	@Test
	public void test2(){
		SpiralMatrix sm = new SpiralMatrix();
		List<Integer> li = sm.spiralOrder(new int[][]{{1,2,3},{4,5,6}});
		logger.info(li);
	}
	
	@Test
	public void test3(){
		SpiralMatrix sm = new SpiralMatrix();
		List<Integer> li = sm.spiralOrder(new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12}});
		logger.info(li);
	}

	@Test
	public void test4(){
		SpiralMatrix sm = new SpiralMatrix();
		List<Integer> li = sm.spiralOrder(new int[][]{{6,9,7}});
		logger.info(li);
	}
}
