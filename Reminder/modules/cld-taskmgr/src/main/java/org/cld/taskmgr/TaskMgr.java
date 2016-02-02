package org.cld.taskmgr;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.entity.SiteConf;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.TasksType;

public class TaskMgr {

	private static Logger logger =  LogManager.getLogger(TaskMgr.class);
	
	public static final String moduleName="taskMgr";

	public static final String taskType_Key = "task.type";
	public static final String taskName_Key="task.name";
	
	public static final String systemTaskName="task";
	public static final String systemTaskClassName="org.cld.taskmgr.entity.Task";
	public static final String systemTaskStatClassName="org.cld.taskmgr.entity.TaskStat";
	
	
	private HashMap<String, TaskTypeConf> ttypeConfs = new HashMap<String, TaskTypeConf>();//conf for each task type
	private HashMap<String, TaskTypeConf> oldTTypeConfs = new HashMap<String, TaskTypeConf>();//conf before reload
	
	private HashMap<String, Task> tasksConf = new HashMap<String, Task>();//task conf after reload
	private HashMap<String, Task> oldTasksConf = new HashMap<String, Task>(); //task conf before reload
	
	private static Map<String, ParsedTasksDef> parsedTaskDefMap = new ConcurrentHashMap<String, ParsedTasksDef>();
	
	private String masterConfFile;
	private Map<String, List<Task>> confTaskMap = new HashMap<String, List<Task>>();
	
	public static final String TASK_RUN_PARAM_CCONF="cconf";
	
	public TaskMgr(){
	}
	
	public static ParsedTasksDef getParsedTasksDef(String siteid){
		return parsedTaskDefMap.get(siteid);
	}
	
	public static void putParsedTasksDef(String siteid, ParsedTasksDef ptd){
		parsedTaskDefMap.put(siteid, ptd);
	}
	
	public void setUp(String masterConfFile){
		this.masterConfFile = masterConfFile;
	}
	
	public Task getTask(String taskName){
		return tasksConf.get(taskName);
	}
	
	public Collection<TaskTypeConf> getAllTaskTypes(){
		return ttypeConfs.values();
	}
	
	public Collection<Task> getAllTasks(){
		return tasksConf.values();
	}
	
	public static boolean isSystemTask(String name){
		if ("end".equals(name)){
			return true;
		}else{
			return false;
		}
	}
	
	public TaskTypeConf getTaskType(String taskType){
		TaskTypeConf ttconf = ttypeConfs.get(taskType);
		if (ttconf == null){
			logger.error("taskType not found:" + taskType);
		}
		return ttconf;
	}
	
	/**
	 * startable task includes
	 * 1. 
	 * @return
	 */
	public List<Task> getStartableTasks(){
		List<Task> tl = new ArrayList<Task>();
		Iterator<Task> it = tasksConf.values().iterator();
		while (it.hasNext()){
			Task t = it.next();
			if (t.isStart()){
				tl.add(t);
			}
		}
		return tl;
	}
	
	/**
	 * load specific task type
	 * @param key: task type
	 * @return
	 */
	private void loadTaskType(TaskTypeConf ttype, ClassLoader pluginClassLoader){
		try {
			ttype.setTaskEntityClass((Class<Task>) Class.forName(ttype.getEntityImpl(), true, pluginClassLoader));
		} catch (ClassNotFoundException e) {
			logger.error("entity class not found for:" + ttype, e);
		}
	
		try {
			ttype.setTaskStatClass((Class<TaskStat>) Class.forName(ttype.getStatImpl(), true, pluginClassLoader));
		} catch (ClassNotFoundException e) {
			logger.error("task stat class not found for:" + ttype, e);
		}
		
		ttypeConfs.put(ttype.getName(), ttype);	
	}
	
	private void loadSystemTaskType(){
		TaskTypeConf taskConf = new TaskTypeConf();
		taskConf.setName(systemTaskName);
		taskConf.setEntityImpl(systemTaskClassName);
		taskConf.setProcessImpl(null);
		taskConf.setStatImpl(systemTaskStatClassName);
	}
	//
	public void loadConf(String propFile, ClassLoader pluginClassLoader, Map<String, Object> params){
		try{
			if (params == null){
				params = new HashMap<String, Object>();
			}
			PropertiesConfiguration properties = new PropertiesConfiguration(propFile);
			
			Iterator<String> enu = properties.getKeys();
			
			while(enu.hasNext()){
				String key = enu.next();	
				String strVal = properties.getString(key);
				logger.debug(String.format("key:%s, value:%s", key, strVal));
				if (taskType_Key.equals(key)){
					//load task type
					List<Object> listVal = properties.getList(key);
					for (int i=0;  i<listVal.size(); i++){
						String tt = (String)listVal.get(i);
						TaskTypeConf taskConf = new TaskTypeConf();
						taskConf.setName(tt);
						taskConf.setEntityImpl(properties.getString(tt + "." + TaskTypeConf.taskEntity_Key));
						taskConf.setProcessImpl(properties.getString(tt + "." + TaskTypeConf.taskProcess_Key));
						taskConf.setStatImpl(properties.getString(tt + "." + TaskTypeConf.taskStat_Key));
						loadTaskType(taskConf, pluginClassLoader);
						logger.debug("taskconf put:" + tt + ", " + taskConf);
					}
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	/**
	 * read task type conf and task conf from configuration
	 * task type conf must come before task conf
	 */
	public void reload(ClassLoader pluginClassLoader, Map<String, Object> params){
		try{	
			//1. backup to oldTTypeConfs
			oldTTypeConfs.clear();
			Iterator<String> its = ttypeConfs.keySet().iterator();
			while (its.hasNext()){
				String key = its.next();
				oldTTypeConfs.put(key, ttypeConfs.get(key));
			}
			ttypeConfs.clear();
			loadSystemTaskType();
			
			//1. backup to oldTasksConf
			oldTasksConf.clear();
			its = tasksConf.keySet().iterator();
			while (its.hasNext()){
				String key = its.next();
				oldTasksConf.put(key, tasksConf.get(key));
			}
			tasksConf.clear();
			
			//2. load conf
			loadConf(this.masterConfFile, pluginClassLoader, params);
			//
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}
	

	public static final String taskParamTaskIndex="taskindex"; //the index of the specific task
	
	//
	private Task getTaskInstance(String taskType, TasksType tasks, ClassLoader pluginClassLoader, 
			Map<String, Object> params, Date utime, String taskName){
		try{
			TaskTypeConf ttypeConf = ttypeConfs.get(taskType);
			Task taskInstance = ttypeConf.getTaskEntityClass().newInstance();
			taskInstance.setName(taskName);
			taskInstance.setLastUpdateDate(utime);
			taskInstance.setTtype(taskType);
			taskInstance.setUp(tasks, pluginClassLoader, params);
			if (params!=null)
				taskInstance.putAllParams(params);
			taskInstance.genId();
			return taskInstance;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	private List<Task> loadTask(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params, Date utime, String confName){
		List<Task> tl = new ArrayList<Task>();
		if (tasks.getInvokeTask().size()>0){
			for (int i=0; i<tasks.getInvokeTask().size(); i++){
				TaskInvokeType tit = tasks.getInvokeTask().get(i);
				String taskType = "org.cld.datacrawl.task.InvokeTaskTaskConf";
				params.put(taskParamTaskIndex, i);
				Task invokeTask=getTaskInstance(taskType, tasks, pluginClassLoader, params, utime, tit.getMyTaskName());
				if (invokeTask!=null){
					invokeTask.setStart(true);
					tasksConf.put(invokeTask.getName(), invokeTask);
					tl.add(invokeTask);
					logger.info("invoke Task loaded:" + invokeTask);
				}
			}
		}
		
		if (tasks.getCatTask().size()>0){//create the default confid_bct->confid_bdt chain
			String siteconfid = tasks.getStoreId();
			String taskType = "org.cld.datacrawl.task.BrowseCategoryTaskConf";
			Task bctTask = getTaskInstance(taskType, tasks, pluginClassLoader, params, utime, siteconfid + "_bct");
			if (bctTask!=null){
				bctTask.setStart(true);
				bctTask.setNextTask(siteconfid + "_bdt");
				tasksConf.put(bctTask.getName(), bctTask);
				tl.add(bctTask);
				logger.info("BCT Task loaded:" + bctTask);
			}
			taskType = "org.cld.datacrawl.task.BrowseDetailTaskConf";
			Task bdtTask = getTaskInstance(taskType, tasks, pluginClassLoader, params, utime, siteconfid + "_bdt");
			if (bdtTask!=null){
				bdtTask.setStart(false);
				tasksConf.put(bdtTask.getName(), bdtTask);
				tl.add(bdtTask);
				logger.info("BDT Task loaded:" + bdtTask);
			}
		}
		
		if (tasks.getPrdTask()!=null){
			String taskType = "org.cld.datacrawl.task.BrowseProductTaskConf";
			for (BrowseDetailType bpt: tasks.getPrdTask()){
				Task prdTask = getTaskInstance(taskType, tasks, pluginClassLoader, params, utime, bpt.getBaseBrowseTask().getTaskName());
				if (prdTask!=null){
					prdTask.setStart(bpt.getBaseBrowseTask().isIsStart());
					tasksConf.put(prdTask.getName(), prdTask);
					tl.add(prdTask);
					logger.info("Prd Task loaded:" + prdTask);
				}
			}
		}
		List<Task> retTL = new ArrayList<Task>();
		for (Task t: tl){
			t.setConfName(confName);
			if (t.isStart()){
				retTL.add(t);
			}
		}
		return retTL;
	}
	
	//setup with the content of the crawl conf file, used by web server, using sc.id as the key
	public List<Task> setUpSite(SiteConf sc, ClassLoader pluginClassLoader, Map<String, Object> params){
		String id = sc.getId();
		if (confTaskMap.containsKey(id)){
			return confTaskMap.get(id);
		}try {
			Source source = new StreamSource(new StringReader(sc.getConfxml()));
			Date d = sc.getUtime();
			JAXBContext jc = JAXBContext.newInstance("org.xml.taskdef");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
			TasksType tasks = root.getValue();
			List<Task> tl = loadTask(tasks, pluginClassLoader, params, d, id);
			confTaskMap.put(id, tl);
			return tl;
		}catch(Exception e){
			logger.error("", e);
			return new ArrayList<Task>();
		}
	}
	
	//setup with the crawl conf file, using the fileName as the key
	public List<Task> setUpSite(String taskconfFileName, ClassLoader pluginClassLoader, Map<String, Object> params){
		if (confTaskMap.containsKey(taskconfFileName)){
			return confTaskMap.get(taskconfFileName);
		}else{
			try {
				Source source = null;
				URL url = null;
				Date d = null;
				if (pluginClassLoader!=null){
					source = new StreamSource(pluginClassLoader.getResourceAsStream(taskconfFileName));
					url = pluginClassLoader.getResource(taskconfFileName);
				}else{
					source = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(taskconfFileName));
					url = Thread.currentThread().getContextClassLoader().getResource(taskconfFileName);
				}
				String fileName=null;
				if (url!=null){
					if (url.getProtocol().equals("file")) {
				        fileName = url.getFile();
				    }else if (url.getProtocol().equals("jar")){
				    	fileName = url.getFile();
				    	fileName = fileName.substring(0, fileName.indexOf("!"));
				    }else {
				        throw new IllegalArgumentException(String.format("can't get the timestamp from protocol: %s", url.getProtocol()));
				    }
				    File file = new File(fileName);
				    d = new Date(file.lastModified());
				}else{
					logger.error(String.format("task conf file %s not found.", taskconfFileName));
				}
				
				JAXBContext jc = JAXBContext.newInstance("org.xml.taskdef");
				Unmarshaller u = jc.createUnmarshaller();
				
				JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
				TasksType tasks = root.getValue();
				List<Task> tl = loadTask(tasks, pluginClassLoader, params, d, taskconfFileName);
				confTaskMap.put(taskconfFileName, tl);
				return tl;
			}catch(Exception e){
				logger.error("", e);
				return new ArrayList<Task>();
			}
		}
	}
}
