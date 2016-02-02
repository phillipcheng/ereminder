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
import org.cld.util.entity.Category;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.LogPattern;
import org.cld.util.entity.Logs;
import org.cld.util.entity.Price;
import org.cld.util.entity.Product;
import org.cld.util.entity.SiteConf;
import org.w3c.dom.Node;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.ParamType;

//store csv file in hdfs
public class HdfsDataStoreManagerImpl implements DataStoreManager{

	private static Logger logger = LogManager.getLogger(HdfsDataStoreManagerImpl.class);

	private Configuration hadoopConfig;
	private String rootDir;//:/reminder/items
	
	public HdfsDataStoreManagerImpl(Configuration hadoopConfig, String rootDir) {
		this.hadoopConfig = hadoopConfig;
		this.rootDir = rootDir;
	}
	
	@Override
	public CrawledItem getCrawledItem(String id, String dataSourceId,
			Class<? extends CrawledItem> crawledItemClazz) {
		return null;
	}

	//for all list typed attributes of ci
	public static String[][] getCSV(CrawledItem ci, BrowseTaskType btt) {
		String id = ci.getId().getId();
		
		List<String> outParamList = new ArrayList<String>();
		for (ParamType pt:btt.getParam()){
			if ("out".equals(pt.getDirection())){
				outParamList.add(pt.getName());
			}
		}
		
		int i=0;
		int size=0;
		List<String> nonListKeys = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (String key:ci.getParamMap().keySet()){
			Object value = ci.getParam(key);
			if (value instanceof List){
				List vl = (List)value;
				if (vl.size()>0){
					Object v = vl.get(0);
					if (v instanceof Node){
						continue;
					}
				}
				if (btt.isDsmHeader()){
					if (i>0){
						sb.append(",");
					}
					sb.append(key);
				}
				if (size==0){//take 1st list's size
					size = ((List)value).size();
				}
				i++;
			}else if (outParamList.contains(key)){
				if (btt.isDsmHeader()){
					if (i>0){
						sb.append(",");
					}
					sb.append(key);
				}
				i++;
				nonListKeys.add(key);
			}else{
				nonListKeys.add(key);
				logger.warn("all param type should be list. skip:" + key + ":" + value);
			}
		}
		logger.info("size:" + size);
		List<String[]> csvList = new ArrayList<String[]>();
		if (btt.isDsmHeader()){
			csvList.add(new String[]{id, sb.toString()});
		}
		for (i=0; i<size; i++){
			sb = new StringBuffer();
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
			csvList.add(new String[]{id, sb.toString()});
		}
		String[][] ret = new String[csvList.size()][];
		for (i=0; i<csvList.size();i++){
			ret[i] = csvList.get(i);
		}
		return ret;
	}
	
	public boolean addCrawledItem(CrawledItem ci, CrawledItem oldCi, String fileName){
		Path op = new Path(rootDir + "/" + fileName);
		BufferedWriter osw = null;
		try {
			FileSystem fs = FileSystem.get(hadoopConfig);
			osw = new BufferedWriter(new OutputStreamWriter(fs.create(op,true), "gb2312"));
			if (ci.getCsvValue()!=null){
				for (String[] v:ci.getCsvValue()){
					if (v[1]!=null){
						osw.write(v[1]);//write out value only
						osw.write("\n");
					}
				}
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
	public boolean addUpdateCrawledItem(CrawledItem ci, CrawledItem oldCi) {
		String fileName = ci.getId().getId(); //as file name
		fileName = fileName.replaceAll("[^a-zA-Z0-9]", "_");
		String outF = ci.getId().getStoreId() + "/" + fileName;
		return addCrawledItem(ci, oldCi, outF);
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
	
}
