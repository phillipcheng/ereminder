package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.GasStation;

import org.junit.Test;

public class TestGasStation {
	
	@Test
	public void test1(){
		GasStation g = new GasStation();
		assertTrue(-1==g.canCompleteCircuit(new int[]{4}, new int[]{5}));
	}
	
	@Test
	public void test2(){
		GasStation g = new GasStation();
		assertTrue(1==g.canCompleteCircuit(new int[]{4,3}, new int[]{5,2}));
	}
	
	@Test
	public void test3(){
		GasStation g = new GasStation();
		assertTrue(4==g.canCompleteCircuit(new int[]{4,4,4,4,10}, new int[]{5,5,5,5,2}));
	}

}
