package org.etl.fci;

import java.util.List;

import org.cld.datastore.entity.CrawledItem;

public interface ICrawlItemToCSV {
	//return list of csv value
	public List<String[]> getCSV(CrawledItem ci);
}
