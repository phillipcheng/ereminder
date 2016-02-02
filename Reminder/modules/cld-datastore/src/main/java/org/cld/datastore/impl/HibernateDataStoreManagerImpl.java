package org.cld.datastore.impl;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreManager;
import org.cld.util.entity.Category;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.Price;
import org.cld.util.entity.Product;

public class HibernateDataStoreManagerImpl implements DataStoreManager {

	private static Logger logger = LogManager.getLogger(DataStoreManager.class);

	private SessionFactory hsf;

	public SessionFactory getHibernateSF(){
		return hsf;
	}
	public void setHibernateSF(SessionFactory sf){
		hsf = sf;
	}
	
	public HibernateDataStoreManagerImpl() {
	}
	
	/******************************
	 * CrawledItem Operations
	 */
	@Override
	public CrawledItem getCrawledItem(String id, String storeId, Class<? extends CrawledItem> crawledItemClazz) {
		Session session = hsf.openSession();
		try {
			List<CrawledItem> results = session.createCriteria(crawledItemClazz)
				.add(Restrictions.eq("id.id", id))
				.add(Restrictions.eq("id.storeId", storeId))
				.addOrder(Property.forName("id.createTime").desc())
				.setMaxResults(1)
				.list();
			CrawledItem p = null;
			if (results != null && !results.isEmpty()) {
				p = (CrawledItem) results.get(0);
			}
			return p;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}finally{
			session.close();
		}
	}

	@Override
	public boolean addUpdateCrawledItem(CrawledItem ci, CrawledItem oldCi) {
		//compare with the oldCi
		if (ci.contentEquals(oldCi))
			return false;
		//append the new one
		Session session = hsf.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(ci);
			tx.commit();
			return true;
		} catch (Exception e) {
			try{
				tx.rollback();
			}catch(Throwable t1){
				logger.error("throwable caught while rollback transaction for:" + ci, t1);
			}
			logger.error("", e);
			return false;
		} finally {
			session.close();
		}
	}
	
	/******************************
	 * Category Operations
	 */
	
	@Override
	public List<Category> getCategoryByRootTaskId(String rootTaskId) {
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Category.class)
					.add(Restrictions.eq("rootTaskId", rootTaskId));
			List<Category> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	@Override
	public List<Category> getCategoryByPcatId(String storeId, String pcatId){
		Session session = hsf.openSession();
		try{
			Criteria cr = null;
			if (pcatId!=null){
				cr = session.createCriteria(Category.class)
						.add(Restrictions.conjunction().
								add(Restrictions.eq("id.storeId", storeId)).
								add(Restrictions.eq("parentCatId", pcatId)));
			}else{
				cr = session.createCriteria(Category.class)
						.add(Restrictions.conjunction().
								add(Restrictions.eq("id.storeId", storeId)).
								add(Restrictions.isNull("parentCatId")));
			}
			List<Category> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	@Override
	public int delCategoryByStoreId(String storeId){
		Session session = hsf.openSession();
		Transaction tx = null;
		int ret = 0;
		try{
			tx = session.beginTransaction();
	
			String hqlDelete = "delete Category cat where cat.id.storeId = :storeId";
			ret = session.createQuery(hqlDelete)
			        .setString( "storeId", storeId)
			        .executeUpdate();
			tx.commit();
		}catch(Exception e){
			logger.error("", e);
			tx.rollback();
		}finally{
			session.close();
		}
		
		return ret;
	}
	@Override
	public long getCategoryCount(String storeId, String pcatId){
		Session session = hsf.openSession();
		long ret = 0;
		if (pcatId==null){
			ret = ((Long) session
		         .createQuery("select COUNT(*) from Category cat where cat.id.storeId = :storeId") 
		         .setString("storeId", storeId)
		         .uniqueResult()).longValue();
		}else{
			ret = ((Long) session
			         .createQuery("select COUNT(*) from Category cat where cat.id.storeId = :storeId and parentCatId=:pcatId") 
			         .setString("storeId", storeId)
			         .setString("pcatId", pcatId)
			         .uniqueResult()).longValue();
		}
		session.close();
		return ret;
	}
	
	/***************************
	 * Product Operations
	 */
	

	@Override
	public List<Product> getProductByRootTaskId(String rootTaskId) {
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Product.class)
					.add(Restrictions.eq("rootTaskId", rootTaskId));
			List<Product> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	
	@Override
	public List<Product> getProductByPcatId(String storeId, String pcatId){
		Session session = hsf.openSession();
		try{
			Criteria cr = null;
			if (pcatId!=null){
				cr = session.createCriteria(Product.class)
						.add(Restrictions.conjunction().
								add(Restrictions.eq("id.storeId", storeId)).
								add(Restrictions.eq("catlist", pcatId)));
			}else{
				cr = session.createCriteria(Category.class)
						.add(Restrictions.conjunction().
								add(Restrictions.eq("id.storeId", storeId)).
								add(Restrictions.isNull("catlist")));
			}
			List<Product> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	
	@Override
	public int delProductAndPriceByStoreId(String storeId){
		Session session = hsf.openSession();
		Transaction tx = null;
		int ret = 0;
		try{
			tx = session.beginTransaction();
	
			String hqlDelete = "delete Product prd where prd.id.storeId = :storeId";
			ret = session.createQuery(hqlDelete)
			        .setString( "storeId", storeId)
			        .executeUpdate();
			
			hqlDelete = "delete Price price where price.id.storeId = :storeId";
			ret = session.createQuery(hqlDelete)
			        .setString( "storeId", storeId)
			        .executeUpdate();
			
			tx.commit();
		}catch(Exception e){
			logger.error("", e);
			tx.rollback();
		}finally{
			session.close();
		}
		return ret;
	}
	
	@Override
	public long getProductCount(String storeId, String pcatId){
		Session session = hsf.openSession();
		long ret;
		if (pcatId==null){
			ret = ((Long) session
			         .createQuery("select COUNT(*) from Product prd where prd.id.storeId = :storeId") 
			         .setString("storeId", storeId)
			         .uniqueResult()).longValue();
		}else{
			ret = ((Long) session
			         .createQuery("select COUNT(*) from Product prd where prd.id.storeId = :storeId and catlist= :pcatId") 
			         .setString("storeId", storeId)
			         .setString("pcatId", pcatId)
			         .uniqueResult()).longValue();
		}
		session.close();
		return ret;
	}
	
	/******************************
	 * Price Operations
	 */

	@Override
	public boolean addPrice(Price price) {
		Session session = hsf.openSession();
		
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(price);

			String productId = price.getId().getId();
			String storeId = price.getId().getStoreId();
			Product updateProduct = (Product) getCrawledItem(productId, storeId, Product.class);
			if (updateProduct != null) {
				if (price.getPrice() != 0) {
					updateProduct.setCurrentPrice(price.getPrice());
					session.saveOrUpdate(updateProduct);
				}
				tx.commit();
			} else {
				logger.error("product with id not found:" + productId
						+ ", price not added:" + price);
				tx.rollback();
			}
			return true;
		} catch (Exception e) {
			try{
				tx.rollback();
			}catch(Throwable t1){
				logger.error("throwable caught while rollback transaction for:"
						+ price, t1);
			}
			logger.error("", e);
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public Price getLatestPrice(String prdId, String storeId) {
		Session session = hsf.openSession();
		
		Query query = session.createQuery("select p from Price p where "
						+ "p.id.id = :id and p.id.storeId=:storeId order by p.id.createTime DESC");
		query.setParameter("id", prdId);
		query.setParameter("storeId", storeId);
		query.setMaxResults(1);
		try {
			List<Object> results = query.list();
			Price p = null;
			if (results != null && !results.isEmpty()) {
				p = (Price) results.get(0);
			}
			return p;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
}
