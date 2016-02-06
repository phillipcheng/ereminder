<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.cld.util.entity.Logs" %>
<%@ page import="org.cld.webconf.InitListener" %>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.cld.util.entity.Category" %>
<%@ page import="org.cld.util.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.cld.datastore.api.DataStoreManager"%>
<% Logger logger = LogManager.getLogger("cld.jsp");%>
<% String userId="cr"; %>
<% DataStoreManager pm = ConfServlet.cconf.getDefaultDsm();%>
<%
	String taskId = request.getParameter(ConfServlet.REQ_PARAM_TEST_TASKID);
	String command = request.getParameter(ConfServlet.CMD_KEY);
%>
<%
	String status = "Finished";
	List<Category> catlist= ConfServlet.cconf.getDefaultDsm().getCategoryByRootTaskId(taskId);
	List<Product> prdlist= ConfServlet.cconf.getDefaultDsm().getProductByRootTaskId(taskId);
%>
Task: <%=taskId%>, Status:<%=status%>
<div>
	<%@include file="prdlist.jspf" %>
</div>




