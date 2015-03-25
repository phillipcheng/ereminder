package org.cld.taskmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBException;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.client.AppClientNodeInf;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

class TaskFuture{
	public Task t;
	public Future<?> f;
	public TaskFuture(Task t, Future<?>f){
		this.t = t;
		this.f = f;
	}
}

class PendingTask{
	public Task t;
	public int round;
	public boolean immediate;
	public PendingTask(Task t, int round, boolean immediate){
		this.t = t;
		this.round = round;
		this.immediate = immediate;
	}
}

public class LocalTaskManager implements LocalTaskMgrInf {

	private static Logger logger =  LogManager.getLogger(LocalTaskManager.class);

	private AppClientNodeInf appNode;
	protected NodeConf nc;

	private SessionFactory taskMgrSF;
	
	private int runRound=0;
	public int getRunRound() {
		return runRound;
	}

	public void setRunRound(int runRound) {
		this.runRound = runRound;
	}

	//
	private ScheduledThreadPoolExecutor executorService = null;
	//per task run round base number
	private ConcurrentHashMap<String, Integer> perTaskInitCount = new ConcurrentHashMap<String, Integer>();
	//per task executed count should be less then run.round + run.round.base.number
	private ConcurrentHashMap<String, Integer> perTaskXCount = new ConcurrentHashMap<String, Integer>(); 
	//running task map to future
	private Map<String, TaskFuture> taskMap = new ConcurrentHashMap<String, TaskFuture>();
	//site name to running tasks belong to this site
	private Map<String, Set<String>> runningTasksPerSite = new ConcurrentHashMap<String, Set<String>>();
	//tasks pending to be submitted because of max running task per site constraint
	private List<PendingTask> pendingTasks = new ArrayList<PendingTask>();
	
	private LocalTaskManager(){
		//to forbid instantiate
	}
	
	public LocalTaskManager(NodeConf nc){
		this.nc = nc;
	}
	
	public Set<String> getRunningTasks(){
		return taskMap.keySet();
	}
	
	public boolean hasTask(String id){
		return taskMap.containsKey(id);
	}
	
	public Set<String> getRunningTasks(String siteId){
		if (runningTasksPerSite.containsKey(siteId)){
			return runningTasksPerSite.get(siteId);
		}else{
			return new HashSet<String>();
		}
	}
	
	public void setup(AppClientNodeInf appNode, NodeConf nc){
		this.nc = nc;
		this.appNode = appNode;
		runRound = nc.getRunRound();
		
		//1
		executorService = new ScheduledThreadPoolExecutor(nc.getThreadSize());

		logger.debug("local task manager setup." + executorService);
		
		//2. setup task sf
		Configuration cfg = DBFactory.setUpCfg(nc.getNodeId(), TaskMgr.moduleName, nc.getDBConf());		
		taskMgrSF = DBFactory.setUpSF(nc.getNodeId(), TaskMgr.moduleName, cfg);

	}
	
	
	/////////////////////////////////
	// Add Tasks
	/////////////////////////////////
	private void realAddTasktoExecutor2(Task t, int r, boolean immediate){
		RerunableTask rrTask = (RerunableTask) appNode.getRunnableTask(t, r);
		Future<?> f = null;
		if (immediate){
			f= executorService.submit(rrTask);
			//house keeping of the runningTasksPerSite only for immediately submitted tasks
			if (runningTasksPerSite.containsKey(t.getStoreId())){
				runningTasksPerSite.get(t.getStoreId()).add(t.getId());
			}else{
				Set<String> idset = new HashSet<String>();
				idset.add(t.getId());
				runningTasksPerSite.put(t.getStoreId(), idset);
			}
		}else{
			if (t.getRerunInterim()>0){
				f= executorService.schedule(rrTask, t.getRerunInterim(), TimeUnit.MINUTES);
			}else{
				//no schedule
			}
		}
		if (f!=null){
			taskMap.put(t.getId(), new TaskFuture(t,f));
			logger.debug(String.format("task %s put to the taskMap.", t.getId()));
			perTaskXCount.put(t.getId(), r);
		}else{
			logger.warn(String.format("executor service schedule/submit task %s return a null future.", t.getId()));
		}
	}
	
	private void realAddTasktoExecutor(Task t, int r, boolean immediate){
		int max = nc.getTaskMgr().getMaxRunningTasks(t.getStoreId());
		if (max!=0){
			int curTasks = 0;
			if (this.runningTasksPerSite.containsKey(t.getStoreId())){
				curTasks = this.runningTasksPerSite.get(t.getStoreId()).size();
			}else{
				curTasks = 0;
			}
			if (curTasks<max){
				realAddTasktoExecutor2(t, r, immediate);
			}else{
				//max reached put in the pendingTasks
				pendingTasks.add(new PendingTask(t,r,immediate));
				logger.debug(String.format("task %s added to pending Tasks.", t.toString()));
			}
		}else{
			//unlimited, just add
			realAddTasktoExecutor2(t, r, immediate);
		}
	}
	/**
	 * @param te
	 * @param thisSBS: pre-fetched simple browse statistics for this task
	 * @param needLookup: true means need to lookup task-history/browse-statistics for the latest version
	 * 					: false means not needed, already looked up
	 * @return false when the task is not added due to runRound has reached.
	 */
	private synchronized boolean addTaskToExecutor(Task t, TaskStat thisSBS, boolean needLookup, boolean immediate){
		String tKey = t.getId();
		Integer c = perTaskXCount.get(tKey);
		if (c!=null){
			Integer init = perTaskInitCount.get(tKey);//c is not null, init can't be null
			int r = c.intValue()+1;
			if (runRound!=0){
				if (r - init.intValue() < runRound){
					realAddTasktoExecutor(t, r, immediate);
					logger.info("task:" + tKey + " added with run.round:" + r);
				}else{
					logger.error("task:" + tKey + " reached run.round:" + runRound + ". task not added.");
					return false;
				}
			}else{
				realAddTasktoExecutor(t, r, immediate);
				logger.info("task:" + tKey + " added with run.round:" + r);
			}
		}else{
			//1st time, always allow, initialize the base number
			int r=0;
			TaskStat sbs = null;
			if (thisSBS==null){
				if (needLookup){
					try {
						sbs = TaskPersistMgr.getLatestStat(taskMgrSF, tKey);
					}catch(DBException dbe){
						logger.error("db exception while get stat for" + tKey, dbe);
					}
				}else{
					sbs = null;
				}
			}else{
				sbs = thisSBS;
			}
			
			if (sbs != null){
				if (sbs.isFinished()){
					r = sbs.getRunRound() + 1;
				}else{
					r = sbs.getRunRound();
				}	
			}else{
				r=1;
			}
			
			realAddTasktoExecutor(t, r, immediate);
			perTaskInitCount.put(tKey, r);
			logger.info("task:" + t + " added with run.round:" + r);
		}
	
		return true;
	}
	
	public synchronized boolean addTaskToExecutor(Task t, boolean immediate){
		return addTaskToExecutor(t, null, true, immediate);
	}
	
	/**
	 * used by client to batch move tasks distributed to it, 
	 * at that time these tasks are already in the db, so the id is generated.
	 * @param tl: task list
	 * @param
	 */
	public synchronized boolean clientAddTasks(List<String> tkl) {
		if (tkl.size()<=0)
			return true;
		
		logger.info("client get request to Tasks:" + tkl);
		List<TaskStat> sbsList = null;
		Map<String, TaskStat> map = new HashMap<String, TaskStat>();
		try {
			//TODO optimization for null bs items in the list
			sbsList = TaskPersistMgr.getLastestSBSListByTKeyList(taskMgrSF, tkl);
			if (sbsList!=null){
				for (int i=0; i<sbsList.size(); i++){
					TaskStat sbs = sbsList.get(i);
					map.put(sbs.getTid(), sbs);
				}
			}
		}catch(DBException dbe){
			logger.error("error get latestBS list:" + tkl, dbe);
		}
		
		
		logger.info("add tasks to executor begin:");
		for(int i=0; i<tkl.size(); i++){
			String tid = tkl.get(i);
			if (tid==null){
				continue;
			}
			//get task from db
			Task task = TaskPersistMgr.getTask(taskMgrSF, tid);
			if (task != null){
				task.setNodeId(nc.getNodeId());
				addTaskToExecutor(task, map.get(tid), false, true);
			}else{
				logger.error("task not found for:" + tid);
			}
		}
		logger.info("add tasks to executor finished.");
		
		logger.info("move tasks in db begin.");
		//this is coupled with DO-NOTHING is removeTasks
		TaskPersistMgr.moveTasks(taskMgrSF, tkl, nc.getNodeId());
		logger.info("move tasks in db end.");
		return true;
	}

	///////////////////////
	// remove tasks
	///////////////////////
	public boolean removeTasksFromDB(List<String> tkl) {
		Set<String> tks = new HashSet<String>();
		tks.addAll(tkl);
		logger.info("removing from db:" + tkl);
		if (taskMgrSF!=null){
			TaskPersistMgr.removeTasksById(taskMgrSF, tks);
		}
		return true;
	}
	
	private void addIfOpen(String storeId){
		int max = nc.getTaskMgr().getMaxRunningTasks(storeId);
		if (max!=0){
			if (runningTasksPerSite.get(storeId).size()<max){
				PendingTask pt = null;
				for (int i=0; i<this.pendingTasks.size(); i++){
					PendingTask pt1 = pendingTasks.get(i);
					if (storeId.equals(pt1.t.getStoreId()) && pt1.immediate){
						//must schedule the immediate one
						pt = pendingTasks.remove(i);
						break;
					}
				}
				if (pt!=null){
					realAddTasktoExecutor2(pt.t, pt.round, pt.immediate);
					logger.debug(String.format("task %s removed from pending Tasks.", pt.t.toString()));
				}
			}
		}else{
			//those will not be in the queue
		}
	}
	
	public synchronized boolean removeTaskFromExecutor(String key){
		//let the system be temporarily cancelable for remove
		nc.setCancelable(true);
		
		if (taskMap.containsKey(key)){
			TaskFuture tf = taskMap.get(key);
			Future<?> f = tf.f;
			Task t = tf.t;
			if (f!= null){
				logger.info("find task future for:" + key + ", cancel it now.");
				f.cancel(true);
				taskMap.remove(key);
				Set<String> runningTasksForThisSite = this.runningTasksPerSite.get(t.getStoreId());
				runningTasksForThisSite.remove(t.getId());
				//
				addIfOpen(t.getStoreId());
			}else{
				logger.warn("task:" + key + " 's future becomes null.");
			}
		}else{
			logger.warn("task:" + key + " not found in the taskMap.");
		}
		
		//set it back
		nc.setCancelable(false);
		return true;
	}

	/**
	 * used by client to batch remove tasks removed from it
	 * @param cmd
	 * @param tl
	 */
	public synchronized boolean clientRemoveTasks(List<String> tkl) {
		logger.info("client get request to remove tasks:" + tkl);
		for(int i=0; i<tkl.size(); i++){
			String key = tkl.get(i);
			removeTaskFromExecutor(key);
		}
		return false;
	}
	
	public synchronized boolean serverRemoveTasks(List<String> tkl) {
		for(int i=0; i<tkl.size(); i++){
			String key = tkl.get(i);
			removeTaskFromExecutor(key);
		}
		
		removeTasksFromDB(tkl);
		return false;
	}
	
	
	//////////////////
	public List<String> localGetMyTaskIDs(boolean clean){
		if (clean){
			TaskPersistMgr.removeAllTasks(taskMgrSF);
		}
		
		List<String> tkl = TaskPersistMgr.getMyTaskIDs(taskMgrSF, nc.getNodeId());
		
		return tkl;
	}
	
	public List<Task> localGetMyTasks(String taskType){		
		
		List<Task> tl = TaskPersistMgr.getMyDistributableTasks(taskMgrSF, nc.getNodeId(), taskType);
		
		return tl;
	}
	
	public void moveTasksInDBToMe(List<String> tkl){
		TaskPersistMgr.moveTasks(taskMgrSF, tkl, nc.getNodeId());
	}
	
	public void nodeConfPropChanged(NodeConfPropChangedEvent ncpce) {
		if (NodeConf.threadSize_Key.equals(ncpce.getPropName())){
			this.getExe().setCorePoolSize(ncpce.getIntValue());
			this.getExe().setMaximumPoolSize(ncpce.getIntValue()+1);
		}
	}
	
	public ThreadPoolExecutor getExe(){
		return executorService;
	}
	
	public SessionFactory getTaskSF(){
		return taskMgrSF;
	}
	
	public void setTaskSF(SessionFactory sf){
		this.taskMgrSF =sf;
	}
	
}
