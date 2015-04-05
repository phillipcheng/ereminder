package org.cld.datacrawl.test;

import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datacrawl.mgr.IListAnalyze;
import org.cld.datacrawl.mgr.IProductAnalyze;
import org.cld.datacrawl.mgr.ListProcessInf;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrsCatStat;
import org.cld.datacrawl.task.BrsDetailStat;
import org.xml.taskdef.TasksType;

public class SiteRuntime {
	BrsCatStat bctBS;	
	BrsDetailStat bdtBS;
	private BrowseCategoryTaskConf bct;
	private BrowseDetailTaskConf bdt;
	
	CrawlTaskConf ctconf;
	
	ICategoryAnalyze ca;
	IListAnalyze la;
	ListProcessInf originalLP;
	IProductAnalyze pa;
	TasksType tasks;
	
	public TasksType getTasks() {
		return tasks;
	}
	public void setTasks(TasksType tasks) {
		this.tasks = tasks;
	}
	public BrowseCategoryTaskConf getBct() {
		return bct;
	}
	public void setBct(BrowseCategoryTaskConf bct) {
		this.bct = bct;
		this.tasks = bct.getTasks();
	}
	
	public BrowseDetailTaskConf getBdt() {
		return bdt;
	}
	public void setBdt(BrowseDetailTaskConf bdt) {
		this.bdt = bdt;
		this.tasks = bdt.getTasks();
	}
	
}
