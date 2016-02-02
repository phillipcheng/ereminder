package org.cld.taskmgr;

import java.util.List;

import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.CrawledItem;

public class TaskResult {
	
	private List<Task> tl;
	private List<CrawledItem> cil;
	
	public TaskResult(List<Task> tl, List<CrawledItem> cil){
		this.tl = tl;
		this.cil = cil;
	}
	
	public List<Task> getTasks(){
		return tl;
	}
	
	public List<CrawledItem> getCIs(){
		return cil;
	}

}
