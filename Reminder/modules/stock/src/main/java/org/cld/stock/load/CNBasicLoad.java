package org.cld.stock.load;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datastore.impl.HbaseDataStoreManagerImpl;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.cld.util.hadoop.DefaultCopyTextReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.javascript.host.Text;


public class CNBasicLoad {
	
	public static final String INPUT_TABLE_NAME="crawledItem";
	
	private static Logger logger =  LogManager.getLogger(CNBasicLoad.class);
	
	public static void genCSVFromHbase(String prop, String outputFileName){
		
		try{
			CrawlConf cconf = CrawlTestUtil.getCConf(prop);
			Configuration conf = HadoopTaskUtil.getHadoopConf(cconf.getNodeConf());
			conf.set(TableInputFormat.INPUT_TABLE, INPUT_TABLE_NAME);
			Scan scan = new Scan();
			scan.addColumn(HbaseDataStoreManagerImpl.CRAWLEDITEM_CF_BYTES, HbaseDataStoreManagerImpl.CRAWLEDITEM_DATA_BYTES);
			Job job = Job.getInstance(conf, "ConvertJob");
			TableMapReduceUtil.initTableMapperJob(INPUT_TABLE_NAME, scan, StockBasicDataHbaseTableMapper.class, 
					Text.class, Text.class, job, false);
			job.setNumReduceTasks(0);
			job.setJarByClass(StockBasicDataHbaseTableMapper.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			FileOutputFormat.setOutputPath(job, new Path(outputFileName));
			if (cconf.getTaskMgr().getHadoopJobTracker()!=null){
				job.submit();
			}else{
				job.waitForCompletion(true);
			}
		}catch(Exception e){
			logger.error("", e);
		}		
		
	}

}
