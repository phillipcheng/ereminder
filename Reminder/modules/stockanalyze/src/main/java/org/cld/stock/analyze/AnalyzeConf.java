package org.cld.stock.analyze;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.util.FsType;
import org.cld.util.jdbc.DBConnConf;

public class AnalyzeConf extends TaskConf {
	private static Logger logger =  LogManager.getLogger(AnalyzeConf.class);
	public static final String BT_FS_TYPE="bt.fs.type";
		public static final String BT_FS_LOCAL="local";
		public static final String BT_FS_HDFS="hdfs";
	public static final String SYMBOLS_FOLDER="symbols_folder";
	
	private DBConnConf dbconf;
	private FsType btFs;
	
	public AnalyzeConf(String propertyFile){
		super(propertyFile);
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration(propertyFile);
			String fstype = pc.getString(BT_FS_TYPE, BT_FS_LOCAL);
			if (BT_FS_HDFS.equals(fstype)){
				setBtFs(FsType.hdfs);
			}else if (BT_FS_LOCAL.equals(fstype)){
				setBtFs(FsType.local);
			}else{
				logger.error("unsupported fs %s", fstype);
			}
			dbconf = new DBConnConf("analyze.", pc);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public DBConnConf getDbconf(){
		return dbconf;
	}

	@Override
	public TaskMgr getTaskMgr() {
		return null;
	}

	public FsType getBtFs() {
		return btFs;
	}

	public void setBtFs(FsType btFs) {
		this.btFs = btFs;
	}

}
