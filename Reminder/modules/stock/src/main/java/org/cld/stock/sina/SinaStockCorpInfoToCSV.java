package org.cld.stock.sina;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.load.ICrawlItemToCSV;
import org.json.JSONArray;

public class SinaStockCorpInfoToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(SinaStockCorpInfoToCSV.class);

	public static final String FIELD_NAME_ATTR="attr";
	@Override
	public String getCSV(CrawledItem ci) {
		Object o = ci.getParam(FIELD_NAME_ATTR);
		JSONArray ls = (JSONArray)o;
		StringBuffer sb = new StringBuffer();
		try{
			for (int i=1; i<ls.length(); i+=2){
				if (i>1) //skip first comma
					sb.append(",");
				String str = ls.getString(i);
				str = str.replace(",", "\\,");
				str = str.replaceAll("\\r\\n|\\r|\\n", " ");
				sb.append(str);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return sb.toString();
	}
	
}
