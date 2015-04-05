package org.cld.datacrawl.pagea;

import java.util.Map;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedTasksDef;

import com.gargoylesoftware.htmlunit.html.HtmlPage;


/*
 * process the page turning for leaf category
 */
public interface ListAnalyzeInf {
	
	boolean needJS(ParsedTasksDef tasksDef);
	
	public String[] getListPageVerifyXPaths(ParsedTasksDef tasksDef);
	
	/**
	 * Main function to get the next-page-url from the current page
	 * @param listPage
	 * @return nextPage
	 */
	public NextPage getNextPageUrlFromPage(HtmlPage listPage, ParsedTasksDef tasksDef, Map<String, Object> attrs, CrawlConf cconf);
	
}
