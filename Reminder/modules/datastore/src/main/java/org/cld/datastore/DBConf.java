package org.cld.datastore;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConf implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(DBConf.class);
	
	public static String dbConnectionUrl_Key="db.connection.url";
	public static String hibernateCfgFile_Key="hibernate.cfg.file";
	
	private String dbConnectionUrl;
	private String hibernateCfgFile;

	public String getDbConnectionUrl() {
		return dbConnectionUrl;
	}

	public void setDbConnectionUrl(String dbConnectionUrl) {
		this.dbConnectionUrl = dbConnectionUrl;
	}

	public String getHibernateCfgFile() {
		return hibernateCfgFile;
	}

	public void setHibernateCfgFile(String hibernateCfgFile) {
		this.hibernateCfgFile = hibernateCfgFile;
	}

	public String toString(){
		return "dbconnectionurl:" + this.dbConnectionUrl;
	}
}
