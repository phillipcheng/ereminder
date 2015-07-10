package org.cld.sites.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TabularCSVConvertTask;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil.browse_type;
import org.cld.datacrawl.test.TestBase;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.sina.ETLUtil;
import org.cld.stock.sina.StockConfig;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.etl.csv.CsvReformatMapredLauncher;
import org.etl.fci.HBaseToCSVMapperLauncher;
import org.junit.Before;
import org.junit.Test;

public class TestSinaStock extends TestBase{
	
	public TestSinaStock(){
		super();
	}
	
	public void setUp(String propFile){
		super.setProp(propFile);
		this.propFile = propFile;
		this.itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
	}
	
	private String propFile = "client1-v2-cluster.properties";
	private String itemsFolder;
	
	@Before
	public void setUp(){
		super.setProp(propFile);
		itemsFolder = this.cconf.getTaskMgr().getHadoopCrawledItemFolder();
	}
	
	//get all stockids by market
	@Test
	public void run_browse_idlist() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category_id", "hs_a");
		browsePrd(StockConfig.SINA_STOCK_IDS + ".xml", null, params);
	}
	//crawl financial report history by market to hdfs
	@Test
	public void run_browse_fr_history() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		List<String> ids = (List<String>) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.size(); i++){
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_FR_HISTORY, 
					StockConfig.SINA_STOCK_FR_HISTORY + ".xml");
			String sid = ids.get(i);
			String stockid = sid.substring(2);
			ttc.putParam("stockid", stockid);
			tlist.add(ttc);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	//fr history convert to csv/hive
	@Test
	public void run_convert_fr_history_tabular_to_csv() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		List<String> ids = (List<String>) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.size(); i++){
			String sid = ids.get(i);
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
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		List<String> jsarry = (List<String>) ci.getParam("ids");
		
		for (String subFR:StockConfig.subFR){
			List<Task> tlist = new ArrayList<Task>();
			for (int i=0; i<jsarry.size(); i++){
				String sid = jsarry.get(i);
				String stockid = sid.substring(2);
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("stockid", stockid);
				paramMap.put("year", year);
				paramMap.put("quarter", quarter);
				
				String confFileName = StockConfig.SINA_STOCK_FR_QUARTER + "-" + subFR + ".xml";
				List<Task> tl = new ArrayList<Task>();
				if (confFileName!=null){
					tl = cconf.setUpSite(confFileName, null);
				}
				Task t = tl.get(0);
				t.putAllParams(paramMap);
				tlist.add(t);
			}
			CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist, 
					StockConfig.SINA_STOCK_FR_QUARTER + "-" + subFR + "/" + year + "_" + quarter);
		}
	}
	//crawl finance report for specific quarter(s)
	@Test
	public void run_browse_fr_quarter() throws Exception {
		int year = 2015;
		int quarter = 1;
		run_browse_fr_quarter(year, quarter);
	}
	
	//crawl corp info to hbase, we need this
	@Test
	public void run_browse_corp_info() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		List<String> ids = (List<String>) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.size(); i++){
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, StockConfig.SINA_STOCK_CORP_INFO, StockConfig.SINA_STOCK_CORP_INFO + ".xml");
			String sid = ids.get(i);
			String stockid = sid.substring(2);
			ttc.putParam("stockid", stockid);
			tlist.add(ttc);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
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
	
	//crawl market history to hdfs/hive
	@Test
	public void run_browse_market_history() throws Exception {
		ETLUtil.getMarketHistory(cconf, this.getPropFile(), "hs_a");
	}
	
	//merge all stocks' market history into one file per quarter
	@Test
	public void run_merge_market_history() throws Exception {
		ETLUtil.mergeMarketHistoryByQuarter(cconf, 1990, 1, 2015, 3);
	}
	
	//crawl market for specific quarter(s) to hdfs/hive
	public void run_browse_market_quarter(int year, int quarter){
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", StockConfig.SINA_STOCK_IDS, null);
		ci.fromParamData();
		List<String> ids = (List<String>) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<ids.size(); i++){
			String sid = ids.get(i);
			String stockid = sid.substring(2);
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, 
					StockConfig.SINA_STOCK_MARKET_HISTORY, StockConfig.SINA_STOCK_MARKET_HISTORY+".xml");
			ttc.putParam("stockid", stockid);
			ttc.putParam("year", year);
			ttc.putParam("quarter", quarter);
			tlist.add(ttc);
		}
		logger.info("sending out:" + tlist.size() + " tasks.");
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
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
	
	//show title
	@Test
	public void test_sina_convert_txt_to_csv_with_header() throws Exception {
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
	
	//test browse single corp-info bpt
	@Test
	public void test_sina_browse_prd_intro() throws Exception{
		cconf.setUpSite(StockConfig.SINA_STOCK_CORP_INFO+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		browsePrd(StockConfig.SINA_STOCK_CORP_INFO+".xml", null, params);
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
