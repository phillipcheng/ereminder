package org.cld.stock.strategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockBase;
import org.cld.util.DateTimeUtil;

public class StrategyResultReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(StrategyResultReducer.class);
	private static SimpleDateFormat msdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	private MultipleOutputs<Text, Text> mos;
	private boolean genDetailFile=true;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		mos = new MultipleOutputs<Text,Text>(context);
		String strGenDetailFile = context.getConfiguration().get(StockBase.GEN_DETAIL_FILE);
		if (strGenDetailFile!=null){
			genDetailFile = Boolean.parseBoolean(strGenDetailFile);
		}
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
       mos.close();
    }
    
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		//input key: bs.name, bs.params, ss.params, :: sample: closedropavg,5.0:0.0,1,2,9.00,1.00
		//input value: startDate, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
		//the 2nd last is used to sort, set the sort field as the mean of the group
		//output: key same as input, value: transaction number, average percent for the whole period
		try{
			String filename = key.toString().replaceAll(":", "-").replaceAll("\\.", "_").replaceAll(",", "_");
			SummaryStatistics sss = new SummaryStatistics();
			float totalDays = 0;
			int failToSell=0;
			int failToBuy=0;
			for (Text v:values){
				if (genDetailFile)
					mos.write(key, v, filename);
				String[] vs = v.toString().split(",");
				float sellPrice = Float.parseFloat(vs[vs.length-2].trim());
				Date sellTime = msdf.parse(vs[vs.length-4].trim());
				float buyPrice = Float.parseFloat(vs[vs.length-5].trim());
				Date buyTime = msdf.parse(vs[vs.length-6].trim());
				int realDays = DateTimeUtil.DateDiff(buyTime, sellTime);
				totalDays += realDays;
				String strPer = vs[vs.length-1].trim();
				if (buyPrice!=0f){
					if (sellPrice!=0f){
						float percent = Float.parseFloat(strPer);
						sss.addValue(1+percent);
					}else{
						failToSell++;
					}
				}else{
					failToBuy++;
				}
			}
			String summaryPerKey = null;
			if (sss.getN()>0){
				float avgHoldingDays = totalDays/sss.getN();
				summaryPerKey = String.format("%.4f,%.4f,%.5f,%d,%.3f,%d,%d,%.4f", sss.getGeometricMean(), sss.getVariance(), 
						sss.getMean(), sss.getN(), avgHoldingDays, failToBuy, failToSell, sss.getMean());//sorted by the last field
			}else{
				summaryPerKey = String.format("%.4f,%.4f,%.5f,%d,%.3f,%d,%d,%.4f", 0f, 0f, 
						0f, 0f, 0f, failToBuy, failToSell, 0f);//sorted by the last field
			}
			context.write(key, new Text(summaryPerKey));
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
