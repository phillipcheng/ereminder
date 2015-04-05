package org.cld.crbook.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNamespaceNode;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.ProductHandler;
import org.cld.datacrawl.mgr.impl.BinaryBoolOpEval;
import org.cld.datacrawl.mgr.impl.CrawlTaskEval;
import org.cld.datacrawl.util.HtmlPageResult;
import org.cld.datacrawl.util.HtmlUnitUtil;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.Product;
import org.cld.taskmgr.entity.Task;
import org.cld.util.PatternIO;
import org.cld.util.PatternResult;
import org.cld.util.StringUtil;
import org.json.JSONArray;
import org.xml.mytaskdef.BrowseCatInst;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ParsedBrowsePrd;
import org.xml.taskdef.AttributeType;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ClickStreamType;
import org.xml.taskdef.ClickType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import cy.common.entity.Book;
import cy.common.entity.EntityUtil;
import cy.common.entity.Page;
import cy.common.entity.Reading;
import cy.common.entity.Volume;
import cy.common.xml.XmlWorker;
import cy.crbook.wsclient.CRBookWSClient;

public class BookHandler implements ProductHandler{
	
	public static final String BOOK_PAGE_URLS="bookpageurls";
	public static final String BOOK_PAGE_URLS_pattern="bookpageurls_pattern";
	
	
	public static final String ITEMTYPE_BOOK="book"; //used as the product's itemtype as well as the 1 param
	public static final String PARAM_PAGELIST="pagelist";//1 param
	private static Logger logger =  LogManager.getLogger(BookHandler.class);
	
	private CRBookWSClient wsclient;
	private CrawlConf cconf;
	
	//must have a constructor with CrawlConf as parameter
	public BookHandler(CrawlConf cconf){
		this.cconf=cconf;
		this.wsclient = getWSClient(cconf);
	}
	
	public static final String WS_MAIN_URL_KEY="ws.main.url";
	
	public static CRBookWSClient getWSClient(CrawlConf cconf){
		String wsMainUrl = cconf.getParams().get(WS_MAIN_URL_KEY);
		if (wsMainUrl==null || "".equals(wsMainUrl)){
			return null;
		}
		CRBookWSClient wsclient;
		if (cconf.isUseProxy()){
			wsclient = new CRBookWSClient(wsMainUrl, cconf.getProxyIP(), cconf.getProxyPort(), cconf.getTimeout());
		}else{
			wsclient = new CRBookWSClient(wsMainUrl, cconf.getTimeout());
		}
		return wsclient;
	}
	
	public static void publishBookPages(Book b, List<Page> pageList, CRBookWSClient wsclient){
		boolean finish=false;
		int retryTime=1;
		do{
			long wsRetCode=0;
			if (pageList==null || pageList.size()==0){
				wsRetCode = wsclient.insertBookIfNotExists(b);
			}else{
				wsRetCode = wsclient.insertBookPagesIfNotExists(b, pageList);
			}
			if (wsRetCode>0){
				logger.info(wsRetCode + " records for book:" + b.getId() + " has added.");
				finish=true;
			}else if (wsRetCode<0){
				if (wsRetCode== (-1*HttpStatus.SC_SERVICE_UNAVAILABLE)){
					if (retryTime<3){
						logger.warn("retCode:" + wsRetCode + ": error occurred for " + b.getId() + " when adding. retrying...");
						retryTime++;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							logger.error("", e);
						}
					}else{
						finish=true;
					}
				}else{
					logger.error("retCode:" + wsRetCode + ": error occurred for " + b.getId() + " when adding, give up.");
					finish = true;
				}
			}else{
				logger.info(b.getId() + " exists on server.");
				finish=true;
			}
		}while(!finish);
	}
	
	public static void readingSetType(Reading r, Task task){
		String rootVolume = task.getTasks().getRootVolume();
		if (Volume.ROOT_VOLUME_LHH.equals(rootVolume)||Volume.ROOT_VOLUME_MH.equals(rootVolume)){
			r.setType(Book.TYPE_PIC);
		}else if (Volume.ROOT_VOLUME_XS.equals(rootVolume)){
			r.setType(Book.TYPE_NOVEL);
		}else{
			r.setType(Book.TYPE_MIX);
		}
	}
	
	public static void writeCRVolXml(String bookDir, Volume v){
		File toSaveDir = new File(bookDir + File.separatorChar + v.getParentCat() + File.separatorChar); 
		File toSave =  new File(toSaveDir, v.getId() + "." + XmlWorker.VOL_SUFFIX_1);
		logger.info("file name:" + toSave.toString());
		if (!toSaveDir.exists()){
			toSaveDir.mkdirs();
		}
		try {
			XmlWorker.writeVolumeXml(v, toSave);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public static void saveFile(WebClient wc, String id, int count, HtmlImage hi, 
			String suffix, String catName, String pcatName, String bookDir){
		File toSaveDir = new File(bookDir + File.separatorChar + pcatName +
				File.separatorChar + catName + File.separatorChar + id + File.separatorChar); 
		File toSave =  new File(toSaveDir, StringUtil.getStringFromNum(count, 3) + suffix);
		if (!toSave.exists()){
			if (!toSaveDir.exists()){
				toSaveDir.mkdirs();
			}
			int i=0;
			int retry=3;
			boolean failed=true;
			while (i<retry && failed){
				try {
					hi.saveAs(toSave);
					failed=false;
				} catch (SocketTimeoutException e) {
					CrawlUtil.doubleTimeout(wc);
					failed=true;
					i++;
				} catch (IOException e1){
					logger.error("", e1);
					failed = false;//do not retry
				}
			}
			if (i==retry && failed){
				logger.warn("time out while get:" + hi.getBaseURI());
			}
		}
	}
	
	public static void publishVolume(Volume v, CRBookWSClient wsclient){
		//check anyway, because now, the volume is put in the store before calling this.
		long wsRetCode = wsclient.insertVolumeIfNotExists(v);
		if (wsRetCode>0){
			logger.info(wsRetCode + " records for volume:" + v.getId() + " has added.");
		}else if (wsRetCode<0){
			logger.error("retCode:" + wsRetCode + ": error occurred for " + v.getId() + " when adding.");
		}else{
			logger.info(v.getId() + " exists on server.");
		}
	}
	
	
	private static void setUpdatedStatus(Book b, Product product){
		Integer status = (Integer) product.getParam(Book.STATUS_KEY);
		if (status!=null){
			b.setStatus(status.intValue());
			if (b.getStatus() == Book.STATUS_FINISHED){
				product.setCompleted(true);
			}
		}
		
		Date d = (Date) product.getParam(Book.UTIME_KEY);
		if (d!=null){
			b.setUtime(d);
		}
	}
	
	private static List<Page> convertToPageList(List urllist, String bookid, VarType bpType){
		List<Page> bplist = new ArrayList<Page>();
		for (int i=0; i<urllist.size(); i++){
			Page p = new Page();
			p.setBookid(bookid);
			p.setPageNum(i+1);
			String bguri = null;
			if (bpType==VarType.STRING){
				bguri = (String) urllist.get(i);
			}else{
				logger.error("unsupported type for book page:" + bpType);
			}
			p.setBackgroundUri(bguri);
			bplist.add(p);
		}
		return bplist;
	}
	
	@Override
	public void handleProduct(Product product, Task task, ParsedBrowsePrd taskDef) {	
		String cid = product.getFirstCat();
		String volId = product.getId().getStoreId() + "." + cid;
		logger.info("title:" + product.getName());
		String name = (String) product.getParam(Book.NAME_KEY);
		String title = product.getName();
		//since id will be used in web service restful API(get by id), / is not permitted
		String bid = product.getId().getId().replace("/", "-");
		Book b = new Book();
		
		BookHandler.readingSetType(b, task);
		
		List<String> pageList=null;
		if (product.getLastUrl()==null){	
			if (name!=null){
				b.setName(name);
			}else{
				b.setName(title);	
			}
			b.setCat(volId);
			b.setId(product.getId().getStoreId() + "." + bid);
			b.setAuthor((String) product.getParam(Book.AUTHOR_KEY));
			setUpdatedStatus(b, product);
			b.setTotalPage(product.getTotalPage());
		}else{
			b.fromTopJSONString((String)product.getParam(BookHandler.ITEMTYPE_BOOK));
			setUpdatedStatus(b, product);
		}
		
		List bookpageurls = (List) product.getParam(BOOK_PAGE_URLS);
		AttributeType bpdef = taskDef.getPdtAttrMap().get(BOOK_PAGE_URLS);
		PatternIO bookpageurlspattern = (PatternIO) product.getParam(BOOK_PAGE_URLS_pattern);
		if (bookpageurlspattern!=null && bookpageurlspattern.isFinished()){
			b.setPageBgUrlPattern(bookpageurlspattern.getPR());
		}
		b.setIndexedPages(bookpageurls.size());
		if (b.getTotalPage()==-1){
			b.setTotalPage(b.getIndexedPages());
		}else{
			if (product.isCompleted()){
				b.setIndexedPages(b.getTotalPage());
			}
		}
		b.dataToJSON();
		
		List<Page> bplist = convertToPageList(bookpageurls, b.getId(), bpdef.getValue().getToEntryType());
		XmlCSV.optimizeBook(b, bplist);
		product.addParam(BookHandler.ITEMTYPE_BOOK, b.toTopJSONString());
		if (bookpageurlspattern!=null && bookpageurlspattern.isFinished()){
			//there might be leftover pages
		}else{
			product.removeParam(BOOK_PAGE_URLS);
		}
		JSONArray js = new JSONArray(pageList);
		product.addParam(BookHandler.PARAM_PAGELIST, js);
	
		try {
			if (wsclient !=null){
				BookHandler.publishBookPages(b, bplist, wsclient);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	///////////////////////////
	// For Volumes
	//////////////////////////
	//rootVolId, when there is no more parent category, using this rootVolId
	private static Volume genCRVol(Category me, CrawlConf cconf, Map<String, Object> paramMap, Task task){
		Volume v = new Volume();
		//set my volume id = storeId + "." + catId
		//since id will be used in web service restful API(get by id), / is not permitted
		String escapedId = me.getId().getId().replace("/", "-");
		v.setId(me.getId().getStoreId() + "." + escapedId);
		BookHandler.readingSetType(v, task);
		v.setName(me.getName());
		//set my parent cat(volume) id = storeId + "." parent category id
		String parentVolId = null;
		String parentCatId = me.getParentCatId();
		if (parentCatId!=null&&!"".equals(parentCatId)){
			parentVolId = me.getId().getStoreId() + "." + parentCatId;
		}else{
			String rootCatId = task.getTasks().getRootVolume();
			if (rootCatId==null){
				if (v.getType()==Reading.TYPE_PIC){
					parentVolId=Volume.ROOT_VOLUME_LHH;
				}else if (v.getType()==Reading.TYPE_NOVEL){
					parentVolId=Volume.ROOT_VOLUME_XS;
				}
			}else{
				parentVolId=rootCatId;
			}
		}
		
		v.setParentCat(parentVolId);
		
		Date utime = (Date) paramMap.get(Volume.UTIME_KEY);
		if (utime!=null)
			v.setUtime(utime);
		
		String coverUri = (String) paramMap.get(Volume.KEY_COVER_URI);
		if (coverUri!=null)
			v.setCoverUri(coverUri);
		
		String contentXPath = (String) paramMap.get(Volume.KEY_CONTENT_XPATH);
		if (contentXPath!=null){
			v.setContentXPath(contentXPath);
		}
		
		String referer = (String)paramMap.get(Volume.KEY_REFERER);
		if (referer!=null){
			v.setReferer(referer);
		}
		
		String author = (String) paramMap.get(Volume.AUTHOR_KEY);
		v.setAuthor(author);
		
		v.dataToJSON();
		
		return v;
	}

	@Override
	public void handleCategory(String requestUrl, HtmlPage catlist, Category cat, Task task) 
			throws InterruptedException {
		
		Volume v = genCRVol(cat, cconf, cat.getParamMap(), task);
		
		logger.debug("volume got:" + v);
		
		if (wsclient!=null)
			BookHandler.publishVolume(v, wsclient);
	}
}
