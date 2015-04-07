package org.cld.sites.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.sinawebo.Login;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestWeibo extends TestBase{
	public static final String SITE_CONF_FILE ="weibo.xml";
	
	public static final String[] startUrls = new String[]{
		"https://www.linkedin.com/vsearch/c?f_I=4&f_CCR=us%3A84&f_CS=D,E&page_num=1",
	};
	
	@Test
	public void run_linkedin_bct() throws Exception{
		for (String startUrl:startUrls){
			catNavigate(SITE_CONF_FILE, startUrl, CrawlTestUtil.BROWSE_CAT_TYPE_RECURSIVE);	
		}
	}
	
	@Test
	public void checkUnlockedAccounts(){
		int i = getUnlockedAccounts(SITE_CONF_FILE);
		logger.info(String.format("%d unlocked accounts for %s", i, SITE_CONF_FILE));
	}
	
	
	@Test
	public void testLogin() throws Exception{
		WebClient wc = CrawlUtil.getWebClient(cconf, null, true);
		HtmlPage page = wc.getPage("http://us.weibo.com/gb#");
		Thread.sleep(1000);
		HtmlAnchor ha = page.getFirstByXPath("//a[contains(text(), '登入微博')]");
		page = ha.click();
		HtmlInput username = page.getFirstByXPath("//p[@class='weibo-logindialog-account']/input");
		username.setValueAttribute("phillipchengyia@gmail.com");
		HtmlInput password = page.getFirstByXPath("//p[@class='weibo-logindialog-password']/input");
		password.setValueAttribute("testtest");
		HtmlAnchor submit = page.getFirstByXPath("//div[@class='weibo-logindialog-form']/p[last()]/a[1]");
		page = submit.click();
		Thread.sleep(5000);
		ha = page.getFirstByXPath("//div[@class='topBar']//span[@class='logout']/a");

		logger.info("ha:" + ha.asText());
		
	}
	
	@Test
	public void testHardCodeLogin() throws Exception{
		WebClient wc = CrawlUtil.getWebClient(cconf, null, false);
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "phillipchengyia@gmail.com");
		params.put("password", "testtest");
		Login l = new Login();
		l.login(wc, params);
	}
}
