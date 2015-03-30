package org.cld.sites.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.load.CNBasicLoad;
import org.cld.stock.load.StockConst;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.junit.Test;

public class TestStock extends TestBase{
	
	public static final String SHSE_STOCK_BASICINFO="shse-stock-basic.xml";
	public static final String SHSE_PRDNAME_SEMIANNUAL="shprd_semiannual";
	public static final String SHSE_PRDNAME_QUARTER="shprd_quarter";
	
	public static final String SZSE_STOCK_BASICINFO="szse-stock-basic.xml";
	public static final String HKSE_STOCK_BASICINFO="hkse-stock-basic.xml";
	
	public TestStock(){
		super();
	}
	
	@Test
	public void run_shse_prd() throws InterruptedException{
		cconf.setUpSite(SHSE_STOCK_BASICINFO, null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbal", "600012");
		List<String> periodList = new ArrayList<String>();
		periodList.add(StockConst.sh_periodid_12m);
		periodList.add(StockConst.sh_periodid_6m);
		params.put("periodId", periodList);
		browsePrd(SHSE_STOCK_BASICINFO, null, SHSE_PRDNAME_SEMIANNUAL, params);
		
		periodList.clear();
		periodList.add(StockConst.sh_periodid_3m);
		periodList.add(StockConst.sh_periodid_9m);
		browsePrd(SHSE_STOCK_BASICINFO, null, SHSE_PRDNAME_QUARTER, params);
	}
	
	@Test
	public void run_shse_bct() throws Exception{
		catNavigate(SHSE_STOCK_BASICINFO, null, CrawlTestUtil.BROWSE_CAT_TYPE_RECURSIVE);	
	}
	
	@Test
	public void run_szse_prd() throws InterruptedException{
		cconf.setUpSite(SZSE_STOCK_BASICINFO, null);
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> periodList = new ArrayList<String>();
		periodList.add(StockConst.sz_periodid_12m);
		params.put("symbal", "002002");
		params.put("periodId", periodList);//report type
		params.put("startyear", "2010");
		browsePrd(SZSE_STOCK_BASICINFO, null, params);
	}
	
	@Test
	public void run_szse_bdt() throws Exception{
		//the totalpagenum variable will not be evaluated, using bct
		runBDT(SZSE_STOCK_BASICINFO, null, true);	
	}
	
	@Test
	public void run_szse_bct() throws Exception{
		catNavigate(SZSE_STOCK_BASICINFO, null, CrawlTestUtil.BROWSE_CAT_TYPE_RECURSIVE);	
	}
	
	@Test
	public void run_hkse_prd() throws InterruptedException{
		cconf.setUpSite(HKSE_STOCK_BASICINFO, null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbal", "00001");
		browsePrd(HKSE_STOCK_BASICINFO, null, params);
	}
	
	@Test
	public void run_hkse_bct() throws Exception{
		catNavigate(HKSE_STOCK_BASICINFO, null, CrawlTestUtil.BROWSE_CAT_TYPE_RECURSIVE);	
	}
	
	@Test
	public void run_stock_transform() throws Exception {
		String outputFile = "/output";
		FileSystem fs = FileSystem.get(HadoopTaskUtil.getHadoopConf(cconf.getNodeConf()));
		fs.delete(new Path(outputFile), true);
		CNBasicLoad.loadHiveFromHbase(this.getPropFile(), outputFile);
	}
}
