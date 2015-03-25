/**
 * Gets an XPath for an element which describes its hierarchical location.
 */
var getElementXPath = function(element) {
    if (element && element.id)
        return '//*[@id="' + element.id + '"]';
    else
        return getElementTreeXPath(element);
};

var getElementTreeXPath = function(element) {
    var paths = [];

    // Use nodeName (instead of localName) so namespace prefix is included (if any).
    for (; element && element.nodeType == 1; element = element.parentNode)  {
        var index = 0;
        // EXTRA TEST FOR ELEMENT.ID
        if (element && element.id) {
            paths.splice(0, 0, '/*[@id="' + element.id + '"]');
            break;
        }

        for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {
            // Ignore document type declaration.
            if (sibling.nodeType == Node.DOCUMENT_TYPE_NODE)
              continue;

            if (sibling.nodeName == element.nodeName)
                ++index;
        }

        var tagName = element.nodeName.toLowerCase();
        var pathIndex = (index ? "[" + (index+1) + "]" : "");
        paths.splice(0, 0, tagName + pathIndex);
    }

    return paths.length ? "/" + paths.join("/") : null;
};

function getStringByXpath(path, element){
	try{
    	return document.evaluate(path, element, null, XPathResult.STRING_TYPE, null).stringValue;
	}catch(err){
		console.log(err);
		return null;
	}
}
function getElementByXpath(path, element) {
	if (!element){
		element=document;
	}
	try{
    	return document.evaluate(path, element, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
	}catch(err){
		console.log(err);
		return null;
	}
};

function getAnyByXpath(path, element) {
	try{
    	return document.evaluate(path, element, null, XPathResult.ANY_TYPE, null);
	}catch(err){
		console.log(err);
		return null;
	}
};

function siteconfinit(){
	pwylInitializeWithEditor();
	editorSetup();
	var inputnodes = YAHOO.util.Selector.query('#xmlForm input');
	var textareas = YAHOO.util.Selector.query('#xmlForm textarea');
	var nodes = inputnodes.concat(textareas);
	//YAHOO.util.Event.on(nodes, 'keyup', highlightXPathWithEvent);
	for (var i=0; i<textareas.length; i++){
		textAreaAutoSize(textareas[i]);
	}
	var urlInputs = YAHOO.util.Selector.query('#xmlForm textarea[name="@startUrl"]');
	urlInputs = urlInputs.concat(YAHOO.util.Selector.query('#xmlForm textarea[name="#sampleUrl"]'));
	for (var i=0; i<urlInputs.length; i++){
		addSubmitForUrl(urlInputs[i]);
	}
	toggleHighlightPage();
}

function submitXmlForm(){
	document.xmlForm.submit();
}
//input
//<div>
//	<textarea value=url>url</textarea> <--input ele
//	<div>url</div>
//</div>
//
//target output
//<div>
//	<textarea value=url>url</textarea> <-- element
//	<div>url</div>
//</div>
//<input type="submit" onclick="findUrlAndSubmit(this);> <--created new input
function addSubmitForUrl(element){
	var input = document.createElement('input');
	input.type="submit";
	input.id="reload";
	input.name="command";
	input.value="reload";
	element.parentNode.parentNode.appendChild(input);
	var orgHtml = input.outerHTML;
	try{
		element.parentNode.parentNode.lastChild.outerHTML = orgHtml.substring(0, orgHtml.length-1) + ' onclick="findUrlAndSubmit();"' + '>';
	}catch(err){
		console.log(err);
	}
	
}

function findUrlAndSubmit(){
	//<div>
	//	<textarea value=url>url</textarea>
	//	<div>url</div>
	//</div>
	//<input type="submit" onclick="findUrlAndSubmit(this);> <--input ele
	//find the url from element
	ele = window.event.srcElement;
	url = ele.parentNode.getElementsByTagName('div')[0].firstChild.value;
	
	setReloadUrlAndSubmit(url);
}

function setReloadUrlAndSubmit(url, enableJS){
	document.getElementById("reloadurl").value = url;
	document.getElementById("enableJS").value = enableJS;
	document.xmlForm.submit();
}

function getAbsUrl(element){
	var orgElement = element;
	while (element.nodeName.toLowerCase() != 'a' && element.id!='ppw_page_body'){
		element=element.parentNode;
	}
	if (element.nodeName.toLowerCase() == 'a'){
		return element.href;
	}else{
		//try going down
		var anchor = getElementByXpath("./a[1]", orgElement);
		if (anchor){
			return anchor.href;
		}else{
			return null;
		}
	}
}

var textAreaAutoSize = function (ele) {
  ele.parentNode.childNodes[1].innerHTML = ele.value + '\n';
};

var orgEleBorderCSS= {};//element's original border css
function highlightXPathWithEvent(){
    var xpath = window.event.srcElement.value;
    highlightXPath(xpath, true);
}

String.prototype.replaceAll = function(search, replace)
{
    //if replace is null, return original string otherwise it will
    //replace search string with 'undefined'.
    if(!replace) 
        return this;

    return this.replace(new RegExp(search, 'g'), replace);
};

function highlightXPath(xpath, hl, element){
	try{
		if (!element){
			element = document;
		}
		//replace xapth if needed
		xpath = xpath.replaceAll('//body', '//*[@id="ppw_page_body"]');
		var anyStuff = getAnyByXpath(xpath, element);
		var oneStuff = anyStuff.iterateNext();
		while (oneStuff){
			//move up to the parent who is a node, since we can't add border to text
			while (oneStuff.nodeType!=1){//
				oneStuff = oneStuff.parentNode;//attribute's parentNode is null? TODO
			}
			if (hl){
				var eleKey = getElementXPath(oneStuff);
				if (typeof orgEleBorderCSS[eleKey]=='undefined'){
					if (oneStuff.style){
						orgEleBorderCSS[eleKey]=oneStuff.style.border;
						oneStuff.style.border="3px solid red";
					}else{
						oneStuff.style = "border:3px solid red";
					}
				}else{
					//if already set, no reset.
				}
			}else{
				if (typeof orgEleBorderCSS[eleKey]!='undefined')
					oneStuff.style.border = orgEleBorderCSS[eleKey];
				else{
					oneStuff.style = undefined;
				}
			}
			oneStuff = anyStuff.iterateNext();
		}
		if (!hl){
			orgEleBorderCSS={};
		}
	}catch(err){
		console.log(err);
	}
}

//find the current task definition, high light the corresponding xpath, collapse other task definitions
var pageHighlighted=false;
function toggleHighlightPage(){
	var currenturl = document.getElementById("currenturl").value;
	var urlInputs = YAHOO.util.Selector.query('#xmlForm textarea[name="@startUrl"]');
	urlInputs = urlInputs.concat(YAHOO.util.Selector.query('#xmlForm textarea[name="#sampleUrl"]'));
	var selectedTextarea;
	for (var i=0; i<urlInputs.length; i++){
		if (urlInputs[i].value === currenturl){
			selectedTextarea = urlInputs[i];
			break;
		};
	}
	var selectedBrowseTask;
	if (selectedTextarea!=null){
		selectedBrowseTask = selectedTextarea.parentNode;
		while (selectedBrowseTask!=null && (selectedBrowseTask.tagName!='FIELDSET' || 
				(selectedBrowseTask.getAttribute('class')!='CatTask' && selectedBrowseTask.getAttribute('class')!='PrdTask'))){
			selectedBrowseTask = selectedBrowseTask.parentNode;
		}
		
		if (selectedBrowseTask!=null){
			selectedBrowseTask = selectedBrowseTask.parentNode;//to XMLComplexContent Level
			//expand selected Browse Task and collapse other unselected Browse Task
			var browseTaskXpath = "//form[@name='xmlForm']/div[1]/fieldset[1]/div[@class='XMLComplexContent']";
			var ccit = getAnyByXpath(browseTaskXpath, document);
			var browseTaskNodeList = [];
			var cc = ccit.iterateNext();
			while (cc){
				browseTaskNodeList.push(cc);
				cc = ccit.iterateNext();
			}
			for (var i=0; i<browseTaskNodeList.length; i++){
				if (selectedBrowseTask.isSameNode(browseTaskNodeList[i])){
					//collapse the idUrlMapping for the selected task
					var idUrlMappingXpath = ".//fieldset[@class='idUrlMapping']";
					var idUrlMapping = getElementByXpath(idUrlMappingXpath, browseTaskNodeList[i]);
					if (idUrlMapping){
						var idUrlMappingEle = idUrlMapping.parentNode;
						collapseElm(idUrlMappingEle);
					}
					//high light the corresponding xpath for this selected task
					var xpathValues = YAHOO.util.Selector.query("textarea[value^=//]", selectedBrowseTask);
					for (var j=0; j<xpathValues.length; j++){
						highlightXPath(xpathValues[j].value, !pageHighlighted);
					};
					
					//high light the relative xpath of subItemList for only BrowseCatTask
					//1. get the absolute xpath of @itemList in subItemList
					var itemListXpath = ".//fieldset[@class='itemList']//span[@class='@value']/div[1]/textarea/@value";
					var itemListXpathVal = getStringByXpath(itemListXpath, selectedBrowseTask);
					if (itemListXpathVal){
						var relativeXpath=[];
						//2. get relative xpath of itemFullUrl
						var itemFullUrlRelXpath = ".//span[@class='@itemFullUrl']/div[1]/textarea/@value";
						var itemFullUrlRelXpathVal = getStringByXpath(itemFullUrlRelXpath, selectedBrowseTask);
						if (itemFullUrlRelXpathVal){
							relativeXpath.push(itemFullUrlRelXpathVal);
						}
						//3. get lastItem relative xpath
						var lastItemRelXpath = ".//span[@class='@lastItem']/div[1]/textarea/@value";
						var lastItemRelXpathVal = getStringByXpath(lastItemRelXpath, selectedBrowseTask);
						if (lastItemRelXpathVal){
							relativeXpath.push(lastItemRelXpathVal);
						}
						
						//4. get relative xpath of name (for each item)
						var nameRelXpath = ".//fieldset[@class='name']//span[@class='@value']/div[1]/textarea/@value";
						var nameRelXpathVal = getStringByXpath(nameRelXpath, selectedBrowseTask);
						if (nameRelXpathVal){
							relativeXpath.push(nameRelXpathVal);
						}
						
						//5. get relative xpath of various user attributes
						var userAttrsXpath = ".//fieldset[@class='userAttribute']//span[@class='@value']/div[1]/textarea/@value";
						var userAttrsXpathValIT = getAnyByXpath(userAttrsXpath, selectedBrowseTask);
						var userAttrsXpathValOne = userAttrsXpathValIT.iterateNext(); 
						while (userAttrsXpathValOne){
							relativeXpath.push(userAttrsXpathValOne.nodeValue);
							userAttrsXpathValOne = userAttrsXpathValIT.iterateNext(); 
						}
						
						//6. show all relative xpath
						for (var j=0; j<relativeXpath.length; j++){
							highlightXPath(itemListXpathVal + "/" + relativeXpath[j], !pageHighlighted);
						}
					}else{
						
					}
				}else{
					collapseElm(browseTaskNodeList[i]);
				}
			}
			
			pageHighlighted = !pageHighlighted;
		};
	};
	
	removeHighZIndexAds();
};

function removeHighZIndexAds(){
	var divs = YAHOO.util.Selector.query('#ppw_page_body div[style*="z-index"]');
	var divs2 = YAHOO.util.Selector.query('body>div[style*="z-index"]');
	divs = divs.concat(divs2);
	for (var i=0; i<divs.length; i++){
		var style = divs[i].getAttribute("style");
		if (style){
			//style="position: fixed; z-index: 9999; extract 9999 from this
			var re = /(.*z-index:\s*)(\d*)(;.*)/;
			var zindex= style.replace(re, "$2");
			if (zindex && parseInt(zindex)>9500){
				divs[i].style.visibility='hidden';
			}
		}
	}
}

function toggleSelectAll(source, selectColumnName) {
  var checkboxes = document.getElementsByName(selectColumnName);
  for (var i=0; i<checkboxes.length; i++){
	  checkbox = checkboxes[i];
	  checkbox.checked = source.checked;
  };
};
