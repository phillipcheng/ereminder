package leet.algo.test;

import static org.junit.Assert.*;
import leet.algo.BestTimeBuySellStockIV;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import algo.util.StringUtil;

public class TestBestBuySell {
	
	private static Logger logger =  LogManager.getLogger(TestBestBuySell.class);
	
	@Test
	public void test1(){
		BestTimeBuySellStockIV bt = new BestTimeBuySellStockIV();
		int[] p = StringUtil.readInts("buyandsale.txt");
		int max = bt.maxProfit(29, p);
		logger.info(max);
	}

}
