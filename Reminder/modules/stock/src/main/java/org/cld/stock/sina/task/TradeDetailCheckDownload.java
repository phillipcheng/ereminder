package org.cld.stock.sina.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.Charsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.hadoop.WholeFileInputFormat;


public class TradeDetailCheckDownload {
	private static Logger logger =  LogManager.getLogger(TradeDetailCheckDownload.class);
	public static String task_name = TradeDetailCheckDownload.class.getName();
	public static String sep = "_";
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static class MyMapper extends Mapper<Text, Text, Text, Text>{
		private static final String firstTitle1 = "成交时间";
		private static final String emptyTitle = "<script";
		private static final String tradeStartTime1 = "09:25";
		private static final String tradeStartTime2 = "09:3";
		private static final String tradeStartTime3 = "15:00";
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
		}
	    
		@Override
	    protected void cleanup(Context context) throws IOException, InterruptedException {
	    }
	    
		@Override
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			String fileName = key.toString();
			if (fileName.contains(".")){
				fileName = fileName.substring(0, fileName.indexOf("."));
				BufferedReader br = new BufferedReader(new StringReader(new String(value.getBytes(), 0, value.getLength(), "gb2312")));
				String line = br.readLine();
				String firstline = line;
				String lastline = firstline;
				while (line!=null){
					lastline = line;
					line = br.readLine();
				}
				if (firstline==null || lastline==null){
					context.write(new Text(fileName), new Text("empty"));
				}else{
					//check first line
					String[] keys = firstline.split("\\s+");
					//check last line
					String[] lastValues = lastline.split("\\s+");
					if (!emptyTitle.equals(keys[0])){//empty file
						if (!firstTitle1.equals(keys[0]) || 
								!(lastValues[0].startsWith(tradeStartTime1) // 09:25
										||lastValues[0].startsWith(tradeStartTime2) //09:30
										||(lastValues[0].startsWith(tradeStartTime3) && "0".equals(lastValues[3]))//15:00 x x 0
										)
								)
							{
							StringBuffer sb = new StringBuffer();
							sb.append(keys[0]);
							sb.append(",");
							sb.append(lastValues[0]);
							context.write(new Text(fileName), new Text(sb.toString()));
						}
					}
				}
			}
		}
	}
	
	public static String launch(CrawlConf cconf, String datePart){
		NodeConf nc = cconf.getNodeConf();
		TaskMgr taskMgr = nc.getTaskMgr();
		String inDir = SinaStockConfig.RAW_ROOT + "/" + datePart + "/" + SinaStockConfig.SINA_STOCK_TRADE_DETAIL;
		String outDir = SinaStockConfig.CHECK_ROOT + "/" + SinaStockConfig.SINA_STOCK_TRADE_DETAIL + "/" + datePart;
		Configuration conf = HadoopTaskLauncher.getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			Job job = Job.getInstance(conf, task_name + sep + inDir);
			//add app specific jars to classpath
			if (nc.getTaskMgr().getYarnAppCp()!=null){
				for (String s: nc.getTaskMgr().getYarnAppCp()){
					//find all the jar,zip files under s if s is a directory
					FileStatus[] fslist = fs.listStatus(new Path(s));
					Path[] plist = FileUtil.stat2Paths(fslist);
					for (Path p:plist){
						job.addFileToClassPath(p);
					}
				}
			}
			job.setJarByClass(MyMapper.class);
			job.setMapperClass(MyMapper.class);
			job.setNumReduceTasks(0);//no reducer
			job.setInputFormatClass(WholeFileInputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			Path in = new Path(inDir);
			FileInputFormat.addInputPath(job, in);
			FileInputFormat.setInputDirRecursive(job, true);
			Path out = new Path(outDir);
			fs.delete(out, true);
			FileOutputFormat.setOutputPath(job, out);
			if (taskMgr.getHadoopJobTracker()!=null){
				job.submit();
			}else{
				job.waitForCompletion(true);
			}
			return job.getJobID().toString();
		}catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
}
