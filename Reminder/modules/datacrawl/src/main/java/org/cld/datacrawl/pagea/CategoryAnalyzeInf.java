package org.cld.datacrawl.pagea;

import java.util.List;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datastore.entity.Category;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ParsedTasksDef;

import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * 
 * @author Cheng Yi
 *
 */
public interface CategoryAnalyzeInf {
	
	//
	public boolean needJS(String url, Task task);
	
	//for browsing sub-categories
	public String[] getCatPageVerifyXPaths(Category cat, Task task);
	
	//get the url of cat at pageNum, normally if the cat has only 1 page, then pageNum =1
	public String getCatURL(Category cat, int pageNum, ParsedTasksDef tasksDef);
	
	public String getCatId(String url, Task task);

	/*
	 * Pagination will be done by getCatPages
	 * @param requestUrl
	 * @param catlist page is the htmlpage after clicking the startCat, page's url maybe different then request url
	 * @param startCat: the parent category
	 * @return list of sub category including myself, for leaf node, only myself is returned
	 */
	public List<Category> getSubCategoyList(String requestUrl, HtmlPage catlist, Category startCat, Task task) 
			throws InterruptedException;
	
	/**
	 * 1) set pageNum for the cat to enable performance improvement by having multiple sub-task
	 * 2) when page is limited, set total item, page size, page limit to enable split cat to multiple sub-cat to crawl more
	 * 3) if nothing set, this cat will be browsed without functional and performance improvement but still works
	 * @param catlist page is the htmlpage after clicking cat
	 * @param cat (in/out)
	 */
	public void setCatItemNum(String requestUrl, HtmlPage catlist, Category cat, Task task) throws InterruptedException;

	
	/********************
	 * For Split Category
	 ******************/
	public void setCTConf(CrawlConf cconf, CrawlTaskConf ctconf);
	
}
