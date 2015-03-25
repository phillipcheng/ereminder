package org.cld.taskmgr;


import org.cld.taskmgr.entity.Task;



/**
 * this remote interface is exposed for local task manager
 * @author Cheng Yi
 *
 */
public interface LocalTaskMgrInf {
	
	//local
	boolean addTaskToExecutor(Task t, boolean immediate);
	boolean removeTaskFromExecutor(String key);
}
