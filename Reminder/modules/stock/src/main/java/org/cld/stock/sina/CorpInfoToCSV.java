package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;
import org.json.JSONArray;

public class CorpInfoToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(CorpInfoToCSV.class);

	public static final String FIELD_NAME_ATTR="attr";
	public static final String FIELD_NAME_STOCKID="stockid";
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String stockid = (String) ci.getParam(FIELD_NAME_STOCKID);
		List<String> ls = (List<String>)ci.getParam(FIELD_NAME_ATTR);
		StringBuffer sb = new StringBuffer();
		try{
			for (int i=1; i<ls.size(); i+=2){
				if (i>1) //skip first comma
					sb.append(",");
				String str = ls.get(i);
				str = str.replace(",", "\\,");
				str = str.replaceAll("\\r\\n|\\r|\\n", " ");
				sb.append(str);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		List<String[]> retlist = new ArrayList<String[]>();
		retlist.add(new String[]{stockid,sb.toString()});
		return retlist;
	}
	
}
