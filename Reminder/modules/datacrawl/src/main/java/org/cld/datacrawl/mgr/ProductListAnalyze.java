package org.cld.datacrawl.mgr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.task.BrsDetailStat;
import org.cld.pagea.general.ProductListAnalyzeUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.Category;
import org.cld.util.entity.CrawledItemId;
import org.cld.util.entity.Price;
import org.cld.util.entity.Product;
import org.xml.taskdef.BDTProcessType;


public class ProductListAnalyze implements ListProcessInf{
	private static Logger logger =  LogManager.getLogger(ProductListAnalyze.class);	
	
	
	public String toString(){
		return System.identityHashCode(this) + "\n";
	}
	
	public ProductListAnalyze(){
		
	}
	
	/**
	 * either full info or url to fetch info
	 * page + cat + productSummary = detailedUrl
	 * @param wc
	 * @param page
	 * @param cat
	 * @param productSummary: the summary xml get from the list page
	 * @param detailedUrl: the detailed product detailedUrl
	 * @param readTime
	 * @param cconf
	 * @param task
	 * @return false, means readItem failed
	 */
	public List<Task> readItem(WebClient wc, HtmlPage page, Category category, DomNode productSummary, 
			String detailedUrl,  Date readTime, CrawlConf cconf, Task task) throws InterruptedException {
		
		List<TargetPrdInvoke> targetPrdInvokeList = new ArrayList<TargetPrdInvoke>();
		String catId = category.getRealCatId();
		String storeId = category.getId().getStoreId();
		
		//create a dummy summary product 
		Product dummySummaryPrd = cconf.getProductInstance(task.getTasks().getProductType());
		//get the attributes assigned from the summary node
		ProductListAnalyzeUtil.setAttributes(productSummary, dummySummaryPrd, task, cconf);
		
		if (productSummary!=null && page != null){
			targetPrdInvokeList = ProductListAnalyzeUtil.getTargetPrdInovokeList(productSummary, page, task, dummySummaryPrd);
		}else{
			TargetPrdInvoke spo = new TargetPrdInvoke(detailedUrl);
			targetPrdInvokeList.add(spo);
		}
		
		if (targetPrdInvokeList.size() == 0){
			logger.error(String.format("fullUrl get from %s is empty.", productSummary.asXml()));
			return null;
		}
		List<Task> tl = new ArrayList<Task>();
		for (TargetPrdInvoke fullOutput:targetPrdInvokeList){
			//generate browse product task
			Map<String, Object> taskParams = new HashMap<String, Object>();
			taskParams.put(CrawlConf.taskParamCConf_Key, cconf);
			taskParams.putAll(fullOutput.getInParams());
			String taskName = null;
			if (fullOutput.getTaskName()!=null){
				taskName = fullOutput.getTaskName();
			}else{
				taskName = task.getParsedTaskDef().getBrowseDetailTask(null).getBrowsePrdTaskType().getBaseBrowseTask().getTaskName();
			}
			BrowseProductTaskConf t = (BrowseProductTaskConf) cconf.getTaskMgr().getTask(taskName);
			t = t.clone(cconf.getPluginClassLoader());
			t.putAllParams(taskParams);
			t.setStartURL(fullOutput.getStartUrl());
			t.setCatId(catId);
			t.genId();
			BDTProcessType processType = t.getBrowseDetailTask(fullOutput.getTaskName()).getBrowsePrdTaskType().getProcessType();
			if (processType == BDTProcessType.GENBPT){
				tl.add(t);
				logger.debug(String.format("Task t:%s generated.", t));
			}else if (processType == BDTProcessType.INLINE){
				//execute browse product now
				BrowseProductTaskConf.browseProduct(t, cconf, storeId, catId, taskName, fullOutput.getInParams(), false, t.getStartDate(), true, 
						null, null, null, null);
			}else{
				logger.error("unsupported bdt process type.");
			}
		}
		return tl;
	}
	
	@Override
	public List<Task> process(HtmlPage listPage, Date readTime,
			Category cat, CrawlConf cconf, Task task, int maxItems, WebClient wc) throws InterruptedException {
		List<DomNode> itemList = ProductListAnalyzeUtil.getItemList(listPage, task, cconf);
		
		boolean orgJSOption = wc.getOptions().isJavaScriptEnabled();
		wc.getOptions().setJavaScriptEnabled(task.getBrowseDetailTask(null).getBrowsePrdTaskType().getBaseBrowseTask().isEnableJS());
		
		List<Task> tl = new ArrayList<Task>();
		try {
			if (itemList != null){
				int itemCount=0;
				logger.debug("this page has:" + itemList.size() + " items.");
				for (int i=0; i<itemList.size(); i++){
					itemCount++;
					if (maxItems>0 && itemCount>maxItems){
						break;
					}
					//wait for cancel
					if (cconf.isCancelable())
						Thread.sleep(100);
					
					//need to process all the items
					List<Task> tl1= readItem(wc, listPage, cat, itemList.get(i), null, readTime, cconf, task);
					if (tl1!=null){
						tl.addAll(tl1);
					}
				}
			}else{
				logger.fatal("itemlist not found in:" + listPage.getUrl().toString());
				return null;
			}
		}finally{
			wc.getOptions().setJavaScriptEnabled(orgJSOption);
		}
		return tl;
	}
}
