package org.cld.datacrawl;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.mgr.CategoryAnalyze;
import org.cld.datacrawl.mgr.ListAnalyze;
import org.cld.datacrawl.mgr.ProductAnalyze;
import org.cld.datacrawl.mgr.ProductListAnalyze;
import org.cld.datastore.DBConf;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.impl.HbaseDataStoreManagerImpl;
import org.cld.datastore.impl.HdfsDataStoreManagerImpl;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.ProxyConf;
import org.cld.util.entity.Product;
import org.cld.util.entity.SiteConf;
import org.cld.util.jdbc.DBConnConf;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public class CrawlConf extends TaskConf {

	private static Logger logger =  LogManager.getLogger(CrawlConf.class);
	
	public static final String maxRetry_Key="retry.num";
	public static final String maxLoop_Key="max.loop";
	public static final String waitTime_Key="wait.time";
	public static final String timeout_Key="time.out";
	public static final String enableStat_Key="enable.stat";
	
	public static final String pluginDir_Key="plugin.dir";
	public static final String pluginJar_Key="plugin.jar";
	public static final String productType_Key="product.type";
	public static final String crawlTaskConf_Key="crawl.taskconf";
	//dsm can be a list, 1st is default, each site can pick one if not use default
	public static final String crawlDsManager_Key="crawl.ds.manager";
		public static final String crawlDsManager_Value_Nothing="nothing";
		public static final String crawlDsManager_Value_Hbase = "hbase";
		public static final String crawlDsManager_Value_Hdfs = "hdfs";
	public static final String crawlDBConnectionUrl_Key = "crawl.db.connection.url";
	
	public static final String DS_BIG="big.dm.";
	public static final String DS_SMALL="small.dm.";
	
	public static final String systemProductClassName="org.cld.util.entity.Product";
	public static final String systemProductName="product";
	
	private ProxyConf proxyConf;

	private int maxRetry=2;//immediate #retry for each broken link
	private int maxLoop=10;//immediate #retry for each broken link
	private int waitTime=1000;//immediate #retry for each broken link

	private int timeout; //page fetch time out in seconds
	private boolean enableSuccessStat=false;
	private String[] pluginDir;
	private String[] pluginJar;
	
	private CategoryAnalyze ca = new CategoryAnalyze();
	private ListAnalyze la = new ListAnalyze();
	private ProductAnalyze pa = new ProductAnalyze();
	private ProductListAnalyze pla = new ProductListAnalyze();
	
	private List<String> crawlDsManagerValue = new ArrayList<String>();

	private String crawlDBConnectionUrl;
	//type to dsm instance, //TODO, name to dsm instance, same type can be multiple different dsm
	private Map<String, DataStoreManager> dsmMap = new HashMap<String, DataStoreManager>();

	private DBConnConf bigdbconf = null;
	private DBConnConf smalldbconf = null;
	
	private Map<String, String> params = new HashMap<String, String>();//store all the parameters in name-value pair for toString
	
	private Map<String, ProductConf> prdConfMap = new ConcurrentHashMap<String, ProductConf>();
	private Map<String, ProductConf> oldPrdConfMap = new HashMap<String, ProductConf>();
	
	private String masterConfFile;//master configuration file: server.properties, client1.properties
	private PropertiesConfiguration properties;//master configuration marshaled
	private List<CrawlConfListener> listeners = new ArrayList<CrawlConfListener>();//list of crawl-conf-change-listeners
	private ClassLoader pluginClassLoader;
	private TaskMgr taskMgr;
	
	public CrawlConf(String properties){
		super(properties);
		taskMgr = new TaskMgr();
		setup(properties);
	}
	
	public DBConf getDBConf(){
		DBConf dbconf = new DBConf();		
		dbconf.setDbConnectionUrl(this.getCrawlDBConnectionUrl());
		dbconf.setHibernateCfgFile("crawl.hibernate.cfg.xml");
		return dbconf;
	}
	
	public void setup(String file){
		taskMgr.setUp(file);
		this.masterConfFile = file;
		try {
			properties = new PropertiesConfiguration(file);
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
		//just for the pluginDir
		readFixedProperties();
		
		reload();
		
		//for system product definition
		loadSystemPrdDef();
	}
	
	//TODO
	//current implementation is wrong, i should first read in new conf
	//then compare with current conf to delete, update or add, instead of
	//clearing the current conf now.
	/**
	 * 2 things can't be changed during reloading
	 * 1. the master config file
	 * 2. the plugin dir
	 */
	public void reload(){
		//after read properties, the new ctconf map is empty
		readPropertiesFromConfig();
		
		URL[] pluginurls = getPlugURL();
		if (pluginurls.length==0){
			pluginClassLoader = this.getClass().getClassLoader();
		}else{
			pluginClassLoader = new URLClassLoader(pluginurls);
		}
		
		la.setup(this, pla);
		
		//reload ProductConf
		Iterator<String> its = this.prdConfMap.keySet().iterator();
		while (its.hasNext()){
			String key = its.next();
			loadProductConf(key);
		}
		
		//
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskMgr.reload(pluginClassLoader, taskParams);
		
		//
		for (String dsmtype:crawlDsManagerValue){
			if (dsmtype.equals(crawlDsManager_Value_Hbase)){
				dsmMap.put(dsmtype, new HbaseDataStoreManagerImpl(HadoopTaskLauncher.getHadoopConf(this)));
			}else if (dsmtype.equals(crawlDsManager_Value_Hdfs)){
				dsmMap.put(dsmtype, new HdfsDataStoreManagerImpl(HadoopTaskLauncher.getHadoopConf(this), 
						this.getHadoopCrawledItemFolder()));
			}
		}
		
	}
	
	private void loadProductConf(String key){
		ProductConf prdConf = this.prdConfMap.get(key);
		Class<Product> clazzPrd;
		try {
			clazzPrd = (Class<Product>) Class.forName(prdConf.getEntityImpl(), true, pluginClassLoader);
			prdConf.setProductClass(clazzPrd);
			//set the handler
			String handlerClassName = prdConf.getHandlerClassName();
			if (handlerClassName!=null){
				try {
					Class<?> cl=Class.forName(handlerClassName, false, pluginClassLoader);
					Constructor<?> cons = cl.getConstructor(CrawlConf.class);
					ProductHandler ph = (ProductHandler) cons.newInstance(this);
					prdConf.setPrdHandler(ph);
				} catch (Exception e) {
					logger.error("", e);
				}
			}else{
				logger.error(String.format("handler class for product type:%s not specified.", key));
			}
			prdConfMap.put(key, prdConf);	
		} catch (ClassNotFoundException e) {
			logger.error("", e);
		}
	}
	
	private void loadSystemPrdDef(){
		ProductConf prdConf = new ProductConf();
		prdConf.setName(systemProductName);
		prdConf.setEntityImpl(systemProductClassName);		
		try {
			Class<Product> clazzPrd = (Class<Product>) Class.forName(prdConf.getEntityImpl());
			prdConf.setProductClass(clazzPrd);
			prdConfMap.put(prdConf.getName(), prdConf);
		} catch (ClassNotFoundException e) {
			logger.error("system 'product' definition not found.", e);
		}		
	}
	
	
	private void readFixedProperties(){
		try{
			Iterator<String> enu = properties.getKeys();
			List<Object> strListVal=null;
			while(enu.hasNext()){
				String key = enu.next();				
				//for all types of crawl tasks
				if (pluginDir_Key.equals(key)){
					strListVal = properties.getList(key);
					if (strListVal != null){
						pluginDir = new String[strListVal.size()];
						for (int i=0; i<strListVal.size(); i++){
							pluginDir[i] = (String) strListVal.get(i);
						}
					}
				}else if (pluginJar_Key.equals(key)){
					strListVal = properties.getList(key);
					if (strListVal != null){
						pluginJar = new String[strListVal.size()];
						for (int i=0; i<strListVal.size(); i++){
							pluginJar[i] = (String) strListVal.get(i);
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}

	private void readPropertiesFromConfig(){
		try{
			oldPrdConfMap.clear();
			Iterator<String> its  = prdConfMap.keySet().iterator();
			while (its.hasNext()){
				String key = its.next();
				oldPrdConfMap.put(key, prdConfMap.get(key));
			}
			//do not need to clear, use the new definition to replace
			//prdConfMap.clear();
			
			//2. reload conf
			properties = new PropertiesConfiguration(this.masterConfFile);
			
			setProxyConf(new ProxyConf(properties));
			bigdbconf = new DBConnConf(DS_BIG, properties);
			smalldbconf = new DBConnConf(DS_SMALL, properties);
			//3. parse new
			Iterator<String> enu = properties.getKeys();
			String strVal=null;
			while(enu.hasNext()){
				String key = enu.next();	
				strVal = properties.getString(key);
				if (maxRetry_Key.equals(key)){
					maxRetry = Integer.parseInt(strVal);
				}else if (timeout_Key.equals(key)){
					timeout=Integer.parseInt(strVal);
				}else if (maxLoop_Key.equals(key)){
					maxLoop =  Integer.parseInt(strVal);
				}else if (waitTime_Key.equals(key)){
					waitTime =  Integer.parseInt(strVal);
				}else if (enableStat_Key.equals(key)){
					enableSuccessStat = Boolean.parseBoolean(strVal);
				}else if (crawlDsManager_Key.equals(key)){
					List<Object> listVal = properties.getList(key);
					for (int i=0;  i<listVal.size(); i++){
						String dsmtype = (String)listVal.get(i);
						if (crawlDsManager_Value_Hbase.equals(dsmtype)){
							crawlDsManagerValue.add(dsmtype);
						}else if (crawlDsManager_Value_Hdfs.equals(dsmtype)){
							crawlDsManagerValue.add(dsmtype);
						}else if (crawlDsManager_Value_Nothing.equals(dsmtype)){
							crawlDsManagerValue.add(dsmtype);
						}else{
							logger.error("unsupported ds manager type:" + dsmtype);
						}
					}
				}else if (crawlDBConnectionUrl_Key.equals(key)){
					crawlDBConnectionUrl = strVal;
				}else if (productType_Key.equals(key)){
					List<Object> listVal = properties.getList(key);
					for (int i=0;  i<listVal.size(); i++){
						String pt = (String)listVal.get(i);
						logger.debug("product key:'" + pt +"'");
						if (!"".equals(pt)){
							ProductConf prdConf = null;
							if (oldPrdConfMap.containsKey(pt)){
								prdConf = oldPrdConfMap.get(pt);
							}else{
								prdConf = new ProductConf();
							}
							prdConf.setName(pt);
							prdConf.setEntityImpl(properties.getString(pt + "." + ProductConf.productEntityImpl_Key));
							prdConf.setHandlerClassName(properties.getString(pt+"."+ProductConf.productHandlerImpl_Key));
							logger.debug("prdConfMap put:" + pt + ", " + prdConf);
							prdConfMap.put(pt, prdConf);
						}
					}
				}
				
				params.put(key, strVal);
			}
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}
	
	/**
	 * setup site, used by web-test module
	 * @param siteconfid
	 * @param confFileName
	 * @param confFileContent, if this is not null, using this, otherwise use the confFileName
	 */
	public List<Task> setUpSite(String confFileName, SiteConf sc){
		Map<String, Object> taskParams = new HashMap<String, Object>();
		if (sc==null){
			return taskMgr.setUpSite(confFileName, pluginClassLoader, taskParams);
		}else if (confFileName==null){
			return taskMgr.setUpSite(sc, pluginClassLoader, taskParams);
		}else{
			logger.error(String.format("one of sc or confFileName should be null."));
			return null;
		}
	}
	
	public void addListener(CrawlConfListener lis){
		listeners.add(lis);
	}
	
	//must be sub-class of org.cld.util.entity.Product
	public Product getProductInstance(String key){
		if (key==null || "".equals(key)){
			key="product";
		}
		logger.debug("prdKey to fetch:" + key);
		ProductConf prdConf = this.prdConfMap.get(key);
		if (prdConf.getPrdHandler()==null){
			logger.error(String.format("handler class for product type:%s not specified.", key));
			return null;
		}
		logger.debug("prdConf:" + prdConf);
		try {
			Product p = prdConf.getProductClass().newInstance();
			p.setItemType(key);
			return p;
		} catch (InstantiationException e) {
			logger.error("", e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("", e);
			return null;
		}
	}
	
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getMaxRetry() {
		return maxRetry;
	}
	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}	

	public boolean isEnableSuccessStat() {
		return enableSuccessStat;
	}
	public void setEnableSuccessStat(boolean enableSuccessStat) {
		this.enableSuccessStat = enableSuccessStat;
	}
	
	public Map<String, ProductConf> getPrdConfMap(){
		return prdConfMap;
	}
	
	public DataStoreManager getDsm(String type) {
		if (type!=null)
			return dsmMap.get(type);
		else
			return null;
	}
	
	public DataStoreManager getDefaultDsm() {
		return dsmMap.get(this.crawlDsManagerValue.get(0));
	}
	
	public URL[] getPlugURL(){
		List<URL> urlList = new ArrayList<URL>();
		
		try {
			if (pluginDir!=null){
				for (int i=0; i<pluginDir.length; i++){
					urlList.add(new URL(new URL("file:"), pluginDir[i]));
				}
			}
			if (pluginJar!=null){
				for (int i=0; i<pluginJar.length; i++){
					urlList.add(new URL(new URL("file:"), pluginJar[i]));
				}
			}
		} catch (MalformedURLException e) {
			logger.error("",e);
		}
		
		URL[] a = new URL[urlList.size()];
		return (URL[]) urlList.toArray(a); 
	}

	public ClassLoader getPluginClassLoader() {
		return pluginClassLoader;
	}

	public void setPluginClassLoader(ClassLoader pluginClassLoader) {
		this.pluginClassLoader = pluginClassLoader;
	}

	public String getCrawlDBConnectionUrl() {
		return crawlDBConnectionUrl;
	}

	public void setCrawlDBConnectionUrl(String crawlDBConnectionUrl) {
		this.crawlDBConnectionUrl = crawlDBConnectionUrl;
	}
	
	public Map<String, String> getParams(){
		return params;
	}
	
	public TaskMgr getTaskMgr(){
		return taskMgr;
	}

	public CategoryAnalyze getCa() {
		return ca;
	}

	public void setCa(CategoryAnalyze ca) {
		this.ca = ca;
	}

	public ListAnalyze getLa() {
		return la;
	}

	public void setLa(ListAnalyze la) {
		this.la = la;
	}

	public ProductAnalyze getPa() {
		return pa;
	}

	public void setPa(ProductAnalyze pa) {
		this.pa = pa;
	}

	public ProductListAnalyze getPla() {
		return pla;
	}

	public void setPla(ProductListAnalyze pla) {
		this.pla = pla;
	}

	public int getMaxLoop() {
		return maxLoop;
	}

	public void setMaxLoop(int maxLoop) {
		this.maxLoop = maxLoop;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	
	public List<String> getCrawlDsManagerValue() {
		return crawlDsManagerValue;
	}

	public void setCrawlDsManagerValue(List<String> crawlDsManagerValue) {
		this.crawlDsManagerValue = crawlDsManagerValue;
	}

	public DBConnConf getBigdbconf() {
		return bigdbconf;
	}

	public void setBigdbconf(DBConnConf bigdbconf) {
		this.bigdbconf = bigdbconf;
	}

	public DBConnConf getSmalldbconf() {
		return smalldbconf;
	}

	public void setSmalldbconf(DBConnConf smalldbconf) {
		this.smalldbconf = smalldbconf;
	}

	public ProxyConf getProxyConf() {
		return proxyConf;
	}

	public void setProxyConf(ProxyConf proxyConf) {
		this.proxyConf = proxyConf;
	}
}
