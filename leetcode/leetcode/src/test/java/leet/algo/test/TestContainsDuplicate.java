package leet.algo.test;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.StringUtil;
import leet.algo.ContainsDuplicateIII;

public class TestContainsDuplicate {
	private static Logger logger =  LogManager.getLogger(TestContainsDuplicate.class);
	@Test
	public void test0(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		assertFalse(cd.containsNearbyAlmostDuplicate(new int[]{0}, 0, 0));
	}
	
	@Test
	public void test1(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		assertFalse(cd.containsNearbyAlmostDuplicate(new int[]{1,2}, 0, 1));
	}
	
	@Test
	public void test2(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		assertTrue(cd.containsNearbyAlmostDuplicate(new int[]{1,3,1}, 2, 1));
	}
	
	@Test
	public void test3(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		assertFalse(cd.containsNearbyAlmostDuplicate(new int[]{-3,3}, 2, 4));
	}
	
	@Test
	public void test4(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		assertFalse(cd.containsNearbyAlmostDuplicate(new int[]{4,2}, 2, 1));
	}
	@Test
	public void test5(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		assertFalse(cd.containsNearbyAlmostDuplicate(new int[]{1,3,1}, 1, 1));
	}
	
	@Test
	public void test6(){
		ContainsDuplicateIII cd = new ContainsDuplicateIII();
		int[] input=StringUtil.readInts("ContainDuplicates.txt");
		assertFalse(cd.containsNearbyAlmostDuplicate(input, 10000, 0));
	}
	
}
