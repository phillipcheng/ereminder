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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;

import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;

import com.google.common.primitives.Ints;

/**
 * input lines of data
 * for consecutive n lines (1..n, 2..n+1, etc)
 * set the n+1 [lableIdx] as the lable, the n lines data (after removing the skipVIdx for each line) as the input
 * output in libsvm format
 * 
 *
 */
public class GenNdLable extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(GenNdLable.class);
	public static final String sep ="-";
	
	private String fileName;
	private int ndays=1;
	private int[] skipVIdx = null;
	private int lableIdx;

	private CrawlConf cconf;

	public GenNdLable(){	
	}
	
	public GenNdLable(String fileName, int ndays, int[] skipVIdx, int lableIdx){
		this.fileName = fileName;
		this.ndays = ndays;
		this.skipVIdx = skipVIdx;
		this.lableIdx = lableIdx;
		genId();
	}
	
	@Override
	public String genId(){
		String inputId = fileName + "_" + ndays;
		inputId = inputId.replace(":", sep);
		inputId = inputId.replace("/", sep);
		inputId = inputId.replace(".", sep);
		this.setId(inputId);
		return this.getId();
	}

	private void doWork(CrawlConf cconf){
		List<Integer> skipV = Ints.asList(skipVIdx);
		logger.info("skipVIdx:" + skipV);
		BufferedReader isr = null;
		BufferedWriter osw = null;
		try{
			Configuration hconf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
			FileSystem fs = FileSystem.get(hconf);
			String encoding = "gb2312";
			Path lp = new Path(fileName);
			Path op = new Path(cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" + GenNdLable.class.getSimpleName() + "/" + lp.getParent().getName() + "/" + ndays + "/" + lp.getName());
			isr= new BufferedReader(new InputStreamReader(fs.open(lp), encoding));
			osw = new BufferedWriter(new OutputStreamWriter(fs.create(op,true), encoding));
			String[] lines = new String[ndays];
			for (int k=0; k<ndays; k++){
				String line =isr.readLine();
				if ( line!= null){
					lines[k]=line;
				}else{
					break;
				}
			}
			String lastline = isr.readLine();
			while (lastline!=null){
				//work with n lines and lastline
				StringBuffer sb = new StringBuffer();
				String[] v2s = lastline.split(",");
				String[] vs = lines[ndays-1].split(",");
				double lableValue = Double.parseDouble(v2s[lableIdx])/Double.parseDouble(vs[lableIdx]);
				sb.append(Double.toString(lableValue));
				int inum=1;
				for (int k=0; k<ndays; k++){
					String[] v1s = lines[k].split(",");
					for (int i=0; i<v1s.length; i++){
						if (skipVIdx==null || !skipV.contains(i)){//filter v
							sb.append(" ");
							sb.append(inum);
							sb.append(":");
							sb.append(v1s[i]);
							inum++;
						}
					}
				}
				sb.append("\n");
				logger.info("a line:" + sb.toString());
				osw.write(sb.toString());
				for (int k=0; k<ndays-1; k++){
					lines[k] = lines[k+1];
				}
				lines[ndays-1]=lastline;
				lastline = isr.readLine();
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try {
				if (isr!=null){
					isr.close();
				}
				if (osw!=null){
					osw.close();
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
		doWork(cconf);
		return null;
	}
	
	public static String[] launch(String propfile, CrawlConf cconf, String specialParam, boolean doMR){
		String[] params = specialParam.split(",");
		if (params.length==4){
			String inputFolder = params[0];
			int ndays = Integer.parseInt(params[1]);
			String[] vs = params[2].split(":");
			int[] skipV = new int[vs.length];
			for (int i=0; i<skipV.length; i++){
				skipV[i] = Integer.parseInt(vs[i]);
			}
			int lableIdx = Integer.parseInt(params[3]);
			return launch(propfile, cconf, inputFolder, ndays, skipV, lableIdx, doMR);
		}else{
			logger.error(String.format("wrong params %s", specialParam));
			return null;
		}
		
	}
	//return jobId list
	public static String[] launch(String propfile, CrawlConf cconf, String inputFolder, int ndays, int[] skipV, int lableIdx, boolean doMR){
		NodeConf nc = cconf.getNodeConf();
		Configuration conf = HadoopTaskLauncher.getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			List<Task> tl = new ArrayList<Task>();
			List<String> jobIdList = new ArrayList<String>();
			Path fromDir = new Path(inputFolder);
			FileStatus[] fsList = fs.listStatus(fromDir);
			for (FileStatus f: fsList){
				if (f.isFile()){
					GenNdLable t = new GenNdLable(f.getPath().toString(), ndays, skipV, lableIdx);
					tl.add(t);
				}
			}
			if (doMR){
				String taskName = GenNdLable.class.getName() + "_" + inputFolder;
				Map<String, String> hadoopJobParams = new HashMap<String, String>();
				HadoopTaskLauncher.updateHadoopParams(3072, hadoopJobParams);
				jobIdList.add(CrawlUtil.hadoopExecuteCrawlTasks(propfile, cconf, tl, taskName, false, hadoopJobParams));
				logger.info(String.format("sending out:%d tasks for hadoop task %s.", tl.size(), taskName));
				String[] jobIds = new String[jobIdList.size()];
				return jobIdList.toArray(jobIds);
			}else{
				for (Task t:tl){
					GenNdLable mt = (GenNdLable) t;
					mt.doWork(cconf);
				}
				return null;
			}
		}catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	public int getNdays() {
		return ndays;
	}
	public void setNdays(int ndays) {
		this.ndays = ndays;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int[] getSkipVIdx() {
		return skipVIdx;
	}

	public void setSkipVIdx(int[] skipVIdx) {
		this.skipVIdx = skipVIdx;
	}

	public int getLableIdx() {
		return lableIdx;
	}

	public void setLableIdx(int lableIdx) {
		this.lableIdx = lableIdx;
	}
}
