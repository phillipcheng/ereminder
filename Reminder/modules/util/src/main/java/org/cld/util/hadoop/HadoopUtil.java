package org.cld.util.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class DirFilter implements PathFilter{	
	private static Logger logger =  LogManager.getLogger(DirFilter.class);
	private FileSystem fs;
	
	public DirFilter(FileSystem fs){
		this.fs = fs;
	}

	@Override
	public boolean accept(Path p) {
		try {
			FileStatus fst = fs.getFileStatus(p);
			return !fst.isFile();
		}catch(Exception e){
			logger.error("", e);
		}
		return false;
	}
}

class FirstFileFilter implements PathFilter{
	private static Logger logger =  LogManager.getLogger(FirstFileFilter.class);
	private FileSystem fs;
	private Set<String> prefixes = new HashSet<String>();
	
	public FirstFileFilter(FileSystem fs){
		this.fs = fs;
	}
	
	@Override
	public boolean accept(Path p) {
		//xxxx-m-000xx
		String name = p.getName();
		if (name.contains("-m-")){
			int idx = name.indexOf("-m-");
			if (idx>-1){
				String partName = name.substring(0, idx-1);
				if (prefixes.contains(partName)){
					return false;
				}else{
					prefixes.add(partName);
					return true;
				}
			}
		}else{
			//by id
		}
		return false;
	}	
}



public class HadoopUtil {
	
	private static Logger logger =  LogManager.getLogger(HadoopUtil.class);
	
	public static Path[] getLeafPath(FileSystem fs, Path start){
		List<Path> leafPaths = new ArrayList<Path>();
		List<Path> checkPaths = new ArrayList<Path>();
		checkPaths.add(start);
		DirFilter dfilter = new DirFilter(fs);
		while (checkPaths.size()>0){
			Path p = checkPaths.remove(0);
			try {
				FileStatus[] fstl = fs.listStatus(p, dfilter);
				if (fstl.length>0){
					//check whether this is leaf dir
					for (FileStatus fst:fstl){
						FileStatus[] subDirs = fs.listStatus(fst.getPath(), dfilter);
						if (subDirs.length==0){
							logger.info(String.format("add leaf dir: %s", fst.getPath().toString()));
							leafPaths.add(fst.getPath());
						}else{
							checkPaths.add(fst.getPath());
						}
					}
				}else{
					//p is a leaf dir
					logger.info(String.format("add leaf dir: %s", p.toString()));
					leafPaths.add(p);
				}
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		Path[] leafs = new Path[leafPaths.size()];
		return leafPaths.toArray(leafs);
	}
	
	//xxxx-m-000xx or none mapred output
	public static String[] getOutputPrefix(FileSystem fs, Path start){
		FirstFileFilter fffilter = new FirstFileFilter(fs);
		try {
			FileStatus[] fstl = fs.listStatus(start, fffilter);
			String[] retPrefix = new String[fstl.length];
			for (int i=0; i<fstl.length; i++){
				String name = fstl[i].getPath().getName();
				int idx = name.indexOf("-m-");
				if (idx>-1){
					String partName = name.substring(0, idx);
					retPrefix[i]=partName;
				}else{
					logger.error(String.format("first file filter returned name %s does not have prefix.", name));
				}
			}
			return retPrefix;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static Path[] getFilesWithPrefix(FileSystem fs, Path p, String prefix){
		try {
			FileStatus[] fsl = fs.listStatus(p, new FileNamePrefixFilter(prefix));
			Path[] paths = new Path[fsl.length];
			for (int i=0; i<fsl.length; i++){
				paths[i] = fsl[i].getPath();
			}
			return paths;
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}
}


