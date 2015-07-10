package org.cld.datacrawl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;

import org.cld.datastore.DBConf;
import org.cld.datastore.DBFactory;
import org.cld.datastore.impl.HibernateDataStoreManagerImpl;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskTypeConf;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskUtil;
import org.cld.util.DownloadUtil;

public class CrawlUtil {
	private static Logger logger =  LogManager.getLogger(CrawlUtil.class);
	
	public static final String CRAWL_PROPERTIES="crawl.properties.file"; //the location of the properties file
	
	
	public static void closeWebClient(WebClient wc){
		logger.debug("close webclient.");
		wc.closeAllWindows();
	}
	
	
	public static void doubleTimeout(WebClient wc){
		int to = wc.getOptions().getTimeout();
		to = to*2;
		wc.getOptions().setTimeout(to);
	}
	
	public static WebClient getWebClient(CrawlConf cconf, String[] skipUrls, boolean enableJS){
		logger.debug("open webclient.");
		WebClient webClient = null;
		if (cconf==null || !cconf.isUseProxy()){
			webClient = new WebClient(BrowserVersion.CHROME);
		}else{
			webClient = new WebClient(BrowserVersion.CHROME, cconf.getProxyIP(), cconf.getProxyPort());
		}
		WebConnection wc = new InterceptWebConnection(webClient, skipUrls);
		webClient.setWebConnection(wc);
		
		//LogManager.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 

	    webClient.setCssErrorHandler(new SilentCssErrorHandler());

	    webClient.setIncorrectnessListener(new IncorrectnessListener() {
	        @Override
	        public void notify(String s, Object o) { }
	    });
	    
	    webClient.getCookieManager().setCookiesEnabled(true);//required by linkedin/sina.weibo
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
	
	public static void setupSessionFactory(NodeConf nc, CrawlConf cconf){
		if (cconf.getDsm(CrawlConf.crawlDsManager_Value_Hibernate)!=null){
			//fix up task session factory
			DBConf taskDBConf = nc.getDBConf();
			Configuration cfg = DBFactory.setUpCfg(nc.getNodeId(), TaskMgr.moduleName, taskDBConf);
			Iterator<TaskTypeConf> itTTC = cconf.getTaskMgr().getAllTaskTypes().iterator();
			while (itTTC.hasNext()){
				TaskTypeConf ttconf = itTTC.next();
				logger.info("adding task defintion:" + ttconf);
				cfg.addAnnotatedClass(ttconf.getTaskEntityClass());
				cfg.addAnnotatedClass(ttconf.getTaskStatClass());
			}
			DBFactory.setUpSF(cconf.getPluginClassLoader(), nc.getNodeId(), TaskMgr.moduleName, cfg);
			
			//fix up crawl session factory via API
			Configuration hCfg = DBFactory.setUpCfg(nc.getNodeId(), DataCrawl.moduleName, cconf.getDBConf());
			Map<String, ProductConf> prdConfMap = cconf.getPrdConfMap();
			Iterator<ProductConf> it = prdConfMap.values().iterator();
			while (it.hasNext()){
				ProductConf pconf = it.next();
				//cfg.addClass(pconf.getProductClass());
				hCfg.addAnnotatedClass(pconf.getProductClass());
				logger.debug("added annotated class:" + pconf.getProductClass());
			}
			DBFactory.setUpSF(cconf.getPluginClassLoader(), nc.getNodeId(), DataCrawl.moduleName, hCfg);
			cconf.setTaskSF(DBFactory.getDBSF(nc.getNodeId(), TaskMgr.moduleName));
			((HibernateDataStoreManagerImpl)cconf.getDsm(CrawlConf.crawlDsManager_Value_Hibernate)).setHibernateSF(DBFactory.getDBSF(nc.getNodeId(), DataCrawl.moduleName));
		}
	}	
	
	public static void addPrdConfToSessionFactory(ProductConf prdConf, CrawlConf cconf, String nodeId){
		if (cconf.getDsm(CrawlConf.crawlDsManager_Value_Hibernate)!=null){
			Configuration cfg = DBFactory.getDBCfg(nodeId, DataCrawl.moduleName);
			cfg.addAnnotatedClass(prdConf.getProductClass());
			DBFactory.setUpSF(cconf.getPluginClassLoader(), nodeId, DataCrawl.moduleName, cfg);
			((HibernateDataStoreManagerImpl)cconf.getDsm(CrawlConf.crawlDsManager_Value_Hibernate)).setHibernateSF(DBFactory.getDBSF(nodeId, DataCrawl.moduleName));
		}
	}	
	
	public static void hadoopExecuteCrawlTasks(String crawlPropertyFile, CrawlConf cconf, List<Task> tlist, 
			String sourceName, String hdfsOutputDir){
		Map<String, String> hadoopCrawlTaskParams = new HashMap<String, String>();
		hadoopCrawlTaskParams.put(CRAWL_PROPERTIES, crawlPropertyFile);
		if (sourceName==null){
			HadoopTaskUtil.executeTasks(cconf.getNodeConf(), tlist, hadoopCrawlTaskParams, hdfsOutputDir);
		}else{
			HadoopTaskUtil.executeTasks(cconf.getNodeConf(), tlist, hadoopCrawlTaskParams, sourceName, hdfsOutputDir);
		}
	}
	
	public static void hadoopExecuteCrawlTasks(String crawlPropertyFile, CrawlConf cconf, List<Task> tlist, 
			String hdfsOutputDir){
		hadoopExecuteCrawlTasks(crawlPropertyFile, cconf, tlist, null, hdfsOutputDir);
	}
	
	public static void hadoopExecuteCrawlTasks(String crawlPropertyFile, CrawlConf cconf, List<Task> tlist){
		hadoopExecuteCrawlTasks(crawlPropertyFile, cconf, tlist, null, null);
	}
	
	public static void downloadPage(CrawlConf cconf, String url, String fileName, String fileSaveDir){
		if (NodeConf.tmframework_hadoop.equals(cconf.getNodeConf().getTaskMgrFramework())){
			String finalSaveDir = cconf.getTaskMgr().getHadoopCrawledItemFolder() + "/" + fileSaveDir;
			DownloadUtil.downloadFileToHdfs(url, cconf.isUseProxy(), cconf.getProxyIP(), cconf.getProxyPort(), 
					finalSaveDir + "/" + fileName, cconf.getTaskMgr().getHdfsDefaultName());
		}else{
			DownloadUtil.downloadFile(url, cconf.isUseProxy(), cconf.getProxyIP(), cconf.getProxyPort(), 
					fileSaveDir, fileName);
		}
	}
}
