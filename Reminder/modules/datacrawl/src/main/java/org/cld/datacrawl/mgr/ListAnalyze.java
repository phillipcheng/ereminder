package org.cld.datacrawl.mgr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.entity.Category;
import org.cld.datacrawl.task.BrsDetailStat;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.SomePageErrorException;
import org.cld.pagea.general.CategoryAnalyzeUtil;
import org.cld.pagea.general.ListAnalyzeUtil;
import org.cld.taskmgr.BinaryBoolOpEval;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedTasksDef;
import org.xml.taskdef.BinaryBoolOp;


public class ListAnalyze {
	
	private static Logger logger =  LogManager.getLogger(ListAnalyze.class);	
	public static int NUM_WRONG_LIST_URL = 10;//the max # of error list pages got to browse the list page for a category
	public static int NUM_WHOLE_LIST_RETRY=3;
	
	private CrawlConf cconf;
	private ListProcessInf lpInf; //process the result
	
	private VerifyPageProductList VPBL; //verify the detailed result
	
	public String toString(){
		return System.identityHashCode(this) + "\n" ;
	}
	
	public ListAnalyze(){
	}
	
	public void setup(CrawlConf cconf, ListProcessInf lpInf){
		this.cconf = cconf;
		this.lpInf = lpInf;
		this.VPBL = new VerifyPageProductList(cconf);
	}

	
	class NextPageAndTasks{
		NextPageAndTasks(NextPage np, List<Task> tl){
			this.np = np;
			this.tl = tl;
		}
		NextPage np;
		List<Task> tl;
	}
	
	public NextPageAndTasks readList(WebClient wc, NextPage np, Category cat, Task task, int maxItems, int curPageNum) 
			throws InterruptedException{	
		
		HtmlPageResult listPageResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, np, VPBL, task, task.getParsedTaskDef(), cconf);
		HtmlPage listPage = listPageResult.getPage();
		//before processing items, if there is screen, scroll them
		String nextScreenXPath = task.getLeafBrowseCatTask().getSubItemList().getNextScreen();
		BinaryBoolOp lastScreenCond = task.getLeafBrowseCatTask().getSubItemList().getLastScreenCondition();
		
		if (nextScreenXPath!=null && lastScreenCond!=null){
			while (!BinaryBoolOpEval.eval(lastScreenCond, cat.getParamMap())){
				HtmlElement he = listPage.getFirstByXPath(nextScreenXPath);
				NextPage nsp = new NextPage(he);
				listPageResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, nsp, VPBL, task, task.getParsedTaskDef(), cconf);
				listPage = listPageResult.getPage();
			}
		}
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
				params.put(ConfKey.CURRENT_PAGENUM, curPageNum);
				logger.debug("params map before eval next page." + params);
				NextPage npget = ListAnalyzeUtil.getNextPageUrlFromPage(listPage, task.getParsedTaskDef(), params, cconf);	
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
	public List<Task> readTopLink(Category category, int fromPage, int toPage, Task task, 
			int maxPages, int maxItems) 
			throws InterruptedException, SomePageErrorException {
		if (toPage == -1){
			if (category.getPageNum()>0){
				toPage = category.getPageNum();
			}else{
				if (category.getItemNum()!=0){//calculate pageNum from itemNum
					if (category.getItemNum() % category.getPageSize()==0)
						toPage = category.getItemNum()/category.getPageSize();
					else
						toPage = category.getItemNum()/category.getPageSize() + 1;
				}else{
					//item num un-set
				}
			}
		}
		logger.debug("toPage after ajustment is:" + toPage);

		String catURL = category.getFullUrl();
		String initUrl;
		if (fromPage != 1){
			initUrl = CategoryAnalyzeUtil.getCatURL(category, fromPage, task.getParsedTaskDef());
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
		int pageCount=1;
		
		WebClient wc = CrawlUtil.getWebClient(cconf, task.getParsedTaskDef().getSkipUrls(), ListAnalyzeUtil.needJS(task.getParsedTaskDef()));
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
				
				NextPageAndTasks npt = readList(wc, np, category, task, maxItems, cur);
				np = npt.np;
				if (npt.tl!=null)
					tl.addAll(npt.tl);
				
				cur++;
				
				if (np.getStatus()==NextPage.STATUS_ERR){
					numWrongUrl++;
					logger.debug("error got, next_url try to recover is:" + np);
				}
				
				logger.debug("final next_url is:" + np);				
				
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
	
	public ListProcessInf getLpInf() {
		return lpInf;
	}

	public void setLpInf(ListProcessInf lpInf) {
		this.lpInf = lpInf;
	}

}
