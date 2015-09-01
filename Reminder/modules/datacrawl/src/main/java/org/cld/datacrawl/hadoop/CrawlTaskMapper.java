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
			BrowseTaskType btt = null;
			CsvTransformType csvtrans = null;
			if (t.getParsedTaskDef()==null){
				//not browse task
			}else{
				btt = t.getBrowseTask(t.getName());
				csvtrans = btt.getCsvtransform();
			}
			if (csvtrans==null){
				List<Task> tl = t.runMyself(crawlTaskParams, null);
				if (tl!=null && tl.size()>0){
					HadoopTaskLauncher.executeTasks(cconf.getNodeConf(), tl, hadoopCrawlTaskParams, null, false, this.getClass().getName(), null);
				}
				logger.info(String.format("I finished and send out %d tasks.", tl!=null?tl.size():0));
			}else {
				List<CrawledItem> cilist = t.runMyselfWithOutput(crawlTaskParams, true);
				for (CrawledItem ci: cilist){
					CsvOutputType cot = csvtrans.getOutputType();
					List<String[]> csv = ci.getCsvValue();
					String outputDirPrefix=null;
					if (csv!=null){
						Map<String, BufferedWriter> hdfsByIdOutputMap = new HashMap<String, BufferedWriter>();
						FileSystem fs = null;
						try{
							if (cot == CsvOutputType.BY_ID){
								fs = FileSystem.get(context.getConfiguration());
								outputDirPrefix = cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" +
										HadoopTaskLauncher.getOutputDir(t) + "/" + ci.getId().getId();
							}
							for (String[] v: csv){
								if (v.length==2){
									if (v[1]!=null && !"".equals(v[1])){
										String csvkey = v[0];
										String csvvalue = v[1];
										if (cot != CsvOutputType.BY_ID){
											context.write(new Text(csvkey), new Text(csvvalue));
										}else{
											//
											String outputFile = outputDirPrefix;
											BufferedWriter br = null;
											if (hdfsByIdOutputMap.containsKey(outputFile)){
												br = hdfsByIdOutputMap.get(outputFile);
											}else{
												br = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(outputFile),true)));
												hdfsByIdOutputMap.put(outputFile, br);
											}
											if (AbstractCrawlItemToCSV.KEY_VALUE_UNDEFINED.equals(csvkey)){
												br.write(csvvalue + "\n");
											}else{
												br.write(csvkey + "," + csvvalue + "\n");
											}
										}
									}
								}else if (v.length==3){
									String outkey=v[0];
									String outvalue=v[1];
									String outfilePrefix=v[2];
									if (cot == CsvOutputType.BY_JOB_MULTI){
										mos.write(HadoopTaskLauncher.NAMED_OUTPUT_TXT, 
												new Text(outkey), new Text(outvalue), outfilePrefix);
									}else if (cot == CsvOutputType.BY_JOB_SINGLE){
										context.write(new Text(outkey), new Text(outvalue));
									}else if (cot == CsvOutputType.BY_ID){
										String outputFile = outputDirPrefix + "_" + outfilePrefix;
										BufferedWriter br = null;
										if (hdfsByIdOutputMap.containsKey(outputFile)){
											br = hdfsByIdOutputMap.get(outputFile);
										}else{
											br = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(outputFile),true)));
											hdfsByIdOutputMap.put(outputFile, br);
										}
										if (AbstractCrawlItemToCSV.KEY_VALUE_UNDEFINED.equals(outkey)){
											br.write(outvalue + "\n");
										}else{
											br.write(outkey + "," + outvalue + "\n");
										}
									}
								}else{
									logger.error("wrong number of csv length: not 2 and 3 but:" + v.length);
								}
							}
						}finally{
							for (BufferedWriter br: hdfsByIdOutputMap.values()){
								br.close();
							}
						}
					}else{
						//called from mapred, but no output specified.
					}
				}
			}
		}catch(RuntimeException re){
			logger.error("runtime excpetion caught, mark this job error.", re);
			throw re;
		}
	}
	
}
