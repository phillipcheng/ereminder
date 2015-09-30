package org.xml.mytaskdef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.RedirectType;
import org.xml.taskdef.RegExpType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ParsedTasksDef {

	private static Logger logger =  LogManager.getLogger(ParsedTasksDef.class);
	
	transient Map<String, BrowseTaskType> allBrowseTasks = new HashMap<String, BrowseTaskType>();
	transient Map<String, ParsedBrowseCat> parsedBCMap = new HashMap<String, ParsedBrowseCat>();
	transient TasksType tasksDef;
	transient BrowseCatType leafBrowseCatTask;
	transient BrowseCatType rootBrowseCatTask;
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
		int size = tasks.getCatTask().size();
		assert(size>=1);
		for (int i=0; i<size; i++){
			BrowseCatType bc = tasks.getCatTask().get(i);
			RegExpType ret = bc.getBaseBrowseTask().getIdUrlMapping();
			IdUrlMapping ium = new IdUrlMapping(ret);
			ret = bc.getBaseBrowseTask().getIdUrlMappingFirstPage();
			IdUrlMapping iumFirstPage = null;
			if (ret!=null){
				iumFirstPage = new IdUrlMapping(ret);
			}
			ParsedBrowseCat pbc = new ParsedBrowseCat(ium, iumFirstPage, bc);
			//do not need name, just use index i
			parsedBCMap.put(i+"", pbc);
			logger.info("ium is:" + ium);
			logger.info("iumFirstPage is:" + iumFirstPage);
			
			if (i==0){
				rootBrowseCatTask = bc;
			}
			if (i==size-1){
				leafBrowseCatTask = bc;
			}
			allBrowseTasks.put(bc.getBaseBrowseTask().getTaskName(), bc.getBaseBrowseTask());
		}
		
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
	

	
	public BrowseCatType getRootBrowseCatTask(){
		return rootBrowseCatTask;
	}
	public BrowseCatType getLeafBrowseCatTask(){
		return leafBrowseCatTask;
	}
	
	public String[] getSkipUrls(){
		return skipUrls;
	}
	
	//get browse cat instance
	public BrowseCatInst getBCI(String url){
		for (String key:parsedBCMap.keySet()){
			ParsedBrowseCat pbc = parsedBCMap.get(key);
			IdUrlMapping ium = pbc.getIum();
			Matcher matcher = ium.match(url);
			if (matcher.find()){
				BrowseCatInst bci = new BrowseCatInst(pbc, ium, matcher);
				return bci;
			}else{
				if (pbc.getIumFirstPage()!=null){
					ium = pbc.getIumFirstPage();
					matcher = ium.match(url);
					if (matcher.find()){
						BrowseCatInst bci = new BrowseCatInst(pbc, ium, matcher);
						return bci;
					}
				}
			}
		}
		logger.error(String.format("url %s can't be matched to a category url", url));
		return null;
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
