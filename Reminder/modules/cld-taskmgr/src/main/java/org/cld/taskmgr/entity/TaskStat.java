package org.cld.taskmgr.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

//this maps to the status of a Task
public class TaskStat implements Serializable {
	
	public static final int STATUS_CREATED=1;
	public static final int STATUS_RUNNING=2;
	public static final int STATUS_FINISHED=3;
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Date lastUpdateDate;
	private Date createDate; //task created date time
	private int status;
	
}
