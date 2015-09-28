package org.cld.stock.sina.jobs;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.stock.StockBase;
import org.cld.stock.sina.SinaStockConfig;


public class IPODateReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(IPODateReducer.class);
	public static String sep = "_";
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private CrawlConf cconf = null;
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		if (cconf==null){
			String propFile = context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES);
			logger.info(String.format("conf file for mapper job is %s", propFile));
			cconf = CrawlTestUtil.getCConf(propFile);
		}
	}
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		Map<String, String> ipoDate = new HashMap<String, String>();
		for (Text v:values){
			String[] vs = v.toString().split(",");
			ipoDate.put(vs[3], vs[4]);
		}
		logger.info(String.format("total stock: %d", ipoDate.size()));
		String marketId = context.getConfiguration().get(AbstractCrawlItemToCSV.FN_MARKETID);
		String strEd = context.getConfiguration().get(AbstractCrawlItemToCSV.FIELD_NAME_ENDDATE);
		Date ed = null;
		try {
			ed = sdf.parse(strEd);
		} catch (ParseException e) {
			logger.error("", e);
		}
		CrawledItem ipoCi = new CrawledItem(CrawledItem.CRAWLITEM_TYPE, "default", 
				new CrawledItemId(marketId, SinaStockConfig.SINA_STOCK_IPODate, ed));
		ipoCi.addParam(StockBase.KEY_IPODate_MAP, ipoDate);
		cconf.getDefaultDsm().addUpdateCrawledItem(ipoCi, null);
	}
	
}
