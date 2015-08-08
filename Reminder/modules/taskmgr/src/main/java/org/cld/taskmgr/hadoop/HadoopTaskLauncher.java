package org.cld.taskmgr.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.util.StringUtil;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.CsvOutputType;

public class HadoopTaskLauncher {

	private static Logger logger =  LogManager.getLogger(HadoopTaskLauncher.class);
	public static final String NAMED_OUTPUT_TXT = "txt";
	private static final int DEFAULT_MB_MEM=256;
	
	public static boolean hasMultipleOutput(Task t){
		if (t.getParsedTaskDef()==null){//not a browse task
			return false;
		}
		BrowseTaskType btt = t.getBrowseTask(t.getName());
		if (btt!=null){
			if (btt.getCsvtransform()!=null)
				return (CsvOutputType.BY_JOB_MULTI == btt.getCsvtransform().getOutputType());
			else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public static String getOutputDir(Task t){
		if (t.getParsedTaskDef()==null){//not a browse task
			return null;
		}
		BrowseTaskType btt = t.getBrowseTask(t.getName());
		if (btt!=null){
			if (btt.getCsvtransform()!=null){
				return (String)TaskUtil.eval(btt.getCsvtransform().getOutputDir(), t.getParamMap());
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	public static int getMbMemory(Task t){
		if (t.getParsedTaskDef()==null){//not a browse task
			return DEFAULT_MB_MEM;
		}
		BrowseTaskType btt = t.getBrowseTask(t.getName());
		if (btt!=null){
			return btt.getMbMemoryNeeded();
		}else{
			return DEFAULT_MB_MEM;
		}
	}
	
	public static Configuration getHadoopConf(NodeConf nc){
		//all the site config should go here, since we can't point the etc/hadoop folder which contains the site config
		TaskMgr taskMgr = nc.getTaskMgr();
		Configuration conf = new Configuration();
		if (taskMgr.getHadoopJobTracker()!=null){
			String jobTracker=taskMgr.getHadoopJobTracker();
			String host = jobTracker.substring(0,jobTracker.indexOf(":"));
			conf.set("mapreduce.jobtracker.address", taskMgr.getHadoopJobTracker());
			conf.set("yarn.resourcemanager.hostname", host);
			conf.set("mapreduce.framework.name", "yarn");
			conf.set("yarn.nodemanager.aux-services", "mapreduce_shuffle");
		}
		conf.set("fs.default.name", taskMgr.getHdfsDefaultName());
		conf.set("mapred.textoutputformat.separator", ",");//default is tab
		conf.set("mapreduce.task.timeout", "0");
		conf.setInt(NLineInputFormat.LINES_PER_MAP, taskMgr.getCrawlTasksPerMapper());
		for (String key:taskMgr.getHadoopConfigs().keySet()){
			String value = taskMgr.getHadoopConfigs().get(key);
			conf.set(key, value);
			logger.info(String.format("key:%s, value:%s", key, value));
		}
		return conf;
	}
	
	public static String getSourceName(List<Task> taskList){
		String sourceName = "";
		int max = 200;
		
		for (int i=0; i<taskList.size(); i++){
			String preSourceName = sourceName;
			Task t = taskList.get(i);
			if (i==0){
				sourceName = t.getId();
			}else{
				sourceName += "__" + t.getId();
			}
			if (sourceName.length()>max){
				sourceName = preSourceName;
				break;
			}
		}
		return sourceName;
	}
	
	public static String executeTasks(NodeConf nc, List<Task> taskList, Map<String, String> hadoopParams, 
			String sourceName, boolean sync){
		TaskMgr taskMgr = nc.getTaskMgr();
		Configuration conf = getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			String taskFileName = null;
			StringBuffer fileContent = new StringBuffer();
			for (Task t: taskList){
				String fn = TaskUtil.taskToJson(t);
				fileContent.append(fn).append("\n");
			}
			if (sourceName==null) sourceName = getSourceName(taskList);
			String escapedName = StringUtil.escapeFileName(sourceName);
			taskFileName = taskMgr.getHdfsTaskFolder() + "/" + escapedName;
			logger.info(String.format("task file: %s with length %d generated.", taskFileName, fileContent.length()));
			Path fileNamePath = new Path(taskFileName);
			FSDataOutputStream fin = fs.create(fileNamePath);
			fin.writeBytes(fileContent.toString());
			fin.close();
			return executeTasks(nc, hadoopParams, fs, new String[]{taskFileName}, taskList.get(0), sync);
		}catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	public static String executeTasksByFile(NodeConf nc, Map<String, String> hadoopParams, 
			String[] sourceName, Map<String, Object> cconfMap){
		TaskMgr taskMgr = nc.getTaskMgr();
		Configuration conf = getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			String[] taskFileName = new String[sourceName.length];
			for (int i=0; i<taskFileName.length; i++){
				taskFileName[i]= taskMgr.getHdfsTaskFolder() + "/" + sourceName[i];
			}
			Path taskPath = new Path(taskFileName[0]);
			if (fs.exists(taskPath)){
				FSDataInputStream input = fs.open(taskPath);
				String firstTaskStr = (new BufferedReader(new InputStreamReader(input))).readLine();
				input.close();
				Task t0 = TaskUtil.taskFromJson(firstTaskStr);
				logger.debug("firstTaskStr:" + firstTaskStr);
				logger.debug("t0:" + t0.toString());
				t0.initParsedTaskDef(cconfMap);
				return executeTasks(nc, hadoopParams,fs, taskFileName, t0, false);
			}else{
				logger.error(String.format("task file %s not exist.", taskFileName[0]));
			}
		}catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * send the taskList to the cluster
	 * @param nc
	 * @param taskList
	 * @param hadoopParams
	 * @param sourceName: the source/generator name, used as the generated task info file name, if taskList is null, the file already exists, just use it
	 * @param hdfsOutputDir
	 * @param multipleOutput
	 * @return jobId
	 */
	public static String executeTasks(NodeConf nc, Map<String, String> hadoopParams, FileSystem fs, 
			String[] taskFileName, Task t, boolean sync){
		try{
			TaskMgr taskMgr = nc.getTaskMgr();
			Configuration conf = getHadoopConf(nc);
			//submit to the hadoop cluster
			for(String key: hadoopParams.keySet()){
				conf.set(key, hadoopParams.get(key));
				logger.info(String.format("add conf entry: %s, %s", key, hadoopParams.get(key)));
			}
			boolean multipleOutput = hasMultipleOutput(t);
			String hdfsOutputDir = getOutputDir(t);
			int mbMem = getMbMemory(t);
			conf.setInt("mapreduce.map.memory.mb", mbMem);
			
			Job job = Job.getInstance(conf, taskFileName[0]+"|"+taskFileName.length);
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
			String className = taskMgr.getHadoopTaskMapperClassName();
			Class<? extends Mapper> mapperClazz = (Class<? extends Mapper>) Class.forName(className);


			job.setJarByClass(mapperClazz);
			job.setMapperClass(mapperClazz);
			job.setNumReduceTasks(0);//no reducer
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setInputFormatClass(NLineInputFormat.class);
			if (multipleOutput)
				MultipleOutputs.addNamedOutput(job, NAMED_OUTPUT_TXT, TextOutputFormat.class, Text.class, Text.class);
			for (String tfn:taskFileName){
				Path in = new Path(tfn);
				FileInputFormat.addInputPath(job, in);
			}
			if (hdfsOutputDir!=null){
				Path out = new Path(taskMgr.getHadoopCrawledItemFolder() + "/" + hdfsOutputDir);
				fs.delete(out, true);
				FileOutputFormat.setOutputPath(job, out);
			}else{
				job.setOutputFormatClass(NullOutputFormat.class);
			}
			if (taskMgr.getHadoopJobTracker()!=null && !sync){
				job.submit();
			}else{
				job.waitForCompletion(true);
			}
			return job.getJobID().toString();
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
}
