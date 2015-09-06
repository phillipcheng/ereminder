package org.cld.stock.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.nasdaq.NasdaqTestStockConfig;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.junit.Before;
import org.junit.Test;

public class TestNasdaqStock {
	private static Logger logger =  LogManager.getLogger(TestNasdaqStock.class);
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static String START_DATE = "2014-11-01";
	private static String END_DATE = "2014-11-10";
	private static Date startDate=null;
	private static Date endDate = null;
	static{
		try{
			startDate = sdf.parse(START_DATE);
			endDate = sdf.parse(END_DATE);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test;
	private String propFile = "client1-v2.properties";
	
	private NasdaqStockBase nsb;

	public TestNasdaqStock(){
		super();
	}
	
	@Before
	public void setUp(){
		nsb = new NasdaqStockBase(propFile, marketId, startDate, endDate);
	}
	
	//
	@Test
	public void testInitTestMarket() throws Exception{
		nsb.getDsm().addUpdateCrawledItem(nsb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
	}
	
	@Test
	public void testBrowseIdlist_with_st() throws Exception{
		Date ed = sdf.parse("2015-08-02");
		nsb.run_browse_idlist(NasdaqStockConfig.MarketId_NASDAQ, ed);
	}
	
	@Test
	public void testCrawl_QuoteHistorical() throws InterruptedException {
		List<Task> tl = nsb.getCconf().setUpSite("nasdaq-quote-historical.xml", null);
		String startDate = "2014-01-01";
		String endDate = "2015-12-31";
		String stockId = "flws";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, nsb.getCconf());
		params.put("marketId", NasdaqStockConfig.MarketId_NASDAQ);
		params.put("stockid", stockId);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		List<CrawledItem> cil = tl.get(0).runMyselfWithOutput(params, false);
		logger.info(cil.get(0));
	}

}
