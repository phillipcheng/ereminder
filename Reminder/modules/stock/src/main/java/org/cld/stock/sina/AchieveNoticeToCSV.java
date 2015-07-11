package org.cld.stock.sina;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.etl.fci.ICrawlItemToCSV;

public class AchieveNoticeToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(AchieveNoticeToCSV.class);

	public static final String FN_STOCKID = "stockid";
	public static final String FN_COL_NUM="ColNum";
	public static final String FN_DATA="data";
	
	
	@Override
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		String stockid = (String) ci.getParam(FN_STOCKID);
		int colnum = (int) ci.getParam(FN_COL_NUM);
		List<String> vl = (List<String>)ci.getParam(FN_DATA);//list of values
		List<String[]> retlist = new ArrayList<String[]>();
		StringBuffer sb = new StringBuffer();
		try{
			if (vl!=null){
				int idx=0;
				while (idx<vl.size()){
					sb = new StringBuffer();
					for (int i=0; i<colnum; i++){
						if (i>0){
							sb.append(",");
						}
						String v = vl.get(idx++);
						//replace comma and new line for csv string
						v = v.replace(",", "\\,").replaceAll("\\r\\n|\\r|\\n", " ");
						sb.append(v);
					}
					retlist.add(new String[]{stockid, sb.toString()});
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return retlist;
	}
}
