<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.cld.datastore.entity.Logs" %>
<%@ page import="org.cld.webconf.InitListener" %>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.cld.datastore.entity.Category" %>
<%@ page import="org.cld.datastore.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.cld.datastore.api.DataStoreManager"%>
<% Logger logger = LogManager.getLogger("cld.jsp");%>
<% String userId="cr"; %>
<% DataStoreManager pm = ConfServlet.getCConf().getDsm();%>
<%
	String taskId = request.getParameter(ConfServlet.REQ_PARAM_TEST_TASKID);
	String command = request.getParameter(ConfServlet.CMD_KEY);
	if (ConfServlet.CMD_CANCEL.equals(command)){
		ConfServlet.getCrawlTestNode().getTaskNode().getTaskInstanceManager().removeTaskFromExecutor(taskId);
	}
%>
<%
	String status = "Finished";
	if (ConfServlet.getCrawlTestNode().getTaskNode().getTaskInstanceManager().hasTask(taskId)){
		status = "Running";
	}
	List<Category> catlist= ConfServlet.getCConf().getDsm().getCategoryByRootTaskId(taskId);
	List<Product> prdlist= ConfServlet.getCConf().getDsm().getProductByRootTaskId(taskId);
%>
Task: <%=taskId%>, Status:<%=status%>
<% List<Logs> loglist = pm.getLogsByTask(taskId, 0, 100);%>
<div>
	<%@include file="catlist.jspf" %>
	<%@include file="prdlist.jspf" %>
	<%@include file="loglist.jspf" %>
</div>




