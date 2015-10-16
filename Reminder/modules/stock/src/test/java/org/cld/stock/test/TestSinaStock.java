package org.cld.stock.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.ETLUtil;
import org.cld.stock.sina.SinaStockBase;
import org.cld.stock.sina.SinaTestStockConfig;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.stock.sina.task.TradeDetailCheckDownload;
import org.cld.stock.sina.task.TradeDetailPostProcessTask;
import org.cld.stock.strategy.CompareSelectSuite;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.task.MergeTask;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
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
	
	private String marketId = SinaStockConfig.MarketId_HS_Test;
	private String propFile = "client1-v2.properties";
	//private String propFile = "cld-stock-cluster.properties";
	
	private SinaStockBase ssb;

	public TestSinaStock(){
		super();
	}
	
	@Before
	public void setUp() throws Exception{
		ssb = new SinaStockBase(propFile, marketId, startDate, endDate);
		ssb.getCconf().getTaskMgr().getHadoopCrawledItemFolder();
		//ssb.getDsm().addUpdateCrawledItem(ssb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
	}
	//
	@Test
	public void testInitTestMarket() throws Exception{
		ssb.getDsm().addUpdateCrawledItem(ssb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);
	}
	@Test
	public void testInitIPODate() throws Exception{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_IPO, SinaStockConfig.MarketId_HS_A, null, sdf.format(new Date()));
	}
	
	@Test
	public void testRunAllCmd1() throws Exception{
		ssb.runAllCmd(SinaTestStockConfig.date_Test_D1, SinaTestStockConfig.date_Test_D2);
	}
	@Test
	public void testPostProcess() throws Exception{
		ssb.setEndDate(sdf.parse("2015-10-09"));
		ssb.postprocess(null);
	}
	@Test
	public void tradedetail_postprocess_1() {
		new TradeDetailPostProcessTask().launch(this.propFile, ssb.getCconf(), SinaTestStockConfig.Test_D1 + "_" + SinaTestStockConfig.Test_D2, new String[]{});
	}
	@Test
	public void testMerge_1() throws Exception{
		MergeTask.launch(ssb.getStockConfig(), this.propFile, ssb.getCconf(), SinaTestStockConfig.Test_D1 + "_" + SinaTestStockConfig.Test_D2, null, false);
	}
	@Test
	public void testMRMerge_1() throws Exception{
		MergeTask.launch(ssb.getStockConfig(), this.propFile, ssb.getCconf(), SinaTestStockConfig.Test_D1 + "_" + SinaTestStockConfig.Test_D2, null, true);
	}
	@Test
	public void run_task_1() throws Exception{
		ssb.run_task(new String[]{"run_corp_manager_2015_07_19_12_14_47_437_history_true_SinaStockConfig.MarketId_HS_Test_"}, null);
	}
	
	/***
	 * Stock ids
	 * */
	//get all stockids by market
	@Test
	public void run_browse_idlist() throws Exception{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_IDS, SinaStockConfig.MarketId_HS_A, null, null);
	}
	@Test
	public void testBrowseIdlist_with_st() throws Exception{
		Date ed = sdf.parse("2015-08-02");
		ssb.run_browse_idlist(SinaStockConfig.MarketId_HS_A, ed);
	}
	/*****
	 * Market history 行情走势
	 **/
	@Test
	public void run_browse_tradedetail() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_TRADE_DETAIL, SinaStockConfig.MarketId_HS_Test, null, sdf.format(new Date()));
	}
	@Test
	public void tradedetail_checkdownload() {
		TradeDetailCheckDownload.launch(ssb.getCconf(), SinaTestStockConfig.Test_D1 + "_" + SinaTestStockConfig.Test_D2);
	}
	//融资融券
	@Test
	public void run_browse_market_rzrq() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_RZRQ, SinaStockConfig.MarketId_HS_Test, null, "2015-10-09");
	}
	//大宗交易
	@Test
	public void run_browse_market_dzjy() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_DZJY, SinaStockConfig.MarketId_HS_Test, "2015-10-02", "2015-10-06");
	}
	//复权交易
	@Test
	public void run_browse_market_fq_quarter() throws ParseException {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_FQ, SinaStockConfig.MarketId_HS_Test, null, "2015-10-10");
	}
	//历史交易
	@Test
	public void run_browse_market_quarter() throws Exception {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_HISTORY, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}

	/****
	 * Corp info 公司资料
	 * */
	@Test
	public void run_browse_corp_info() throws Exception {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_INFO, SinaStockConfig.MarketId_HS_Test, null, END_DATE);
	}
	@Test
	public void run_corp_manager(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_MANAGER, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_corp_related(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_RELATED, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	@Test
	public void run_corp_related_other(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_RELATED_OTHER, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	/***
	 * 发行分配
	 */
	@Test
	public void run_issue_sharebonus() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_ISSUE_SHAREBONUS, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	/***********
	 * 股本股东
	 */
	@Test //Stock Structure
	public void run_stock_structure(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_STRUCTURE, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	
	@Test //Stock holder
	public void run_stock_holder(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER, SinaStockConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	@Test //Circulate Stock holder
	public void run_circulate_stock_holder(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, SinaStockConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	@Test //Fund Stock holder
	public void run_fund_stock_holder(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_FUND, SinaStockConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	/****
	 * 财务数据
	 */
	//fr-quarter
	@Test
	public void run_browse_fr_quarter() throws Exception {
		//ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_QUARTER_BALANCE_SHEET, SinaStockConfig.MarketId_HS_Test, null, "2015-09-25");
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_QUARTER_CASHFLOW, SinaStockConfig.MarketId_HS_Test, null, "2015-09-25");
		//ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, SinaStockConfig.MarketId_HS_Test, "2006-01-01", "2006-12-25");
	}
	@Test
	public void run_browse_fr_footnote(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_FOOTNOTE, SinaStockConfig.MarketId_HS_Test, null, "2015-09-25");
	}
	//Achievement Notice
	@Test
	public void run_fr_achievenotice(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_AchieveNotice, SinaStockConfig.MarketId_HS_Test, null, "2015-09-25");
	}
	//Finance Guideline
	@Test
	public void run_fr_finance_guideline(){//600028,600026 ipo date is error
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_GUIDELINE_YEAR, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	//Asset Devalue
	@Test
	public void run_fr_assetdevalue(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, SinaStockConfig.MarketId_HS_Test, "2015-08-29", "2015-09-22");
	}
	
	@Test
	public void testCompareSelectSuite(){
		//CompareSelectSuite.selectHSAAllTimeLow("2015-10-09", ssb.getCconf(), "C:/mydoc/myprojects/ereminder/Reminder/modules/stock/output");
		//CompareSelectSuite.selectHSABreakIPO("2015-10-09", ssb.getCconf(), "C:/mydoc/myprojects/ereminder/Reminder/modules/stock/output");
		SelectStrategy ss = CompareSelectSuite.getHSARallyRatio("C:/mydoc/myprojects/ereminder/Reminder/modules/stock/output");
		ss.select(ssb.getCconf(), ss, "2015-10-12");
	}
	
	@Test
	public void testTradeStrategy1(){
		
	}
	
	//test crawl
	@Test
	public void test_crawl_1() throws InterruptedException{
		List<Task> tl = ssb.getCconf().setUpSite("sina-stock-stock-holder.xml", null);
		String startDate = "2014-01-01";
		String endDate = "2015-12-31";
		String stockId = "002216";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, ssb.getCconf());
		params.put("stockid", stockId);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		List<CrawledItem> cil = tl.get(0).runMyselfWithOutput(params, false);
		logger.info(cil.get(0));
	}
	

}
