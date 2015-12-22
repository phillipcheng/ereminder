package org.cld.taskmgr.hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;

public class TaskMapper extends Mapper<Object, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(TaskMapper.class);
	
	private TaskConf tconf = null;
	private MultipleOutputs<Text, Text> mos;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		if (tconf==null){
			String propFile = context.getConfiguration().get(TaskUtil.TASKCONF_PROPERTIES);
			logger.info(String.format("conf file for mapper job is %s", propFile));
			tconf = TaskUtil.getTaskConf(propFile);
		}
		mos = new MultipleOutputs<Text,Text>(context);
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        mos.close();
    }
	
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String taskJson = value.toString();
		Task t = TaskUtil.taskFromJson(taskJson);
		if (t.getConfName()!=null){
			Map<String, Object> taskParams = new HashMap<String, Object>();
			tconf.getTaskMgr().setUpSite(t.getConfName(), null, this.getClass().getClassLoader(), taskParams);
		}
		logger.info("I get task:" + t);
		Map<String, Object> crawlTaskParams = new HashMap<String, Object>();
		crawlTaskParams.put(TaskMgr.TASK_RUN_PARAM_CCONF, tconf);
		Map<String, String> hadoopCrawlTaskParams = new HashMap<String, String>();
		hadoopCrawlTaskParams.put(TaskUtil.TASKCONF_PROPERTIES, context.getConfiguration().get(TaskUtil.TASKCONF_PROPERTIES));
		try{
			t.initParsedTaskDef();
			if (!t.hasOutput()){
				List<Task> tl = t.runMyself(crawlTaskParams, null);
				if (tl!=null && tl.size()>0){
					HadoopTaskLauncher.executeTasks(tconf, tl, hadoopCrawlTaskParams, null, false, this.getClass(), null);
				}
				logger.info(String.format("I finished and send out %d tasks.", tl!=null?tl.size():0));
			}else {
				t.runMyselfAndOutput(crawlTaskParams, context, mos);
			}
		}catch(RuntimeException re){
			logger.error("runtime excpetion caught, mark this job error.", re);
			throw re;
		}
	}
}
