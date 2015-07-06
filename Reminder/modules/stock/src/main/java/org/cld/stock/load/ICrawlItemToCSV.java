package org.cld.stock.load;

import org.cld.datastore.entity.CrawledItem;

public interface ICrawlItemToCSV {
	public String getCSV(CrawledItem ci);
}
