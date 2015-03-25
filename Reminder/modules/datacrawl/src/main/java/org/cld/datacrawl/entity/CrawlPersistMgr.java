package org.cld.datacrawl.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBException;
import org.cld.taskmgr.entity.BrokenPage;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.datastore.entity.Category;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;



public class CrawlPersistMgr {
	private static Logger logger = LogManager.getLogger(CrawlPersistMgr.class);
//	public static final int TITLE_MAX_LENGTH=400;

//	/////// category
//	/**
//	 * 
//	 * @return null for non-existing product 
//	 * @throws db operation error
//	 */
//	public static List<Category> getCatList(SessionFactory sessionFactory) throws DBException {
//		Session session = sessionFactory.openSession();
//		
//		try {
//			Query query = session.getNamedQuery("getAllCat");
//			List<Category> plist = query.list();
//			return plist;
//		}catch(Throwable t){
//			throw new DBException(t);
//		}finally{
//			session.close();
//		}
//	}
//	
//	public static Category getCategory(SessionFactory sessionFactory, String id) throws DBException {
//		Session session = sessionFactory.openSession();
//		
//		try {
//			Query query = session.getNamedQuery("getCatByID").setString("ID", id);			
//			query.setMaxResults(1);
//			Category c = (Category)query.uniqueResult();
//			return c;
//		}catch(Throwable t){
//			throw new DBException(t);
//		}finally{
//			session.close();
//		}
//	}
//	
//	public static List<Category>  getSubCategory(SessionFactory sessionFactory, String parentId) throws DBException {
//		Session session = sessionFactory.openSession();
//		
//		try {
//			Query query = session.getNamedQuery("getSubCatByParentID").setString("parentID", parentId);
//			List<Category> plist = query.list();
//			return plist;
//		}catch(Throwable t){
//			throw new DBException(t);
//		}finally{
//			session.close();
//		}
//	}
//	
//	public static void removeCats(SessionFactory sessionFactory, Collection<String> catIds){
//		Session session = sessionFactory.openSession();
//		Transaction tr = session.beginTransaction();
//		try {
//			tr.begin();	
//			Query query = session.getNamedQuery("removeCats").setParameterList("catIdList", catIds);
//			int i = query.executeUpdate();
//			logger.info(i + " category deleted.");
//			tr.commit();
//		}catch(Throwable t){
//			logger.error("throwable caught while removeCats for:" + catIds, t);
//			tr.rollback();
//		}finally{
//			session.close();
//		}
//	}
//	
//	/**
//	 * 
//	 * @param sessionFactory
//	 * @param cat
//	 * @return last category stored if exist
//	 */
//	public static Category addOrUpdateCat(SessionFactory sessionFactory, Category cat) {
//		Session session = sessionFactory.openSession();
//		Transaction tr = session.beginTransaction();
//		try {
//			Category retCat = null;
//			tr.begin();	
//			cat.setUpdateTime(new Date());
//			
//			Query query = session.getNamedQuery("getCatByID").setString("ID", cat.getCatId());			
//			query.setMaxResults(1);
//			Category oldCat = (Category)query.uniqueResult();
//			if (oldCat!=null){
//				//do not update itemNum if it is 0, since some site has item num in parent cat but does not in leaf
//				oldCat.setUpdateTime(new Date());
//				oldCat.setLeaf(cat.getLeaf());
//				oldCat.setPageSize(cat.getPageSize());
//				oldCat.setPagelimit(cat.getPagelimit());
//				if (cat.getItemNum()>0){
//					oldCat.setItemNum(cat.getItemNum());
//				}else{
//					cat.setItemNum(oldCat.getItemNum());
//				}
//				session.update(oldCat);
//				retCat = oldCat;
//			}else{
//				session.save(cat); 
//			}
//			tr.commit();
//			return retCat;
//		}catch(Throwable t){
//			logger.error("throwable caught while addOrUpdateBrowseStat for:" + cat, t);
//			tr.rollback();
//			return null;
//		}finally{
//			session.close();
//		}
//	}
	
//	/// for promotion
//	/**
//	 * 
//	 * @param id
//	 * @return null for non-existing product 
//	 * @throws db operation error
//	 */
//	public static Promotion getPromotion(SessionFactory sessionFactory, String id) throws DBException {
//		Session session = sessionFactory.openSession();
//		
//		try {
//			Query query = session.getNamedQuery("getPromotionByID").setString("ID", id);			
//			query.setMaxResults(1);
//			Promotion p = (Promotion)query.uniqueResult();
//			return p;
//		}catch(Throwable t){
//			throw new DBException(t);
//		}finally{
//			session.close();
//		}
//	}
//	
//	public static boolean addPromotion(SessionFactory sessionFactory, Promotion p) {
//		Session session = sessionFactory.openSession();
//		Transaction tr = session.beginTransaction();
//		try {
//			tr.begin();
//			Query query = session.getNamedQuery("getPromotionByID").setString("ID", p.getId());			
//			query.setMaxResults(1);
//			Promotion lastP = (Promotion)query.uniqueResult();
//			if (lastP == null){
//				session.save(p);
//			}			
//			tr.commit();
//			return true;
//		}catch(Throwable t){
//			logger.warn("throwable caught while addpromotion for:" +  p.getId(), t);
//			tr.rollback();
//			return false;
//		}finally{
//			session.close();
//		}
//		
//	}
//	
//	////// for product
//	/**
//	 * @param internalId
//	 * @return null for non-existing product
//	 * @throws db operation exception
//	 */
//	public static Product getProduct(SessionFactory sessionFactory, String internalId) throws DBException {
//		Session session = sessionFactory.openSession();
//		
//		try {
//			Query query = session.getNamedQuery("getProductByInternalId").setString("InternalId", internalId);			
//			query.setMaxResults(1);
//			Product prd = (Product)query.uniqueResult();
//			return prd;
//		}catch(Throwable t){
//			throw new DBException(t);
//		}finally{
//			session.close();
//		}
//	}
//	
//	//add product and price at the same time for 1st time, adding final check
//	public static boolean addProductAndPrice(SessionFactory sessionFactory, Product product, Price price) {
//		product.setCreateDateTime(new Date(System.currentTimeMillis()));
//		Session session = sessionFactory.openSession();
//		Transaction tr = session.beginTransaction();
//		try {
//			tr.begin();
//			Query query = session.getNamedQuery("getProductByInternalId").setString("InternalId", product.getInternalId());			
//			query.setMaxResults(1);
//			Product lastPrd = (Product)query.uniqueResult();
//			if (lastPrd == null){
//				if (product.getTitle().length()>TITLE_MAX_LENGTH){
//					product.setTitle(product.getTitle().substring(0, TITLE_MAX_LENGTH));
//				}
//				session.save(product);
//			}
//			
//			query = session.getNamedQuery("getLatestProductPriceByInternalId").setString("InternalId", price.getPid().getItemId());
//			query.setMaxResults(1);
//			Price lastPrice = (Price) query.uniqueResult();
//			if (lastPrice == null){
//				session.save(price);
//			}
//			tr.commit();
//			return true;
//		}catch(Throwable t){
//			logger.error("throwable caught while addProduct for:" +  product, t);
//			tr.rollback();
//			return false;
//		}finally{
//			session.close();
//		}		
//	}
//	
//	/**
//	 * @param product
//	 * @return true for success, false for db error
//	 */
//	public static boolean updateProduct(SessionFactory sessionFactory, Product product) {
//		Session session = sessionFactory.openSession();
//		Transaction tr = session.beginTransaction();
//		try {			
//			tr.begin();
//			session.update(product);
//			tr.commit();
//			return true;
//		}catch(Throwable t){
//			logger.warn("throwable caught while addproduct for:" +  product, t);
//			tr.rollback();
//			return false;
//		}finally{			
//			session.close();
//		}		
//	}
//	
//	/////// for product price, adding final check
//	public static boolean addProductPrice(SessionFactory sessionFactory, Price p) {
//		Session session = sessionFactory.openSession();
//		Transaction tr = session.beginTransaction();
//		try {
//			tr.begin();
//			Query query = session.getNamedQuery("getLatestProductPriceByInternalId").setString("InternalId", p.getPid().getItemId());
//			query.setMaxResults(1);
//			Price lastP = (Price) query.uniqueResult();
//			if (lastP==null || lastP.reallyChanged(p)){
//				//1st price or changed price, save it
//				session.save(p);
//			}
//			tr.commit();
//			return true;
//		}catch(Throwable t){
//			logger.warn("throwable caught while addproductprice for:" +  p, t);
//			tr.rollback();
//			return false;
//		}finally{
//			session.close();
//		}
//	}
//	
//	/**
//	 * 
//	 * @param internalId
//	 * @return null for none-existing price 
//	 * @throws db operation error
//	 */
//	public static Price getLastestPrice(SessionFactory sessionFactory, String internalId) throws DBException {
//		Session session = sessionFactory.openSession();
//		
//		try {
//			Query query = session.getNamedQuery("getLatestProductPriceByInternalId").setString("InternalId", internalId);
//			query.setMaxResults(1);
//			Price p = (Price) query.uniqueResult();					
//			return p;
//		}catch(Throwable t){
//			logger.warn("throwable caught while getlastest price for:" +  internalId, t);
//			throw new DBException(t);
//		}finally{
//			session.close();
//		}
//	}	
}
