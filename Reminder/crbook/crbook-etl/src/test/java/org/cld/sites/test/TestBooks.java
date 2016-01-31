package org.cld.sites.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.crbook.util.BookHandler;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.TestBase;
import org.cld.imagedata.ImageDataUtil;
import org.cld.util.DownloadUtil;
import org.cld.util.PatternIO;
import org.cld.util.PatternResult;
import org.cld.util.StringUtil;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.Product;
import org.junit.Before;
import org.junit.Test;
import org.xml.imagedata.Data;

import cy.common.entity.Book;

import org.cld.datacrawl.test.CrawlTestUtil.browse_type;

public class TestBooks extends TestBase {
	private static Logger logger =  LogManager.getLogger(TestBooks.class);
	private String propFile = "client1-v2.properties";
	
	public TestBooks(){
		super();
	}
	
	public static final String CBO_CONF="cbo.xml";
	public static final String A8Z8_CONF="a8z8.xml";
	public static final String MOM001_CONF="mom001.xml";
	public static final String DMZJ_CONF="dmzj.xml";
	public static final String CL_CONF="childrenslibrary.xml";
	public static final String XRS52_CONF="52xrs.xml";
	public static final String FKB_CONF="freekidsbooks.xml";
	public static final String BAOLINY_CONF="baoliny.xml";
	
	private String[] allConf = new String[]{
			//CBO_CONF,  
			//A8Z8_CONF, //register user only (paid)
			//MOM001_CONF, //died
			//DMZJ_CONF,
			//CL_CONF,
			XRS52_CONF,
			//FKB_CONF,
			//BAOLINY_CONF,
	};
	
	
	@Before
    public void setUp() throws Exception{
		this.setProp(propFile);
    }
	
	//Test browse category
	@Test
	public void rootNav() throws Exception{
		
		catNavigate(CBO_CONF, null, browse_type.one_path);
		
		catNavigate(A8Z8_CONF, null, browse_type.one_path);
		
		catNavigate(MOM001_CONF, null, browse_type.one_path);
		
		catNavigate(DMZJ_CONF, null, browse_type.one_path);
		
		catNavigate(CL_CONF, null, browse_type.one_path);
		
		catNavigate(XRS52_CONF, null, browse_type.one_path);
		
		catNavigate(FKB_CONF, null, browse_type.one_path);
		
		catNavigate(BAOLINY_CONF, null, browse_type.one_path);
		
	}
	
	@Test
	public void runMom001Main() throws Exception{
		catNavigate(MOM001_CONF, "http://www.mom001.com/", browse_type.recursive);
	}
	
	@Test
	public void runMom001Group() throws Exception{
		catNavigate(MOM001_CONF, "http://lianhuanhua.mom001.com/groups/index.html", browse_type.recursive);
	}
	
	@Test
	public void runA8Z8Main() throws Exception{
		catNavigate(A8Z8_CONF, null, browse_type.recursive);
	}
	
	//sequential
	@Test
	public void regressionAll() throws Exception{
		regressionAll(allConf); //1 path
	}
	
	//parallel
	@Test
	public void regressionTaskAll() throws Exception {
		regressionTaskAll(allConf);
	}
		
	//Test browse category 1 level
	@Test
	public void catNav_DMZJ1() throws Exception{
		catNavigate(DMZJ_CONF, "http://manhua.dmzj.com/rishi/", browse_type.one_level);
	}
	
	//Test browse details task (leaf category)
	@Test
	public void testBDT() throws Exception{
		//runBDT(CBO_CONF, "http://www.childrensbooksonline.org/library-early.htm", false);
		//runBDT(A8Z8_CONF, "http://lhh.a8z8.com/type-433-86-1.html", false);
		//runBDT(MOM001_CONF, "http://lianhuanhua.mom001.com/sh/", false);
		//runBDT(DMZJ_CONF, "http://manhua.dmzj.com/blood/", false);
		runBDT(XRS52_CONF, "http://www.52xrs.com/list/lsgs.htm", false);
	}
	
	//Test turning pages for product list browsing
	@Test
	public void testBDT_NextPage() throws Exception{
		//runBDT(CBO_CONF, "http://www.childrensbooksonline.org/library-pre-reader.htm", true);
		//runBDT(A8Z8_CONF, "http://lhh.a8z8.com/type-433-86-1.html", true);
		//runBDT(MOM001_CONF, "http://lianhuanhua.mom001.com/mz/", true);
		runBDT(XRS52_CONF, "http://www.52xrs.com/list/lsgs.htm", true);
	}
	
	//Test One Book
	@Test
	public void testOneBook() throws Exception{
		//browsePrd(CBO_CONF, "http://www.childrensbooksonline.org/Aladdin_or_The_Wonderful_Lamp/index.htm");
		//browsePrd(MOM001_CONF, "http://lianhuanhua.mom001.com/rw/2011/1125/6623.html");
		//browsePrd(DMZJ_CONF, "http://manhua.dmzj.com/dftrmh/23330.shtml");
		//browsePrd(A8Z8_CONF, "http://lhh.a8z8.com/thread-458039-1-1.html");
		//browsePrd(CL_CONF, "http://www.childrenslibrary.org/icdl/BookPreview?bookid=flomuki_00510003&route=all&lang=English&msg=&ilang=English");
		browsePrd(XRS52_CONF, "http://www.52xrs.com/comic/2413/");
		//browsePrd(XRS123_CONF, "http://www.5xiaxiaoshuo.com/jingdianmingzhu/20121126/2082_1.html");
		//browsePrd(BAOLINY_CONF, "http://www.baoliny.com/18757/index.html");
		//browsePrd(SQSXS_CONF, "http://www.sqsxs.com/25/25040/index.html");
		//browsePrd(FKB_CONF, "http://freekidsbooks.org/view/126");
		//browsePrd(VYMING_CONF, "http://bbs.vyming.com/novel-view-30510.html");
		//browsePrd(HAODU5_CONF, "http://haodu5.com/5200/17/17513/");
	}
	
	public void xrsDownload(String bookSeries, int startBookIdx, int bookNumber) throws Exception{
		xrsDownload(bookSeries, startBookIdx, bookNumber, false);
	}
	public void xrsDownload(String bookSeries, int startBookIdx, int bookNumber, boolean onlyCover) throws Exception{
		ExecutorService exeService = Executors.newFixedThreadPool(20);
		String rootDir = "http://www.52xrs.com/comic/";
		String localRoot = "C:\\mydoc\\picbook";
		for (int i=0; i<bookNumber; i++){
			int bookIdx = startBookIdx + i;
			String bookUrl = String.format("%s%d/", rootDir, bookIdx);
			List<CrawledItem> cil = browsePrd(XRS52_CONF, bookUrl);
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
		xrsDownload(bookSeries, startBookIdx, bookNumber, true);
	}
	
	@Test
	public void testDongZhouLieGuo() throws Exception {
		String bookSeries = "东周列国故事";
		int startBookIdx = 237;
		int bookNumber = 50;
		xrsDownload(bookSeries, startBookIdx, bookNumber, true);
		
		bookSeries = "隋唐演义";
		startBookIdx = 833;
		bookNumber = 60;
		xrsDownload(bookSeries, startBookIdx, bookNumber, true);
	}

	@Test
	public void testSongShi() throws Exception {
		String bookSeries = "宋史";
		int startBookIdx = 2274;
		int bookNumber = 20;
		xrsDownload(bookSeries, startBookIdx, bookNumber);
	}

}
