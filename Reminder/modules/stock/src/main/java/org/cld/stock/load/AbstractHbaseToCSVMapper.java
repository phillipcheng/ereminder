package org.cld.stock.load;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.Product;
import org.cld.datastore.impl.HbaseDataStoreManagerImpl;

public class AbstractHbaseToCSVMapper extends TableMapper<Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(AbstractHbaseToCSVMapper.class);
	
	@Override
	public void map(ImmutableBytesWritable key, Result value, Context context) 
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		String storeFilter = conf.get(HBaseToCSVMapperLauncher.STOREID_FILTER);
		String toCSVClazz = conf.get(HBaseToCSVMapperLauncher.ToCSVClass);
		String rowKey = Bytes.toString(key.get());
		String firstKey = rowKey.substring(0, rowKey.indexOf(HbaseDataStoreManagerImpl.rowkey_sep));
		CrawledItem ci = HbaseDataStoreManagerImpl.getCrawledItemFromResult(rowKey, value);
		if (!Product.CRAWLITEM_TYPE.equals(ci.getType())){
			logger.info("filter non product type crawledItem." + ci.getType());
			return;
		}
		if (storeFilter.equals(ci.getId().getStoreId())){
			try{
				ICrawlItemToCSV tocsv = (ICrawlItemToCSV) Class.forName(toCSVClazz).newInstance();
				String output = tocsv.getCSV(ci);
				context.write(new Text(firstKey), new Text(output));
			}catch(Exception e){
				logger.error("", e);
			}
		}else{
			logger.info("storeFilter:" + storeFilter + ", ci storeId:" + ci.getId().getStoreId());
		}
	}
}
