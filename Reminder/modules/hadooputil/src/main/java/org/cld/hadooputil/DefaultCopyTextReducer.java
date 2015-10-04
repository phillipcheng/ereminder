package org.cld.hadooputil;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultCopyTextReducer extends Reducer<Text,Text,Text,Text>{

	public static final Logger logger = LogManager.getLogger(DefaultCopyTextReducer.class);
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context ) throws IOException, InterruptedException {
		logger.info(String.format("in reducer, key:%s, values:%s", key, values));
		//start the script configured passing the parameters
		try{
			for (Text value: values){
				context.write(key, value);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
