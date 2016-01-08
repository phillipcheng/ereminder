package org.cld.stocksheet;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DBConnConf;

public class SheetMgr {

	private static Logger logger =  LogManager.getLogger(SheetMgr.class);
	public static final String SHEET_CONF="sheet.properties";
	
	private DBConnConf dbconf;
	
	public SheetMgr(){
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration(SHEET_CONF);
			setDbconf(new DBConnConf("dm.", pc));
		}catch(Exception e){
			logger.error("", e);
		}
	}

	public DBConnConf getDbconf() {
		return dbconf;
	}

	public void setDbconf(DBConnConf dbconf) {
		this.dbconf = dbconf;
	}
	
	

}
