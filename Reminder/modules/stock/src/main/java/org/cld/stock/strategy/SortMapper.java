package org.cld.stock.strategy;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SortMapper extends Mapper<Object, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(SortMapper.class);
	
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String v = value.toString();
		String vs[] = v.split(",");
		String ratio = vs[vs.length-2];
		context.write(new Text(ratio), value);
	}
}
