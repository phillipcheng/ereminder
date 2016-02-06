package algo.test.leet;

import static org.junit.Assert.*;

import org.junit.Test;

import algo.leet.ContainerWithMostWater;
import algo.util.StringUtil;

public class TestContainerWithMostWater {

	@Test
	public void test() {
		long start, end;
		start = System.nanoTime();
		int[] height=StringUtil.genIntArray(1, 1, 15000);
		ContainerWithMostWater cwmw = new ContainerWithMostWater();
		int area= cwmw.maxArea(height);
		end = System.nanoTime();
		System.out.println("time:" + (end-start) + ", area:" + area);
		
		
		start = System.nanoTime();
		area= cwmw.maxAreaNSquare(height);
		end = System.nanoTime();
		System.out.println("time:" + (end-start) + ", area:" + area);
	}
	
	@Test
	public void test1() {
		long start = System.nanoTime();
		int[] height=StringUtil.genReverseIntArray(1, 1, 15000);
		ContainerWithMostWater cwmw = new ContainerWithMostWater();
		int area= cwmw.maxArea(height);
		long end = System.nanoTime();
		System.out.println("time:" + (end-start) + ", area:" + area);
		
		start = System.nanoTime();
		area= cwmw.maxAreaNSquare(height);
		end = System.nanoTime();
		System.out.println("time:" + (end-start) + ", area:" + area);
	}

}
