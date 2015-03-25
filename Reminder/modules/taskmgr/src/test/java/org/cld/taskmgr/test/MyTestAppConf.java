package org.cld.taskmgr.test;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.NodeConfChangedEvent;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.reload.ClassReloadFactory;

public class MyTestAppConf implements AppConf, Serializable{


	private static Logger logger = LogManager.getLogger(MyTestAppConf.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String rerun_Key="rerun";

	private boolean rerun=false;
	private TaskMgr taskMgr = new TaskMgr();
	
	@Override
	public void setup(String file, NodeConf nc) {
		try {
			PropertiesConfiguration properties = new PropertiesConfiguration(file);		
			Iterator<String> enu = properties.getKeys();
			while(enu.hasNext()){
				String key = enu.next();				
				//for all types of crawl tasks
				if (rerun_Key.equals(key)){
					setRerun(properties.getBoolean(key));
				}
			}
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
		
		taskMgr.setUp(file, nc);
		taskMgr.reload(this.getClass().getClassLoader(), null);
	}

	@Override
	public void nodeConfChanged(NodeConfChangedEvent ncce) {
		
	}
	

	public boolean isRerun() {
		return rerun;
	}
	public void setRerun(boolean rerun) {
		this.rerun = rerun;
	}
	public TaskMgr getTaskMgr(){
		return taskMgr;
	}

}
