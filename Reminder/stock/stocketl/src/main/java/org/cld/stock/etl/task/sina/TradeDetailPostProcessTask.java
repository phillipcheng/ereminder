package org.cld.stock.etl.task.sina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.stock.etl.LaunchableTask;
import org.cld.stock.etl.base.ETLConfig;
import org.cld.stock.etl.base.SinaETLConfig;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;

public class TradeDetailPostProcessTask extends Task implements Serializable, LaunchableTask{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(TradeDetailPostProcessTask.class);
	public static String encoding="GBK";
	public static final String sep ="-";
	public static final String stockId_date_sep ="_";
	private static final String firstTitle = "成交时间";
	private static final String noChange = "--";
	private static final String dir_sell = "卖盘";
	private static final String dir_buy = "买盘";
	
	private String pathName; // fullname /reminder/items/raw/date-part/sina-stock-market-tradedetail	
	private CrawlConf cconf;
	
	private static TradeDetailPostProcessTask instance;
	public static LaunchableTask getLaunchInstance() {
		if (instance==null){
			instance=new TradeDetailPostProcessTask();
		}
		return instance;
	}

	public TradeDetailPostProcessTask(){
		this.setId(TradeDetailPostProcessTask.class.getName());
	}
	
	public TradeDetailPostProcessTask(String pathName){
		this.pathName = pathName;
		genId();
	}
	
	@Override
	public String genId(){
		String inputId = pathName;
		inputId = inputId.replace(":", sep);
		inputId = inputId.replace("/", sep);
		inputId = inputId.replace(".", sep);
		this.setId(inputId);
		return this.getId();
	}

	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		try{
			cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
			FileSystem fs = FileSystem.get(HadoopTaskLauncher.getHadoopConf(cconf));
			logger.info("process convert task: " + pathName);
			Path ip = new Path(pathName);
			String opathName = pathName.replace("raw", "postprocess");
			Path op = new Path(opathName);
			BufferedReader isr= new BufferedReader(new InputStreamReader(fs.open(ip), encoding));
			BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fs.create(op,true), encoding));
			String fileName = ip.getName();
			if (fileName.contains(".")){
				fileName = fileName.substring(0, fileName.indexOf("."));
				String[] fp = fileName.split(stockId_date_sep);
				int itemsPerLine = 6;
				if (fp.length==2){
					String stockId=fp[0];
					stockId = stockId.substring(2);
					String date = fp[1];
					String content = isr.readLine();
					while (content!=null){
						String[] fields = content.split("\\s+");
						if (fields.length==itemsPerLine && //remove empty data file
								!firstTitle.equals(fields[0])){ //remove title line
							StringBuffer sb = new StringBuffer();
							sb.append(stockId);
							sb.append(",");
							String timestamp;
							for (int i=0; i<itemsPerLine; i++){
								if (i==0){//time field idx
									String time = fields[i];
									timestamp = date + " " + time;
									sb.append(timestamp);
								}else if (i==2){//price delta field idx
									String delta = fields[i];
									if (noChange.equals(delta)){
										delta="0";
									}
									sb.append(delta);
								}else if (i==5){//buy/sell
									if (dir_sell.equals(fields[i])){
										sb.append("1");
									}else{
										sb.append("0");
									}
								}else{
									sb.append(fields[i]);
								}
								if (i<(itemsPerLine-1))
									sb.append(",");
							}
							osw.write(sb.append("\n").toString());
						}
						content = isr.readLine();
					}
				}
			}
			isr.close();
			osw.close();
			
		}catch(Exception e){
			logger.error("", e);
		}
		return null;
	}

	//need to generate getter and setter for task serialization, if not this will be null
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
	private static String submitTasks(int batchId, String datePart, String propfile, List<Task> tl, CrawlConf cconf){
		String taskName = TradeDetailPostProcessTask.class.getSimpleName()+ "_" + datePart + "_" + batchId;
		int mbMem = 512;
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "500");
		return TaskUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, taskName, false, hadoopJobParams);
	}
	
	@Override
	public String[] launch(String propfile, String baseMarketId, CrawlConf cconf, String datePart, String[] cmds){
		Configuration conf = HadoopTaskLauncher.getHadoopConf(cconf);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			String root = ETLConfig.RAW_ROOT + "/" + datePart + "/" + SinaETLConfig.SINA_STOCK_TRADE_DETAIL + "/";
			RemoteIterator<LocatedFileStatus> fsit = fs.listFiles(new Path(root), true);
			List<Task> tl = new ArrayList<Task>();
			List<String> jobIdList = new ArrayList<String>();
			int batchId=0;
			while (fsit.hasNext()){
				LocatedFileStatus lfs = fsit.next();
				TradeDetailPostProcessTask t = new TradeDetailPostProcessTask(lfs.getPath().toString());
				tl.add(t);
				if (tl.size()>=200000){
					jobIdList.add(submitTasks(batchId, datePart, propfile, tl, cconf));
					batchId++;
					tl = new ArrayList<Task>();
				}
			}
			jobIdList.add(submitTasks(batchId, datePart, propfile, tl, cconf));
			String[] jobIds = new String[jobIdList.size()];
			return jobIdList.toArray(jobIds);
		}catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
}
