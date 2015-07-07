package org.cld.taskmgr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskMgr;
import org.h2.util.StringUtils;
import org.json.JSONObject;
import org.xml.mytaskdef.BrowseCatInst;
import org.xml.mytaskdef.IdUrlMapping;
import org.xml.mytaskdef.ParsedBrowseCat;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.TasksType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="ttype",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("task")
@Table(name="TASK")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements Comparable<Task>, Serializable{
	public static final String TASK_KEY = "Task";
	public static final String TASK_ATTR_NAME = "taskName";
	public static final String TASK_ATTR_TYPE = "xsi:type";
	public static final String TASK_ATTR_RERUN = "rerunInterim";
	public static final String TASK_ATTR_STARTURL = "startUrl";
	public static final String TASK_ATTR_IDURL = "idUrlMapping";
	
	private static Logger logger =  LogManager.getLogger(Task.class);
	private static final long serialVersionUID = 1L;
	public static final String startTask_Key="start";
	public static final String taskType_Key="task.type";
	public static final String taskRerunInterim_Key="rerun.interim";
	public static final String next_Key="next";
	
	public static final String END_TASK="end";
	
	private boolean start=false; //true for start task
	@Id
	private String id;	//run time instance id, to be defined by sub-class usually
	private String storeId;
	private String name; //task name
	private String rootTaskId;
	@Column(insertable=false, updatable=false, length = 100)
	private String ttype="task"; //task type
	private String nextTask = END_TASK;
	private int rerunInterim;//minutes waited before reschedule this task, for rerunable task, if not specified means "at once"
	private Date lastUpdateDate;
	private Date startDate;
	private String nodeId;//assigned to which node to execute
	private String paramData;

	@JsonIgnore
	private transient TreeMap<String, Object> paramMap = new TreeMap<String, Object>();

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

	public String genId(){
		String paramValues="";
		if (paramMap!=null){
			for (String key:paramMap.keySet()){
				Object val = paramMap.get(key);
				if (val!=null){
					if (val instanceof String){
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
	
	public Task copy(Task t){
		t.setId(this.id);
		t.setStart(this.start);
		t.setName(this.name);
		t.setTtype(this.ttype);
		t.setNextTask(this.nextTask);
		t.setRerunInterim(rerunInterim);
		t.setStoreId(this.storeId);
		return t;
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
	
	//serialize the paramMap to json param data, only selected types (now:string,int) will be stored
	public void toParamData(){
		JSONObject jobj = new JSONObject();
		if (paramMap!=null){
			for (String key: paramMap.keySet()){
				Object val = paramMap.get(key);
				if (val instanceof String){
					jobj.put(key, (String)val);
				}else if (val instanceof Integer){
					jobj.put(key, val);
				}else{
					logger.warn(String.format("unsupported type for paramMap json serialization. key:%s, value:%s.",
							key, val));
				}
			}
		}
		paramData = jobj.toString();
	}
	//deserialize
	public void fromParamData(){
		if (paramData!=null){
			try{
				JSONObject jobj = new JSONObject(paramData);
				String[] names = JSONObject.getNames(jobj);
				if (names!=null){
					for (String name:names){
						paramMap.put(name, jobj.opt(name));
					}
				}
			}catch(Exception e){
				logger.error("the paramData is:" + paramData, e);
			}
		}
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
		StringBuffer sb = new StringBuffer("id:" + getId() + ", name:" + getName() + ", ttype:" + getTtype() + 
				", rerunInterim:" + this.getRerunInterim());
		sb.append(", paramData:" + paramData);
		if (parsedTaskDef!=null)
			sb.append(", parsedTaskDef:" + parsedTaskDef);
		sb.append(", paramMap:");
		for (String key: paramMap.keySet()){
			sb.append("," + key + ":" + paramMap.get(key));
		}
		return sb.toString();
	}
	@Override
	public int compareTo(Task o) {
		return id.compareTo(o.getId());
	}
	
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		logger.info("super runMyself do nothing.");
		return new ArrayList<Task>();
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
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNextTask() {
		return nextTask;
	}
	public void setNextTask(String nextTask) {
		this.nextTask = nextTask;
	}
	public int getRerunInterim() {
		return rerunInterim;
	}
	public void setRerunInterim(int rerunInterim) {
		this.rerunInterim = rerunInterim;
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
	@JsonIgnore
	public TreeMap<String, Object> getParamMap() {
		return paramMap;
	}
	public void putParam(String key, Object val) {
		paramMap.put(key, val);
	}
	
	public void putAllParams(Map<String, Object> params){
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
			ptd.setUp(tasks, pluginClassLoader, params);
			TaskMgr.putParsedTasksDef(storeId, ptd);
		}
		this.parsedTaskDef = ptd;
	}
	@JsonIgnore
	public ParsedTasksDef getParsedTaskDef() {
		return parsedTaskDef;
	}
	@JsonIgnore
	public BrowseCatType getRootBrowseCatTask(){
		return parsedTaskDef.getRootBrowseCatTask();
	}
	@JsonIgnore
	public BrowseCatType getLeafBrowseCatTask(){
		return parsedTaskDef.getLeafBrowseCatTask();
	}
	@JsonIgnore
	public String[] getSkipUrls(){
		return parsedTaskDef.getSkipUrls();
	}
	@JsonIgnore
	public BrowseCatInst getBCI(String url){
		return parsedTaskDef.getBCI(url);
	}
	@JsonIgnore
	public TasksType getTasks(){
		return parsedTaskDef.getTasks();
	}
	@JsonIgnore
	public ParsedBrowsePrd getBrowseDetailTask(String taskName){
		return parsedTaskDef.getBrowseDetailTask(taskName);
	}
}
