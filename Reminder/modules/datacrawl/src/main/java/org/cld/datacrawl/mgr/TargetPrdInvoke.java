package org.cld.datacrawl.mgr;

import java.util.HashMap;
import java.util.Map;

public class TargetPrdInvoke {
	
	private String taskName;
	private String startUrl; //start url for the detailed prd
	private Map<String, Object> inParams = new HashMap<String, Object>(); //in params for the detailed prd
	
	public TargetPrdInvoke(String fullUrl, Map<String, Object> inParams){
		this.startUrl = fullUrl;
		this.inParams.putAll(inParams);
	}
	public TargetPrdInvoke(String taskName, String fullUrl, Map<String, Object> inParams){
		this(fullUrl, inParams);
		this.taskName = taskName;
	}
	
	public TargetPrdInvoke(String fullUrl){
		this.startUrl = fullUrl;
	}
	
	public String getStartUrl() {
		return startUrl;
	}
	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}
	public Map<String, Object> getInParams() {
		return inParams;
	}
	public void putParam(String key, Object val){
		inParams.put(key, val);
	}
	
	public String getTaskName() {
		return taskName;
	}
	
}
