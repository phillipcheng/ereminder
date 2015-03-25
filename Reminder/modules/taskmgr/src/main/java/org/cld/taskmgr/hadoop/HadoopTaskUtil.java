package org.cld.taskmgr.hadoop;

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
			conf.set("yarn.application.classpath", taskMgr.getYarnAppCp());
		}
		conf.set("fs.default.name", taskMgr.getHdfsDefaultName());
		conf.set("mapreduce.tasktracker.map.tasks.maximum", nc.getThreadSize() + "");
		conf.set("mapreduce.task.timeout", "0"); 
		return conf;
	}
	
	//send the taskList to the cluster
	public static void executeTasks(NodeConf nc, List<Task> taskList, Map<String, String> params){
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
			String fileName = taskMgr.getHdfsTaskFolder() + "/" + IdUtil.getId(nc.getNodeId());
			logger.info(String.format("task file: %s with length %d generated.", fileName, fileContent.length()));
			Path fileNamePath = new Path(fileName);
			FSDataOutputStream fin = fs.create(fileNamePath);
			fin.writeBytes(fileContent);
			fin.close();
			
			//submit to the hadoop cluster
			conf.setInt(NLineInputFormat.LINES_PER_MAP, 1);//default to 1
			for(String key: params.keySet()){
				conf.setStrings(key, params.get(key));
			}
			Job job = Job.getInstance(conf, "TaskJob");
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
