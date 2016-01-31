package org.cld.etl.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.cld.util.entity.CrawledItem;

/**
 * output none list attributes as csv
 * 
 * name, age, etc

 * 
 */
public class CrawlItemNoListAsCSV extends AbstractCrawlItemToCSV{
	
	private static Logger logger =  LogManager.getLogger(CrawlItemNoListAsCSV.class);
	
	@Override
	public String[][] getCSV(CrawledItem ci, Map<String, Object> paramMap) {
		List<String> csvs = new ArrayList<String>();
		Map<String, Object> params = ci.getParamMap();
		StringBuffer sb = new StringBuffer();
		for (String key:params.keySet()){
			Object v = params.get(key);
			if (!(v instanceof List) && (v instanceof Serializable)){
				sb.append(v);
				sb.append(",");
			}
		}
		csvs.add(sb.toString());
		String[][] retlist = new String[csvs.size()][];
		for (int i=0; i<csvs.size(); i++){
			retlist[i] = new String[]{ci.getId().toString(), csvs.get(i)};
		}
		return retlist;
	}
}
