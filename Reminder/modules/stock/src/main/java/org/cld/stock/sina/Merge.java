package org.cld.stock.sina;

import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.hadoop.HadoopUtil;

public class Merge {
	private static Logger logger =  LogManager.getLogger(Merge.class);
	
	//run_merge("sd-ed")
	//RAW_ROOT/sd-ed/storeid/.../part-00, MERGE_ROOT/storeid/sd-ed/.../merged
	//RAW_ROOT/sd-ed/storeid/.../xxx-00, MERGE_ROOT/storeid/xxx/sd-ed/.../merged  (multiple output)
	//
	public static void run_merge(CrawlConf cconf, String datePart){
		Configuration hconf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
		try {
			FileSystem fs = FileSystem.get(hconf);
			Path fromDir = new Path(StockConfig.RAW_ROOT+"/"+datePart);
			FileStatus[] fsList = fs.listStatus(fromDir);
			for (FileStatus store: fsList){
				if (!store.isFile()){
					logger.info(String.format("store %s found.", store.getPath().toString()));
					String storeid = store.getPath().getName();
					Path storePath = store.getPath();
					if (StockConfig.SINA_STOCK_TRADE_DETAIL.equals(storeid)){//trade detail has been post-processed, so the input folder changed
						storePath = new Path(storePath.toString().replace("raw", "postprocess"));
					}
					Path[] leafDirs = HadoopUtil.getLeafPath(fs, storePath);
					for (Path lp : leafDirs){
						logger.info(String.format("leaf dir found %s", lp.toString()));
						String[] prefixes = HadoopUtil.getOutputPrefix(fs, lp);
						if (prefixes!=null){
							logger.info("prefixes got:" + Arrays.toString(prefixes));
							String strLP = lp.toString();
							String midPart = strLP.substring(strLP.indexOf(storeid)+storeid.length(), strLP.length());
							logger.info("midPart:" + midPart);
							if (prefixes.length>1){
								//multiple output
								for (String prefix:prefixes){
									Path dest = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + prefix + "/" + datePart + "/" + midPart + "/merged");
									Path[] srcs = HadoopUtil.getFilesWithPrefix(fs, lp, prefix);
									FileUtil.copy(fs, srcs, fs, dest, false, true, hconf);
								}
							}else{
								//single output: mapreduce or id output
								//src dir is lp
								Path dest = new Path(StockConfig.MERGE_ROOT + "/" + storeid + "/" + datePart + "/" + midPart + "/merged");
								FileUtil.copyMerge(fs, lp, fs, dest, false, hconf, "");
							}
						}
					}
				}
			}
		}catch(IOException e){
			logger.error("", e);
		}
	}

}
