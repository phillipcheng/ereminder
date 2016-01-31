package cy.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cy.FourSum;
import cy.util.IntUtil;

public class TestFourSum {
	@Test
	public void test() {
		FourSum fs = new FourSum();
		int[] input = new int[]{1,0,-1,0,-2,2};
		ArrayList<ArrayList<Integer>> aal = fs.fourSum(input, 0);
		assertTrue(aal.size()==3);
		ArrayList<Integer> output = null;
		output = IntUtil.getIntArrayListFromString("-1,  0, 0, 1");
		aal.contains(output);
		output = IntUtil.getIntArrayListFromString("-2, -1, 1, 2");
		aal.contains(output);
		output = IntUtil.getIntArrayListFromString("-2,  0, 0, 2");
		aal.contains(output);
		
	}
	
	@Test
	public void test1() {
		FourSum fs = new FourSum();
		int[] input = new int[]{-3,-1,0,2,4,5};
		ArrayList<Integer> output = IntUtil.getIntArrayListFromString("[-3, -1, 0, 4]");
		ArrayList<ArrayList<Integer>> aal = fs.fourSum(input, 0);
		assertTrue(aal.size()==1);
		assertTrue(aal.contains(output));
	}
	
	@Test
	public void test2() {
		FourSum fs = new FourSum();
		int[] input = new int[]{-5,-4,-3,-2,-1,0,0,1,2,3,4,5};
		ArrayList<ArrayList<Integer>> aal = fs.fourSum(input, 0);
		
		ArrayList<Integer> output = null;
		String str = "[[-5,-4,4,5],[-5,-3,3,5],[-5,-2,2,5],[-5,-2,3,4],[-5,-1,1,5],[-5,-1,2,4],[-5,0,0,5],[-5,0,1,4],[-5,0,2,3],[-4,-3,2,5],[-4,-3,3,4],[-4,-2,1,5],[-4,-2,2,4],[-4,-1,0,5],[-4,-1,1,4],[-4,-1,2,3],[-4,0,0,4],[-4,0,1,3],[-3,-2,0,5],[-3,-2,1,4],[-3,-2,2,3],[-3,-1,0,4],[-3,-1,1,3],[-3,0,0,3],[-3,0,1,2],[-2,-1,0,3],[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]";
		String[] str1 = str.split("\\],\\[");
		System.out.println(aal);
		assertTrue(aal.size()==str1.length);
		for (int i=0; i<str1.length; i++){
			String str2=str1[i];
			output = IntUtil.getIntArrayListFromString(str2);
			assertTrue(aal.contains(output));
		}		
	}
}
