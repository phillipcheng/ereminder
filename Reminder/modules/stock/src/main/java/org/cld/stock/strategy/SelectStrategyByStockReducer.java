package org.cld.stock.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectStrategyByStockReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(SelectStrategyByStockReducer.class);
    

	public static final String MaxSelectNumber="MaxSelectNumber";
	
	int maxSelectNumber=0;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		maxSelectNumber = context.getConfiguration().getInt(MaxSelectNumber, 0);
		logger.info(String.format("max select number get: %d", maxSelectNumber));
	}
	
    /**
     * input key: bs.name, dt, bs.orderDirection, bs.params
     * input values: stockId, value, buyPrice
     * from given select strategy, we use direction to output the topN (value) of the day
     * output: stockid, value, buyPrice, dt, rank, bs.name, bs.params
     */
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		String[] kv = key.toString().split(",");
		String bsName=kv[0];
		String dt = kv[1];
		String orderDir = kv[2];
		String bsParams = kv[3];
		
		//order by value
		TreeMap<Float, List<String>> map = new TreeMap<Float, List<String>>();
		for (Text v:values){
			String[] vv = v.toString().split(",");
			float value = Float.parseFloat(vv[1]);
			List<String> sl = map.get(value);
			if (sl==null){
				sl = new ArrayList<String>();
				map.put(value, sl);
			}
			sl.add(v.toString());
		}
		Iterator<Float> kl = null;
		if (orderDir.equals(StrategyConst.V_ASC)){
			kl = map.keySet().iterator();
		}else{
			kl = map.descendingKeySet().iterator();
		}
		int cnt=1;
		while (kl.hasNext()){
			float f = kl.next();
			List<String> sl = map.get(f);
			for (String vstr:sl){
				if (maxSelectNumber == 0 || cnt<=maxSelectNumber){
					String k = String.format("%s,%s,%d", vstr, dt, cnt);
					String v = String.format("%s,%s", bsName, bsParams);
					context.write(new Text(k), new Text(v));
					cnt++;
				}else{
					break;
				}
			}
		}
	}
}
