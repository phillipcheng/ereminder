package org.cld.taskmgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreRSClient;
import org.cld.datastore.entity.SiteConf;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamValueType;
import org.xml.taskdef.TaskInvokeType;
import org.xml.taskdef.TasksType;

import static org.cld.taskmgr.entity.Task.*;

public class TaskMgr {

	private static Logger logger =  LogManager.getLogger(TaskMgr.class);
	
	public static final String moduleName="taskMgr";
	
	//keys
	public static final String webconf_wsurl_Key="taskconf.main.url";
	public static final String useProxy_Key="use.proxy";
	public static final String proxyIP_Key="proxy.ip";
	public static final String proxyPort_Key="proxy.port";
	public static final String maxRetry_Key="retry.num";
	public static final String timeout_Key="time.out";
	
	public static final String hdfsDefaultName_Key= "hadoop.hdfs.default.name";//hadoop.hdfs.default.name=hdfs://localhost:19000
	public static final String hdfsTaskFolder_Key = "hdfs.task.folder";//hdfs.task.folder:/reminder/task
	public static final String hadoopJobTracker_Key = "hadoop.job.tracker";//hadoop.job.tracker:localhost:9001
	public static final String hadoopTaskMapperClassName_Key="task.mapper.class";
	public static final String yarnApplicationClasspath_Key="yarn.application.classpath";
	public static final String hdfsCrawledItemFolder_Key="hdfs.crawleditem.folder";
	public static final String crawlTasksPerMapper_Key = "crawl.task.per.mapper"; //number of tasks/lines each mapper will process
	
	//values
	private String webconfWSUrl = null;
	private boolean useProxy=false;
	private String proxyIP;
	private int proxyPort;
	private int timeout; //page fetch time out in seconds
	
	private String hdfsDefaultName = null;
	private String hdfsTaskFolder = null;
	private String hadoopJobTracker = null;
	private String hadoopTaskMapperClassName = null;
	private String yarnAppCp = null;
	private String hadoopCrawledItemFolder=null;
	private int crawlTasksPerMapper = 1;
	

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
	
	//max running tasks per site, this is configured for each site, restricted by the site robot policy, 0 for unlimited
	private Map<String, Integer> maxRunningTasksPerSite = new HashMap<String, Integer>();
	
	private PropertiesConfiguration properties;
	
	private String masterConfFile;
	private NodeConf nc;
	
	public TaskMgr(){
	}
	
	public static ParsedTasksDef getParsedTasksDef(String siteid){
		return parsedTaskDefMap.get(siteid);
	}
	
	public static void putParsedTasksDef(String siteid, ParsedTasksDef ptd){
		parsedTaskDefMap.put(siteid, ptd);
	}
	
	public int getMaxRunningTasks(String siteId){
		if (maxRunningTasksPerSite.containsKey(siteId)){
			return maxRunningTasksPerSite.get(siteId);
		}else{
			return 0;
		}
	}
	public void setMaxRunningTasks(String siteId, int max){
		maxRunningTasksPerSite.put(siteId, max);
	}
	
	public void setUp(String masterConfFile, NodeConf nc){
		this.masterConfFile = masterConfFile;
		this.nc = nc;
		this.nc.setTaskMgr(this);
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
		
		if (!oldTTypeConfs.containsKey(ttype.getName())){
			//newly added, fire event if needed.	
		}else{
			//compare oldTTypeConfs.get(key) and ttypeConfs.get(key) and fire updated event
		}	
	}
	
	private void loadSystemTaskType(){
		TaskTypeConf taskConf = new TaskTypeConf();
		taskConf.setName(systemTaskName);
		taskConf.setEntityImpl(systemTaskClassName);
		taskConf.setProcessImpl(null);
		taskConf.setStatImpl(systemTaskStatClassName);
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
			
			//2. 
			properties = new PropertiesConfiguration(this.masterConfFile);
			
			Iterator<String> enu = properties.getKeys();
			
			while(enu.hasNext()){
				String key = enu.next();	
				String strVal = properties.getString(key);
				logger.debug(String.format("key:%s, value:%s", key, strVal));
				if (webconf_wsurl_Key.equals(key)){
					webconfWSUrl = strVal;
				}else if (useProxy_Key.equals(key)){
					useProxy = Boolean.parseBoolean(strVal);
				}else if (proxyIP_Key.equals(key)){
					proxyIP = strVal;
				}else if (proxyPort_Key.equals(key)){
					proxyPort = Integer.parseInt(strVal);
				}else if (timeout_Key.equals(key)){
					timeout=Integer.parseInt(strVal);
				}else if (hdfsTaskFolder_Key.equals(key)){
					this.hdfsTaskFolder = strVal;
				}else if (hdfsDefaultName_Key.equals(key)){
					this.hdfsDefaultName = strVal;
				}else if (hadoopJobTracker_Key.equals(key)){
					this.hadoopJobTracker = strVal;
				}else if (hadoopTaskMapperClassName_Key.equals(key)){
					this.setHadoopTaskMapperClassName(strVal);
				}else if (hdfsCrawledItemFolder_Key.equals(key)){
					this.setHadoopCrawledItemFolder(strVal);
				}else if (crawlTasksPerMapper_Key.equals(key)){
					this.setCrawlTasksPerMapper(Integer.parseInt(strVal));
				}else if (yarnApplicationClasspath_Key.equals(key)){
					List<Object> listVal = properties.getList(key);
					strVal="";
					for (int i=0; i<listVal.size(); i++){
						if (i>0){
							strVal +=",";
						}
						strVal += listVal.get(i);
					}
					this.setYarnAppCp(strVal);
				}else if (taskType_Key.equals(key)){
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
					//fire deleted event
					its = oldTTypeConfs.keySet().iterator();
					while (its.hasNext()){
						String ttypeName = its.next();
						if (!ttypeConfs.containsKey(ttypeName)){
							//fire task type deleted event if needed
						}
					}	
				}else if (taskName_Key.equals(key)){
					//load task from files
					List<Object> listVal = properties.getList(key);
					logger.debug(String.format("key:%s, value:%s, listVal.size():%d", key, listVal, listVal.size()));
					if (!"".equals(strVal.trim())){
						for (int i=0;  i<listVal.size(); i++){
							String tname = (String)listVal.get(i);
							//no ctconf defined in properties, it is v2, try to parse the xml pointed by tname
							if (setUpSite(tname, null, pluginClassLoader, params).size()==0){
								logger.error("task type not found:" + tname + "." + Task.taskType_Key);
							}
						}
					}
				}
				
				//task.name=, get the task definition from web service
				if (webconfWSUrl!=null){
					logger.info("get siteconf from ws.");
					DataStoreRSClient dsrsclient = null;
					if (useProxy){
						dsrsclient = new DataStoreRSClient(webconfWSUrl, proxyIP, proxyPort, timeout);
					}else{
						dsrsclient = new DataStoreRSClient(webconfWSUrl, timeout);
					}
					List<SiteConf> scl = dsrsclient.getDeployedSiteConf();
					if (scl!=null){
						logger.info(String.format("siteconf got: %d", scl.size()));
						for (SiteConf sc: scl){
							setUpSite(null, sc, pluginClassLoader, params);
						}
					}else{
						logger.error("no deployed siteconf found.");
					}
				}
			}
			
			for (String key: oldTasksConf.keySet()){
				if (!tasksConf.containsKey(key)){
					NodeConfPropChangedEvent ncpce = new NodeConfPropChangedEvent();
					ncpce.setPropName(TaskMgr.taskName_Key);
					ncpce.setOpType(NodeConfPropChangedEvent.OP_REMOVE);
					ncpce.setStrValue(key);
					nc.fireNCPCEvent(ncpce);
					logger.info("fire remove task event:" + ncpce);
				}
			}
			for (String key : tasksConf.keySet()){
				Task t = getTask(key);
				if (!oldTasksConf.containsKey(key)){
					NodeConfPropChangedEvent ncpce = new NodeConfPropChangedEvent();
					ncpce.setPropName(TaskMgr.taskName_Key);
					ncpce.setOpType(NodeConfPropChangedEvent.OP_ADD);
					Task newTask = t.clone(pluginClassLoader);
					ncpce.setObjectValue(newTask);					
					nc.fireNCPCEvent(ncpce);
					logger.info("fire add task event:" + ncpce);
				}else{
					Task oldT = oldTasksConf.get(key);
					if (oldT.getLastUpdateDate().before(t.getLastUpdateDate())){
						//fire update
						NodeConfPropChangedEvent ncpce = new NodeConfPropChangedEvent();
						ncpce.setPropName(TaskMgr.taskName_Key);
						ncpce.setOpType(NodeConfPropChangedEvent.OP_UPDATE);
						Task newTask = t.clone(pluginClassLoader);
						ncpce.setObjectValue(newTask);	
						ncpce.setOldObjValue(oldT);
						nc.fireNCPCEvent(ncpce);
						logger.info("fire update task event:" + ncpce);
					}
				}
			}
			//
		}catch(Exception e){
			logger.error("exception while read properties.", e);
		}
	}
	

	public static final String taskParamTaskIndex="taskindex"; //the index of the specific task
	
	public Task getTaskInstTemplate(String taskType, TasksType tasks, ClassLoader pluginClassLoader, 
			Map<String, Object> params, Date utime, String taskName){
		try{
			TaskTypeConf ttypeConf = ttypeConfs.get(taskType);
			Task taskInstTemplate = ttypeConf.getTaskEntityClass().newInstance();
			taskInstTemplate.setName(taskName);
			taskInstTemplate.setLastUpdateDate(utime);
			taskInstTemplate.setTtype(taskType);
			taskInstTemplate.setUp(tasks, pluginClassLoader, params);
			taskInstTemplate.putAllParams(params);
			taskInstTemplate.genId();
			return taskInstTemplate;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	private List<Task> loadTaskV2(TasksType tasks, ClassLoader pluginClassLoader, Map<String, Object> params, Date utime){
		List<Task> tl = new ArrayList<Task>();
		if (tasks.getInvokeTask().size()>0){
			for (int i=0; i<tasks.getInvokeTask().size(); i++){
				TaskInvokeType tit = tasks.getInvokeTask().get(i);
				String taskType = "org.cld.datacrawl.task.InvokeTaskTaskConf";
				params.put(taskParamTaskIndex, i);
				Task invokeTask=getTaskInstTemplate(taskType, tasks, pluginClassLoader, params, utime, tit.getMyTaskName());
				if (invokeTask!=null){
					invokeTask.setStart(true);
					tasksConf.put(invokeTask.getName(), invokeTask);
					tl.add(invokeTask);
				}
			}
		}else if (tasks.getCatTask().size()>0){
			String siteconfid = tasks.getStoreId();
			String taskType = "org.cld.datacrawl.task.BrowseCategoryTaskConf";
			Task bctTask = getTaskInstTemplate(taskType, tasks, pluginClassLoader, params, utime, siteconfid + "_bct");
			if (bctTask!=null){
				if (tasks.getCatTask().get(0).getBaseBrowseTask().getRerunInterim()==null){
					//do not set
				}else{
					bctTask.setRerunInterim(tasks.getCatTask().get(0).getBaseBrowseTask().getRerunInterim());
				}
				bctTask.setStart(true);
				bctTask.setNextTask(siteconfid + "_bdt");
				tasksConf.put(bctTask.getName(), bctTask);
				tl.add(bctTask);
			}
			taskType = "org.cld.datacrawl.task.BrowseDetailTaskConf";
			Task bdtTask = getTaskInstTemplate(taskType, tasks, pluginClassLoader, params, utime, siteconfid + "_bdt");
			if (bdtTask!=null){
				bdtTask.setStart(false);
				tasksConf.put(bdtTask.getName(), bdtTask);
				tl.add(bdtTask);
			}
			taskType = "org.cld.datacrawl.task.BrowseProductTaskConf";
			for (BrowseDetailType bpt: tasks.getPrdTask()){
				Task prdTask = getTaskInstTemplate(taskType, tasks, pluginClassLoader, params, utime, bpt.getBaseBrowseTask().getTaskName());
				if (prdTask!=null){
					prdTask.setStart(bpt.getBaseBrowseTask().isIsStart());
					tasksConf.put(prdTask.getName(), prdTask);
					tl.add(prdTask);
				}
			}
			this.setMaxRunningTasks(tasks.getStoreId(), tasks.getMaxThread());
		}else if (tasks.getPrdTask()!=null){
			String taskType = "org.cld.datacrawl.task.BrowseProductTaskConf";
			for (BrowseDetailType bpt: tasks.getPrdTask()){
				Task prdTask = getTaskInstTemplate(taskType, tasks, pluginClassLoader, params, utime, bpt.getBaseBrowseTask().getTaskName());
				if (prdTask!=null){
					prdTask.setStart(bpt.getBaseBrowseTask().isIsStart());
					tasksConf.put(prdTask.getName(), prdTask);
					tl.add(prdTask);
				}
			}
		}else{
			logger.error("not supported tasks def:" + tasks);
		}
		
		return tl;
	}
	
	//to convert the xml definition to tasksConf within cconf
	public List<Task> setUpSite(String taskconfFileName, SiteConf sc, ClassLoader pluginClassLoader, Map<String, Object> params){
		//this will create 2 tasks, 1 bct, 1 bdt
		try {
			Source source = null;
			URL url = null;
			Date d = null;
			if (sc==null){
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
			}else{
				source = new StreamSource(new StringReader(sc.getConfxml()));
				d = sc.getUtime();
			}
			JAXBContext jc = JAXBContext.newInstance("org.xml.taskdef");
			Unmarshaller u = jc.createUnmarshaller();
			
			JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
			TasksType tasks = root.getValue();
			return loadTaskV2(tasks, pluginClassLoader, params, d);
		}catch(Exception e){
			logger.error("", e);
			return new ArrayList<Task>();
		}
	}
	
	public String getHdfsTaskFolder() {
		return hdfsTaskFolder;
	}

	public void setHdfsTaskFolder(String hdfsTaskFolder) {
		this.hdfsTaskFolder = hdfsTaskFolder;
	}

	public String getHdfsDefaultName() {
		return hdfsDefaultName;
	}

	public void setHdfsDefaultName(String hdfsDefaultName) {
		this.hdfsDefaultName = hdfsDefaultName;
	}

	public String getHadoopJobTracker() {
		return hadoopJobTracker;
	}

	public void setHadoopJobTracker(String hadoopJobTracker) {
		this.hadoopJobTracker = hadoopJobTracker;
	}

	public String getHadoopTaskMapperClassName() {
		return hadoopTaskMapperClassName;
	}

	public void setHadoopTaskMapperClassName(String hadoopTaskMapperClassName) {
		this.hadoopTaskMapperClassName = hadoopTaskMapperClassName;
	}

	public String getYarnAppCp() {
		return yarnAppCp;
	}

	public void setYarnAppCp(String yarnAppCp) {
		this.yarnAppCp = yarnAppCp;
	}

	public String getHadoopCrawledItemFolder() {
		return hadoopCrawledItemFolder;
	}

	public void setHadoopCrawledItemFolder(String hadoopCrawledItemFolder) {
		this.hadoopCrawledItemFolder = hadoopCrawledItemFolder;
	}

	public int getCrawlTasksPerMapper() {
		return crawlTasksPerMapper;
	}

	public void setCrawlTasksPerMapper(int crawlTasksPerMapper) {
		this.crawlTasksPerMapper = crawlTasksPerMapper;
	}
}
