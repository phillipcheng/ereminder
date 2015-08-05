package org.cld.datacrawl.mgr;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.cld.pagea.general.ProductAnalyzeUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.NextPage;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.CrawledItem;
import org.cld.datastore.entity.Product;
import org.cld.datastore.impl.HdfsDataStoreManagerImpl;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datacrawl.util.VerifyPage;
import org.cld.datacrawl.util.VerifyPageByBoolOp;
import org.cld.datacrawl.util.VerifyPageByBoolOpXPath;
import org.cld.datacrawl.util.VerifyPageByXPath;
import org.cld.etl.fci.AbstractCrawlItemToCSV;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.CsvTransformType;

public class ProductAnalyze{
	
	private static Logger logger =  LogManager.getLogger(ProductAnalyze.class);
	
	private VerifyPage VPXP; //
	
	public String toString(){
		return System.identityHashCode(this) + "\n";
	}
	
	public ProductAnalyze(){
	}

	/**
	 * 
	 * @param wc
	 * @param url
	 * @param product
	 * @param lastProduct
	 * @param task
	 * @param taskDef
	 * @param cconf
	 * @param retCsv: true, return the csv content for map to process, false save it to hdfs if dsm is hdfs
	 * @return the csv[] to return
	 * @throws InterruptedException
	 */
	public CrawledItem addProduct(WebClient wc, String url, Product product, Product lastProduct, Task task, 
			ParsedBrowsePrd taskDef, CrawlConf cconf, boolean retCsv, boolean addToDB) 
			throws InterruptedException{
		BrowseDetailType bdt = taskDef.getBrowsePrdTaskType();
		boolean monitorPrice = bdt.isMonitorPrice();
		DataStoreManager dsManager = null;
		if (bdt.getBaseBrowseTask().getDsm()!=null){
			dsManager = cconf.getDsm(bdt.getBaseBrowseTask().getDsm());
		}else{
			dsManager = cconf.getDefaultDsm();
		}
		product.setRootTaskId(task.getRootTaskId());
		product.setFullUrl(url);

		String[] xpaths = ProductAnalyzeUtil.getPageVerifyXPaths(task, taskDef);
		BinaryBoolOp[] bbops = ProductAnalyzeUtil.getPageVerifyBoolOp(task, taskDef);
		VerifyPageByBoolOp vpbbo = xpaths!=null? new VerifyPageByBoolOp(bbops, cconf):null;
		VerifyPageByXPath vpbxp = bbops!=null? new VerifyPageByXPath(xpaths):null;
		VPXP = new VerifyPageByBoolOpXPath(vpbbo, vpbxp);
		
		//
		HtmlPageResult detailsResult;
		HtmlPage details = null;
		detailsResult = HtmlUnitUtil.clickNextPageWithRetryValidate(wc, new NextPage(url), VPXP, null, task.getParsedTaskDef(), cconf);	
		details = detailsResult.getPage();
		
		if (detailsResult.getErrorCode() == HtmlPageResult.SUCCSS){			
			//product name
			if (product.getName()==null){
				String title = ProductAnalyzeUtil.getTitle(details, task, taskDef, cconf);
				product.setName(title);
			}
			if (monitorPrice){
				//
			}
			//call back
			ProductAnalyzeUtil.callbackReadDetails(wc, details, product, task, taskDef, cconf);
			product.getId().setCreateTime(new Date());
			logger.debug("product got:" + product);
			CsvTransformType csvTransform = bdt.getBaseBrowseTask().getCsvtransform();
			if (csvTransform!=null){
				//do the transform and set to crawledItem.csv
				try {
					AbstractCrawlItemToCSV cicsv = (AbstractCrawlItemToCSV) 
							Class.forName(csvTransform.getTransformClass()).newInstance();
					List<String[]> csv = cicsv.getCSV(product, null);
					product.setCsvValue(csv);
					if (CrawlConf.crawlDsManager_Value_Hbase.equals(bdt.getBaseBrowseTask().getDsm()) && addToDB){
						dsManager.addUpdateCrawledItem(product, lastProduct);
					}
					if (retCsv) return product;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				if (CrawlConf.crawlDsManager_Value_Hdfs.equals(bdt.getBaseBrowseTask().getDsm())){
					product.setCsvValue(HdfsDataStoreManagerImpl.getCSV(product, bdt.getBaseBrowseTask()));
				}
			}
			if (dsManager!=null && addToDB)
				dsManager.addUpdateCrawledItem(product, lastProduct);	
		}
		
		return product;
	}
}
