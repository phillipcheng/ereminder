package org.cld.sites.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.cld.datacrawl.CrawlUtil;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestWeibo extends TestBase{
	public static final String SITE_CONF_FILE ="weibo.xml";
	@Test
	public void checkUnlockedAccounts(){
		int i = getUnlockedAccounts(SITE_CONF_FILE);
		logger.info(String.format("%d unlocked accounts for %s", i, SITE_CONF_FILE));
	}
	
	@Test
	public void testLogin() throws Exception{
		WebClient wc = CrawlUtil.getWebClient(cconf, null, true);
		HtmlPage page = wc.getPage("http://us.weibo.com/gb#");
		HtmlAnchor ha = page.getFirstByXPath("//a[contains(text(), '登入微博')]");
		page = ha.click();
		HtmlInput username = page.getFirstByXPath("//p[@class='weibo-logindialog-account']/input");
		username.setValueAttribute("phillipchengyia@gmail.com");
		HtmlInput password = page.getFirstByXPath("//p[@class='weibo-logindialog-password']/input");
		password.setValueAttribute("testtest");
		HtmlAnchor submit = page.getFirstByXPath("//div[@class='weibo-logindialog-form']/p[last()]/a[1]");
		submit.click();
		while(true){
			wc.waitForBackgroundJavaScript(1000);
			logger.info("wait...");
		}
	}
	
}
