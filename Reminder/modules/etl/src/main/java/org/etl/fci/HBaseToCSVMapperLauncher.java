package org.etl.fci;

import org.cld.datastore.impl.HbaseDataStoreManagerImpl;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HBaseToCSVMapperLauncher {
	
	public static final String INPUT_TABLE_NAME="crawledItem";
	public static final String STOREID_FILTER="storeFilter";
	public static final String ID_FILTER="idFilter";
	public static final String ToCSVClass = "toCSVClass";
	
	private static Logger logger =  LogManager.getLogger(HBaseToCSVMapperLauncher.class);
	
	//
	public static void genCSVFromHbase(String prop, String outputFileName, String storeId, 
			String idFilter, String toCSVClazz){
		try{
			NodeConf nc = TaskUtil.getNodeConf(prop);
			TaskMgr tm = new TaskMgr();
			tm.loadConf(prop, null, null);
			Configuration conf = HadoopTaskUtil.getHadoopConf(nc);
			conf.set(TableInputFormat.INPUT_TABLE, INPUT_TABLE_NAME);
			conf.set(STOREID_FILTER, storeId);
			conf.set(ID_FILTER, idFilter);
			conf.set(ToCSVClass, toCSVClazz);
			Scan scan = new Scan();
			scan.addColumn(HbaseDataStoreManagerImpl.CRAWLEDITEM_CF_BYTES, HbaseDataStoreManagerImpl.CRAWLEDITEM_DATA_BYTES);
			Job job = Job.getInstance(conf, "ConvertJob");
			FileSystem fs = FileSystem.get(conf);
			if (tm.getYarnAppCp()!=null){
				for (String s: tm.getYarnAppCp()){
					//find all the jar,zip files under s if s is a directory
					FileStatus[] fslist = fs.listStatus(new Path(s));
					Path[] plist = FileUtil.stat2Paths(fslist);
					for (Path p:plist){
						job.addFileToClassPath(p);
					}
				}
			}
			TableMapReduceUtil.initTableMapperJob(INPUT_TABLE_NAME, scan, AbstractHbaseToCSVMapper.class, 
					Text.class, Text.class, job, false);
			job.setNumReduceTasks(0);
			job.setJarByClass(AbstractHbaseToCSVMapper.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			Path op = new Path(outputFileName);
			fs.delete(op, true);
			FileOutputFormat.setOutputPath(job, op);
			if (tm.getHadoopJobTracker()!=null){
				job.submit();
			}else{
				job.waitForCompletion(true);
			}
		}catch(Exception e){
			logger.error("", e);
		}		
		
	}

}
