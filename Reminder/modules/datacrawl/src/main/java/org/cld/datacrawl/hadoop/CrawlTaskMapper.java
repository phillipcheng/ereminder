package org.cld.datacrawl.hadoop;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.CsvOutputType;
import org.xml.taskdef.CsvTransformType;

public class CrawlTaskMapper extends Mapper<Object, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(CrawlTaskMapper.class);
	
	private CrawlConf cconf = null;
	private MultipleOutputs<Text, Text> mos;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		if (cconf==null){
			String propFile = context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES);
			logger.info(String.format("conf file for mapper job is %s", propFile));
			cconf = CrawlTestUtil.getCConf(propFile);
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
			taskParams.put(CrawlConf.taskParamCConf_Key, cconf);
			cconf.getTaskMgr().setUpSite(t.getConfName(), null, this.getClass().getClassLoader(), taskParams);
		}
		logger.info("I get task:" + t);
		Map<String, Object> crawlTaskParams = new HashMap<String, Object>();
		crawlTaskParams.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
		Map<String, String> hadoopCrawlTaskParams = new HashMap<String, String>();
		hadoopCrawlTaskParams.put(CrawlUtil.CRAWL_PROPERTIES, context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES));
		try{
			t.initParsedTaskDef();
			
			if (!(t instanceof BrowseProductTaskConf)){
				List<Task> tl = t.runMyself(crawlTaskParams, null);
				if (tl!=null && tl.size()>0){
					HadoopTaskLauncher.executeTasks(cconf.getNodeConf(), tl, hadoopCrawlTaskParams, null, false, this.getClass().getName(), null);
				}
				logger.info(String.format("I finished and send out %d tasks.", tl!=null?tl.size():0));
			}else {
				
				BrowseTaskType btt = null;
				if (t.getParsedTaskDef()==null){
					//not browse task
				}else{
					btt = t.getBrowseTask(t.getName());
					logger.info("btt:" + btt.getTaskName());
				}
				t.runMyselfAndOutput(crawlTaskParams, true, btt, context, mos);
			}
		}catch(RuntimeException re){
			logger.error("runtime excpetion caught, mark this job error.", re);
			throw re;
		}
	}
}
