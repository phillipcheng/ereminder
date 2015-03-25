package org.cld.datastore.impl;

import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.entity.LogPattern;
import org.cld.datastore.entity.Logs;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.datastore.entity.SiteConf;

public class HbaseDataStoreManagerImpl implements DataStoreManager {

	public static final String rowkey_sep = "|";
	public static final String CRAWLEDITEM_TABLE_NAME="crawledItem";
	public static final String CRAWLEDITEM_CF="cf";
	public static final byte[] CRAWLEDITEM_CF_BYTES=CRAWLEDITEM_CF.getBytes();
	public static final String CRAWLEDITEM_DATA="data";
	public static final byte[] CRAWLEDITEM_DATA_BYTES=CRAWLEDITEM_DATA.getBytes();
	
	
	private static Logger logger = LogManager.getLogger(HbaseDataStoreManagerImpl.class);

	private Configuration hbaseConf;
	
	
	public HbaseDataStoreManagerImpl() {
		hbaseConf = HBaseConfiguration.create();
	}

	public static String getRowKey(CrawledItemId cid){
		return cid.getId() + rowkey_sep + cid.getStoreId();
	}
	
	public static String[] fromRowKey(String rowKey){
		int idx = rowKey.indexOf(rowkey_sep);
		if (idx>=0){
			String[] res = new String[2];
			res[0]=rowKey.substring(0,idx);
			res[1]=rowKey.substring(idx+1, rowKey.length());
			return res;
		}else{
			logger.error(String.format("rowkey_sep not found in the rowkey: %s", rowKey));
			return null;
		}
	}
	
	public static String getRowKey(String id, String dataSourceId){
		return id + "|" + dataSourceId;
	}
	
	public static CrawledItem getCrawledItemFromResult(String rowKey, Result rs){
		String[] keys = fromRowKey(rowKey);
		if (keys!=null){
			return getCrawledItemFromResult(keys[0], keys[1], rs);
		}else{
			return null;
		}
	}
	
	public static CrawledItem getCrawledItemFromResult(String id, String dataSourceId, Result rs){
		if (!rs.isEmpty()){
        	Cell cell = rs.getColumnLatestCell(CRAWLEDITEM_CF_BYTES, CRAWLEDITEM_DATA_BYTES);
        	String json = new String(CellUtil.cloneValue(cell));
        	CrawledItem ci = CrawledItem.fromJson(json);
        	CrawledItemId ciid = new CrawledItemId(id, dataSourceId, new Date(cell.getTimestamp()));
        	ci.setId(ciid);
            return ci;
        }else{
        	return null;
        }
	}
	
	@Override
	public CrawledItem getCrawledItem(String id, String dataSourceId,
			Class<? extends CrawledItem> crawledItemClazz) {
		HTable table = null;
		try{
			table = new HTable(hbaseConf, CRAWLEDITEM_TABLE_NAME);
			String rowKey = getRowKey(id, dataSourceId);
	        Get get = new Get(rowKey.getBytes());
	        Result rs = table.get(get);
	        return getCrawledItemFromResult(id, dataSourceId, rs);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			if (table!=null){
				try{
					table.close();
				}catch(Exception e){
					logger.error("error close table.", e);
				}
			}
		}
	}


	@Override
	public boolean addCrawledItem(CrawledItem ci, CrawledItem oldCi) {
		ci.toParamData();
		if (oldCi!=null)
			oldCi.toParamData();
		if (ci.contentEquals(oldCi)){
			return false;
		}
		
        HTable table = null;
		try {
			table = new HTable(hbaseConf, CRAWLEDITEM_TABLE_NAME);
			String rowKey = getRowKey(ci.getId());
            Put put = new Put(Bytes.toBytes(rowKey));
            put.add(Bytes.toBytes(CRAWLEDITEM_CF), Bytes.toBytes(CRAWLEDITEM_DATA), Bytes
                    .toBytes(ci.toJson(false)));
            table.put(put);
            return true;
        }catch (Exception e){
        	logger.error("", e);
        	return false;
        }finally{
        	if (table!=null){
        		try{
        			table.close();
        		}catch(Exception e){
        			logger.error("error at close table.", e);
        		}
        	}
        }
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
