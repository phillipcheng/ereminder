<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- <script src="../js/xpath.js"></script> -->
<script>
function submitNoNew(){
	document.listForm.target="";
	document.listForm.submit();
}
function submitNew(){
	document.listForm.target="_blank";
	document.listForm.submit();
}
</script>
<title>SiteConf List</title>
</head>
<body>
<%@ page import="org.cld.webconf.InitListener" %>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.cld.webconf.ServletUtil" %>
<%@ page import="org.cld.webconf.WebConfPersistMgr" %>
<%@ page import="org.cld.util.HtmlUtil" %>
<%@ page import="org.cld.util.entity.SiteConf" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.apache.logging.log4j.core.LoggerContext"%>
<%@ page import="org.apache.logging.log4j.core.config.LoggerConfig"%>
<%@ page import="org.apache.logging.log4j.Level"%>
<%@ page import="org.hibernate.cfg.Configuration"%>
<%@ page import="org.cld.taskmgr.entity.RunType"%>


<% Logger logger = LogManager.getLogger("cld.jsp");%>
<% String userId="cr"; %>
<% WebConfPersistMgr wfpm = new WebConfPersistMgr(new Configuration().configure().buildSessionFactory());%>
<% List<SiteConf> lsc = wfpm.getSiteConf(userId);%>
<% logger.info("lsc size:" + lsc.size());%>
	Existing:
	<form name="listForm" action="/cldwebconf/CrawlConf" method="post" target='_blank'>
		<table border=1 cellpadding=5>
	    	<tr>
	    		<th>
	    			<input type="checkbox" onclick="toggleSelectAll(this,'selectedSiteConf')"/>
	    		Select
	    		</th>
	        	<th>SiteConfId</th>
	    		<th>BrowseType</th>
	    		<th>startUrl</th>
	    		<th>taskName</th>
	            <th>UserId</th>
	            <th>UpdateTime</th>
			</tr>
<% for (SiteConf sc: lsc){%>
			<tr>
				<td>
					<input type="checkbox" name="selectedSiteConf" value="<%=sc.getId() %>" />
				</td>
				<td>
					<a href="/cldwebconf/CrawlConf?command=edit&siteconfid=<%=sc.getId()%>&userid=<%=userId%>"><%= sc.getId()%></a>
				</td>
				<td>
					<select name='testType_<%=sc.getId()%>'>
						<% 
							List<String> ttNames = new ArrayList<String>();
							for (RunType bt: RunType.class.getEnumConstants()){
								ttNames.add(bt.name());
							}
							RunType sbt = RunType.all;
						%>
						<%= HtmlUtil.genOptionList(ttNames, sbt.getId()+"")%>
					</select>
				</td>
				<td>
					<input name='startUrl_<%=sc.getId()%>' type="text" size="50">
				</td>
				<td>
					<input name='taskName_<%=sc.getId()%>' type="text" size="15">
				</td>
	            <td><%= sc.getUserid() %></td>
	            <td><%= sc.getUtimeAsString() %></td>
	        </tr>
<% } %>
		</table>
		Level:
		<select name="logLevel">
			<% 
				LoggerContext context = (LoggerContext) LogManager.getContext(false);
				LoggerConfig loggerConfig = context.getConfiguration().getLoggerConfig("org.cld");
				Level l = loggerConfig.getLevel();
				List<String> strLevels = new ArrayList<String>();
				for (Level lvl: Level.values()){
					strLevels.add(lvl.name());
				}
			%>
			<%= HtmlUtil.genOptionList(strLevels, l.name())%>
		</select>
		<input type="submit" name="command" value="test" onclick="submitNew();">
		<input type="submit" name="command" value="deploy" onclick="submitNoNew();">
		<input type="submit" name="command" value="undeploy" onclick="submitNoNew();">
		<input type="submit" name="command" value="listRunningTasks" onclick="submitNew();">
		<input type="submit" name="command" value="delete" onclick="submitNoNew();">
		<input type="submit" name="command" value="cancel" onclick="submitNoNew();">
		<input type="submit" name="command" value="export" onclick="submitNoNew();">
	</form>
	<form action="/cldwebconf/Upload" method="post" enctype="multipart/form-data">
	    <input type="file" name="file"/>
	    <input type="submit" />
	    <input type="hidden" name="origin" value="<%=request.getRequestURL()%>"/>
	</form>
</body>
</html>