package org.cld.datacrawl.test;

import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datacrawl.mgr.IListAnalyze;
import org.cld.datacrawl.mgr.IProductAnalyze;
import org.cld.datacrawl.mgr.ListProcessInf;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrsCatStat;
import org.cld.datacrawl.task.BrsDetailStat;

public class SiteRuntime {
	BrsCatStat bctBS;	
	BrsDetailStat bdtBS;
	BrowseCategoryTaskConf bct;
	BrowseDetailTaskConf bdt;
	
	CrawlTaskConf ctconf;
	
	ICategoryAnalyze ca;
	IListAnalyze la;
	ListProcessInf originalLP;
	IProductAnalyze pa;
	
}
