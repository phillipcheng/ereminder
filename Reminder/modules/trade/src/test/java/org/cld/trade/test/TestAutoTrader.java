package org.cld.trade.test;

import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.trade.AutoTrader;
import org.cld.trade.HistoryDumpMgr;
import org.cld.trade.TradeApi;
import org.cld.trade.TradeDataMgr;
import org.cld.trade.TradeSimulatorConnector;
import org.junit.Test;

public class TestAutoTrader {
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
}
