package org.cld.stock.test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.StockBase;
import org.cld.stock.StockUtil;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.sina.SinaStockBase;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.strategy.CompareSelectSuite;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.SellStrategy;
import org.cld.stock.strategy.StrategyValidationTask;
import org.junit.Test;

public class TestStock {
	private static Logger logger =  LogManager.getLogger(TestStock.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat mdssdf = new SimpleDateFormat("MMddhhmmss");
	
	@Test
	public void testDateInHbase() throws Exception{
		NasdaqStockBase nsb = new NasdaqStockBase("cld-stock-cluster.properties", "ALL", null, sdf.parse("2015-10-11"));
		Map<String, Date> stockLUMap = StockPersistMgr.getStockLUDateByCmd(nsb.getStockConfig(), "nasdaq-quote-fq-historical", nsb.getCconf());
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
	public void testStrategyValidation() throws Exception{
		String time = mdssdf.format(new Date());
		String outputDir = String.format("/reminder/sresult/test_%s", time);
		SelectStrategy scs = CompareSelectSuite.getHSAAllTimeLow(outputDir);
		SellStrategy sls = new SellStrategy(3, 5, 2);
		Date startDate = sdf.parse("2015-09-02");
		Date endDate = sdf.parse("2015-09-10");
		String pFile = "client1-v2.properties";
		CrawlConf cconf = CrawlTestUtil.getCConf(pFile);
		String marketBaseId ="sina";
		StrategyValidationTask.launch(pFile, cconf, marketBaseId, scs, sls, startDate, endDate, outputDir);
	}
	
	@Test
	public void testStrategyValidationFromFile() throws Exception{
		String pFile = "client1-v2.properties";
		String marketId = "hs_a"; //not used
		Date sd = sdf.parse("2015-09-02");
		Date ed = sdf.parse("2015-09-10");
		StockBase sb = new SinaStockBase(pFile, marketId, sd, ed);
		sb.validateStrategy("strategy1.properties");
	}
}
