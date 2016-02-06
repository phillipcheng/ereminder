package org.cld.sites.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.crbook.util.BookHandler;
import org.cld.datacrawl.test.TestBase;
import org.cld.taskmgr.entity.RunType;
import org.cld.util.CsvUtil;
import org.cld.util.DownloadUtil;
import org.cld.util.PatternIO;
import org.cld.util.PatternResult;
import org.cld.util.StringUtil;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.Product;
import org.junit.Before;
import org.junit.Test;

public class TestBooks extends TestBase {
	private static Logger logger =  LogManager.getLogger(TestBooks.class);
	private String propFile = "client1-v2.properties";
	
	public TestBooks(){
		super();
	}

	public static final String XRS52_CONF="52xrs.xml";
	public static final String A8Z8_CONF="a8z8.xml";
	public static final String DMZJ_CONF="dmzj.xml";
	public static final String CL_CONF="childrenslibrary.xml";
	public static final String FKB_CONF="freekidsbooks.xml";
	public static final String BAOLINY_CONF="baoliny.xml";

	public static final String CBO_CONF="cbo.xml";
	private String[] allConf = new String[]{
			XRS52_CONF,
			A8Z8_CONF, //register user only (paid)
			DMZJ_CONF,
			//CL_CONF,
			//FKB_CONF,
			//BAOLINY_CONF,
	};
	
	
	@Before
    public void setUp() throws Exception{
		this.setProp(propFile);
    }
	
	//
	public void testOneRoot(String rootConf) throws Exception{
		List<CrawledItem> cil = browsePrd(rootConf, null, "lvl1", RunType.onePath);
		logger.info(cil);
		List<String> csvs = CsvUtil.outputCsv(cil, null);
		logger.info(csvs);
	}
	
	@Test
	public void testOneRoot1() throws Exception{
		testOneRoot(XRS52_CONF);
	}
	@Test
	public void testOneRoot2() throws Exception{
		testOneRoot(A8Z8_CONF);
	}
	@Test
	public void testOneRoot3() throws Exception{
		testOneRoot(DMZJ_CONF);
	}
	
	//
	public void testOneCategory(String catConf, String url) throws Exception{
		List<CrawledItem> cil = browsePrd(catConf, url, "lvl2", RunType.oneLevel);
		logger.info(cil);
		List<String> csvs = CsvUtil.outputCsv(cil, null);
		logger.info(csvs);
	}
	@Test
	public void testOneCategory1() throws Exception{
		testOneCategory(XRS52_CONF, "http://www.52xrs.com/list/lsgs.htm");
	}
	
	@Test
	public void testOneCategory2() throws Exception{
		testOneCategory(DMZJ_CONF, "http://manhua.dmzj.com/gsmmx21/");
	}
	
	//
	public void testPrd(String bookConf, String url) throws Exception{
		List<CrawledItem> cil =browsePrd(bookConf, url, "prd", RunType.onePrd);
		logger.info(cil);
		List<String> csvs = CsvUtil.outputCsv(cil, null);
		logger.info(csvs);
	}
	
	@Test
	public void testPrd1() throws Exception {
		testPrd(XRS52_CONF, "http://www.52xrs.com/comic/2413/");
	}
	@Test
	public void testPrd2() throws Exception {
		testPrd(DMZJ_CONF, "http://manhua.dmzj.com/gsmmx21/18175.shtml");
	}
	
	
	public void xrsDownload(String bookSeries, int startBookIdx, int bookNumber) throws Exception{
		xrsDownload(bookSeries, startBookIdx, 0, bookNumber, false);
	}
	public void xrsDownload(String bookSeries, int startBookIdx, int startBookNumber, int endBookNumber, boolean onlyCover) throws Exception{
		ExecutorService exeService = Executors.newFixedThreadPool(20);
		String rootDir = "http://www.52xrs.com/comic/";
		String localRoot = "C:\\mydoc\\picbook";
		for (int i=startBookNumber; i<endBookNumber; i++){
			int bookIdx = startBookIdx + i;
			String bookUrl = String.format("%s%d/", rootDir, bookIdx);
			List<CrawledItem> cil = browsePrd(XRS52_CONF, bookUrl, null, RunType.onePrd);
			if (cil!=null && cil.size()>0){
				List<String> imageUrls = new ArrayList<String>();
				Product prd = (Product) cil.get(0);
				List<String> additionalPageList = (List<String>) prd.getParam(BookHandler.BOOK_PAGE_URLS);
				if (additionalPageList!=null){
					imageUrls.addAll(additionalPageList);
				}
				if (!onlyCover){
					PatternIO bookpageurlspattern = (PatternIO) prd.getParam(BookHandler.BOOK_PAGE_URLS_pattern);
					int totalPage = prd.getTotalPage();
					if(bookpageurlspattern!=null){
						PatternResult pattern = bookpageurlspattern.getPR();
						if (pattern!=null){
							for (int page=1; page<totalPage; page++){
								String url = PatternResult.guessUrl(pattern, page-1);
								logger.info(String.format("bookIdx %d, url for page %d is %s", bookIdx, page, url));
								imageUrls.add(url);
							}
						}
					}else{
						logger.error(String.format("pattern not found for %s", bookUrl));
					}
				}
				String localBookDir = String.format("%s%s%s%s%s", localRoot, File.separator, bookSeries, File.separator, StringUtil.getStringFromNum(i, 2));
				DownloadUtil du = new DownloadUtil(localBookDir, imageUrls);
				exeService.submit(du);
			}
		}
		
		exeService.shutdown();
		exeService.awaitTermination(4, TimeUnit.HOURS);
	}
	
	@Test
	public void testShuiHuZhuan() throws Exception {
		String bookSeries = "水浒传";
		int startBookIdx = 401;
		int bookNumber = 30;
		xrsDownload(bookSeries, startBookIdx, bookNumber);
	}
	
	@Test
	public void testDongZhouLieGuo() throws Exception {
		String bookSeries = "东周列国故事";
		int startBookIdx = 237;
		xrsDownload(bookSeries, startBookIdx, 30, 50, false);
	}
	
	@Test
	public void testSTYY() throws Exception {
		String bookSeries = "隋唐演义";
		int startBookIdx = 833;
		int bookNumber = 60;
		xrsDownload(bookSeries, startBookIdx, 0, bookNumber, true);
	}

	@Test
	public void testSongShi() throws Exception {
		String bookSeries = "宋史";
		int startBookIdx = 2274;
		int bookNumber = 20;
		xrsDownload(bookSeries, startBookIdx, bookNumber);
	}

}
