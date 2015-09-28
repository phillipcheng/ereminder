package org.cld.stock.sina.task;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.stock.sina.SinaStockConfig;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;

public class TradeDetailPostProcessTask extends Task implements Serializable{
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


	public TradeDetailPostProcessTask(){	
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
			FileSystem fs = FileSystem.get(HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf()));
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
				if (fp.length==2){
					String stockId=fp[0];
					stockId = stockId.substring(2);
					String date = fp[1];
					String content = isr.readLine();
					while (content!=null){
						String[] fields = content.split("\\s+");
						if (fields.length==6 && //remove empty data file
								!firstTitle.equals(fields[0])){ //remove title line
							StringBuffer sb = new StringBuffer();
							sb.append(stockId);
							sb.append(",");
							String timestamp;
							for (int i=0; i<6; i++){
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
								if (i<5)
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
	
	//return jobId list
	public static String[] launch(String propfile, CrawlConf cconf, String datePart){
		NodeConf nc = cconf.getNodeConf();
		Configuration conf = HadoopTaskLauncher.getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			String root = SinaStockConfig.RAW_ROOT + "/" + datePart + "/" + SinaStockConfig.SINA_STOCK_TRADE_DETAIL + "/";
			RemoteIterator<LocatedFileStatus> fsit = fs.listFiles(new Path(root), true);
			List<Task> tl = new ArrayList<Task>();
			List<String> jobIdList = new ArrayList<String>();
			int batchId=0;
			while (fsit.hasNext()){
				LocatedFileStatus lfs = fsit.next();
				TradeDetailPostProcessTask t = new TradeDetailPostProcessTask(lfs.getPath().toString());
				tl.add(t);
				if (tl.size()>=100000){
					String taskName = "TradeDetailPostProcess_" + datePart + "_" + batchId;
					jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, taskName, false));
					logger.info(String.format("sending out:%d tasks for hadoop task %s.", tl.size(), taskName));
					batchId++;
					tl = new ArrayList<Task>();
				}
			}
			String taskName = "TradeDetailPostProcess_" + batchId;
			int mbMem = 3072;
			String optValue = "-Xmx" + mbMem + "M";
			Map<String, String> hadoopJobParams = new HashMap<String, String>();
			hadoopJobParams.put("mapreduce.map.speculative", "false");//since we do not allow same map multiple instance
			hadoopJobParams.put("mapreduce.map.memory.mb", mbMem+"");
			hadoopJobParams.put("mapreduce.map.java.opts", optValue);
			jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, "TradeDetailPostProcess_" + batchId, false, hadoopJobParams));
			logger.info(String.format("sending out:%d tasks for hadoop task %s.", tl.size(), taskName));
			String[] jobIds = new String[jobIdList.size()];
			return jobIdList.toArray(jobIds);
		}catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
}
