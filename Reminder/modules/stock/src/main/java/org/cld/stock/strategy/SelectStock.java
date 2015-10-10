package org.cld.stock.strategy;

import java.util.List;

import org.cld.datacrawl.CrawlConf;

public interface SelectStock {
	
	public List<String> select(String name, CrawlConf cconf, String outputFileDir, Object[] params);

}
