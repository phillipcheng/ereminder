package org.cld.datacrawl.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import org.xml.taskdef.CredentialType;
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

enum LoginStatus{
	LoginNotNeeded (0),
	LoginSuccess(1), 
	LoginFailed(2), 
	LoginCatchya(3);
	
	private final int status;
	
	LoginStatus(int status){
		this.status = status;
	}
}

public class HtmlUnitUtil {
	private static final String KEY_USERNAME="username";
	private static final String KEY_PASSWORD="password";
	private static final String KEY_CREDENTIAL_IDX="cidx";
	
	private static Logger logger =  LogManager.getLogger(HtmlUnitUtil.class);

	/**
	 * 
	 * @param loginInfo
	 * @param preIdx, return the credential idx differ than the preIdx
	 * @return
	 */
	private static Map<String, Object> getCredentialParamMap(LoginType loginInfo, Set<String> usedCredentials){
		List<CredentialType> clist = loginInfo.getCredential();
		int count = clist.size();
		if (count>0){
			Random r = new Random();
			int idx = r.nextInt(count);
			String username;
			CredentialType c = clist.get(idx);
			username = c.getUsername();
			while (usedCredentials.contains(username)){
				idx = r.nextInt(count);
				c = clist.get(idx);
				username = c.getUsername();
			}
			
			c = clist.get(idx);
			Map<String, Object> pMap = new HashMap<String, Object>();
			pMap.put(KEY_USERNAME, c.getUsername());
			pMap.put(KEY_PASSWORD, c.getPassword());
			pMap.put(KEY_CREDENTIAL_IDX, idx);
			return pMap;
		}else{
			logger.error("no credential is configured.");
			return null;
		}
	}
	
	/**
	 * 
	 * @param clickstream
	 * @param pageMap: in-out
	 * @param task: task instance
	 * @throws InterruptedException 
	 */
	public static void clickClickStream(ClickStreamType clickstream, Map<String, List<? extends DomNode>> pageMap, 
			Map<String, Object> params, CrawlConf cconf, String currentUrl) throws InterruptedException{
		for (ClickType clickType: clickstream.getLink()){
			//input parameter assignments, based on current page
			if (clickType.getInput()!=null){
				for (AttributeType input:clickType.getInput()){
					Object inputValue = CrawlTaskEval.eval(pageMap, input.getValue(), cconf, params);
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
			Object value = CrawlTaskEval.eval(pageMap, vt, cconf, params);
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
	 * @param landingUrl
	 * @param tryUrl
	 * @param wc
	 * @param task
	 * @param cconf
	 * @return true means login successful, false means either no login performed or login failed.
	 * @throws InterruptedException
	 */
	private static LoginStatus tryLogin(String landingUrl, String tryUrl, WebClient wc, Task task, CrawlConf cconf) 
			throws InterruptedException{
		if (task!=null){
			if (tryUrl!=null && !landingUrl.contains(tryUrl)){
				//np's url does not contain np's next url, meaning redirected
				LoginType loginInfo = task.getParsedTaskDef().getTasks().getLoginInfo();
				if (loginInfo!=null){
					boolean requireLogin = false;
					String gotchaUrl = loginInfo.getGotchaURL();
					String loginUrl = loginInfo.getLoginURL();
					if (gotchaUrl!=null && landingUrl.contains(gotchaUrl)){
						requireLogin = true;
					}else {
						List<String> possibleRedirectedUrls = loginInfo.getRedirectedURL();
						for (String redirectUrl: possibleRedirectedUrls){
							if (landingUrl.contains(redirectUrl)){
								requireLogin = true;
								break;
							}
						}
					}
					//redirected to one of the possibleRedirectedUrls
					if (requireLogin) {
						wc.getCookieManager().clearCookies();
						//being redirected to login pages
						int preCredentialIdx = -1;//1st time
						Set<String> usedCredentials = new HashSet<String>();
						while (usedCredentials.size()<loginInfo.getCredential().size()){
							HtmlPage loginPage;
							try {
								loginPage = wc.getPage(loginUrl);
							} catch (FailingHttpStatusCodeException | IOException e) {
								logger.error(String.format("get login page:%s error.", loginUrl), e);
								return LoginStatus.LoginFailed;
							}
							Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
							List<HtmlPage> pagelist= new ArrayList<HtmlPage>();
							pagelist.add(loginPage);
							pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
							Map<String, Object> paramMap = getCredentialParamMap(loginInfo, usedCredentials);
							if (paramMap!=null){
								usedCredentials.add((String) paramMap.get(KEY_USERNAME));
								preCredentialIdx = (int) paramMap.get(KEY_CREDENTIAL_IDX);
								clickClickStream(loginInfo.getClick(), pageMap, paramMap, cconf, loginUrl);
								pagelist = (List<HtmlPage>) pageMap.get(ConfKey.CURRENT_PAGE);
								String afterLoginUrl = pagelist.get(0).getUrl().toExternalForm();
								if (afterLoginUrl.equals(loginUrl)){
									logger.error(String.format("login failed? remain on the same page, with loginInfo: %s", loginInfo));
									return LoginStatus.LoginFailed;
								}else if(afterLoginUrl.contains(loginInfo.getGotchaURL())){
									logger.info(String.format("catchya using user %s", paramMap.get(KEY_USERNAME)));
									continue;
								}else{
									logger.info(String.format("login successfully, current url: %s, before url:%s", afterLoginUrl, loginUrl));
									return LoginStatus.LoginSuccess;
								}
							}else{
								//error will be generated by getCredentialParamMap
								return LoginStatus.LoginFailed;
							}
						}
						logger.error("used up all the credential still catchya");
						return LoginStatus.LoginFailed;
					}else{
						logger.error(String.format("No Login: landing page is %s, expected landing is %s, but not in the possibleRedirectedUrls.", 
								landingUrl, tryUrl));
					}
				}else{
					logger.warn(String.format("No Login: landing page is %s, expected landing is %s, but conf has no login info defined.", 
								landingUrl, tryUrl));
				}
			}else{
				logger.debug(String.format("No Login: landing page %s contains the expected page %s.", landingUrl, tryUrl));
			}
		}else{
			logger.debug(String.format("No Login: task is null."));
		}
		return LoginStatus.LoginNotNeeded;
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
					int maxloop = 10;
					int innerloop=0;
					while(innerloop<maxloop){
						if (page != null){
							String landingUrl = page.getUrl().toExternalForm();
							//
							LoginStatus loginStatus = tryLogin(landingUrl, np.getNextUrl(), wc, task, cconf);
							if (LoginStatus.LoginNotNeeded != loginStatus){
								if (LoginStatus.LoginSuccess == loginStatus){
									page = getPage(wc, np);
									if (page==null){
										logger.error(String.format("after login, go to org request %s, got null page.", np));
										return result;
									}else{
										logger.info(String.format("after login, go to org request %s, success.", np.toString()));
										//to verify
									}
								}else if (LoginStatus.LoginCatchya == loginStatus){
									result.setErrorCode(HtmlPageResult.GOTCHA);
									return result;
								}else if (LoginStatus.LoginFailed == loginStatus){
									result.setErrorCode(HtmlPageResult.LOGIN_FAILED);
									return result;
								}
							}
							
							result.setPage(page);
							if (vp !=null){
								//if has verification, default is failed
								result.setErrorCode(HtmlPageResult.VERIFY_FAILED);
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
									result.setErrorCode(HtmlPageResult.VERIFY_FAILED);
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
					result.setErrorCode(HtmlPageResult.SYSTEM_ERROR);
					result.setErrorInfo(e);
					result.setErrorMsg("excpetion get click page:" + e);
					logger.error(String.format("exception get when click, retry..."), e);
				}
				if (page!=null){
					synchronized(page){
						page.wait(1500*(tried/2+1));//wait for resubmit the request
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
