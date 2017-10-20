package algo.util.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.MaxHeap;


public class TestMaxHeap {
	private static Logger logger =  LogManager.getLogger(TestMaxHeap.class);
	
	@Test
	public void testConstruct(){
		int[] input=new int[]{1,4,3,4,5,6,7,11,5,14,17,101,0,9};
		int[] input2 = Arrays.copyOf(input, input.length);
		Arrays.sort(input2);
		MaxHeap mh = new MaxHeap(input);
		mh.heapSort();
		assertTrue(Arrays.equals(input, input2));
	}
}
