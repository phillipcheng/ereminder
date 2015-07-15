package org.cld.taskmgr.hadoop;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
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
import org.cld.util.IdUtil;
import org.cld.util.StringUtil;

public class HadoopTaskLauncher {

	private static Logger logger =  LogManager.getLogger(HadoopTaskLauncher.class);
	public static final String NAMED_OUTPUT_TXT = "txt";
	
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
			
			/*
			conf.set("yarn.application.classpath", taskMgr.getYarnAppCp());
			//or
			Collection<String> yarncps = conf.getStringCollection("yarn.application.classpath");
			if (taskMgr.getYarnAppCp()!=null && !yarncps.contains(taskMgr.getYarnAppCp())){
				yarncps.add(taskMgr.getYarnAppCp());
				conf.setStrings("yarn.application.classpath", yarncps.toArray(new String[yarncps.size()]));
			}
			logger.debug(String.format("the yarn classpaths is:%s", conf.get("yarn.application.classpath")));
			*/
		}
		conf.set("fs.default.name", taskMgr.getHdfsDefaultName());
		conf.set("mapreduce.tasktracker.map.tasks.maximum", nc.getThreadSize() + "");
		conf.set("mapred.textoutputformat.separator", ",");//default is tab
		conf.set("mapreduce.task.timeout", "0");
		conf.setInt(NLineInputFormat.LINES_PER_MAP, taskMgr.getCrawlTasksPerMapper());
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
	
	/**
	 * send the taskList to the cluster
	 * @param nc
	 * @param taskList
	 * @param params
	 * @param sourceName: the source/generator name, used as the generated task info file name
	 * @param hdfsOutputDir
	 * @param multipleOutput
	 */
	public static void executeTasks(NodeConf nc, List<Task> taskList, Map<String, String> params, 
			String sourceName, String hdfsOutputDir, boolean multipleOutput){
		TaskMgr taskMgr = nc.getTaskMgr();
		Configuration conf = getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			StringBuffer fileContent = new StringBuffer();
			for (Task t: taskList){
				String fn = TaskUtil.taskToJson(t);
				fileContent.append(fn).append("\n");
			}
			if (sourceName==null) sourceName = getSourceName(taskList);
			String escapedName = StringUtil.escapeFileName(sourceName);
			String fileName = taskMgr.getHdfsTaskFolder() + "/" + IdUtil.getId(escapedName);
			logger.info(String.format("task file: %s with length %d generated.", fileName, fileContent.length()));
			Path fileNamePath = new Path(fileName);
			FSDataOutputStream fin = fs.create(fileNamePath);
			fin.writeBytes(fileContent.toString());
			fin.close();
			
			//submit to the hadoop cluster
			for(String key: params.keySet()){
				conf.set(key, params.get(key));
				logger.info(String.format("add conf entry: %s, %s", key, params.get(key)));
			}
			
			Job job = Job.getInstance(conf, sourceName);
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
			Path in = new Path(fileName);
			FileInputFormat.addInputPath(job, in);
			if (hdfsOutputDir!=null){
				Path out = new Path(taskMgr.getHadoopCrawledItemFolder() + "/" + hdfsOutputDir);
				fs.delete(out, true);
				FileOutputFormat.setOutputPath(job, out);
			}else{
				job.setOutputFormatClass(NullOutputFormat.class);
			}
			if (taskMgr.getHadoopJobTracker()!=null){
				job.submit();
			}else{
				job.waitForCompletion(true);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
