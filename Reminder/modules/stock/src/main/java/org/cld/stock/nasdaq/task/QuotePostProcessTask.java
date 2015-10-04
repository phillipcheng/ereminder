package org.cld.stock.nasdaq.task;

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
import org.cld.stock.ETLUtil;
import org.cld.stock.StockConfig;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;


public class QuotePostProcessTask extends Task implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(QuotePostProcessTask.class);
	public static final String sep = "-";
	public static final String stockId_date_sep = "_";

	private String pathName;
	private CrawlConf cconf;

	public QuotePostProcessTask() {
	}

	public QuotePostProcessTask(String pathName) {
		this.pathName = pathName;
		genId();
	}

	@Override
	public String genId() {
		String inputId = pathName;
		inputId = inputId.replace(":", sep);
		inputId = inputId.replace("/", sep);
		inputId = inputId.replace(".", sep);
		this.setId(inputId);
		return this.getId();
	}

	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException {
		try {
			cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
			FileSystem fs = FileSystem.get(HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf()));
			logger.info("process convert task: " + pathName);
			Path ip = new Path(pathName);
			String opathName = pathName.replace("raw", "postprocess");
			Path op = new Path(opathName);
			BufferedReader isr = new BufferedReader(new InputStreamReader(fs.open(ip)));
			BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fs.create(op, true)));
			String fileName = ip.getName();
			String[] fp = fileName.split(stockId_date_sep);
			if (fp.length == 2) {
				String stockId = fp[0];
				String date = fp[1];
				String content = isr.readLine();
				int itemsPerLine = 4;
				while (content != null) {
					String[] fields = content.split(",");
					if (fields.length == itemsPerLine) { 
						StringBuffer sb = new StringBuffer();
						String timestamp;
						for (int i = 0; i < itemsPerLine; i++) {
							if (i == 1) {// time field idx
								String time = fields[i];
								timestamp = date + " " + time;
								sb.append(timestamp);
							} else {
								sb.append(fields[i]);
							}
							if (i < (itemsPerLine-1))
								sb.append(",");
						}
						osw.write(sb.append("\n").toString());
					}
					content = isr.readLine();
				}
			}
			isr.close();
			osw.close();

		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	// need to generate getter and setter for task serialization, if not this
	// will be null
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	private static String submitTasks(int batchId, String datePart, String propfile, List<Task> tl, CrawlConf cconf, String cmd){
		String taskName = QuotePostProcessTask.class.getSimpleName()+ "_" + cmd + "_" + datePart + "_" + batchId;
		int mbMem = 1024;
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		return CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, taskName, false, hadoopJobParams);
	}
	
	// return jobId list
	public static String[] launch(String propfile, CrawlConf cconf, String datePart, String[] cmds) {
		NodeConf nc = cconf.getNodeConf();
		Configuration conf = HadoopTaskLauncher.getHadoopConf(nc);
		// generate task list file
		FileSystem fs;
		try {
			// generate the task file
			fs = FileSystem.get(conf);
			List<String> jobIdList = new ArrayList<String>();
			for (String cmd: cmds){
				String root = StockConfig.RAW_ROOT + "/" + datePart + "/" + cmd + "/";
				RemoteIterator<LocatedFileStatus> fsit = fs.listFiles(new Path(root), true);
				List<Task> tl = new ArrayList<Task>();
				int batchId = 0;
				while (fsit.hasNext()) {
					LocatedFileStatus lfs = fsit.next();
					QuotePostProcessTask t = new QuotePostProcessTask(lfs.getPath().toString());
					tl.add(t);
					if (tl.size() >= ETLUtil.maxBatchSize) {
						jobIdList.add(submitTasks(batchId, datePart, propfile, tl, cconf, cmd));
						batchId++;
						tl = new ArrayList<Task>();
					}
				}
				jobIdList.add(submitTasks(batchId, datePart, propfile, tl, cconf, cmd));
			}
			String[] jobIds = new String[jobIdList.size()];
			return jobIdList.toArray(jobIds);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
}
