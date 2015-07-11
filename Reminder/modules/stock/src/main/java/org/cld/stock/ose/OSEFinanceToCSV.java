package org.cld.stock.ose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;
import org.cld.util.StringUtil;
import org.json.JSONArray;

public class OSEFinanceToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(OSEFinanceToCSV.class);
	public static final String value_sep = ",";
	public static final String FIELD_NAME_SYMBAL="symbal";
	
	
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
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String symbal = (String) ci.getParam(FIELD_NAME_SYMBAL);
		JSONArray allColumns = (JSONArray) ci.getParam(StockConst.param_sh_columns);
		JSONArray data = (JSONArray) ci.getParam(StockConst.param_sh_rows);
		String id = ci.getId().getId();
		String periodId = StringUtil.getStringBetweenFirstPreFirstPost(id, StockConst.SH_REPORT_PERIOD_ID_KEY, null);
		String quoteId = StringUtil.getStringBetweenFirstPreFirstPost(id, null, StockConst.SH_REPORT_ID_KEY);
		String storeId = ci.getId().getStoreId();
		JSONArray years = allColumns.getJSONArray(0);
		List<String[]> retList = new ArrayList<String[]>();
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
			retList.add(new String[]{symbal, output});
		}

		return retList;
	}
}
