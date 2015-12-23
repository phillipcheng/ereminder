// Copyright 2004-5 by Edmund K. Lai

// function determine the type of node

XMLAnonymous = 2;
XMLAttribute = 1;
XMLSimpleType = 3;
XMLSimpleContent = 4;
XMLComplexContent = 5;
  
function xmlType(frag) {
  // TBD : use index
  switch (frag.className) {
  case 'XMLAttribute':
    return XMLAttribute;
  case 'XMLSimpleContent':
    return XMLSimpleContent;
  case 'XMLComplexContent':
    return XMLComplexContent;
  case 'XMLSimpleType':
    var span = frag.getElementsByTagName('span');
    if (span[0] && span[0].className == 'XMLAnonymous') return XMLAnonymous;
    return XMLSimpleType;
  default:
    return 0;
  }
}
  
function isSimpleWithAttribute(frag) {
  return (xmlType(frag) == XMLSimpleType && xmlType(frag.parentNode) == XMLSimpleContent);
}
  
function isDeleted(frag) {
  while (frag && frag.nodeName != 'FORM') {
    frag = frag.parentNode;
    if (frag.nodeName == 'DEL') return true;
  }
  return false;
}
  
function isRoot(frag) {
  return (frag.parentNode.tagName == 'FORM');
}

function getRoot() {
  return getChildByTagNames(document.xmlForm, ['DIV']);
}

function rejectRoot(item) {
  if (isRoot(item)) {
    alert('You cannot do this to the root element');
    return true;
  }
  return false;
}

function markUpCompatiable(str) {
  var result = str.replace(/&/g, '&amp;');
  result = result.replace(/</g, '&lt;');
  result = result.replace(/>/g, '&gt;');
  result = result.replace(/'/g, '&apos;');
  return result.replace(/"/g, '&quot;'); // " to make project builder happy
}
  
function subtractFromArray(theList, items) {
  for (var i=0;i<items.length;i++) {
    for (var j=0;j<theList.length;j++) {
      if (items[i] === theList[j]) {
        theList.splice(j,1);
        break;
      }
    }
  }  
}

function indexOfItemInArray(theList, item) {
  for (var j=0;j<theList.length;j++) {
    if (item === theList[j]) {
      return j;
    }
  }
  return -1;
}
  
function indexOfValueInOptions(options, value) {
  for (var j=0;j< options.length;j++) {
    if (value == options[j].value)  return j;
  }
  return -1;
}
  
function addOptionToSelect(select, optionName, optionValue, before) {
  var options = select.options;
  if (indexOfValueInOptions(options, optionValue) != -1) return; // already there, no need to add
  if (before >= 0) {
    // first make a copy
    var copy = new Array();
    for (i=0;i<options.length;i++) {
      copy.push(options[i]);
    }
    copy.splice(before,0,new Option(optionName, optionValue, false, false))
    // since we can only append, we need to delete everything and put things back to insert
    options.length = 0;
    for (var i=0;i<copy.length;i++) {
      options[i] = copy[i];
    }
  } else {
    options[options.length] = new Option(optionName, optionValue, false, false);
  }
}

function removeOptionFromSelect(select, index) {
  var options = select.options;
  var i;
  // first make a copy
  var copy = new Array();
  for (i=0;i<options.length;i++) {
    if (i != index) copy.push(options[i]);
  }
  options.length = 0;
  for (i=0;i<copy.length;i++) {
    options[i] = copy[i];
  }
}

// walking XML tree by walking HTML tree

function getChildByTagNames(item, names) {
//    if (!(item.hasChildNodes())) return null; // not supported by IE
  if (!item.childNodes || !item.childNodes.length) return null;
  var nodeList = item.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    var nodeTag = node.tagName;
    if (nodeTag) {
      for (var j=0; j<names.length; j++) {
        if (nodeTag == names[j]) return node;
      }
    }
  }
  return null;
}

function doToEachChild(item, func, param1, param2 ) {
  var nodeList = item.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    if (node.nodeType != 1) continue;
    if (node.tagName == 'DEL') {
      node = node.firstChild;
    }
    if (xmlType(node)) {
      func(node, param1, param2);
    }
  }
}

function getXMLElementName(xmlNode) {
  var nodetype = xmlType(xmlNode);
  if (!nodetype) return '';
  if (nodetype == XMLAnonymous) return '#string';
  if (nodetype == XMLSimpleContent) xmlNode = getChildByTagNames(xmlNode, ['DIV','SPAN']);
  return getChildByTagNames(xmlNode, ['FIELDSET','DIV','SPAN']).className;
}

function getXMLInputValue(xmlNode) {
  var input = getInputOfItem(xmlNode);
  return (input ? getInputValue(input) : null);
}

function getXMLParent(xmlNode) {
  if (isRoot(xmlNode)) return null;
  var parent = xmlNode.parentNode;
  if (parent.tagName == 'DEL') parent = parent.parentNode;
  if (xmlType(parent) == XMLSimpleContent) {
    if (xmlType(xmlNode) == XMLAttribute) {
      return getChildByTagNames(parent, ['DIV','SPAN']); // ???
    }
    parent = parent.parentNode;
    if (parent.tagName == 'DEL') parent = parent.parentNode;
  }
  return parent.parentNode;
}

function getXMLChildren(xmlNode, includeAttribute) {
  var nodeType = xmlType(xmlNode);
  if (nodeType <= XMLSimpleContent) return null;
//  var includeType = includeAttribute ? XMLAnonymous : XMLSimpleType;
  var result = new Array();
  var container = getChildByTagNames(xmlNode, ['FIELDSET','DIV','SPAN']);
  var nodeList = container.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    nodeType = xmlType(node);
//      if (xmlType(node) >= includeType) {
    if (nodeType && (includeAttribute || nodeType != XMLAttribute)) {
      result.push(node);
    }
  }
  return result;
}
  
function getNamedXMLChild(xmlNode, name) {
  var nodeType = xmlType(xmlNode);
  if (nodeType < XMLSimpleContent) return null;
  var container = (nodeType == XMLSimpleContent) ? xmlNode : getChildByTagNames(xmlNode, ['FIELDSET','DIV','SPAN']);
  var nodeList = container.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    var nodeType = xmlType(node);
    if (nodeType) {
      var nodeName = getXMLElementName(node);
      if (nodeType == XMLAttribute) nodeName = '@' + nodeName;
      if (name == nodeName) return node;
    }
  }
  return null;
}
  
function getXMLAttributeNames(xmlNode) {
  var nodeType = xmlType(xmlNode);
  if (nodeType < XMLSimpleType) return null;
  var result = new Array();
  var container;
  switch (nodeType) {
    case XMLSimpleType:
      container = xmlNode.parentNode;
      if (xmlType(container) != XMLSimpleContent) return null;
      nodeType = XMLSimpleContent; // now claim to be container
      break;
    case XMLSimpleContent:
      container = xmlNode;
      break;
    default: 
      container = getChildByTagNames(xmlNode, ['FIELDSET','DIV','SPAN']);
  }
  var nodeList = container.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    var childType = xmlType(node);
    if (childType >= XMLSimpleType) {
      if (nodeType == XMLSimpleContent && childType == XMLSimpleType) continue;
      break;
    } else if (childType == XMLAttribute) {
      result.push('<@' + getXMLElementName(node) + '>');
    }
  }
  return result;
}
  
function getXMLPrevSibling (xmlNode) {
  var result = xmlNode;
  while ((result = result.previousSibling) != null) {
    if (xmlType(result)) return result;
  }
  return null;
}

function getXMLNextSibling (xmlNode) {
  var result = xmlNode;
  while ((result = result.nextSibling) != null) {
    if (xmlType(result)) return result;
  }
  return null;
}

function getXMLFirstChild(xmlNode, attributeToo) {
  var nodeType = xmlType(xmlNode);
  if (nodeType < XMLSimpleContent) return null;
  var container = (nodeType == XMLSimpleContent) ? xmlNode : getChildByTagNames(xmlNode, ['FIELDSET','DIV','SPAN']);
  var nodeList = container.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    if (xmlType(node) >= (attributeToo ? XMLAttribute : XMLAnonymous)) return(node);
  }
  return null;
}

function getXMLLastChild(xmlNode, attributeToo) {
  var nodeType = xmlType(xmlNode);
  if (nodeType < XMLSimpleContent) return null;
  var container = (nodeType == XMLSimpleContent) ? xmlNode : getChildByTagNames(xmlNode, ['FIELDSET','DIV','SPAN']);
  var nodeList = container.childNodes;
  for (var i=nodeList.length-1;i>=0;i--) {
    var node = nodeList[i];
    if (xmlType(node) >= (attributeToo ? XMLAttribute : XMLAnonymous)) return(node);
  }
  return null;
}
  
// end of walking XML tree by walking HTML tree

function doubleQuote(x) {
  return '"' + x + '"';
}

function singleQuote(x) {
  return "'" + x + "'";
}
