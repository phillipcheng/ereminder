package algo.string.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import algo.string.KMP;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KMPTest {
	private static Logger logger =  LogManager.getLogger(KMPTest.class);
	
	@Test
	public void kmpFF1(){
		int[] ff = KMP.failureFunction("aaa");
		logger.info(Arrays.toString(ff));
		int[] res = new int[]{0,1,2};
		assertTrue(Arrays.equals(ff,  res));
	}
	
	@Test
	public void kmpFF2(){
		int[] ff = KMP.failureFunction("aabaa");
		logger.info(Arrays.toString(ff));
		int[] res = new int[]{0,1,0,1,2};
		assertTrue(Arrays.equals(ff,  res));
	}
	
	@Test
	public void kmp0(){
		List<Integer> li = KMP.search("aabaaaabaa", "aabaa");
		logger.info(li);
		List<Integer> res = new ArrayList<Integer>();
		res.add(0);
		res.add(5);
		assertTrue(res.toString().equals(li.toString()));
	}
	
	@Test
	public void kmp1(){
		List<Integer> li = KMP.search("aaabaaabbaaaabaabbb", "aabaa");
		logger.info(li);
		List<Integer> res = new ArrayList<Integer>();
		res.add(1);
		res.add(11);
		assertTrue(res.toString().equals(li.toString()));
	}
}
