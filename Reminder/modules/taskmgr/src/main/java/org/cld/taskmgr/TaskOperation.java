package org.cld.taskmgr;

import java.util.Set;

import org.cld.taskmgr.entity.Task;

public class TaskOperation {
	public static final int OP_ADD=1; 
	public static final int OP_REMOVE=2;
	
	private int opType;
	private Set<? extends Task> taskSet;
	
	public int getOpType() {
		return opType;
	}
	public void setOpType(int opType) {
		this.opType = opType;
	}
	public Set<? extends Task> getTaskSet() {
		return taskSet;
	}
	public void setTaskSet(Set<? extends Task> taskSet) {
		this.taskSet = taskSet;
	}
	
	public String toString(){
		return "opType:" + opType + ", task set:" + taskSet;
	}
}
