package org.cld.stock.strategy;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrategyResultReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(StrategyResultReducer.class);
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		//input key: bs.name, bs.params, ss.params, 
		//input value: startDate, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
		//output: key same as input, value: transaction number, average percent for the whole period
		int count=0;
		float totalPercentage=0;
		for (Text v:values){
			String[] vs = v.toString().split(",");
			String strPer = vs[vs.length-1].trim();
			if (!"-".equals(strPer)){
				totalPercentage +=Float.parseFloat(strPer);
				count++;
			}
		}
		float avgPercentage = totalPercentage/count;
		String summaryPerKey = String.format("%.5f,%d", avgPercentage, count);
		context.write(key, new Text(summaryPerKey));
	}
}
