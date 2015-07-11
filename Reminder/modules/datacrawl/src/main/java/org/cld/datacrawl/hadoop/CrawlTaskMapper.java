package org.cld.datacrawl.hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;

public class CrawlTaskMapper extends Mapper<Object, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(CrawlTaskMapper.class);
	
	private CrawlConf cconf = null;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		if (cconf==null){
			String propFile = context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES);
			logger.info(String.format("conf file for mapper job is %s", propFile));
			cconf = CrawlTestUtil.getCConf(propFile);
		}
	}
	
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String taskJson = value.toString();
		Task t = TaskUtil.taskFromJson(taskJson);
		logger.info("I get task:" + t);
		Map<String, Object> crawlTaskParams = new HashMap<String, Object>();
		crawlTaskParams.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
		Map<String, String> hadoopCrawlTaskParams = new HashMap<String, String>();
		hadoopCrawlTaskParams.put(CrawlUtil.CRAWL_PROPERTIES, context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES));
		try{
			if (t instanceof BrowseProductTaskConf){
				BrowseProductTaskConf bpt = (BrowseProductTaskConf) t;
				List<String[]> csv = bpt.runMyselfFromMapred(crawlTaskParams);
				if (csv!=null){
					for (String[] v: csv){
						if (v[1]!=null && !"".equals(v[1]))
							context.write(new Text(v[0]), new Text(v[1]));
					}
				}else{
					//called from mapred, but no output specified.
				}
			}else{//for other types
				List<Task> tl = t.runMyself(crawlTaskParams, null);
				if (tl!=null && tl.size()>0){
					HadoopTaskUtil.executeTasks(cconf.getNodeConf(), tl, hadoopCrawlTaskParams, t.getId());
				}
				logger.info(String.format("I finished and send out %d tasks.", tl!=null?tl.size():0));
			}
		}catch(RuntimeException re){
			logger.error("runtime excpetion caught, mark this job error.", re);
			throw re;
		}
	}
}
