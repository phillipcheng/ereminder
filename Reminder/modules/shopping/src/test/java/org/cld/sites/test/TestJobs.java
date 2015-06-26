package org.cld.sites.test;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.CrawlTestUtil.browse_cat_type;
import org.cld.datacrawl.test.TestBase;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.cld.stock.load.CNBasicLoad;

public class TestJobs extends TestBase{
	
	public static final String LINKEDIN_COMPANY="linkedin-company.xml";
	
	private String propFile = "client1-v2.properties";
	
	@Before
	public void setUp(){
		super.setProp(propFile);
	}
	
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
			catNavigate(LINKEDIN_COMPANY, startUrl, browse_cat_type.recursive);	
		}
	}
	
	@Test
	public void run_linkedin_bct_one() throws Exception{
		catNavigate(LINKEDIN_COMPANY, "https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=B&page_num=1", browse_cat_type.recursive);	
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
		FileSystem fs = FileSystem.get(HadoopTaskUtil.getHadoopConf(cconf.getNodeConf()));
		fs.delete(new Path(outputFile), true);
		CNBasicLoad.loadHiveFromHbase(this.getPropFile(), outputFile);
	}
	
	@Test
	public void checkUnlockedAccounts(){
		int i = getUnlockedAccounts("https://www.linkedin.com/uas/login", LINKEDIN_COMPANY);
		logger.info(String.format("%d unlocked accounts for %s", i, LINKEDIN_COMPANY));
	}
	
}
