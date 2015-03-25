package org.cld.booksites.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.junit.Test;

public class TestBooks extends TestBase {
	private static Logger logger =  LogManager.getLogger(TestBooks.class);
	
	public TestBooks(){
		super();
	}
	
	public static final String CBO_CONF="cbo.xml";
	public static final String A8Z8_CONF="a8z8.xml";
	public static final String MOM001_CONF="mom001.xml";
	public static final String XRS123_CONF="xrs123.xml";
	public static final String DMZJ_CONF="dmzj.xml";
	public static final String HAODU5_CONF="haodu5.xml";
	public static final String CL_CONF="childrenslibrary.xml";
	public static final String XRS52_CONF="52xrs.xml";
	public static final String FKB_CONF="freekidsbooks.xml";
	public static final String VYMING_CONF="vyming.xml";
	public static final String WX114_CONF="wx114.xml";
	public static final String BAOLINY_CONF="baoliny.xml";
	public static final String SQSXS_CONF="sqsxs.xml";
	
	public static final String YAHOO_FINANCE="yahoo.finance.xml";
	public static final String USCIS_CONF = "uscis.xml";
	public static final String INVOKE_YAHOO_FINANCE_CONF1="invoke.yahoo.finance.xml";
	
	private String[] allConf = new String[]{
			CBO_CONF,  
			A8Z8_CONF,
			MOM001_CONF,
			DMZJ_CONF,
			CL_CONF,
			XRS52_CONF,
			FKB_CONF,
			XRS123_CONF,
			BAOLINY_CONF,
			SQSXS_CONF,
//			HAODU5_CONF, //-- dead
//			VYMING_CONF, //-- dead
//			WX114_CONF, //--updated
	};
	
	//Test browse category
	@Test
	public void rootNav() throws Exception{
		catNavigate(CBO_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(DMZJ_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(MOM001_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(A8Z8_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(CL_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(XRS52_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(XRS123_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(BAOLINY_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		catNavigate(SQSXS_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		//catNavigate(FKB_CONF, null, CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
		//catNavigate(HAODU5_CONF, "http://haodu5.com/", CrawlTestUtil.BROWSE_CAT_TYPE_1_PATH);
	}
	
	//sequential
	@Test
	public void regressionAll() throws Exception{
		regressionAll(allConf);
	}
	
	//parallel
	@Test
	public void regressionTaskAll(String[] allConf) throws Exception {
		regressionTaskAll(allConf);
	}
		
	//Test browse category 1 level
	@Test
	public void catNav_DMZJ1() throws Exception{
		catNavigate(DMZJ_CONF, "http://manhua.dmzj.com/rishi/", CrawlTestUtil.BROWSE_CAT_TYPE_1_LVL);
	}
	
	//Test browse details task (leaf category)
	@Test
	public void testBDT() throws Exception{
		runBDT(CBO_CONF, "http://www.childrensbooksonline.org/library-early.htm", false);
		runBDT(A8Z8_CONF, "http://lhh.a8z8.com/type-433-86-1.html", false);
		runBDT(MOM001_CONF, "http://lianhuanhua.mom001.com/sh/", false);
		runBDT(XRS123_CONF, "http://www.xiaorenshu123.com/category/1.html", false);
		runBDT(DMZJ_CONF, "http://manhua.dmzj.com/blood/", false);
		runBDT(HAODU5_CONF, "http://haodu5.com/class_11_1.html", false);
	}
	
	//Test turning pages for product list browsing
	@Test
	public void testBDT_NextPage() throws Exception{
		runBDT(CBO_CONF, "http://www.childrensbooksonline.org/library-pre-reader.htm", true);
		runBDT(A8Z8_CONF, "http://lhh.a8z8.com/type-433-86-1.html", true);
		runBDT(MOM001_CONF, "http://lianhuanhua.mom001.com/mz/", true);
		runBDT(XRS123_CONF, "http://www.xiaorenshu123.com/category/4.html", true);
		runBDT(HAODU5_CONF, "http://haodu5.com/class_11_1.html", true);
		runBDT(XRS52_CONF, "http://www.52xrs.com/list/wxmz.htm", true);
	}
	
	//Test One Book
	@Test
	public void testOneBook() throws Exception{
		//browsePrd(CBO_CONF, "http://www.childrensbooksonline.org/Aladdin_or_The_Wonderful_Lamp/index.htm");
		//browsePrd(MOM001_CONF, "http://lianhuanhua.mom001.com/rw/2011/1125/6623.html");
		//browsePrd(DMZJ_CONF, "http://manhua.dmzj.com/dftrmh/23330.shtml");
		//browsePrd(A8Z8_CONF, "http://lhh.a8z8.com/thread-458039-1-1.html");
		//browsePrd(CL_CONF, "http://www.childrenslibrary.org/icdl/BookPreview?bookid=flomuki_00510003&route=all&lang=English&msg=&ilang=English");
		//browsePrd(XRS52_CONF, "http://www.52xrs.com/comic/2413/");
		//browsePrd(XRS123_CONF, "http://www.5xiaxiaoshuo.com/jingdianmingzhu/20121126/2082_1.html");
		//browsePrd(BAOLINY_CONF, "http://www.baoliny.com/18757/index.html");
		//browsePrd(SQSXS_CONF, "http://www.sqsxs.com/25/25040/index.html");
		
		//browsePrd(FKB_CONF, "http://freekidsbooks.org/view/126");
		//browsePrd(VYMING_CONF, "http://bbs.vyming.com/novel-view-30510.html");
		//browsePrd(HAODU5_CONF, "http://haodu5.com/5200/17/17513/");
		
	}
	
	@Test
	public void testOnePrd_USCIS() throws Exception{
		browsePrd(USCIS_CONF, null);
	}
	
	//invoke task via invokeTaskTask
	@Test
	public void invokeTask_YahooFinance() throws Exception{
		cconf.setUpSite(YAHOO_FINANCE, null);
		CrawlTestUtil.invokeTask(INVOKE_YAHOO_FINANCE_CONF1, cconf);
	}
	
	//invoke task via API
	@Test
	public void run_YahooFinance() throws InterruptedException{
		cconf.setUpSite(YAHOO_FINANCE, null);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("symbal", "HPQ");
		browsePrd(YAHOO_FINANCE, null, params);
	}
	
	
}
