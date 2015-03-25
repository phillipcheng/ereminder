package org.cld.datacrawl.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.taskdef.ParamValueType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.TasksType;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;


@Entity
@DiscriminatorValue("org.cld.datacrawl.task.InvokeTaskTaskConf")
public class InvokeTaskTaskConf extends Task implements Serializable{
	private static final long serialVersionUID = 1L;

	private static Logger logger =  LogManager.getLogger(InvokeTaskTaskConf.class);

	private String refTaskName;

	private String paramsListData; //paramsList in json format to store
	//internal variables
	private transient TreeMap<String,Object> paramsMap = new TreeMap<String,Object>();
	//methods
	public InvokeTaskTaskConf(){
	}
	
	public boolean equals(Object obj){
		if (obj instanceof InvokeTaskTaskConf){
			InvokeTaskTaskConf inTask = (InvokeTaskTaskConf)obj;
			return super.equals(obj) && 
					StringUtils.equals(refTaskName, inTask.getRefTaskName());
		}else{
			return false;
		}
	}
	public String toString(){
		StringBuffer sb = new StringBuffer(super.toString() + ", ");
		sb.append("refTaskName:" + refTaskName + ", " 
				+ "paramsListData:" + paramsListData);
		return sb.toString();
	}
	
	@Override
	public InvokeTaskTaskConf clone(ClassLoader classLoader){
		try {
			Class<?> taskClass = Class.forName(InvokeTaskTaskConf.class.getName(), true, classLoader);
			InvokeTaskTaskConf te = (InvokeTaskTaskConf) taskClass.newInstance();
			copy(te);
			return te;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
	
	public String genId(){
		this.setId(super.genId());
		return this.getId();
	}

	//from xml to Task object
	@Override
	public void setUp(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params) {
		super.setUp(tasks, pluginClassLoader, params);
		this.setStoreId(tasks.getStoreId());
		int idx = (Integer)params.get(TaskMgr.taskParamTaskIndex);
		TaskInvokeType tit = tasks.getInvokeTask().get(idx);
		this.refTaskName = tit.getToCallTaskName();
		for (ParamValueType pvt:tit.getParam()){
			paramsMap.put(pvt.getParamName(), pvt.getValue());
		}
	}
	
	@Override
	public List<Task> runMyself(Map<String, Object> params, TaskStat ts) throws InterruptedException{
		CrawlConf cconf = (CrawlConf) params.get(CrawlClientNode.TASK_RUN_PARAM_CCONF);
		List<Task> tl = new ArrayList<Task>();
		Task refTask = cconf.getTaskMgr().getTask(this.refTaskName);
		if (refTask==null){
			logger.error(String.format("%s is not setup yet.", this.refTaskName));
		}else{
			Task taskInst = refTask.clone(cconf.getPluginClassLoader());
			taskInst.setStart(true);
			taskInst.putAllParams(paramsMap);
			taskInst.genId();
			tl.add(taskInst);
		}
		return tl;
	}
	
	public String getRefTaskName() {
		return refTaskName;
	}
	public void setRefTaskName(String refTaskName) {
		this.refTaskName = refTaskName;
	}
}
