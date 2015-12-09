package org.cld.stock.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.StockBase;
import org.cld.stock.StockUtil;
import org.cld.stock.hk.HKStockBase;
import org.cld.stock.hk.HKStockConfig;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.nasdaq.NasdaqTestStockConfig;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SelectStrategyByStockTask;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.SellStrategyByStockMapper;
import org.cld.stock.strategy.SellStrategyByStockReducer;
import org.cld.stock.strategy.StockIdDateGroupingComparator;
import org.cld.stock.strategy.StockIdDatePair;
import org.cld.stock.strategy.StockIdDatePartitioner;
import org.cld.stock.strategy.StrategyConst;
import org.cld.stock.strategy.select.OverTrade;
import org.cld.stock.task.LoadDBDataTask;
import org.cld.stock.trade.BuySellResult;
import org.cld.stock.trade.TradeSimulator;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.JsonUtil;
import org.junit.Test;

public class TestStock {
	private static Logger logger =  LogManager.getLogger(TestStock.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Test
	public void testHKIds() throws Exception{
		HKStockBase hksb = new HKStockBase("client1-v2.properties", HKStockConfig.MarketId_HGT, null, sdf.parse("2015-10-11"));
		hksb.runAllCmd(null);
	}
	@Test
	public void testDateInHbase() throws Exception{
		NasdaqStockBase nsb = new NasdaqStockBase("cld-stock-cluster.properties", "ALL", null, sdf.parse("2015-10-11"));
		Map<String, Date> stockLUMap = StockPersistMgr.getStockLUDateByCmd(nsb.getStockConfig(), "nasdaq-quote-fq-historical", nsb.getCconf().getBigdbconf());
		String stockid="BABA";
		Date sd = null;
		if (stockLUMap.containsKey(stockid)){
			sd = stockLUMap.get(stockid);//date in the db is loaded from text files which are displayed on webpage which is in each market's tz
			if (sd!=null){
				String strSd = nsb.getStockConfig().getSdf().format(sd);
				logger.info(String.format("last update date for stock:%s is %s in tz:%s", stockid, strSd, nsb.getStockConfig().getSdf().getTimeZone()));
			}
		}
	}
	
	@Test
	public void testTZ() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("PST"));//my working timezone
		Date d = sdf.parse("2015-09-24");
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");//nasdaq timezone
		sdf2.setTimeZone(TimeZone.getTimeZone("EST"));
		logger.info(sdf2.format(d));
		
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");//hs_a timezone
		sdf3.setTimeZone(TimeZone.getTimeZone("CTT"));
		logger.info(sdf3.format(d));
	}
	
	@Test
	public void testOpenDay() throws Exception{
		Date d = sdf.parse("2015-09-30");
		//Date d = sdf.parse("2015-09-30");
		Date nd = StockUtil.getNextOpenDay(d, SinaStockConfig.CNHolidays);
		logger.info(String.format("nd is %s", sdf.format(nd)));
		assertTrue("2015-10-08".equals(sdf.format(nd)));
		
		d = sdf.parse("2015-12-31");
		nd = StockUtil.getNextOpenDay(d, SinaStockConfig.CNHolidays);
		logger.info(String.format("nd is %s", sdf.format(nd)));
		assertTrue("2016-01-04".equals(sdf.format(nd)));
		
		Date fd = sdf.parse("2015-09-30");
		Date td = sdf.parse("2016-01-05");
		
		List<Date> dl = StockUtil.getOpenDayList(fd, td, SinaStockConfig.CNHolidays);
		logger.info(String.format("dl is %s", dl));
		
		fd = sdf.parse("2004-10-01");
		td = sdf.parse("2015-09-26");
		dl = StockUtil.getOpenDayList(fd, td, SinaStockConfig.CNHolidays);
		logger.info(String.format("dl is %s", dl));
	}
	
	@Test
	public void testLoadDB() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		LoadDBDataTask.launch("sina", "hs_a", cconf.getSmalldbconf(), 5, "C:\\mydoc\\mydata\\stock\\merge\\sina-stock-stock-structure", 
				new String[]{}, new String[]{});
	}
	
	@Test
	public void testBuySell() throws Exception {
		String pFile = "client1-v2.properties";
		Date startDate = sdf.parse("2015-09-01");
		Date endDate = sdf.parse("2015-10-01");
		String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test;
		String baseMarketId = "nasdaq";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String sn = "macd";
		StockUtil.validateAllStrategyByStock(pFile, cconf, baseMarketId, marketId, startDate, endDate, sn, null);
	}
	
	@Test
	public void testBuyOvertrade() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		Date startDate = sdf.parse("2011-04-06");
		Date endDate = sdf.parse("2012-04-07");
		String sn = "strategy.overtradeone.properties";
		List<SelectStrategy> ssl = SelectStrategy.gen(new PropertiesConfiguration(sn), sn, "nasdaq");
		SelectStrategy[] ssa = new SelectStrategy[ssl.size()];
		ssl.toArray(ssa);
		List<Object[]> kvl = SelectStrategyByStockTask.getKVL(cconf, ssa, "AAPL", startDate, endDate, null);
		for (Object[] kv:kvl){
			SelectCandidateResult scr = (SelectCandidateResult) kv[0];
			SelectStrategy bs = (SelectStrategy) kv[1];
			logger.info(String.format("scr:%s, bs:%s", scr, bs.paramsToString()));
		}
	}
	
	@Test
	public void testBuyMACD() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		Date startDate = sdf.parse("2011-09-06");
		Date endDate = sdf.parse("2011-09-08");
		String sn = "strategy.macd.properties";
		List<SelectStrategy> ssl = SelectStrategy.gen(new PropertiesConfiguration(sn), sn, "nasdaq");
		SelectStrategy[] ssa = new SelectStrategy[ssl.size()];
		ssl.toArray(ssa);
		List<Object[]> kvl = SelectStrategyByStockTask.getKVL(cconf, ssa, "AAPL", startDate, endDate, null);
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
		List<SelectStrategy> ssl = SelectStrategy.gen(new PropertiesConfiguration(sn), sn, "nasdaq");
		SelectStrategy[] ssa = new SelectStrategy[ssl.size()];
		ssl.toArray(ssa);
		List<Object[]> kvl = SelectStrategyByStockTask.getKVL(cconf, ssa, "AAPL", startDate, endDate, null);
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
		String[] stockids=new String[]{"CETC"};
		SellStrategy ss = new SellStrategy(1, StrategyConst.V_UNIT_DAY, 3, 9f, 1f, true);
		for (String stockid:stockids){
			SelectCandidateResult scr = new SelectCandidateResult(stockid, 
					nsb.getStockConfig().getNormalTradeStartTime(sdf.parse("2011-04-26")), 0, 9.95f*1.005f);
			BuySellResult bsr = TradeSimulator.trade(scr, ss, nsb.getStockConfig(), cconf);
			logger.info(bsr);
		}
	}
}
