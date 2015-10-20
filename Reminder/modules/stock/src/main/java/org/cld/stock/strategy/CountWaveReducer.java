package org.cld.stock.strategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.StockBase;

public class CountWaveReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(CountWaveReducer.class);
	
	private Map<String, String> stockInfoMap = new HashMap<String, String>();
	
	@Override
	public void setup(Context context){
		if (stockInfoMap.size()==0){
			String propFile = context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES);
			CrawlConf cconf = CrawlTestUtil.getCConf(propFile);
			DataStoreManager dsm = cconf.getDsm(CrawlConf.crawlDsManager_Value_Hbase);
			String marketId = context.getConfiguration().get(CountWaveTask.HCK_MARKETID);
			String storeId = context.getConfiguration().get(CountWaveTask.HCK_STOREID);
			CrawledItem ci = dsm.getCrawledItem(marketId, storeId, null);
			List<String> ids = (List<String>) ci.getParam(StockBase.KEY_IDS);
			String[][] csvValues = ci.getCsvValue();
			for (int i=0; i<ids.size(); i++){
				String[] kv = csvValues[i];
				stockInfoMap.put(ids.get(i), kv[1]);
			}
		}
	}

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		//input:key: stockid, value: date, upcount, downcount, totalcount, avgPrice
		//output:key: stockid, value: date-number, total-up, total-down, total-total, avgPrice
		int count=0;
		int tup=0;
		int tdown=0;
		int tt=0;
		int days=0;
		float tprice=0;
		for (Text v:values){
			//context.write(key, v);
			String[] vs = v.toString().split(",");
			if (vs.length==5){
				days++;
				int up = Integer.parseInt(vs[1]);
				int down = Integer.parseInt(vs[2]);
				int updown = Integer.parseInt(vs[3]);
				count++;
				tup +=up;
				tdown +=down;
				tt += updown;
				tprice += Float.parseFloat(vs[4]);
			}else{
				logger.error("wrong format.");
			}
		}
		float avgPrice =0;
		if (days>0){
			avgPrice = tprice/days;
			logger.info("tprice:" + tprice);
			logger.info("avgprice:" + avgPrice);
		}
		String stockid = key.toString();
		String info = stockInfoMap.get(stockid);
		String output = String.format("%d, %d, %d, %d, %.3f,%s", count, tup, tdown, tt, avgPrice,info);
		context.write(key, new Text(output));
	}
}
