package org.cld.stock.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.util.DateTimeUtil;

import org.cld.datastore.entity.CrawledItem;

import org.cld.taskmgr.entity.Task;

import org.cld.etl.csv.CsvReformatMapredLauncher;
import org.cld.etl.fci.HBaseToCSVMapperLauncher;

import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.test.TestBase;

import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.FRFootNoteToCSV;
import org.cld.stock.sina.StockConfig;

import org.junit.Before;
import org.junit.Test;

public class TestSinaStock extends TestBase{
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	public static final String MarketId_Test = "test";
	
	private String marketId = MarketId_Test;
	
	public TestSinaStock(){
		super();
	}
	
	public void setUp(String propFile){
		super.setProp(propFile);
		this.propFile = propFile;
		this.itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
	}
	
	private String propFile = "client1-v2.properties";
	private String itemsFolder;
	
	@Before
	public void setUp(){
		super.setProp(propFile);
		itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
	}
	
	public String[] getStockIdByMarketId(String marketId){
		if (MarketId_Test.equals(marketId)){
			return new String[]{"sh600000", "sh601766", "sz000001"};
		}else{
			CrawledItem ci = cconf.getDsm("hbase").getCrawledItem(marketId, StockConfig.SINA_STOCK_IDS, null);
			ci.fromParamData();
			List<String> ids = (List<String>) ci.getParam("ids");
			String[] idarray = new String[ids.size()];
			idarray = ids.toArray(idarray);
			return idarray;
		}
	}
	
	public void run_all_id_by_task(String marketId, String confName, Map<String, Object> params, String outputDir){
		String[] idarray = getStockIdByMarketId(marketId);
		String confFileName = confName + ".xml";
		List<Task> tl = new ArrayList<Task>();
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<idarray.length; i++){
			String sid = idarray[i];
			String stockid = sid.substring(2);
			for (Task t: tl){
				Task t1 = t.clone(getClass().getClassLoader());
				t1.putAllParams(params);
				t1.putParam("stockid", stockid);
				tlist.add(t1);
			}
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, outputDir);
	}
	/***
	 * Stock ids
	 * */
	//get all stockids by market
	@Test
	public void run_browse_idlist() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category_id", "hs_a");
		browsePrd(StockConfig.SINA_STOCK_IDS + ".xml", null, params);
	}
	
	/****
	 * financial report
	 */
	//crawl financial report history by market to hdfs
	@Test
	public void run_browse_fr_history() throws Exception {
		//set output to null, needs follow up etl
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_FR_HISTORY, null, null);
		
	}
	//fr history convert to csv/hive
	@Test
	public void run_convert_fr_history_tabular_to_csv() throws Exception {
		String[] ids = getStockIdByMarketId(marketId);
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.length; i++){
			String sid = ids[i];
			String stockid = sid.substring(2);
			for (String subFR: StockConfig.subFR){
				TabularCSVConvertTask ct = new TabularCSVConvertTask(stockid, 
						StockConfig.SINA_STOCK_FR_HISTORY + "/" + subFR, 
						StockConfig.SINA_STOCK_FR_HISTORY_OUT+ "/" + subFR);
				tlist.add(ct);
			}
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	//fr history reformat from split by stockid to split by quarter
	@Test
	public void run_fr_reformat() throws Exception{
		for (String subFR: StockConfig.subFR){
			CsvReformatMapredLauncher.format(this.getPropFile(), 
					itemsFolder + "/" + StockConfig.SINA_STOCK_FR_HISTORY_OUT + "/" + subFR, 
					1, 
					itemsFolder  + "/" + StockConfig.SINA_STOCK_FR_HISTORY_QUARTER_OUT + "/" + subFR);
		}
	}
	public void run_browse_fr_quarter(int year, int quarter){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("year", year);
		params.put("quarter", quarter);
		for (String subFR:StockConfig.subFR){
			String confFileName = StockConfig.SINA_STOCK_FR_QUARTER + "-" + subFR;
			String outputDir = StockConfig.SINA_STOCK_FR_QUARTER + "-" + subFR + "/" + year + "_" + quarter;
			run_all_id_by_task(marketId, confFileName, params, outputDir);
		}
	}
	//crawl finance report for specific quarter(s)
	@Test
	public void run_browse_fr_quarter() throws Exception {
		int year = 2015;
		int quarter = 1;
		run_browse_fr_quarter(year, quarter);
	}
	
	/****
	 * Corp info
	 * */
	//crawl corp info to hbase, we need this
	@Test
	public void run_browse_corp_info() throws Exception {
		//set output to null needs followup, can be merged, actually we need hbase data
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_CORP_INFO, null, null);
	}
	
	//convert corp info to hdfs/hive
	@Test
	public void run_corp_info_to_csv() throws Exception{
		HBaseToCSVMapperLauncher.genCSVFromHbase(this.getPropFile(), 
				itemsFolder + "/" + StockConfig.SINA_STOCK_CORP_INFO, 
				StockConfig.SINA_STOCK_CORP_INFO, 
				null, 
				"org.cld.stock.sina.CorpInfoToCSV");
	}
	
	/*****
	 * Market history
	 **/
	//crawl market history to hdfs/hive
	@Test
	public void run_browse_market_history() {
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYearQuarter(ids, cconf, this.getPropFile(), StockConfig.SINA_STOCK_MARKET_HISTORY);
	}
	
	//merge all stocks' market history into one file per quarter
	@Test
	public void run_merge_market_history() throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(cconf, 1990, 1, 2015, 3);
	}
	
	//crawl market for specific quarter(s) to hdfs/hive
	public void run_browse_market_quarter(int year, int quarter){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("year", year);
		params.put("quarter", quarter);
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_MARKET_HISTORY, params, null);
	}
	@Test
	public void run_browse_market_quarter() throws Exception {
		int year=2015;
		int quarter=2;
		run_browse_market_quarter(year, quarter);
	}
	@Test
	public void run_browse_market_cur_quarter(){
		int[] cyq = DateTimeUtil.getYearQuarter(new Date());
		run_browse_market_quarter(cyq[0], cyq[1]);
	}
	public void run_merge_market_history_quarter(int year, int quarter) throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(cconf, year, quarter, year, quarter);
	}
	
	/******
	 * FR FootNote
	 */
	@Test
	public void run_browse_fr_footnote_history(){
		//need follow up
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_FR_FOOTNOTE, null, null);
	}
	@Test
	public void run_fr_footnote_to_csv(){
		for (String subArea:FRFootNoteToCSV.FootNoteSubArea){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(FRFootNoteToCSV.FR_FOOTNOTE_ATTR_NAME, subArea);
			HBaseToCSVMapperLauncher.genCSVFromHbase(
					this.getPropFile(), 
					itemsFolder + "/" + StockConfig.SINA_STOCK_FR_FOOTNOTE + "/" + subArea, 
					StockConfig.SINA_STOCK_FR_FOOTNOTE, 
					null, 
					"org.cld.stock.sina.FRFootNoteToCSV", 
					params);
		}
	}
	
	/***
	 * Achievement Notice
	 */
	@Test
	public void run_fr_achievenotice(){
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_FR_AchieveNotice, null, 
				StockConfig.SINA_STOCK_FR_AchieveNotice);
	}
	
	/***
	 * Finance Guideline
	 * */
	@Test
	public void run_fr_finance_guideline(){
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYear(ids, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, 
				StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR);
	}
	
	/***
	 * Asset Devalue
	 * */
	@Test
	public void run_fr_assetdevalue(){
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYear(ids, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, 
				StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR);
	}
	
	/***
	 * Befenit Change 股东权益增减 data has some problem only 2013 and 2014
	 * */
	
	/***
	 * Bad Account same as Asset Devalue
	 * */
	
	/**
	 * Stock Structure
	 */
	@Test
	public void run_fr_stock_structure(){
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYear(ids, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_STOCK_STRUCTURE, 
				StockConfig.SINA_STOCK_STOCK_STRUCTURE);
	}
	//////Tests
	//show title
	@Test
	public void test_sina_convert_txt_to_csv_with_header() throws Exception {
		String[] sids = new String[]{"600000","601766"};
		for (String sid:sids){
			TabularCSVConvertTask ct = new TabularCSVConvertTask(sid, true);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
			ct.runMyself(params, null);
		}
	}
	
	@Test
	public void test_browse_fr_footnote() throws Exception {
		String[] sids = new String[]{"600000","601766"};
		for (String sid:sids){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stockid", sid);
			params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
			browsePrd(StockConfig.SINA_STOCK_FR_FOOTNOTE+".xml", null, params);
		}
	}
	
	//test browse sina-stock-market-history bpt
	@Test
	public void test_sina_browse_prd_market_history() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_MARKET_HISTORY+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("year", "1999");
		params.put("quarter", "4");
		browsePrd(StockConfig.SINA_STOCK_MARKET_HISTORY+".xml", null, params);
		params.put("year", "2014");
		browsePrd(StockConfig.SINA_STOCK_MARKET_HISTORY+".xml", null, params);
	}
	
	//test browse single corp-info bpt
	@Test
	public void test_browse_corp_info() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_CORP_INFO +".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		browsePrd(StockConfig.SINA_STOCK_CORP_INFO +".xml", null, params);
	}
	//test browse achievement notice
	@Test
	public void test_browse_achieve_notice() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_FR_AchieveNotice +".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		browsePrd(StockConfig.SINA_STOCK_FR_AchieveNotice +".xml", null, params);
	}
	
	//test browse single stock quarter fr
	@Test
	public void test_sina_browse_financial_report_quarter() throws Exception {
		cconf.setUpSite(StockConfig.SINA_STOCK_FR_QUARTER+"-" + StockConfig.subFR[0] +".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("year", 2015);
		params.put("quarter", 2);
		browsePrd(StockConfig.SINA_STOCK_FR_QUARTER+"-" + StockConfig.subFR[0] +".xml", null, params);
		params.put("quarter", 1);
		browsePrd(StockConfig.SINA_STOCK_FR_QUARTER+"-" + StockConfig.subFR[0] +".xml", null, params);
	}
	
	
	public static final String browse_market_quarter = "brs-mkt-qut";
	public static final String merge_market_quarter = "mrg-mkt-qut";
	public static final String browse_fr_quarter = "brs-fr-qut";
	
	public static void main(String[] args) throws Exception {
		int year = 2015;
		int quarter = 1;
		String cmd="";
		String propFile="";
		if (args.length>=1){
			propFile = args[0];
		}
		TestSinaStock tss = new TestSinaStock();
		tss.setProp(propFile);
		
		if (args.length>=2){
			cmd = args[1];
		}
		if (args.length>=3){
			year = Integer.parseInt(args[2]);
			quarter = Integer.parseInt(args[3]);
		}else{
			if (cmd.endsWith("-qut")){
				logger.info("please input year, quarter");
				return;
			}
		}
		
		if (browse_market_quarter.equals(cmd)){
			tss.run_browse_market_quarter(year, quarter);
		}else if (merge_market_quarter.equals(cmd)){
			tss.run_merge_market_history_quarter(year, quarter);
		}else if (browse_fr_quarter.equals(cmd)){
			tss.run_browse_fr_quarter(year, quarter);
		}
	}
}
