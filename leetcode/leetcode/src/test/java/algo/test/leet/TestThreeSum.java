package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.ThreeSum;
import algo.leet.ThreeSumClosest;

public class TestThreeSum {

	@Test
	public void test() {
		int[] input = new int[]{-1, 0, 1, 2, -1, -4};
		ThreeSum ts = new ThreeSum();
		System.out.println(ts.threeSumONSquareLgN(input));
		ts = new ThreeSum();
		System.out.println(ts.threeSumONSquare(input));
	}
	
	@Test
	public void testThreeSum() {
		long start = System.nanoTime();
		int[] input = new int[]{7,-1,14,-12,-8,7,2,-15,8,8,-8,-14,-4,-5,7,9,11,-4,-15,-6,1,-14,4,3,10,-5,2,1,6,11,2,-2,-5,-7,-6,2,-15,11,-6,8,-4,2,1,-1,4,-6,-15,1,5,-15,10,14,9,-8,-6,4,-6,11,12,-15,7,-1,-9,9,-1,0,-4,-1,-12,-2,14,-9,7,0,-3,-4,1,-2,12,14,-10,0,5,14,-1,14,3,8,10,-8,8,-5,-2,6,-11,12,13,-7,-12,8,6,-13,14,-2,-5,-11,1,3,-6};
		ThreeSum ts = new ThreeSum();
		System.out.println(ts.threeSumONSquareLgN(input));

		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
		
		start = System.nanoTime();
		ts = new ThreeSum();
		System.out.println(ts.threeSumONSquare(input));
		end = System.nanoTime();
		System.out.println("time:" + (end-start));
	}

	@Test
	public void testThreeSumClosest() {
		long start = System.nanoTime();
		int[] input = new int[]{-1, 2, 1, -4};
		int target = 1;
		int result = new ThreeSumClosest().threeSumClosest(input,target);
		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
		assertTrue(result==2);
	}
	
	@Test
	public void testThreeSumClosest1() {
		long start = System.nanoTime();
		int[] input = new int[]{1,2,4,8,16,32,64,128};
		int target = 82;
		int result = new ThreeSumClosest().threeSumClosest(input,target);
		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
		assertTrue(result==82);
	}
	
	@Test
	public void testThreeSumClosest2() {
		long start = System.nanoTime();
		int[] input = new int[]{0,-4,1,-5};
		int target = 0;
		int result = new ThreeSumClosest().threeSumClosest(input,target);
		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
		assertTrue(result==-3);
	}
	
	@Test
	public void testThreeSumClosest3() {
		long start = System.nanoTime();
		int[] input = new int[]{13,2,0,-14,-20,19,8,-5,-13,-3,20,15,20,5,13,14,-17,-7,12,-6,0,20,-19,-1,-15,-2,8,-2,-9,13,0,-3,-18,-9,-9,-19,17,-14,-19,-4,-16,2,0,9,5,-7,-4,20,18,9,0,12,-1,10,-17,-11,16,-13,-14,-3,0,2,-18,2,8,20,-15,3,-13,-12,-2,-19,11,11,-10,1,1,-10,-2,12,0,17,-19,-7,8,-19,-17,5,-5,-10,8,0,-12,4,19,2,0,12,14,-9,15,7,0,-16,-5,16,-12,0,2,-16,14,18,12,13,5,0,5,6};
		int target = -59;
		int result = new ThreeSumClosest().threeSumClosest(input,target);
		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
		assertTrue(result==-58);
	}
	
	@Test
	public void testThreeSumClosest4() {
		long start = System.nanoTime();
		int[] input = new int[]{87,6,-100,-19,10,-8,-58,56,14,-1,-42,-45,-17,10,20,-4,13,-17,0,11,-44,65,74,-48,30,-91,13,-53,76,-69,-19,-69,16,78,-56,27,41,67,-79,-2,30,-13,-60,39,95,64,-12,45,-52,45,-44,73,97,100,-19,-16,-26,58,-61,53,70,1,-83,11,-35,-7,61,30,17,98,29,52,75,-73,-73,-23,-75,91,3,-57,91,50,42,74,-7,62,17,-91,55,94,-21,-36,73,19,-61,-82,73,1,-10,-40,11,54,-81,20,40,-29,96,89,57,10,-16,-34,-56,69,76,49,76,82,80,58,-47,12,17,77,-75,-24,11,-45,60,65,55,-89,49,-19,4};
		int target = -275;
		int result = new ThreeSumClosest().threeSumClosest(input,target);
		long end = System.nanoTime();
		System.out.println("time:" + (end-start));
		assertTrue(result==-274);
	}

}
