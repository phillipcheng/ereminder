package leet.algo.test;

import static org.junit.Assert.*;

import org.junit.Test;

import leet.algo.CoinChange;

public class TestCoinChange {
	
	@Test
	public void test1(){
		CoinChange cc = new CoinChange();
		assertTrue(3 == cc.coinChange(new int[]{1,2,5}, 11));
	}
	
	@Test
	public void test2(){
		CoinChange cc = new CoinChange();
		assertTrue(-1 == cc.coinChange(new int[]{2}, 3));
	}
	
	@Test
	public void test3(){
		CoinChange cc = new CoinChange();
		assertTrue(2 == cc.coinChange(new int[]{1,3,4}, 6));
	}
	
	@Test
	public void test4(){
		CoinChange cc = new CoinChange();
		assertTrue( 1== cc.coinChange(new int[]{288,160,10,249,40,77,314,429}, 9208));
	}


}
