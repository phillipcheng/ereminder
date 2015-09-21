package org.cld.stock.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.StockUtil;
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
	public void setUp(){nsb = new NasdaqStockBase(propFile, marketId, startDate, endDate);}
	
	@Test
	public void testInitTestMarket() throws Exception{nsb.getDsm().addUpdateCrawledItem(nsb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);}
	
	@Test
	public void testRunAllCmd1() throws Exception{nsb.runAllCmd(NasdaqTestStockConfig.date_Test_SD, NasdaqTestStockConfig.date_Test_END_D2);}
	
	@Test
	public void testBrowseIdlist() throws Exception{nsb.run_browse_idlist(NasdaqStockConfig.MarketId_NASDAQ, sdf.parse("2015-08-02"));}
	
	@Test
	public void testCmd_QuoteTick(){
		String strD = sdf.format(StockUtil.getUSLatestOpenMarketDate());
		nsb.runCmd(NasdaqStockConfig.QUOTE_TICK, marketId, strD, strD);
	}
	@Test
	public void testCmd_QuotePreMarket(){
		String strD = sdf.format(StockUtil.getUSLatestOpenMarketDate());
		nsb.runCmd(NasdaqStockConfig.QUOTE_PREMARKET, marketId, strD, strD);
	}
	@Test
	public void testCmd_QuoteAfterHours(){
		String strD = sdf.format(StockUtil.getUSLatestOpenMarketDate());
		nsb.runCmd(NasdaqStockConfig.QUOTE_AFTERHOURS, marketId, strD, strD);
	}
	@Test
	public void testCmd_Fr_QuarterlyIncomeStatement(){
		nsb.runCmd(NasdaqStockConfig.INCOME_STATEMENT, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_Fr_QuarterlyIncomeStatementOneQ(){
		nsb.runCmd(NasdaqStockConfig.INCOME_STATEMENT, marketId, sdf.format(StockUtil.getUSLatestOpenMarketDate()), sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_Fr_QuarterlyBalanceSheet(){
		nsb.runCmd(NasdaqStockConfig.BALANCE_SHEET, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_Fr_QuarterlyCashFlow(){
		nsb.runCmd(NasdaqStockConfig.CASH_FLOW, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_Fr_QuarterlyRevenueOneQ(){
		nsb.runCmd(NasdaqStockConfig.REVENUE, marketId, sdf.format(StockUtil.getUSLatestOpenMarketDate()),  sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_Fr_QuarterlyRevenue(){
		nsb.runCmd(NasdaqStockConfig.REVENUE, marketId, null,  sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_Issue_DividendHistory(){
		nsb.runCmd(NasdaqStockConfig.DIVIDEND_HISTORY, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	
	@Test
	public void testCmd_QuoteHistorical(){
		nsb.runCmd(NasdaqStockConfig.QUOTE_HISTORY, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_QuoteShortInterest(){
		nsb.runCmd(NasdaqStockConfig.QUOTE_SHORT_INTEREST, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_HolderSummary(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_SUMMARY, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
		nsb.runCmd(NasdaqStockConfig.HOLDING_TOP5, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_HolderInstitutional(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_INSTITUTIONAL, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	@Test
	public void testCmd_HolderInsiders(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_INSIDERS, marketId, null, sdf.format(StockUtil.getUSLatestOpenMarketDate()));
	}
	
	
	@Test
	public void testCrawl_QuoteHistorical() throws InterruptedException {
		List<Task> tl = nsb.getCconf().setUpSite("nasdaq-quote-historical.xml", null);
		String startDate = "2014-01-01";
		String endDate = "2015-09-02";
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
	
	@Test
	public void testCrawl_QuoteTick() throws InterruptedException {
		List<Task> tl = nsb.getCconf().setUpSite("nasdaq-quote-tick.xml", null);
		Date d = StockUtil.getUSLatestOpenMarketDate();
		String strD = sdf.format(d);
		//String stockId = "ba";
		String stockId = "disca";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, nsb.getCconf());
		params.put("marketId", NasdaqStockConfig.MarketId_NASDAQ);
		params.put("stockid", stockId);
		params.put("startDate", strD);
		params.put("endDate", strD);
		List<CrawledItem> cil = tl.get(0).runMyselfWithOutput(params, false);
		for (CrawledItem ci: cil){
			logger.info(ci);
		}
	}
	
	@Test
	public void testCrawl_QuoteAfterHours() throws InterruptedException {
		List<Task> tl = nsb.getCconf().setUpSite("nasdaq-quote-afterhours.xml", null);
		Date d = StockUtil.getUSLatestOpenMarketDate();
		String strD = sdf.format(d);
		//String stockId = "ba";
		//String stockId = "baba";
		String stockId = "aapl";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, nsb.getCconf());
		params.put("marketId", NasdaqStockConfig.MarketId_NASDAQ);
		params.put("stockid", stockId);
		params.put("startDate", strD);
		params.put("endDate", strD);
		List<CrawledItem> cil = tl.get(0).runMyselfWithOutput(params, false);
		for (CrawledItem ci: cil){
			logger.info(ci);
		}
	}

}
