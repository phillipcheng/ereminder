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
		//input:key: select + sell,  value: strategyname, startDate, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
		//output:key: select + sell,  value: transaction number, average percent for the whole period
		int count=0;
		float totalPercentage=0;
		String strategyName="";
		for (Text v:values){
			mos.write(HadoopTaskLauncher.NAMED_OUTPUT_TXT, key, v, "details");
			String[] vs = v.toString().split(",");
			if (vs.length==9){
				String strPer = vs[8].trim();
				strategyName=vs[0];
				if (!"-".equals(strPer)){
					totalPercentage +=Float.parseFloat(strPer);
					count++;
				}
			}else{
				logger.error("input data wrong.");
			}
		}
		float avgPercentage = totalPercentage/count;
		String summaryPerKey = String.format("%s,%.5f,%d", strategyName, avgPercentage, count);
		mos.write(HadoopTaskLauncher.NAMED_OUTPUT_TXT, key, new Text(summaryPerKey), "summary");
	}
}
