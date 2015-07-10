package org.cld.datacrawl.util;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.cld.util.PatternResult;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.ClickType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class VerifyPageByXPath implements VerifyPage {
	public static Logger logger = LogManager.getLogger(VerifyPageByXPath.class);
	
	private String[] xpathsSuccess;
	private String[] expectedValues;
	
	public VerifyPageByXPath(){
	}
	
	public VerifyPageByXPath(ClickType ct){
		List<String> xpaths = new ArrayList<String>();
		List<AttributeType> atl = ct.getInput();
		for (AttributeType at:atl){
			if (at.getValue().getFromType()==VarType.XPATH || at.getValue().getValue().contains("//")){
				xpaths.add(at.getValue().getValue());
			}
		}

		if (ct.getNextpage().getCondition()==null){
			if (ct.getNextpage().getSuccessNextPage().getValue().getFromType()==VarType.XPATH ||
					ct.getNextpage().getSuccessNextPage().getValue().getValue().contains("//")){
				xpaths.add(ct.getNextpage().getSuccessNextPage().getValue().getValue());
			}
		}
		xpathsSuccess = xpaths.toArray(new String[xpaths.size()]);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (String x:xpathsSuccess){
			sb.append(x);
		}
		return sb.toString();
	}
	
	public VerifyPageByXPath(String[] xpaths){
		this.xpathsSuccess =  xpaths;
	}
	
	public VerifyPageByXPath(String[] xpaths, String[] values){
		this.xpathsSuccess = xpaths;
		this.expectedValues = values;
	}
	
	public void setXPaths(String[] xpaths){
		this.xpathsSuccess = xpaths;
	}
	
	@Override
	public boolean verifySuccess(HtmlPage page, Object param) {
		//no success defined, means always pass
		if (xpathsSuccess == null){
			return true;
		}
		for (int i=0; i<xpathsSuccess.length; i++){
			if (xpathsSuccess[i]!=null){
				Object result = page.getFirstByXPath(xpathsSuccess[i]);
				if (result==null){
					logger.warn(String.format("xpath:%s not found on page %s", xpathsSuccess[i], page.getUrl().toExternalForm()));;
					return false;
				}else{
					String strResult = CrawlTaskEval.getStringValue(result);
					if (expectedValues!=null){
						if (strResult!=null){
							if (!expectedValues[i].equals(strResult)){
								logger.warn(String.format("result get from xpath %s on page %s is [%s] different then expected [%s]", 
										xpathsSuccess[i], page.getUrl().toExternalForm(), strResult, expectedValues[i]));
								return false;
							}
						}else{
							logger.warn(String.format("xpath result:%s can't be transformed to string.", result));
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}
