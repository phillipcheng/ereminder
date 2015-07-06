package org.cld.datastore.impl;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.LogPattern;
import org.cld.datastore.entity.Logs;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.datastore.entity.SiteConf;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.ParamType;

//store csv file in hdfs
public class HdfsDataStoreManagerImpl implements DataStoreManager {

	private static Logger logger = LogManager.getLogger(HdfsDataStoreManagerImpl.class);

	private Configuration hadoopConfig;
	private String rootDir;
	
	public HdfsDataStoreManagerImpl(Configuration hadoopConfig, String rootDir) {
		this.hadoopConfig = hadoopConfig;
		this.rootDir = rootDir;
	}
	
	@Override
	public CrawledItem getCrawledItem(String id, String dataSourceId,
			Class<? extends CrawledItem> crawledItemClazz) {
		return null;
	}


	@Override
	public boolean addCrawledItem(CrawledItem ci, CrawledItem oldCi, BrowseTaskType btt) {
		//TODO pre-cache this
		List<String> outParamList = new ArrayList<String>();
		for (ParamType pt:btt.getParam()){
			if ("out".equals(pt.getDirection())){
				outParamList.add(pt.getName());
			}
		}
		String fileName = ci.getId().getId(); //as file name
		fileName = fileName.replaceAll("[^a-zA-Z0-9]", "_");
		String outF = rootDir + "/" + ci.getId().getStoreId() + "/" + fileName;
		Path op = new Path(outF);
		BufferedWriter osw = null;
		try {
			FileSystem fs = FileSystem.get(hadoopConfig);
			osw = new BufferedWriter(new OutputStreamWriter(fs.create(op,true), "GBK"));
			int i=0;
			int size=0;
			List<String> nonListKeys = new ArrayList<String>();
			for (String key:ci.getParamMap().keySet()){
				Object value = ci.getParam(key);
				if (value instanceof List){
					if (btt.isDsmHeader()){
						if (i>0){
							osw.write(",");
						}
						osw.write(key);
					}
					if (size==0){//take 1st list's size
						size = ((List)value).size();
					}
					i++;
				}else if (outParamList.contains(key)){
					if (btt.isDsmHeader()){
						if (i>0){
							osw.write(",");
						}
						osw.write(key);
					}
					i++;
					nonListKeys.add(key);
				}else{
					nonListKeys.add(key);
					logger.warn("all param type should be list. skip:" + key + ":" + value);
				}
			}
			logger.info("size:" + size);
			if (btt.isDsmHeader()){
				osw.write("\n");
			}
			for (i=0; i<size; i++){
				StringBuffer sb = new StringBuffer();
				int j=0;
				for (String key:ci.getParamMap().keySet()){
					if (!nonListKeys.contains(key)){
						List value = (List) ci.getParam(key);
						if (j>0){
							sb.append(",");
						}
						String v = (String) value.get(i);
						sb.append(v.trim());
						j++;
					}else if (outParamList.contains(key)){
						String v = (String) ci.getParam(key);
						if (j>0){
							sb.append(",");
						}
						sb.append(v);
						j++;
					}
				}
				sb.append("\n");
				osw.write(sb.toString());
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (osw!=null){
				try{
					osw.close();
				}catch(Exception e){
					logger.error("", e);
				}
			}
		}
		return true;
	}


	@Override
	public boolean addPrice(Price price) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Price getLatestPrice(String id, String storeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int delProductAndPriceByStoreId(String storeId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<Product> getProductByPcatId(String storeId, String pcatId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Product> getProductByRootTaskId(String rootTaskId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long getProductCount(String storeId, String pcatId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<Category> getCategoryByRootTaskId(String rootTaskId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Category> getCategoryByPcatId(String storeId, String pcatId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int delCategoryByStoreId(String storeId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getCategoryCount(String storeId, String pcatId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<SiteConf> getSiteConf(String uid) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<SiteConf> getSiteConf(String uid, boolean withXml, int status) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SiteConf getFullSitConf(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean saveXmlConf(String id, String uid, String xml) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int deployConf(String[] ids, boolean deploy) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<Logs> getLogsByTask(String taskId, int offset, int limit) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long getLogsCountByTask(String taskId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int clearLogs(String taskid) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<Logs> getLogsByResolvedPattern(String patternId,
			String orderByField, boolean asc, int offset, int limit) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long getLogsCountByResolvedPattern(String patternId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<Logs> getLogsNotResolvedByPattern(String regexp,
			String orderByField, boolean asc, int maxCount) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long resolveLog(Long[] logIds, String patternId) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<Logs> getLogsByString(String searchString, String orderByField,
			boolean asc, int offset, int limit) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public long getLogsCountByString(String searchString) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<LogPattern> getAllLogPattern() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void mergeLogPattern(LogPattern lp) {
		// TODO Auto-generated method stub		
	}

	@Override
	public LogPattern getLogPattern(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
