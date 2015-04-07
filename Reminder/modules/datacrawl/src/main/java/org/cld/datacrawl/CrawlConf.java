package org.cld.datacrawl;

import java.io.Serializable;
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

import org.hibernate.SessionFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cld.datacrawl.mgr.IProductAnalyze;
import org.cld.datacrawl.mgr.IProductListAnalyze;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datacrawl.mgr.IListAnalyze;
import org.cld.datacrawl.mgr.ListProcessInf;
import org.cld.datacrawl.pagea.ProductAnalyzeInf;
import org.cld.datacrawl.pagea.ProductListAnalyzeInf;
import org.cld.datacrawl.pagea.CategoryAnalyzeInf;
import org.cld.datacrawl.pagea.ListAnalyzeInf;
import org.cld.datastore.DBConf;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.Product;
import org.cld.datastore.entity.SiteConf;
import org.cld.datastore.impl.HbaseDataStoreManagerImpl;
import org.cld.datastore.impl.HibernateDataStoreManagerImpl;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.NodeConfChangedEvent;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;

public class CrawlConf implements AppConf, Serializable {
	
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(CrawlConf.class);
	
	public static final String useProxy_Key="use.proxy";
	public static final String proxyIP_Key="proxy.ip";
	public static final String proxyPort_Key="proxy.port";
	public static final String maxRetry_Key="retry.num";
	public static final String timeout_Key="time.out";
	public static final String enableStat_Key="enable.stat";
	public static final String brokenRetry_Key="broken.retry";
	public static final String pluginDir_Key="plugin.dir";
	public static final String pluginJar_Key="plugin.jar";	
	public static final String productAnalyze_Key="product.analyze";
	public static final String categoryAnalyze_Key="category.analyze";
	public static final String productListAnalyze_Key="productlist.analyze";
	public static final String listAnalyze_Key="list.analyze";
	public static final String promotionAnalyze_Key="promotion.analyze";
	public static final String productType_Key="product.type";
	public static final String crawlTaskConf_Key="crawl.taskconf";
	public static final String crawlDsManager_Key="crawl.ds.manager";
		public static final String crawlDsManager_Value_Hibernate="hibernate";
		public static final String crawlDsManager_Value_Hbase = "hbase";
	public static final String crawlDBConnectionUrl_Key = "crawl.db.connection.url";
	public static final String systemProductClassName="org.cld.datastore.entity.Product";
	
	public static final String systemProductName="product";
	
	public static final String taskParamCConf_Key="cconf";
	
	private boolean useProxy=false;
	private String proxyIP;
	private int proxyPort;
	private int maxRetry=3;//immediate #retry for each broken link	
	private int timeout; //page fetch time out in seconds
	private boolean enableSuccessStat=false;
	private int brokenRetry=0;//#retry after a whole set of op (top-link browse, all cat browse)
	private String[] pluginDir;
	private String[] pluginJar;
	private String productAnalyzeImpl = "org.cld.datacrawl.mgr.impl.ProductAnalyze";
	private String categoryAnalyzeImpl = "org.cld.datacrawl.mgr.impl.CategoryAnalyze";
	private String productListAnalyzeImpl = "org.cld.datacrawl.mgr.impl.ProductListAnalyze";
	private String listAnalyzeImpl = "org.cld.datacrawl.mgr.impl.ListAnalyze";
	private String promotionAnalyzeImpl = "org.cld.datacrawl.mgr.impl.PromotionAnalyze";
	private String crawlDsManagerValue = "nothing";
	private String crawlDBConnectionUrl;
	private DataStoreManager dsm;

	private Map<String, String> params = new HashMap<String, String>();//store all the parameters in name-value pair for toString
	
	private Map<String, CrawlTaskConf> tasksConf = new ConcurrentHashMap<String, CrawlTaskConf>();
	private Map<String, CrawlTaskConf> oldTasksConf = new HashMap<String, CrawlTaskConf>(); //for reload compare
	
	private Map<String, ProductConf> prdConfMap = new ConcurrentHashMap<String, ProductConf>();
	private Map<String, ProductConf> oldPrdConfMap = new HashMap<String, ProductConf>();
	
	private String masterConfFile;//master configuration file: server.properties, client1.properties
	private PropertiesConfiguration properties;//master configuration marshaled
	private List<CrawlConfListener> listeners = new ArrayList<CrawlConfListener>();//list of crawl-conf-change-listeners
	private ClassLoader pluginClassLoader;
	private TaskMgr taskMgr;
	private SessionFactory taskSF;
	private NodeConf nodeConf;
	
	public CrawlConf(){
		taskMgr = new TaskMgr();
	}
	
	public CrawlConf(String file, NodeConf nc){
		this();
		setup(file, nc);		
	}
	
	public DBConf getDBConf(){
		DBConf dbconf = new DBConf();		
		dbconf.setDbConnectionUrl(this.getCrawlDBConnectionUrl());
		dbconf.setHibernateCfgFile("crawl.hibernate.cfg.xml");
		return dbconf;
	}
	
	public void setup(String file, NodeConf nc){
		this.setNodeConf(nc);
		taskMgr.setUp(file, nc);
		this.masterConfFile = file;
		//InputStream inStream = CrawlConf.class.getClassLoader().getResourceAsStream(file);
		try {
			properties = new PropertiesConfiguration(file);
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
		//just for the pluginDir
		readFixedProperties();
		logger.info("plugin dir:" + StringUtils.join(this.pluginDir));
		logger.info("plugin jar:" + StringUtils.join(this.pluginJar));
		
		reload();
		
		//for system product definition
		loadSystemPrdDef();
	}
	
	//fill CTConf with newly created sub-instances
	private void reloadCTConf(CrawlTaskConf ctconf){
		try {			
			//reload CategoryAnalyzeInf and ICategoryAnalyze
			Class<?> caInfClass = Class.forName(ctconf.getCatImpl(), true, pluginClassLoader);
			CategoryAnalyzeInf cai = (CategoryAnalyzeInf) caInfClass.newInstance();
			cai.setCTConf(this, ctconf);
			ctconf.setCaInf(cai);
			
			Class<?> caClass= Class.forName(this.getCategoryAnalyzeImpl(), true, pluginClassLoader);
			ICategoryAnalyze ca = (ICategoryAnalyze) caClass.newInstance();
			ca.setup(this, ctconf);
			ctconf.setCa(ca);
			
			//reload ProductListAnalyzeInf and IProductListAnalyze
			Class<?> plaInfClass = Class.forName(ctconf.getProductListImpl(), true, pluginClassLoader);
			ProductListAnalyzeInf plai = (ProductListAnalyzeInf) plaInfClass.newInstance();
			plai.setCConf(this);
			ctconf.setBlaInf(plai);
			
			Class<?> plaClass = Class.forName(this.getProductListAnalyzeImpl(), true, pluginClassLoader);
			IProductListAnalyze pla = (IProductListAnalyze) plaClass.newInstance();
			pla.setup(this, ctconf);
			ctconf.setBla(pla);
			
			//reload ListAnalyzeInf and IListAnalyze
			Class<?> laInfClass = Class.forName(ctconf.getListImpl(), true, pluginClassLoader);
			ctconf.setLaInf( (ListAnalyzeInf) laInfClass.newInstance());
			
			Class<?> laClass = Class.forName(this.getListAnalyzeImpl(), true, pluginClassLoader);
			IListAnalyze la = (IListAnalyze) laClass.newInstance();
			la.setup(this, ctconf, (ListProcessInf) pla);
			ctconf.setLa(la);
			
			//reload ProductAnalyzeInf and IProductAnalyze
			Class<?> prdInfClass = Class.forName(ctconf.getProductDetailImpl(), true, pluginClassLoader);				
			ProductAnalyzeInf paInf =  (ProductAnalyzeInf) prdInfClass.newInstance();
			ctconf.setBaInf(paInf);
			paInf.setCConf(this);
			
			Class<?> prdSysClazz = Class.forName(this.getProductAnalyzeImpl(), true, pluginClassLoader);
			IProductAnalyze ba = (IProductAnalyze)  prdSysClazz.newInstance();
			ba.setup(this, ctconf);				
			ctconf.setBa(ba);
		}catch (Exception e) {
			logger.error("", e);
		} 
	}
	
	/**
	 * 
	 * @param key
	 * @return true for updated, false for nothing new
	 */
	private void loadCTConf(String key){
		CrawlTaskConf ctconf = tasksConf.get(key);
		
		reloadCTConf(ctconf);
		
		tasksConf.put(key, ctconf);	
		
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
		
		//
		if (dsm==null){
			if (crawlDsManagerValue.equals(crawlDsManager_Value_Hibernate)){
				dsm = new HibernateDataStoreManagerImpl();
			}else if (crawlDsManagerValue.equals(crawlDsManager_Value_Hbase)){
				dsm = new HbaseDataStoreManagerImpl();
			}else{
				logger.error("unsupported data store manager type:" + crawlDsManagerValue);
			}
		}
		
		URL[] pluginurls = getPlugURL();
		if (pluginurls.length==0){
			pluginClassLoader = this.getClass().getClassLoader();
		}else{
			pluginClassLoader = new URLClassLoader(pluginurls);
		}
		
		//reload ctconf
		Iterator<String> its = tasksConf.keySet().iterator();
		while (its.hasNext()){
			String key = its.next();
			if (!oldTasksConf.containsKey(key)){
				//newly added
				loadCTConf(key);
				CrawlConfChangedEvent ccce = new CrawlConfChangedEvent();
				ccce.setOpType(CrawlConfChangedEvent.OP_ADD);
				ccce.setPropName(CrawlConfChangedEvent.PROP_NAME_CTCONF);
				ccce.setCtcValue(tasksConf.get(key));
				fireEvent(ccce);	
			}else{
				//existing
				loadCTConf(key);
			}
		}
		
		its = oldTasksConf.keySet().iterator();
		while (its.hasNext()){
			String key = its.next();
			if (!tasksConf.containsKey(key)){
				//deleted task conf
				CrawlConfChangedEvent ccce = new CrawlConfChangedEvent();
				ccce.setOpType(CrawlConfChangedEvent.OP_REMOVE);
				ccce.setPropName(CrawlConfChangedEvent.PROP_NAME_CTCONF);
				ccce.setCtcValue(oldTasksConf.get(key));
				fireEvent(ccce);	
			}
		}	
		
		//reload ProductConf
		its = this.prdConfMap.keySet().iterator();
		while (its.hasNext()){
			String key = its.next();
			loadProductConf(key);
		}
		logger.info("prdConfMap:" + prdConfMap);
		
		//
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put(taskParamCConf_Key, this);
		taskMgr.reload(pluginClassLoader, taskParams);
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
			//1. clone to the oldTasksConf, oldPrdConf
			oldTasksConf.clear();
			Iterator<String> its = tasksConf.keySet().iterator();
			while (its.hasNext()){
				String key = its.next();
				oldTasksConf.put(key, tasksConf.get(key));
			}
			tasksConf.clear();
			
			oldPrdConfMap.clear();
			its = prdConfMap.keySet().iterator();
			while (its.hasNext()){
				String key = its.next();
				oldPrdConfMap.put(key, prdConfMap.get(key));
			}
			//do not need to clear, use the new definition to replace
			//prdConfMap.clear();
			
			//2. reload conf
			properties = new PropertiesConfiguration(this.masterConfFile);
			
			//3. parse new
			Iterator<String> enu = properties.getKeys();
			String strVal=null;
			while(enu.hasNext()){
				String key = enu.next();	
				strVal = properties.getString(key);
				//for all types of crawl tasks
				if (useProxy_Key.equals(key)){
					useProxy = Boolean.parseBoolean(strVal);
				}else if (proxyIP_Key.equals(key)){
					proxyIP = strVal;
				}else if (proxyPort_Key.equals(key)){
					proxyPort = Integer.parseInt(strVal);
				}else if (maxRetry_Key.equals(key)){
					maxRetry = Integer.parseInt(strVal);
				}else if (timeout_Key.equals(key)){
					timeout=Integer.parseInt(strVal);
				}else if (enableStat_Key.equals(key)){
					enableSuccessStat = Boolean.parseBoolean(strVal);
				}else if (brokenRetry_Key.equals(key)){
					brokenRetry =  Integer.parseInt(strVal);
				}else if (crawlDsManager_Key.equals(key)){
					if (crawlDsManager_Value_Hibernate.equals(strVal)){
						crawlDsManagerValue = crawlDsManager_Value_Hibernate;
					}else if (crawlDsManager_Value_Hbase.equals(strVal)){
						crawlDsManagerValue = crawlDsManager_Value_Hbase;
					}else{
						logger.error("unsupported ds manager type:" + strVal);
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
				}else if (crawlTaskConf_Key.equals(key)){
					List<Object> listVal = properties.getList(key);
					for (int i=0;  i<listVal.size(); i++){
						String tt = (String)listVal.get(i);
						CrawlTaskConf cctconf = null;
						if (oldTasksConf.containsKey(tt)){
							cctconf = oldTasksConf.get(tt);
						}else{
							cctconf = new CrawlTaskConf();
						}
						cctconf.setName(tt);
						cctconf.setCatImpl(properties.getString(tt + "." + CrawlTaskConf.catImpl_Key));
						cctconf.setListImpl(properties.getString(tt + "." + CrawlTaskConf.listImpl_Key));
						cctconf.setProductListImpl(properties.getString(tt + "." + CrawlTaskConf.productListImpl_Key));
						cctconf.setProductDetailImpl(properties.getString(tt + "." + CrawlTaskConf.productDetailImpl_Key));
						cctconf.setPromDetailImpl(properties.getString(tt + "." + CrawlTaskConf.promDetailImpl_Key));
						tasksConf.put(tt, cctconf);
					}
				}
				
				params.put(key, strVal);
			}
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}
	
	private void fireEvent(CrawlConfChangedEvent ccce){		
		for (int i=0; i<listeners.size();i++){
			CrawlConfListener ccl = listeners.get(i);
			ccl.crawlConfChanged(ccce);
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
		taskParams.put(taskParamCConf_Key, this);
		return taskMgr.setUpSite(confFileName, sc, pluginClassLoader, taskParams);
	}
	
	public void addListener(CrawlConfListener lis){
		listeners.add(lis);
	}	
	
	public CrawlTaskConf getCCTConf(String taskType){
		return tasksConf.get(taskType);
	}
	
	public Map<String, CrawlTaskConf> getTasksConf() {
		return tasksConf;
	}	
	
	@Override
	public void nodeConfChanged(NodeConfChangedEvent ncce) {
		reload();		
	}
	
	//must be sub-class of org.cld.datastore.entity.Product
	public Product getProductInstance(String key){
		if (key==null || "".equals(key)){
			key="product";
		}
		logger.info("prdKey to fetch:" + key);
		ProductConf prdConf = this.prdConfMap.get(key);
		if (prdConf.getPrdHandler()==null){
			logger.error(String.format("handler class for product type:%s not specified.", key));
			return null;
		}
		logger.info("prdConf:" + prdConf);
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
	
	public String getProductAnalyzeImpl() {
		return productAnalyzeImpl;
	}
	public void setProductAnalyzeImpl(String productAnalyzeImpl) {
		this.productAnalyzeImpl = productAnalyzeImpl;
	}
	public String getCategoryAnalyzeImpl() {
		return categoryAnalyzeImpl;
	}
	public void setCategoryAnalyzeImpl(String categoryAnalyzeImpl) {
		this.categoryAnalyzeImpl = categoryAnalyzeImpl;
	}
	public String getProductListAnalyzeImpl() {
		return productListAnalyzeImpl;
	}
	public void setProductListAnalyzeImpl(String productListAnalyzeImpl) {
		this.productListAnalyzeImpl = productListAnalyzeImpl;
	}
	public String getListAnalyzeImpl() {
		return listAnalyzeImpl;
	}
	public void setListAnalyzeImpl(String listAnalyzeImpl) {
		this.listAnalyzeImpl = listAnalyzeImpl;
	}
	public String getPromotionAnalyzeImpl() {
		return promotionAnalyzeImpl;
	}
	public void setPromotionAnalyzeImpl(String promotionAnalyzeImpl) {
		this.promotionAnalyzeImpl = promotionAnalyzeImpl;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getProxyIP() {
		return proxyIP;
	}
	public void setProxyIP(String proxyIP) {
		this.proxyIP = proxyIP;
	}
	public int getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	public boolean isUseProxy() {
		return useProxy;
	}
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
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
	public int getBrokenRetry() {
		return brokenRetry;
	}
	public void setBrokenRetry(int brokenRetry) {
		this.brokenRetry = brokenRetry;
	}
	
	public Map<String, ProductConf> getPrdConfMap(){
		return prdConfMap;
	}

	public SessionFactory getTaskSF() {
		return taskSF;
	}

	public void setTaskSF(SessionFactory taskSF) {
		this.taskSF = taskSF;
	}
	
	public DataStoreManager getDsm() {
		return dsm;
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

	public NodeConf getNodeConf() {
		return nodeConf;
	}

	public void setNodeConf(NodeConf nodeConf) {
		this.nodeConf = nodeConf;
	}
	
	public boolean isCancelable(){
		return nodeConf.isCancelable();
	}

	public String getCrawlDsManager() {
		return crawlDsManagerValue;
	}

	public void setCrawlDsManager(String crawlDsManager) {
		this.crawlDsManagerValue = crawlDsManager;
	}
}
