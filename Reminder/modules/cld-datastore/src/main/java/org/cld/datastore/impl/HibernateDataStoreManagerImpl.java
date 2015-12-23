package org.cld.datastore.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.xml.taskdef.BrowseTaskType;
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

public class HibernateDataStoreManagerImpl implements DataStoreManager {

	private static Logger logger = LogManager.getLogger(DataStoreManager.class);

	private SessionFactory hsf;
	
	public HibernateDataStoreManagerImpl() {
	}

	public SessionFactory getHibernateSF(){
		return hsf;
	}
	public void setHibernateSF(SessionFactory sf){
		hsf = sf;
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
	
	/************************************
	 * site conf
	 */
	@Override
	public List<SiteConf> getSiteConf(String uid){
		return getSiteConf(uid, false, -1);
	}
	
	@Override
	public List<SiteConf> getSiteConf(String uid, boolean withXml, int status){
		Session session = hsf.openSession();
		try{
			ProjectionList pl = Projections.projectionList()
									.add(Projections.property("id"), "id")
									.add(Projections.property("userid"), "userid")
									.add(Projections.property("status"), "status")
									.add(Projections.property("utime"), "utime");
			if (withXml){
				pl.add(Projections.property("confxml"), "confxml");
			}
			
			Criteria cr = session.createCriteria(SiteConf.class)
					.setProjection(pl)
					.setResultTransformer(Transformers.aliasToBean(SiteConf.class));
			if (uid!=null && !"".equals(uid)){
				cr.add(Restrictions.eq("userid", uid));
			}
			if (status!=-1){
				cr.add(Restrictions.eq("status", status));
			}
			List<SiteConf> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	@Override
	public SiteConf getFullSitConf(String id){
		Session session = hsf.openSession();
		try{
			SiteConf sc = (SiteConf) session.get(SiteConf.class, id);  
			return sc;
		} finally{
			session.close();
		}
	}
	@Override
	public boolean saveXmlConf(String id, String uid, String xml){
		Session session = this.hsf.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			SiteConf sc = (SiteConf) session.get(SiteConf.class, id);  
			if (sc!=null){
				sc.setConfxml(xml);
			}else{
				sc = new SiteConf();
				sc.setId(id);
				sc.setUserid(uid);
				sc.setConfxml(xml);
			}
			sc.setStatus(SiteConf.STATUS_TESTING);
			sc.setUtime(new Date());
			session.save(sc);
			tx.commit();
			return true;
		}catch(Exception e){
			if (tx!=null){
				tx.rollback();
			}
			logger.error("", e);
			return false;
		}finally{
			session.close();
		}
	}
	@Override
	public int deployConf(String[] ids, boolean deploy){
		Session session = this.hsf.openSession();
		Transaction tx = null;
		int ret=0;
		try{
			tx = session.beginTransaction();
			String hqlUpdate = "update SiteConf sc set sc.status=:status where sc.id in :ids";
			int status = 0;
			if (deploy){
				status = SiteConf.STATUS_DEPLOYED;
			}else{
				status = SiteConf.STATUS_TESTING;
			}
			ret = session.createQuery(hqlUpdate).
				setParameterList("ids", ids).
				setInteger("status", status).
				executeUpdate();
			
			tx.commit();
		}catch(Exception e){
			if (tx!=null){
				tx.rollback();
			}
			logger.error("", e);
		}finally{
			session.close();
		}
		return ret;
	}
	
	/***************
	 * Logs operation
	 */
	@Override
	public List<Logs> getLogsByTask(String taskId, int offset, int limit){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Logs.class)
					.add(Restrictions.eq("taskid", taskId))
					.setFirstResult(offset)
					.setMaxResults(limit);
			List<Logs> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	@Override
	public long getLogsCountByTask(String taskId){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Logs.class)
					.add(Restrictions.eq("taskid", taskId))
					.setProjection(Projections.rowCount());
			long ret = ((Long)cr.uniqueResult()).longValue();
			return ret;
		} finally{
			session.close();
		}
	}
	@Override
	public int clearLogs(String taskid){
		Session session = hsf.openSession();
		Transaction tx = null;
		int ret = 0;
		try{
			tx = session.beginTransaction();
			String hqlDelete = "delete Logs where taskid = :taskid";
			ret = session.createQuery(hqlDelete)
			        .setString("taskid", taskid)
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
	
	/*
	 * @param patternId: null, no patternId or a specific id
	 * @param orderByField: dated, patternId
	 * @param offset and limit
	 */
	@Override
	public List<Logs> getLogsByResolvedPattern(String patternId, String orderByField, boolean asc, int offset, int limit){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Logs.class);
			if (patternId==null){
				cr.add(Restrictions.isNull("patternId"));
			}else{
				cr.add(Restrictions.eq("patternId", patternId));
			}
			if (asc){
				cr.addOrder(Order.asc(orderByField));
			}else{
				cr.addOrder(Order.desc(orderByField));
			}
			cr.setFirstResult(offset);
			cr.setMaxResults(limit);
			List<Logs> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	@Override
	public long getLogsCountByResolvedPattern(String patternId){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Logs.class);
			if (patternId==null){
				cr.add(Restrictions.isNull("patternId"));
			}else{
				cr.add(Restrictions.eq("patternId", patternId));
			}
			cr.setProjection(Projections.rowCount());
			long ret = ((Long)cr.uniqueResult()).longValue();
			return ret;
		} finally{
			session.close();
		}
	}
	@Override
	public List<Logs> getLogsByString(String searchString, String orderByField, boolean asc, int offset, int limit){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Logs.class);
			if (!"".equals(searchString)){
				cr.add(Restrictions.disjunction()
						.add(Restrictions.like("message", searchString, MatchMode.ANYWHERE))
						.add(Restrictions.like("throwable", searchString, MatchMode.ANYWHERE))
						);
			}
			if (asc){
				cr.addOrder(Order.asc(orderByField));
			}else{
				cr.addOrder(Order.desc(orderByField));
			}
			cr.setFirstResult(offset);
			cr.setMaxResults(limit);
			List<Logs> list = cr.list();
			return list;
		} finally{
			session.close();
		}
	}
	@Override
	public long getLogsCountByString(String searchString){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(Logs.class);
			if (!"".equals(searchString)){
				cr.add(Restrictions.disjunction()
						.add(Restrictions.like("message", searchString, MatchMode.ANYWHERE))
						.add(Restrictions.like("throwable", searchString, MatchMode.ANYWHERE))
						);
			}
			cr.setProjection(Projections.rowCount());
			long ret = ((Long)cr.uniqueResult()).longValue();
			return ret;
		} finally{
			session.close();
		}
	}
	//return the logs fitting the lp and not yet resolved as this lp
	@Override
	public List<Logs> getLogsNotResolvedByPattern(String regexp, String orderByField, boolean asc, int maxCount){
		List<Logs> ll = new ArrayList<Logs>();
		Session session = hsf.openSession();
		ScrollableResults rs =null;
		try{
			Criteria cr = session.createCriteria(Logs.class);
			cr.add(Restrictions.isNull("patternId"));
			if (asc){
				cr.addOrder(Order.asc(orderByField));
			}else{
				cr.addOrder(Order.desc(orderByField));
			}
			rs= cr.scroll();
			int i=0;
			LogPattern lp = new LogPattern(regexp);
			boolean end=false;
			while(rs.next() && !end){
				Logs log = (Logs) rs.get(0);
				List<Logs> matchedLogs = new ArrayList<Logs>();
				for (int j=0; j<lp.getPatterns().length; j++){
					Pattern p1 = lp.getPatterns()[j];
					String combined = log.getMessage() + log.getThrowable();
					Matcher messageMatcher = p1.matcher(combined);
					if (messageMatcher.matches()){
						if (j==lp.getPatterns().length-1){
							//all the patterns matched, the series of Logs is matched with the LogPattern
							ll.addAll(matchedLogs);
							ll.add(log);
							i++;
						}else{
							matchedLogs.add(log);
							//get 1 more log
							if (rs.next()){
								log = (Logs) rs.get(0);
							}else{
								end= true;
								break;
							}
						}
					}else{
						//not fully matched
						break;
					}
				}
				
				if (i>maxCount){
					break;
				}
			}
			return ll;
		} finally{
			if (rs!=null){
				rs.close();
			}
			session.close();
		}
	}
	@Override
	public long resolveLog(Long[] logIds, String patternId){
		Session session = hsf.openSession();
		Transaction tx = null;
		long ret = 0;
		try{
			tx = session.beginTransaction();
	
			String hqlDelete = "update Logs l set l.patternId = :patternId where l.id in (:ids)";
			ret = session.createQuery(hqlDelete)
			        .setParameter("patternId", patternId)
			        .setParameterList("ids", logIds)
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
	
	/***************
	 * Log Pattern operation
	 */
	@Override
	public List<LogPattern> getAllLogPattern(){
		Session session = hsf.openSession();
		try{
			Criteria cr = session.createCriteria(LogPattern.class);
			List<LogPattern> lpl = cr.list();
			for (LogPattern lp :lpl){
				lp.compile();
			}
			return lpl;
		} finally{
			session.close();
		}
	}
	
	@Override
	public void mergeLogPattern(LogPattern lp){
		Session session = hsf.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(lp);
			tx.commit();
		} catch (Exception e) {
			try{
				tx.rollback();
			}catch(Throwable t1){
				logger.error("throwable caught while rollback transaction for:" + lp, t1);
			}
			logger.error("", e);
		} finally {
			session.close();
		}
	}
	
	@Override
	public LogPattern getLogPattern(String id){
		Session session = hsf.openSession();
		try{
			LogPattern lp = (LogPattern) session.get(LogPattern.class, id);
			return lp;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			session.close();
		}
	}
}
