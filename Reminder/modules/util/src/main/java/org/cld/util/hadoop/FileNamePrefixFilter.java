package org.cld.util.hadoop;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileNamePrefixFilter implements PathFilter{
	private static Logger logger =  LogManager.getLogger(FileNamePrefixFilter.class);
	private String prefix = null;
	
	public FileNamePrefixFilter(String prefix){
		this.prefix = prefix;
	}
	
	@Override
	public boolean accept(Path p) {
		//xxxx-m-000xx
		String name = p.getName();
		if (name.startsWith(prefix)){
			return true;
		}
		return false;
	}	
}