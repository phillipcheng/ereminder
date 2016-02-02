<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Book Content</title>
</head>
<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.json.JSONObject"%>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="cy.crbook.wsserver.CRBookWebUtil"%>
<%@ page import="cy.common.entity.Reading"%>
<%@ page import="cy.common.entity.Book"%>
<%@ page import="org.cld.util.PatternResult"%>


<%
Logger logger = LogManager.getLogger("cld.jsp");
String jsonData = request.getParameter(CRBookWebUtil.PARAM_BOOK_JSON_DATA);	
jsonData = URLDecoder.decode(jsonData, "UTF-8");
logger.info(String.format("jsonData got:%s", jsonData));
JSONObject jobj = new JSONObject(jsonData);
jobj = jobj.getJSONObject("Book");
String siteconfid = request.getParameter(CRBookWebUtil.PARAM_SITECONF_ID);
Book book = new Book();
book.fromTopJSONObject(jobj);
%>
<script>
var currentFirstPage =0;
var pageUrls = [
<% 
PatternResult pr = book.getPageBgUrlPattern();
for (int i=1; i<=book.getTotalPage(); i++) {
	String bgUrl = PatternResult.guessUrl(pr, i-pr.getStartImageCount());
	if (i<book.getTotalPage()){
%>		"<%=bgUrl%>",
<%	}else{
%>		"<%=bgUrl%>"
<%
	}
}
%>
];
var bookType=<%=book.getType()%>;
function getBgUrls(startpage, pagesize, next){
	var retUrls = [];
	var endpage;
	var beginpage;
	if (next==true){
		beginpage = startpage;
		endpage = startpage+pagesize;
		if (endpage>pageUrls.length){
			endpage = pageUrls.length;
		}
	}else{
		endpage=startpage;
		beginpage=startpage-pagesize;
		if (beginpage<1){
			beginpage=1;
		}
	}
	
	for (var i=beginpage; i<endpage; i++){
		retUrls.push(pageUrls[i]);
	}
	return retUrls;
}
function genPages(next){
	document.getElementById("totalPage").value=<%=book.getTotalPage()%>
	pagesize = parseInt(document.getElementById("pagesize").value);
	retUrls = getBgUrls(currentFirstPage, pagesize, next);
	var orgCurrentFirstPage = currentFirstPage;
	var currentPages = "";
	if (next){
		currentFirstPage +=retUrls.length;
		for (i=orgCurrentFirstPage; i<currentFirstPage; i++){
			currentPages += i + ',';
		}
	}else{
		currentFirstPage -=retUrls.length;
		for (i=currentFirstPage; i<orgCurrentFirstPage; i++){
			currentPages += i + ',';
		}
	}
	var parentDiv = document.getElementById("pages");
	//remove old
	while( parentDiv.hasChildNodes() ){
		parentDiv.removeChild(parentDiv.lastChild);
	}
	//add new
	for (i=0; i<retUrls.length; i++){
		var newSpan = document.createElement("span");
		newSpan.id="page" + i;
		var newEle;
		if (bookType==<%=Reading.TYPE_PIC%>){
			newEle= document.createElement("img");
			newEle.setAttribute("src", retUrls[i]);
			newSpan.appendChild(newEle);
		}
		parentDiv.appendChild(newSpan);
	};
	for (i=0; i<retUrls.length; i++){
		if (bookType==<%=Reading.TYPE_NOVEL%>){
			$("#" + "page"+i).load( "/cldwebconf/jsp/GetContent.jsp?currenturl=" + retUrls[i] + "&siteconfid=" + "<%=siteconfid%>");
		}
	}
	document.getElementById("currentPage").value = currentPages;
}
</script>
<body onload="genPages(true)">
	<div id="pages">
		<!-- <imag href=""></imag>-->
	</div>
	<span>
		<button id="previous" onclick="genPages(false)">Previous</button>
		Current Page: <input id="currentPage" type="text" value="" size=5 readonly>
		Total Page: <input id="totalPage" type="text" value="" size=5 readonly>
		<button id="next" onclick="genPages(true)"/>Next</button>
		<input id="pagesize" type="text" value="2">
	</span>
</body>
</html>