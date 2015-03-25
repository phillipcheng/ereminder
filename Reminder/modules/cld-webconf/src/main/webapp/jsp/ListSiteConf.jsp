<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="../js/xpath.js"></script>
<script>
function submitOpenNew(){
	document.listForm.target="_blank";
	document.listForm.submit();
}
function submitNoNew(){
	document.listForm.target="";
	document.listForm.submit();
}
</script>
<title>SiteConf List</title>
</head>
<body>
<%@ page import="org.cld.webconf.InitListener" %>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.cld.webconf.ServletUtil" %>
<%@ page import="org.cld.util.HtmlUtil" %>
<%@ page import="org.cld.datastore.entity.SiteConf" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.apache.logging.log4j.core.LoggerContext"%>
<%@ page import="org.apache.logging.log4j.core.config.Configuration"%>
<%@ page import="org.apache.logging.log4j.core.config.LoggerConfig"%>
<%@ page import="org.apache.logging.log4j.Level"%>

<% Logger logger = LogManager.getLogger("cld.jsp");%>
<% String userId="cr"; %>
<% List<SiteConf> lsc = ConfServlet.getCConf().getDsm().getSiteConf(userId);%>
<% logger.info("lsc size:" + lsc.size());%>
	Existing:
	<form name="listForm" action="/cldwebconf/CrawlConf" method="post" target='_blank'>
		<table border=1 cellpadding=5>
	    	<tr>
	    		<th>
	    			<input type="checkbox" onclick="toggleSelectAll(this,'selectedSiteConf')"/>
	    		Select
	    		</th>
	    		<th>Type</th>
	        	<th>SiteConfId</th>
	            <th>UserId</th>
	            <th>Status</th>
	            <th>Category</th>
	            <th>Product</th>
	            <th>UpdateTime</th>
	            <th>Tasks</th>
			</tr>
<% for (SiteConf sc: lsc){%>
			<tr>
				<td>
					<input type="checkbox" name="selectedSiteConf" value="<%=sc.getId() %>" />
				</td>
				<td>
				</td>
				</td>
				<td>
					<a href="/cldwebconf/CrawlConf?command=edit&siteconfid=<%=sc.getId()%>&userid=<%=userId%>"><%= sc.getId()%></a>
				</td>
	            <td><%= sc.getUserid() %></td>
	            <td><%= sc.getStatusAsString() %></td>
	            <td>
	            	<a href=<%=ServletUtil.genViewCatUrl(sc.getId(), null)%>><%= ConfServlet.getCConf().getDsm().getCategoryCount(sc.getId(), null) %></a>
	            </td>
	            <td><%= ConfServlet.getCConf().getDsm().getProductCount(sc.getId(), null) %></td>
	            <td><%= sc.getUtimeAsString() %></td>
	            <td><%
	            	Set<String> tids = ConfServlet.getCrawlTestNode().getTaskNode().getTaskInstanceManager().getRunningTasks(sc.getId());
	            %>
	            	<a href=<%=ServletUtil.genViewTaskUrl(tids) %>><%=tids.toString() %></a>
	            </td>
	        </tr>
<% } %>
		</table>
		URL:<input type="text" name="startUrl" size="100"><br>
		<input type="checkbox" name="testType" value="1">One Path
		<input type="checkbox" name="testType" value="2">Category Recursive
		<input type="checkbox" name="testType" value="3">Leaf Category
		<input type="checkbox" name="testType" value="4">Leaf Category Turn Page Only
		<input type="checkbox" name="testType" value="5">One Product
		<br>
		Level:
		<select name="logLevel">
			<% 
				LoggerContext context = (LoggerContext) LogManager.getContext(false);
				Configuration logconfig = context.getConfiguration();
				LoggerConfig loggerConfig = logconfig.getLoggerConfig("org.cld");
				Level l = loggerConfig.getLevel();
				List<String> strLevels = new ArrayList<String>();
				for (Level lvl: Level.values()){
					strLevels.add(lvl.name());
				}
			%>
			<%= HtmlUtil.genOptionList(strLevels, l.name())%>
		</select>
		<input type="submit" name="command" value="test" onclick="submitOpenNew();">
		<input type="submit" name="command" value="deploy" onclick="submitNoNew();">
		<input type="submit" name="command" value="undeploy" onclick="submitNoNew();">
		<input type="submit" name="command" value="listRunningTasks" onclick="submitOpenNew();">
		<input type="submit" name="command" value="deleteCatPrd" onclick="submitNoNew();">
		<input type="submit" name="command" value="cancel" onclick="submitNoNew();">
		<input type="submit" name="command" value="export" onclick="submitNoNew();">
	</form>
	<form action="/cldwebconf/Upload" method="post" enctype="multipart/form-data">
	    <input type="file" name="file" />
	    <input type="submit" />
	</form>
	<br>
	New:
	<form action="/cldwebconf/CrawlConf" method="post">
		Site Id: <input type="text" name="siteconfid"><br>
  		Start Url: <input type="text" name="starturl" size="100"><br>
  		User Id: <input type="text" name="userid" value="cr"><br>
  		<input type="submit" name="command" value="new">
	</form>
</body>
</html>