package org.cld.stock.load;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReformatMapper extends Mapper<Object, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(ReformatMapper.class);
	
	@Override
	protected void map(Object key, Text value, Context context) 
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		int keyIdx = conf.getInt(ReformatMapredLauncher.KEY_INDEX, 0);
		String csv = value.toString();
		String[] values = csv.split(",");
		String realKey = values[keyIdx];
		logger.debug("key:" + realKey);
		context.write(new Text(realKey), value);
	}
}
