package org.cld.taskmgr.hadoop;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.util.IdUtil;
import org.cld.util.StringUtil;

public class HadoopTaskUtil {

	private static Logger logger =  LogManager.getLogger(HadoopTaskUtil.class);
	
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
		conf.set("mapreduce.task.timeout", "0");
		conf.setInt(NLineInputFormat.LINES_PER_MAP, taskMgr.getCrawlTasksPerMapper());
		return conf;
	}
	
	public static void executeTasks(NodeConf nc, List<Task> taskList, Map<String, String> params){
		String sourceName = "";
		for (int i=0; i<taskList.size(); i++){
			Task t = taskList.get(i);
			if (i==0){
				sourceName = t.getId();
			}else{
				sourceName += "__" + t.getId();
			}
		}
		executeTasks(nc, taskList, params, sourceName);
	}
	/**
	 * send the taskList to the cluster
	 * @param nc
	 * @param taskList
	 * @param params
	 * @param sourceName: the source/generator name, used as the generated task info file name
	 */
	public static void executeTasks(NodeConf nc, List<Task> taskList, Map<String, String> params, String sourceName){
		TaskMgr taskMgr = nc.getTaskMgr();
		Configuration conf = getHadoopConf(nc);
		//generate task list file
		FileSystem fs;
		try {
			//generate the task file
			fs = FileSystem.get(conf);
			String fileContent = "";
			for (Task t: taskList){
				String fn = TaskUtil.taskToJson(t);
				fileContent += fn + "\n";
			}
			String escapedName = StringUtil.escapeFileName(sourceName);
			String fileName = taskMgr.getHdfsTaskFolder() + "/" + IdUtil.getId(escapedName);
			logger.info(String.format("task file: %s with length %d generated.", fileName, fileContent.length()));
			Path fileNamePath = new Path(fileName);
			FSDataOutputStream fin = fs.create(fileNamePath);
			fin.writeBytes(fileContent);
			fin.close();
			
			//submit to the hadoop cluster
			for(String key: params.keySet()){
				conf.set(key, params.get(key));
				logger.info(String.format("add conf entry: %s, %s", key, params.get(key)));
			}
			
			Job job = Job.getInstance(conf, sourceName);
			//add app specific jars to classpath
			for (String s: nc.getTaskMgr().getYarnAppCp()){
				job.addFileToClassPath(new Path(s));
			}
			String className = taskMgr.getHadoopTaskMapperClassName();
			Class<? extends Mapper> mapperClazz = (Class<? extends Mapper>) Class.forName(className);
			job.setJarByClass(mapperClazz);
			job.setMapperClass(mapperClazz);
			job.setNumReduceTasks(0);//no reducer
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(LongWritable.class);
			job.setOutputFormatClass(NullOutputFormat.class);
			job.setInputFormatClass(NLineInputFormat.class);
			Path in = new Path(fileName);
			FileInputFormat.addInputPath(job, in);
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
