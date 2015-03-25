package org.cld.taskmgr.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="ttype",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("taskstat")
@Table(name="TASKSTAT")
public class TaskStat implements Serializable {
	
	public static final int TASK_STATUS_CREATED=1;
	public static final int TASK_STATUS_RUNNING=2;
	public static final int TASK_STATUS_FINISHED=3;
	
	private static final long serialVersionUID = 1L;
	@Id
	private TSKey tsKey;
	private String storeId;
	private String taskName;
	private Date startDate;
	private Date lastUpdateDate;
	private boolean latest = false;//whether this is the latest bs for this key
	private Date createDate; //task created date time
	private String nodeId; 
	private int status;
	@Column(insertable=false, updatable=false)
	private String ttype;

	public TaskStat(){
		this.setTtype(this.getClass().getName());
	}
	
	public void setUp(String tid, String ttype, int runNum, String nodeId, String taskName){
		tsKey = new TSKey(tid, runNum);	
		this.setNodeId(nodeId);		
		this.setTaskName(taskName);
	}
	
	public TaskStat(String tid){
		this();
		tsKey = new TSKey(tid, 1);	
	}
	
	public boolean isFinished(){
		return !startDate.equals(lastUpdateDate);
	}
	
	//to be overridden
	public void add(TaskStat ts){
		
	}
	
	//to be overridden
	public List<BrokenPage> getBPL(){
		return null;
	}
	
	public String getTid(){
		return tsKey.getTid();
	}
	public int getRunRound(){
		return tsKey.getRunRound();
	}
	public boolean getLatest() {
		return latest;
	}
	public void setLatest(boolean latest) {
		this.latest = latest;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public TSKey getTsKey() {
		return tsKey;
	}

	public void setTsKey(TSKey tsKey) {
		this.tsKey = tsKey;
	}
	public String getTtype() {
		return ttype;
	}

	public void setTtype(String ttype) {
		this.ttype = ttype;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
