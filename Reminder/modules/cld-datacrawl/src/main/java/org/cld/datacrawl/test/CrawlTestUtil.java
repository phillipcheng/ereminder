package org.cld.datacrawl.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.entity.RunType;
import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.CrawledItem;
import org.cld.util.entity.SiteConf;

public class CrawlTestUtil{
	
	private static Logger logger =  LogManager.getLogger(CrawlTestUtil.class);
	
	//
	public static BrowseProductTaskConf getPrdTask(SiteConf siteconf, String confFileName, String url, String prdTaskName,
			CrawlConf cconf, Map<String, Object> inparams, RunType rt){
		List<Task> ttl = cconf.setUpSite(confFileName, siteconf);
		BrowseProductTaskConf bpt = null;//if prdTaskName is null, take the 1st browsePrdTask, if not find the match
		for (Task t: ttl){
			if (t instanceof BrowseProductTaskConf){
				t.setStartDate(new Date());
				if (prdTaskName!=null){
					if (!prdTaskName.equals(t.getName())){
						continue;
					}
				}
				bpt = (BrowseProductTaskConf)t;
				break;
			}
		}
		if (bpt!=null){
			if (url!=null && !"".equals(url)){
				bpt.setStartURL(url);
			}
			if (inparams!=null){
				bpt.putAllParams(inparams);
			}
			bpt.setRt(rt);
			if (siteconf!=null){
				bpt.setSiteconfid(siteconf.getId());
			}else{
				bpt.setSiteconfid(confFileName);
			}
		}
		return bpt;
	}
	
	public static List<CrawledItem> browsePrd(BrowseProductTaskConf bpt, CrawlConf cconf, String rootTaskId, boolean addToDB) throws InterruptedException{
		if (bpt!=null){
			bpt.setRootTaskId(rootTaskId);
			List<CrawledItem> cilist = new ArrayList<CrawledItem>();
			List<Task> til = new ArrayList<Task>();
			til.add(bpt);
			boolean addMore=true;
			while (til.size()>0){
				Task t = til.remove(0);
				t.putParam(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
				TaskResult tr = t.runMyself(null, addToDB, null, null);
				if (tr!=null && tr.getCIs()!=null){
					cilist.addAll(tr.getCIs());
				}
				if (bpt.getRt() == RunType.onePrd){
					addMore=false;
				}
				if (tr!=null && tr.getTasks()!=null && addMore){
					if (bpt.getRt() == RunType.onePath){
						til.add(tr.getTasks().get(0));
					}else{
						til.addAll(tr.getTasks());
					}
				}
				if (bpt.getRt() == RunType.oneLevel){
					addMore = false;
				}
			}//add till no more for all
		
			return cilist;
		}else{
			return null;
		}
	}
	/**
	 * locally iterate according to BrowseType
	 * Sample tasks
	 * Ta
	 *  |
	 * Tb1 		 	Tb2
	 *	|			|
	 * Tc1 Tc2 	    Td1 Td2
	 * onePrd(0),  //prdTaskName Ta, traverse Ta
	 * oneLevel(1), //prdTaskName Ta, traverse Ta, Tb1, Tb2
	 * onePath(2), //prdTaskName Ta, traverse Ta, Tb1, Tc1
	 * all(3);//prdTaskName Ta, traverse all
	 */
	public static List<CrawledItem> browsePrd(SiteConf siteconf, String confFileName, String url, String prdTaskName,
			CrawlConf cconf, String rootTaskId, Map<String, Object> inparams, boolean addToDB, RunType bt) throws InterruptedException{
		BrowseProductTaskConf bpt = getPrdTask(siteconf, confFileName, url, prdTaskName, cconf, inparams, bt);
		return browsePrd(bpt, cconf, rootTaskId, addToDB);
	}
}
