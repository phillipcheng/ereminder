package org.cld.datacrawl.mgr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlTaskConf;
import org.cld.datacrawl.CrawlTaskGenerator;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.entity.Category;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datacrawl.pagea.CategoryAnalyzeInf;
import org.cld.datacrawl.task.BrowseCategoryTaskConf;
import org.cld.datacrawl.task.BrowseDetailTaskConf;
import org.cld.datacrawl.task.BrsCatStat;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.VerifyPageByXPath;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskStat;
import org.xml.mytaskdef.BrowseCatInst;
import org.xml.taskdef.BrowseCatType;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class CategoryAnalyze implements ICategoryAnalyze{
	private static Logger logger =  LogManager.getLogger(CategoryAnalyze.class);
	
	public static final String CAT_ROOT="ROOT";
	public static final String CAT_SEP=":";
	
	private CrawlTaskConf ctconf;
	private CrawlConf cconf;
	
	//
	public static BrowseCategoryTaskConf genBCTFromCat(BrowseCategoryTaskConf parentBCT, Category cat, CrawlConf cconf, CrawlTaskConf ctconf){
		BrowseCategoryTaskConf bct1 = parentBCT.clone(cconf.getPluginClassLoader());
		bct1.setStartURL(cat.getFullUrl());
		bct1.setPcatId(cat.getParentCatId());
		bct1.setPageNum(0);//need to ask
		bct1.setId(bct1.genId());
		return bct1;
	}
	
	public static List<Task> genBCTFromBCT(BrowseCategoryTaskConf bct, int pages, CrawlConf cconf, CrawlTaskConf ctconf){
		List<Task> bctList = new ArrayList<Task>();
		for (int i=0; i<pages; i++){
			BrowseCategoryTaskConf bct1 = bct.clone(cconf.getPluginClassLoader());
			bct1.setStartURL(ctconf.getCaInf().getCatURL(bct.getNewCat(), i+1, bct.getParsedTaskDef()));
			bct1.setId(bct1.genId());
			bct1.setPageNum(1);
			bctList.add(bct1);
		}
		return bctList;
	}
	
	private static BrowseDetailTaskConf genDefaultBDTFromBCT(BrowseCategoryTaskConf bct, Category cat, CrawlConf cconf){
		BrowseDetailTaskConf bdt = (BrowseDetailTaskConf) CrawlTaskGenerator.getNextTask(bct, cconf);
		if (bdt != null){
			bdt.setCatId(cat.getId().getId());
			bdt.setFromPage(1);
			bdt.setToPage(-1);
			bdt.setId(bdt.genId());
		}
		return bdt;
	}
	
	/**
	 * generate the bdt from bct, multiple bdt is possible if there is item limitation for each bct
	 * @param bct
	 * @param cat
	 * @param cconf
	 * @param ctconf
	 * @return set of bdt
	 */
	public static List<Task> genBDTFromBCT(BrowseCategoryTaskConf bct, Category cat, 
			CrawlConf cconf, CrawlTaskConf ctconf, BrowseCatType bcdef){
		
		List<Task> bdtList = new ArrayList<Task>();
		
		if (cat.getPageNum()<=0 || bcdef.getPagesPerBDT()<=0){
			logger.warn(String.format("page num not found or pages per bdt set to <=0 :%s, 1 task browse all!", cat.getId()));
			bdtList.add(genDefaultBDTFromBCT(bct, cat, cconf));
		}else{
			//split by page
			int pagesPerTask = bcdef.getPagesPerBDT();
			int totalPages=cat.getPageNum();
			int batch = totalPages/pagesPerTask;
			logger.debug(String.format("total pages:%d, pagesPerTask:%d, tasks needed:%d", totalPages, pagesPerTask, batch));
			int leftPages = totalPages % pagesPerTask;
			logger.debug("still left pages for this cat:" + leftPages);
			
			//every page is full
			for (int i=0; i<batch; i++){
				BrowseDetailTaskConf bdt = genDefaultBDTFromBCT(bct, cat, cconf);
				bdt.setFromPage(i*pagesPerTask + 1);
				bdt.setToPage((i+1)*pagesPerTask);
				bdt.setCatId(cat.getId().getId());
				bdt.setId(bdt.genId());
				bdtList.add(bdt);
			}
			if (leftPages != 0){
				BrowseDetailTaskConf bte = genDefaultBDTFromBCT(bct, cat, cconf);
				bte.setFromPage(batch*pagesPerTask + 1);
				bte.setToPage(-1);//to the end
				bte.setCatId(cat.getId().getId());
				bte.setId(bte.genId());
				bdtList.add(bte);
			}
		}
		
		return bdtList;	
	}
	
	public String toString(){
		return System.identityHashCode(this) + "\n" +
				"ctconf:" + System.identityHashCode(ctconf);
	}

	public CategoryAnalyze(){	
	}

	@Override
	public void setup(CrawlConf cconf, CrawlTaskConf ctconf){
		this.cconf = cconf;
		this.ctconf = ctconf;
	}
	
	public CategoryAnalyze(CrawlConf cconf, CrawlTaskConf ctconf){
		setup(cconf, ctconf);
	}
	
	private boolean hasUpdate(Category oldCat, Category newCat){
		if (oldCat==null){ //1st time
			return true;
		}
		if (oldCat.getUpdateTime()!=null){
			//compare update time
			if (newCat.getUpdateTime()!=null){
				return newCat.getUpdateTime().after(oldCat.getUpdateTime());
			}else{
				return true;
			}
		}else if (oldCat.getLastItem()!=null){
			//compare last item
			if (newCat.getLastItem()!=null){
				return !(newCat.getLastItem().equals(oldCat.getLastItem()));
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * @throws InterruptedException 
	 */
	private List<Task> navigateCategoryOneLvl(BrowseCategoryTaskConf bct, BrowseCatType bcdef) throws InterruptedException{
		CategoryAnalyzeInf caInf = ctconf.getCaInf();
		Category category = bct.getNewCat();
		String url = bct.getStartURL();
		
		boolean needJS = caInf.needJS(url, bct);
		WebClient wc = CrawlUtil.getWebClient(cconf, bct.getParsedTaskDef().getSkipUrls(), needJS);
		HtmlPageResult catPageResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(url), 
				new VerifyPageByXPath(caInf.getCatPageVerifyXPaths(category, bct)), null, bct.getTasks().getLoginInfo(), cconf);
		HtmlPage catPage = catPageResult.getPage();
		CrawlUtil.closeWebClient(wc);
		
		if (catPageResult.getErrorCode() != HtmlPageResult.SUCCSS || catPage == null){
			logger.warn("get url:" + url +", to contains:" + 
					ArrayUtils.toString(caInf.getCatPageVerifyXPaths(category, bct)) 
					+ " failed with:" + catPageResult.getErrorMsg());
			return new ArrayList<Task>();
		}else{
			if (category.isLeaf()){
				caInf.setCatItemNum(category.getFullUrl(), catPage, category, bct);
				//add one version if differ
				cconf.getDsm().addCrawledItem(category, bct.getOldCat());
				return genBDTFromBCT(bct, category, cconf, ctconf, bcdef);
			}else{
				int catPages = bct.getPageNum();
				if (catPages==0){
					//we ask, 1st time, we navigate this category detail
					caInf.setCatItemNum(url, catPage, category, bct);
					catPages = category.getPageNum();
					//only save the category for the 1st time
					cconf.getDsm().addCrawledItem(category, bct.getOldCat());
				}

				if (catPages>1){
					//we generate bct from bct for each page
					return genBCTFromBCT(bct, catPages, cconf, ctconf);
				}else{ //catPages =1
					List<Category> catList = caInf.getSubCategoyList(url, catPage, category, bct);
					List<Task> tasks = new ArrayList<Task>();
					for (Category newCat:catList){
						if (!newCat.getFullUrl().equals(category.getFullUrl())){
							//prevent having myself as the sub-category
							newCat.setRootTaskId(bct.getRootTaskId());
							Category oldCat = (Category) cconf.getDsm().getCrawledItem(newCat.getId().getId(),
									newCat.getId().getStoreId(), Category.class);
							if (hasUpdate(oldCat, newCat)){
								newCat.setParentCatId(category.getId().getId());
								BrowseCategoryTaskConf bct1 = genBCTFromCat(bct, newCat, cconf, ctconf);
								tasks.add(bct1);
							}
						}
					}
					return tasks;
				}
			}
		}
	}
	
	@Override
	public List<Task> navigateCategory(Task task, TaskStat taskStat) throws InterruptedException{
		BrowseCategoryTaskConf bct = (BrowseCategoryTaskConf) task;
		String startUrl = bct.getStartURL();
		
		Category newCat = new Category(task.getParsedTaskDef().getTasks().getProductType());
		Category oldCat = (Category) cconf.getDsm().getCrawledItem(ctconf.getCaInf().getCatId(startUrl, task), 
				task.getParsedTaskDef().getTasks().getStoreId(), Category.class);
		bct.setOldCat(oldCat);
		BrowseCatInst bci = bct.getParsedTaskDef().getBCI(startUrl);
		newCat.setRootTaskId(task.getRootTaskId());
		newCat.setFullUrl(startUrl);
		newCat.getId().setId(ctconf.getCaInf().getCatId(startUrl, task));
		newCat.getId().setStoreId(task.getParsedTaskDef().getTasks().getStoreId());
		newCat.getId().setCreateTime(new Date());
		newCat.setLeaf(bci.getBc().isIsLeaf());
		newCat.setParentCatId(bct.getPcatId());
		if (hasUpdate(oldCat, newCat)){
			bct.setNewCat(newCat);	
			return navigateCategoryOneLvl(bct, bci.getBc());
		}else{
			return new ArrayList<Task>();
		}
	}
	
	@Override
	public List<Task> retryCat(CrawlConf cconf, TaskStat taskStat) throws InterruptedException{
		List<Task> tasklist = new ArrayList<Task>();
		BrsCatStat bs =(BrsCatStat)taskStat;
		BrowseCategoryTaskConf[] bctArray = new BrowseCategoryTaskConf[bs.getBrokenCat().size()];
		bs.getBrokenCat().toArray(bctArray);
		for (int i=0; i<bctArray.length; i++){
			BrowseCategoryTaskConf bct = bctArray[i];
			logger.info("retry:" + bct);
			tasklist.addAll(navigateCategory(bct, bs));
		}
		return tasklist;
	}
}
