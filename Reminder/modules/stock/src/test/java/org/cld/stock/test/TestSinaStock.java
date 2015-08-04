package org.cld.stock.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.stock.sina.SinaStockBase;
import org.cld.stock.sina.StockConfig;
import org.cld.taskmgr.TaskMgr;
import org.junit.Before;
import org.junit.Test;

public class TestSinaStock {
	private static Logger logger =  LogManager.getLogger(TestSinaStock.class);
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	public static final String MarketId_Test = "test";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static String START_DATE = "2014-11-10";
	private static String END_DATE = "2014-11-10";
	
	private String marketId = MarketId_Test;
	private String propFile = "client1-v2.properties";
	
	private SinaStockBase ssb;

	public TestSinaStock(){
		super();
	}
	
	@Before
	public void setUp(){
		ssb = new SinaStockBase(propFile, marketId);
		ssb.getCconf().getTaskMgr().getHadoopCrawledItemFolder();
	}

	/***
	 * Stock ids
	 * */
	//get all stockids by market
	@Test
	public void run_browse_idlist() throws Exception{
		ssb.runCmd(StockConfig.SINA_STOCK_IDS, MarketId_HS_A, null, null);
	}
	@Test
	public void testBrowseIdlist_with_st() throws Exception{
		Date ed = sdf.parse("2015-08-02");
		ssb.run_browse_idlist(MarketId_HS_A, ed);
	}
	
	@Test
	public void testRunAllCmd1() throws Exception{
		ssb.runAllCmd(SinaStockBase.Test_SD, SinaStockBase.Test_D1);
	}
	@Test
	public void testRunAllCmd2() throws Exception{
		ssb.runAllCmd(SinaStockBase.Test_SD, SinaStockBase.Test_D2);
	}
	@Test
	public void testRunAllCmd3() throws Exception{
		ssb.runAllCmd(SinaStockBase.Test_SD, SinaStockBase.Test_D3);
	}
	
	
	@Test
	public void run_task_1() throws Exception{
		//ssb.run_task("run_corp_manager_2015_07_19_12_14_47_437_history_true_MarketId_test_");
	}
	/*****
	 * Market history 行情走势
	 **/
	//成交明细
	@Test
	public void run_browse_tradedetail1() throws ParseException{
		ssb.runCmd(StockConfig.SINA_STOCK_TRADE_DETAIL, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_tradedetail2() throws ParseException{
		String sd = "2015-07-15";
		ssb.runCmd(StockConfig.SINA_STOCK_TRADE_DETAIL, MarketId_Test, sd, null);
	}
	//融资融券
	@Test
	public void run_browse_market_rzrq1() throws ParseException{
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_RZRQ, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_rzrq2() throws ParseException{
		String sd = "2015-07-15";
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_RZRQ, MarketId_Test, sd, null);
	}
	//大宗交易
	@Test
	public void run_browse_market_dzjy1() throws ParseException{
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_DZJY, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_dzjy2() throws ParseException{
		String sd = "2015-07-15";
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_DZJY, MarketId_Test, sd, null);
	}
	//复权交易
	@Test
	public void run_browse_market_fq_quarter1() throws ParseException {
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_FQ, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_fq_quarter2() throws ParseException {
		String sd = "2015-07-15";
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_FQ, MarketId_Test, sd, null);
	}
	@Test
	public void run_browse_market_fq_quarter3() throws ParseException {
		String sd = "2015-06-15";
		String ed = "2015-06-20";
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_FQ, MarketId_Test, sd, ed);
	}
	
	//历史交易
	@Test
	public void run_browse_market_quarter1() throws Exception {
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_HISTORY, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_quarter2() throws Exception {
		String sd = "2015-07-15";
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_HISTORY, MarketId_Test, sd, null);
	}
	@Test
	public void run_browse_market_quarter3() throws Exception {
		String sd = "2015-06-15";
		String ed = "2015-07-15";
		ssb.runCmd(StockConfig.SINA_STOCK_MARKET_HISTORY, MarketId_Test, sd, ed);
	}

	/****
	 * Corp info 公司资料
	 * */
	@Test
	public void run_browse_corp_info() throws Exception {
		ssb.runCmd(StockConfig.SINA_STOCK_CORP_INFO, MarketId_Test, null, null);
	}
	
	@Test
	public void run_corp_manager1(){
		ssb.runCmd(StockConfig.SINA_STOCK_CORP_MANAGER, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_corp_manager2(){
		ssb.runCmd(StockConfig.SINA_STOCK_CORP_MANAGER, MarketId_Test, END_DATE, null);
	}
	
	@Test
	public void run_corp_related(){
		ssb.runCmd(StockConfig.SINA_STOCK_CORP_RELATED, MarketId_Test, null, null);
	}
	
	@Test
	public void run_corp_related_other(){
		ssb.runCmd(StockConfig.SINA_STOCK_CORP_RELATED_OTHER, MarketId_Test, null, null);
	}
	
	/***
	 * 发行分配
	 */
	@Test
	public void run_issue_sharebonus1() throws ParseException{
		ssb.runCmd(StockConfig.SINA_STOCK_ISSUE_SHAREBONUS, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_issue_sharebonus2() throws ParseException{
		ssb.runCmd(StockConfig.SINA_STOCK_ISSUE_SHAREBONUS, MarketId_Test, END_DATE, null);
	}
	/***********
	 * 股本股东
	 */
	@Test //Stock Structure
	public void run_stock_structure1(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_STRUCTURE, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_stock_structure2(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_STRUCTURE, MarketId_Test, END_DATE, null);
	}
	
	@Test //Stock holder
	public void run_stock_holder1(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_stock_holder2(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_stock_holder3(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER, MarketId_Test, null, null);
	}
	
	@Test //Circulate Stock holder
	public void run_circulate_stock_holder1(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_circulate_stock_holder2(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_circulate_stock_holder3(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, MarketId_Test, null, null);
	}
	
	@Test //Fund Stock holder
	public void run_fund_stock_holder1(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fund_stock_holder2(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_fund_stock_holder3(){
		ssb.runCmd(StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, MarketId_Test, null, null);
	}
	
	/****
	 * 财务数据
	 */
	//crawl financial report history by market to hdfs
	@Test
	public void run_browse_fr_history() throws Exception {
		ssb.run_browse_fr_history();
	}
	//fr history convert to csv/hive
	@Test
	public void run_convert_fr_history_tabular_to_csv() throws Exception {
		ssb.run_convert_fr_history_tabular_to_csv();
	}
	//fr history reformat from split by stockid to split by quarter
	@Test
	public void run_fr_reformat() throws Exception{
		ssb.run_fr_reformat();
	}
	//fr-quarter
	@Test
	public void run_browse_fr_quarter1() throws Exception {
		ssb.runCmd(StockConfig.SINA_STOCK_FR_QUARTER_BALANCE_SHEET, MarketId_Test, END_DATE, null);
		ssb.runCmd(StockConfig.SINA_STOCK_FR_QUARTER_CASHFLOW, MarketId_Test, END_DATE, null);
		ssb.runCmd(StockConfig.SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, MarketId_Test, END_DATE, null);
	}
	//FR FootNote
	@Test
	public void run_browse_fr_footnote1(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_FOOTNOTE, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_fr_footnote2(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_FOOTNOTE, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_browse_fr_footnote3(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_FOOTNOTE, MarketId_Test, null, null);
	}
	
	//Achievement Notice
	@Test
	public void run_fr_achievenotice1(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_AchieveNotice, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fr_achievenotice2(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_AchieveNotice, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_fr_achievenotice3(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_AchieveNotice, MarketId_Test, null, null);
	}
	
	//Finance Guideline
	@Test
	public void run_fr_finance_guideline1(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fr_finance_guideline2(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_fr_finance_guideline3(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, MarketId_Test, null, null);
	}
	
	//Asset Devalue
	@Test
	public void run_fr_assetdevalue1(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fr_assetdevalue2(){
		ssb.runCmd(StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, MarketId_Test, END_DATE, null);
	}
}
