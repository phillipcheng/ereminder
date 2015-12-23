package org.cld.hadooputil;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NullKeyCopyTextReducer extends Reducer<Text,Text,NullWritable,Text>{

	public static final Logger logger = LogManager.getLogger(NullKeyCopyTextReducer.class);
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		try{
			for (Text value: values){
				context.write(NullWritable.get(), value);
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
