package org.cld.sites.test;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;

import org.cld.stock.load.CNBasicLoad;

public class TestJobs extends TestBase{
	
	public static final String LINKEDIN_COMPANY="linkedin-company.xml";
	
	public TestJobs(){
		super();
	}
	
	@Test
	public void run_linkedin_bct() throws Exception{
		catNavigate(LINKEDIN_COMPANY, null, CrawlTestUtil.BROWSE_CAT_TYPE_RECURSIVE);	
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
}
