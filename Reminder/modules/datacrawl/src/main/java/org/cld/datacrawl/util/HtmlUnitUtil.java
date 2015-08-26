package org.cld.datacrawl.util;

import java.io.IOException;
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
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.NextPage;
import org.cld.datacrawl.mgr.BinaryBoolOpEval;
import org.cld.datacrawl.mgr.CrawlTaskEval;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.ClickType;
import org.xml.taskdef.ConditionalNextPage;
import org.xml.taskdef.CredentialType;
import org.xml.taskdef.LoginType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
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
	private static Logger logger =  LogManager.getLogger(HtmlUnitUtil.class);
	
	/*
	 * directly get page from webclient without the validation, retrying, etc
	 */
	private static HtmlPage getDirectPage(WebClient wc, NextPage np) throws IOException, RuntimeException {
		HtmlPage page;
		if (np.getNextUrl()!=null)
			page = wc.getPage(np.getNextUrl());
		else{
			if (np.getNextItem()!=null){
				logger.debug(String.format("clicking item: %s on page %s now.", np.getNextItem().asXml(), np.getNextItem().getPage().getUrl().toExternalForm()));
				page = np.getNextItem().click();
			}else{
				logger.error("wrong np, no next url, no next item.");
				return null;
			}
		}
		return page;
	}
	
	private static boolean isGotcha(HtmlPage landingPage, LoginType loginInfo, CrawlConf cconf){
		if (loginInfo.getLoginGotchaCondition()!=null){
			boolean ret = BinaryBoolOpEval.eval(landingPage, cconf, loginInfo.getLoginSuccessCondition(), null);
			if (ret){
				return true;
			}else{
				return false;
			}
		}else{
			if (loginInfo.getGotchaURL()!=null){
				if (loginInfo.getGotchaURL().getFromType()==VarType.URL){
					if (landingPage.getUrl().toExternalForm().contains(loginInfo.getGotchaURL().getValue())){
						return true;
					}else{
						return false;
					}
				}else{
					logger.error("gotcha url vartype not supported:" + loginInfo.getGotchaURL().getFromType());
				}
			}else{
				logger.info("both gotcha condition and gotcha url not configured, means no gotcha.");
			}
		}
		return false;
	}
	
	/**
	 * invoke the login click stream based on the landingUrl
	 * @param landingUrl: 
	 * @param loginInfo:
	 * @param credential
	 * @param wc
	 * @param cconf
	 * @return
	 * @throws InterruptedException
	 */
	private static LoginStatus login(HtmlPage landingPage, ClickStreamType loginClickStream, ParsedTasksDef tasksDef, CredentialType credential, 
			WebClient wc, CrawlConf cconf) 
			throws InterruptedException{
		//prepare parameters to invoke clickClickStream
		Map<String, List<? extends DomNode>> pageMap = new HashMap<String, List<? extends DomNode>>();
		List<HtmlPage> pagelist= new ArrayList<HtmlPage>();
		pagelist.add(landingPage); //we can reuse the current page
		String landingUrl = landingPage.getUrl().toExternalForm();
		pageMap.put(ConfKey.CURRENT_PAGE, pagelist);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(KEY_USERNAME, credential.getUsername());
		paramMap.put(KEY_PASSWORD, credential.getPassword());
		NextPage np = new NextPage(landingUrl);
		clickClickStream(loginClickStream, pageMap, paramMap, cconf, np);
		pagelist = (List<HtmlPage>) pageMap.get(ConfKey.CURRENT_PAGE);
		HtmlPage afterLoginPage = pagelist.get(0);
		LoginType loginInfo = tasksDef.getTasks().getLoginInfo();
		//check gotcha
		if (isGotcha(afterLoginPage, loginInfo, cconf)){
			return LoginStatus.LoginCatchya;
		}else{
			if (loginInfo.getLoginSuccessCondition()!=null){
				boolean ret = BinaryBoolOpEval.eval(afterLoginPage, cconf, loginInfo.getLoginSuccessCondition(), null);
				if (ret){
					return LoginStatus.LoginSuccess;
				}else{
					return LoginStatus.LoginFailed;
				}
			}else{//if no login success condition defined using below system logic
				String afterLoginUrl = afterLoginPage.getUrl().toExternalForm();
				if (afterLoginUrl.equals(landingUrl)){
					logger.error(String.format("login failed? remain on the same page, with loginInfo: %s", loginInfo));
					return LoginStatus.LoginFailed;
				}else{
					logger.info(String.format("login successfully, current url: %s, before url:%s", afterLoginUrl, np));
					return LoginStatus.LoginSuccess;
				}
			}
		}
	}
	
	public static int checkLockedCrendentials(String landingUrl, ParsedTasksDef siteDef, CrawlConf cconf) throws InterruptedException{
		int unlocked=0;
		ClickStreamType loginClickStream = siteDef.getLoginClickStream(landingUrl);
		for (CredentialType ct: siteDef.getTasks().getLoginInfo().getCredential()){
			WebClient wc = CrawlUtil.getWebClient(cconf, null, true);
			HtmlPage landingPage = null;
			try {
				landingPage = wc.getPage(landingUrl);
				LoginStatus ls = login(landingPage, loginClickStream, siteDef, ct, wc, cconf);
				if (LoginStatus.LoginSuccess == ls){
					unlocked++;
				}
			} catch (FailingHttpStatusCodeException | IOException e) {
				logger.error("", e);
			}
			wc.closeAllWindows();
		}
		return unlocked;
	}
	
	/**
	 * 
	 * @param loginInfo
	 * @return credential which is not in the usedCredentials set
	 */
	private static CredentialType getCredential(LoginType loginInfo, Set<String> usedCredentials){
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
			return c;
		}else{
			logger.error("no credential is configured.");
			return null;
		}
	}
	
	/**
	 * 
	 * @param clickstream
	 * @param pageMap: in-out
	 * @param params
	 * @param cconf
	 * @param currentNP, the url of current page for log
	 * @throws InterruptedException 
	 */
	public static void clickClickStream(ClickStreamType clickstream, Map<String, List<? extends DomNode>> pageMap, 
			Map<String, Object> params, CrawlConf cconf, NextPage currentNP) throws InterruptedException{
		//setup the map
		Map<String, ClickType> clickMap = new HashMap<String, ClickType>(); //click name to click definition map
		for (ClickType clickType: clickstream.getLink()){
			if (clickType.getPageName()!=null){
				clickMap.put(clickType.getPageName(), clickType);
			}
		}
		
		if (clickstream.getLink().size()>0){
			int clickIdx=0;
			ClickType curClick = clickstream.getLink().get(clickIdx);
			while (curClick!=null){
				DomNode currentPage = pageMap.get(ConfKey.CURRENT_PAGE).get(0);
				//input parameter assignments, based on current page
				if (curClick.getInput()!=null){
					for (AttributeType input:curClick.getInput()){
						Object inputValue = CrawlTaskEval.eval(pageMap, input.getValue(), cconf, params);
						if (inputValue instanceof String){
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
				//do the print
				if (curClick.getPrint()!=null){
					for (AttributeType print:curClick.getPrint()){
						Object printValue = CrawlTaskEval.eval(pageMap, print.getValue(), cconf, params);
						if (printValue!=null)
							logger.info(print.getName() + ":" + printValue.toString());
						else
							logger.info(print.getName() + " is null.");
					}
				}
				//eval condition
				ConditionalNextPage cnp = curClick.getNextpage();
				AttributeType nextPage = null;
				if (cnp.getCondition()==null){
					nextPage = cnp.getSuccessNextPage();
				}else{
					if (BinaryBoolOpEval.eval(currentPage, cconf, cnp.getCondition(), null)){
						nextPage = cnp.getSuccessNextPage();
					}else{
						nextPage = cnp.getFailNextPage();
					}
				}
				
				if (cnp.getWaitTime()!=null){
					Thread.sleep(cnp.getWaitTime()*1000);
				}
				
				//do the click
				ValueType vt = nextPage.getValue();
				if (vt!=null){
					vt.setToType(VarType.PAGE);//for click stream, the to type is page
					Object value = CrawlTaskEval.eval(pageMap, vt, cconf, params);
					if (value!=null && value instanceof HtmlPage){
						List<HtmlPage> pagelist1= new ArrayList<HtmlPage>();
						pagelist1.add((HtmlPage)value);
						pageMap.put(nextPage.getName(), pagelist1);
						pageMap.put(ConfKey.CURRENT_PAGE, pagelist1); //set current page
					}else{
						logger.error(String.format("click stream:%s eval to %s, not a page, check toType. prdPage is:%s", 
								vt.getValue(), value, currentNP==null? "null":currentNP));
						break;
					}
				}
				//get the next click
				curClick = clickMap.get(nextPage.getName());
				if (curClick==null){
					//page name not defined for each click, try get the next click
					if (clickIdx<clickstream.getLink().size()-1){
						clickIdx++;
						curClick = clickstream.getLink().get(clickIdx);
					}
				}
				if (curClick==null){
					logger.debug(String.format("next click with name:%s is null, exit.", nextPage.getName()));
				}
			}
		}
	}

	/**
	 * For every url, check the landingUrl to see whether login required,
	 * if so, login in (find the account which is not locked) and go to the tryUrl, 
	 * making the login transparent
	 * @param landingUrl
	 * @param tryUrl
	 * @param wc
	 * @param task
	 * @param cconf
	 * @return true means login successful, false means either no login performed or login failed.
	 * @throws InterruptedException
	 */
	private static LoginStatus tryLogin(HtmlPage landingPage, String tryUrl, WebClient wc, ParsedTasksDef siteDef, CrawlConf cconf) 
			throws InterruptedException{
		String landingUrl = landingPage.getUrl().toExternalForm();
		if (siteDef!=null && siteDef.getTasks().getLoginInfo()!=null){
			LoginType loginInfo = siteDef.getTasks().getLoginInfo();
			if (tryUrl!=null && !landingUrl.contains(tryUrl)){
				boolean requireLogin = false;
				ClickStreamType loginClickStream = null;
				if (isGotcha(landingPage, loginInfo, cconf)){//check gotcha
					requireLogin = true;
				}else {//check login redirected
					loginClickStream = siteDef.getLoginClickStream(landingUrl);
					if (loginClickStream == null){
						loginClickStream = siteDef.getLoginClickStream(landingPage);
					}
					if (loginClickStream != null){
						requireLogin = true;
					}
				}
				//redirected to one of the possibleRedirectedUrls
				if (requireLogin) {
					Set<String> usedCredentials = new HashSet<String>();
					while (usedCredentials.size()<loginInfo.getCredential().size()){
						CredentialType ct = getCredential(loginInfo, usedCredentials);
						if (ct!=null){
							usedCredentials.add(ct.getUsername());
							LoginStatus ls = login(landingPage, loginClickStream, siteDef, ct, wc, cconf);
							if(LoginStatus.LoginCatchya==ls){
								logger.info(String.format("catchya using user %s", ct.getUsername()));
								continue;
							}else{
								return ls;
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
				logger.debug(String.format("No Login: landing page %s contains the expected page %s.", landingUrl, tryUrl));
			}
		}else{
			logger.debug(String.format("No Login: loginInfo is null."));
		}
		return LoginStatus.LoginNotNeeded;
	}
	
	public static final int maxloop = 15; //number of wait time
	public static final int waitTime = 1000; //ms for each wait
	//total is maxloop * waitTime
	public static boolean waitVerify(HtmlPage page, VerifyPage vp, Object param) throws InterruptedException{
		int innerloop=0;
		Date tick1 = null;
		Date tick2 = null;
		if (logger.isDebugEnabled()){
			tick1 = new Date();
		}
		while(innerloop<maxloop){
			if (page != null){
				if (vp !=null){
					//if has verification, default is failed
					if (vp.verifySuccess(page, param)){
						if (logger.isDebugEnabled()){
							tick2 = new Date();
							long elapse = tick2.getTime() - tick1.getTime();
							logger.debug("time elapse:" + elapse + " for loading:" + page.getUrl().toExternalForm());
						}
						return true;
					}else{
						synchronized(page){
							page.wait(1000);//wait for the necessary js done
							logger.warn(String.format("wait for the expected value come out, wait %d times, max %d times", innerloop, maxloop));
						}
						innerloop++;
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param wc
	 * @param np: next page
	 * @param vp: verification plugin
	 * @param param: the parameter to be used for customer verification, can be task instance
	 * @param LoginInfo: the login info defined in tasksdef
	 * @param cconf, contains retry times, cancelable
	 * @return the page result
	 * @throws InterruptedException
	 */
	public static HtmlPageResult clickNextPageWithRetryValidate(WebClient wc, NextPage np, VerifyPage vp, Object param, 
			ParsedTasksDef siteDef, CrawlConf cconf) 
			throws InterruptedException{
		HtmlPageResult result = new HtmlPageResult();
		HtmlPage page = null;
		int tried = 0;
		while (tried < cconf.getMaxRetry()){
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
					page = getDirectPage(wc, np);
					int innerloop=0;
					while(innerloop<maxloop){
						//refetch the page
						//page = (HtmlPage) page.getWebClient().getWebWindows().get(0).getEnclosedPage();
						if (page != null){
							//
							LoginStatus loginStatus = tryLogin(page, np.getNextUrl(), wc, siteDef, cconf);
							if (LoginStatus.LoginNotNeeded != loginStatus){
								if (LoginStatus.LoginSuccess == loginStatus){
									page = getDirectPage(wc, np);
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
										page.wait(waitTime);//wait for the necessary js done
										logger.warn(String.format("wait for the expected value come out, wait %d times, max %d times", innerloop, maxloop));
										//logger.debug(page.asText());
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
						}else{
							result.setErrorMsg("page got is null.");
							logger.error(String.format("page got is null."));
							result.setErrorCode(HtmlPageResult.SYSTEM_ERROR);
							return result;
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
		
		logger.warn("retried with :" + np + ". reach max times:" + cconf.getMaxRetry());
		return result;
	}
}
