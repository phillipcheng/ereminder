/**
 * 
 */

function goPages(isNext, triggerElement){
	var paginationForm = triggerElement.parentNode;
	var currentPageNode = document.evaluate("./input[1]", paginationForm, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
	var strCurrentPage = document.evaluate("./input[1]/@value", paginationForm, null, XPathResult.STRING_TYPE, null).stringValue;
	var strTotalPage = document.evaluate("./input[2]/@value", paginationForm, null, XPathResult.STRING_TYPE, null).stringValue;
	var strPageSize= document.evaluate("./input[3]/@value", paginationForm, null, XPathResult.STRING_TYPE, null).stringValue;
	var curPage = parseInt(strCurrentPage);
	var pageSize = parseInt(strPageSize);
	var totalPage = parseInt(strTotalPage);
	var skipSubmit=false;
	if (isNext){
		if (curPage + 1 > totalPage){
			skipSubmit=true;
		}else{
			curPage = curPage + 1;
		}
	}else{
		if (curPage - 1 < 0){
			skipSubmit = true;
		}else{
			curPage = curPage - 1;
		}
	}
	currentPageNode.value = curPage;
	if (!skipSubmit){
		paginationForm.submit();
	}
}