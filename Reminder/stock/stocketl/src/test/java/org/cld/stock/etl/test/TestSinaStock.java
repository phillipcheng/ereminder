package org.cld.stock.etl.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.StockUtil;
import org.cld.stock.config.SinaStockConfig;
import org.cld.stock.etl.StockBase;
import org.cld.stock.etl.base.ETLConfig;
import org.cld.stock.etl.base.SinaETLConfig;
import org.cld.stock.etl.base.SinaStockBase;
import org.cld.stock.etl.task.MergeTask;
import org.cld.stock.etl.task.sina.TradeDetailCheckDownload;
import org.cld.stock.etl.task.sina.TradeDetailPostProcessTask;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.CrawledItem;
import org.junit.Before;
import org.junit.Test;

public class TestSinaStock {
	private static Logger logger =  LogManager.getLogger(TestSinaStock.class);
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
	
	private String marketId = SinaETLConfig.MarketId_HS_Test;
	private String propFile = "client1-v2.properties";
	//private String propFile = "cld-stock-cluster.properties";
	
	private SinaStockBase ssb;
	private ETLConfig etlconfig;

	public TestSinaStock(){
		super();
	}
	
	@Before
	public void setUp() throws Exception{
		ssb = new SinaStockBase(propFile, marketId, startDate, endDate);
		ssb.getCconf().getHadoopCrawledItemFolder();
		ssb.getDsm().addUpdateCrawledItem(ssb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
		etlconfig = ETLConfig.getETLConfig(StockUtil.SINA_STOCK_BASE);
	}
	//
	@Test
	public void testInitTestMarket() throws Exception{
		ssb.getDsm().addUpdateCrawledItem(ssb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
	}
	@Test
	public void testIPO() throws Exception{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_IPO, SinaETLConfig.MarketId_HS_Test, null, sdf.format(new Date()));
	}
	
	@Test
	public void testRunAllCmd1() throws Exception{
		ssb.runAllCmd(null);
	}
	@Test
	public void testPostProcess() throws Exception{
		ssb.setEndDate(sdf.parse("2015-10-09"));
		ssb.postprocess(null);
	}
	@Test
	public void tradedetail_postprocess_1() {
		new TradeDetailPostProcessTask().launch(this.propFile, ssb.getBaseMarketId(), ssb.getCconf(), SinaETLConfig.Test_D1 + "_" + SinaETLConfig.Test_D2, new String[]{});
	}
	@Test
	public void testMerge_1() throws Exception{
		MergeTask.launch(etlconfig, this.propFile, ssb.getBaseMarketId(), ssb.getCconf(), SinaETLConfig.Test_D1 + "_" + SinaETLConfig.Test_D2, null, false);
	}
	@Test
	public void testMRMerge_1() throws Exception{
		MergeTask.launch(etlconfig, this.propFile, ssb.getBaseMarketId(), ssb.getCconf(), SinaETLConfig.Test_D1 + "_" + SinaETLConfig.Test_D2, null, true);
	}
	
	/***
	 * Stock ids
	 * */
	//get all stockids by market
	@Test
	public void run_browse_idlist() throws Exception{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_IDS, SinaETLConfig.MarketId_HS_A, null, null);
	}
	@Test
	public void testBrowseIdlist_with_st() throws Exception{
		Date ed = sdf.parse("2015-08-02");
		ssb.run_browse_idlist(SinaETLConfig.MarketId_HS_A, ed);
	}
	@Test
	public void testBulletin() throws ParseException{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_BULLETIN, SinaETLConfig.MarketId_HS_Test, null, sdf.format(new Date()));
	}
	/*****
	 * Market history 行情走势
	 **/
	@Test
	public void run_browse_tradedetail() throws ParseException{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_TRADE_DETAIL, SinaETLConfig.MarketId_HS_Test, null, sdf.format(new Date()));
	}
	@Test
	public void tradedetail_checkdownload() {
		TradeDetailCheckDownload.launch(ssb.getCconf(), SinaETLConfig.Test_D1 + "_" + SinaETLConfig.Test_D2);
	}
	//融资融券
	@Test
	public void run_browse_market_rzrq() throws ParseException{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_MARKET_RZRQ, SinaETLConfig.MarketId_HS_Test, null, "2015-10-09");
	}
	//大宗交易
	@Test
	public void run_browse_market_dzjy() throws ParseException{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_MARKET_DZJY, SinaETLConfig.MarketId_HS_Test, "2015-10-02", "2015-10-06");
	}
	//复权交易
	@Test
	public void run_browse_market_fq_quarter() throws ParseException {
		ssb.runCmd(SinaETLConfig.SINA_STOCK_MARKET_FQ, SinaETLConfig.MarketId_HS_Test, null, "2015-10-10");
	}
	//历史交易
	@Test
	public void run_browse_market_quarter() throws Exception {
		ssb.runCmd(SinaETLConfig.SINA_STOCK_MARKET_HISTORY, SinaETLConfig.MarketId_HS_Test, null, START_DATE);
	}

	/****
	 * Corp info 公司资料
	 * */
	@Test
	public void run_browse_corp_info() throws Exception {
		ssb.runCmd(SinaETLConfig.SINA_STOCK_CORP_INFO, SinaETLConfig.MarketId_HS_Test, null, END_DATE);
	}
	@Test
	public void run_corp_manager(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_CORP_MANAGER, SinaETLConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_corp_related(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_CORP_RELATED, SinaETLConfig.MarketId_HS_Test, null, null);
	}
	@Test
	public void run_corp_related_other(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_CORP_RELATED_OTHER, SinaETLConfig.MarketId_HS_Test, null, null);
	}
	
	@Test
	public void run_issue_sharebonus() throws ParseException{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_ISSUE_SHAREBONUS, SinaETLConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_issue_addstock() throws ParseException{
		ssb.runCmd(SinaETLConfig.SINA_STOCK_ISSUE_ADDSTOCK, SinaETLConfig.MarketId_HS_Test, null, START_DATE);
	}
	/***********
	 * 股本股东
	 */
	@Test //Stock Structure
	public void run_stock_structure(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_STOCK_STRUCTURE, SinaETLConfig.MarketId_HS_Test, null, START_DATE);
	}
	
	@Test //Stock holder
	public void run_stock_holder(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_STOCK_HOLDER, SinaETLConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	@Test //Circulate Stock holder
	public void run_circulate_stock_holder(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, SinaETLConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	@Test //Fund Stock holder
	public void run_fund_stock_holder(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_STOCK_HOLDER_FUND, SinaETLConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	/****
	 * 财务数据
	 */
	@Test
	public void run_browse_fr_quarter() throws Exception {
		//ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_QUARTER_BALANCE_SHEET, SinaETLConfig.MarketId_HS_Test, null, "2015-09-25");
		ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_QUARTER_CASHFLOW, SinaETLConfig.MarketId_HS_Test, null, "2015-09-25");
		//ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, SinaETLConfig.MarketId_HS_Test, "2006-01-01", "2006-12-25");
	}
	@Test
	public void run_browse_fr_footnote(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_FOOTNOTE, SinaETLConfig.MarketId_HS_Test, null, "2015-09-25");
	}
	@Test
	public void run_fr_achievenotice(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_AchieveNotice, SinaETLConfig.MarketId_HS_Test, null, "2015-09-25");
	}
	@Test
	public void run_fr_finance_guideline(){//600028,600026 ipo date is error
		ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_GUIDELINE_YEAR, SinaETLConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_fr_assetdevalue(){
		ssb.runCmd(SinaETLConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, SinaETLConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
}
