package org.cld.etl.fci;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.cld.util.JsonUtil;

public class AbstractHbaseToCSVMapper extends TableMapper<Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(AbstractHbaseToCSVMapper.class);
	
	@Override
	public void map(ImmutableBytesWritable key, Result value, Context context) 
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		String storeFilter = conf.get(HBaseToCSVMapperLauncher.KEY_STOREID_FILTER);
		//first group is the output entry id, e.g. ([0-9]+)_a_b
		String idFilter = conf.get(HBaseToCSVMapperLauncher.KEY_ID_FILTER);
		Pattern idp = null;
		if (idFilter!=null) idp = Pattern.compile(idFilter);
		String toCSVClazz = conf.get(HBaseToCSVMapperLauncher.KEY_ToCSVClass);
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
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String jsonParam = conf.get(HBaseToCSVMapperLauncher.KEY_PARAMMAP);
			if (jsonParam!=null){
				JsonUtil.fromJsonString(jsonParam, paramMap);
			}
			
			try{
				ICrawlItemToCSV tocsv = (ICrawlItemToCSV) Class.forName(toCSVClazz).newInstance();
				String outputId = id;
				if (m!=null){
					outputId = m.group(1);
				}
				List<String[]> retcsv = tocsv.getCSV(ci, paramMap);
				for (String[] output: retcsv){
					if (output!=null && !"".equals(output)){
						context.write(new Text(outputId), new Text(output[1]));
					}
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}else{
			//logger.info("storeFilter:" + storeFilter + ", ci storeId:" + ci.getId().getStoreId());
		}
	}
}
