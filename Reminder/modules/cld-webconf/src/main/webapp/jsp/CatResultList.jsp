<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Category List</title>
</head>
<body>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.cld.webconf.ConfServlet" %>
<%@ page import="org.apache.logging.log4j.LogManager"%>
<%@ page import="org.apache.logging.log4j.Logger"%>
<%@ page import="org.cld.util.entity.Category" %>
<%@ page import="org.cld.util.entity.Product" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.cld.webconf.ServletUtil" %>

<%	Logger logger = LogManager.getLogger("cld.jsp");
	String userId="cr"; 

	String siteconfid = request.getParameter(ConfServlet.REQ_PARAM_SITECONF_ID);
	String pcatid = request.getParameter(ConfServlet.REQ_PARAM_PCAT_ID);
	
	logger.info(String.format("cats to display for siteconfid: %s, pcatId: %s", siteconfid, pcatid));
	List<Category> catlist = new ArrayList<Category>();
	List<Product> prdlist = new ArrayList<Product>();
	String grandParentCatId=null;
	if (siteconfid!=null){
		if (pcatid==null){
			List<Category> rootCatList = ConfServlet.cconf.getDefaultDsm().getCategoryByPcatId(siteconfid, null);
			if (rootCatList.size()!=1){
				logger.error(String.format("root cat list wrong: %s for siteid:%s", rootCatList, siteconfid));
			}else{
				Category rootCat = rootCatList.get(0);
				catlist.add(rootCat);
				pcatid = rootCat.getId().getId();
			}
		}else{
			//get grandParentCatId
			Category pcat = (Category)ConfServlet.cconf.getDefaultDsm().getCrawledItem(pcatid,siteconfid,Category.class);
			grandParentCatId = pcat.getParentCatId();
		}
		List<Category> secondLevelCats = ConfServlet.cconf.getDefaultDsm().getCategoryByPcatId(siteconfid, pcatid);
		catlist.addAll(secondLevelCats);
		
		prdlist = ConfServlet.cconf.getDefaultDsm().getProductByPcatId(siteconfid, pcatid);
		
	}else{
		logger.error("sitconfid is null.");
	}
%>
	<div id="navPanel">
		<a name="ToSiteConfList" href=<%=ServletUtil.genViewSiteConfUrl() %>>[Siteconf List]</a>
		<a name="ToRootCat" href=<%=ServletUtil.genViewCatUrl(siteconfid, null) %>>[Root Cat]</a>
<%if (grandParentCatId!=null) {%>
		<a name="ToParentCat" href=<%=ServletUtil.genViewCatUrl(siteconfid, grandParentCatId) %>>[Parent Cat]</a>
<%} %>
	</div>
	

	<%@include file="catlist.jspf" %>
	<%@include file="prdlist.jspf" %>

</body>
</html>