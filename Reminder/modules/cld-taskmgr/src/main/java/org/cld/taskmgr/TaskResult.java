package org.cld.taskmgr;

import java.util.ArrayList;
import java.util.List;

import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.CrawledItem;

public class TaskResult {
	
	private List<Task> tl;
	private List<CrawledItem> cil;
	
	public TaskResult(){
	}
	
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
	
	public void addTasks(List<Task> tl){
		if (tl!=null){
			if (this.tl==null){
				this.tl = new ArrayList<Task>();
			}
			this.tl.addAll(tl);
		}
	}

	public void addCI(CrawledItem ci){
		if (ci!=null){
			if (this.cil==null){
				this.cil = new ArrayList<CrawledItem>();
			}
			this.cil.add(ci);
		}
	}
	
	public void addCIs(List<CrawledItem> cil){
		if (cil!=null){
			if (this.cil==null){
				this.cil = new ArrayList<CrawledItem>();
			}
			this.cil.addAll(cil);
		}
	}
	
	public void addTR(TaskResult tr){
		addTasks(tr.getTasks());
		addCIs(tr.getCIs());
	}
}
