package org.cld.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesUtil {
	public static Logger logger = LogManager.getLogger(PropertiesUtil.class);
	
	public static PropertiesConfiguration getPC(String conf){
		PropertiesConfiguration pc = null;
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(conf);
			pc = new PropertiesConfiguration(url);
		} catch (ConfigurationException e) {
			File f = new File(conf);
			try {
				pc = new PropertiesConfiguration(f);
			}catch(Exception e1){
				logger.error("", e1);
			}
		}
		return pc;
	}
}
