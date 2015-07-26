package org.cld.stock.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.stock.sina.SinaStockBase;
import org.cld.stock.sina.StockConfig;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
		ssb.run_cmd(StockConfig.SINA_STOCK_IDS, MarketId_HS_A, null, null);
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
		ssb.run_cmd(StockConfig.SINA_STOCK_TRADE_DETAIL, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_tradedetail2() throws ParseException{
		String sd = "2015-07-15";
		ssb.run_cmd(StockConfig.SINA_STOCK_TRADE_DETAIL, MarketId_Test, sd, null);
	}
	//融资融券
	@Test
	public void run_browse_market_rzrq1() throws ParseException{
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_RZRQ, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_rzrq2() throws ParseException{
		String sd = "2015-07-15";
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_RZRQ, MarketId_Test, sd, null);
	}
	//大宗交易
	@Test
	public void run_browse_market_dzjy1() throws ParseException{
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_DZJY, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_dzjy2() throws ParseException{
		String sd = "2015-07-15";
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_DZJY, MarketId_Test, sd, null);
	}
	//复权交易
	@Test
	public void run_browse_market_fq_quarter1() throws ParseException {
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_FQ, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_fq_quarter2() throws ParseException {
		String sd = "2015-07-15";
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_FQ, MarketId_Test, sd, null);
	}
	//历史交易
	@Test
	public void run_browse_market_quarter1() throws Exception {
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_HISTORY, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_market_quarter2() throws Exception {
		String sd = "2015-07-15";
		ssb.run_cmd(StockConfig.SINA_STOCK_MARKET_HISTORY, MarketId_Test, sd, null);
	}

	/****
	 * Corp info 公司资料
	 * */
	@Test
	public void run_browse_corp_info() throws Exception {
		ssb.run_cmd(StockConfig.SINA_STOCK_CORP_INFO, MarketId_Test, null, null);
	}
	
	@Test
	public void run_corp_manager1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_CORP_MANAGER, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_corp_manager2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_CORP_MANAGER, MarketId_Test, END_DATE, null);
	}
	
	@Test
	public void run_corp_related(){
		ssb.run_cmd(StockConfig.SINA_STOCK_CORP_RELATED, MarketId_Test, null, null);
	}
	
	@Test
	public void run_corp_related_other(){
		ssb.run_cmd(StockConfig.SINA_STOCK_CORP_RELATED_OTHER, MarketId_Test, null, null);
	}
	
	/***
	 * 发行分配
	 */
	@Test
	public void run_issue_sharebonus1() throws ParseException{
		ssb.run_cmd(StockConfig.SINA_STOCK_ISSUE_SHAREBONUS, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_issue_sharebonus2() throws ParseException{
		ssb.run_cmd(StockConfig.SINA_STOCK_ISSUE_SHAREBONUS, MarketId_Test, END_DATE, null);
	}
	/***********
	 * 股本股东
	 */
	@Test //Stock Structure
	public void run_stock_structure1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_STRUCTURE, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_stock_structure2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_STRUCTURE, MarketId_Test, END_DATE, null);
	}
	
	@Test //Stock holder
	public void run_stock_holder1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_stock_holder2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_stock_holder3(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER, MarketId_Test, null, null);
	}
	
	@Test //Circulate Stock holder
	public void run_circulate_stock_holder1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_circulate_stock_holder2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_circulate_stock_holder3(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, MarketId_Test, null, null);
	}
	
	@Test //Fund Stock holder
	public void run_fund_stock_holder1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fund_stock_holder2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_fund_stock_holder3(){
		ssb.run_cmd(StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, MarketId_Test, null, null);
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
		for (String name: StockConfig.subFR){
			ssb.run_cmd(StockConfig.SINA_STOCK_FR_QUARTER+"-"+name, MarketId_Test, END_DATE, null);
		}
	}
	//FR FootNote
	@Test
	public void run_browse_fr_footnote1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_FOOTNOTE, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_browse_fr_footnote2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_FOOTNOTE, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_browse_fr_footnote3(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_FOOTNOTE, MarketId_Test, null, null);
	}
	
	//Achievement Notice
	@Test
	public void run_fr_achievenotice1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_AchieveNotice, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fr_achievenotice2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_AchieveNotice, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_fr_achievenotice3(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_AchieveNotice, MarketId_Test, null, null);
	}
	
	//Finance Guideline
	@Test
	public void run_fr_finance_guideline1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fr_finance_guideline2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, MarketId_Test, END_DATE, null);
	}
	@Test
	public void run_fr_finance_guideline3(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, MarketId_Test, null, null);
	}
	
	//Asset Devalue
	@Test
	public void run_fr_assetdevalue1(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, MarketId_Test, null, START_DATE);
	}
	@Test
	public void run_fr_assetdevalue2(){
		ssb.run_cmd(StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, MarketId_Test, END_DATE, null);
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
	
	@Test
	public void testXpath() throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\cheyi\\Downloads\\ac.xls"), "GBK")));
		Document doc = builder.parse(is);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//table/tr/td");
		NodeList nl =(NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for (int i=0; i<nl.getLength(); i++){
			logger.info(nl.item(i).getTextContent());
		}
	}
	@Test
	public void testSZAIds() throws Exception{
		ssb.getCconf().setUpSite("szse-stock-ids.xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", "2015-07-25");
		ssb.browsePrd("szse-stock-ids.xml", null, params);
	}
}
