package org.cld.hadooputil.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.hadooputil.TransferHdfsFile;

public class TestHdfsUtil {

	public static final Logger logger = LogManager.getLogger(TestHdfsUtil.class);
	
	Configuration conf = null;
	
	@Before
	public void setup(){
		conf = new Configuration();
		conf.set("fs.default.name", "hdfs://192.85.247.104:19000");
	}
	@Test
	public void test() throws Exception {
		FileSystem fs = FileSystem.get(conf);
		Path p = new Path("/reminder/items/raw/2004-10-01_2015-10-03");
		ContentSummary cs = fs.getContentSummary(p);
		logger.info(String.format("du for dir %s is %s", p, cs.toString()));
	}
	
	@Test
	public void testDump() throws Exception{
		TransferHdfsFile.main(new String[]{"10", "hdfs://192.85.247.104:19000","/reminder/items/merge/sina-stock-market-fq",
				"C:\\mydoc\\mydata\\stock\\merge\\sina-stock-market-fq","hs_a_2015-10-03", "false"});
		
		//20 hdfs://192.85.247.104:19000 /reminder/items/merge /data/cydata/merge nasdaq-quote-afterhours,nasdaq-quote-tick,nasdaq-quote-premarket,sina-stock-market-tradedetail -
	}
	
	@Test
	public void testUpload() throws Exception{
		TransferHdfsFile.main(new String[]{"1", "hdfs://192.85.247.104:19000","/reminder/nasdaq/daily",
				"C:\\Kibot\\daily","-","-","true"});
	}

}
