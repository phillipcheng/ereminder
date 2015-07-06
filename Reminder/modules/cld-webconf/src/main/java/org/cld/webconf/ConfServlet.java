package org.cld.webconf;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.task.TestTaskConf;
import org.cld.datastore.entity.SiteConf;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.xml.mytaskdef.TasksTypeUtil;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.TasksType;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlScript;

public class ConfServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger("cld.jsp");
	
	//schema key
	public static final String REFERER_KEY="referer";
	
	public static final String BOOK_WEBAPP_ROOT_KEY="CRBOOK_WEBAPP";
	public static final String TESTNODE_PROPERTIES_KEY="TESTNODE_PROPERTIES";
	
	//replace key
	public static final String APPROOT_KEY="APP_ROOT";
	public static final String SITECONF_KEY="SITECONF_ID";
	public static final String USERID_KEY="USER_ID";
	public static final String CURRENT_URL_KEY="CURRENT_URL";
	
	//commands
	public static final String CMD_KEY="command";
	
	public static final String CMD_NEW="new";
	public static final String CMD_EDIT="edit";
	public static final String CMD_SAVE="saveconf";
	public static final String CMD_BACK="back";
	public static final String CMD_TEST="test";
	public static final String CMD_DEPLOY="deploy";
	public static final String CMD_UNDEPLOY="undeploy";
	public static final String CMD_CANCEL="cancel";
	public static final String CMD_LIST="list";
	public static final String CMD_DELETE="delete";
	public static final String CMD_EXPORT="export";
	
	//request param
	public static final String REQ_PARAM_PCAT_ID="pcatid";
	public static final String REQ_PARAM_SITECONF_ID="siteconfid";
	public static final String REQ_PARAM_USER_ID="userid";
	public static final String REQ_PARAM_START_URL="starturl";
	public static final String REQ_PARAM_SITE_IDS ="selectedSiteConf";
	public static final String REQ_PARAM_RELOAD_URL ="reloadurl";
	public static final String REQ_PARAM_ENABLE_JS ="enableJS";
	public static final String REQ_PARAM_CURRENT_URL ="currenturl";
	public static final String REQ_PARAM_TEST_TYPES="testType";
	public static final String REQ_PARAM_TEST_STARTURL="startUrl";
	public static final String REQ_PARAM_TEST_TASKIDS="taskids";
	public static final String REQ_PARAM_TEST_TASKID="taskid";
	public static final String REQ_PARAM_TEST_LOGLEVEL="logLevel";
	
	
	//pages
	public static final String SiteConfListPage="/cldwebconf/jsp/ListSiteConf.jsp";
	public static final String TestResultPage="/cldwebconf/jsp/TestResultList.jsp";
	public static final String CatResultPage="/cldwebconf/jsp/CatResultList.jsp";
	public static final String PrdResultPage="/cldwebconf/jsp/PrdResultList.jsp";
	
	public static String EDIT_BODY_PATH=StringEscapeUtils.escapeXml("//*[@id=\"ppw_page_body\"]");
	public static String RUN_BODY_PATH=StringEscapeUtils.escapeXml("//body");
	
	public static String appRoot;
	public static String bookWebAppRoot;

	private static CrawlClientNode crawlTestNode;
	
	private static CrawlConf cconf;
	private static Map<String, TasksType> ttCache = new ConcurrentHashMap<String, TasksType>();//siteconfid to taskstype
	
	public static CrawlClientNode getCrawlTestNode() {
		return crawlTestNode;
	}
	public static CrawlConf getCConf() {
		return cconf;
	}

	public static void setCrawlTestNode(CrawlClientNode crawlnode) {
		ConfServlet.crawlTestNode = crawlnode;
		ConfServlet.cconf = crawlnode.getCConf();
	}
	
	public static TasksType getSiteConf(String siteconfid){
		if (ttCache.containsKey(siteconfid)){
			return ttCache.get(siteconfid);
		}else{
			SiteConf sc = cconf.getDefaultDsm().getFullSitConf(siteconfid);
			if (sc!=null){
			JAXBContext jc;
				try {
					jc = JAXBContext.newInstance("org.xml.taskdef");
					Unmarshaller u = jc.createUnmarshaller();
					Source source = new StreamSource(new StringReader(sc.getConfxml()));
					JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
					ttCache.put(siteconfid, root.getValue());
					return root.getValue();
				} catch (JAXBException e) {
					logger.error("", e);
				}
			}else{
				logger.error("siteconfid:" + siteconfid + " not found in db.");
			}
		}
		return null;
	}
	
	@Override
	public void init() {
		appRoot = getInitParameter(APPROOT_KEY);
		logger.info(String.format("init param: %s, %s", APPROOT_KEY, appRoot));
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
		String cmd = request.getParameter(CMD_KEY);
		String siteconfid = request.getParameter(REQ_PARAM_SITECONF_ID);
		String uid = request.getParameter(REQ_PARAM_USER_ID);
		String reloadurl = request.getParameter(REQ_PARAM_RELOAD_URL);
		String[] siteids = request.getParameterValues(REQ_PARAM_SITE_IDS);
		String starturl = null;
		TasksType tt = null;
		
		//process input
		if (CMD_DEPLOY.equals(cmd)){
			cconf.getDefaultDsm().deployConf(siteids, true);
			response.sendRedirect(SiteConfListPage);
		}else if (CMD_UNDEPLOY.equals(cmd)){
			cconf.getDefaultDsm().deployConf(siteids, false);
			response.sendRedirect(SiteConfListPage);
		}else if (CMD_CANCEL.equals(cmd)){
			if (siteids!=null){
				for (String siteid:siteids){
					Set<String> tids = ConfServlet.getCrawlTestNode().getTaskNode().getTaskInstanceManager().getRunningTasks(siteid);
					for (String tid: tids){
						ConfServlet.getCrawlTestNode().getTaskNode().getTaskInstanceManager().removeTaskFromExecutor(tid);
					}
				}
			}
			response.sendRedirect(SiteConfListPage);
			return;
		}else if (CMD_DELETE.equals(cmd)){
			if (siteids!=null){
				for (String storeId:siteids){
					logger.info("delete prd/cat/price for storeId:" + storeId);
					cconf.getDefaultDsm().delCategoryByStoreId(storeId);
					cconf.getDefaultDsm().delProductAndPriceByStoreId(storeId);
				}
			}
			response.sendRedirect(SiteConfListPage);
			return;
		}else if (CMD_EDIT.equals(cmd)){
			if (siteconfid!=null){
				//get the url
				String xmlconf=cconf.getDefaultDsm().getFullSitConf(siteconfid).getConfxml();
				xmlconf = xmlconf.replace(RUN_BODY_PATH, EDIT_BODY_PATH);
				if (xmlconf!=null){
					JAXBContext jc;
					try {
						jc = JAXBContext.newInstance("org.xml.taskdef");
						Unmarshaller u = jc.createUnmarshaller();
						Source source = new StreamSource(new StringReader(xmlconf));
						JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
						tt = root.getValue();
						starturl = TasksTypeUtil.getOrgStartUrl(tt);
					} catch (JAXBException e) {
						logger.error("", e);
					}
				}else{
					logger.error(String.format("xmlconf is null for siteconfid:%s", siteconfid));
				}
			}else{
				starturl=request.getParameter(REQ_PARAM_START_URL);
				if (starturl==null){
					logger.error("starturl(new) and siteconfid(edit) can't both be null.");
				}
			}
		}else if (CMD_NEW.equals(cmd)){
			starturl = request.getParameter(REQ_PARAM_START_URL);
			//create new siteconf
			tt = new TasksType();
			tt.setStoreId(siteconfid);
			tt.setRootVolume("999999");
			BrowseCatType bct = new BrowseCatType();
			bct.setIsLeaf(false);
			BrowseTaskType btt = new BrowseTaskType();
			btt.setStartUrl(starturl);
			btt.setEnableJS(false);			
			bct.setBaseBrowseTask(btt);
			tt.getCatTask().add(bct);
		}else if (CMD_BACK.equals(cmd)){
			response.sendRedirect(SiteConfListPage);
			return;
		}else if (CMD_TEST.equals(cmd)){
			//set the level
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			Configuration config = context.getConfiguration();
			LoggerConfig loggerConfig = config.getLoggerConfig("org.cld");
			Level olvl = loggerConfig.getLevel();
			String strLogLevel = request.getParameter(REQ_PARAM_TEST_LOGLEVEL);
			Level l = Level.getLevel(strLogLevel);
			if (olvl!=l){
				loggerConfig.setLevel(l);
				context.updateLoggers();
			}
			
			String startUrl = request.getParameter(REQ_PARAM_TEST_STARTURL);
			String requestUrl = TestResultPage + "?";
			int testCount=0;
			List<String> selectedtaskids = new ArrayList<String>();
			List<Task> tl = new ArrayList<Task>();
			if (siteids!=null){
				for (String siteid: siteids){
					String[] testTypes = request.getParameterValues(REQ_PARAM_TEST_TYPES);
					for (int i=0; i<testTypes.length;i++){
						boolean init=false;
						if (i==0){
							init=true;
						}
						int testType = Integer.parseInt(testTypes[i]);
						testCount++;
						//TODO
						TestTaskConf tbt = new TestTaskConf(init, null, siteid, null, startUrl);
						selectedtaskids.add(tbt.getId());
						tl.add(tbt);
						if (testCount>0){
							requestUrl+="&";
						}
						requestUrl += REQ_PARAM_TEST_TASKIDS + "=" + tbt.getId();
					}
				}
			}
			TaskUtil.executeTasks(crawlTestNode.getTaskNode(), tl);
			logger.info("requestUrl:" + requestUrl);
			response.sendRedirect(requestUrl);
		}else if (CMD_LIST.equals(cmd)){
			response.sendRedirect(TestResultPage);
		}else if (CMD_SAVE.equals(cmd)){
			starturl = request.getParameter(REQ_PARAM_CURRENT_URL);
		}else if (CMD_EXPORT.equals(cmd)){
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment; filename=\"siteconfs.zip\"");
			
			int DEFAULT_BUFFER_SIZE=2000;
			ZipOutputStream output = null;
			try{
				output = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE));
				for (String sid: siteids){
					String xmlconf=cconf.getDefaultDsm().getFullSitConf(sid).getConfxml();
					output.putNextEntry(new ZipEntry(sid+".xml"));
					output.write(xmlconf.getBytes());
					output.closeEntry();
				}
			}catch(Exception e){
				logger.error("", e);
			}finally{
				output.close();
			}
		}else{//reload set command to save
			if (reloadurl!=null && !"".equals(reloadurl)){
				cmd=CMD_SAVE;
				starturl = reloadurl;
			}
		}
		
		if (CMD_SAVE.equals(cmd)){
			Map<String, String[]> map = request.getParameterMap();
			for (String key: map.keySet()){
				String[] values = map.get(key);
				logger.info(String.format("key:%s, value:%s", key, Arrays.asList(values).toString()));
			}
			String xml = HtmlToXml.toXml(map);
			xml = xml.replace(EDIT_BODY_PATH, RUN_BODY_PATH);
			//clean the cache for this entry
			ttCache.remove(siteconfid);
			//save it to db
			cconf.getDefaultDsm().saveXmlConf(siteconfid, uid, xml);
			logger.info("xml generated:" + xml);
			if (xml!=null){
				JAXBContext jc;
				try {
					jc = JAXBContext.newInstance("org.xml.taskdef");
					Unmarshaller u = jc.createUnmarshaller();
					Source source = new StreamSource(new StringReader(xml));
					JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
					tt = root.getValue();
				} catch (JAXBException e) {
					logger.error("", e);
				}
			}else{
				logger.error(String.format("xmlconf is null for siteconfid:%s", siteconfid));
			}
		}
		
		
		
		if (CMD_EDIT.equals(cmd)||CMD_NEW.equals(cmd)||CMD_SAVE.equals(cmd)){
			//
			Map<String, String> idReplaceMap = new HashMap<String, String>();
			idReplaceMap.put(SITECONF_KEY, request.getParameter(REQ_PARAM_SITECONF_ID));
			idReplaceMap.put(USERID_KEY, request.getParameter(REQ_PARAM_USER_ID));
			
			Map<String, String> headerReplaceMap = new HashMap<String, String>();
			headerReplaceMap.put(APPROOT_KEY, appRoot);
			headerReplaceMap.put(CURRENT_URL_KEY, starturl);//this is the org start url needed to be used to lookup for browse task
			
			//this evaluated start url is used to load the page
			BrowseTaskType btt = TasksTypeUtil.getBTTByStartUrl(tt, starturl);
			if (btt!=null){
				starturl = TasksTypeUtil.getEvaledStartUrl(btt, starturl);
			}
			
			//generate output conf edit page based on tt
			String tasksHtml = XmlToHtml.genHtmlComplexSimpleContent(tt, "Tasks");
			String enableJSHidden = request.getParameter(REQ_PARAM_ENABLE_JS);
			boolean enableJS = getEnableJS(tt, starturl, Boolean.parseBoolean(enableJSHidden));
			String[] skipUrls = new String[tt.getSkipUrl().size()];
			tt.getSkipUrl().toArray(skipUrls);
			WebClient wc = CrawlUtil.getWebClient(getCConf(), skipUrls, enableJS);
			HtmlPage page = null;
			try{
				page = wc.getPage(starturl);
				response.setContentType("text/html");
				response.setCharacterEncoding("utf-8");
				PrintWriter out = response.getWriter();
				out.println("<html>");
				out.println("<head>");
				out.println("<base href=\"" + starturl + "\">");
				ServletUtil.writeFileContent(getServletContext().getRealPath("/html/includeHeader.html"), out, headerReplaceMap);
				//
				HtmlElement header = page.getHead();
				if (enableJS){
					out.print(header.asXml());
				}else{
					out.print(trimScripts(header));
				}
				out.println("</head>");
				//
				ServletUtil.writeFileContent(getServletContext().getRealPath("/html/bodyprexmlform.html"), out, headerReplaceMap);
				out.print(tasksHtml);
				ServletUtil.writeFileContent(getServletContext().getRealPath("/html/bodypostxmlform.html"), out, idReplaceMap);
				
				HtmlElement body = page.getBody();
				if (enableJS){
					out.print(body.asXml());
				}else{
					out.print(trimScripts(body));
				}
				
				ServletUtil.writeFileContent(getServletContext().getRealPath("/html/bodypost.html"), out, null);
			}catch(Exception e){
				logger.error("", e);
			}finally{
				wc.closeAllWindows();
			}
		}
	}
	
	private String trimScripts(HtmlElement he){
		List l = he.getByXPath("node()");
		StringBuffer sb = new StringBuffer();
		for (Object o:l){
			if (o instanceof DomElement){
				if (o instanceof HtmlScript){
					//filtered
				}else{
					sb.append(((DomElement)o).asXml());
				}
			}else if (o instanceof DomText){
				sb.append(((DomText)o).asXml());
			}else{
				if (o instanceof DomNode){
					logger.info("discard domnode:" + ((DomNode)o).asXml());
				}else{
					logger.info("unrecognized object:" + o);
				}
			}
		}
		return sb.toString();
	}
	
	private boolean getEnableJS(TasksType tt, String starturl, boolean defaultValue) {
		BrowseTaskType btt = TasksTypeUtil.getBTTByStartUrl(tt, starturl);
		if (btt!=null){
			return btt.isEnableJS();
		}else{
			logger.info("starturl:" + starturl + " not found on the taskdefinition, so use the submitted hidden value!");
			return defaultValue;
		}
	}
}
