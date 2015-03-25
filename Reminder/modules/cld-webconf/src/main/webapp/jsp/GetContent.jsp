<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.cld.datacrawl.CrawlUtil" %>
<%@ page import="org.cld.datastore.entity.SiteConf" %>
<%@ page import="org.xml.taskdef.TasksType" %>
<%@ page import="com.gargoylesoftware.htmlunit.WebClient" %>
<%@ page import="com.gargoylesoftware.htmlunit.html.HtmlPage" %>
<%@ page import="com.gargoylesoftware.htmlunit.html.HtmlElement" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%
Logger logger = LogManager.getLogger("cld.jsp");
String siteconfid = request.getParameter(ConfServlet.REQ_PARAM_SITECONF_ID);
String currentUrl = request.getParameter(ConfServlet.REQ_PARAM_CURRENT_URL);
logger.info("siteconfid:" + siteconfid);
logger.info("currentUrl:" + currentUrl);

WebClient wc = CrawlUtil.getWebClient(ConfServlet.getCConf(), null, false);
HtmlPage hp = wc.getPage(currentUrl);
TasksType tt = ConfServlet.getSiteConf(siteconfid);
wc.closeAllWindows();
%>
