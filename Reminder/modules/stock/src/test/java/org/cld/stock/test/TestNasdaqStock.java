package org.cld.stock.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.nasdaq.NasdaqTestStockConfig;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.junit.Before;
import org.junit.Test;

public class TestNasdaqStock {
	private static Logger logger =  LogManager.getLogger(TestNasdaqStock.class);
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static String START_DATE = "2014-11-01";
	private static String END_DATE = "2015-10-17";
	private static Date startDate=null;
	private static Date endDate = null;
	static{
		try{
			sdf.setTimeZone(TimeZone.getTimeZone("EST"));
			startDate = sdf.parse(START_DATE);
			endDate = sdf.parse(END_DATE);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private String marketId = NasdaqStockConfig.MarketId_AMEX;
	//private String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test;
	private String propFile = "client1-v2.properties";
	
	private NasdaqStockBase nsb;

	public TestNasdaqStock(){
		super();
	}
	
	@Before
	public void setUp()throws Exception{
		nsb = new NasdaqStockBase(propFile, marketId, startDate, endDate);
		//nsb.getDsm().addUpdateCrawledItem(nsb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
	}
	
	@Test
	public void testInitTestMarket() throws Exception{
		nsb.getDsm().addUpdateCrawledItem(nsb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
	}
	@Test
	public void testIdsCmd() throws Exception{
		nsb.runCmd(NasdaqStockConfig.STOCK_IDS, marketId, null, "2015-10-17");
	}
	@Test
	public void testRunAllCmd1() throws Exception{
		nsb.runAllCmd();
	}
	
	@Test
	public void testRunCmd1() throws Exception{
		nsb.runCmd(NasdaqStockConfig.BALANCE_SHEET, marketId, sdf.format(startDate), sdf.format(endDate));
	}
	
	@Test
	public void testBrowseIdlist() throws Exception{nsb.run_browse_idlist(NasdaqStockConfig.MarketId_NASDAQ, sdf.parse("2015-08-02"));}
	
	@Test
	public void testIPO() throws Exception{
		nsb.runCmd(NasdaqStockConfig.STOCK_IPO, marketId, null, "2015-10-09");
	}
	@Test
	public void testCmd_QuoteFq() throws Exception{
		nsb.runCmd(NasdaqStockConfig.QUOTE_FQ_HISTORY, marketId, null, "2015-10-10");
	}
	@Test
	public void testPostProcess() throws Exception{
		nsb.setEndDate(sdf.parse("2015-10-09"));
		nsb.postprocess(null);
	}
	//daily
	@Test
	public void testCmd_QuoteTick(){
		nsb.runCmd(NasdaqStockConfig.QUOTE_TICK, marketId, null, null);
	}
	@Test
	public void testCmd_QuotePreMarket(){
		String strD = sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date()));
		nsb.runCmd(NasdaqStockConfig.QUOTE_PREMARKET, marketId, strD, strD);
	}
	@Test
	public void testCmd_QuoteAfterHours(){
		nsb.runCmd(NasdaqStockConfig.QUOTE_AFTERHOURS, marketId, null, null);
	}
	@Test
	public void testCmd_HolderSummary(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_SUMMARY, marketId, null, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
		nsb.runCmd(NasdaqStockConfig.HOLDING_TOP5, marketId, null, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_EarnAnnounce(){
		nsb.runCmd(NasdaqStockConfig.EARN_ANNOUNCE, marketId, null, "2010-01-10");
	}
	@Test
	public void testCmd_Fr_QuarterlyIncomeStatement(){
		nsb.runCmd(NasdaqStockConfig.INCOME_STATEMENT, marketId, null, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Fr_QuarterlyIncomeStatementOneQ(){
		nsb.runCmd(NasdaqStockConfig.INCOME_STATEMENT, marketId, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())), sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Fr_QuarterlyBalanceSheet(){
		nsb.runCmd(NasdaqStockConfig.BALANCE_SHEET, marketId, null, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Fr_QuarterlyCashFlow(){
		nsb.runCmd(NasdaqStockConfig.CASH_FLOW, marketId, null, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Fr_QuarterlyRevenueOneQ(){
		nsb.runCmd(NasdaqStockConfig.REVENUE, marketId, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())),  sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Fr_QuarterlyRevenue(){
		nsb.runCmd(NasdaqStockConfig.REVENUE, marketId, null,  sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}	
	@Test
	public void testCmd_HolderInstitutional(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_INSTITUTIONAL, marketId, null, "2015-10-06");
	}
	//date range
	@Test
	public void testCmd_Issue_DividendHistory(){
		nsb.runCmd(NasdaqStockConfig.DIVIDEND_HISTORY, marketId, "2015-08-01", sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_QuoteShortInterest(){
		nsb.runCmd(NasdaqStockConfig.QUOTE_SHORT_INTEREST, marketId, "2015-08-01", sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_HolderInsiders(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_INSIDERS, marketId, null, sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_HolderInsidersDateRange(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_INSIDERS, marketId, "2015-02-01", sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
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
		Date d = nsb.getStockConfig().getLatestOpenMarketDate(new Date());
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
		Date d = nsb.getStockConfig().getLatestOpenMarketDate(new Date());
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
