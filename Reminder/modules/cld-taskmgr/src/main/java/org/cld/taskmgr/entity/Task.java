package org.cld.taskmgr.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.util.JsonUtil;
import org.h2.util.StringUtils;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.TasksType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements Comparable<Task>, Serializable{
	
	private static Logger logger =  LogManager.getLogger(Task.class);
	private static final long serialVersionUID = 1L;
	
	public static final String startTask_Key="start";
	public static final String taskType_Key="task.type";
	public static final String next_Key="next";
	
	public static final String END_TASK="end";
	
	private boolean start=false; //true for start task
	@Id
	private String id;	//run time instance id, to be defined by sub-class usually
	protected String storeId;
	private String name; //task name
	private String rootTaskId;
	@Column(insertable=false, updatable=false, length = 100)
	private String ttype="task"; //task type
	private String nextTask = END_TASK;
	private Date lastUpdateDate;
	private Date startDate;
	private String paramData;
	private String confName; //the conf file contains this task
	
	//@JsonIgnore
	private transient Map<String, Object> paramMap = new TreeMap<String, Object>();

	@JsonIgnore
	private transient ParsedTasksDef parsedTaskDef;
	
	public Task(){
		setTtype(this.getClass().getName());
	}
	
	public Task(String name){
		this();
		this.name = name;
		this.id = genId();
	}
	
	public Task copy(Task t){
		t.setStart(this.start);
		t.setId(this.id);
		t.setStoreId(this.storeId);
		t.setName(this.name);
		t.setRootTaskId(this.rootTaskId);
		t.setTtype(this.ttype);
		t.setNextTask(this.nextTask);
		t.setLastUpdateDate(lastUpdateDate);
		t.setStartDate(startDate);
		t.setParamData(paramData);
		t.getParamMap().putAll(this.getParamMap());
		t.setParsedTaskDef(parsedTaskDef);
		t.setConfName(confName);
		return t;
	}

	public String genId(){
		String paramValues="";
		if (paramMap!=null){
			for (String key:paramMap.keySet()){
				Object val = paramMap.get(key);
				if (val!=null){
					if (val instanceof String){
						paramValues+=val.toString();
					}else if (val instanceof Boolean){
						paramValues+=val.toString();
					}else{
						logger.warn(String.format("type %s of value %s not supported to be included in the id.", val.getClass(), val.toString()));
					}
				}else{
					paramValues+="null";
				}
				paramValues+=",";
			}
		}
		return storeId + "|" + paramValues;
	}
	
	public Task clone(ClassLoader classLoader){
		try {
			Class<?> taskClass = Class.forName(Task.class.getName(), true, classLoader);
			Task t = (Task) taskClass.newInstance();
			t= copy(this);
			return t;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public boolean hasMultipleOutput(){
		return false;
	}
	
	//serialize the paramMap to json param data, only selected types (now:string,int) will be stored
	public void toParamData(){
		paramData = JsonUtil.toJsonStringFromMap(paramMap);
	}
	
	//deserialize
	public void fromParamData(){
		paramMap = JsonUtil.fromJsonStringToMap(paramData);
	}
	
	//id is enough, id is md5 of the content
	@Override
	public boolean equals(Object o){
		if (o instanceof Task){
			Task t = (Task)o;
			return StringUtils.equals(id, t.getId());
		}else{
			return false;
		}
	}
	public int hashCode(){
		return id.hashCode();
	}
	public String toString(){
		StringBuffer sb = new StringBuffer("id:" + getId() + ", name:" + getName() + ", ttype:" + getTtype());
		sb.append(", paramData:" + paramData);
		if (parsedTaskDef!=null)
			sb.append(", parsedTaskDef:" + parsedTaskDef);
		if (paramMap!=null){
			sb.append(", paramMap:");
			for (String key: paramMap.keySet()){
				sb.append("," + key + ":" + paramMap.get(key));
			}
		}
		return sb.toString();
	}
	@Override
	public int compareTo(Task o) {
		return id.compareTo(o.getId());
	}
	
	public TaskResult runMyself(Map<String, Object> params, boolean addToDB, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		logger.info("super runMyself do nothing.");
		return null;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTtype() {
		return ttype;
	}
	public void setTtype(String ttype) {
		this.ttype = ttype;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getNextTask() {
		return nextTask;
	}
	public void setNextTask(String nextTask) {
		this.nextTask = nextTask;
	}
	public boolean isStart() {
		return start;
	}
	public void setStart(boolean start) {
		this.start = start;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getRootTaskId() {
		return rootTaskId;
	}

	public void setRootTaskId(String rootTaskId) {
		this.rootTaskId = rootTaskId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	//@JsonIgnore
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	public void putParam(String key, Object val) {
		paramMap.put(key, val);
	}
	
	public void putAllParams(Map<String, Object> params){
		if (paramMap==null){
			paramMap = new TreeMap<String, Object>();
		}
		paramMap.putAll(params);
	}
	public String getParamData() {
		return paramData;
	}

	public void setParamData(String paramData) {
		this.paramData = paramData;
	}
	
	//parsed tasks definition
	public void setParsedTaskDef(ParsedTasksDef parsedTaskDef) {
		this.parsedTaskDef = parsedTaskDef;
	}

	public void setUp(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params) {
		this.storeId = tasks.getStoreId();
		ParsedTasksDef ptd = TaskMgr.getParsedTasksDef(storeId);
		if (ptd == null){
			ptd = new ParsedTasksDef();
			ptd.setUp(tasks, pluginClassLoader);
			TaskMgr.putParsedTasksDef(storeId, ptd);
		}
		this.parsedTaskDef = ptd;
	}
	@JsonIgnore
	public ParsedTasksDef getParsedTaskDef() {
		return parsedTaskDef;
	}
	@JsonIgnore
	public String[] getSkipUrls(){
		return parsedTaskDef.getSkipUrls();
	}
	@JsonIgnore
	public TasksType getTasks(){
		return parsedTaskDef.getTasks();
	}
	@JsonIgnore
	public ParsedBrowsePrd getBrowseDetailTask(String taskName){
		return parsedTaskDef.getBrowseDetailTask(taskName);
	}
	
	public ParsedTasksDef initParsedTaskDef(){
		return null;
	}
	
	public BrowseTaskType getBrowseTask(String taskName){
		logger.info("getBrowseTask taskName:" + taskName);
		return getParsedTaskDef().getBrowseTask(taskName);
	}

	public String getConfName() {
		return confName;
	}

	public void setConfName(String confName) {
		this.confName = confName;
	}
	
	public String getOutputDir(Map<String, Object> paramMap, TaskConf tconf){
		return "";
	}
}
