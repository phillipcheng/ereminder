package org.cld.hadooputil;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileNameFilter implements PathFilter{
	private static Logger logger =  LogManager.getLogger(FileNameFilter.class);
	private String key = null;
	
	public FileNameFilter(String key){
		this.key = key;
	}
	
	@Override
	public boolean accept(Path p) {
		String name = p.getName();
		if (name.contains(key)){
			return true;
		}
		return false;
	}	
}