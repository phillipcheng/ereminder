package org.cld.stock.load;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		//stockid_year_quarter: ([0-9]+)_2014_2
		String idFilter = conf.get(HBaseToCSVMapperLauncher.ID_FILTER);
		Pattern idp = null;
		if (idFilter!=null) idp = Pattern.compile(idFilter);
		String toCSVClazz = conf.get(HBaseToCSVMapperLauncher.ToCSVClass);
		String rowKey = Bytes.toString(key.get());
		//id|storeid
		String id = rowKey.substring(0, rowKey.indexOf(HbaseDataStoreManagerImpl.rowkey_sep));
		
		
		CrawledItem ci = HbaseDataStoreManagerImpl.getCrawledItemFromResult(rowKey, value);
		if (!Product.CRAWLITEM_TYPE.equals(ci.getType())){
			logger.info("filter non product type crawledItem." + ci.getType());
			return;
		}
		if (storeFilter.equals(ci.getId().getStoreId())){
			Matcher m = null;
			if (idp!=null){
				m = idp.matcher(id);
				if (!m.matches())
					return;
			}
			try{
				ICrawlItemToCSV tocsv = (ICrawlItemToCSV) Class.forName(toCSVClazz).newInstance();
				String output = tocsv.getCSV(ci);
				String stockid = id;
				if (m!=null){
					stockid = m.group(1);
				}
				if (output!=null && !"".equals(output)){
					context.write(new Text(stockid), new Text(output));
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}else{
			//logger.info("storeFilter:" + storeFilter + ", ci storeId:" + ci.getId().getStoreId());
		}
	}
}
