package org.cld.datacrawl.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlServerNode;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.mgr.EmptyListProcessor;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.task.BrsCatStat;
import org.cld.datacrawl.task.BrsDetailStat;
import org.cld.datacrawl.task.InvokeTaskTaskConf;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.pagea.general.CategoryAnalyzeUtil;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.cld.taskmgr.server.ServerNodeImpl;

public class CrawlTestUtil{
	
	private static Logger logger =  LogManager.getLogger(CrawlTestUtil.class);
	
	public static int BROWSE_CAT_TYPE_1_LVL = 1;
	public static int BROWSE_CAT_TYPE_1_PATH = 2;
	public static int BROWSE_CAT_TYPE_RECURSIVE = 3;

	public static CrawlConf getCConf(String properties){
		NodeConf nc = null;
		CrawlConf cconf = null;
		nc = new NodeConf(properties);
		cconf = new CrawlConf(properties, nc);
		if (CrawlConf.crawlDsManager_Value_Hibernate.equals(cconf.getCrawlDsManager())){
			CrawlUtil.setupSessionFactory(nc, cconf);
		}
		return cconf;
	}
	
	public static CrawlServerNode getCSNode(String serverProperties){
		NodeConf nc = null;
		CrawlConf cconf = null;
		ServerNodeImpl tn;
		CrawlServerNode csn;
		
		nc = new NodeConf(serverProperties);
		cconf = new CrawlConf(serverProperties, nc);
		tn = new ServerNodeImpl(nc);
		CrawlUtil.setupSessionFactory(nc, cconf);
		tn.setTaskMgrSF(cconf.getTaskSF());

		csn = (CrawlServerNode)tn.getASN();
		csn.setServerNode(tn);
		csn.setCConf(cconf);
		return csn;
	}
	
	//
	public static CrawlClientNode getCCNode(String clientProperties){
		NodeConf nc = null;
		CrawlConf cconf = null;
		ClientNodeImpl tn;
		CrawlClientNode ctn;
		
		nc = new NodeConf(clientProperties);
		cconf = new CrawlConf(clientProperties, nc);
		tn = new ClientNodeImpl(nc);
		if (CrawlConf.crawlDsManager_Value_Hibernate.equals(cconf.getCrawlDsManager())){
			CrawlUtil.setupSessionFactory(nc, cconf);
			tn.getTaskInstanceManager().setTaskSF(cconf.getTaskSF());
		}
		ctn = (CrawlClientNode)tn.getACN();
		ctn.setTaskNode(tn);
		ctn.setCConf(cconf);
		return ctn;
	}
	
	private static void setupSRT(SiteRuntime srt, CrawlConf cconf, String rootTaskId){
		if (srt.getBct()!=null)
			srt.getBct().setRootTaskId(rootTaskId);
		if (srt.getBdt()!=null)
			srt.getBdt().setRootTaskId(rootTaskId);
		
		srt.bctBS = new BrsCatStat("1");
		srt.bdtBS = new BrsDetailStat("1");
		
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
			startUrl = srt.getBdt().getLeafBrowseCatTask().getBaseBrowseTask().getStartUrl();
		}
		Category category = new Category(new CrawledItemId(
				CategoryAnalyzeUtil.getCatId(startUrl, srt.getBdt().getParsedTaskDef()), 
				srt.getBct().getTasks().getStoreId(),
				new Date()), srt.getBdt().getTasks().getProductType());
		category.setFullUrl(startUrl);
		srt.getBct().setNewCat(category);
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
			String rootTaskId, CrawlClientNode ccnode, String propFile) 
			throws InterruptedException{
		catNavigate(siteconfid, confFileName, null, BROWSE_CAT_TYPE_1_PATH, cconf, rootTaskId, null, ccnode, propFile, 0);
	}
	//defaults to startUrl and 1 path type
	public static void catNavigate(String siteconfid, String confFileName, CrawlConf cconf, 
			String rootTaskId, CrawlClientNode ccnode, String propFile, int pageNum) 
			throws InterruptedException{
		catNavigate(siteconfid, confFileName, null, BROWSE_CAT_TYPE_1_PATH, cconf, rootTaskId, null, ccnode, propFile, pageNum);
	}
	
	public static void catNavigate(String siteconfid, String confFileName, String catUrl, int type, 
			CrawlConf cconf, String rootTaskId, CrawlClientNode ccnode, String propFile, int pageNum) throws InterruptedException {
		catNavigate(siteconfid, confFileName, catUrl, type, cconf, rootTaskId, null, ccnode, propFile, pageNum);
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
	public static void catNavigate(String siteconfid, String confFileName, String catUrl, int type, 
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams, CrawlClientNode ccnode, String propFile, int pageNum) 
			throws InterruptedException {
		if (confFileName!=null){
			cconf.setUpSite(confFileName, null);
			logger.debug("setup sites completed.");
		}
		SiteRuntime srt = getSRT(siteconfid, cconf, rootTaskId);
		logger.debug("set start url for cat navigate.");
		srt.getBct().setPageNum(pageNum);
		if (catUrl==null || "".equals(catUrl)){
			srt.getBct().setStartURL(srt.getBct().getRootBrowseCatTask().getBaseBrowseTask().getStartUrl());
		}else{
			srt.getBct().setStartURL(catUrl);
		}
		List<Task> taskList = srt.ca.navigateCategory(srt.getBct(), srt.bctBS, cconf);
		logger.info("stat:" + srt.bctBS);
		
		if (type != BROWSE_CAT_TYPE_1_LVL){//not just 1 level, go deeper
			List<Task> bdttl = new ArrayList<Task>();
			while(taskList.size()>0){
				Task t = taskList.remove(0);
				t.setRootTaskId(rootTaskId);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
				if (inparams!=null)
					params.putAll(inparams);
				if (t instanceof BrowseCategoryTaskConf){
					BrowseCategoryTaskConf bct = (BrowseCategoryTaskConf)t;
					List<Task> newTaskList = bct.runMyself(params, srt.bctBS);
					if (type == BROWSE_CAT_TYPE_1_PATH){
						//if only 1 path, after browsing the 1st cat, change the list to sub tasks
						taskList.clear();
						taskList.addAll(newTaskList);
					}else{
						taskList.addAll(newTaskList);
					}
				}else if (t instanceof BrowseDetailTaskConf){
					BrowseDetailTaskConf bdt = (BrowseDetailTaskConf)t;
					if (type == BROWSE_CAT_TYPE_1_PATH){
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_PAGE, 1);
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_ITEM, 1);
					}else{
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_PAGE, -1);
						params.put(BrowseDetailTaskConf.TASK_RUN_PARAM_MAX_ITEM, -1);
					}
					if (type==BROWSE_CAT_TYPE_1_PATH){
						bdt.runMyself(params, srt.bdtBS);
						break;
					}else{//
						if (ccnode!=null){
							bdttl.add(bdt);
						}else{
							taskList.addAll(bdt.runMyself(params, srt.bdtBS));
						}
					}
				}else{
					logger.error("task type not supported:" + t);
				}
			}
			if (cconf.getNodeConf().getTaskMgrFramework().equals(NodeConf.tmframework_old)){
				if (ccnode!=null){
					TaskUtil.executeTasks(ccnode.getTaskNode(), bdttl);
					while(!ccnode.getTaskNode().getTaskInstanceManager().getRunningTasks().isEmpty()){
						Thread.sleep(2000);
					}
				}
			}else if (cconf.getNodeConf().getTaskMgrFramework().equals(NodeConf.tmframework_hadoop)){
				CrawlUtil.hadoopExecuteCrawlTasks(propFile, cconf, bdttl, srt.getBct().genId());
			}else{
				logger.error("unsupported taskMgrFramework.");
			}
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
		params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
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
		params.put(CrawlClientNode.TASK_RUN_PARAM_CCONF, cconf);
		for (Task t: tl){
			InvokeTaskTaskConf itt = (InvokeTaskTaskConf)t;
			List<Task> ntl = itt.runMyself(params, null);
			for (Task nt:ntl){
				TaskStat ts = null;
				if (nt instanceof BrowseDetailTaskConf){
					ts = new BrsDetailStat("1"); 
				}else if (nt instanceof BrowseCategoryTaskConf){
					ts = new BrsCatStat("1");
				}
				nt.runMyself(params, ts);
			}
		}
	}
	
}
