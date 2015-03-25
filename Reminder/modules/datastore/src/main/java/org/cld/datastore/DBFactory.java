package org.cld.datastore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBFactory {

	private static Logger logger =  LogManager.getLogger(DBFactory.class);
	
	//save the nodeId:moduleName to Configuration mapping
		private static Map<String, Configuration> cfgMap = 
				new ConcurrentHashMap<String, Configuration>();
	//save the nodeId:moduleName to SessionFactory mapping
	private static Map<String, SessionFactory> sfMap = 
			new ConcurrentHashMap<String, SessionFactory>();
	
	//setup normal hibernate cfg
	public static Configuration setUpCfg(String nodeId, String moduleName, DBConf dbconf) {
		logger.debug("before setup hibernate cfg:" + dbconf);
		Configuration cfg = new Configuration();
		cfg.configure(dbconf.getHibernateCfgFile());
		cfg.setProperty("hibernate.connection.url", dbconf.getDbConnectionUrl());
		cfgMap.put(nodeId + ":" + moduleName, cfg);
		return cfg;
	}
	
	//setup normal hibernate session factory with classloader
	public static SessionFactory setUpSF(ClassLoader classLoader, String nodeId, String moduleName, Configuration cfg) {
		cfgMap.put(nodeId + ":" + moduleName, cfg);
		ClassLoader oldCld = Thread.currentThread().getContextClassLoader();
		if (classLoader!=null){
			Thread.currentThread().setContextClassLoader(classLoader);
		}
		SessionFactory sessionFactory = cfg.buildSessionFactory();
		Thread.currentThread().setContextClassLoader(oldCld);
		sfMap.put(nodeId + ":" + moduleName, sessionFactory);
		return sessionFactory;
	}	
	
	//setup normal hibernate session factory without classloader
	public static SessionFactory setUpSF(String nodeId, String moduleName, Configuration cfg) {	
		cfgMap.put(nodeId + ":" + moduleName, cfg);		
		SessionFactory sessionFactory = cfg.buildSessionFactory();
		sfMap.put(nodeId + ":" + moduleName, sessionFactory);
		return sessionFactory;
	}
	
	//setup normal hibernate without classloader, do 2 steps in 1
	public static void setUp(String nodeId, String moduleName, DBConf dbconf) {
		Configuration cfg = setUpCfg(nodeId, moduleName, dbconf);
		setUpSF(nodeId, moduleName, cfg);		
	}
	
	public static SessionFactory getDBSF(String nodeid, String moduleName){
		return sfMap.get(nodeid + ":" + moduleName);
	}
	
	public static Configuration getDBCfg(String nodeid, String moduleName){
		return cfgMap.get(nodeid + ":" + moduleName);
	}
}
