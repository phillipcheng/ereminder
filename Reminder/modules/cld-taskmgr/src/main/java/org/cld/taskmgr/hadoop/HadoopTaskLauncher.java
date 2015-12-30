package org.cld.taskmgr.hadoop;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.hadooputil.RegexFilter;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.util.StringUtil;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.CsvOutputType;

public class HadoopTaskLauncher {

	private static Logger logger =  LogManager.getLogger(HadoopTaskLauncher.class);
	public static final String NAMED_OUTPUT_TXT = "txt";
	private static final int DEFAULT_MB_MEM=1024;
	private static final int DEFAULT_TASKS_PER_JOB=2;
	
	public static boolean hasMultipleOutput(Task t){
		if (t.getParsedTaskDef()==null){//not a browse task
			return t.hasMultipleOutput();
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
	
	public static int getTaskPerJob(Task t){
		if (t.getParsedTaskDef()==null){//not a browse task
			return DEFAULT_TASKS_PER_JOB;
		}
		BrowseTaskType btt = t.getBrowseTask(t.getName());//
		return btt.getTaskNumPerJob();
	}
	
	public static Configuration getHadoopConf(TaskConf tconf){
		//all the site config should go here, since we can't point the etc/hadoop folder which contains the site config
		Configuration conf = new Configuration();
		if (tconf.getHadoopJobTracker()!=null){
			String jobTracker=tconf.getHadoopJobTracker();
			String host = jobTracker.substring(0,jobTracker.indexOf(":"));
			conf.set("mapreduce.jobtracker.address", tconf.getHadoopJobTracker());
			conf.set("yarn.resourcemanager.hostname", host);
			conf.set("mapreduce.framework.name", "yarn");
			conf.set("yarn.nodemanager.aux-services", "mapreduce_shuffle");
		}
		conf.set("fs.default.name", tconf.getHdfsDefaultName());
		conf.setInt(NLineInputFormat.LINES_PER_MAP, tconf.getTasksPerMapper());
		
		for (String key:tconf.getHadoopConfigs().keySet()){
			String value = tconf.getHadoopConfigs().get(key);
			//logger.info(String.format("key:%s,value:%s", key, value));
			conf.set(key, value);
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
	
	public static void updateHadoopParams(Task t, Map<String, String> hadoopParams){
		if (!hadoopParams.containsKey(NLineInputFormat.LINES_PER_MAP)){
			hadoopParams.put(NLineInputFormat.LINES_PER_MAP, getTaskPerJob(t)+"");
		}
		if (!hadoopParams.containsKey("mapreduce.map.memory.mb")){
			int mbMapperMem = getMbMemory(t);
			updateHadoopMemParams(mbMapperMem, hadoopParams);
		}
	}
	
	public static void updateHadoopMemParams(int mbMapperMem, Map<String, String> hadoopParams){
		String optValue = "-Xmx" + mbMapperMem + "M";
		hadoopParams.put("mapreduce.map.memory.mb", mbMapperMem+"");
		hadoopParams.put("mapreduce.map.java.opts", optValue);
		hadoopParams.put("mapreduce.reduce.memory.mb", mbMapperMem+"");
		hadoopParams.put("mapreduce.reduce.java.opts", optValue);
	}
	
	public static String executeTasks(TaskConf tconf, List<Task> taskList, Map<String, String> hadoopParams, 
			String sourceName, boolean sync, Class mapperClass, Class reducerClass){
		if (taskList.size()>0){
			Configuration conf = getHadoopConf(tconf);
			//generate task list file
			try {
				//generate the task file
				FileSystem fs = FileSystem.get(conf);
				String taskFileName = null;
				StringBuffer fileContent = new StringBuffer();
				for (Task t: taskList){
					String fn = TaskUtil.taskToJson(t);
					fileContent.append(fn).append("\n");
				}
				if (sourceName==null) sourceName = getSourceName(taskList);
				String escapedName = StringUtil.escapeFileName(sourceName);
				taskFileName = tconf.getHdfsTaskFolder() + "/" + escapedName;
				logger.info(String.format("task file: %s with length %d generated.", taskFileName, fileContent.length()));
				Path fileNamePath = new Path(taskFileName);
				OutputStream fin = fs.create(fileNamePath);
				BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fin, "UTF-8" ));
				br.write(fileContent.toString());
				br.close();
				logger.info("before update hadoop params:" + hadoopParams);
				Task t = taskList.get(0);
				updateHadoopParams(t, hadoopParams);
				logger.info("after update hadoop params:" + hadoopParams);
				boolean multipleOutput = hasMultipleOutput(t);
				String outputDir = t.getOutputDir(null);
				return hadoopExecuteTasks(tconf, hadoopParams, new String[]{taskFileName}, multipleOutput, outputDir, sync, mapperClass, reducerClass, true);
			}catch (Exception e) {
				logger.error("", e);
			}
		}
		return null;
	}
	
	/**
	 * general mr job lanuch
	 * @param nc
	 * @param hadoopParams
	 * @param inputPaths
	 * ...
	 * @return jobId
	 */
	public static final String FILTER_REGEXP_KEY="file.pattern";
	public static String hadoopExecuteTasks(TaskConf tconf, Map<String, String> hadoopParams, 
			String[] inputPaths, boolean multipleOutput, String outputDir, 
			boolean sync, Class<? extends Mapper> mapperClass, Class<? extends Reducer> reducerClass, boolean uselinesPerMap){
		return hadoopExecuteTasks(tconf, hadoopParams, inputPaths, multipleOutput, outputDir, sync, mapperClass, reducerClass, null, null, null, uselinesPerMap);
	}
	
	public static String hadoopExecuteTasks(TaskConf tconf, Map<String, String> hadoopParams, 
			String[] inputPaths, boolean multipleOutput, String outputDir, 
			boolean sync, 
			Class<? extends Mapper> mapperClass, 
			Class<? extends Reducer> reducerClass, 
			Class<? extends Partitioner> partitionerClass, 
			Class<? extends RawComparator> groupComparatorClass, 
			Class mapperOutputKeyClass, boolean uselinesPerMap){
		try{
			Configuration conf = getHadoopConf(tconf);
			FileSystem fs = FileSystem.get(conf);
			if (hadoopParams!=null){
				for(String key: hadoopParams.keySet()){
					conf.set(key, hadoopParams.get(key));
					//logger.info(String.format("add conf entry: %s, %s", key, hadoopParams.get(key)));
				}
			}
			Job job = Job.getInstance(conf, inputPaths[0]+"|"+inputPaths.length);
			if (uselinesPerMap){
				job.setInputFormatClass(NLineInputFormat.class);
			}
			//add app specific jars to classpath
			if (tconf.getYarnAppCp()!=null){
				for (String s: tconf.getYarnAppCp()){
					//find all the jar,zip files under s if s is a directory
					FileStatus[] fslist = fs.listStatus(new Path(s));
					Path[] plist = FileUtil.stat2Paths(fslist);
					for (Path p:plist){
						job.addFileToClassPath(p);
					}
				}
			}
			job.setJarByClass(mapperClass);
			job.setMapperClass(mapperClass);
			if (mapperOutputKeyClass!=null){
				job.setMapOutputKeyClass(mapperOutputKeyClass);
			}
			if (reducerClass==null){
				job.setNumReduceTasks(0);//no reducer
			}else{
				job.setReducerClass(reducerClass);
				//job.setNumReduceTasks(1);
			}
			if (partitionerClass!=null){
				job.setPartitionerClass(partitionerClass);
			}
			if (groupComparatorClass!=null){
				job.setGroupingComparatorClass(groupComparatorClass);
			}
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			if (multipleOutput){
				MultipleOutputs.addNamedOutput(job, NAMED_OUTPUT_TXT, TextOutputFormat.class, Text.class, Text.class);
			}
			FileInputFormat.setInputDirRecursive(job, true);
			if (hadoopParams!=null){
				if (hadoopParams.containsKey(FILTER_REGEXP_KEY)){
					FileInputFormat.setInputPathFilter(job, RegexFilter.class);
				}
			}
			for (String tfn:inputPaths){
				Path in = new Path(tfn);
				FileInputFormat.addInputPath(job, in);
			}
			
			if (outputDir!=null && !"".equals(outputDir)){
				logger.info(String.format("going to be deleted!!! output dir is %s", outputDir));
				Path out = null;
				if (outputDir.startsWith("/")){
					out = new Path(outputDir);
				}else{
					out = new Path(tconf.getHadoopCrawledItemFolder() + "/" + outputDir);
				}
				fs.delete(out, true);
				FileOutputFormat.setOutputPath(job, out);
			}else{
				job.setOutputFormatClass(NullOutputFormat.class);
			}
			if (tconf.getHadoopJobTracker()!=null && !sync){
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
