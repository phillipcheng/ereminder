package org.cld.datacrawl.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.mgr.impl.CrawlTaskEval;
import org.cld.taskmgr.entity.Task;
import org.xml.mytaskdef.ConfKey;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.ClickType;
import org.xml.taskdef.LoginType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class HtmlUnitUtil {
	
	private static Logger logger =  LogManager.getLogger(HtmlUnitUtil.class);
	
	/**
	 * 
	 * @param clickstream
	 * @param pageMap: in-out
	 * @param task: task instance
	 * @throws InterruptedException 
	 */
	public static void clickClickStream(ClickStreamType clickstream, Map<String, List<? extends DomNode>> pageMap, 
			Task task, CrawlConf cconf, String currentUrl) throws InterruptedException{
		for (ClickType clickType: clickstream.getLink()){
			//input parameter assignments, based on current page
			if (clickType.getInput()!=null){
				for (AttributeType input:clickType.getInput()){
					Object inputValue = CrawlTaskEval.eval(pageMap, input.getValue(), cconf, task.getParamMap());
					if (inputValue instanceof String){
						DomNode currentPage = pageMap.get(ConfKey.CURRENT_PAGE).get(0);
						String inputXpath = input.getName();
						HtmlElement he = currentPage.getFirstByXPath(inputXpath);
						if (he != null){
							if (he instanceof HtmlInput){
								((HtmlInput)he).setValueAttribute((String)inputValue);
							}else{
								logger.error(String.format("unsupported assignment to type: %s for xpath: %s", he, inputXpath));
							}
						}else{
							logger.error(String.format("xpath %s not found for assignment.", inputXpath));
						}
					}else{
						logger.error(String.format("unsupported inputValue evaluated: %s for %s", inputValue, input.getName()));
					}
				}
			}
			//do the click
			ValueType vt = clickType.getNextpage().getValue();
			vt.setToType(VarType.PAGE);//for click stream, the to type is page
			Object value = CrawlTaskEval.eval(pageMap, vt, cconf, task.getParamMap());
			if (value!=null && value instanceof HtmlPage){
				List<HtmlPage> pagelist1= new ArrayList<HtmlPage>();
				pagelist1.add((HtmlPage)value);
				pageMap.put(clickType.getNextpage().getName(), pagelist1);
				pageMap.put(ConfKey.CURRENT_PAGE, pagelist1); //set current page
			}else{
				logger.error(String.format("click stream:%s eval to %s, not a page, check toType. prdPage is:%s", 
						vt.getValue(), value, currentUrl));
				break;
			}
		}
	}
	
	private static HtmlPage getPage(WebClient wc, NextPage np) throws IOException, RuntimeException {
		HtmlPage page;
		if (np.getNextUrl()!=null)
			page = wc.getPage(np.getNextUrl());
		else{
			page = np.getNextItem().click();
		}
		return page;
	}
	/**
	 * 
	 * @param wc
	 * @param np: next page
	 * @param vp: verification plugin
	 * @param param: the parameter to be used for customer verification
	 * @param times: retry times
	 * @param cancelable: whether this click can be cancelled
	 * @param task: the task instance containing both the task param values and task definition
	 * @param cconf
	 * @return the page
	 * @throws InterruptedException
	 */
	public static HtmlPageResult clickNextPageWithRetryValidate(WebClient wc, NextPage np, VerifyPage vp, Object param, 
			int times, boolean cancelable, Task task, CrawlConf cconf) 
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
					page = getPage(wc, np);
					int maxloop = 40;
					int innerloop=0;
					while(innerloop<maxloop){
						if (page != null){
							String landingUrl = page.getUrl().toExternalForm();
							if (task!=null){
								//check whether being redirected to the login page
								if (np.getNextUrl()!=null && !landingUrl.contains(np.getNextUrl())){
									//np's url does not contain np's next url, meaning redirected
									LoginType loginInfo = task.getParsedTaskDef().getTasks().getLoginInfo();
									if (loginInfo!=null){
										List<String> possibleRedirectedUrls = loginInfo.getUrl();
										String loginUrl = possibleRedirectedUrls.get(0);
										boolean requireLogin = false;
										for (String redirectUrl: possibleRedirectedUrls){
											if (landingUrl.contains(redirectUrl)){
												requireLogin = true;
												break;
											}
										}
										//redirected to one of the possibleRedirectedUrls
										if (requireLogin) {
											//being redirected to login pages
											HtmlPage loginPage = wc.getPage(loginUrl);
											Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
											List<HtmlPage> pagelist= new ArrayList<HtmlPage>();
											pagelist.add(loginPage);
											pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
											HtmlUnitUtil.clickClickStream(loginInfo.getClick(), pageMap, task, cconf, loginUrl);
											pagelist = (List<HtmlPage>) pageMap.get(ConfKey.CURRENT_PAGE);
											String afterLoginUrl = pagelist.get(0).getUrl().toExternalForm();
											if (afterLoginUrl.equals(loginUrl)){
												logger.error(String.format("remain on the same page, login failed? with loginInfo: %s", loginInfo));
												return result;
											}else{
												logger.info(String.format("login successfully, current url: %s, before url:%s", afterLoginUrl, loginUrl));
												page = getPage(wc, np);
												if (page==null){
													logger.error(String.format("after login tried %s get null page.", np));
													return result;
												}else{
													logger.info(String.format("after login, go to org request:%s", np.toString()));
													continue;
												}
											}
										}else{
											logger.error(String.format("landing page is %s, expected landing is %s, but not in the possibleRedirectedUrls.", 
													landingUrl, np.getNextUrl()));
										}
									}else{
										logger.warn(String.format("landing page is %s, expected landing is %s, but conf has no login info defined.", 
													landingUrl, np.getNextUrl()));
									}
								}else{
									logger.debug(String.format("landing page is %s contains the expected page %s.", landingUrl, np.getNextUrl()));
								}
							}else{
								logger.debug(String.format("task is null, so no checking for login."));
							}
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
		}else if (xpathResult instanceof HtmlElement){
			strResult = ((HtmlElement)xpathResult).asText();
		}else{
			logger.error(String.format("xpath result:%s can't be converted to string", xpathResult));
		}
		return strResult;
	}
}
