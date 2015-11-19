package org.cld.stock;

import org.cld.datacrawl.CrawlConf;

public interface LaunchableTask {
	//return jobId list
	public String[] launch(String propfile, String baseMarketId, CrawlConf cconf, String datePart, String[] cmds);

}
