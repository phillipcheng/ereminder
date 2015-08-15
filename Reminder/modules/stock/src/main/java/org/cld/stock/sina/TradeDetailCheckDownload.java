package org.cld.stock.sina;

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
		private static final String firstTitle = "成交时间";
		private static final String tradeStartTime = "09:25";
		
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
					if (!firstTitle.equals(keys[0])|| !lastValues[0].startsWith(tradeStartTime)){
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
	
	public static String launch(CrawlConf cconf, Date endDate){
		NodeConf nc = cconf.getNodeConf();
		TaskMgr taskMgr = nc.getTaskMgr();
		String ed = sdf.format(endDate);
		String inDir = StockConfig.SINA_STOCK_TRADE_DETAIL + "/in/" + ed;
		String outDir = StockConfig.SINA_STOCK_TRADE_DETAIL + "/check/" + ed;
		Configuration conf = HadoopTaskLauncher.getHadoopConf(nc);
		logger.info("here1");
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			logger.info("here2");
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
			logger.info("here3");
			job.setJarByClass(MyMapper.class);
			job.setMapperClass(MyMapper.class);
			job.setNumReduceTasks(0);//no reducer
			job.setInputFormatClass(WholeFileInputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			Path in = new Path(taskMgr.getHadoopCrawledItemFolder() + "/" + inDir);
			FileInputFormat.addInputPath(job, in);
			FileInputFormat.setInputDirRecursive(job, true);
			Path out = new Path(taskMgr.getHadoopCrawledItemFolder() + "/" + outDir);
			fs.delete(out, true);
			FileOutputFormat.setOutputPath(job, out);
			logger.info("here4");
			if (taskMgr.getHadoopJobTracker()!=null){
				job.submit();
				logger.info("here5");
			}else{
				logger.info("here6");
				job.waitForCompletion(true);
			}
			return job.getJobID().toString();
		}catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
}
