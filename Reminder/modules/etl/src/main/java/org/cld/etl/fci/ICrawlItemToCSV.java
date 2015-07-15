package org.cld.etl.fci;

import java.util.List;
import java.util.Map;

import org.cld.datastore.entity.CrawledItem;

public interface ICrawlItemToCSV {
	//return list of csv value
	//2 tuple: key, value
	//3 tuple: key, value, fileName
	public List<String[]> getCSV(CrawledItem ci, Map<String, Object> paramMap);
}
