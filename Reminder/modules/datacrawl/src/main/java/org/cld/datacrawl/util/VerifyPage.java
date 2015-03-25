package org.cld.datacrawl.util;


import com.gargoylesoftware.htmlunit.html.HtmlPage;


public interface VerifyPage {
	public boolean verifySuccess(HtmlPage page, Object param);

}
