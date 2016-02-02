package org.cld.sites.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datacrawl.test.TestBase;
import org.cld.datacrawl.test.BrowseType;
import org.cld.sinawebo.Login;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

public class TestWeibo extends TestBase{
	public static final String SITE_CONF_FILE ="weibo.xml";
	
	private String propFile = "client1-v2.properties";
	
	@Before
	public void setUp(){
		super.setProp(propFile);
	}
	
	public static final String[] bct_startUrls = new String[]{
		"http://www.weibo.com/hwrichardyu",
	};
	
	@Test
	public void run_weibo_bct() throws Exception{
		for (String startUrl:bct_startUrls){
			catNavigate(SITE_CONF_FILE, startUrl, BrowseType.recursive, 1);	
		}
	}
	
	@Test
	public void checkUnlockedAccounts1(){
		int i = getUnlockedAccounts("http://us.weibo.com/gb", SITE_CONF_FILE);
		logger.info(String.format("%d unlocked accounts for %s", i, SITE_CONF_FILE));
	}
	
	@Test
	public void checkUnlockedAccounts2(){
		int i = getUnlockedAccounts("http://www.weibo.com/login.php", SITE_CONF_FILE);
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
	public void testLogin2() throws Exception{
		WebClient wc = CrawlUtil.getWebClient(cconf, null, true);
		HtmlPage page = wc.getPage("http://www.weibo.com/login.php");
		Thread.sleep(5000);
		HtmlInput username = page.getFirstByXPath("id('pl_login_form')/div[5]/div[1]/div/input");
		username.setValueAttribute("phillipchengyia@gmail.com");
		HtmlInput password = page.getFirstByXPath("id('pl_login_form')/div[5]/div[2]/div/input");
		password.setValueAttribute("testtest");
		HtmlSpan submit = page.getFirstByXPath("id('pl_login_form')/div[5]/div[6]/div[1]/a/span");
		logger.info(submit);
		page = submit.click();
		Thread.sleep(15000);
		HtmlElement ha = page.getFirstByXPath("//a[@nm='name']");

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
