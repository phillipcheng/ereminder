package org.cld.datacrawl.mgr.impl;

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
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.mgr.IProductListAnalyze;
import org.cld.datacrawl.mgr.ListProcessInf;
import org.cld.datacrawl.mgr.TargetPrdInvoke;
import org.cld.datacrawl.pagea.ProductListAnalyzeInf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.task.BrsDetailStat;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.xml.taskdef.BDTProcessType;


public class ProductListAnalyze implements ListProcessInf, IProductListAnalyze {
	private static Logger logger =  LogManager.getLogger(ProductListAnalyze.class);	
	
	private CrawlConf cconf;
	private CrawlTaskConf ctconf;
	
	
	public String toString(){
		return System.identityHashCode(this) + "\n" +
				"ctconf:" + System.identityHashCode(ctconf);
	}
	
	public ProductListAnalyze(){
		
	}
	
	public ProductListAnalyze(CrawlConf cconf, CrawlTaskConf ctconf){
		this.cconf =  cconf;
		this.ctconf = ctconf;
	}
	

	@Override
	public void setup(CrawlConf cconf, CrawlTaskConf ctconf){
		this.cconf =  cconf;
		this.ctconf = ctconf;
	}

	@Override
	public void setCTConf(CrawlTaskConf ctconf) {
		this.ctconf = ctconf;		
	}
	
	
	@Override
	public Price readGeneralPriceItem(String internalId, String storeId, DomNode summary, Date rt, 
			Product product, Task task) throws InterruptedException{
		ProductListAnalyzeInf blaInf = ctconf.getBlaInf();
		Price price = null;
		if (summary != null){
			//get d_price
			double dprice = blaInf.getCurPrice(summary);
			if (dprice == -1){
				//no price
			}else{
				price = new Price();
				price.setId(new CrawledItemId(internalId, storeId, rt));
				price.setPrice(dprice);
				logger.debug("summary bp:" + price);
			}
			blaInf.setAttributes(summary, product, task);
		}
		
		return price;
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
	 * @return false, means readItem failed
	 */
	@Override
	public List<Task> readItem(WebClient wc, HtmlPage page, Category category, DomNode productSummary, 
			String detailedUrl,  Date readTime, CrawlConf cconf, Task task) throws InterruptedException {
		
		ProductListAnalyzeInf blaInf = ctconf.getBlaInf();
		List<TargetPrdInvoke> targetPrdInvokeList = new ArrayList<TargetPrdInvoke>();
		String catId = category.getRealCatId();
		String storeId = category.getId().getStoreId();
		
		//create a dummy summary product 
		Product dummySummaryPrd = cconf.getProductInstance(task.getTasks().getProductType());
		//get the attributes assigned from the summary node
		blaInf.setAttributes(productSummary, dummySummaryPrd, task);
		
		if (productSummary!=null && page != null){
			targetPrdInvokeList = blaInf.getTargetPrdInovokeList(productSummary, page, task, dummySummaryPrd);
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
			BrowseProductTaskConf t = (BrowseProductTaskConf) cconf.getTaskMgr().getTaskInstTemplate("org.cld.datacrawl.task.BrowseProductTaskConf", 
					task.getTasks(), cconf.getPluginClassLoader(), taskParams, new Date(), taskName);
			t.setStartURL(fullOutput.getStartUrl());
			t.genId();
			BDTProcessType processType = t.getBrowseDetailTask(fullOutput.getTaskName()).getBrowsePrdTaskType().getProcessType();
			if (processType == BDTProcessType.GENBPT){
				tl.add(t);
				logger.debug(String.format("Task t:%s generated.", t));
			}else if (processType == BDTProcessType.INLINE){
				//execute browse product now
				BrowseProductTaskConf.browseProduct(t, cconf, ctconf, wc, storeId, catId, taskName, fullOutput.getInParams());
			}else{
				logger.error("unsupported bdt process type.");
			}
		}
		return tl;
	}
	
	@Override
	public List<Task> process(HtmlPage listPage, Date readTime,
			Category cat, CrawlConf cconf, Task task, int maxItems, WebClient wc) throws InterruptedException {
		ProductListAnalyzeInf blaInf = ctconf.getBlaInf();
		List<DomNode> itemList = blaInf.getItemList(listPage, task);
		
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
