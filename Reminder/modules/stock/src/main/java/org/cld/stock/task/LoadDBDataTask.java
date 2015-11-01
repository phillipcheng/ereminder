package org.cld.stock.task;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.util.jdbc.DBConnConf;

class MyPathFilter implements IOFileFilter {
	private static Logger logger =  LogManager.getLogger(MyPathFilter.class);
	private String[] skipFolders;
	private String[] includeFolders;
	
	//check order include, exclude
	public MyPathFilter(String[] includeFolders, String[] skipFolders){
		this.includeFolders = includeFolders;
		this.skipFolders = skipFolders;
	}

	public boolean accept(String fileName) {
		try{
			if (includeFolders.length>0){
				boolean included = false;
				for (String includeFolder:includeFolders){
					if (fileName.contains(includeFolder)){
						included = true;
						break;
					}
				}
				if (!included)
					return false;
			}
			for (String skipFolder:skipFolders){
				if (fileName.contains(skipFolder)){
					return false;
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return true;
	}

	@Override
	public boolean accept(File file) {
		return accept(file.toString());
	}

	@Override
	public boolean accept(File dir, String name) {
		String fileName = dir.toString() + File.separator + name;
		return accept(fileName);
	}
}

class LoadTask implements Runnable {
	private static Logger logger =  LogManager.getLogger(LoadTask.class);
	
	private File inFile;
	private StockConfig sc;
	private String marketId;
	private DBConnConf dbconf;
	
	public LoadTask(File f, String baseMarketId, String marketId, DBConnConf dbconf){
		this.inFile = f;
		this.sc = StockUtil.getStockConfig(baseMarketId);
		this.marketId = marketId;
		this.dbconf = dbconf;
	}
	
	@Override
	public void run() {
		try{
			String[] cmds = sc.getAllCmds(marketId);
			String belongCmd = null;
			for (String cmd:cmds){
				if (inFile.toString().contains(cmd)){
					if (belongCmd!=null){
						if (cmd.length()>belongCmd.length()){//keep the longed matched
							belongCmd = cmd;
						}
					}else{
						belongCmd = cmd;
					}
				}
			}
			
			if (belongCmd!=null){
				String tName=null;
				Map<String, String> tableNames = sc.getTablesByCmd(belongCmd);
				if (tableNames!=null){
					if (tableNames.size()>1){
						for (String tableName:tableNames.keySet()){
							String prefix = tableNames.get(tableName);
							if (inFile.toString().contains(prefix)){
								tName = tableName;
								break;
							}
						}
					}else{
						tName = tableNames.keySet().iterator().next();
					}
					if (tName==null){
						logger.error(String.format("can't find tableName for file %s on cmd:%s with tableNames map:%s", inFile.toString(), belongCmd, tableNames));
					}else{
						logger.info(String.format("start to load file:%s into table:%s", inFile.toString(), tName));
						StockPersistMgr.loadData(dbconf, inFile.toString(), tName);
					}
				}else{
					logger.error(String.format("table not found for cmd:%s", belongCmd));
				}
			}else{
				logger.error(String.format("can't find a cmd this file:%s belongs to.", inFile.toString()));
			}
		}catch(Throwable t){
			logger.error("", t);
		}
	}
}

public class LoadDBDataTask {
	private static Logger logger =  LogManager.getLogger(LoadDBDataTask.class);
	
	public static final String KEY_THREAD_NUM="thread";
	public static final String KEY_ROOT_DIR="dir";
	public static final String KEY_INCLUDE="include";
	public static final String KEY_EXCLUDE="exclude";
	
	public static void launch(String baseMarketId, String marketId, DBConnConf dbconf,
			int threadNum, String localDirRoot, String[] includeFolderNames, String skipFolderNames[]){
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		try{
			File localRoot = new File(localDirRoot);
			MyPathFilter mpf = new MyPathFilter(includeFolderNames, skipFolderNames);
			Collection<File> files = FileUtils.listFiles(localRoot, mpf, mpf);
			Iterator<File> it = files.iterator();
			while(it.hasNext()){
				File f = it.next();
				LoadTask t = new LoadTask(f, baseMarketId, marketId, dbconf);
				executor.submit(t);
			}
			executor.shutdown();
			executor.awaitTermination(2, TimeUnit.HOURS);
		}catch(Exception e){
			logger.error("", e);
		}
	}

}
