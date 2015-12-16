package org.cld.stock.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.common.TradeHour;
import org.cld.stock.config.NasdaqStockConfig;
import org.cld.stock.framework.SelectStrategyByStockTask;
import org.cld.stock.framework.StockBase;
import org.cld.stock.stockbase.ETLConfig;
import org.cld.stock.stockbase.NasdaqETLConfig;
import org.cld.stock.stockbase.NasdaqStockBase;
import org.cld.stock.strategy.BuySellResult;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyConst;
import org.cld.stock.task.LoadDBDataTask;
import org.cld.stock.trade.TradeSimulator;
import org.cld.util.JsonUtil;
import org.junit.Test;

public class TestStock {
	private static Logger logger =  LogManager.getLogger(TestStock.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Test
	public void testLoadDB() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		LoadDBDataTask.launch("sina", "hs_a", cconf.getSmalldbconf(), 5, "C:\\mydoc\\mydata\\stock\\merge\\sina-stock-stock-structure", 
				new String[]{}, new String[]{});
	}
	
	@Test
	public void testClone() throws Exception{
		String sn = "strategy.simple.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		SelectStrategy bs = ssl.get(0);
		logger.info(JsonUtil.ObjToJson(bs));
		SelectStrategy abs = (SelectStrategy) JsonUtil.deepClone(bs);
		logger.info(JsonUtil.ObjToJson(abs));
	}
	
	@Test
	public void testBuySell() throws Exception {
		String pFile = "client1-v2.properties";
		Date startDate = sdf.parse("2015-09-01");
		Date endDate = sdf.parse("2015-10-01");
		String marketId = NasdaqETLConfig.MarketId_NASDAQ_Test;
		String baseMarketId = "nasdaq";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String sn = "macd";
		StockBase.validateAllStrategyByStock(pFile, cconf, baseMarketId, marketId, startDate, endDate, sn, null, TradeHour.Normal);
	}
	
	@Test
	public void testBuyOvertrade() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		Date startDate = sdf.parse("2011-04-06");
		Date endDate = sdf.parse("2012-04-07");
		String sn = "strategy.overtradeone.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(cconf, ssl, "AAPL", startDate, endDate, TradeHour.Normal, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testBuySimple() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		Date startDate = msdf.parse("2014-08-14 09:40");
		Date endDate = msdf.parse("2014-08-14 16:00");
		String sn = "strategy.simple.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(cconf, ssl, "GPRO", startDate, endDate, TradeHour.Normal, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testBuyWShape() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		Date startDate = sdf.parse("2011-04-06");
		Date endDate = sdf.parse("2011-04-07");
		String sn = "strategy.wshapeone.properties";
		List<SelectStrategy> ssl = SelectStrategy.genList(new PropertiesConfiguration(sn), sn, "nasdaq");
		List<Object[]> kvl = SelectStrategyByStockTask.getBuyOppList(cconf, ssl, "AAPL", startDate, endDate, TradeHour.Normal, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testSell() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String marketId = NasdaqStockConfig.MarketId_ALL; //not used
		StockBase nsb = new NasdaqStockBase(pFile, marketId, null, null);
		StockConfig sc = StockUtil.getStockConfig(StockUtil.NASDAQ_STOCK_BASE);
		String[] stockids=new String[]{"CETC"};
		SellStrategy ss = new SellStrategy(1, StrategyConst.V_UNIT_DAY, 3, 9f, 1f, true);
		for (String stockid:stockids){
			SelectCandidateResult scr = new SelectCandidateResult(stockid, 
					sc.getNormalTradeStartTime(sdf.parse("2011-04-26")), 0, 9.95f*1.005f);
			BuySellResult bsr = TradeSimulator.trade(scr, ss, sc, cconf, TradeHour.Normal);
			logger.info(bsr);
		}
	}
}
