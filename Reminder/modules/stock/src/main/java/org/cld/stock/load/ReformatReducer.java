package org.cld.stock.load;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReformatReducer extends Reducer<Text, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(ReformatReducer.class);
	
	private MultipleOutputs<Text, Text> mos;
	
	public void setup(Context context) {
		 mos = new MultipleOutputs<Text,Text>(context);
	}
	
	protected void reduce(Text key, Iterable<Text> values, Context context
            ) throws IOException, InterruptedException {
		for(Text value: values) {
			logger.debug("reducer: key:" + key);
			mos.write(ReformatMapredLauncher.NAMED_OUTPUT, value, null, key.toString());
		}
	}

}
