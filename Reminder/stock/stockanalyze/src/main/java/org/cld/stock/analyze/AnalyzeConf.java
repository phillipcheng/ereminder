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
	public static final String BT_MODE_KEY="bt.mode";//task manager
		public static final String BT_HADOOP = "hadoop";
		public static final String BT_LOCAL = "local";
	public static final String BT_FS_TYPE="bt.fs.type";
		public static final String BT_FS_LOCAL="local";
		public static final String BT_FS_HDFS="hdfs";
	public static final String BT_DATA_FOLDER="bt.data.folder";
	public static final String BT_OUTPUT="bt.output.folder";
	
	private DBConnConf dbconf;
	private String btMode; //back testing mode
	private FsType btFs; //back testing filesytem type
	private String btDataFolder;//back testing data folder
	private String btOutputFolder;
	
	public AnalyzeConf(String propertyFile){
		super(propertyFile);
		try{
			PropertiesConfiguration pc = new PropertiesConfiguration(propertyFile);
			setBtMode(pc.getString(BT_MODE_KEY, BT_HADOOP));
			String fstype = pc.getString(BT_FS_TYPE, BT_FS_LOCAL);
			if (BT_FS_HDFS.equals(fstype)){
				setBtFs(FsType.hdfs);
			}else if (BT_FS_LOCAL.equals(fstype)){
				setBtFs(FsType.local);
			}else{
				logger.error("unsupported fs %s", fstype);
			}
			setBtDataFolder(pc.getString(BT_DATA_FOLDER));
			this.btOutputFolder = pc.getString(BT_OUTPUT);
			dbconf = new DBConnConf("analyze.", pc);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public String toString(){
		return String.format("analyze-conf: dbconf:%s, filesystem:%s", dbconf, btFs);
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

	public String getBtDataFolder() {
		return btDataFolder;
	}

	public void setBtDataFolder(String btDataFolder) {
		this.btDataFolder = btDataFolder;
	}

	public String getBtMode() {
		return btMode;
	}

	public void setBtMode(String btMode) {
		this.btMode = btMode;
	}

	public String getBtOutputFolder() {
		return btOutputFolder;
	}

	public void setBtOutputFolder(String btOutputFolder) {
		this.btOutputFolder = btOutputFolder;
	}

}
