package org.cld.webconf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.entity.LogPattern;
import org.cld.util.entity.Logs;
import org.cld.util.entity.SiteConf;
import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

public class WebConfPersistMgr {
	private static Logger logger = LogManager.getLogger(WebConfPersistMgr.class);

	private SessionFactory hsf;
	
	public WebConfPersistMgr(SessionFactory hsf){
		this.hsf = hsf;
	}
	
	/************************************
	 * site conf
	 */
	
	public List<SiteConf> getSiteConf(String uid){
		return getSiteConf(uid, false, -1);
	}
	
	
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
	
	public SiteConf getFullSitConf(String id){
		Session session = hsf.openSession();
		try{
			SiteConf sc = (SiteConf) session.get(SiteConf.class, id);  
			return sc;
		} finally{
			session.close();
		}
	}
	
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
