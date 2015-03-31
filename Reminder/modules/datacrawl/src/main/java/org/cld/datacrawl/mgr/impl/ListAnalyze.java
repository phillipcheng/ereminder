package org.cld.datacrawl.mgr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.entity.Category;
import org.cld.datacrawl.mgr.IListAnalyze;
import org.cld.datacrawl.mgr.ListProcessInf;
import org.cld.datacrawl.mgr.VerifyPageProductList;
import org.cld.datacrawl.pagea.ListAnalyzeInf;
import org.cld.datacrawl.task.BrsDetailStat;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedTasksDef;


public class ListAnalyze implements IListAnalyze {
	
	private static Logger logger =  LogManager.getLogger(ListAnalyze.class);	
	public static int NUM_WRONG_LIST_URL = 10;//the max # of error list pages got to browse the list page for a category
	public static int NUM_WHOLE_LIST_RETRY=3;
	
	private CrawlConf cconf;
	private CrawlTaskConf ctconf;
	private ListProcessInf lpInf; //process the result
	
	private VerifyPageProductList VPBL; //verify the detailed result
	
	public String toString(){
		return System.identityHashCode(this) + "\n" +
				"ctconf:" + System.identityHashCode(ctconf);
	}
	
	
	public ListAnalyze(){
	}

	//runtime, lpInf is ProductListAnalyze
	public ListAnalyze(CrawlConf cconf, CrawlTaskConf ctconf, ListProcessInf lpInf){
		this.cconf = cconf;
		this.ctconf = ctconf;
		this.lpInf = lpInf;
		this.VPBL = new VerifyPageProductList(ctconf);
	}
	
	@Override
	public void setup(CrawlConf cconf, CrawlTaskConf ctconf, ListProcessInf lpInf){
		this.cconf = cconf;
		this.ctconf = ctconf;
		this.lpInf = lpInf;
		this.VPBL = new VerifyPageProductList(ctconf);
	}

	@Override
	public void setCTConf(CrawlTaskConf ctconf) {
		this.ctconf = ctconf;		
	}
	
	class NextPageAndTasks{
		NextPageAndTasks(NextPage np, List<Task> tl){
			this.np = np;
			this.tl = tl;
		}
		NextPage np;
		List<Task> tl;
	}
	
	public NextPageAndTasks readList(WebClient wc, NextPage np, Category cat, Task task, int maxItems) 
			throws InterruptedException{	
		ListAnalyzeInf laInf = ctconf.getLaInf();
		
		HtmlPageResult listPageResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, np, VPBL, task, 
				cconf.getMaxRetry(), cconf.isCancelable(), task, cconf);
		HtmlPage listPage = listPageResult.getPage();
		
		if (listPageResult.getErrorCode() == HtmlPageResult.SUCCSS){
			Date readTime = new Date(System.currentTimeMillis());
			List<Task> tl = lpInf.process(listPage, readTime, cat, cconf, task, maxItems, wc);
			if (tl==null)
				return new NextPageAndTasks(new NextPage(NextPage.STATUS_ERR), null);
			else{
				//re-evaluate the attributes on the category from the new page
				CrawlTaskEval.setUserAttributes(listPage, task.getParsedTaskDef().getLeafBrowseCatTask().getBaseBrowseTask().getUserAttribute(), 
						cat.getParamMap(), cconf, task.getParamMap());
				Map<String, Object> params = cat.getParamMap();
				params.put(ConfKey.TOTAL_PAGENUM, cat.getPageNum());
				logger.debug("params map before eval next page." + params);
				NextPage npget = laInf.getNextPageUrlFromPage(listPage, task.getParsedTaskDef(), params);	
				return new NextPageAndTasks(npget, tl);
			}
		}else{
			logger.warn("url:" + np + ", failed with:" + listPageResult.getErrorMsg());
			return new NextPageAndTasks(new NextPage(NextPage.STATUS_ERR), null);
		}
	
	}
	
	/**
	 *
	 * @param cat: contains stype, fullUrl, catId
	 * @param bds
	 * @param sf
	 * @param fromPage: 1 means from head
	 * @param toPage: -1 means to tail
	 * @param read till either maxPages or maxItems reached, if both -1, means browse all
	 */
	@Override
	public List<Task> readTopLink(Category category, int fromPage, int toPage, Task task, 
			int maxPages, int maxItems) 
			throws InterruptedException, SomePageErrorException {
		if (toPage == -1){
			if (category.getItemNum()!=0){
				if (category.getItemNum() % category.getPageSize()==0)
					toPage = category.getItemNum()/category.getPageSize();
				else
					toPage = category.getItemNum()/category.getPageSize() + 1;
			}else{
				//item num un-set
				
			}
		}
		logger.debug("toPage after ajustment is:" + toPage);

		String catURL = category.getFullUrl();
		String initUrl;
		if (fromPage != 1){
			initUrl = ctconf.getCaInf().getCatURL(category, fromPage, task.getParsedTaskDef());
		}else{
			initUrl = catURL;
		}
		
		if (category.getId().getId()==null){//for invocation not via listBrowseThread
			logger.fatal("category id is null.");
			return new ArrayList<Task>();
		}
		
		NextPage np=new NextPage(initUrl);
		int numWrongUrl=0;
		int maxWrongTry = NUM_WRONG_LIST_URL;
		if (toPage != -1){
			maxWrongTry = toPage - fromPage + 1;
		}
		
		int cur = fromPage;
		int nextcur;
		int pageCount=1;
		
		WebClient wc = CrawlUtil.getWebClient(cconf, task.getParsedTaskDef().getSkipUrls(), ctconf.getLaInf().needJS(task.getParsedTaskDef()));
		List<Task> tl = new ArrayList<Task>();
		try{
			do {
				pageCount++;
				logger.debug("current page:" + cur + ", toPage:" + toPage + ", fromPage:" + fromPage);
				
				if (toPage!=-1 && cur > toPage){
					//knows the end page
					break;
				}
				
				logger.debug("url to try in ListAnalyze:" + np);
				
				NextPageAndTasks npt = readList(wc, np, category, task, maxItems);
				np = npt.np;
				if (npt.tl!=null)
					tl.addAll(npt.tl);
				
				nextcur = cur + 1;
				
				if (np.getStatus()==NextPage.STATUS_ERR){
					numWrongUrl++;
					logger.debug("error got, next_url try to recover is:" + np);
				}
				
				logger.debug("final next_url is:" + np);				
				
				cur = nextcur;
				
				if (maxPages>0 && pageCount>maxPages)
					break;
			}while (np.getStatus()!=NextPage.STATUS_LASTPAGE && numWrongUrl< maxWrongTry);
			
			if (numWrongUrl >= maxWrongTry){
				logger.warn("exceeds max # retries for error list page:" + maxWrongTry + ", last problem url:" + initUrl + ", problem next_url:" + np);
				SomePageErrorException spe = new SomePageErrorException();
				spe.setErrorPage(np);
				throw spe;
			}
		}finally{
			wc.closeAllWindows();
		}
		return tl;
	}
	
	@Override
	public VerifyPageProductList getVPBL() {
		return VPBL;
	}
	
	@Override
	public void setVPBL(VerifyPageProductList vPBL) {
		VPBL = vPBL;
	}
	
	@Override
	public ListProcessInf getLpInf() {
		return lpInf;
	}
	
	@Override
	public void setLpInf(ListProcessInf lpInf) {
		this.lpInf = lpInf;
	}

}
