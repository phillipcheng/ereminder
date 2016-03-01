package leet.algo.test;

import java.util.Arrays;

import leet.algo.RemoveDupSortedArray;
import leet.algo.RemoveDupSortedArrayII;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class TestRemoveDupSortedArray {
	
	private static Logger logger =  LogManager.getLogger(TestRemoveDupSortedArray.class);
	
	@Test
	public void test1(){
		RemoveDupSortedArray rdsa = new RemoveDupSortedArray();
		int[] a = new int[]{1,1,2,2,3,3,3,4};
		int num = rdsa.removeDuplicates(a);
		logger.info(String.format("array:%s, num:%d", Arrays.toString(a), num));
	}
	
	@Test
	public void testA(){
		RemoveDupSortedArrayII rdsa = new RemoveDupSortedArrayII();
		int[] a = new int[]{1,1,2,2,2,3,3};
		int num = rdsa.removeDuplicates(a);
		logger.info(String.format("array:%s, num:%d", Arrays.toString(a), num));
	}
	
	@Test
	public void testB(){
		RemoveDupSortedArrayII rdsa = new RemoveDupSortedArrayII();
		int[] a = new int[]{1,1,1,1,1,1,2,2,2,3,4,4,4};
		int num = rdsa.removeDuplicates(a);
		logger.info(String.format("array:%s, num:%d", Arrays.toString(a), num));
	}

}
