package org.cld.hadooputil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegexFilter extends Configured implements PathFilter {
 
    Pattern pattern;
    Configuration conf;
    FileSystem fs;
    
	private static Logger logger =  LogManager.getLogger(RegexFilter.class);
	
    @Override
    public boolean accept(Path path) {
    	try{
	    	if (fs.isDirectory(path)) {
	            return true;
	    	}else{
		        Matcher m = pattern.matcher(path.toString());
		        logger.info("Is path : " + path.toString() + " matching " + conf.get("file.pattern") + " ? , " + m.matches());
		        return m.matches();
	    	}
    	}catch(Exception e){
    		logger.error("", e);
    		return false;
    	}
    }
 
    @Override
    public void setConf(Configuration conf) {
    	if (conf!=null){
    		try{
	    		this.conf = conf;
	    		fs = FileSystem.get(conf);
	    		pattern = Pattern.compile(conf.get("file.pattern"));
    		}catch(Exception e){
    			logger.error("", e);
    		}
    	}
    }
}