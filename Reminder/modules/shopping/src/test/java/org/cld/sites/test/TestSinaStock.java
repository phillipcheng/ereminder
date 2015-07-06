package org.cld.sites.test;

import java.text.SimpleDateFormat;
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
import org.cld.stock.sina.SinaStockCorpInfoToCSV;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

public class TestSinaStock extends TestBase{
	
	public static final String SINA_STOCK_IDS ="sina-stock-ids";
	public static final String SINA_STOCK_FINANCIAL_REPORT="sina-stock-financial-report";
	public static final String SINA_STOCK_MARKET_HISTORY="sina-stock-market-history";
	public static final String SINA_STOCK_CORP_INFO="sina-stock-corp-info";
	
	public static final int IPO_DATE_IDX = 7;
	public static final int FOUND_DATE_IDX=13;
	
	public TestSinaStock(){
		super();
	}
	
	private String propFile = "client1-v2.properties";
	
	@Before
	public void setUp(){
		super.setProp(propFile);
	}
	
	//get all stockids
	@Test
	public void run_sina_browse_idlist() throws Exception{
		cconf.setUpSite(SINA_STOCK_IDS + ".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category_id", "hs_a");
		browsePrd(SINA_STOCK_IDS + ".xml", null, params);
	}
	//crawl financial report to hdfs
	@Test
	public void run_sina_browse_prd_financial_report() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, SINA_STOCK_FINANCIAL_REPORT, 
					SINA_STOCK_FINANCIAL_REPORT + ".xml");
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			ttc.putParam("stockid", stockid);
			tlist.add(ttc);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	//convert to csv/hive
	@Test
	public void run_sina_convert_financial_report_txt_to_csv() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			TabularCSVConvertTask ct = new TabularCSVConvertTask(stockid);
			tlist.add(ct);
		}
		CrawlUtil.hadoopExecuteCrawlTasks(this.getPropFile(), cconf, tlist);
	}
	
	//crawl corp info to hbase
	@Test
	public void run_sina_browse_stock_corp_info() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		for (int i=0; i<jsarry.length(); i++){
			TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, SINA_STOCK_CORP_INFO, SINA_STOCK_CORP_INFO + ".xml");
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
		HBaseToCSVMapperLauncher.genCSVFromHbase(this.getPropFile(), "/reminder/items/"+SINA_STOCK_CORP_INFO, SINA_STOCK_CORP_INFO, "org.cld.stock.sina.SinaStockCorpInfoToCSV");
	}
	
	//crawl market history to hdfs/hive
	@Test
	public void run_sina_browse_prd_market_history() throws Exception {
		CrawledItem ci = cconf.getDsm("hbase").getCrawledItem("hs_a", SINA_STOCK_IDS, null);
		ci.fromParamData();
		JSONArray jsarry = (JSONArray) ci.getParam("ids");
		List<Task> tlist = new ArrayList<Task>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i=0; i<jsarry.length(); i++){
			String sid = jsarry.getString(i);
			String stockid = sid.substring(2);
			//get the IPODate
			CrawledItem corpInfo = cconf.getDsm("hbase").getCrawledItem(stockid, SINA_STOCK_CORP_INFO, null);
			JSONArray jsa = (JSONArray)corpInfo.getParam(SinaStockCorpInfoToCSV.FIELD_NAME_ATTR);
			String ipoDateStr = jsa.getString(IPO_DATE_IDX).trim();
			String foundDateStr = jsa.getString(FOUND_DATE_IDX).trim();
			String dateUsed = ipoDateStr;
			int[] yq = DateTimeUtil.getYearQuarter(ipoDateStr, sdf);
			if (yq==null){
				logger.warn("wrong ipo date found:" + ipoDateStr + ", for stock:" + stockid + ", try found date:" + foundDateStr);
				yq = DateTimeUtil.getYearQuarter(foundDateStr, sdf);
				dateUsed = foundDateStr;
			}
			int[] cyq = DateTimeUtil.getYearQuarter(new Date());
			if (yq!=null){
				int year;
				int quarter;
				for (year=yq[0]; year<=cyq[0]; year++){
					int startQ;
					int endQ;
					if (cyq[0]==yq[0]){//IPODate and CurrentDate same year
						startQ=yq[1];
						endQ =cyq[1];
					}else{
						if (year==yq[0]){//for the IPO year
							startQ = yq[1];
							endQ=4;
						}else if (year==cyq[0]){//for the current year
							startQ=1;
							endQ=cyq[1];
						}else{//for any year between
							startQ=1;
							endQ=4;
						}
					}
					for (quarter=startQ;quarter<=endQ;quarter++){
						TestTaskConf ttc = new TestTaskConf(false, browse_type.bpt, 
								SINA_STOCK_MARKET_HISTORY, SINA_STOCK_MARKET_HISTORY+".xml");
						ttc.putParam("stockid", stockid);
						ttc.putParam("year", year+"");
						ttc.putParam("quarter", quarter+"");
						tlist.add(ttc);
						//logger.info("add ttc with param:" + stockid + "," + year + "," + quarter);
					}
				}
			}else{
				logger.error("stock:" + stockid + ", with wrong date:" + dateUsed);
			}
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
		cconf.setUpSite(SINA_STOCK_MARKET_HISTORY+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "600000");
		params.put("year", "1999");
		params.put("quarter", "4");
		browsePrd(SINA_STOCK_MARKET_HISTORY+".xml", null, params);
		params.put("year", "2014");
		browsePrd(SINA_STOCK_MARKET_HISTORY+".xml", null, params);
		
	}
	
	//test browse sina-stock-intro bpt
	@Test
	public void test_sina_browse_prd_intro() throws Exception{
		cconf.setUpSite(SINA_STOCK_CORP_INFO+".xml", null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stockid", "000001");
		browsePrd(SINA_STOCK_CORP_INFO+".xml", null, params);
	}
}
