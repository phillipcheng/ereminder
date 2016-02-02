package org.cld.sites.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;
import org.cld.datacrawl.test.BrowseType;
import org.cld.datacrawl.test.TestBase;
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
	public static final String LINKEDIN_COMPANY="linkedin-company.xml";
	public static final String[] startUrls = new String[]{
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=D,E&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=F,G,H,I&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=C&f_NFR=NFR2,NFR3,NFR5,NFR4&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=C&f_NFR=NFR1&page_num=1",
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=B&page_num=1",
	};
	
	@Test
	public void run_linkedin_bct() throws Exception{
		for (String startUrl:startUrls){
			catNavigate(LINKEDIN_COMPANY, startUrl, BrowseType.recursive);	
		}
	}
	
	@Test
	public void run_linkedin_bct_one() throws Exception{
		catNavigate(LINKEDIN_COMPANY, "https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=B&page_num=1", BrowseType.recursive);	
	}
	
	@Test
	public void run_linkedin_bdt() throws Exception{
		runBDT(LINKEDIN_COMPANY, "https://www.linkedin.com/vsearch/c?f_CCR=us%3A84&f_I=4&f_CS=C&page_num=1", false);
	}
	
	@Test
	public void run_linkedin_bdt_turnpage_only() throws Exception{
		runBDT(LINKEDIN_COMPANY, "https://www.linkedin.com/vsearch/c?f_CCR=us%3A84&f_I=4&f_CS=C&page_num=1", true);
	}
	
	@Test
	public void run_jobs_transform() throws Exception {
		String outputFile = "/output/jobs";
		FileSystem fs = FileSystem.get(HadoopTaskLauncher.getHadoopConf(cconf));
		fs.delete(new Path(outputFile), true);
	}
	
	@Test
	public void checkUnlockedAccounts(){
		int i = getUnlockedAccounts("https://www.linkedin.com/uas/login", LINKEDIN_COMPANY);
		logger.info(String.format("%d unlocked accounts for %s", i, LINKEDIN_COMPANY));
	}
	
	//club.xml
	public static final String CLUB="club.xml";
	@Test
	public void testClub() throws Exception{
		List<CrawledItem> cil = browsePrd(CLUB, null, new Date(), false);
		List<String> csvs = new ArrayList<String>();
		for (CrawledItem ci:cil){
			for (String[] csv: ci.getCsvValue()){
				if (csv!=null && csv.length==2){
					csvs.add(csv[1]);
				}
			}
		}
		CsvUtil.outputCsv(csvs, "club.csv");
	}
	
	//
}
