package org.cld.sites.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;
import org.cld.datacrawl.test.TestBase;
import org.cld.taskmgr.entity.RunType;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.CsvUtil;
import org.cld.util.entity.CrawledItem;

public class TestMisc extends TestBase{
	
	private String propFile = "client1-v2.properties";
	
	@Before
	public void setUp(){
		super.setProp(propFile);
	}
	
	//linkedin
	public static final String LINKEDIN_COMPANY="linkedinCompanyV2.xml";
	public static final String[] startUrls = new String[]{
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=D,E&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=F,G,H,I&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=C&f_NFR=NFR2,NFR3,NFR5,NFR4&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=C&f_NFR=NFR1&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=B&page_num=1",
	};

	
	@Test
	public void getLinkedinCompanyInfo() throws Exception {
		browsePrd(LINKEDIN_COMPANY, startUrls[0], "companyList", RunType.all);
	}

	
	//club.xml
	public static final String CLUB="club.xml";
	@Test
	public void testClub() throws Exception{
		List<CrawledItem> cil = browsePrd(CLUB, null, null, RunType.onePrd);
		CsvUtil.outputCsv(cil, "club.csv");
	}
	
	//uscis
	public static final String USCIS="uscis.xml";
	@Test
	public void checkStatus() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String[] caseIds = new String[]{"LIN1591460823","LIN1591460824","LIN1591460825","LIN1591460826"};
		String caseIdKey="caseId";
		for (String caseId:caseIds){
			paramMap.put(caseIdKey, caseId);
			List<CrawledItem> cil = browsePrd(USCIS, null, null, paramMap, RunType.onePrd);
			logger.info(String.format("ci for caseId %s is %s", caseId, cil.get(0)));
		}
	}
}
