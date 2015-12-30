package org.cld.trade.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.trade.AutoTrader;
import org.cld.trade.HistoryDumpMgr;
import org.cld.trade.TradeDataMgr;
import org.cld.trade.TradeSimulatorConnector;
import org.cld.trade.evt.MarketOpenCloseTrdMsg;
import org.cld.trade.evt.MarketStatusType;
import org.junit.Test;

public class TestAutoTrader {
	private static Logger logger =  LogManager.getLogger(TestAutoTrader.class);
	@Test
	public void test1() throws Exception {
		AutoTrader at = new AutoTrader();
		StockConfig sc = StockUtil.getStockConfig(at.getBaseMarketId());
		TradeDataMgr tdm = new TradeDataMgr(at, sc);
		
		TradeSimulatorConnector tradeApi = new TradeSimulatorConnector("C:\\mydoc\\myprojects\\ereminder\\Reminder\\modules\\trade\\input", tdm);
		at.setTm(tradeApi);
		at.initEngine();
		new Thread(at).start();
		
		HistoryDumpMgr hdm = new HistoryDumpMgr(at.getHistoryDumpProperties(), tdm, sc);
		new Thread(hdm).start();
		new Thread(tradeApi).start();
		Thread.sleep(3000000);//30 seconds
	}
	
	@Test
	public void testMarketStatus(){
		MarketStatusType mst = AutoTrader.getMarketStatus();
		logger.info(mst);
	}
	
	@Test
	public void testStreamQuerySwitch() throws Exception{
		AutoTrader at = new AutoTrader();
		at.startStreamMgr(MarketStatusType.Regular);
		Thread.sleep(5000);
		at.startStreamMgr(MarketStatusType.After);
		Thread.sleep(5000);
		at.startStreamMgr(MarketStatusType.Close);
		Thread.sleep(5000);
		at.startStreamMgr(MarketStatusType.Pre);
		Thread.sleep(5000);
		at.startStreamMgr(MarketStatusType.Regular);
		Thread.sleep(5000);
	}
	
	@Test
	public void testCrawlUpdate(){
		AutoTrader at = new AutoTrader();
		MarketOpenCloseTrdMsg.crawlUpdate(at);
	}
}
