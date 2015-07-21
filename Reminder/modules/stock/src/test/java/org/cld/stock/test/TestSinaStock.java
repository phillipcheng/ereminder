package org.cld.stock.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.SinaStockBase;
import org.cld.stock.sina.StockConfig;
import org.junit.Before;
import org.junit.Test;

public class TestSinaStock {
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	public static final String MarketId_Test = "test";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
		ssb.run_browse_idlist();
	}

	@Test
	public void run_task_1() throws Exception{
		ssb.run_task("run_corp_manager_2015_07_19_12_14_47_437_history_true_MarketId_test_");
	}
	/*****
	 * Market history 行情走势
	 **/
	//成交明细
	@Test
	public void run_browse_tradedetail() throws ParseException{
		ssb.run_browse_tradedetail("2015-07-16");
	}
	
	//融资融券
	@Test
	public void run_browse_market_rzrq() throws ParseException{
		ssb.run_browse_market_rzrq("2015-07-12");
	}
	//大宗交易
	@Test
	public void run_browse_market_dzjy() throws ParseException{
		ssb.run_browse_market_dzjy("2014-07-01");
	}
	
	//历史交易
	//crawl market history to hdfs/hive
	@Test
	public void run_browse_market_history() {
		ssb.run_browse_market_history();
	}
	
	//merge all stocks' market history into one file per quarter
	@Test
	public void run_merge_market_history() throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(ssb.getCconf(), 1990, 1, 2015, 3);
	}

	@Test
	public void run_browse_market_quarter() throws Exception {
		ssb.run_browse_market_quarter(2015, 2);
	}

	@Test
	public void run_merge_market_history_quarter() throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(ssb.getCconf(), 2015, 2, 2015, 2);
	}

	/****
	 * Corp info 公司资料
	 * */
	@Test
	public void run_browse_corp_info() throws Exception {
		ssb.run_browse_corp_info();
	}
	
	//Corp Manager
	@Test
	public void run_corp_manager(){
		ssb.run_corp_manager(true);
	}
	
	//Corp Related
	@Test
	public void run_corp_related(){
		ssb.run_corp_related();
	}
	
	//Corp Related other
	@Test
	public void run_corp_related_other(){
		ssb.run_corp_related_other();
	}
	
	/***
	 * 发行分配
	 */
	@Test
	public void run_issue_sharebonus(){
		ssb.run_issue_sharebonus();
	}
	
	/***********
	 * 股本股东
	 */
	@Test //Stock Structure
	public void run_stock_structure(){
		ssb.run_stock_structure();
	}
	
	@Test //Stock holder
	public void run_stock_holder(){
		ssb.run_stock_holder(true);
	}
	
	@Test //Circulate Stock holder
	public void run_circulate_stock_holder(){
		ssb.run_circulate_stock_holder(true);
	}

	@Test //Fund Stock holder
	public void run_fund_stock_holder(){
		ssb.run_fund_stock_holder(true);
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
	//crawl finance report for specific quarter(s)
	@Test
	public void run_browse_fr_quarter() throws Exception {
		ssb.run_browse_fr_quarter(2015, 1);
	}
	
	//FR FootNote
	@Test
	public void run_browse_fr_footnote_history(){
		ssb.run_browse_fr_footnote_history();
	}
	
	//Achievement Notice
	@Test
	public void run_fr_achievenotice(){
		ssb.run_fr_achievenotice();
	}
	
	//Finance Guideline
	@Test
	public void run_fr_finance_guideline(){
		ssb.run_fr_finance_guideline();
	}
	
	//Asset Devalue
	@Test
	public void run_fr_assetdevalue(){
		ssb.run_fr_assetdevalue();
	}
	

	
	//////Tests
	@Test
	public void test_sina_market_dzjy_1() throws Exception{
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_MARKET_DZJY+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", "2015-07-13");//2 pages
		ssb.browsePrd(StockConfig.SINA_STOCK_MARKET_DZJY+".xml", null, params);
		
		params = new HashMap<String, Object>();
		params.put("date", "2010-07-09");//1 page
		ssb.browsePrd(StockConfig.SINA_STOCK_MARKET_DZJY+".xml", null, params);
	}
	
	@Test
	public void test_sina_market_dzjy_2() throws Exception{//no page
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_MARKET_DZJY+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", "2015-07-12");
		ssb.browsePrd(StockConfig.SINA_STOCK_MARKET_DZJY+".xml", null, params);
	}
	
	@Test
	public void test_sina_stock_holder_genheader() throws Exception{
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_STOCK_HOLDER+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("history", true);
		params.put("GenHeader", true);
		ssb.browsePrd(StockConfig.SINA_STOCK_STOCK_HOLDER+".xml", null, params);
	}
	
	@Test
	public void test_sina_convert_txt_to_csv_with_header() throws Exception {
		String[] sids = new String[]{"600000","601766"};
		for (String sid:sids){
			TabularCSVConvertTask ct = new TabularCSVConvertTask(sid, true);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, ssb.getCconf());
			ct.runMyself(params, null);
		}
	}
	
	@Test
	public void test_browse_fr_footnote() throws Exception {
		String[] sids = new String[]{"600000","601766"};
		for (String sid:sids){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stockid", sid);
			params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, ssb.getCconf());
			ssb.browsePrd(StockConfig.SINA_STOCK_FR_FOOTNOTE+".xml", null, params);
		}
	}
	
	//test browse sina-stock-market-history bpt
	@Test
	public void test_sina_browse_prd_market_history() throws Exception{
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_MARKET_HISTORY+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("year", "1999");
		params.put("quarter", "4");
		ssb.browsePrd(StockConfig.SINA_STOCK_MARKET_HISTORY+".xml", null, params);
		params.put("year", "2014");
		ssb.browsePrd(StockConfig.SINA_STOCK_MARKET_HISTORY+".xml", null, params);
	}
	
	//test browse single corp-info bpt
	@Test
	public void test_browse_corp_info() throws Exception{
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_CORP_INFO +".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		ssb.browsePrd(StockConfig.SINA_STOCK_CORP_INFO +".xml", null, params);
	}
	//test browse achievement notice
	@Test
	public void test_browse_achieve_notice() throws Exception{
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_FR_AchieveNotice +".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		ssb.browsePrd(StockConfig.SINA_STOCK_FR_AchieveNotice +".xml", null, params);
	}
	
	//test browse single stock quarter fr
	@Test
	public void test_sina_browse_financial_report_quarter() throws Exception {
		ssb.getCconf().setUpSite(StockConfig.SINA_STOCK_FR_QUARTER+"-" + StockConfig.subFR[0] +".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("year", 2015);
		params.put("quarter", 2);
		ssb.browsePrd(StockConfig.SINA_STOCK_FR_QUARTER+"-" + StockConfig.subFR[0] +".xml", null, params);
		params.put("quarter", 1);
		ssb.browsePrd(StockConfig.SINA_STOCK_FR_QUARTER+"-" + StockConfig.subFR[0] +".xml", null, params);
	}
}
