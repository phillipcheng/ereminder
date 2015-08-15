package org.cld.stock.sina;

import java.io.IOException;
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


public class TradeDetailPostProcess {
	private static final String NAMED_OUTPUT_TXT = "text";
	private static Logger logger =  LogManager.getLogger(TradeDetailPostProcess.class);
	public static String task_name = TradeDetailPostProcess.class.getName();
	public static String sep = "_";
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static class MyMapper extends Mapper<Object, Text, Text, Text>{
		private static final String firstTitle = "成交时间";
		private static final String noChange = "--";
		private MultipleOutputs<Text, Text> mos;
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			mos = new MultipleOutputs<Text,Text>(context);
		}
	    
		@Override
	    protected void cleanup(Context context) throws IOException, InterruptedException {
	        mos.close();
	    }
	    
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
			if (fileName.contains(".")){
				fileName = fileName.substring(0, fileName.indexOf("."));
				String[] fp = fileName.split(sep);
				if (fp.length==2){
					String stockId=fp[0];
					stockId = stockId.substring(2);
					String date = fp[1];
					String content =  new String(value.getBytes(), 0, value.getLength(), "gb2312");
					String[] fields = content.split("\\s+");
					if (fields.length==6 && //remove empty data file
							!firstTitle.equals(fields[0])){ //remove title line
						StringBuffer sb = new StringBuffer();
						String timestamp;
						for (int i=0; i<6; i++){
							if (i==0){
								String time = fields[i];
								timestamp = date + " " + time;
								sb.append(timestamp);
							}else if (i==2){
								String delta = fields[i];
								if (noChange.equals(delta)){
									delta="0";
								}
								sb.append(delta);
							}else{
								sb.append(fields[i]);
							}
							if (i<5)
								sb.append(",");
						}
						mos.write(NAMED_OUTPUT_TXT, new Text(stockId), new Text(sb.toString()), stockId);
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
		String outDir = StockConfig.SINA_STOCK_TRADE_DETAIL + "/out/" + ed;
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
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			Path in = new Path(taskMgr.getHadoopCrawledItemFolder() + "/" + inDir);
			FileInputFormat.addInputPath(job, in);
			FileInputFormat.setInputDirRecursive(job, true);
			Path out = new Path(taskMgr.getHadoopCrawledItemFolder() + "/" + outDir);
			fs.delete(out, true);
			FileOutputFormat.setOutputPath(job, out);
			MultipleOutputs.addNamedOutput(job, NAMED_OUTPUT_TXT, TextOutputFormat.class, Text.class, Text.class);
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
