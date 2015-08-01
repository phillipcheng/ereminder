package org.cld.datacrawl.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.mgr.CategoryAnalyze;
import org.cld.datacrawl.mgr.EmptyListProcessor;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.task.InvokeTaskTaskConf;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.pagea.general.CategoryAnalyzeUtil;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;

import com.fasterxml.jackson.annotation.JsonValue;


public class CrawlTestUtil{
	
	private static Logger logger =  LogManager.getLogger(CrawlTestUtil.class);
	
	public enum browse_type{
		one_level(0),
		one_path(1),
		recursive(2),
		bct(3),
		bdt(4),
		bdt_turnpage_only(5),
		bpt(6);
		
		private int id;
		private browse_type(final int id){
			this.id = id;
		}
		
		@JsonValue
		public int getId(){
			return id;
		}
	}

	public static CrawlConf getCConf(String properties){
		NodeConf nc = null;
		CrawlConf cconf = null;
		nc = new NodeConf(properties);
		cconf = new CrawlConf(properties, nc);
		
		if (cconf.getDsm(CrawlConf.crawlDsManager_Value_Hibernate)!=null){
			CrawlUtil.setupSessionFactory(nc, cconf);
		}
		return cconf;
	}
	
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
	
	public static SiteRuntime getSRT(String siteconfid, CrawlConf cconf, String taskName, String rootTaskId){
		SiteRuntime srt = new SiteRuntime();
		srt.setBct((BrowseCategoryTaskConf) cconf.getTaskMgr().getTask(taskName));
		srt.setBdt((BrowseDetailTaskConf) cconf.getTaskMgr().getTask(taskName));
		
		setupSRT(srt, cconf, rootTaskId);
		
		return srt;
	}
	
	public static SiteRuntime getSRT(String siteconfid, CrawlConf cconf, String rootTaskId){
		SiteRuntime srt = new SiteRuntime();
		srt.setBct((BrowseCategoryTaskConf) cconf.getTaskMgr().getTask(siteconfid + "_bct"));
		srt.setBdt((BrowseDetailTaskConf) cconf.getTaskMgr().getTask(siteconfid + "_bdt"));
		
		setupSRT(srt, cconf, rootTaskId);
		
		return srt;
	}
	
	public static void runBDT(String siteconfid, String confFileName, String startUrl, 
			boolean turnPagesOnly, CrawlConf cconf, String rootTaskId) 
			throws InterruptedException, SomePageErrorException
			{
		if (confFileName!=null){
			cconf.setUpSite(confFileName, null);
		}else{
			//setup is called explicitly already
		}
		SiteRuntime srt = getSRT(siteconfid, cconf, rootTaskId);
		
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
	
	public static void catNavigate(String siteconfid, String confFileName, CrawlConf cconf, 
			String rootTaskId, String propFile) 
			throws InterruptedException{
		catNavigate(siteconfid, confFileName, null, browse_type.one_path, cconf, rootTaskId, null, propFile, 0);
	}
	//defaults to startUrl and 1 path type
	public static void catNavigate(String siteconfid, String confFileName, CrawlConf cconf, 
			String rootTaskId, String propFile, int pageNum) 
			throws InterruptedException{
		catNavigate(siteconfid, confFileName, null, browse_type.one_path, cconf, rootTaskId, null, propFile, pageNum);
	}
	
	public static void catNavigate(String siteconfid, String confFileName, String catUrl, browse_type type, 
			CrawlConf cconf, String rootTaskId, String propFile, int pageNum) throws InterruptedException {
		catNavigate(siteconfid, confFileName, catUrl, type, cconf, rootTaskId, null, propFile, pageNum);
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
	public static void catNavigate(String siteconfid, String confFileName, String catUrl, browse_type type, 
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams, String propFile, int pageNum) 
			throws InterruptedException {
		if (confFileName!=null){
			cconf.setUpSite(confFileName, null);
			logger.debug("setup sites completed.");
		}
		SiteRuntime srt = getSRT(siteconfid, cconf, rootTaskId);
		logger.debug("set start url for cat navigate.");
		srt.getBct().setPageNum(pageNum);
		if (catUrl==null || "".equals(catUrl)){
			String startUrl = (String) TaskUtil.eval(srt.getBct().getRootBrowseCatTask().getBaseBrowseTask().getStartUrl(), inparams);
			srt.getBct().setStartURL(startUrl);
		}else{
			srt.getBct().setStartURL(catUrl);
		}
		List<Task> taskList = new ArrayList<Task>();
		if (type == browse_type.recursive){
			taskList.add(srt.getBct());
			executeTasks(taskList, cconf, propFile);
		}else{
			taskList = srt.ca.navigateCategory(srt.getBct(), srt.bctBS, cconf);
			logger.info("stat:" + srt.bctBS);
			
			if (type != browse_type.one_level){//one path
				List<Task> bdttl = new ArrayList<Task>();
				while(taskList.size()>0){
					Task t = taskList.remove(0);
					t.setRootTaskId(rootTaskId);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
					if (inparams!=null)
						params.putAll(inparams);
					if (t instanceof BrowseCategoryTaskConf){
						BrowseCategoryTaskConf bct = (BrowseCategoryTaskConf)t;
						List<Task> newTaskList = bct.runMyself(params, srt.bctBS);
						//if only 1 path, after browsing the 1st cat, change the list to sub tasks
						taskList.clear();
						taskList.addAll(newTaskList);
					}else if (t instanceof BrowseDetailTaskConf){
						BrowseDetailTaskConf bdt = (BrowseDetailTaskConf)t;
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_PAGE, 1);
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_ITEM, 1);
						List<Task> tl = bdt.runMyself(params, srt.bdtBS);
						if (tl.size()>0){
							taskList.addAll(tl);
						}else{
							//last task is bdt
							break;
						}
					}else if (t instanceof BrowseProductTaskConf){
						BrowseProductTaskConf bpt = (BrowseProductTaskConf)t;
						bpt.runMyself(params, srt.bdtBS);
						break;
					}else{
						logger.error("task type not supported:" + t);
					}
				}
				executeTasks(bdttl, cconf, propFile);
			}
		}
	}
	
	/*
	 * tl, cconf common to all execution env
	 * ccnode specific to old taskmgr
	 * propFile specific to hadoop taskmgr
	 */
	public static void executeTasks(List<Task> tl, CrawlConf cconf, String propFile){
		if (cconf.getNodeConf().getTaskMgrFramework().equals(NodeConf.tmframework_hadoop)){
			if (tl.size()>0){
				CrawlUtil.hadoopExecuteCrawlTasks(propFile, cconf, tl, null);
			}
		}else{
			logger.error("unsupported taskMgrFramework.");
		}
	}
	
	public static void browsePrd(String siteconfid, String confFileName, String url, 
			CrawlConf cconf, String rootTaskId) throws InterruptedException{
		browsePrd(siteconfid, confFileName, url, cconf, rootTaskId, null);
	}
	
	public static void browsePrd(String siteconfid, String confFileName, String url, 
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams) throws InterruptedException{
		browsePrd(siteconfid, confFileName, url, null, cconf, rootTaskId, inparams);
	}
	
	public static void browsePrd(String siteconfid, String confFileName, String url, String prdTaskName,
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams) throws InterruptedException{
		List<Task> tl = new ArrayList<Task>();
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		Map<String, Object> params= new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
		if (inparams!=null)
			params.putAll(inparams);
		for (Task t: tl){
			if (t instanceof BrowseProductTaskConf){
				if (prdTaskName!=null){
					if (!prdTaskName.equals(t.getName())){
						continue;
					}
				}
				BrowseProductTaskConf bpt = (BrowseProductTaskConf)t;
				if (url!=null){
					bpt.setStartURL(url);
				}
				bpt.setRootTaskId(rootTaskId);
				bpt.runMyself(params, null);
			}
		}
	}
	
	//either myTaskName, which has been registered in the TaskMgr or confFileName
	public static void invokeTask(String confFileName, CrawlConf cconf) throws Exception{
		List<Task> tl = new ArrayList<Task>();
		if (confFileName!=null){
			tl = cconf.setUpSite(confFileName, null);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
		for (Task t: tl){
			InvokeTaskTaskConf itt = (InvokeTaskTaskConf)t;
			List<Task> ntl = itt.runMyself(params, null);
			for (Task nt:ntl){
				TaskStat ts = null;
				nt.runMyself(params, ts);
			}
		}
	}
	
}
