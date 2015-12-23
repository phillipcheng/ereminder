package org.cld.util.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Logs")
public class Logs {
	@Column(name = "id")
	@Id
	@GeneratedValue
	private long id;
	
	private String taskid;
	
	private Date dated;
	
	private String logger;
	
	private String level;
	
	@Column(name = "message", length=1000)
	private String message;
	
	@Column(name = "throwable", length=5000)
	private String throwable;
	
	private String patternId;
	
	public static String datetimeformat="yyyy-MM-dd-hh-mm-ss-SSS";
	public static SimpleDateFormat sdf = new SimpleDateFormat(datetimeformat);
	
	public Logs(){
		
	}
	
	public Logs(String taskid, Date dated, String logger, String level, String message, String throwable, String patternId){
		this.setTaskid(taskid);
		this.dated = dated;
		this.logger = logger;
		this.level = level;
		this.message = message;
		this.throwable = throwable;
		this.patternId = patternId;
	}

	public Date getDated() {
		return dated;
	}
	public String getStrDated(){
		return sdf.format(dated);
	}

	public long getId(){
		return id;
	}
	public void setDated(Date dated) {
		this.dated = dated;
	}

	public String getLogger() {
		return logger;
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getThrowable() {
		return throwable;
	}

	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getPatternId() {
		return patternId;
	}

	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	
}
