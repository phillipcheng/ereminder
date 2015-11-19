package org.cld.hadooputil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MyPathFilter implements PathFilter {
	private static Logger logger =  LogManager.getLogger(DumpHdfsFile.class);
	private String[] skipFolders;
	private String[] includeFolders;
	private FileSystem fs;
	
	//check order include, exclude
	public MyPathFilter(FileSystem fs, String[] skipFolders, String[] includeFolders){
		this.fs = fs;
		this.skipFolders = skipFolders;
		this.includeFolders = includeFolders;
	}

	@Override
	public boolean accept(Path path) {
		try{
			if (!fs.isFile(path)){
				return false;
			}
			
			if (includeFolders.length>0){
				boolean included = false;
				for (String includeFolder:includeFolders){
					if (path.toString().contains(includeFolder)){
						included = true;
						break;
					}
				}
				if (!included)
					return false;
			}
			for (String skipFolder:skipFolders){
				if (path.toString().contains(skipFolder)){
					return false;
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return true;
	}
}

class DumpTask implements Runnable {
	private static Logger logger =  LogManager.getLogger(DumpHdfsFile.class);
	FileSystem fs;
	String fsDefaultName;
	Path fromPath;
	String toLocalRoot;
	String[] skipFolderNames;
	String[] includeFolderNames;
	LocatedFileStatus fl;
	
	private static List<String> getRelativePaths(Path path, Path root, String fsDefaultName){
		List<String> names = new ArrayList<String>();
		Path newPath = path;
		String strRoot = fsDefaultName + root.toString();
		while (!newPath.toString().equals(strRoot)){
			names.add(newPath.getName());
			newPath = newPath.getParent();
		}
		return names;
	}
	
	public DumpTask(String fsDefaultName, Path fromPath, String toLocalRoot, FileSystem fs, 
			String[] skipFolderNames, String[] includeFolderNames, LocatedFileStatus fl){
		this.fs = fs;
		this.fsDefaultName = fsDefaultName;
		this.fromPath = fromPath;
		this.toLocalRoot = toLocalRoot;
		this.skipFolderNames = skipFolderNames;
		this.includeFolderNames = includeFolderNames;
		this.fl = fl;
	}
	
	public String toString(){
		return String.format("from Path:%s, toLocalRoot: %s, skip:%s, include:%s, filePath:%s", 
				fromPath, toLocalRoot, Arrays.toString(skipFolderNames), Arrays.toString(includeFolderNames), fl.getPath().toString());
	}
	@Override
	public void run() {
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			//logger.info(String.format("task:%s", this.toString()));
			MyPathFilter mpf = new MyPathFilter(fs, skipFolderNames, includeFolderNames);
			if (mpf.accept(fl.getPath())){
				//hdfs://192.85.247.104:19000/reminder/items/merge/sina-stock-market-fq/hs_a_2015-10-21
				//C:\\mydoc\\mydata\\stock\\merge\\sina-stock-market-fq\\hs_a_2015-10-21
				List<String> names = getRelativePaths(fl.getPath(), fromPath, fsDefaultName);
				String strDst = toLocalRoot;
				for (int i=0; i<names.size(); i++){
					strDst += File.separator + names.get(names.size()-1-i);
				}
				File dst = new File(strDst);
				File parentFolder = new File(dst.getParent());
				if (!parentFolder.exists()){
					parentFolder.mkdirs();
				}
				logger.info(String.format("start copying file from %s to %s", fl.getPath().toString(), dst.toString()));
				FileUtil.copy(fs, fl.getPath(), dst, false, conf);
				logger.info(String.format("finished copying file from %s", fl.getPath().toString()));
			}else{
				//logger.warn(String.format("fl:%s not accepted.", fl.getPath().toString()));
			}
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}

public class DumpHdfsFile {
	private static Logger logger =  LogManager.getLogger(DumpHdfsFile.class);
	
	public static String usage(){
		//DumpFiles 
		return String.format("%s threadNum fsDefaultName fromHdfsRoot toLocalRoot skipFolderNames includeFolderNames", DumpHdfsFile.class.getSimpleName());
	}
	
	public static void launch(int threadNum, String fsDefaultName, String fromHdfsRoot, String localDirRoot, 
			String[] includeFolderNames, String skipFolderNames[]){
		logger.info(String.format("dumpHdfsFile with includeFolderNames:%s, skipFolderNames:%s", 
				Arrays.asList(includeFolderNames), Arrays.asList(skipFolderNames)));
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			FileSystem fs = FileSystem.get(conf);//fs can't be closed
			Path fromPath = new Path(fromHdfsRoot);
			RemoteIterator<LocatedFileStatus> fls = fs.listFiles(fromPath, true);
			while(fls.hasNext()){
				LocatedFileStatus fl = fls.next();
				DumpTask dt = new DumpTask(fsDefaultName, fromPath, localDirRoot, fs, skipFolderNames, includeFolderNames, fl);
				executor.submit(dt);
			}
			executor.shutdown();
			executor.awaitTermination(2, TimeUnit.HOURS);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	public static void main(String[] args){
		
		int idx=0;
		int threadNum = Integer.parseInt(args[idx++]);
		String fsDefaultName = args[idx++];
		String fromHdfsRoot = args[idx++];
		String toLocalRoot = args[idx++];
		String strincludeFolders = args[idx++];
		String strSkipFolders = args[idx++];
		
		String[] includeFolderNames = new String[]{};
		if (!strincludeFolders.equals("") && !strincludeFolders.equals("-")){
			includeFolderNames = strincludeFolders.split(",");
		}
		
		String[] skipFolderNames = new String[]{};
		if (!strSkipFolders.equals("") && !strSkipFolders.equals("-")){
			skipFolderNames = strSkipFolders.split(",");
		}
		
		launch(threadNum, fsDefaultName, fromHdfsRoot, toLocalRoot, includeFolderNames, skipFolderNames);
		
	}

}
