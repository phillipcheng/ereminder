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
import org.cld.stock.StockBase;
import org.cld.stock.nasdaq.NasdaqStockBase;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.nasdaq.NasdaqTestStockConfig;
import org.cld.stock.sina.SinaStockBase;
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
			//sdf.setTimeZone(TimeZone.getTimeZone("EST"));
			startDate = sdf.parse(START_DATE);
			endDate = sdf.parse(END_DATE);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	//private String marketId = NasdaqStockConfig.MarketId_AMEX;
	private String marketId = NasdaqTestStockConfig.MarketId_NASDAQ_Test;
	private String propFile = "client1-v2.properties";
	
	private NasdaqStockBase nsb;

	public TestNasdaqStock(){
		super();
	}
	
	@Before
	public void setUp()throws Exception{
		nsb = new NasdaqStockBase(propFile, marketId, startDate, endDate);
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
	public void testRunAllCmd() throws Exception{
		nsb.setStartDate(null);
		nsb.setEndDate(NasdaqTestStockConfig.date_Test_END_D1);
		nsb.runAllCmd(null);
		
		nsb.setStartDate(null);
		nsb.setEndDate(NasdaqTestStockConfig.date_Test_END_D3);
		nsb.runAllCmd(null);
		
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
		nsb.runCmd(NasdaqStockConfig.QUOTE_FQ_HISTORY, marketId, null, "2015-11-18");
	}
	@Test
	public void testCmd_QuoteHistory(){
		nsb.runCmd(NasdaqStockConfig.QUOTE_HISTORY, marketId, null, "2015-11-19");
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
		nsb.runCmd(NasdaqStockConfig.EARN_ANNOUNCE, marketId, "2015-11-01", "2015-11-03");
	}
	@Test
	public void testCmd_EarnAnnounceTime(){
		nsb.runCmd(NasdaqStockConfig.EARN_ANNOUNCE_TIME, marketId, "2015-11-01", "2015-11-03");
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
	public void testCmd_Issue_Dividend(){
		nsb.runCmd(NasdaqStockConfig.ISSUE_DIVIDEND, marketId, "2015-08-01", sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Issue_ExDivSplit(){
		nsb.runCmd(NasdaqStockConfig.ISSUE_XDIVSPLIT_HISTORY, marketId, "2011-08-01", sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
	}
	@Test
	public void testCmd_Issue_Split(){
		nsb.runCmd(NasdaqStockConfig.ISSUE_SPLIT, marketId, "2011-08-01", sdf.format(nsb.getStockConfig().getLatestOpenMarketDate(new Date())));
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
	public void testCmd_HolderInstitutional(){
		nsb.runCmd(NasdaqStockConfig.HOLDING_INSTITUTIONAL, marketId, null, "2015-10-06");
	}
	//strategy
	@Test
	public void testAllStrategy() throws Exception{
		String pFile = "client1-v2.properties";
		Date sd = sdf.parse("2014-10-01");
		Date ed = sdf.parse("2015-10-15");
		StockBase sb = new NasdaqStockBase(pFile, marketId, sd, ed);
		sb.validateAllStrategyByStock(null);
	}
	
	@Test
	public void testRallySS() throws Exception{
		String pFile = "client1-v2.properties";
		Date sd = sdf.parse("2015-09-01");
		Date ed = sdf.parse("2015-11-02");
		StockBase sb = new NasdaqStockBase(pFile, marketId, sd, ed);
		sb.validateAllStrategyByStock("bs.rally");
	}
	
	@Test
	public void testCloseDropSS() throws Exception{
		String pFile = "client1-v2.properties";
		Date sd = sdf.parse("2015-09-01");
		Date ed = sdf.parse("2015-11-02");
		StockBase sb = new NasdaqStockBase(pFile, marketId, sd, ed);
		sb.validateAllStrategyByStock("closedropavgclose");
	}
	
	@Test
	public void testReadyCrawl() throws Exception{
		String pFile = "client1-v2.properties";
		StockBase sb = new NasdaqStockBase(pFile, marketId, null, null);
		sb.fqReady(new Date());
	}
	
	@Test
	public void testPrepareOneDayData() throws Exception{
		String pFile = "client1-v2.properties";
		StockBase sb = new NasdaqStockBase(pFile, marketId, null, sdf.parse("2015-11-13"));
		sb.prepareOneDayData("closedropavg");
	}
}
