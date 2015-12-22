package org.cld.datacrawl;

import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;

import org.cld.hadooputil.HdfsDownloadUtil;
import org.cld.util.ProxyConf;

public class CrawlUtil {
	private static Logger logger =  LogManager.getLogger(CrawlUtil.class);
	
	public static void closeWebClient(WebClient wc){
		if (wc!=null){
			try {
				wc.closeAllWindows();
			}catch(Throwable t){
				logger.error("ThreadDeath exception caught ignored", t);
			}
		}
	}
	
	
	public static void doubleTimeout(WebClient wc){
		int to = wc.getOptions().getTimeout();
		to = to*2;
		wc.getOptions().setTimeout(to);
	}
	
	public static WebClient getWebClient(CrawlConf cconf, String[] skipUrls, boolean enableJS){
		logger.debug("open webclient.");
		WebClient webClient = null;
		ProxyConf proxyConf = cconf.getProxyConf();
		if (cconf==null || !proxyConf.isUseProxy()){
			webClient = new WebClient(BrowserVersion.CHROME);
		}else{
			webClient = new WebClient(BrowserVersion.CHROME, proxyConf.getHost(), proxyConf.getPort());
		}
		WebConnection wc = new InterceptWebConnection(webClient, skipUrls);
		webClient.setWebConnection(wc);
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF); 

	    webClient.setCssErrorHandler(new SilentCssErrorHandler());

	    webClient.setIncorrectnessListener(new IncorrectnessListener() {
	        @Override
	        public void notify(String s, Object o) { }
	    });
	    
	    webClient.getCookieManager().setCookiesEnabled(false);//required by linkedin/sina.weibo
	    webClient.getOptions().setCssEnabled(true);
	    webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setAppletEnabled(false);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setJavaScriptEnabled(enableJS);
		webClient.getOptions().setUseInsecureSSL(true);
		if (enableJS){
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.waitForBackgroundJavaScript(20000);
			webClient.waitForBackgroundJavaScriptStartingBefore(20000);
		}
		int timeout = 10;
		if (cconf!=null){
			timeout = cconf.getTimeout();
		}
		webClient.getOptions().setTimeout(timeout*1000);
		
		return webClient;
	}
	
	public static void downloadPage(CrawlConf cconf, String url, String fileName){
		HdfsDownloadUtil.downloadFileToHdfs(url, cconf.getProxyConf(), 
				cconf.getHadoopCrawledItemFolder() + "/" + fileName, cconf.getHdfsDefaultName());
		
	}
	
	public static void downloadPage(CrawlConf cconf, InputStream is, String fileName){
		HdfsDownloadUtil.downloadFileToHdfs(is, cconf.getHadoopCrawledItemFolder() + "/" + fileName, cconf.getHdfsDefaultName());
		
	}
}
