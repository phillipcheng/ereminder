package org.cld.stock.sina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cld.util.DateTimeUtil;
import org.cld.datastore.entity.CrawledItem;
import org.cld.taskmgr.entity.Task;
import org.cld.etl.csv.CsvReformatMapredLauncher;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.test.TestBase;
import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.StockConfig;

public class SinaStockBase extends TestBase{
	
	public static final String MarketId_Test = "test";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static final String MarketId_HS_A ="hs_a"; //hu sheng A gu
	public static final int HS_A_START_YEAR=1990;
	public static final String HS_A_FIRST_DATE_DETAIL_TRADE="2004-10-1";
	public static final String HS_A_FIRST_DATE_RZRQ="2012-11-12";
	
	private String marketId = MarketId_Test;
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

	public void run_all_date_by_task(Date startDate, String confName, 
			Map<String, Object> params, String outputDir){
		List<Task> tl = new ArrayList<Task>();
		String confFileName = confName + ".xml";
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		Date toDate = new Date();
		LinkedList<Date> dll = DateTimeUtil.getWorkingDayList(startDate, toDate);
		if (DateTimeUtil.isWorkingDay(toDate)){
			dll.add(toDate);
		}
		List<Task> tlist = new ArrayList<Task>();
		Iterator<Date> tryDates = dll.listIterator();
		while(tryDates.hasNext()){
			Date d = tryDates.next();
			logger.debug("try date:" + d);
			String dstr = sdf.format(d);
			for (Task t: tl){
				Task t1 = t.clone(getClass().getClassLoader());
				if (params!=null)
					t1.putAllParams(params);
				t1.putParam("date", dstr);
				tlist.add(t1);
			}
		}
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(this.propFile, cconf, tlist, null, outputDir);
	}
	
	public void run_all_id_by_task(String marketId, String confName, 
			Map<String, Object> params, String outputDir){
		String[] idarray = getStockIdByMarketId(marketId);
		String confFileName = confName + ".xml";
		List<Task> tl = new ArrayList<Task>();
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<idarray.length; i++){
			String stockid = idarray[i];
			stockid = stockid.substring(2);
			
			for (Task t: tl){
				Task t1 = t.clone(getClass().getClassLoader());
				if (params!=null)
					t1.putAllParams(params);
				t1.putParam("stockid", stockid);
				tlist.add(t1);
			}
		}
		//TODO fill the mapreduce task name
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, null, outputDir);
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
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartDate(ids, cconf, this.propFile, 
				StockConfig.SINA_STOCK_TRADE_DETAIL, minStartDate);
	}
	
	//融资融券
	public void run_browse_market_rzrq(String fromDate) throws ParseException{
		Date startDate = ETLUtil.sdf.parse(fromDate); 
		run_all_date_by_task(startDate, StockConfig.SINA_STOCK_MARKET_RZRQ, 
				null, StockConfig.SINA_STOCK_MARKET_RZRQ);
	}
	
	//历史交易
	//crawl market history to hdfs/hive
	public void run_browse_market_history() {
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYearQuarter(ids, cconf, this.getPropFile(), StockConfig.SINA_STOCK_MARKET_HISTORY);
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
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_MARKET_HISTORY, params, null);
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
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_CORP_INFO, 
				null, StockConfig.SINA_STOCK_CORP_INFO);
	}
	
	//Corp Manager
	public void run_corp_manager(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", allHistory);
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_CORP_MANAGER, 
				paramMap, StockConfig.SINA_STOCK_CORP_MANAGER);
	}
	
	//Corp Related
	public void run_corp_related(){
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_CORP_RELATED, 
				null, StockConfig.SINA_STOCK_CORP_RELATED);
	}
	
	//Corp Related other
	public void run_corp_related_other(){
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_CORP_RELATED_OTHER, 
				null, StockConfig.SINA_STOCK_CORP_RELATED_OTHER);
	}
	
	/***
	 * 发行分配
	 */
	public void run_issue_sharebonus(){
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_ISSUE_SHAREBONUS, 
				null, StockConfig.SINA_STOCK_ISSUE_SHAREBONUS);
	}
	
	/***********
	 * 股本股东
	 */
	//Stock Structure
	public void run_stock_structure(){
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYear(ids, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_STOCK_STRUCTURE, 
				StockConfig.SINA_STOCK_STOCK_STRUCTURE);
	}
	
	//Stock holder
	public void run_stock_holder(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", allHistory);
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_STOCK_HOLDER, 
				paramMap, StockConfig.SINA_STOCK_STOCK_HOLDER);
	}
	
	//Circulate Stock holder
	public void run_circulate_stock_holder(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", allHistory);
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE, 
				paramMap, StockConfig.SINA_STOCK_STOCK_HOLDER_CIRCULATE);
	}

	//Fund Stock holder
	public void run_fund_stock_holder(boolean allHistory){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("history", true);
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_STOCK_HOLDER_FUND, 
				paramMap, StockConfig.SINA_STOCK_STOCK_HOLDER_FUND);
	}
	
	
	/****
	 * 财务数据
	 */
	//crawl financial report history by market to hdfs
	public void run_browse_fr_history() throws Exception {
		//set output to null, needs follow up etl
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_FR_HISTORY, null, null);
		
	}
	//fr history convert to csv/hive
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
			run_all_id_by_task(marketId, confFileName, params, outputDir);
		}
	}
	
	//FR FootNote
	public void run_browse_fr_footnote_history(){
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_FR_FOOTNOTE, 
				null, StockConfig.SINA_STOCK_FR_FOOTNOTE);
	}
	
	//Achievement Notice
	public void run_fr_achievenotice(){
		run_all_id_by_task(marketId, StockConfig.SINA_STOCK_FR_AchieveNotice, null, 
				StockConfig.SINA_STOCK_FR_AchieveNotice);
	}
	
	//Finance Guideline
	public void run_fr_finance_guideline(){
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYear(ids, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR, 
				StockConfig.SINA_STOCK_FR_FINANCE_GUIDELINE_YEAR);
	}
	
	//Asset Devalue
	public void run_fr_assetdevalue(){
		String[] ids = getStockIdByMarketId(marketId);
		ETLUtil.getDataFromStartYear(ids, cconf, this.getPropFile(), 
				StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR, 
				StockConfig.SINA_STOCK_FR_ASSETDEVALUE_YEAR);
	}
}
