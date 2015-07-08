package org.cld.sites.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil.browse_type;
import org.cld.datacrawl.test.TestBase;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.load.HBaseToCSVMapperLauncher;
import org.cld.stock.load.TabularCSVConvertTask;
import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.StockConfig;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

public class TestSinaStock extends TestBase{
	
	public TestSinaStock(){
		super();
	}
	
	private String propFile = "client1-v2-cluster.properties";
	
	@Before
	public void setUp(){
		super.setProp(propFile);
	}
	
	//get all stockids by market
	@Test
	public void run_sina_browse_idlist() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category_id", "hs_a");
		browsePrd(StockConfig.SINA_STOCK_IDS + ".xml", null, params);
	}
	//crawl financial report history by market to hdfs
	@Test
	public void run_sina_browse_fr_history() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_FR_HISTORY, 
					StockConfig.SINA_STOCK_FR_HISTORY + ".xml");
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			ttc.putParam("stockid", stockid);
			tlist.add(ttc);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	//convert to csv/hive
	@Test
	public void run_sina_convert_fr_history_tabular_to_csv() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			TabularCSVConvertTask ct = new TabularCSVConvertTask(stockid, StockConfig.SINA_STOCK_FR_HISTORY);
			tlist.add(ct);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	//crawl finance report for specific quarter(s)
	@Test
	public void run_sina_browse_fr_quarter() throws Exception {
		int year = 2014;
		int quarter = 2;
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("stockid", stockid);
			paramMap.put("year", year);
			paramMap.put("quarter", quarter);
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER, 
					StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER + ".xml");
			ttc.putAllParams(paramMap);
			tlist.add(ttc);
			ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_FR_CASHFLOW_QUARTER, 
					StockConfig.SINA_STOCK_FR_CASHFLOW_QUARTER + ".xml");
			ttc.putAllParams(paramMap);
			tlist.add(ttc);
			ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_FR_PROFITSTATEMENT_QUARTER, 
					StockConfig.SINA_STOCK_FR_PROFITSTATEMENT_QUARTER + ".xml");
			ttc.putAllParams(paramMap);
			tlist.add(ttc);
			
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	//convert finance-report-quarter to hdfs/hive
	@Test
	public void run_sina_fr_quarter_to_csv() throws Exception{
		String year="2014";
		String quarter = "2";
		String idFilter = "([0-9]+)_"+year+"_"+quarter;
		HBaseToCSVMapperLauncher.genCSVFromHbase(this.getPropFile(), 
				"/reminder/items/"+ StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER +"/"+year+"_"+quarter, 
				StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER, idFilter, "org.cld.stock.sina.FinanceReportToCSV");
		HBaseToCSVMapperLauncher.genCSVFromHbase(this.getPropFile(), 
				"/reminder/items/"+ StockConfig.SINA_STOCK_FR_CASHFLOW_QUARTER +"/"+year+"_"+quarter, 
				StockConfig.SINA_STOCK_FR_CASHFLOW_QUARTER, idFilter, "org.cld.stock.sina.FinanceReportToCSV");
		HBaseToCSVMapperLauncher.genCSVFromHbase(this.getPropFile(), 
				"/reminder/items/"+ StockConfig.SINA_STOCK_FR_PROFITSTATEMENT_QUARTER +"/"+year+"_"+quarter, 
				StockConfig.SINA_STOCK_FR_PROFITSTATEMENT_QUARTER, idFilter, "org.cld.stock.sina.FinanceReportToCSV");
	}
	//crawl corp info to hbase
	@Test
	public void run_sina_browse_stock_corp_info() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_CORP_INFO, StockConfig.SINA_STOCK_CORP_INFO + ".xml");
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			ttc.putParam("stockid", stockid);
			tlist.add(ttc);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	
	//convert corp info to hdfs/hive
	@Test
	public void run_sina_prd_intro_to_csv() throws Exception{
		HBaseToCSVMapperLauncher.genCSVFromHbase(this.getPropFile(), "/reminder/items/"+StockConfig.SINA_STOCK_CORP_INFO, StockConfig.SINA_STOCK_CORP_INFO, null, "org.cld.stock.sina.CorpInfoToCSV");
	}
	
	//crawl market history to hdfs/hive
	@Test
	public void run_sina_browse_prd_market_history() throws Exception {
		ETLUtil.getMarketHistory(cconf, this.getPropFile(), "hs_a");
	}
	
	//merge all stocks' market history into one file per quarter
	@Test
	public void run_sina_merge_market_history() throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(cconf, 1990, 1, 2015, 3);
	}
	
	//crawl market for specific quarter(s) to hdfs/hive
	@Test
	public void run_sina_browse_prd_market_this_quarter() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			int[] cyq = DateTimeUtil.getYearQuarter(new Date());
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, 
					StockConfig.SINA_STOCK_MARKET_HISTORY, StockConfig.SINA_STOCK_MARKET_HISTORY+".xml");
			ttc.putParam("stockid", stockid);
			ttc.putParam("year", cyq[0]+"");
			ttc.putParam("quarter", cyq[1]+"");
			tlist.add(ttc);
		}
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	
	//show title
	@Test
	public void test_sina_convert_txt_to_csv_title() throws Exception {
		String sid = "600000";
		TabularCSVConvertTask ct = new TabularCSVConvertTask(sid, true);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
		ct.runMyself(params, null);
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
	
	//test browse sina-stock-intro bpt
	@Test
	public void test_sina_browse_prd_intro() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_CORP_INFO+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		browsePrd(StockConfig.SINA_STOCK_CORP_INFO+".xml", null, params);
	}
	
	@Test
	public void test_sina_browse_financial_report_quarter() throws Exception {
		cconf.setUpSite(StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("year", 2014);
		params.put("quarter", 2);
		browsePrd(StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER+".xml", null, params);
		params.put("stockid", "600007");
		browsePrd(StockConfig.SINA_STOCK_FR_BALANCESHEET_QUARTER+".xml", null, params);
	}
}
