package org.cld.stock.sina.jobs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;


public class SplitByStockMapper extends Mapper<Object, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(SplitByStockMapper.class);
	public static String sep = "_";
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//private MultipleOutputs<Text, Text> mos;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		//mos = new MultipleOutputs<Text,Text>(context);
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        //mos.close();
    }
    
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		//value: stockid, date, xxx
		String line = value.toString();
		String[] vs = line.split(",");
		if (vs.length>2){
			String date = vs[1];
			context.write(new Text(date), value);
		}
		
	}
	
}
