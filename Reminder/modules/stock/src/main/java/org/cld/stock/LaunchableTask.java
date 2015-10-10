package org.cld.stock;

import org.cld.datacrawl.CrawlConf;

public interface LaunchableTask {
	//return jobId list
	public String[] launch(String propfile, CrawlConf cconf, String datePart, String[] cmds);

}
