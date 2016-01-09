package org.cld.trade.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.strategy.SellStrategy;
import org.cld.trade.AutoTrader;
import org.cld.trade.HistoryDumpMgr;
import org.cld.trade.TradeDataMgr;
import org.cld.trade.TradeSimulatorConnector;
import org.cld.trade.evt.MarketOpenCloseTrdMsg;
import org.cld.trade.evt.MarketStatusType;
import org.junit.Test;

public class TestAutoTrader {
	private static Logger logger =  LogManager.getLogger(TestAutoTrader.class);
	
	//for FIT, given buy price 27.3, there will be buy,sell,buy,sell, followed by buy, buy, buy, buy (not filled)
	@Test
	public void testAutoTraderEngineNoStopSellOrder() throws Exception {
		AutoTrader at = new AutoTrader();
		at.setCurMst(MarketStatusType.Regular);
		StockConfig sc = StockUtil.getStockConfig(at.getBaseMarketId());
		TradeDataMgr tdm = new TradeDataMgr(at, sc);
		SellStrategy ss = at.getSs("FIT", "strategy.range.properties");
		ss.setStopPercentage(11);
		TradeSimulatorConnector tradeApi = new TradeSimulatorConnector("C:\\mydoc\\myprojects\\ereminder\\Reminder\\stock\\stocktrade\\input", tdm);
		at.setTm(tradeApi);
		at.initEngine();
		new Thread(at).start();
		
		new Thread(tradeApi).start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public void testAutoTraderEngineHasStopSellOrder() throws Exception {
		AutoTrader at = new AutoTrader();
		at.setCurMst(MarketStatusType.Regular);
		StockConfig sc = StockUtil.getStockConfig(at.getBaseMarketId());
		TradeDataMgr tdm = new TradeDataMgr(at, sc);
		SellStrategy ss = at.getSs("FIT", "strategy.range.properties");
		ss.setStopPercentage(2);
		TradeSimulatorConnector tradeApi = new TradeSimulatorConnector("C:\\mydoc\\myprojects\\ereminder\\Reminder\\stock\\stocktrade\\input", tdm);
		at.setTm(tradeApi);
		at.initEngine();
		new Thread(at).start();
		
		new Thread(tradeApi).start();
		Thread.sleep(Long.MAX_VALUE);
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
	public void testStreamSimulator() throws Exception{
		AutoTrader at = new AutoTrader();
		at.startStreamMgr(MarketStatusType.Pre);
		HistoryDumpMgr hdm = new HistoryDumpMgr(at.getHistoryDumpProperties(), at.getTradeDataMgr(), at.getSc());
        new Thread(hdm).start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public void testCrawlUpdate(){
		AutoTrader at = new AutoTrader();
		MarketOpenCloseTrdMsg.crawlUpdate(at);
	}
	
	@Test
	public void testQuartz() throws Exception{
		AutoTrader at = new AutoTrader();
		int hour=18;
		int min=54;
		
		String preMarketOpenCron = String.format("0 %s %s ? * 2-6", min, hour);
		String preMarketCloseCron = String.format("0 %s %s ? * 2-6", min+1, hour);
		String regularMarketOpenCron=String.format("0 %s %s ? * 2-6", min+2, hour);
		String regularMarketClosedCron = String.format("0 %s %s ? * 2-6", min+3, hour);
		String afterHourMarketOpenCron = String.format("0 %s %s ? * 2-6", min+4, hour);
		String afterHourMarketCloseCron = String.format("0 %s %s ? * 2-6", min+5, hour);
		at.setPreMarketOpenCron(preMarketOpenCron);
		at.setPreMarketCloseCron(preMarketCloseCron);
		at.setRegularMarketOpenCron(regularMarketOpenCron);
		at.setRegularMarketClosedCron(regularMarketClosedCron);
		at.setAfterHourMarketOpenCron(afterHourMarketOpenCron);
		at.setAfterHourMarketCloseCron(afterHourMarketCloseCron);
		
		at.initEngine();
		new Thread(at).start();
		//
		MarketStatusType mst = AutoTrader.getMarketStatus(at);
		logger.info(String.format("market status is %s", mst));
		at.startStreamMgr(mst);
        //
        HistoryDumpMgr hdm = new HistoryDumpMgr(at.getHistoryDumpProperties(), at.getTradeDataMgr(), at.getSc());
        new Thread(hdm).start();
        //
        AutoTrader.startScheduler(at);
        //
        while(true){
        	Thread.sleep(10000);
        }
	}
	
	@Test
	public void testMgmt() throws Exception {
		AutoTrader.main(null);
	}
}
