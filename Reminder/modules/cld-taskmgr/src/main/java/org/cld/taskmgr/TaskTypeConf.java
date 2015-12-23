package org.cld.taskmgr;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

public class TaskTypeConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(TaskTypeConf.class);
	

	public static final String taskEntity_Key= "entity";
	public static final String taskProcess_Key= "process";
	public static final String taskStat_Key="stat";

	private String name;
	private String entityImpl;
	private String processImpl;
	private String statImpl;
	
	private Class<Task> taskEntityClass;
	private Class<TaskStat> taskStatClass;
	
	public String toString(){
		return "name:" + name + "\n" +
				"taskEntityClass:" + taskEntityClass + "\n" +
				"taskStatClass:" + taskStatClass + "\n";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntityImpl() {
		return entityImpl;
	}

	public void setEntityImpl(String entityImpl) {
		this.entityImpl = entityImpl;
	}

	public String getProcessImpl() {
		return processImpl;
	}

	public void setProcessImpl(String processImpl) {
		this.processImpl = processImpl;
	}

	public Class<Task> getTaskEntityClass() {
		return taskEntityClass;
	}

	public void setTaskEntityClass(Class<Task> taskEntityClass) {
		this.taskEntityClass = taskEntityClass;
	}

	public String getStatImpl() {
		return statImpl;
	}

	public void setStatImpl(String statImpl) {
		this.statImpl = statImpl;
	}

	public Class<TaskStat> getTaskStatClass() {
		return taskStatClass;
	}

	public void setTaskStatClass(Class<TaskStat> taskStatClass) {
		this.taskStatClass = taskStatClass;
	}
	
}
