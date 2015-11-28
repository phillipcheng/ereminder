package org.cld.stock.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
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
import org.cld.stock.StockConfig;
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
import org.cld.stock.strategy.select.OpenCloseDropAvgD;
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
	public void testSelectCloseDropAvg() throws Exception {
		String pFile = "client1-v2.properties";
		String startDate = "2014-04-01";
		String endDate = "2015-11-10";
		String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test;
		String baseMarketId = "nasdaq";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String sn = "closedropavgone";
		List<SelectStrategy> bsl = SelectStrategy.gen(new PropertiesConfiguration("strategy."+sn+".properties"), sn);
		SelectStrategy bs = bsl.get(0);
		bs.setBaseMarketId(baseMarketId);
		bs.init();
		String folderName = String.format("%s_%s_%s", marketId, startDate, endDate);
		String outputDir1 = String.format("/reminder/sresult/%s/all1", folderName);
		String outputDir2 = String.format("/reminder/sresult/%s/all2", folderName);
		SelectStrategyByStockTask.launch(pFile, cconf, baseMarketId, marketId, outputDir1, 
				new SelectStrategy[]{bs}, sdf.parse(startDate), sdf.parse(endDate));
		Map<String, Object> sssMap = new HashMap<String, Object>();
		SellStrategy[] slsl = SellStrategy.gen(new PropertiesConfiguration("strategy."+sn+".properties"));
		sssMap.put(sn, slsl);
		String sssString = JsonUtil.ObjToJson(sssMap);
		Map<String, String> hadoopParams = new HashMap<String, String>();
		hadoopParams.put(SellStrategy.KEY_SELL_STRATEGYS, sssString);
		hadoopParams.put(CrawlUtil.CRAWL_PROPERTIES, pFile);
		hadoopParams.put(StockBase.KEY_BASE_MARKET_ID, baseMarketId);
		HadoopTaskLauncher.executeTasks(cconf.getNodeConf(), hadoopParams, new String[]{outputDir1}, false, outputDir2, true, 
				SellStrategyByStockMapper.class, SellStrategyByStockReducer.class, false);
	}
	
	@Test
	public void testTradeSimulator() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String marketId = NasdaqStockConfig.MarketId_ALL; //not used
		StockBase nsb = new NasdaqStockBase(pFile, marketId, null, null);
		String[] stockids=new String[]{"TUES"};
		SellStrategy ss = new SellStrategy(1, 1, SellStrategy.HU_DAY, 2f, 1f, true);
		for (String stockid:stockids){
			SelectCandidateResult scr = new SelectCandidateResult(stockid, nsb.getStockConfig().getNormalTradeStartTime(sdf.parse("2015-08-21")), 0, 5.98f);
			BuySellResult bsr = TradeSimulator.trade(scr, ss, nsb.getStockConfig(), cconf);
			logger.info(bsr);
		}
		//using daily data
		//2015-10-29, AIF, 2015-10-29, 14.67, 2015-11-04, MarktOnClose, 14.78, 0.007498272
		//2015-10-29, GOOG, 2015-10-29, 710.5, 2015-11-02, stoptrailingpercentage, 707.23, -0.00460242
		//2015-10-29, AAPL, 2015-10-29, 118.7, 2015-11-03, limit, 121.667496, 0.024999991
		
		//5 min data, no trailing
		//2015-10-29 09:30, AIF, 2015-10-29 09:30, 14.557, 2015-11-05 09:30, MarktOnClose, 14.711, 0.010579122
		//2015-10-29 09:30, GOOG, 2015-10-29 09:30, 711.23, 2015-11-04 00:05, limit, 729.01074, 0.025000017
		//2015-10-29 09:30, AAPL, 2015-10-29 09:30, 118.19, 2015-11-02 15:25, limit, 121.14475, 0.025


		//5 minute trailing
		//2015-10-29 09:30, AIF, 2015-10-29 09:30, 14.557, 2015-11-05 09:30, MarktOnClose, 14.711, 0.010579122
		//2015-10-29 09:30, GOOG, 2015-10-29 09:30, 711.23, 2015-11-02 09:40, stoptrailingpercentage, 707.48615, -0.0052638887
		//2015-10-29 09:30, AAPL, 2015-10-29 09:30, 118.19, 2015-10-30 15:00, stoptrailingpercentage, 119.017555, 0.0070018847

	}
	
	@Test
	public void testTradeSimulator1() throws Exception{
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String marketId = NasdaqStockConfig.MarketId_ALL; //not used
		StockBase nsb = new NasdaqStockBase(pFile, marketId, null, null);
		String[] stockids=new String[]{"XGTI"};
		SellStrategy ss = new SellStrategy(1, 5, SellStrategy.HU_DAY, 2.5f, 1.5f, true);
		for (String stockid:stockids){
			SelectCandidateResult scr = new SelectCandidateResult(stockid, nsb.getStockConfig().getNormalTradeStartTime(sdf.parse("2015-08-14")), 0, 0);
			BuySellResult bsr = TradeSimulator.trade(scr, ss, nsb.getStockConfig(), cconf);
			logger.info(bsr);
		}
	}
	
	@Test
	public void testGenCloseDropAvgForDay(){
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test; //not used
		StockBase nsb = new NasdaqStockBase(pFile, marketId, null, null);
		nsb.runSpecial("genCloseDropAvg", "2015-11-05");
		//expectd output
		//AAPL,2015-11-05,122.570,2
		//GOOG,2015-11-05,731.250,0
	}
	
	@Test
	public void testCloseDropAvgWithParams() throws Exception {
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test; //not used
		StockBase nsb = new NasdaqStockBase(pFile, marketId, sdf.parse("2015-01-01"), sdf.parse("2015-11-07"));
		nsb.runSpecial("validateAllStrategyByStock", "closedropavg");
	}
	
	@Test
	public void testGenCloseDropAvgSelect() throws Exception {
		String pFile = "cld-stock-cluster.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String baseMarketId="nasdaq";
		StockConfig sc = StockUtil.getStockConfig(baseMarketId);
		String marketId="ALL";
		OpenCloseDropAvgD bs = new OpenCloseDropAvgD();
		bs.setOrderDirection(SelectStrategy.DESC);
		bs.setParams(new Object[]{7f,0f});
		Date startDay= sdf.parse("2015-10-03");
		Date endDay = sdf.parse("2015-11-07");
		Date day = startDay;
		while (day.before(endDay)){
			List<SelectCandidateResult> sl = bs.selectByCurrent(cconf, baseMarketId, marketId, day, 10, null);
			logger.info(String.format("%s,%s", sdf.format(day), sl));
			day = StockUtil.getNextOpenDay(day, sc.getHolidays());
		}
	}
}
