package org.cld.taskmgr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TaskConf {
	private static Logger logger =  LogManager.getLogger(TaskConf.class);
	//keys
	public static final String hdfsDefaultName_Key= "hadoop.hdfs.default.name";//hadoop.hdfs.default.name=hdfs://localhost:19000
	public static final String hdfsTaskFolder_Key = "hdfs.task.folder";//hdfs.task.folder:/reminder/task
	public static final String hadoopJobTracker_Key = "hadoop.job.tracker";//hadoop.job.tracker:localhost:9001
	public static final String hadoopTaskMapperClassName_Key="task.mapper.class";
	public static final String yarnApplicationClasspath_Key="yarn.application.classpath";
	public static final String hdfsCrawledItemFolder_Key="hdfs.crawleditem.folder";
	public static final String tasksPerMapper_Key = "task.per.mapper"; //number of tasks/lines each mapper will process
	
	private String hdfsDefaultName = null;
	private String hdfsTaskFolder = null;
	private String hadoopJobTracker = null;
	private String hadoopTaskMapperClassName = null;
	private String[] yarnAppCp = null;
	private String hadoopCrawledItemFolder=null;//: /reminder/items
	private int tasksPerMapper = 1;
	private Map<String, String> hadoopConfigs = new HashMap<String,String>();

	public TaskConf(String propertyFile){
		try{
			PropertiesConfiguration properties = new PropertiesConfiguration(propertyFile);
			
			Iterator<String> enu = properties.getKeys();
			
			while(enu.hasNext()){
				String key = enu.next();	
				String strVal = properties.getString(key);
				logger.debug(String.format("key:%s, value:%s", key, strVal));
				if (hdfsTaskFolder_Key.equals(key)){
					this.hdfsTaskFolder = strVal;
				}else if (hdfsDefaultName_Key.equals(key)){
					this.hdfsDefaultName = strVal;
				}else if (hadoopJobTracker_Key.equals(key)){
					this.hadoopJobTracker = strVal;
				}else if (hadoopTaskMapperClassName_Key.equals(key)){
					this.setHadoopTaskMapperClassName(strVal);
				}else if (hdfsCrawledItemFolder_Key.equals(key)){
					this.setHadoopCrawledItemFolder(strVal);
				}else if (tasksPerMapper_Key.equals(key)){
					this.setTasksPerMapper(Integer.parseInt(strVal));
				}else if (yarnApplicationClasspath_Key.equals(key)){
					this.setYarnAppCp(properties.getStringArray(key));
				}else if (key.startsWith("yarn.") || key.startsWith("mapred.") || key.startsWith("mapreduce.") || key.startsWith("dfs.")){
					hadoopConfigs.put(key, strVal);
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public String getHdfsTaskFolder() {
		return hdfsTaskFolder;
	}

	public void setHdfsTaskFolder(String hdfsTaskFolder) {
		this.hdfsTaskFolder = hdfsTaskFolder;
	}

	public String getHdfsDefaultName() {
		return hdfsDefaultName;
	}

	public void setHdfsDefaultName(String hdfsDefaultName) {
		this.hdfsDefaultName = hdfsDefaultName;
	}

	public String getHadoopJobTracker() {
		return hadoopJobTracker;
	}

	public void setHadoopJobTracker(String hadoopJobTracker) {
		this.hadoopJobTracker = hadoopJobTracker;
	}

	public String getHadoopTaskMapperClassName() {
		return hadoopTaskMapperClassName;
	}

	public void setHadoopTaskMapperClassName(String hadoopTaskMapperClassName) {
		this.hadoopTaskMapperClassName = hadoopTaskMapperClassName;
	}


	public String getHadoopCrawledItemFolder() {
		return hadoopCrawledItemFolder;
	}

	public void setHadoopCrawledItemFolder(String hadoopCrawledItemFolder) {
		this.hadoopCrawledItemFolder = hadoopCrawledItemFolder;
	}

	public int getTasksPerMapper() {
		return tasksPerMapper;
	}

	public void setTasksPerMapper(int tasksPerMapper) {
		this.tasksPerMapper = tasksPerMapper;
	}

	public String[] getYarnAppCp() {
		return yarnAppCp;
	}

	public void setYarnAppCp(String[] yarnAppCp) {
		this.yarnAppCp = yarnAppCp;
	}
	
	public Map<String, String> getHadoopConfigs() {
		return hadoopConfigs;
	}
	
	public abstract TaskMgr getTaskMgr();
	
}
