package org.cld.pagea.general;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.mgr.impl.BinaryBoolOpEval;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.SubListType;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

//this is for the product list
public class ListAnalyzeUtil {
	private static Logger logger =  LogManager.getLogger(ListAnalyzeUtil.class);
	
	
	public static NextPage getNextPageUrlFromPage(HtmlPage listPage, ParsedTasksDef tasksDef, Map<String, Object> attrs, CrawlConf cconf) {
		BrowseCatType bc = tasksDef.getLeafBrowseCatTask();
		SubListType slt = bc.getSubItemList();
		String npxpath = slt.getNextPage();
		if (npxpath!=null){
			if (npxpath.contains("/")){
				//it is an xpath
				HtmlElement he = listPage.getFirstByXPath(npxpath);
				//eval last page condition before eval get next page
				BinaryBoolOp bbo = slt.getLastPageCondition();
				if (bbo!=null){
					Map<String, Object> values = new HashMap<String, Object>();
					values.put(ConfKey.PRD_LIST_NextPage, he);
					values.putAll(attrs);
					if (BinaryBoolOpEval.eval(listPage, cconf, bbo, values)){
						return new NextPage(NextPage.STATUS_LASTPAGE);
					}
				}
				if (he!=null){
					if (he instanceof HtmlAnchor){
						//use url more stable than js
						try {
							String url = listPage.getFullyQualifiedUrl(((HtmlAnchor)he).getHrefAttribute()).toExternalForm();
							return new NextPage(url);
						} catch (MalformedURLException e) {
							logger.error("error when get fully qualified url for %s", ((HtmlAnchor)he).getHrefAttribute());
						}
					}
					return new NextPage(he);
				}else{
					logger.error(String.format("next page xpath %s not found on page %s", npxpath, listPage.getUrl().toExternalForm()));
					return new NextPage(NextPage.STATUS_ERR);
				}
			}else{
				logger.warn(String.format("next page xpath %s is not an xpath, so last page reached.", npxpath));
				return new NextPage(NextPage.STATUS_LASTPAGE);
			}
		}else{
			//if no next page xpath defined then return last page
			return new NextPage(NextPage.STATUS_LASTPAGE);
		}
	}

	
	public static boolean needJS(ParsedTasksDef tasksDef) {
		BrowseCatType bc = tasksDef.getLeafBrowseCatTask();
		return bc.getBaseBrowseTask().isEnableJS();
	}	
}
