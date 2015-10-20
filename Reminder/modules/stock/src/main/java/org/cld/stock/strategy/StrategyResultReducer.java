package org.cld.stock.strategy;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;

public class StrategyResultReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(StrategyResultReducer.class);

	private MultipleOutputs<Text, Text> mos;
	@Override
	public void setup(Context context){
		mos = new MultipleOutputs<Text,Text>(context);
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        mos.close();
    }
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		//input:key: startDate, value: startDate, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
		//output:key: startDate, value: transaction number, average percent
		int count=0;
		float totalPercentage=0;
		for (Text v:values){
			mos.write(HadoopTaskLauncher.NAMED_OUTPUT_TXT, key, v, "details");
			String[] vs = v.toString().split(",");
			if (vs.length==8){
				String strPer = vs[7].trim();
				if (!"-".equals(strPer)){
					totalPercentage +=Float.parseFloat(strPer);
					count++;
				}
			}else{
				logger.error("input data wrong.");
			}
		}
		float avgPercentage = totalPercentage/count;
		String summaryPerKey = String.format("%d, %.5f", count, avgPercentage);
		mos.write(HadoopTaskLauncher.NAMED_OUTPUT_TXT, key, new Text(summaryPerKey), "summary");
	}
}
