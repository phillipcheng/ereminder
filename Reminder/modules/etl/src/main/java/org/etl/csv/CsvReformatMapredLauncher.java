package org.etl.csv;

import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CsvReformatMapredLauncher {
	
	public static final String KEY_INDEX="keyIndex";
	public static final String NAMED_OUTPUT="text";
	
	private static Logger logger =  LogManager.getLogger(CsvReformatMapredLauncher.class);
	
	//the keyIdx of the input csv value to be the reduce key
	public static void format(String prop, String inputFolder, int keyIdx, String outputFolder){
		try{
			NodeConf nc = TaskUtil.getNodeConf(prop);
			TaskMgr tm = new TaskMgr();
			tm.loadConf(prop, tm.getClass().getClassLoader(), null);
			nc.setTaskMgr(tm);
			Configuration conf = HadoopTaskUtil.getHadoopConf(nc);
			conf.setInt(KEY_INDEX, keyIdx);
			conf.set("mapred.textoutputformat.separator", "");//default is tab
			Job job = Job.getInstance(conf, "Format");
			FileSystem fs = FileSystem.get(conf);
			
			if (tm.getYarnAppCp()!=null){
				for (String s: tm.getYarnAppCp()){
					//find all the jar,zip files under s if s is a directory
					FileStatus[] fslist = fs.listStatus(new Path(s));
					Path[] plist = FileUtil.stat2Paths(fslist);
					for (Path p:plist){
						job.addFileToClassPath(p);
					}
				}
			}
			MultipleOutputs.addNamedOutput(job, NAMED_OUTPUT, TextOutputFormat.class, Text.class, NullWritable.class);
			job.setJarByClass(CsvReformatMapredLauncher.class);
			job.setMapperClass(CsvReformatMapper.class);
			job.setReducerClass(CsvReformatReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.setInputPaths(job, new Path(inputFolder));
			FileOutputFormat.setOutputPath(job, new Path(outputFolder));
			if (tm.getHadoopJobTracker()!=null){
				job.submit();
			}else{
				job.waitForCompletion(true);
			}
		}catch(Exception e){
			logger.error("", e);
		}		
		
	}

}
