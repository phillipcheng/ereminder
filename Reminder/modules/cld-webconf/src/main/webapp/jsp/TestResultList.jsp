<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>SiteConf List</title>
<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
</head>
<body>
<script>
function loadTaskLog(taskid){
	$("#" + taskid).load( "/cldwebconf/jsp/TestResult.jsp?command=list&taskid=" + taskid);
}
function cancelTask(taskid){
	clearInterval(refreshId);
	$("#" + taskid).load( "/cldwebconf/jsp/TestResult.jsp?command=cancel&taskid=" + taskid);
}
</script>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>

<%
	Logger logger = LogManager.getLogger("cld.jsp");
	String userId="cr"; 
	List<String> taskidList = new ArrayList<String>();
 	//selected tasks
	String[] taskids = request.getParameterValues(ConfServlet.REQ_PARAM_TEST_TASKIDS);
 	if (taskids!=null && taskids.length>0){
 		taskidList.addAll(Arrays.asList(taskids));
 	}else{
 		//no specific tasks specified, list all runing tasks
 		Set<String> runningTaskIds = ConfServlet.getCrawlTestNode().getTaskNode().getTaskInstanceManager().getRunningTasks();
 		if (runningTaskIds!=null){
	 		for (String taskid: runningTaskIds){
	 			if (!taskidList.contains(taskid)){
	 				taskidList.add(taskid);
	 			}
	 		}
 		}
 	}

	logger.info("tasks to display:" + taskidList.size());
	for (String taskid: taskidList){
%>
	<button onclick="loadTaskLog('<%=taskid%>')">Reload</button>
	<button onclick="cancelTask('<%=taskid%>')">Cancel</button>
	<div id="<%=taskid%>">	
	</div>
<% } 
%>
<script>
	var refreshId = setInterval(function(){
		<%for (String taskid:taskidList){
		%>
			loadTaskLog('<%=taskid%>');
		<%
		}
		%>
	}, 2000);
</script>
</body>
</html>