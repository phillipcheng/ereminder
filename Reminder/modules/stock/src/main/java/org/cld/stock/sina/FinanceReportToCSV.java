package org.cld.stock.sina;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.entity.CrawledItem;
import org.cld.stock.load.ICrawlItemToCSV;
import org.json.JSONArray;

public class FinanceReportToCSV implements ICrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(FinanceReportToCSV.class);

	//data=[2014-06-30, 50,714,900.00, --, --, --, 504,900.00, 13,103,300.00, 3,105,000.00, --, --, --, --, 229,600.00, 3,611,800.00, 37,229,700.00, --, --, --, --, 1,505,700.00, 189,834,400.00, 4,722,500.00, --, 17,679,100.00, 13,873,100.00, --, --, 254,500.00, --, --, 60,480,000.00, --, 869,500.00, 216,200.00, --, 71,400.00, --, 138,400.00, --, --, --, 931,900.00, 3,390,800.00, --, 393,021,700.00, 70,800.00, --, 65,385,800.00, 59,591,100.00, 5,794,700.00, --, --, 309,200.00, --, 7,005,400.00, 275,678,300.00, --, --, --, --, --, --, --, 815,800.00, 3,717,700.00, --, --, 1,600.00, --, --, --, --, --, 9,301,600.00, --, --, 7,825,300.00, 370,889,800.00, 1,865,300.00, 5,936,700.00, --, --, --, 4,964,700.00, 3,670,000.00, --, 5,394,500.00, --, --, --, 21,831,200.00, 300,700.00, 22,131,900.00, 393,021,700.00]	
	public static final String FIELD_NAME_DATA="data";
	@Override
	public String getCSV(CrawledItem ci) {
		Object o = ci.getParam(FIELD_NAME_DATA);
		JSONArray ls = (JSONArray)o;
		StringBuffer sb = new StringBuffer();
		try{
			for (int i=0; i<ls.length(); i++){
				if (i>0){
					sb.append(",");
				}
				String str = ls.getString(i);
				if ("--".equals(str)){
					str = "0";
				}else if (str.contains(",")){
					str = str.replace(",", "");//remove comma
				}
				sb.append(str);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return sb.toString();
	}
	
}
