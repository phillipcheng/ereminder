import static org.junit.Assert.*;

import org.junit.Test;


public class ParallelProgrammingSuite {

	@Test
	public void test1() {
		System.out.println(ParallelProgramming.toStr(ParallelProgramming.getSteps(3, 2)));
	}
	
	@Test
	public void test2() {
		System.out.println(ParallelProgramming.toStr(ParallelProgramming.getSteps(4, 2)));
	}
	
	@Test
	public void test3() {
		System.out.println(ParallelProgramming.toStr(ParallelProgramming.getSteps(11, 5)));
	}
	
	@Test
	public void test4() {
		System.out.println(ParallelProgramming.toStr(ParallelProgramming.getSteps(1, 4)));
	}
	
	@Test
	public void test5() {
		System.out.println(ParallelProgramming.toStr(ParallelProgramming.getSteps(2, 1)));
	}

}
