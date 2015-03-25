package org.cld.datacrawl.pagea;

import org.cld.datacrawl.util.Content;
import org.cld.util.DateTimeRange;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

public interface PromAnalyzeInf {
	
	public String[] getPageVerifyXPaths();
	/**
	 * get promotion id from either url or brief-desc of the promotion as quick-promotion in az
	 * should not return null;
	 * @param content
	 * @return
	 */
	public String getIdFromContent(Content content);
	
	//
	public int getProType(String str);
	
	//return null if not found, or throw format exception
	public DateTimeRange getDateRange(HtmlElement element);
	
	public String getTitle(HtmlElement element);
	
	public String getPromInfo(HtmlElement element);

}
