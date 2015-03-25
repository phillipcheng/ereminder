package org.cld.taskmgr;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBConf;


public class NodeConf implements Serializable, FileAlterationListener{

	private NodeConf oldConf;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(NodeConf.class);
	
	public static String nodeId_Key="node.id";
	public static String threadSize_Key="thread.num";	
	public static String isServer_Key="is.server";
	public static String serverIP_Key="server.ip";
	public static String serverPort_Key="server.port";
	public static String localIP_Key="local.ip";
	public static String localPort_Key="local.port";
	public static String rmiCodebase_Key="java.rmi.server.codebase";
	public static String taskDBConnectionUrl_Key = "task.db.connection.url";
	public static final String taskMgrFramework_Key = "task.mgr.framework";
		public static final String tmframework_old="old";
		public static final String tmframework_hadoop="hadoop";

	//
	public static String appConfImpl_Key="app.conf.impl";
	public static String appClientImpl_Key="app.client.impl";
	public static String appServerImpl_Key="app.server.impl";
	//
	public static String runRound_Key="run.round";
	//
	
	
	private String nodeId;
	private int threadSize=0;
	private boolean isServer=false;
	private String serverIP;
	private int serverPort;
	private String localIP;
	private int localPort;
	private String rmiCodebase;
	private String taskDBConnectionUrl = null;
	private String taskMgrFramework = null;
	
	private String appConfImpl = "org.cld.datacrawl.CrawlConf";
	private String appClientImpl = "org.cld.datacrawl.CrawlClientNode";
	private String appServerImpl = "org.cld.datacrawl.CrawlServerNode";
	private String appHadoopClientImpl = "org.cld.datacrawl.HadoopCrawlNode";
	private int runRound=0;
	
	public HashMap<String, String> params = new HashMap<String, String>();
	
	//list of node-conf-listeners
	private transient List<NodeConfListener> ncListeners = new ArrayList<NodeConfListener>();

	//list of node-conf-prop-listeners
	private transient List<NodeConfPropListener> ncpListeners = new ArrayList<NodeConfPropListener>();
	
	private String confFile=null;
	private transient PropertiesConfiguration masterConf;
	//
	public static int CONF_WATCH_PERIOD=4000;
	
	private String propDir;
	
	private transient TaskMgr taskMgr;
	
	private boolean cancelable = false;//can be cancelled or not
	
	public TaskMgr getTaskMgr(){
		return taskMgr;
	}
	
	public void setTaskMgr(TaskMgr taskMgr){
		this.taskMgr = taskMgr;
	}
	
	public String getPropDir(){
		return propDir;
	}
	
	public void addNodeConfListener(NodeConfListener lsn){
		ncListeners.add(lsn);
	}
	
	public void addNodeConfPropListener(NodeConfPropListener lsn){
		ncpListeners.add(lsn);
	}
	
	public void fireNCCEvent(NodeConfChangedEvent ncce){
		for (int i=0; i<ncListeners.size(); i++){
			NodeConfListener ncl = ncListeners.get(i);
			ncl.nodeConfChanged(ncce);
		}
	}
	
	public void fireNCPCEvent(NodeConfPropChangedEvent ncpce){
		logger.info("fire NCPCEvent:" + ncpce + " to " + ncpListeners.size());
		for (int i=0; i<ncpListeners.size(); i++){
			NodeConfPropListener ncl = ncpListeners.get(i);
			logger.info("ncl:" + ncl);
			ncl.nodeConfPropChanged(ncpce);
		}
	}
	
	public String toString(){
		return params.toString();
	}
	
	//TODO to complete the clone
	public NodeConf clone(){
		NodeConf nc = new NodeConf();
		nc.threadSize = this.threadSize;
		nc.nodeId = this.nodeId;
		nc.params.put(nodeId_Key, nodeId);
		nc.params.put(threadSize_Key, threadSize + "");
		return nc;
	}
	
	public NodeConf() {
		
	}
	
	private void storeOldConf(){
		if (oldConf == null){
			oldConf = new NodeConf();
		}
		oldConf.setThreadSize(this.getThreadSize());
	}
	
	private void reload(){
		//store old properties
		storeOldConf();
		
		//read new properties
		readProperties();
		
		//fire conf changed event, ask app conf to reload
		NodeConfChangedEvent ncce= new NodeConfChangedEvent();
		this.fireNCCEvent(ncce);
		
		//fire property changed event
		if (oldConf.getThreadSize() != this.getThreadSize()){
			NodeConfPropChangedEvent ncpce = new NodeConfPropChangedEvent();
			ncpce.setIntValue(this.getThreadSize());
			ncpce.setPropName(NodeConf.threadSize_Key);
			ncpce.setOpType(NodeConfPropChangedEvent.OP_UPDATE);
			fireNCPCEvent(ncpce);
		}
		
	}
	
	private void monitorConfChange(){
		String dir = masterConf.getBasePath();
		File directory = null;
		try {
			directory = new File(new URI(dir));
		} catch (URISyntaxException e1) {
			logger.error("uriexception.",e1);
		}
		propDir = directory.getParent();
		FileAlterationObserver observer = new FileAlterationObserver(directory.getParent(), 
				FileFilterUtils.nameFileFilter(this.confFile));
		logger.info("adding observer to directory:" + observer.getDirectory());
		observer.addListener(this);
		FileAlterationMonitor monitor = new FileAlterationMonitor(CONF_WATCH_PERIOD);
		monitor.addObserver(observer);
		try {
			monitor.start();
		} catch (Exception e) {
			logger.error("monitor start exception.", e);
		}
	}
	
	public NodeConf(String conf){
		//
		this.confFile = conf;
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(conf);
			masterConf = new PropertiesConfiguration(url);
		} catch (ConfigurationException e) {
			logger.error("", e);
		}

		//monitorConfChange();
		
		reload();
	}
	
	public void addStringParameter(String key, String value){
		params.put(key, value);
	}
	
	public String getStringParameter(String key){
		return params.get(key);
	}
	
	public void readProperties(){
		try{
			
			masterConf = new PropertiesConfiguration(this.confFile);
			Iterator<String> enu = masterConf.getKeys();
			while(enu.hasNext()){
				String key = enu.next();
				String value = masterConf.getString(key);
				if (nodeId_Key.equals(key)){
					setNodeId(value);
				}else if (threadSize_Key.equals(key)){
					setThreadSize(Integer.parseInt(value));
				}else if (isServer_Key.equals(key)){
					isServer = Boolean.parseBoolean(value);
					logger.info("isServer:" + isServer);
				}else if (serverIP_Key.equals(key)){
					serverIP=value;
					logger.info("serverIP:" + serverIP);
				}else if (serverPort_Key.equals(key)){
					serverPort=Integer.parseInt(value);
					logger.info("serverPort:" + serverPort);
				}else if (localIP_Key.equals(key)){
					localIP=value;
					logger.info("localIP:" + localIP);
				}else if (localPort_Key.equals(key)){
					localPort=Integer.parseInt(value);
					logger.info("localPort:" + localPort);
				}else if (rmiCodebase_Key.equals(key)){
					setRmiCodebase(value);
				}else if (taskDBConnectionUrl_Key.equals(key)){
					setTaskDBConnectionUrl(value);
				}else if (taskMgrFramework_Key.equals(key)){
					setTaskMgrFramework(value);
				}else if (appConfImpl_Key.equals(key)){
					setAppConfImpl(value);
				}else if (appClientImpl_Key.equals(key)){				
					setAppClientImpl(value);
				}else if (appServerImpl_Key.equals(key)){
					setAppServerImpl(value);
				}else if (runRound_Key.equals(key)){
					runRound=Integer.parseInt(value);
				}
				
				params.put(key, value);
			}
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
		params.put(nodeId_Key, nodeId);
	}

	public int getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
		params.put(threadSize_Key, threadSize + "");
	}
	
	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}


	public String getAppConfImpl() {
		return appConfImpl;
	}

	public void setAppConfImpl(String appConfImpl) {
		this.appConfImpl = appConfImpl;
	}

	public String getRmiCodebase() {
		return rmiCodebase;
	}

	public void setRmiCodebase(String rmiCodebase) {
		this.rmiCodebase = rmiCodebase;
	}

	public String getTaskDBConnectionUrl() {
		return taskDBConnectionUrl;
	}

	public void setTaskDBConnectionUrl(String taskDBConnectionUrl) {
		this.taskDBConnectionUrl = taskDBConnectionUrl;
	}

	public String getAppClientImpl() {
		return appClientImpl;
	}

	public void setAppClientImpl(String appClientImpl) {
		this.appClientImpl = appClientImpl;
	}

	public String getAppServerImpl() {
		return appServerImpl;
	}

	public void setAppServerImpl(String appServerImpl) {
		this.appServerImpl = appServerImpl;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getLocalIP() {
		return localIP;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	public int getRunRound() {
		return runRound;
	}
	public void setRunRound(int runRound) {
		this.runRound = runRound;
	}
	
	public DBConf getDBConf(){
		DBConf dbconf = new DBConf();		
		dbconf.setDbConnectionUrl(this.getTaskDBConnectionUrl());
		dbconf.setHibernateCfgFile("taskmgr.hibernate.cfg.xml");
		return dbconf;
	}

	@Override
	public void onStart(FileAlterationObserver observer) {
		
	}

	@Override
	public void onDirectoryCreate(File directory) {
		logger.info("onDirectoryCreate:" + directory);
	}

	@Override
	public void onDirectoryChange(File directory) {
		logger.info("onDirectoryChange:" + directory);
		
	}

	@Override
	public void onDirectoryDelete(File directory) {
		logger.info("onDirectoryDelete:" + directory);
		
	}

	@Override
	public void onFileCreate(File file) {
		logger.info("onFileCreate:" + file);
	}

	@Override
	public void onFileChange(File file) {
		logger.info("file changed..." + file);
		reload();
	}

	@Override
	public void onFileDelete(File file) {
		logger.info("onFileDelete:" + file);
		
	}

	@Override
	public void onStop(FileAlterationObserver observer) {
	}

	public boolean isCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}
	
	public String getTaskMgrFramework() {
		return taskMgrFramework;
	}

	public void setTaskMgrFramework(String taskMgrFramework) {
		this.taskMgrFramework = taskMgrFramework;
	}

	public String getAppHadoopClientImpl() {
		return appHadoopClientImpl;
	}

	public void setAppHadoopClientImpl(String appHadoopClientImpl) {
		this.appHadoopClientImpl = appHadoopClientImpl;
	}


}
