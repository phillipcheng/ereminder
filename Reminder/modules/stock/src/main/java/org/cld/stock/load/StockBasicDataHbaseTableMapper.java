package org.cld.stock.load;

import java.io.IOException;
import java.util.List;

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
import org.cld.util.StringUtil;
import org.json.JSONArray;

public class StockBasicDataHbaseTableMapper extends TableMapper<Text, Text>{
	
	private static Logger logger =  LogManager.getLogger(StockBasicDataHbaseTableMapper.class);
	private static int[] lineNumber = new int[]{29, 32, 10, 80, 34, 53};
	public static final String value_sep = "|";
	
	//return the string in YYYYMMDD format
	public static String getDateForPeriodId(String year, String periodId, String siteId){
		if (StockConst.SH_STOCK_SITE_ID.equals(siteId)){
			if (StockConst.sh_periodid_3m.equals(periodId)){
				return year + "0301";
			}else if (StockConst.sh_periodid_6m.equals(periodId)){
				return year + "0601";
			}else if (StockConst.sh_periodid_9m.equals(periodId)){
				return year + "0901";
			}else if (StockConst.sh_periodid_12m.equals(periodId)){
				return year + "1201";
			}else{
				logger.error("unsupported periodId for sh:" + periodId);
				return null;
			}
		}else{
			logger.error("unsupported site id:" + siteId);
			return null;
		}
	}
	@Override
	public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
		String rowKey = Bytes.toString(key.get());
		CrawledItem ci = HbaseDataStoreManagerImpl.getCrawledItemFromResult(rowKey, value);
		if (!Product.CRAWLITEM_TYPE.equals(ci.getType())){
			logger.info("filter non product type crawledItem." + ci.getType());
			return;
		}
		if (StockConst.SH_STOCK_SITE_ID.equals(ci.getId().getStoreId())){
			JSONArray allColumns = (JSONArray) ci.getParam(StockConst.param_sh_columns);
			JSONArray data = (JSONArray) ci.getParam(StockConst.param_sh_rows);
			String id = ci.getId().getId();
			String periodId = StringUtil.getStringBetweenFirstPreFirstPost(id, StockConst.SH_REPORT_PERIOD_ID_KEY, null);
			String quoteId = StringUtil.getStringBetweenFirstPreFirstPost(id, null, StockConst.SH_REPORT_ID_KEY);
			String storeId = ci.getId().getStoreId();
			JSONArray years = allColumns.getJSONArray(0);
			//skip the 1st column
			for (int i=1; i<years.length(); i++){
				String year = years.getString(i);
				String output = StockConst.SH_STOCK_MARKET_ID + value_sep;
				output += quoteId + value_sep;
				String dpd = getDateForPeriodId(year, periodId, storeId);
				if (dpd==null){
					break;
				}
				output +=  dpd + value_sep;
				for (int j=0; j<data.length(); j++){//tab index
					JSONArray oneTab = data.getJSONArray(j);
					int step = years.length();
					//get the data belongs to year i
					int rowsThisTab = oneTab.length()/step;
					for (int row=0; row<rowsThisTab; row++){
						output += oneTab.getString(row*step+i) + value_sep;
						if (j==2 && row>15){
							break;//to skip all the ratio on the 2nd tab
						}
					}
				}
				//
				logger.info(String.format("context.write key:%s, value %s", rowKey, output));
				context.write(new Text(rowKey), new Text(output));
			}
		}
	}
}
