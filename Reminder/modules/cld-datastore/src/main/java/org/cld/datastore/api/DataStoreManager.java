package org.cld.datastore.api;

import java.util.List;

import org.cld.util.entity.Category;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.LogPattern;
import org.cld.util.entity.Logs;
import org.cld.util.entity.Price;
import org.cld.util.entity.Product;
import org.cld.util.entity.SiteConf;

public interface DataStoreManager {
	
	public static final int TITLE_MAX_LENGTH=400;
	
	/****************
	 * CrawledItem operations
	 */
	/**
	 * @return the latest version
	 */
	public CrawledItem getCrawledItem(String id, String storeId, Class<? extends CrawledItem> crawledItemClazz);
	
	/**
	 * if content different, will add a new version
	 * @param ci
	 * @param oldCi
	 * @return true, new version added
	 */
	public boolean addUpdateCrawledItem(CrawledItem ci, CrawledItem oldCi);	

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
}
