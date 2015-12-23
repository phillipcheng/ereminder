package org.cld.taskmgr.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBException;
import org.cld.datastore.QueryUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

public class TaskPersistMgr {
	private static Logger logger = LogManager.getLogger(TaskPersistMgr.class);
	
	public static final int BATCH_SIZE=800;
	
	///////////////////////////
	/////// task management
	///////////////////////////
	public static List<String> getTaskIds(SessionFactory sf, String taskName, int offset, int limit){
		Session session = sf.openSession();
		try{
			Criteria cr = session.createCriteria(Task.class)
					.add(Restrictions.eq("name", taskName))
			    .setProjection(Projections.projectionList()
			    .add(Projections.property("id"), "id"))
			    .setResultTransformer(Transformers.aliasToBean(Task.class));
			if (offset>0){
				cr.setFirstResult(offset);
			}
			if (limit>0){
				cr.setMaxResults(limit);
			}
			List<Task> list = cr.list();
			List<String> idList = new ArrayList<String>();
			for (Task t: list){
				idList.add(t.getId());
			}
			return idList;			  
		} finally{
			session.close();
		}
	}
	
	public static Task getTask(SessionFactory sf, String tid){
		Session session = sf.openSession();
		try{
			Task t = (Task) session.get("org.cld.taskmgr.entity.Task", tid);
			return t;
		}finally{
			session.close();
		}
	}
	
	private static void expandTasks(List<Task> tl){
		for (Task t: tl){
			t.fromParamData();
		}
	}
	
	public static List<Task> getAllTask(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		try {
			Query query = session.getNamedQuery("getAllTask");
			List<Task> plist = query.list();
			expandTasks(plist);
			return plist;
		}finally{
			session.close();
		}
	}
	
	public static boolean removeAllTasks(SessionFactory sessionFactory){
		logger.info("remove all tasks in db.");
		Session session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			tr.begin();
			Query query = session.getNamedQuery("removeAllTasks");
			int i = query.executeUpdate();
			logger.info(i + " tasks deleted.");
			tr.commit();
			return true;
		}catch(Throwable t){
			logger.warn("throwable caught while removeAllTasks.", t);
			tr.rollback();
			return false;
		}finally{
			session.close();
		}
	}
	
	/**
	 * 
	 * @param sessionFactory
	 * @param tl: task list
	 * @param nodeId
	 * @return
	 */
	public static boolean addTasks(SessionFactory sessionFactory, Collection<? extends Task> tl){
		Session session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			tr.begin();
			Iterator<? extends Task> it = tl.iterator();
			while (it.hasNext()){
				Task t = it.next();
				Query query = session.getNamedQuery("getTaskById").setString("tid", t.getId());
				query.setMaxResults(1);
				Task oldT= (Task) query.uniqueResult();
				if (oldT == null){
					t.setLastUpdateDate(new Date());
					t.toParamData();
					session.save(t);
				}else{
					//session.save(oldT);
				}
			}
			tr.commit();
			return true;
		}catch(Throwable t){
			logger.warn("throwable caught while addTasks for:" + tl, t);
			tr.rollback();
			return false;
		}finally{
			session.close();
		}
	}
	/**
	 * 
	 * @param sessionFactory
	 * @param tkl
	 * @return
	 */
	public static boolean removeTasksById(SessionFactory sessionFactory, Collection<String> tkl){
		logger.info("remove tasks in db." + tkl);
		Session session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			tr.begin();
			Iterator<String> tklit = tkl.iterator();
			while (tklit.hasNext()){
				String id = tklit.next();
				Task t = (Task) session.get("org.cld.taskmgr.entity.Task", id);
				if (t!= null){
					session.delete(t);
				}else{
					logger.error("task not found with id:" + id); 
				}
			}
			tr.commit();
			return true;
		}catch(Throwable t){
			logger.warn("throwable caught while removeTasks for:" + tkl, t);
			tr.rollback();
			return false;
		}finally{
			session.close();
		}
	}
	
	/////////////////////////////
	//// Task Stat
	/////////////////////////////
	public static boolean removeAllBS(SessionFactory sessionFactory){
		Session session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			tr.begin();
			Query query;
			int deletedRow = 0;
			query = session.getNamedQuery("removeAllTS");
			deletedRow += query.executeUpdate();
			logger.info(deletedRow + " rows deleted.");
			tr.commit();
			return true;
		}catch(Throwable t){
			logger.warn("throwable caught while removeAllBS.", t);
			tr.rollback();
			return false;
		}finally{
			session.close();
		}
	}
	
	/**
	 * @param ts
	 * @return
	 */
	public static boolean addOrUpdateTS(SessionFactory sf, TaskStat ts) {
		return true;	
	}
	
	/**
	 *
	 * @return null for not found
	 * @throws DBException, means db exception occurs
	 */
	public static TaskStat getStat(SessionFactory sessionFactory, String key) throws DBException {
		return null;
	}
	
	/**
	 * @param key: 
	 * @return null for not found
	 * @throws DBException, means db exception occurs
	 */
	public static TaskStat getLatestStat(SessionFactory sessionFactory, String tKey) throws DBException {
		Session session = sessionFactory.openSession();
		
		try {
			Query query = session.getNamedQuery("getLatestStatByID").setParameter("tid", tKey);
			query.setMaxResults(1);
			TaskStat stat = (TaskStat) query.uniqueResult();
			return stat;
		}catch(Throwable t){
			logger.warn("throwable caught while getStat:" +  tKey, t);
			throw new DBException(t);
		}finally{
			session.close();
		}
	}	
	
	/**
	 * @param key: key.runRound = 0, find the latest run round of this key
	 * @return null for not found
	 * @throws DBException, means db exception occurs
	 */
	public static List<TaskStat> getLastestSBSListByTKeyList(SessionFactory sessionFactory, 
			List<String> tklist) throws DBException {
		logger.info("begin latest bs keylist by telist, telist size:" + tklist.size());
		Session session = sessionFactory.openSession();
		try {
			List<Query> queryList = QueryUtil.getQuerys(session, "getLastestStatByKeyList", "tidList", tklist, BATCH_SIZE);
			List<TaskStat> blist = new ArrayList<TaskStat>();
			for (int i=0; i<queryList.size(); i++){
				Query q = queryList.get(i);
				logger.info("sub query start list size:");
				List<TaskStat> bl = q.list();
				logger.info("sub query end list size:" + bl.size() );
				blist.addAll(bl);
			}
			return blist;
		}catch(Throwable t){
			logger.warn("throwable caught while getLastestStatByKeyList:" +  tklist, t);
			throw new DBException(t);
		}finally{
			session.close();
			logger.info("get latest bs begin by keylist size:" + tklist.size());
		}
	}	
	
	public static boolean addOrUpdateTaskStat(SessionFactory sf, TaskStat bcStat) {
		int maxTry = 3;
		int tryNum=0;
		Throwable dbt = null;
		
		while (tryNum < maxTry){
			try {
				TaskPersistMgr.addOrUpdateTS(sf, bcStat);
				break;
			}catch(Throwable t){
				logger.info("throwable in addOrUpdateBS" ,t);
				tryNum++;
				dbt=t;
			}
		}
		if (tryNum == maxTry){
			logger.error("max error reached when addOrUpdateBS." + bcStat, dbt);
			return false;
		}
		
		return true;
	}
	
}
