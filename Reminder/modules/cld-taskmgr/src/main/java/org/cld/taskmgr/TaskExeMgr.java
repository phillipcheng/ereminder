package org.cld.taskmgr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskExeMgr {
	
	private static Logger logger = LogManager.getLogger(TaskExeMgr.class);
	
	private ExecutorService es;
	private Map<String, Future> runningTasks = new ConcurrentHashMap<String, Future>();//taskId to Future 1-1
	private Map<String, Set<String>> taskMap = new ConcurrentHashMap<String, Set<String>>(); //userkey to taskId 1-many
	
	public TaskExeMgr(int poolSize){
		es = Executors.newFixedThreadPool(poolSize);
	}
	
	public void submit(String taskId, String userkey, Runnable r){
		Future f = es.submit(r);
		runningTasks.put(taskId, f);
		Set<String> tids = taskMap.get(userkey);
		if (tids == null){
			tids = new HashSet<String>();//no current set
		}
		tids.add(taskId);
	}
	
	//cancel all
	public void cancel(String userkey){
		if (taskMap.containsKey(userkey)){
			Set<String> taskIdSet = taskMap.get(userkey);
			for (String taskId:taskIdSet){
				Future f = runningTasks.get(taskId);
				try {
					if (f.get()!=null){
						f.cancel(true);
					}
				}catch(Exception e){
					logger.error("", e);
				}
				runningTasks.remove(taskId);
			}
			taskMap.remove(userkey);
		}
	}
	
	public void cancel(String userkey, String taskId){
		if (taskMap.containsKey(userkey)){
			Set<String> taskIdSet = taskMap.get(userkey);
			for (String id:taskIdSet){
				if (taskId.equals(id)){
					Future f = runningTasks.get(taskId);
					try {
						if (f.get()!=null){
							f.cancel(true);
						}
					}catch(Exception e){
						logger.error("", e);
					}
					runningTasks.remove(taskId);
					break;
				}
			}
			Set<String> tids = taskMap.get(userkey);
			if (tids.contains(taskId)){
				tids.remove(taskId);
			}
			if (tids.size()==0){
				taskMap.remove(userkey);
			}
		}
	}
}
