package org.cld.datacrawl.util;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlPageResult {

	public static final int UNCHECKED = -1;
	
	//success
	public static final int SUCCSS = 0;
	
	//failure >0
	public static final int VERIFY_FAILED=1;
	public static final int GOTCHA =2;//robot detected by sites
	public static final int LOGIN_FAILED=3;
	public static final int SYSTEM_ERROR =4;
	
	
	private HtmlPage page;
	private int errorCode=UNCHECKED;
	private Exception errorInfo;
	private String errorMsg;
	
	public HtmlPage getPage() {
		return page;
	}
	public void setPage(HtmlPage page) {
		this.page = page;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public Exception getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(Exception errorInfo) {
		this.errorInfo = errorInfo;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
