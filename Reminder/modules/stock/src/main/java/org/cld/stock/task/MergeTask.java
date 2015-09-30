package org.cld.stock.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.hadooputil.HadoopUtil;
import org.cld.stock.ETLUtil;
import org.cld.stock.StockConfig;

@Entity
@DiscriminatorValue("org.cld.stock.sina.task.MergeTask")
public class MergeTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(MergeTask.class);
	public static final String sep ="-";
	
	private String leafDir;
	private boolean needOverwrite;
	private String datePart;
	private String storeid;

	private CrawlConf cconf;

	public MergeTask(){	
	}
	
	public MergeTask(String leafDir, String datePart, String storeid, boolean needOverwrite){
		this.leafDir = leafDir;
		this.datePart = datePart;
		this.storeid = storeid;
		this.needOverwrite = needOverwrite;
		genId();
	}
	
	@Override
	public String genId(){
		String inputId = leafDir;
		inputId = inputId.replace(":", sep);
		inputId = inputId.replace("/", sep);
		inputId = inputId.replace(".", sep);
		this.setId(inputId);
		return this.getId();
	}

	private void doWork(CrawlConf cconf){
		try{
			Configuration hconf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
			FileSystem fs = FileSystem.get(hconf);
			Path lp = new Path(leafDir);
			logger.info(String.format("leaf dir found %s", lp.toString()));
			String[] partNames = HadoopUtil.getPartNames(fs, lp);
			if (partNames!=null){
				logger.info("prefixes got:" + Arrays.toString(partNames));
				String strLP = lp.toString();
				String midPart = strLP.substring(strLP.indexOf(storeid)+storeid.length(), strLP.length());
				logger.info("midPart:" + midPart);
				if (partNames.length>1){
					//multiple output
					for (String partName:partNames){
						Path[] srcs = HadoopUtil.getFileWithNameContains(fs, lp, partName);
						Path destDir = null;
						Path destFile = null;
						if (!needOverwrite){
							destDir = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + partName + "/" + datePart + "/" + midPart + "/tmp/");
							destFile = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + partName + "/" + datePart + "/" + midPart + "/merge");
						}else{
							destDir = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + partName + "/" + midPart + "/tmp/");
							destFile = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + partName + "/" + midPart + "/merge");
						}
						if (!fs.exists(destDir)){
							fs.mkdirs(destDir);
						}
						FileUtil.copy(fs, srcs, fs, destDir, false, true, hconf);
						if (fs.exists(destFile)){
							fs.delete(destFile, false);
						}
						FileUtil.copyMerge(fs, destDir, fs, destFile, false, hconf, "");
						fs.delete(destDir, true);
					}
				}else{
					//single output: mapreduce or id output
					//src dir is lp
					Path dest = null;
					if (!needOverwrite){
						dest = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + datePart + "/" + midPart + "/merged");
					}else{
						dest = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + midPart + "/merged");
					}
					if (fs.exists(dest)){
						fs.delete(dest, false);
					}
					FileUtil.copyMerge(fs, lp, fs, dest, false, hconf, "");
				}
			}
			
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		doWork(cconf);
		return null;
	}
	
	//return jobId list
	public static String[] launch(StockConfig sc, String propfile, CrawlConf cconf, String datePart, String whichStore, boolean doMR){
		NodeConf nc = cconf.getNodeConf();
		Configuration conf = HadoopTaskLauncher.getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			List<Task> tl = new ArrayList<Task>();
			List<String> jobIdList = new ArrayList<String>();
			Path fromDir = new Path(StockConfig.RAW_ROOT+"/"+datePart);
			logger.info(String.format("root from path: %s", fromDir.toString()));
			logger.info(String.format("whichStore is: %s", whichStore));
			FileStatus[] fsList = fs.listStatus(fromDir);
			for (FileStatus store: fsList){
				if (!store.isFile()){
					logger.info(String.format("store %s found.", store.getPath().toString()));
					String storeid = store.getPath().getName();
					if (whichStore == null || (whichStore!=null && whichStore.equals(storeid))){
						boolean needOverwrite = ETLUtil.needOverwrite(cconf, storeid);
						Path storePath = store.getPath();
						if (Arrays.asList(sc.getPostProcessCmds()).contains(storeid)){
							//some cmd has post-processed, so the input folder changed
							storePath = new Path(storePath.toString().replace("raw", "postprocess"));
						}
						try{
							Path[] leafDirs = HadoopUtil.getLeafPath(fs, storePath);
							for (Path leafDir:leafDirs){
								MergeTask t = new MergeTask(leafDir.toString(), datePart, storeid, needOverwrite);
								tl.add(t);
							}
						}catch(Exception e){
							logger.warn(String.format("cmd %s data %s not exist, skip..", storeid, storePath));
						}
					}
				}
			}
			if (doMR){
				String taskName = MergeTask.class.getName() + "_" + datePart + "_" + whichStore;
				int mbMem = 3072;
				String optValue = "-Xmx" + mbMem + "M";
				Map<String, String> hadoopJobParams = new HashMap<String, String>();
				hadoopJobParams.put("mapreduce.map.speculative", "false");//since we do not allow same map multiple instance
				hadoopJobParams.put("mapreduce.map.memory.mb", mbMem+"");
				hadoopJobParams.put("mapreduce.map.java.opts", optValue);
				jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, taskName, false, hadoopJobParams));
				logger.info(String.format("sending out:%d tasks for hadoop task %s.", tl.size(), taskName));
				String[] jobIds = new String[jobIdList.size()];
				return jobIdList.toArray(jobIds);
			}else{
				for (Task t:tl){
					MergeTask mt = (MergeTask) t;
					mt.doWork(cconf);
				}
				return null;
			}
		}catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	//need to generate getter and setter for task serialization, if not this will be null
	public String getPathName() {
		return leafDir;
	}

	public void setPathName(String pathName) {
		this.leafDir = pathName;
	}
	public boolean isNeedOverwrite() {
		return needOverwrite;
	}
	public void setNeedOverwrite(boolean needOverwrite) {
		this.needOverwrite = needOverwrite;
	}
	public String getDatePart() {
		return datePart;
	}
	public void setDatePart(String datePart) {
		this.datePart = datePart;
	}
	public String getStoreid() {
		return storeid;
	}

	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}
}
