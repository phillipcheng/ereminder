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
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MyPathFilter {
	private static Logger logger =  LogManager.getLogger(TransferHdfsFile.class);
	private String[] skipFolders;
	private String[] includeFolders;
	
	//check order include, exclude
	public MyPathFilter(String[] skipFolders, String[] includeFolders){
		this.skipFolders = skipFolders;
		this.includeFolders = includeFolders;
	}

	public boolean accept(String path) {
		try{
			if (includeFolders.length>0){
				boolean included = false;
				for (String includeFolder:includeFolders){
					if (path.contains(includeFolder)){
						included = true;
						break;
					}
				}
				if (!included)
					return false;
			}
			for (String skipFolder:skipFolders){
				if (path.contains(skipFolder)){
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
	private static Logger logger =  LogManager.getLogger(TransferHdfsFile.class);
	FileSystem fs;
	String fsDefaultName;
	Path hdfsRoot;
	File localRoot;
	String[] skipFolderNames;
	String[] includeFolderNames;
	LocatedFileStatus hdfsFl;//hdfs src
	File localFl; //local src
	boolean toHdfs;
	
	//hdfs://192.85.247.104:19000/reminder/items/merge/sina-stock-market-fq/hs_a_2015-10-21
	//C:\\mydoc\\mydata\\stock\\merge\\sina-stock-market-fq\\hs_a_2015-10-21
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
	
	private static List<String> getRelativePaths(File path, File root){
		List<String> names = new ArrayList<String>();
		File newPath = path;
		String strRoot = root.toString();
		while (!newPath.toString().equals(strRoot)){
			names.add(newPath.getName());
			newPath = newPath.getParentFile();
		}
		return names;
	}
	
	public DumpTask(String fsDefaultName, Path hdfsPath, File localRoot, FileSystem fs, 
			String[] skipFolderNames, String[] includeFolderNames, LocatedFileStatus fl, File localFl, boolean toHdfs){
		this.fs = fs;
		this.fsDefaultName = fsDefaultName;
		this.hdfsRoot = hdfsPath;
		this.localRoot = localRoot;
		this.skipFolderNames = skipFolderNames;
		this.includeFolderNames = includeFolderNames;
		this.hdfsFl = fl;
		this.localFl = localFl;
		this.toHdfs = toHdfs;
	}
	
	public String toString(){
		return String.format("hdfs Path:%s, localRoot: %s, skip:%s, include:%s, filePath:%s, toHdfs:%b", 
				hdfsRoot, localRoot, Arrays.toString(skipFolderNames), Arrays.toString(includeFolderNames), hdfsFl.getPath().toString(), toHdfs);
	}
	
	@Override
	public void run() {
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			
			MyPathFilter mpf = new MyPathFilter(skipFolderNames, includeFolderNames);
			Path hdfsFile = null;
			if (hdfsFl!=null)
				hdfsFile = hdfsFl.getPath();
			boolean goAhead=false;
			if (toHdfs){
				if (localFl.isFile() && mpf.accept(localFl.toString())){
					goAhead = true;
					List<String> names = getRelativePaths(localFl, localRoot);
					String strHdfsFile = hdfsRoot.toString();
					for (int i=0; i<names.size(); i++){
						strHdfsFile += Path.SEPARATOR + names.get(names.size()-1-i);
					}
					hdfsFile = new Path(strHdfsFile);
					Path parentFolder = hdfsFile.getParent();
					if (!fs.exists(parentFolder)){
						fs.mkdirs(parentFolder);
					}
				}
			}else{
				if (fs.isFile(hdfsFile) && mpf.accept(hdfsFile.toString())){
					goAhead = true;
					List<String> names = getRelativePaths(hdfsFl.getPath(), hdfsRoot, fsDefaultName);
					String strLocalFile = localRoot.toString();
					for (int i=0; i<names.size(); i++){
						strLocalFile += File.separator + names.get(names.size()-1-i);
					}
					localFl = new File(strLocalFile);
					File parentFolder = new File(localFl.getParent());
					if (!parentFolder.exists()){
						parentFolder.mkdirs();
					}
				}
			}
			if (goAhead){
				if (!toHdfs){//copy to local
					logger.info(String.format("start copying file from %s to %s", hdfsFl.getPath().toString(), localFl.toString()));
					FileUtil.copy(fs, hdfsFile, localFl, false, conf);
				}else{//copy to hdfs
					logger.info(String.format("start copying file from %s to %s", localFl.toString(), hdfsFile.toString()));
					FileUtil.copy(localFl, fs, hdfsFile, false, conf);
				}
				logger.info(String.format("finished copying file from %s", hdfsFile.toString()));
			}else{
				//logger.warn(String.format("fl:%s not accepted.", fl.getPath().toString()));
			}
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}

public class TransferHdfsFile {
	private static Logger logger =  LogManager.getLogger(TransferHdfsFile.class);
	
	public static String usage(){
		//DumpFiles 
		return String.format("%s threadNum fsDefaultName fromHdfsRoot toLocalRoot skipFolderNames includeFolderNames", TransferHdfsFile.class.getSimpleName());
	}
	
	public static void launch(int threadNum, String fsDefaultName, String hdfsRoot, String localRoot, 
			String[] includeFolderNames, String skipFolderNames[], boolean toHdfs){
		logger.info(String.format("dumpHdfsFile with includeFolderNames:%s, skipFolderNames:%s", 
				Arrays.asList(includeFolderNames), Arrays.asList(skipFolderNames)));
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			//FileSystem fs = FileSystem.get(conf);//fs can't be closed
			FileSystem fs = FileSystem.get(FileSystem.getDefaultUri(conf), conf, "dbadmin");
			Path fpHdfsRoot = new Path(hdfsRoot);
			if (!toHdfs){
				RemoteIterator<LocatedFileStatus> fls = fs.listFiles(fpHdfsRoot, true);
				while(fls.hasNext()){
					LocatedFileStatus fl = fls.next();
					DumpTask dt = new DumpTask(fsDefaultName, fpHdfsRoot, new File(localRoot), fs, skipFolderNames, includeFolderNames, fl, null, toHdfs);
					executor.submit(dt);
				}
			}else{
				File localRootFile = new File(localRoot);
				File[] localFiles = localRootFile.listFiles();
				for (File f: localFiles){
					DumpTask dt = new DumpTask(fsDefaultName, fpHdfsRoot, new File(localRoot), fs, skipFolderNames, includeFolderNames, null, f, toHdfs);
					executor.submit(dt);
				}
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
		String hdfsRoot = args[idx++];
		String localRoot = args[idx++];
		String strincludeFolders = args[idx++];
		String strSkipFolders = args[idx++];
		boolean toHdfs = Boolean.parseBoolean(args[idx++]); //
		
		String[] includeFolderNames = new String[]{};
		if (!strincludeFolders.equals("") && !strincludeFolders.equals("-")){
			includeFolderNames = strincludeFolders.split(",");
		}
		
		String[] skipFolderNames = new String[]{};
		if (!strSkipFolders.equals("") && !strSkipFolders.equals("-")){
			skipFolderNames = strSkipFolders.split(",");
		}
		
		launch(threadNum, fsDefaultName, hdfsRoot, localRoot, includeFolderNames, skipFolderNames, toHdfs);
	}
}
