package org.cld.stock.sina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.util.DateTimeUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.etl.csv.CsvReformatMapredLauncher;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.test.TestBase;
import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.StockConfig;

public class SinaStockBase extends TestBase{
	

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	
	public static final int HS_A_START_YEAR=1990;
	public static final String HS_A_FIRST_DATE_DETAIL_TRADE="2004-10-1";
	public static final String HS_A_FIRST_DATE_RZRQ="2012-11-12";
	
	private String marketId = ETLUtil.MarketId_Test;
	private String propFile = "client1-v2.properties";
	private String itemsFolder;
	
	public SinaStockBase(String propFile, String marketId){
		super();
		this.marketId = marketId;
		super.setProp(propFile);
		this.propFile = propFile;
		this.itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
	}
	
	public CrawlConf getCconf(){
		return this.cconf;
	}
	
	/***
	 * Stock ids
	 * */
	//get all stockids by market
	public void run_browse_idlist() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category_id", "hs_a");
		browsePrd(StockConfig.SINA_STOCK_IDS + ".xml", null, params);
	}

	/*****
	 * 行情走势
	 **/
	//成交明细
	public void run_browse_tradedetail(String fromDate) throws ParseException{
		Date minStartDate = ETLUtil.sdf.parse(fromDate);
		ETLUtil.runTaskByMarketIdStartDate(marketId, cconf, this.propFile, 
				StockConfig.SINA_STOCK_TRADE_DETAIL, minStartDate);
	}
	
	//融资融券
	public void run_browse_market_rzrq(String fromDate) throws ParseException{
		Date startDate = ETLUtil.sdf.parse(fromDate); 
		ETLUtil.runTaskByStartDate(startDate, cconf, this.getPropFile(), StockConfig.SINA_STOCK_MARKET_RZRQ, 
				null, StockConfig.SINA_STOCK_MARKET_RZRQ);
	}
	
	//历史交易
	//crawl market history to hdfs/hive
	public void run_browse_market_history() {
		ETLUtil.runTaskByMarketIdStartQuarter(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_MARKET_HISTORY);
	}
	
	//merge all stocks' market history into one file per quarter
	public void run_merge_market_history() throws Exception {
		int[] cyq = DateTimeUtil.getYearQuarter(new Date());
		ETLUtil.mergeMarketHistoryByQuarter(cconf, HS_A_START_YEAR, 1, cyq[0], cyq[1]);
	}
	
	//crawl market for specific quarter(s) to hdfs/hive
	public void run_browse_market_quarter(int year, int quarter){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("year", year);
		params.put("quarter", quarter);
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_MARKET_HISTORY, params, null);
	}
	public void run_browse_market_cur_quarter(){
		int[] cyq = DateTimeUtil.getYearQuarter(new Date());
		run_browse_market_quarter(cyq[0], cyq[1]);
	}
	public void run_merge_market_history_quarter(int year, int quarter) throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(cconf, year, quarter, year, quarter);
	}
	
	
	/****
	 * 公司资料
	 * */
	//Corp info: crawl corp info to hbase and batch csv
	public void run_browse_corp_info() throws Exception {
		//set output to null needs followup, can be merged, actually we need hbase data
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_CORP_INFO, 
				null, StockConfig.SINA_STOCK_CORP_INFO);
	}
	
	//Corp Manager
	public void run_corp_manager(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", allHistory);
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_CORP_MANAGER, 
				paramMap, StockConfig.SINA_STOCK_CORP_MANAGER);
	}
	
	//Corp Related
	public void run_corp_related(){
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_CORP_RELATED, 
				null, StockConfig.SINA_STOCK_CORP_RELATED);
	}
	
	//Corp Related other
	public void run_corp_related_other(){
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_CORP_RELATED_OTHER, 
				null, StockConfig.SINA_STOCK_CORP_RELATED_OTHER);
	}
	
	/***
	 * 发行分配
	 */
	public void run_issue_sharebonus(){
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_ISSUE_SHAREBONUS, 
				null, StockConfig.SINA_STOCK_ISSUE_SHAREBONUS);
	}
	
	/***********
	 * 股本股东
	 */
	//Stock Structure
	public void run_stock_structure(){
		ETLUtil.runTaskByMarketIdStartYear(marketId, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_STOCK_STRUCTURE, 
				StockConfig.SINA_STOCK_STOCK_STRUCTURE);
	}
	
	//Stock holder
	public void run_stock_holder(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", allHistory);
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_STOCK_HOLDER, 
				paramMap, StockConfig.SINA_STOCK_STOCK_HOLDER);
	}
	
	//Circulate Stock holder
	public void run_circulate_stock_holder(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", allHistory);
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, 
				paramMap, StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE);
	}

	//Fund Stock holder
	public void run_fund_stock_holder(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", true);
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, 
				paramMap, StockConfig.SINA_STOCK_STOCK_HOLDER_FUND);
	}
	
	
	/****
	 * 财务数据
	 */
	//crawl financial report history by market to hdfs
	public void run_browse_fr_history() throws Exception {
		//set output to null, needs follow up etl
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_FR_HISTORY, null, null);
		
	}
	//fr history convert to csv/hive
	public void run_convert_fr_history_tabular_to_csv() throws Exception {
		String[] ids = ETLUtil.getStockIdByMarketId(marketId, cconf);
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
		//TODO fill the mapred task name
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, null, null);
	}
	//fr history reformat from split by stockid to split by quarter
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
			ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), confFileName, params, outputDir);
		}
	}
	
	//FR FootNote
	public void run_browse_fr_footnote_history(){
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_FR_FOOTNOTE, 
				null, StockConfig.SINA_STOCK_FR_FOOTNOTE);
	}
	
	//Achievement Notice
	public void run_fr_achievenotice(){
		ETLUtil.runTaskByMarket(marketId, cconf, this.getPropFile(), StockConfig.SINA_STOCK_FR_AchieveNotice, null, 
				StockConfig.SINA_STOCK_FR_AchieveNotice);
	}
	
	//Finance Guideline
	public void run_fr_finance_guideline(){
		ETLUtil.runTaskByMarketIdStartYear(marketId, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, 
				StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR);
	}
	
	//Asset Devalue
	public void run_fr_assetdevalue(){
		ETLUtil.runTaskByMarketIdStartYear(marketId, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, 
				StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR);
	}
}
