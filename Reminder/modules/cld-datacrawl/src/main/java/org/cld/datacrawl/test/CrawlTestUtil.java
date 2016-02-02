package org.cld.datacrawl.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.mgr.CategoryAnalyze;
import org.cld.datacrawl.mgr.EmptyListProcessor;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.task.InvokeTaskTaskConf;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.pagea.general.CategoryAnalyzeUtil;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.util.entity.Category;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.CrawledItemId;
import org.cld.util.entity.SiteConf;

import com.fasterxml.jackson.annotation.JsonValue;



public class CrawlTestUtil{
	
	private static Logger logger =  LogManager.getLogger(CrawlTestUtil.class);

	private static void setupSRT(SiteRuntime srt, CrawlConf cconf, String rootTaskId){
		if (srt.getBct()!=null)
			srt.getBct().setRootTaskId(rootTaskId);
		if (srt.getBdt()!=null)
			srt.getBdt().setRootTaskId(rootTaskId);
		
		srt.ca = cconf.getCa();
		srt.la = cconf.getLa();
		srt.originalLP = srt.la.getLpInf();
		srt.pa = cconf.getPa();
	}
	
	public static SiteRuntime getSRT(CrawlConf cconf, String taskName, String rootTaskId){
		SiteRuntime srt = new SiteRuntime();
		srt.setBct((BrowseCategoryTaskConf) cconf.getTaskMgr().getTask(taskName));
		srt.setBdt((BrowseDetailTaskConf) cconf.getTaskMgr().getTask(taskName));
		setupSRT(srt, cconf, rootTaskId);
		return srt;
	}
	
	public static SiteRuntime getSRT(SiteConf siteconf, String confFileName, CrawlConf cconf, String rootTaskId){
		String scid=null;
		if (siteconf!=null){
			scid = siteconf.getId();
		}else{
			scid = confFileName;
		}
		
		SiteRuntime srt = new SiteRuntime();
		srt.setBct((BrowseCategoryTaskConf) cconf.getTaskMgr().getTask(scid + "_bct"));
		srt.setBdt((BrowseDetailTaskConf) cconf.getTaskMgr().getTask(scid + "_bdt"));
		setupSRT(srt, cconf, rootTaskId);
		
		return srt;
	}
	
	public static void runBDT(SiteConf siteconf, String confFileName, String startUrl, 
			boolean turnPagesOnly, CrawlConf cconf, String rootTaskId) 
			throws InterruptedException, SomePageErrorException
			{
		cconf.setUpSite(confFileName, siteconf);
		SiteRuntime srt = getSRT(siteconf, confFileName, cconf, rootTaskId);
		
		if (startUrl==null){//using start url defined
			startUrl = srt.getBdt().getLeafBrowseCatTask().getBaseBrowseTask().getStartUrl().getValue();
		}
		Category category = new Category(new CrawledItemId(
				CategoryAnalyzeUtil.getCatId(startUrl, srt.getBdt().getParsedTaskDef()), 
				srt.getBct().getTasks().getStoreId(),
				new Date()), srt.getBdt().getTasks().getProductType());
		category.setLeaf(true);
		category.setFullUrl(startUrl);
		srt.getBct().setNewCat(category);
		srt.getBct().setStartURL(startUrl);
		//navigate this leaf category
		CategoryAnalyze.navigateCategoryOneLvl(srt.getBct(), srt.getBdt().getLeafBrowseCatTask(), cconf);
		if (turnPagesOnly){
			srt.la.setLpInf(new EmptyListProcessor());
		}else{
			srt.la.setLpInf(srt.originalLP);
		}
		//need to load javascript
		srt.la.readTopLink(category, 1, -1, srt.getBdt(), -1, -1);
		logger.info("bds:" + srt.bdtBS);
	}
	
	public static void catNavigate(SiteConf siteconf, String confXmlFileName, CrawlConf cconf, 
			String rootTaskId, String propFile) 
			throws InterruptedException{
		catNavigate(siteconf, confXmlFileName, null, BrowseType.onePath, cconf, rootTaskId, null, propFile, 0);
	}
	//defaults to startUrl and 1 path type
	public static void catNavigate(SiteConf siteconf, String confXmlFileName, CrawlConf cconf, 
			String rootTaskId, String propFile, int pageNum) 
			throws InterruptedException{
		catNavigate(siteconf, confXmlFileName, null, BrowseType.onePath, cconf, rootTaskId, null, propFile, pageNum);
	}
	
	public static void catNavigate(SiteConf siteconf, String confXmlFileName, String catUrl, BrowseType type, 
			CrawlConf cconf, String rootTaskId, String propFile, int pageNum) throws InterruptedException {
		catNavigate(siteconf, confXmlFileName, catUrl, type, cconf, rootTaskId, null, propFile, pageNum);
	}
	
	/**
	 * 
	 * @param siteconfid
	 * @param confFileName
	 * @param catUrl
	 * @param type
	 * @param cconf
	 * @param rootTaskId
	 * @param inparams
	 * @param ccnode: if null, execute sequentially, if not null, in parallel
	 * @throws Exception
	 */
	public static void catNavigate(SiteConf siteconf, String confXmlFileName, String catUrl, BrowseType type, 
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams, String propFile, int pageNum) 
			throws InterruptedException {
		if (confXmlFileName!=null){
			cconf.setUpSite(confXmlFileName, siteconf);
			logger.debug("setup sites completed.");
		}
		SiteRuntime srt = getSRT(siteconf, confXmlFileName, cconf, rootTaskId);
		logger.debug("set start url for cat navigate.");
		srt.getBct().setPageNum(pageNum);
		if (catUrl==null || "".equals(catUrl)){
			String startUrl = (String) TaskUtil.eval(srt.getBct().getRootBrowseCatTask().getBaseBrowseTask().getStartUrl(), inparams);
			srt.getBct().setStartURL(startUrl);
		}else{
			srt.getBct().setStartURL(catUrl);
		}
		List<Task> taskList = new ArrayList<Task>();
		if (type == BrowseType.recursive){
			taskList.add(srt.getBct());
			executeTasks(taskList, cconf, propFile);
		}else{
			taskList = srt.ca.navigateCategory(srt.getBct(), cconf);//one level
			if (type == BrowseType.onePath){//one path
				while(taskList.size()>0){
					Task t = taskList.remove(0);
					t.setRootTaskId(rootTaskId);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
					if (inparams!=null)
						params.putAll(inparams);
					if (t instanceof BrowseCategoryTaskConf){
						BrowseCategoryTaskConf bct = (BrowseCategoryTaskConf)t;
						TaskResult tr = bct.runMyself(params, true, null, null);
						taskList.clear();
						if (tr!=null){
							//if browse one path, after browsing the 1st cat, change the list to sub tasks
							List<Task> newTaskList = tr.getTasks();
							taskList.addAll(newTaskList);
						}
					}else if (t instanceof BrowseDetailTaskConf){
						BrowseDetailTaskConf bdt = (BrowseDetailTaskConf)t;
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_PAGE, 1);
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_ITEM, 1);
						TaskResult tr = bdt.runMyself(params, true, null, null);
						if (tr!=null){
							taskList.addAll(tr.getTasks());
						}else{
							//last task is bdt
							break;
						}
					}else if (t instanceof BrowseProductTaskConf){
						BrowseProductTaskConf bpt = (BrowseProductTaskConf)t;
						bpt.runMyself(params, true, null, null);
						break;
					}else{
						logger.error("task type not supported:" + t);
					}
				}
			}
		}
	}
	
	/*
	 * tl, cconf common to all execution env
	 * ccnode specific to old taskmgr
	 * propFile specific to hadoop taskmgr
	 */
	public static void executeTasks(List<Task> tl, CrawlConf cconf, String propFile){
		if (tl.size()>0){
			TaskUtil.hadoopExecuteCrawlTasks(propFile, cconf, tl, null, false);
		}
	}
	
	public static List<CrawledItem> browsePrd(SiteConf siteconf, String confFileName, String url, 
			CrawlConf cconf, String rootTaskId, Date runDateTime, 
			boolean addToDB, MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		return browsePrd(siteconf, confFileName, url, cconf, rootTaskId, null, runDateTime, addToDB, context, mos);
	}
	
	public static List<CrawledItem> browsePrd(SiteConf siteconf, String confFileName, String url, 
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams, Date runDateTime, 
			boolean addToDB, MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		return browsePrd(siteconf, confFileName, url, null, cconf, rootTaskId, inparams, runDateTime, addToDB, context, mos);
	}
	
	public static List<CrawledItem> browsePrd(SiteConf siteconf, String confFileName, String url, String prdTaskName,
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams, Date runDateTime, 
			boolean addToDB, MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		List<Task> tl = cconf.setUpSite(confFileName, siteconf);
		Map<String, Object> params= new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
		if (inparams!=null)
			params.putAll(inparams);
		List<CrawledItem> cilist = new ArrayList<CrawledItem>();
		for (Task t: tl){
			if (t instanceof BrowseProductTaskConf){
				t.setStartDate(runDateTime);
				if (prdTaskName!=null){
					if (!prdTaskName.equals(t.getName())){
						continue;
					}
				}
				BrowseProductTaskConf bpt = (BrowseProductTaskConf)t;
				if (url!=null && !"".equals(url)){
					bpt.setStartURL(url);
				}
				bpt.setRootTaskId(rootTaskId);
				TaskResult tr = bpt.runMyself(params, addToDB, context, mos);
				if (tr!=null){
					cilist.addAll(tr.getCIs());
				}
			}
		}
		return cilist;
	}
}
