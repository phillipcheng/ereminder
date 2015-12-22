package org.cld.stock.analyze;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrategyResultMapper extends Mapper<Object, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(StrategyResultMapper.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		//input:value: bs.name, bs.params, ss.params, startDate, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
		//split at 1st date
		//output key: bs.name, bs.params, ss.params, stockId, value: startDate, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
		String[] vs = value.toString().split(",");
		List<String> kl = new ArrayList<String>();
		//int stockIdIndex = 0;
		for (int i=0; i<vs.length; i++){
			try{
				sdf.parse(vs[i]);
				//stockIdIndex=i+1;//the one after date
				break;
			}catch(ParseException e){
				kl.add(vs[i]);
			}
		}
		//String stockId = vs[stockIdIndex];
		//kl.add(stockId);
		String[] keys = new String[kl.size()];
		keys = kl.toArray(keys);
		String kv = StringUtils.join(keys, ",");
		context.write(new Text(kv), value);
	}
}
