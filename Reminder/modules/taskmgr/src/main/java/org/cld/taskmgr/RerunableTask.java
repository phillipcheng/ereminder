package org.cld.taskmgr;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.entity.TaskStat;
import org.hibernate.SessionFactory;

public class RerunableTask implements Runnable, Comparable<RerunableTask> {

	private static Logger logger =  LogManager.getLogger(RerunableTask.class);

	private Task t;
	private int runNum;
	private boolean reRun=false;
	
	private ClientNodeImpl clientNode;
	private SessionFactory taskSF;
	private LocalTaskMgrInf ltmInf;
	private TaskStat ts;
	private Map<String, Object> params;//task runtime params, only cconf now
	
	public RerunableTask(ClientNodeImpl clientNode, TaskMgr taskMgr, Task t, int runNum, SessionFactory taskSF, 
			LocalTaskMgrInf ltmInf, boolean reRun, Map<String, Object> params){
		this.clientNode = clientNode;
		this.t = t;
		this.runNum = runNum;
		this.taskSF=taskSF;
		this.ltmInf=ltmInf;
		this.params = params;
		this.reRun = reRun;
		//create the task stat
		TaskTypeConf ttconf = taskMgr.getTaskType(t.getTtype());
		if (ttconf!=null){
			Class<TaskStat> tsClazz = ttconf.getTaskStatClass();
			try {
				ts = tsClazz.newInstance();
				ts.setUp(t.getId(), t.getTtype(), runNum, t.getNodeId(), t.getName());
				ts.setStoreId(t.getStoreId());
				ts.setCreateDate(new Date());
				ts.setStatus(TaskStat.TASK_STATUS_CREATED);
			} catch (Throwable e) {
				logger.error("instantiate exception for:" + tsClazz, e);
			}
		}else{
			logger.error("ttconf not found for:" + t.getTtype());
		}
	}
	
	public Task getTask(){
		return t;
	}
	
	public int getRunNum(){
		return runNum;
	}
	
	public TaskStat getTS(){
		return ts;
	}
	
	public boolean equals(Object o){
		RerunableTask rrt = (RerunableTask)o;
		return (this.t.equals(rrt.t));
	}
	
	public int hashCode(){
		return t.hashCode();
	}

	@Override
	public int compareTo(RerunableTask b) {
		if (runNum == b.getRunNum()){
			return 0;
		}else if (runNum > b.getRunNum()){
			//when the num of run-round is bigger, then lower priority. Let others to do their runRound 1st.
			return 1;
		}else{ 
			return -1;
		}
	}
	
	public String toString(){
		return "rerun:" + reRun + "," + t.toString() + ", runNum:" + runNum + ", task:" + this.getTask().getId();
	}
	
	public void run() {	
		ts.setStatus(TaskStat.TASK_STATUS_RUNNING);
		boolean cancelled=false;
		logger.info("rerunable task started:"+ this);
		try {
			//1
			ts.setStartDate(new Date());							
			TaskPersistMgr.addOrUpdateTS(taskSF, ts);
			
			//2
			List<Task> tl = t.runMyself(params, ts);
			if (tl!=null && tl.size()>0){
				TaskUtil.executeTasks(clientNode, tl);
			}
			
			Thread.sleep(100);
			
		}catch(InterruptedException ie){
			logger.error("interrupted return:" + this.toString());
			cancelled=true;
		}catch(Throwable t1){
			logger.error("throwable caught in run." + ts, t1);
		}
		ts.setStatus(TaskStat.TASK_STATUS_FINISHED);//3
		TaskPersistMgr.addOrUpdateTS(taskSF, ts);
		ltmInf.removeTaskFromExecutor(t.getId());
		if (!cancelled){
			if (reRun && t.getRerunInterim()>0){
				try {
					ltmInf.addTaskToExecutor(t, false);
				}catch(Throwable t2){
					logger.error("throwable caught in addTaskToExecutor:" + t, t2);
				}
			}
		}else{
			//do not need to remove, it will be removed by the canceller
		}
		
		
		logger.debug("rerunable task ended:"+ this);
	}
}
