package org.cld.taskmgr;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBConf;


public class NodeConf implements Serializable, FileAlterationListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(NodeConf.class);
	
	public static String taskDBConnectionUrl_Key = "task.db.connection.url";
	public static final String taskMgrFramework_Key = "task.mgr.framework";
		public static final String tmframework_hadoop="hadoop";
	public static String appConfImpl_Key="app.conf.impl";
	
	private String taskDBConnectionUrl = null;
	private String taskMgrFramework = null;	
	private String appConfImpl = "org.cld.datacrawl.CrawlConf";
	private String appHadoopClientImpl = "org.cld.datacrawl.HadoopCrawlNode";
	public HashMap<String, String> params = new HashMap<String, String>();
	
	private String confFile=null;
	private transient PropertiesConfiguration masterConf;
	//
	
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
	
	public String toString(){
		return params.toString();
	}
	
	public NodeConf() {
		
	}
	
	private void reload(){
		
		//read new properties
		readProperties();
		
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
				if (taskDBConnectionUrl_Key.equals(key)){
					setTaskDBConnectionUrl(value);
				}else if (taskMgrFramework_Key.equals(key)){
					setTaskMgrFramework(value);
				}else if (appConfImpl_Key.equals(key)){
					setAppConfImpl(value);
				}
				
				params.put(key, value);
			}
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}

	public String getAppConfImpl() {
		return appConfImpl;
	}

	public void setAppConfImpl(String appConfImpl) {
		this.appConfImpl = appConfImpl;
	}

	public String getTaskDBConnectionUrl() {
		return taskDBConnectionUrl;
	}

	public void setTaskDBConnectionUrl(String taskDBConnectionUrl) {
		this.taskDBConnectionUrl = taskDBConnectionUrl;
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
