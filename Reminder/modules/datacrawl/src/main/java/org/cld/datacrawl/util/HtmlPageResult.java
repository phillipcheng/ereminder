package org.cld.datacrawl.util;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlPageResult {

	public static final int UNCHECKED =0;
	//success
	public static final int SUCCSS =1;
	
	//system layer error code
	public static final int EC_SYSTEM_ERROR=2;
	
	//application layer error code
	public static final int EC_APP_ITEM_NOT_FOUND=100;
	public static final int EC_VERI_FAILED=101;
	
	
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
