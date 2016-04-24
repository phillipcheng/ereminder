package leet.algo.test;

import java.util.Arrays;

import leet.algo.CreateMaxNum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestCreateMaxNum {
	private static Logger logger =  LogManager.getLogger(TestCreateMaxNum.class);
	@Test
	public void test1(){
		CreateMaxNum cmn = new CreateMaxNum();
		int[] ret = cmn.maxNumber(new int[]{3,4,6,5}, new int[]{9,1,2,5,8,3}, 5);
		logger.info(Arrays.toString(ret));
	}

}
