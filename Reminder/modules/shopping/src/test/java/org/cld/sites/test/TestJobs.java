package org.cld.sites.test;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.cld.stock.load.CNBasicLoad;

public class TestJobs extends TestBase{
	
	public static final String LINKEDIN_COMPANY="linkedin-company.xml";
	
	public TestJobs(String conf){
		super(conf);
	}
	
	public TestJobs(){
		super();
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
			catNavigate(LINKEDIN_COMPANY, startUrl, CrawlTestUtil.BROWSE_CAT_TYPE_RECURSIVE);	
		}
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
		CNBasicLoad.loadHiveFromHbase(propFile, outputFile);
	}
	
	@Test
	public void checkUnlockedAccounts(){
		int i = getUnlockedAccounts(LINKEDIN_COMPANY);
		logger.info(String.format("%d unlocked accounts for %s", i, LINKEDIN_COMPANY));
	}
	
	public static void main(String[] args){
		if (args.length<1){
			logger.error("usage: TestJobs propFile");
			return;
		}
		
		String prop = args[0];
		TestJobs tj = new TestJobs(prop);
		try {
			tj.run_linkedin_bct();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
