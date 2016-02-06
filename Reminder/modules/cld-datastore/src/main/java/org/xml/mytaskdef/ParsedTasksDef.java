package org.xml.mytaskdef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.RedirectType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ParsedTasksDef {

	private static Logger logger =  LogManager.getLogger(ParsedTasksDef.class);
	
	transient Map<String, BrowseTaskType> allBrowseTasks = new HashMap<String, BrowseTaskType>();
	transient TasksType tasksDef;
	transient String[] skipUrls;
	transient ParsedBrowsePrd defaultBrowsePrdTask;//browse product
	transient Map<String, ParsedBrowsePrd> browsePrdTaskMap = new HashMap<String, ParsedBrowsePrd>();
	//from landing url to loginClickStream
	transient Map<String, ClickStreamType> landingUrlMap = new HashMap<String, ClickStreamType>();
	//from stream name to loginClickStream
	transient Map<String, ClickStreamType> clickStreamMap = new HashMap<String, ClickStreamType>();
	
	
	public ClickStreamType getLoginClickStream(String landingUrl){
		for (String key: landingUrlMap.keySet()){
			if (landingUrl.startsWith(key)){
				return landingUrlMap.get(key);
			}
		}
		return null;
	}
	
	public ClickStreamType getLoginClickStream(HtmlPage page){
		List<RedirectType> redirectedUrls = tasksDef.getLoginInfo().getRedirectedURL();
		for (RedirectType rt: redirectedUrls){
			if (rt.getLanding().getFromType()==VarType.XPATH){
				if (page.getFirstByXPath(rt.getLanding().getValue())!=null){
					return clickStreamMap.get(rt.getClickstream());
				}
			}
		}
		return null;
	}
	
	public void setUp(TasksType tasks, ClassLoader pluginClassLoader) {
		this.tasksDef = tasks;
		
		List<BrowseDetailType> bptList = tasks.getPrdTask();
		for (int i=0; i<bptList.size(); i++){
			BrowseDetailType bpt = bptList.get(i);
			ParsedBrowsePrd pbpt = new ParsedBrowsePrd(bpt);
			if (i==0){
				defaultBrowsePrdTask = pbpt;
			}
			if (bpt.getBaseBrowseTask().getTaskName()!=null)
				browsePrdTaskMap.put(bpt.getBaseBrowseTask().getTaskName(), pbpt);
			else{
				if (bptList.size()>1)
					logger.error(String.format("browse prd task name not specified among a list of prd tasks for site: %s", tasks.getStoreId()));
			}
			allBrowseTasks.put(bpt.getBaseBrowseTask().getTaskName(), bpt.getBaseBrowseTask());
		}
		
		skipUrls = new String[tasks.getSkipUrl().size()];
		tasks.getSkipUrl().toArray(skipUrls);
		
		if (tasks.getLoginInfo()!=null){
			//setup the click stream map
			List<ClickStreamType> loginClickStreams = tasks.getLoginInfo().getLoginClickStream();
			for (ClickStreamType cst: loginClickStreams){
				clickStreamMap.put(cst.getName(), cst);
			}
			//setup the login click stream map
			List<RedirectType> redirectedUrls = tasks.getLoginInfo().getRedirectedURL();
			for (RedirectType rt: redirectedUrls){
				if (rt.getLanding().getFromType()==VarType.URL){
					landingUrlMap.put(rt.getLanding().getValue(), clickStreamMap.get(rt.getClickstream()));
				}
			}
		}
	}
	
	public String[] getSkipUrls(){
		return skipUrls;
	}
	
	public TasksType getTasks(){
		return tasksDef;
	}

	public ParsedBrowsePrd getDefaultBrowseDetailTask(){
		return defaultBrowsePrdTask;
	}
	
	public ParsedBrowsePrd getBrowseDetailTask(String taskName){
		if (taskName==null || !browsePrdTaskMap.containsKey(taskName)){
			return defaultBrowsePrdTask;
		}else{
			return browsePrdTaskMap.get(taskName);
		}
	}
	
	public BrowseTaskType getBrowseTask(String taskName){
		//if taskName is null, usually from TestTask return first
		if (taskName==null || !allBrowseTasks.containsKey(taskName)){
			return (BrowseTaskType) allBrowseTasks.values().toArray()[0];
		}else{
			return allBrowseTasks.get(taskName);
		}
	}
	
	public Map<String, ParsedBrowsePrd> getBrowsePrdTaskMap(){
		return browsePrdTaskMap;
	}
}
