package org.cld.webconf;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.task.BrowseProductTaskConf;
import org.cld.datacrawl.task.LocalCrawlables;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.util.entity.SiteConf;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.cld.taskmgr.TaskExeMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.RunType;
import org.cld.taskmgr.entity.Task;

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
	public static final String REQ_PARAM_TEST_TaskName="taskName";
	public static final String REQ_PARAM_TEST_TASKIDS="taskids";
	public static final String REQ_PARAM_TEST_TASKID="taskid";
	public static final String REQ_PARAM_TEST_LOGLEVEL="logLevel";
	
	
	//pages
	public static final String SiteConfListPage="/cldwebconf/jsp/ListSiteConf.jsp";
	public static final String TestResultListPage="/cldwebconf/jsp/TestResultList.jsp";
	public static final String CatResultPage="/cldwebconf/jsp/CatResultList.jsp";
	public static final String PrdResultPage="/cldwebconf/jsp/PrdResultList.jsp";
	
	public static String appRoot;
	public static String bookWebAppRoot;

	public static String propFile;
	public static CrawlConf cconf;
	
	private WebConfPersistMgr wcpm;
	private TaskExeMgr tem;
	
	@Override
	public void init() {
		appRoot = getInitParameter(APPROOT_KEY);
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		wcpm = new WebConfPersistMgr(factory);
		logger.info(String.format("init param: %s, %s", APPROOT_KEY, appRoot));
		tem = new TaskExeMgr(5);
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
		
		//process input
		if (CMD_DEPLOY.equals(cmd)){
			wcpm.deployConf(siteids, true);
			response.sendRedirect(SiteConfListPage);
		}else if (CMD_UNDEPLOY.equals(cmd)){
			wcpm.deployConf(siteids, false);
			response.sendRedirect(SiteConfListPage);
		}else if (CMD_CANCEL.equals(cmd)){
			if (siteids!=null){
				for (String siteid:siteids){
					tem.cancel(siteid);
				}
			}
			response.sendRedirect(SiteConfListPage);
			return;
		}else if (CMD_DELETE.equals(cmd)){
			if (siteids!=null){
				wcpm.delete(siteids);
			}
			response.sendRedirect(SiteConfListPage);
			return;
		}else if (CMD_BACK.equals(cmd)){
			response.sendRedirect(SiteConfListPage);
			return;
		}else if (CMD_TEST.equals(cmd)){
			boolean useHadoop = false;
			//set the level
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			LoggerConfig loggerConfig = context.getConfiguration().getLoggerConfig("org.cld");
			Level olvl = loggerConfig.getLevel();
			String strLogLevel = request.getParameter(REQ_PARAM_TEST_LOGLEVEL);
			Level l = Level.getLevel(strLogLevel);
			if (olvl!=l){
				loggerConfig.setLevel(l);
				context.updateLoggers();
			}
			
			String requestUrl = TestResultListPage + "?";
			int testCount=0;
			List<BrowseProductTaskConf> tl = new ArrayList<BrowseProductTaskConf>();
			if (siteids!=null){
				for (String siteid: siteids){
					testCount++;
					String strTestType = request.getParameter(REQ_PARAM_TEST_TYPES+"_"+siteid);
					RunType bt = RunType.valueOf(strTestType);
					SiteConf siteconf = wcpm.getFullSitConf(siteid);//get confXml from siteid
					String taskName = request.getParameter(REQ_PARAM_TEST_TaskName + "_" + siteid);
					String startUrl = request.getParameter(REQ_PARAM_TEST_STARTURL + "_" + siteid);
					Map<String, Object> paramMap = new HashMap<String, Object>();
					if (siteconf!=null){
						BrowseProductTaskConf t = CrawlTestUtil.getPrdTask(siteconf, null, startUrl, taskName, cconf, paramMap, bt);
						String taskId = t.getId();
						if (!useHadoop){
							tem.submit(taskId, siteid, new LocalCrawlables(t, taskId, false, cconf, tem));
						}else{
							tl.add(t);
						}
						if (testCount>0){
							requestUrl+="&";
						}
						requestUrl += REQ_PARAM_TEST_TASKIDS + "=" + t.getId();
					}else{
						logger.error(String.format("siteconf %s not found.", siteconfid));
					}
				}
			}
			if (useHadoop){
				List<Task> taskList = new ArrayList<Task>();
				taskList.addAll(tl);
				TaskUtil.hadoopExecuteCrawlTasks(propFile, cconf, taskList, null, false);
			}
			
			logger.info("requestUrl:" + requestUrl);
			response.sendRedirect(requestUrl);
			return;
		}else if (CMD_LIST.equals(cmd)){
			response.sendRedirect(TestResultListPage);
		}else if (CMD_EXPORT.equals(cmd)){
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment; filename=\"siteconfs.zip\"");
			
			int DEFAULT_BUFFER_SIZE=2000;
			ZipOutputStream output = null;
			try{
				output = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE));
				for (String sid: siteids){
					String xmlconf=wcpm.getFullSitConf(sid).getConfxml();
					output.putNextEntry(new ZipEntry(sid+".xml"));
					output.write(xmlconf.getBytes());
					output.closeEntry();
				}
			}catch(Exception e){
				logger.error("", e);
			}finally{
				output.close();
			}
		}
	}
}
