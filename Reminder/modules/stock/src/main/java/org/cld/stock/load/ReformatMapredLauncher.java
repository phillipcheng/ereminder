package org.cld.stock.load;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class ReformatMapredLauncher {
	
	public static final String INPUT_TABLE_NAME="crawledItem";
	public static final String KEY_INDEX="keyIndex";
	public static final String NAMED_OUTPUT="text";
	
	private static Logger logger =  LogManager.getLogger(ReformatMapredLauncher.class);
	
	//the keyIdx of the input csv value
	public static void format(String prop, String inputFolder, int keyIdx, String outputFolder){
		try{
			CrawlConf cconf = CrawlTestUtil.getCConf(prop);
			Configuration conf = HadoopTaskUtil.getHadoopConf(cconf.getNodeConf());
			conf.setInt(KEY_INDEX, keyIdx);
			conf.set("mapred.textoutputformat.separator", "");//default is tab
			Job job = Job.getInstance(conf, "Format");
			FileSystem fs = FileSystem.get(conf);
			if (cconf.getTaskMgr().getYarnAppCp()!=null){
				for (String s: cconf.getTaskMgr().getYarnAppCp()){
					//find all the jar,zip files under s if s is a directory
					FileStatus[] fslist = fs.listStatus(new Path(s));
					Path[] plist = FileUtil.stat2Paths(fslist);
					for (Path p:plist){
						job.addFileToClassPath(p);
					}
				}
			}
			MultipleOutputs.addNamedOutput(job, NAMED_OUTPUT, TextOutputFormat.class,Text.class, Text.class);
			job.setJarByClass(ReformatMapredLauncher.class);
			job.setMapperClass(ReformatMapper.class);
			job.setReducerClass(ReformatReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.setInputPaths(job, new Path(inputFolder));
			FileOutputFormat.setOutputPath(job, new Path(outputFolder));
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
