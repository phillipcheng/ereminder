package org.cld.stock.sina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.CsvTransformType;

public class MultiOutputPostProcess {
	private static Logger logger =  LogManager.getLogger(MultiOutputPostProcess.class);
	
	private static String whichPrefix(String[] filePrefix, String srcFileName){
		for (String fp:filePrefix){
			if (srcFileName.startsWith(fp)){
				return fp;
			}
		}
		return null;
	}
	
	//rootFolder/date-folder/filePrefix-m-000  => rootFolder/filePrefix/date-folder/part-m-000
	public static void splitFolder(CrawlConf cconf, String rootFolder, String[] filePrefix){
		
		Configuration conf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
		FileSystem fs;
		try {
			Path rootPath = new Path(rootFolder);
			//generate the task file
			fs = FileSystem.get(conf);
			RemoteIterator<LocatedFileStatus> fsList = fs.listFiles(rootPath, true);
			while(fsList.hasNext()){
		        LocatedFileStatus fileStatus = fsList.next();
		        Path srcPath = fileStatus.getPath();
				String srcFileName = srcPath.getName();
				String datePart = srcPath.getParent().getName();
				Path tryRoot = srcPath.getParent().getParent();
				Path tryRootSimple = Path.getPathWithoutSchemeAndAuthority(tryRoot);
				String fp = whichPrefix(filePrefix, srcFileName);
				if (tryRootSimple.compareTo(rootPath)==0 && fp!=null){
					String dstFileName = srcFileName.replaceAll(fp, "part");
					Path destPath = new Path(new Path(new Path(tryRoot, fp), datePart), dstFileName);
					Path destParentPath = destPath.getParent();
					if (!fs.exists(destParentPath)){
						fs.mkdirs(destParentPath);
					}
					boolean ret = fs.rename(srcPath, destPath);
					if (ret){
						logger.info(String.format("renamed %s to %s", srcPath, destPath));
					}
				}
			}
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public static void postProcessMultiOutput(CrawlConf cconf){
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(CrawlConf.taskParamCConf_Key, cconf);
		for (String conf: StockConfig.allConf){
			List<Task> tl = cconf.getTaskMgr().setUpSite(conf+".xml", null, MultiOutputPostProcess.class.getClassLoader(), taskParams);
			if (tl.size()>0){
				ParsedTasksDef ptd = tl.get(0).initParsedTaskDef(taskParams);
				Map<String, ParsedBrowsePrd> bptMap = ptd.getBrowsePrdTaskMap();
				for (ParsedBrowsePrd pbp: bptMap.values()){
					Map<String, AttributeType> attrMap = pbp.getPdtAttrMap();
					AttributeType at = attrMap.get("RowCsvName");
					if (at!=null){
						String csvnames = at.getValue().getValue();
						String[] fps = csvnames.split(",");
						if (fps.length>1){
							CsvTransformType csvt = pbp.getBrowsePrdTaskType().getBaseBrowseTask().getCsvtransform();
							if (csvt!=null){
								String outputDirExp = csvt.getOutputDir().getValue();
								int startIdx = outputDirExp.indexOf("sina-stock");
								int endIdx = outputDirExp.indexOf("/");
								if (startIdx!=-1 && endIdx!=-1){
									String rootFolder = cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" + outputDirExp.substring(startIdx, endIdx);
									MultiOutputPostProcess.splitFolder(cconf, rootFolder, fps);
								}
							}
						}
					}
				}
			}
		}
	}

}
