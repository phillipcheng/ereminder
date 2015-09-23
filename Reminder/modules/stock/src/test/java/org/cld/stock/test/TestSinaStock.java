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
	
	private SinaStockBase ssb;

	public TestSinaStock(){
		super();
	}
	
	@Before
	public void setUp(){
		ssb = new SinaStockBase(propFile, marketId, startDate, endDate);
		ssb.getCconf().getTaskMgr().getHadoopCrawledItemFolder();
	}
	
	//
	@Test
	public void testInitTestMarket() throws Exception{ssb.getDsm().addUpdateCrawledItem(ssb.run_browse_idlist(this.marketId, sdf.parse(END_DATE)), null);}
	@Test
	public void testRunAllCmd1() throws Exception{
		ssb.runAllCmd(SinaTestStockConfig.date_Test_D1, SinaTestStockConfig.date_Test_D2);
	}
	@Test
	public void tradedetail_postprocess_1() {
		TradeDetailPostProcessTask.launch(this.propFile, ssb.getCconf(), SinaTestStockConfig.Test_D1 + "_" + SinaTestStockConfig.Test_D2);
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
	public void testRunAllCmd2() throws Exception{
		ssb.runAllCmd(SinaTestStockConfig.date_Test_D2, SinaTestStockConfig.date_Test_D3);
	}
	@Test
	public void tradedetail_postprocess_2() {
		TradeDetailPostProcessTask.launch(this.propFile, ssb.getCconf(), SinaTestStockConfig.Test_D2 + "_" + SinaTestStockConfig.Test_D3);
	}
	@Test
	public void testMerge_2() throws Exception{
		MergeTask.launch(ssb.getStockConfig(), this.propFile, ssb.getCconf(), SinaTestStockConfig.Test_D2 + "_" + SinaTestStockConfig.Test_D3, null, false);
	}
	//
	
	@Test
	public void testRunAllCmd3() throws Exception{
		ssb.runAllCmd(null, SinaTestStockConfig.date_Test_D3);
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
	@Test
	public void testIPODate() throws Exception{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_IPODate, SinaStockConfig.MarketId_HS_A, "2015-05-01", sdf.format(new Date()));
	}
	@Test
	public void testGetStockIPO(){
		ETLUtil.getIPODateByStockId(ssb.getStockConfig(), SinaStockConfig.MarketId_HS_Test, "600191", ssb.getCconf());
	}
	/*****
	 * Market history 行情走势
	 **/
	//成交明细
	@Test
	public void run_browse_tradedetail1() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_TRADE_DETAIL, SinaStockConfig.MarketId_HS_Test, START_DATE, END_DATE);
	}
	@Test
	public void run_browse_tradedetail2() throws ParseException{
		String sd = "2015-07-15";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_TRADE_DETAIL, SinaStockConfig.MarketId_HS_Test, sd, null);
	}
	@Test
	public void tradedetail_checkdownload() {
		TradeDetailCheckDownload.launch(ssb.getCconf(), SinaTestStockConfig.Test_D1 + "_" + SinaTestStockConfig.Test_D2);
	}
	
	
	//融资融券
	@Test
	public void run_browse_market_rzrq1() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_RZRQ, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_rzrq2() throws ParseException{
		String sd = "2015-07-15";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_RZRQ, SinaStockConfig.MarketId_HS_Test, sd, null);
	}
	//大宗交易
	@Test
	public void run_browse_market_dzjy1() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_DZJY, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_dzjy2() throws ParseException{
		String sd = "2015-07-15";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_DZJY, SinaStockConfig.MarketId_HS_Test, sd, null);
	}
	//复权交易
	@Test
	public void run_browse_market_fq_quarter1() throws ParseException {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_FQ, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_fq_quarter2() throws ParseException {
		String sd = "2015-07-15";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_FQ, SinaStockConfig.MarketId_HS_Test, sd, null);
	}
	@Test
	public void run_browse_market_fq_quarter3() throws ParseException {
		String sd = "2015-06-15";
		String ed = "2015-06-20";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_FQ, SinaStockConfig.MarketId_HS_Test, sd, ed);
	}
	
	//历史交易
	@Test
	public void run_browse_market_quarter1() throws Exception {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_HISTORY, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_quarter2() throws Exception {
		String sd = "2015-07-15";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_HISTORY, SinaStockConfig.MarketId_HS_Test, sd, null);
	}
	@Test
	public void run_browse_market_quarter3() throws Exception {
		String sd = "2015-06-15";
		String ed = "2015-07-15";
		ssb.runCmd(SinaStockConfig.SINA_STOCK_MARKET_HISTORY, SinaStockConfig.MarketId_HS_Test, sd, ed);
	}

	/****
	 * Corp info 公司资料
	 * */
	@Test
	public void run_browse_corp_info() throws Exception {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_INFO, SinaStockConfig.MarketId_HS_Test, null, END_DATE);
	}
	
	@Test
	public void run_corp_manager1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_MANAGER, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_corp_manager2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_CORP_MANAGER, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
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
	public void run_issue_sharebonus1() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_ISSUE_SHAREBONUS, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_issue_sharebonus2() throws ParseException{
		ssb.runCmd(SinaStockConfig.SINA_STOCK_ISSUE_SHAREBONUS, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	/***********
	 * 股本股东
	 */
	@Test //Stock Structure
	public void run_stock_structure1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_STRUCTURE, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_stock_structure2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_STRUCTURE, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	
	@Test //Stock holder
	public void run_stock_holder1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER, SinaStockConfig.MarketId_HS_Test, "2014-11-01", "2015-11-01");
	}
	@Test
	public void run_stock_holder2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	@Test
	public void run_stock_holder3(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	@Test //Circulate Stock holder
	public void run_circulate_stock_holder1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_circulate_stock_holder2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	@Test
	public void run_circulate_stock_holder3(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	@Test //Fund Stock holder
	public void run_fund_stock_holder1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_FUND, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_fund_stock_holder2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_FUND, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	@Test
	public void run_fund_stock_holder3(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_STOCK_HOLDER_FUND, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	/****
	 * 财务数据
	 */
	//fr-quarter
	@Test
	public void run_browse_fr_quarter1() throws Exception {
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_QUARTER_BALANCE_SHEET, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_QUARTER_CASHFLOW, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_QUARTER_PROFIT_STATEMENT, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	//FR FootNote
	@Test
	public void run_browse_fr_footnote1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_FOOTNOTE, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_browse_fr_footnote2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_FOOTNOTE, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	@Test
	public void run_browse_fr_footnote3(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_FOOTNOTE, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	//Achievement Notice
	@Test
	public void run_fr_achievenotice1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_AchieveNotice, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_fr_achievenotice2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_AchieveNotice, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	@Test
	public void run_fr_achievenotice3(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_AchieveNotice, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	//Finance Guideline
	@Test
	public void run_fr_finance_guideline1(){//600028,600026 ipo date is error
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_GUIDELINE_YEAR, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_fr_finance_guideline2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_GUIDELINE_YEAR, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
	}
	@Test
	public void run_fr_finance_guideline3(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_GUIDELINE_YEAR, SinaStockConfig.MarketId_HS_Test, null, null);
	}
	
	//Asset Devalue
	@Test
	public void run_fr_assetdevalue1(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, SinaStockConfig.MarketId_HS_Test, null, START_DATE);
	}
	@Test
	public void run_fr_assetdevalue2(){
		ssb.runCmd(SinaStockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, SinaStockConfig.MarketId_HS_Test, END_DATE, null);
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
