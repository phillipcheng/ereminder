package org.cld.datastore.api;

import java.util.List;

import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.entity.LogPattern;
import org.cld.datastore.entity.Logs;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.datastore.entity.SiteConf;
import org.hibernate.SessionFactory;

public interface DataStoreManager {
	
	public static final int TITLE_MAX_LENGTH=400;
	
	/****************
	 * CrawledItem operations
	 */
	public CrawledItem getCrawledItem(String id, String dataSourceId, Class<? extends CrawledItem> crawledItemClazz);
	public boolean addCrawledItem(CrawledItem ci, CrawledItem oldCi);	

	/***************************
	 *  Price operations
	 */
	public boolean addPrice(Price price);
	public Price getLatestPrice(String id, String storeId);
	
	/************************************
	 * Management/Web Console operations
	 */
	//product
	public int delProductAndPriceByStoreId(String storeId);
	public List<Product> getProductByPcatId(String storeId, String pcatId);
	public List<Product> getProductByRootTaskId(String rootTaskId);
	public long getProductCount(String storeId, String pcatId);
	//category
	public List<Category> getCategoryByRootTaskId(String rootTaskId);
	public List<Category> getCategoryByPcatId(String storeId, String pcatId);	//pcatId = null, means get root category
	public int delCategoryByStoreId(String storeId);
	public long getCategoryCount(String storeId, String pcatId);


	/**************
	 * siteconf operation
	 */
	public List<SiteConf> getSiteConf(String uid);//default to withXml = false, status = -1 (any status)
	/**
	 * 
	 * @param uid
	 * @param withXml
	 * @param status = -1 means all status
	 * @return
	 */
	public List<SiteConf> getSiteConf(String uid, boolean withXml, int status);
	public SiteConf getFullSitConf(String id);
	public boolean saveXmlConf(String id, String uid, String xml);
	public int deployConf(String[] ids, boolean deploy);//deploy: true to deploy, false to undeploy
	
	/***************
	 * Logs operation
	 */
	public List<Logs> getLogsByTask(String taskId, int offset, int limit);
	public long getLogsCountByTask(String taskId);
	public int clearLogs(String taskid);
	
	/*
	 * @param patternId: null, no patternId or a specific id
	 * @param orderByField: dated, patternId
	 * @param offset and limit
	 */
	public List<Logs> getLogsByResolvedPattern(String patternId, String orderByField, boolean asc, int offset, int limit);
	public long getLogsCountByResolvedPattern(String patternId);
	
	//return the logs fitting the lp and not yet resolved as this lp
	public List<Logs> getLogsNotResolvedByPattern(String regexp, String orderByField, boolean asc, int maxCount);
	public long resolveLog(Long[] logIds, String patternId);
	
	public List<Logs> getLogsByString(String searchString, String orderByField, boolean asc, int offset, int limit);
	public long getLogsCountByString(String searchString);
	
	/***************
	 * Log Pattern operation
	 */
	public List<LogPattern> getAllLogPattern();
	public void mergeLogPattern(LogPattern lp);
	public LogPattern getLogPattern(String id);
}
