package org.cld.stock.load;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReformatReducer extends Reducer<Text, Text, Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(ReformatReducer.class);
	
	private MultipleOutputs<Text, Text> mos;
	
	@Override
	public void setup(Context context) {
		 mos = new MultipleOutputs<Text,Text>(context);
	}
	
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context
            ) throws IOException, InterruptedException {
		int i=0;
		for(Text value: values) {
			mos.write(ReformatMapredLauncher.NAMED_OUTPUT, value, NullWritable.get(), key.toString());
			i++;
		}
		logger.debug(String.format("reduce key:%s, record number:%d",  key.toString(), i));
		
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        mos.close();
    }

}
