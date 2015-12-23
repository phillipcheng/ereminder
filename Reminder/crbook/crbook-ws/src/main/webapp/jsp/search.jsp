<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.cld.util.PagingUtil"%>
<%@ page import="org.cld.util.HtmlUtil"%>
<%@ page import="cy.common.entity.Reading" %>
<%@ page import="cy.common.entity.Book" %>
<%@ page import="cy.common.entity.Volume" %>
<%@ page import="cy.crbook.wsserver.InitListener" %>
<%@ page import="cy.crbook.persist.JDBCPersistService" %>
<%@ page import="cy.crbook.persist.SetBookNum" %>
<%@ page import="cy.crbook.persist.SetVolumeCover" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="../js/pages.js"></script>
<title>Search</title>
</head>
<body>
<%
	Logger logger = LogManager.getLogger("crbook.jsp");
	String command = request.getParameter("command");
	String searchString = request.getParameter(InitListener.PARAM_SEARCH_STRING);
	if (searchString==null){
		searchString="";
	}
	int readingType=Reading.TYPE_PIC;
	String strReadingType = request.getParameter(InitListener.PARAM_READING_TYPE);
	if (strReadingType!=null){
		readingType = Integer.parseInt(strReadingType);
	}
	String parentCat = request.getParameter("parentCat");
	JDBCPersistService pService = (JDBCPersistService)application.getAttribute(InitListener.PERSIST_MANAGER_KEY);
	int pageLimit = 20;
	String strLimit =request.getParameter("bookPageLimit");
	if (strLimit!=null){
		pageLimit = Integer.parseInt(strLimit);
	}
	String strCurPage = request.getParameter("currentPage");
	int curPage=0;
	long bookCount=0;
	long volumeCount=0;
	long bookTotalPage=0;
	long volumeTotalPage=0;
	long totalPage=0;
	List<Book> bookList = new ArrayList<Book>();
	List<Volume> volumeList = new ArrayList<Volume>();
	if (strCurPage!=null){
		curPage = Integer.parseInt(strCurPage);
	}
	if (command!=null){
		if (command.equals("setBookNum")){
			SetBookNum setBookNum = new SetBookNum(pService);
			setBookNum.setBookNum();
		}else if (command.equals("setCoverUri")){
			SetVolumeCover svc = new SetVolumeCover(pService);
			svc.setAllCoverUrl();
		}
	}
	if (parentCat==null || "".equals(parentCat)){
		if (searchString!=null && !"".equals(searchString)){
			bookList= pService.getBooksByName(searchString, readingType, curPage*pageLimit, pageLimit);
			bookCount = pService.getBCByName(searchString, readingType);
			bookTotalPage = PagingUtil.getPageNum(bookCount,pageLimit);
			
			volumeList = pService.getVolumesLike(searchString, readingType, curPage*pageLimit, pageLimit);
			volumeCount = pService.getVCLike(searchString, readingType);
			volumeTotalPage = PagingUtil.getPageNum(volumeCount,pageLimit);
		}else{
			bookList = pService.getLatestBooks(readingType, curPage*pageLimit, pageLimit);
			bookTotalPage=9999999;//unlimited
		}
	}else{
		List<String> catlist = new ArrayList<String>();
		if ("Root".equals(parentCat)){
			catlist = Volume.getRootVolumes();
		}else{
			catlist.add(parentCat);
		}
		bookList = pService.getBooksByCat(catlist, curPage*pageLimit, pageLimit);
		bookCount = pService.getBCByCat(catlist);
		bookTotalPage = PagingUtil.getPageNum(bookCount,pageLimit);
		
		volumeList = pService.getVolumesByPCat(catlist, curPage*pageLimit, pageLimit);
		volumeCount = pService.getVCByPCat(catlist);
		volumeTotalPage = PagingUtil.getPageNum(volumeCount,pageLimit);
	}
	totalPage = Math.max(bookTotalPage, volumeTotalPage);
%>

<form action="search.jsp" method="post">
	<button id="previous" onclick="goPages(false, this);">Previous</button>
	Current Page: <input name="currentPage" type="text" value="<%=curPage%>" size=5 readonly>
	Total Page: <input name="totalPage" type="text" value="<%=totalPage%>" size=5 readonly>
	<button id="next" onclick="goPages(true, this);">Next</button>
	<input name="pageLimit" type="text" value="<%=pageLimit%>">
	<br>
	<select name="readingType">
	  <option <%=(readingType==Reading.TYPE_PIC?"selected":"")%> value=<%=Reading.TYPE_PIC%>>Picture</option>
	  <option <%=(readingType==Reading.TYPE_NOVEL?"selected":"")%> value=<%=Reading.TYPE_NOVEL%>>Novel</option>
	</select>
	<select name="parentCat">
<%
	if (parentCat==null || "".equals(parentCat)){
		parentCat = "None";
	}
	List<String> strOptions = new ArrayList<String>();
	strOptions.add("None");
	strOptions.add("Root");
	if (!strOptions.contains(parentCat)){
		strOptions.add(parentCat);
	}
%>
	<%=HtmlUtil.genOptionList(strOptions, parentCat)%>

	</select>
	<input type="text" size=100 name="searchString" value="<%=searchString%>">
	<input type="submit" name="command" value="search">
	<input type="submit" name="command" value="setBookNum">
	<input type="submit" name="command" value="setCoverUri">
</form>
<%@include file="volumeList.jspf" %>
<%@include file="bookList.jspf" %>


</body>
</html>