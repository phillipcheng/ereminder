package org.cld.datacrawl.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.NextPage;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class HtmlUnitUtil {
	
	private static Logger logger =  LogManager.getLogger(HtmlUnitUtil.class);
	
	public static HtmlPageResult clickURLWithRetryValidateByXPaths(WebClient wc, String url, String[] xpaths, 
			int times, boolean cancelable) throws InterruptedException{
		VerifyPageByXPath vpbx = new VerifyPageByXPath(xpaths);
		return clickNextPageWithRetryValidate(wc, new NextPage(url), vpbx, null, times, cancelable);
	}
	
	public static HtmlPageResult clickURLWithRetryValidateByXPath(WebClient wc, String url, String xpath, 
			int times, boolean cancelable) throws InterruptedException{
		String xpaths[] = new String[1];
		xpaths[0]=xpath;
		VerifyPageByXPath vpbx = new VerifyPageByXPath(xpaths);
		return clickNextPageWithRetryValidate(wc, new NextPage(url), vpbx, null, times, cancelable);
	}
	
	public static HtmlPageResult clickNextPageWithRetryValidate(WebClient wc, NextPage np, VerifyPage vp, Object param, 
			int times, boolean cancelable) 
			throws InterruptedException{
		HtmlPageResult result = new HtmlPageResult();
		HtmlPage page = null;
		int tried = 0;
		while (tried < times){
			if (tried>=1){
				logger.info("retried with :" + np.toString() + ". times:" + tried);
			}
			try{
				Date tick1 = null;
				Date tick2 = null;
				if (logger.isDebugEnabled()){
					tick1 = new Date();
				}
				try {
					if (np.getNextUrl()!=null)
						page = wc.getPage(np.getNextUrl());
					else{
						page = np.getNextItem().click();
					}
					int maxloop = 40;
					int innerloop=0;
					while(innerloop<maxloop){
						if (page != null){
							result.setPage(page);
							if (vp !=null){
								//if has verification, default is failed
								result.setErrorCode(HtmlPageResult.EC_VERI_FAILED);
								if (vp.verifySuccess(page, param)){
									result.setErrorCode(HtmlPageResult.SUCCSS);
									if (logger.isDebugEnabled()){
										tick2 = new Date();
										long elapse = tick2.getTime() - tick1.getTime();
										logger.debug("time elapse:" + elapse + " for loading:" + np);
									}
									return result;
								}else{
									synchronized(page){
										page.wait(1000);//wait for the necessary js done
										logger.warn(String.format("wait for the expected value come out, wait %d times, max %d times", innerloop, maxloop));
									}
									innerloop++;
									result.setErrorCode(HtmlPageResult.EC_VERI_FAILED);
									result.setErrorMsg("verification failed.");
								}
							}else{
								//no validation needed
								result.setErrorCode(HtmlPageResult.SUCCSS);				
								return result;
							}
						}
					}
				} catch (IOException|RuntimeException e) {
					//sleep for server busy
					Thread.sleep(1000);
					result.setErrorCode(HtmlPageResult.EC_SYSTEM_ERROR);
					result.setErrorInfo(e);
					result.setErrorMsg("excpetion get click page:" + e);
					logger.info(String.format("exception %s get when click, retry...", e));
				}
				if (page!=null){
					synchronized(page){
						page.wait(1000);//wait for resubmit the request
					}
				}
				if (logger.isDebugEnabled()){
					tick2 = new Date();
					long elapse = tick2.getTime() - tick1.getTime();
					logger.debug("time elapse:" + elapse + " for loading:" + np);
				}
			}finally{
				tried++;
			}
		}
		
		logger.warn("retried with :" + np + ". reach max times:" + times);
		return result;
	}
	
	public static String xpathResultToString(Object xpathResult){
		String strResult = null;
		if (xpathResult instanceof HtmlInput){
			strResult = ((HtmlInput)xpathResult).getValueAttribute();
		}else if (xpathResult instanceof DomNamespaceNode){
			strResult = ((DomNamespaceNode)xpathResult).getTextContent().trim();
		}else if (xpathResult instanceof DomText){
			strResult = ((DomText)xpathResult).getTextContent().trim();
		}else{
			logger.error(String.format("xpath result:%s can't be converted to string", xpathResult));
		}
		return strResult;
	}
}
