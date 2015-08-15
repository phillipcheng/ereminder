package org.cld.stock.sina.jobs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datastore.entity.CrawledItem;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;


public class IPODateMapper extends Mapper<Object, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(IPODateMapper.class);
	public static String sep = "_";
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
    protected void cleanup(Context context) throws IOException, InterruptedException {
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
			t.initParsedTaskDef(crawlTaskParams);
			List<CrawledItem> cilist = t.runMyselfWithOutput(crawlTaskParams, false);
			if (cilist!=null && cilist.size()==1){
				CrawledItem ci = cilist.get(0);
				List<String[]> csvlist = ci.getCsvValue();
				if (csvlist!=null && csvlist.size()>0){
					for (String[] csvKV:csvlist){
						String csvV = csvKV[1];
						String csvK = csvKV[0];
						context.write(new Text(csvK), new Text(csvV));
					}
				}
			}
		}catch(RuntimeException re){
			logger.error("runtime excpetion caught, mark this job error.", re);
			throw re;
		}
	}
	
}
