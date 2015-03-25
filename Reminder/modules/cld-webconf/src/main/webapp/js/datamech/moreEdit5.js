
// Copyright 2004-05 by Edmund K. Lai

// to do: deleting item containing something already deleted
// io do: deleion of mixed content

// TBD : when make new element, take account of global/local

document.onkeydown = keyhit;
xmlSelection = null;
focusField = null;

isIE = false;
isMoz = false;
isOpera = false;
isSafari = false;
isKonqueror = false;
if (navigator.userAgent.indexOf('Opera') != -1 ) {
  isOpera = true;
} else if (navigator.userAgent.indexOf('Safari') != -1 ) {
  isSafari = true;
} else if (navigator.userAgent.indexOf('Konqueror') != -1 ) {
  isKonqueror = true;
} else if (navigator.userAgent.indexOf('Opera') != -1 ) {
  isOpera = true;
} else if (navigator.appName.indexOf('Microsoft') != -1) {
  isIE = true;
} else if (navigator.appName.indexOf('Netscape') != -1) {
  isMoz = true;
}

// trigger by editing

function textChanged(inputChanged) {
  var item = getItemOfInput(inputChanged);
  toUndo(setFieldValue, item, document.undoState.oldTextValue);
  var preference = document.preference;
  if (!preference.checkSchema) return;
  var errorStr = verifyInputField(inputChanged);
  if (errorStr) {
    alert(errorStr);
    if (preference.obeySchema) {
      inputChanged.select();
      inputChanged.focus();
    }
  }
}

function unnilIfChanged(inputChanged) {
  unnilItemOfInput(inputChanged);
  var item = getItemOfInput(inputChanged);
  inputChanged.setAttribute('onchange', 'textChanged(this)');
  // not needed for IE, what about opera or safari
  var v = inputChanged.value;
  var inputChanged = getInputOfItem(item);
  inputChanged.value = v;
  //
  textChanged(inputChanged);
}
  
function removeUndefinedFromSelect(select) {
  if (select.options[0].value == '') { // TBD : what if '' is a valid option
    unnilItemOfInput(select);
    removeOptionFromSelect(select, 0);
  }
}

function noUndefined(select) {
  removeUndefinedFromSelect(select);
  toUndo(setFieldValue, getItemOfInput(select), document.undoState.oldTextValue);
}

function removeNoRadio(input) {
  unnilItemOfInput(input);
  var nodeList = input.parentNode.childNodes;
  for (var i=0;i<nodeList.length;i++) {
    var node = nodeList[i];
    if (node.tagName == 'INPUT') {
      if (node.type == 'hidden' && node.value == '') {
        input.parentNode.removeChild(node);
      } else if (node.type == 'radio') {
        node.setAttribute('onchange', 'radioChanged(this)');
      }
    }
  }
  toUndo(setFieldValue, getItemOfInput(input), document.undoState.oldTextValue);
}

function radioChanged(input) {
  toUndo(setFieldValue, getItemOfInput(input), document.undoState.oldTextValue);
//    noUndo();
}

function radioClicked(input) {
  document.undoState.oldTextValue = getInputOfItem(getItemOfInput(input)).value;
}

function focusGained(input) {
  var tag = input.tagName;
  if (tag =='INPUT' || tag =='TEXTAREA') {
    focusField = getItemOfInput(input);
    document.undoState.oldTextValue = input.value;
    //Cheng Yi - Begin
    var xpath = getElementXPath(input);
    document.getElementById("xmlForm").setAttribute("selectedXPath", xpath);
    //Cheng Yi - End
  } else {
    focusField = null;
    if (tag == 'SELECT') document.undoState.oldTextValue = input.value;
  }
}

function keyhit(evt) {

  function switchFocus(evt) {
    var item = xmlSelection;
    if (item) {
      selectXMLForm();
      if (xmlType(item) <= XMLSimpleType) {
        var input = getInputOfItem(item);
        if (input) {
  //      var index = -1;
  //      if (input.tagName == 'SELECT') {
  //      index = input.selectedIndex;
  //      }
          input.focus();
          if (input.tagName != 'SELECT') input.select();
  //      if (index >= 0) {
  //      input.selectedIndex = index;
  //      }
        }
      }
    } else {
      var input = isIE ? evt.srcElement : evt.target;
      var tag = input.tagName;
      var isInput = (tag =='INPUT' || tag =='TEXTAREA' || tag =='SELECT' || tag =='OPTION');
      if (isInput) {
        input.blur();
        selectXMLForm(getItemOfInput(input));
      }
    }
    return false;
  }
    
  function getTab(item) {
    var itemType = xmlType(item);
    var dest;
    if (itemType > XMLSimpleType) {
      dest = getXMLFirstChild(item, true);
      if (dest) return dest;
    }
    dest = getXMLNextSibling(item);
    var next = item;
    while (!dest) {
      if (isRoot(next)) return next;
      next = getParent(next);
      dest = getXMLNextSibling(next);
    }
    return dest;
  }
    
  function getShiftTab(item) {
    var prev = getXMLPrevSibling(item);
    if (!prev) {
      if (isRoot(item))
        prev = item
      else
        return getParent(item);
    }
    while (prev) {
      if (xmlType(prev) >= XMLSimpleContent) {
      var dest = getXMLLastChild(prev, true);
      if (!dest) return prev;
      prev = dest;
    } else
      return prev;
    }
    return null; 
  }
    
  function getParent(item) {
    var dest = getXMLParent(item);
    if (dest && xmlType(dest) == XMLSimpleType) dest = item.parentNode;
    return dest;
  }
    
  function getPrevSibling(item) {
    var dest = getXMLPrevSibling(item);
    if (dest) return dest;
    if (isSimpleWithAttribute(item))
      return (getXMLPrevSibling(item.parentNode));
    return null;
  }

  function getNextSibling(item) {
    var dest = getXMLNextSibling(item);
    if (dest) return dest;
    if (xmlType(item) == XMLAttribute && xmlType(item.parentNode) == XMLSimpleContent)
      return (getXMLNextSibling(item.parentNode));
    return null;
  }

  function getFirstChild(item) {
    if (xmlType(item) < XMLSimpleContent) {
      switchFocus();
      return null;
    }
    var dest = getXMLFirstChild(item);
    if (dest) return dest;
    // it may be that we have attributes but no child element
    return getXMLFirstChild(item, true);
  }
    
  function getTwin(item, func) {
    var elemDesc = getElementDescription(item);
    var nextItem = item;
    var toAvoid = null;
    var isSimple = false;
    if (isSimpleWithAttribute(item)) {
      toAvoid = item.parentNode;
      isSimple = true;
    } else if (xmlType(item) == XMLSimpleContent)
      toAvoid = getXMLFirstChild(item);
    do {
      nextItem = func(nextItem);
      // TBD : what about complexContent vs mixedContent
      if (nextItem == toAvoid) continue;
      var thisElemDesc = getElementDescription(nextItem);
      if (thisElemDesc.itemName == elemDesc.itemName && thisElemDesc.parentName == elemDesc.parentName) break;
    } while (item != nextItem)
    if (xmlType(item) == XMLSimpleType && xmlType(nextItem) == XMLSimpleContent)
      nextItem = getXMLFirstChild(nextItem);
    return nextItem;
  }
  
  function getNextTwin(item) {
    return getTwin(item, getTab);
  }
      
  function getPrevTwin(item) {
    return getTwin(item, getShiftTab);
  }
      
  function goToElement(evt, func) {
    var item = getSelection();
    if (item) {
      var dest = func(item);
      if (dest) {
        revealElm(dest);
        if (!isIE) evt.preventDefault();
        return false;
      }
    }
    return true;
  }

  if (!evt) evt = window.event;
  var alt = isOpera ? evt.ctrlKey : evt.altKey;
//window.status = evt.keyCode;
  switch (evt.keyCode) {
    case 9:
      return goToElement(evt, evt.shiftKey ? getShiftTab : getTab);
    case 27:
      return switchFocus(evt);
    case 37:
      if (isIE && alt) break;
      return goToElement(evt, alt ? getShiftTab : getParent);
    case 38:
      return goToElement(evt, alt ? getPrevTwin : getPrevSibling);
    case 39:
      if (isIE && alt) break;
      return goToElement(evt, alt ? getTab : getFirstChild);
    case 40:
      return goToElement(evt, alt ? getNextTwin : getNextSibling);
    default:
      break;
  }
  return true;
}

function getPreference(propName) {
  var preference = document.preference;
  if (propName)
    window.status = preference[propName]
  else {
    var total = '';
    for (var prop in preference) {
      total += ' ' + prop + ':' + preference[prop] + ';';
    }
    window.status = total;
  }
}

function setPreference(propName, value) {
  var preference = document.preference;
  if (preference[propName] !== undefined) preference[propName] = value;
  getPreference(propName);
}

function makePreferenceScript() {
  var preference = document.preference;
  var total = '';
  for (var prop in preference) {
    var value = preference[prop];
    if (typeof(value) == 'string') value = doubleQuote(value);
    total += 'preference.' + prop + '=' + value + ";\n";
  }
  return total;
}

function showPreferenceScript() {
  alert(makePreferenceScript());
}

function makeACopy (item ) {
  selectXMLForm(); // deselect it so we won't clone the selection color
  var copy = item.cloneNode(true);
  // many of the following is not needed for IE, but won't hurt
  var srcNodes = item.getElementsByTagName('input');
  var dstNodes = copy.getElementsByTagName('input');
  var srcNode,destNode;
  var srcName = '?';
  var dstName;
  for (var i=0;i<srcNodes.length;i++) {
    srcNode = srcNodes[i];
    dstNode = dstNodes[i];
    dstNode.value = srcNode.value;
    if (srcNode.type == 'radio') {
      dstNode.value = srcNodes[i].value;
      if (srcNode.name != srcName) {
        srcName = srcNode.name;
        dstName = getRealName(srcNode) + ',' + document.preference.radioIndex++;
      }
      dstNode.name = dstName;
      dstNode.checked = srcNode.checked;
      // IE setting checked now would not work, so set another flag first
      if (isIE) dstNode.shouldBeChecked = srcNode.checked;
    } else
      dstNode.defaultValue = srcNode.value;
  }
  srcNodes = item.getElementsByTagName('textarea');
  dstNodes = copy.getElementsByTagName('textarea');
  for (var j=0;j<srcNodes.length;j++) {
    dstNodes[j].value = srcNodes[j].value;
    dstNodes[j].defaultValue = srcNodes[j].value;
  }
  srcNodes = item.getElementsByTagName('select');
  dstNodes = copy.getElementsByTagName('select');
  for (var k=0;k<srcNodes.length;k++) {
    dstNodes[k].selectedIndex = srcNodes[k]. selectedIndex;
  }
  copy.ondblclick = item.ondblclick;
  return copy;
}

function copyTextAttribute(src, dst) {
  dst.value = getInputValue(src);
  dst.className = src.className;
  dst.name = getRealName(src);
  dst.disabled = src.disabled;
}

function htmlToNode(htmlStr) {
  var node = document.createElement('div');
  node.innerHTML = htmlStr;
  return node.firstChild;
}

function goThruInputs(item, func) {
  checkEachNode(item.getElementsByTagName('input'), func);
  checkEachNode(item.getElementsByTagName('textarea'), func);
}

function checkEachNode(nodeList, func) {
  for (var i=nodeList.length-1;i>=0;i--) { // revese order because we may be deleting
    var item = nodeList[i];
    if (item.type != 'hidden') func(item);
  }
}

function selectXMLForm (newSelect) {
  var form = document.xmlForm;
  var oldSelect = xmlSelection;
  if (oldSelect) {
    oldSelect.style.backgroundColor = oldSelect.originalColor;
    // delect oldSelect.originalColor;
  }
  if (newSelect) {
    xmlSelection = newSelect;
    newSelect.originalColor = newSelect.style.backgroundColor;
    newSelect.style.backgroundColor  = "#0469B3";
  } else
    xmlSelection = null;
}

function getErrorElementName(item) {
  return 'Element ' + doubleQuote(getXMLElementName(item));
}

function toUndo(operator, data, optionalData, option) {
  if (false && operator) {
    var item = optionalData;
    if (typeof(item) == 'object') {
      item = item.innerHTML;
      if (item.length > 400) item = item.substr(0,400);
    }
    alert('to undo use optionalData: ' + item + "\noption: " + option + "\nin " + operator.toString().substr(0,30));
  }
  document.undoState.operator = operator;
  document.undoState.data = data;
  document.undoState.optionalData = optionalData;
  document.undoState.option = option;
  document.undoState.changeCount++;
}

function getChangeCount() {
  return document.undoState.changeCount;
}

function noUndo() {
  toUndo('');
}

function undo() {
  var op = document.undoState;
  var operation = op.operator;
  if (operation) {
    if (false) {
      var item = op.optionalData;
      if (typeof(item) == 'object') {
        item = item.innerHTML;
        if (item.length > 400) item = item.substr(0,400);
      }
      alert('undo by using optionalData: ' + item + "\noption: " + op.option + "\nin " + operation.toString().substr(0,30));
    }
    var oldSelection = getSelection();
    selectXMLForm(); // deselect it
    operation(op.data,op.optionalData,op.option);
    if (!xmlSelection) selectXMLForm(oldSelection);
  } else {
    alert('Undo not possible.');
  }
}

//
function selectelm(x, evt) {
  if (isIE) {
    evt.cancelBubble = true;
  } else {
    evt.stopPropagation();
  }
  var tag = isIE ? evt.srcElement.tagName : evt.target.tagName;
  var isInput = (tag =='INPUT' || tag =='TEXTAREA' || tag =='SELECT' || tag =='OPTION');
  if (isInput) {
    if (evt.type == 'click') {
      selectXMLForm(); // deselect it
    } else {
      // what should we do here
      selectXMLForm(); // double click on text field select it
    }
    // IE radio button does not work, so do it by JavaScript
    if (isIE && evt.srcElement.type == 'radio') { 
      var radios = evt.srcElement.parentNode.getElementsByTagName('input');
      for (var i=0;i<radios.length;i++) radios[i].checked = false;
      evt.srcElement.checked = true;
    }
  } else if (evt.type == 'click') {
    selectXMLForm(x);
  } else {
    if (evt.ctrlKey) {
      duplicateElm(x);
    } else {
      hideOrShowChildrenOfElm( x, 0);
    }
  }
}

function clickFormBody(x, evt) {
  var tag = isIE ? evt.srcElement.tagName : evt.target.tagName;
  if (tag =='FORM') {
    selectXMLForm();
    focusField = null;
  }
}

function hideOrShowChildrenOfElm (item, expand) {
  // expand == 1 expand, == 0 toggle, == -1 collapse

  var fieldSet = getChildByTagNames(item, ['FIELDSET']);
  var expandState = 0;
  if (fieldSet) {
    var legend = getChildByTagNames(fieldSet,['LEGEND']);
    if (legend) {
      var expandSymbol = legend.innerHTML.charAt(0);
      if (expandSymbol == '+') { 
        expandState = 1;
      } else if (expandSymbol == '-') {
        expandState = -1;
      }
    }
    //Cheng Yi
    if (expandState != 0 && expand * expandState >= 0) {
      for (var j=0;j<fieldSet.childNodes.length;j++) {
        var node = fieldSet.childNodes[j];
        if (node.nodeType == 1 && node.tagName != 'LEGEND') {
          if (expandState == -1) {
            node.style.display = 'none';
          } else {
            node.style.display = 'block'; // 'inherit';
          }       
        }
      }
      if (expandState == -1) {
        legend.innerHTML = legend.innerHTML.replace('-', '+');
      } else if (expandState == 1) {
        legend.innerHTML = legend.innerHTML.replace('+', '-');
      }
    }
  }
  if (expandState == 0) {
    alert('you can only expand or collapse element with child elements');
    return false;
  }
  return true;
}

function expandElm (item) {
  return hideOrShowChildrenOfElm(item, +1);
}

function collapseElm (item) {
  return hideOrShowChildrenOfElm(item, -1);
}

function checkRadio(x) {
  if (isIE) { // IE checked does not work unless set after added to document
    var inputs = x.getElementsByTagName('input');
    for (var i=0;i<inputs.length;i++) {
      var node = inputs[i];
      if (node.type == 'radio' && node.shouldBeChecked) node.checked = true;
    }
  }
}

// TBD : factor these function out 

function simpleTypeToSimpleContent (item) {
  var newNode = getPrototype('$');
  item.parentNode.replaceChild(newNode, item);
  newNode.appendChild(item);
  return newNode;
}
  
function simpleTypeToComplexContent (item) {
  var value = getXMLInputValue(item);
  var newNode = htmlToNode(getPrototypeHTML('#'+getXMLElementName(item)).replace(/!cn!/, getAnonymousHTML(value)));
  item.parentNode.replaceChild(newNode, item);
  return newNode;
}

function simpleContentToComplexContent (item) {
  var name = getXMLElementName(item);
  var copy = item.cloneNode(true);
  var textPart = getNamedXMLChild(copy, name);
  var value = getXMLInputValue(textPart);
  textPart.parentNode.removeChild(textPart);
  var newNode = htmlToNode(getPrototypeHTML('#' + name).replace(/!cn!/, copy.innerHTML + getAnonymousHTML(value)));
  item.parentNode.replaceChild(newNode, item);
  return newNode;
}
  
function complexContentToSimpleContent (item) {
  var name = getXMLElementName(item);
  var newNode = getPrototype('$' + name);
  var childList = getXMLChildren(item, true);
  for (var i=0;i<childList.length;i++) {
    var child = childList[i];
    var nodeType = xmlType(child);
    if (nodeType == XMLAttribute) {
      newNode.appendChild(child.cloneNode(true));
    } else if (nodeType == XMLAnonymous) {
        newNode.insertBefore(htmlToNode(getPrototypeHTML('!' + name).replace(/!cn!/, getXMLInputValue(child))), newNode.firstChild);
    } else {
      return item;
    }
  }
  return newNode;
}

function duplicateElm (item ) {
  if (xmlType(item) == XMLAttribute) {
    alert("Cannot duplicate an attribute because you don't want two attributes with same name");
    return false;
  } else if (isSimpleWithAttribute(item)) {
    alert("The element has attributes, you need to select the whole element if you want to duplicate it");
    return false;
  } else if (rejectRoot(item)) {
    return false;
  } else {
    var parent = getXMLParent(item);
    var childList = getXMLChildren(parent);
    childList.splice(indexOfItemInArray(childList, item), 0, item);
    if (!okToProceed('We should not duplicate this element', parent, childList)) return false;
    var copy = makeACopy(item);
    item.parentNode.insertBefore(copy, item.nextSibling);
    checkRadio(copy);
    toUndo(unDuplicateElm, copy, item);
    revealElm( copy ); // select the new copy
    return true;
  }
}

function unDuplicateElm(item, original) {
  deleteElm(item);
  toUndo(duplicateElm, original);
}

function copyElm (item ) {
  selectXMLForm(); // deselect it so we won't clone the selection color
  document.xmlForm.scrap = makeACopy(item);
  selectXMLForm( item ); // select it again
  return true;
}

function cutElm (item) {
  // essentially delete + removeDeleted + put on scrap
  var rst = deleteElm(item);
  if (!rst) return false;
  if (isIE) {
    document.xmlForm.scrap = item;
  } else { // to get around Moz bug
    document.xmlForm.scrap = makeACopy(item);
  }
  return true;
}

function deleteElm (item) {
  if (rejectRoot(item)) return false;
  if (isSimpleWithAttribute(item)) return deleteElm(item.parentNode);
  var parent = getXMLParent(item);
  var nextElm = getXMLNextSibling(item);
  removeElm(item);
  if (nextElm) {
    toUndo(pasteCopyWithUndo, item, nextElm, 'before');
  } else {
    toUndo(pasteCopyWithUndo, item, parent, 'into');
  }
  var splitLoc = 0;
  if (xmlType(parent) >= XMLComplexContent) {
    var childList = getXMLChildren(parent, true);
    var elmCount = 0;
    var attribCount = 0;
    var anonCount = 0;
    var lastType = 0;
    var wholeStr;
    var nextAnonymous;
    // combine adjacent text node and take inventory
    for (var i=childList.length-1; i>=0; i--) {
      var child = childList[i];
      var elmType = xmlType(child);
      if (elmType == XMLAttribute) {
        attribCount++;
      } else if (elmType == XMLAnonymous) {
        anonCount++;
        var input = getInputOfItem(child);
        if (elmType == lastType) {
          var thisStr = getInputValue(input);
          splitLoc = thisStr.length;
          wholeStr = thisStr + wholeStr;
          if (input.tag != 'TEXTAREA' && input.type != 'text') {
        	  //Cheng Yi
            if (wholeStr.length > 25) {
              becomeTextareaElm(child);
            } else {
              becomeTextfieldElm(child);
            }
            input = getInputOfItem(child);
          }
          input.value = wholeStr;
          nextAnonymous.parentNode.removeChild(nextAnonymous);
          toUndo(pasteAfterSplit, item, child, splitLoc);
        } else {
          wholeStr = getInputValue(input);
          nextAnonymous = child;
        }
      } else if (elmType) {
        elmCount++;
      }
      lastType = elmType;
    }
    // see if we need to convert to simpleType or simplecontent
    if (elmCount == 0 && anonCount) {
      var name = getXMLElementName(parent);
      if (attribCount) {
        if (anonCount) {
          var newNode = complexContentToSimpleContent(parent);
          if (newNode != parent) {
            undo();
            parent.parentNode.replaceChild(newNode, parent);
            toUndo(pasteCopyWithUndo, parent, newNode, 'replace'); // to undo slam it back
          }
        }
      } else {
        var newElm = htmlToNode(getPrototypeHTML('!' + name).replace(/!cn!/, wholeStr));
        undo();
        parent.parentNode.replaceChild(newElm, parent);
        toUndo(pasteCopyWithUndo, parent, newElm, 'replace'); // to undo slam it back
      }
    }
  }
  return true;
}

function pasteAfterSplit(item, splitItem, loc) {
  var input = getInputOfItem(splitItem);
  var value = getInputValue(input);
  input.value = value.substr(0, loc);
  var newNode = htmlToNode(getAnonymousHTML( value.substr(loc) ));
  splitItem.parentNode.insertBefore(newNode, splitItem.nextSibling);
  pasteCopyWithUndo(item, splitItem, 'after');
}

function pasteElm (item, where) {
  if (!document.xmlForm.scrap) {
    alert('Nothing to paste, cut or copy something first');
    return false;
  }
  var copy = makeACopy(document.xmlForm.scrap);
  if (copy) return pasteCopyWithUndo(copy, item, where);
  return false;
}

function pasteCopyWithUndo (copy, item, where) {
  var pasteOK = pasteCopyTo(copy, item, where);
  if (pasteOK) {
    if (where == 'replace') {
      toUndo(pasteCopyWithUndo, item, copy, 'replace');
    } else {
      toUndo(deleteElm, copy);
    }
    selectXMLForm( copy ); // select the new copy the selection
    revealElm( copy ); // select the new copy the selection
    checkRadio(copy);
  }
  return pasteOK;
}

function pasteCopyTo (copy, item, where) {
  function addAttribute(container, copy, skipFirst, before) {
    var nodeList = container.childNodes;
    var attributeList = new Array();
    for (var i=0;i<nodeList.length;i++) {
      var node = nodeList[i];
      if (xmlType(node) == XMLAttribute) {
        attributeList.push(node);
      }
    }
    
    var attributeName = copy.getElementsByTagName('span')[0].className;
    for (var i=0;i<attributeList.length;i++) {
      var node = attributeList[i];
      if (node.getElementsByTagName('span')[0].className == attributeName) {
        if (confirm('This attribute alreay exists in the destination node, do you want to replace it?')) {
          container.replaceChild(copy, node);
          // TBD : undo
          return true;
        } else {
          return false;
        }
      }
    }

    // TBD: rewrite this, too confusing
    // we are adding an attribute to the element, we may need to update schema
    var element;
    if (container.className == 'XMLSimpleContent') {
      element = getXMLElementName(container);
    } else {
      element = container.className;
    }
    var attribName = getXMLElementName(copy);
    var attribReg = '\\\(<@' + attribName + '>\\\)';
    var schema = getSchema(element);
    if (!schema || schema.search(attribReg) < 0) { // attribute is not in schema
      if (!overrideSchema('Attribute "' + attribName + '" is not in the content model of element "' + element + '"')) {
        return false;
      }
    }
    if (!before) {
      if (attributeList.length)
        before = attributeList[attributeList.length-1].nextSibling
      else if (skipFirst)
        before = null
      else
        before = container.firstChild;
    }
    container.insertBefore(copy, before);
    return true;
  }
    
  function pasteAttribute(item, copy, where) {
    var pasteOK;
    var destType = xmlType(item);
    if (where == 'into') {
      if (destType == XMLSimpleContent) {
        pasteOK = addAttribute(item, copy, true);
      } else if (isSimpleWithAttribute(item)) {
        pasteOK = addAttribute(item.parentNode, copy, true);
      } else if (destType == XMLSimpleType) { // when an attribute is added to simple type, it becoms complex type
        pasteOK = addAttribute(simpleTypeToSimpleContent(item), copy, true);
      } else if (destType < XMLSimpleType) {
        return alert ('You cannot put an attribute here');
      } else { // complex content and mixed content
        var before = getXMLFirstChild(item);
        if (!before) {
          var candidates = item.getElementsByTagName('input');
          for (var i=candidates.length-1;i>=0;i--) {
            before = candidates[i];
            if (before.name == '.tg') break;
          }
          if (!before) return false;
        }
        pasteOK = addAttribute(before.parentNode, copy, false, before);
      }
    } else { // before or after or replace, only if dest is attribute or before first child element
      if (destType != XMLAttribute) { // TBD : OK if before first element
        if (where == 'before') {
          var itemParent = getXMLParent(item);
          if (getXMLFirstChild(itemParent) == item) {
            pasteOK = addAttribute(item.parentNode, copy, false, item);
          } 
        }
        if (pasteOK == undefined) return alert('You cannot put an attribute here');
      } else {
        pasteOK = addAttribute(item.parentNode, copy, false, where == 'before' ? item : item.nextSibling);
        if (where == 'replace' && pasteOK) {
          if (item.parentNode) item.parentNode.removeChild(item); // if original still there, remove it
        }
      } 
    }
    return pasteOK;
  }
    
  // start of pasteCopyTo
  var srcType = xmlType(copy);
  var destType = xmlType(item);
  var container = item;
  if (srcType == XMLAttribute ) {
    return pasteAttribute(container, copy, where);
  } else if (destType >= XMLComplexContent) { // complex or mixed content
    if (where != 'into') { // into, just pasteAny
      if (isRoot(item)) {
        alert('Cannot paste outside of the root element');
        return false;
      }
      // else pasteAny
    }
  } else { // simple type or complex type or attribute
    if (where == 'into') {
      if (srcType >= XMLAnonymous) {
        if (destType == XMLSimpleType) {
          if (isSimpleWithAttribute(item)) {
            item = item.parentNode;
            container = item;
          }
          container = simpleTypeToComplexContent(item);
        } else if (destType == XMLSimpleContent) {
          container = simpleContentToComplexContent(item);
        } else {
        // paste into anonymous is same as paste after anonymous
        where = 'after';
        }
      }
    } else if (where == 'replace') {
      if (destType == XMLAttribute) {
        alert('An element cannot replace an attribute.');
        return false;
      } else if (isSimpleWithAttribute(item)) {
        alert('It is not clear what you are trying to do, so we would not proceed.');
        return false;
      }
    }
  }
  var pasteOK = pasteAny(container, copy, where);
  if (!pasteOK && item != container) {
    container.parentNode.replaceChild(item, container);
  };
  return pasteOK;
}
    
function pasteAny (item, copy, where) {
  if (isSimpleWithAttribute(item)) item = item.parentNode;
  var xmlParent = (where == 'into') ? item : getXMLParent(item);
  var childList = getXMLChildren(xmlParent);
  switch (where) {
    case 'into':
      if (childList) {
        childList.push(copy);
      } else {
        childList = [copy];
      }
      break;
    // TBD: paste into simpletype of complextype
    case 'replace':
      if (childList) {
        childList.splice(indexOfItemInArray(childList,item), 1, copy);
      } else {
        childList = [copy];
      }
      break;
    default:
      if (!childList) {
        alert('It cannot be pasted here.');
        return false;
      }
      var index = indexOfItemInArray(childList, (where=='before')?item:item.nextSibling);
      if (index >= 0) {
        childList.splice(index, 0, copy);
      } else {
        childList.push(copy);
      }
    }
    if (!okToProceed('We should not paste the element', xmlParent, childList)) {
      return false;
  }
  switch (where) {
    case 'before':      
      if (xmlType(copy) != XMLAttribute && xmlType(item) == XMLAttribute) {
        alert('Elements cannot come before attribute.');
        selectXMLForm(item);
        return;
      }
      item.parentNode.insertBefore(copy, item);
      break;
    case 'after':
      if (xmlType(copy) != XMLAttribute) isNil(getXMLParent(item), true);
      item.parentNode.insertBefore(copy, item.nextSibling);
      break;
    case 'into':
      if (xmlType(copy) != XMLAttribute) isNil(item, true);
      var nodeList = item.childNodes;
      for (var i=0;i<nodeList.length;i++) {
//      if (nodeList[i].hasChildNodes()) { // not supported by IE
        if (nodeList[i].childNodes.length) {
          nodeList[i].insertBefore(copy, nodeList[i].lastChild);
          break;
        }
      }
      break;
    case 'replace':
      item.parentNode.replaceChild(copy, item);
      break;
    default:
      return;
  }
  return true;
} // pasteAny

function removeElm(item) {
  selectXMLForm(); // deselect it so we won't it won't be on selection
  var ok;
  var xmlParent = getXMLParent(item);
  var childList = getXMLChildren(xmlParent);
  var isAttribute = (xmlType(item) == XMLAttribute);
  if (isAttribute) {
    ok = okToProceed('We should not delete this attribute', xmlParent, childList, getXMLElementName(item));
  } else {
    childList.splice(indexOfItemInArray(childList, item), 1);
    ok = okToProceed('We should not delete this element', xmlParent, childList);
  }
  if (!ok) {
    selectXMLForm(item ); // select it again
    return false;
  }
  var container = item.parentNode;
  container.removeChild(item);
  if (isAttribute && xmlType(xmlParent) == XMLSimpleContent) {
    // TBD : should rewrite
    var nodeList = container.childNodes;
    var moreAttribute = false;
    var simpleNode = null;
    // do we have more attributes?
    for (var i=0;i<nodeList.length;i++) {
      var theType = xmlType(nodeList[i]);
      if (theType == XMLSimpleType) {
        simpleNode = nodeList[i];
      } else if (theType == XMLAttribute) {
        moreAttribute = true;
        break;
      }
    }
    if (!moreAttribute && simpleNode) {
      // no more attribute, we don't need the simpleContent container
      container.parentNode.replaceChild(simpleNode, container);
    }
  }
  selectXMLForm(item ); // select it again
  return true;
}

function setFieldValue(item, value) {
  function callOnChange(input) {
    input.onchange(input);
    selectXMLForm(item);
  }
  
  function setSelectIndex(select, value) {
    var options = select.options;
    var index = indexOfValueInOptions(options, value);
    if (index >= 0) {
      select.selectedIndex = index;
      removeUndefinedFromSelect(select);
    } else {
      select.value = '';
      addOptionToSelect(select, '**Undefined**', '', 0);
      select.selectedIndex = 0;
    }
  }

  var input = getInputOfItem(item);
  var oldValue = getInputValue(input);
  document.undoState.oldTextValue = oldValue;
  if (value == oldValue) return;
  var tag = input.tagName;
  if (tag == 'INPUT') {
    if (input.type == 'radio') {
      var selectedRadio;
      var radios = item.getElementsByTagName('input');
      for (var i=0;i<radios.length;i++) {
        if (radios[i].value == value) {
          radios[i].checked = true;
          selectedRadio = radios[i];
        } else {
          radios[i].checked = false;
        }
      }
      if (selectedRadio) {
        if (!oldValue) removeNoRadio(input);
      } else {
        for (var i=0;i<radios.length;i++) {
          radios[i].setAttribute('onchange', 'removeNoRadio(this)');
        }
        var nodeList = input.parentNode.childNodes;
        var firstAttribute = null;
        for (var i=0;i<nodeList.length;i++) {
          var node = nodeList[i];
          if (node.tagName == 'INPUT') {
            if (node.value == '' && (node.type == 'radio' || node.type == 'hidden')) return;
              if (!firstAttribute && node.name.charAt(0) == '@') firstAttribute = node;
          }
        }
        var name = input.name.replace(/,.*/, '');
        input.parentNode.insertBefore(htmlToNode('<input type="hidden" value="" name="' + name + '"/>'), firstAttribute);
      }
    } else {
      input.value = value;
      return callOnChange(input);
    }
  } else if (tag == 'TEXTAREA') {
    input.value = value;
    return callOnChange(input);
  } else if (tag == 'SELECT') {
    setSelectIndex(input, value);
  } else {
    noUndo();
    return;
  }
  selectXMLForm(item);
  toUndo(setFieldValue, item, oldValue);
}

function clearElm(item, ignoreNil) {
  // TBD : if there is a default value, use it instead of empty string
  function clearField(x) {
    x.value = '';
  }
  
  function clearInput(x) {
    if (x.type == 'text') {
      x.value = ''
    } else if (x.type == 'radio') {
      x.checked = (x.value == '');
      var nodeList = x.parentNode.childNodes;
      var firstAttribute = null;
      for (var i=0;i<nodeList.length;i++) {
        var node = nodeList[i];
        if (node.tagName == 'INPUT') {
          if (node.value == '' && (node.type == 'radio' || node.type == 'hidden')) return;
          if (!firstAttribute && node.name.charAt(0) == '@') firstAttribute = node;
        }
      }
      var name = x.name.replace(/,.*/, '');
      x.parentNode.insertBefore(htmlToNode('<input type="hidden" value="" name="' + name + '"/>'), firstAttribute);
      x.parentNode.innerHTML = x.parentNode.innerHTML.replace(/radioChanged\(this\)/g, 'removeNoRadio(this)');
    }
  }
  
  function unselectField(x) {
    if (x.value != '') {
      x.value = '';
      addOptionToSelect(x, '**Undefined**', '', 0);
      x.selectedIndex = 0;
    }
  }
  
  function reborn(item) {
    if (xmlType(item) < XMLComplexContent) return;
    selectXMLForm();
    var node, i, input;
    var attributes = new Object();
    var nodeList = getChildByTagNames(item, ['FIELDSET']).childNodes;
    for (i=0;i<nodeList.length;i++) {
      node = nodeList[i];
      if (xmlType(node) == XMLAttribute) {
        input = getInputOfItem(node);
        attributes[input.name] = input.value;
      }
    }
    var name = getXMLElementName(item);
    var metaElm = getMetaElement(item);
    if (!metaElm) metaElm = guessMetaInfo(name);
    var newItem = makeKnownElement(name, metaElm);
    item.parentNode.replaceChild(newItem,item);
    var parent = getChildByTagNames(newItem, ['FIELDSET']);
    nodeList = parent.childNodes;
    for (i=nodeList.length-1;i>=0;i--) {
      node = nodeList[i];
      if (xmlType(node) == XMLAttribute) {
        input = getInputOfItem(node);
        var oldValue = attributes[input.name];
        if (oldValue) {
          input.value = oldValue;
        } else if (oldValue == undefined) {
          parent.removeChild(node);
        }
      }
    }
    selectXMLForm(newItem);
    return newItem;
  }
  
  if (!ignoreNil && isNil(item)) {
    isNil(item, true);
    item = reborn(item);
    toUndo(nilElm, item);
  } else {
    if (xmlType(item) >= XMLSimpleContent) {
      checkEachNode(item.getElementsByTagName('input'), clearInput);
      checkEachNode(item.getElementsByTagName('textarea'), clearInput);
      checkEachNode(item.getElementsByTagName('select'), unselectField);
      noUndo();
    } else {
      setFieldValue(item, ''); // set value so we can undo
    }
  }
  return true;
}
  
function nilElm(item) {
  function setOnchange(input) {
    var tag = input.tagName;
    if (tag == 'INPUT') {
      if (input.type == 'text') {
        input.setAttribute('onchange', 'unnilIfChanged(this)');
      } else if (input.type == 'radio') {
        input.setAttribute('onchange', 'removeNoRadio(this)'); // TBD: do for every radio
      }
    } else if (tag == 'TEXTAREA') {
      input.setAttribute('onchange', 'unnilIfChanged(this)');
    } else if (tag == 'SELECT') {
      // TBD
    }
  }

  function addNilAttribute(item) {
    // TBD simpleType inside simpleContent is special case
    var nilVal = document.preference.xsiPrefix;
    if (!nilVal) {
      // TBD : make an xsi attribute in root element
      nilVal = 'xsi:';
    }
    nilVal = '@' + nilVal + 'nil';
    var itemType = xmlType(item);
    var parentNode = (itemType==XMLSimpleContent)?item:getChildByTagNames(item, ['FIELDSET','DIV','SPAN']);
    if (!parentNode) return false;
    var nilAttribute = htmlToNode('<input type="hidden" name="' + nilVal + '" value="true"/>');
    if ( itemType >= XMLComplexContent) {
      var newItem = getPrototype('#' + getXMLElementName(item));
      pasteCopyTo(nilAttribute, newItem, 'into');
      var childList = getXMLChildren(item, true); // include attribute
      for (var n=0; n<childList.length;n++) {
        if (xmlType(childList[n]) == XMLAttribute) {
          pasteCopyTo(childList[n].cloneNode(true), newItem, 'into');
        } else
          break;
      }
      pasteCopyWithUndo(newItem, item, 'replace');
    } else {
      var nodeList = parentNode.childNodes;
      for (var i=0;i<nodeList.length;i++) {
        var node = nodeList[i];
        var iTag = node.tagName;
        // TBD : combine these two
        if ( iTag == 'INPUT' || iTag == 'SELECT' || iTag == 'TEXTAREA' ) {
          if ( itemType == XMLSimpleType ) {
            var undoData = item.cloneNode(true);
            clearElm(item, true);
            parentNode.appendChild(nilAttribute);
            setOnchange(node);
            toUndo(pasteCopyWithUndo, undoData, item, 'replace');
            return true;
          }
        } else if ( iTag == 'DIV' || iTag == 'SPAN' ) { 
          if (itemType==XMLSimpleContent && node.className == 'XMLSimpleType') {
            var undoData = item.cloneNode(true);
            clearElm(node, true);
            parentNode.insertBefore(nilAttribute, node.nextSibling); 
            setOnchange(getInputOfItem(node));
            toUndo(pasteCopyWithUndo, undoData, item, 'replace');
            return true;
          }
        }
      }
    }
    return true;
  }

  if (isSimpleWithAttribute(item)) return nilElm(item.parentNode);
  if (isNil(item)) return true;
  var nodeType = xmlType(item);
  if (nodeType < XMLSimpleType) {
    alert('You can only nil an element.');
    return false;
  } else {
    if (document.preference.checkSchema || document.preference.obeySchema) {
      var metaElm = getMetaElement(item);
      if (metaElm && !metaElm['#ni']) {
        if (!overrideSchema(getErrorElementName(item) + ' is not nillable')) return false;
      }
    }
    addNilAttribute(item);
  }
}

function revealElm(item) {
  var parent = item;
  while (parent != null) {
    if (xmlType(parent) > XMLSimpleContent) expandElm(parent);
    parent = getXMLParent(parent);
  }
  selectXMLForm(item); // so the user know which element we are talking about
//  if (item.scrollIntoView) // not supported by opera
  item.scrollIntoView();
  window.scrollBy(0, -70);
}
  
function unnilItemOfInput(input) {
  var item = getItemOfInput(input);
  if (xmlType(item) != XMLSimpleType) return;
  var tagName = input.tagName;
  if (tagName == 'INPUT') {
    if (input.type == 'text') {
      input.setAttribute('onchange', 'textChanged(this)');
    } else if (input.type == 'radio') {
      // TBD
    }
  } else if (tagName == 'TEXTAREA') {
    input.setAttribute('onchange', 'textChanged(this)');
  } else if (tagName == 'SELECT') {
    // TBD
  }
  if (xmlType(item.parentNode) == XMLSimpleContent) item = item.parentNode; 
  isNil(item, true);
}
  
function isNil(item, remove) {
  if (isSimpleWithAttribute(item)) return isNil(item.parentNode, remove);
  var itemType = xmlType(item);
  if (itemType < XMLSimpleType) return false;
  var nilVal = document.preference.xsiPrefix;
  if (!nilVal) nilVal = 'xsi:';
  nilVal = '@' + nilVal + 'nil';
  var isSimpleContent = (xmlType(item)==XMLSimpleContent);
  var parentNode = (isSimpleContent)?item:getChildByTagNames(item, ['FIELDSET','DIV','SPAN']);
  if (!parentNode) return false;
  var nodeList = parentNode.childNodes;
  for (var j=0;j<nodeList.length;j++) {
    var node = nodeList[j];
    var iTag = node.tagName;
    if ( iTag == 'INPUT' ) {
      if ( node.name == nilVal ) {
        if (remove) parentNode.removeChild(node); 
        return true;
      }
    } else if ( iTag == 'DIV' || iTag == 'SPAN' ) { 
      if (!isSimpleContent) return false;
      if (node.className == 'XMLAttribute') return false;
    }
  }
  return false;
}
  
function verifyElm(item) {
  function verifyInputItem(node, idInfo) {
    if (xmlType(node) == XMLAnonymous) return true;
    var error, metaElm;
    var nilled = isNil(node);
    var input = getInputOfItem(node);
    if (nilled) {
      metaElm = getMetaElement(node);
      if (metaElm && !metaElm['#ni']) {
        error = getErrorElementName(node) + ' is nil but it is not nilable'
      } else if (input) {
        if (input.value) 
          error = getErrorElementName(node) + ' should be nil but it has value ' + doubleQuote(input.value);
      }
    } else {
      if (input) {
        error = verifyInputField(input, idInfo);
        if (error) {
          metaElm = getMetaElement(node);
          if (metaElm) {
            var typeInfo = getTypeInfo(getErrorElementName(node), metaElm);
            if (typeInfo) {
              error += "\nAccording to the schema, " + typeInfo;
            }
          }
        }
      }
    }
    if (error) {
      revealElm(node);
      if (!confirm(error)) return false;
    }
    return true;
  }

  function verifyElement(node, idInfo) {
    var childList, error;
    var nodeType = xmlType(node);
    if (nodeType > XMLSimpleType) { 
      childList = getXMLChildren(node);
      error = checkTagSequence(node, childList, isNil(node));
      if (error) {
        revealElm(node);
        if (!confirm(error+'.')) return false;
      }
      if (nodeType == XMLSimpleContent) { 
        childList = node.childNodes;
      } else {
        childList = getXMLChildren(node, true); // include attribute
      }
      if (!childList) return true;
      for (var i=0;i<childList.length;i++) {
        var childNode = childList[i];
        nodeType = xmlType(childNode);
        if (nodeType > XMLSimpleType) {
          if (!verifyElement(childNode, idInfo)) return false;
        } else if (nodeType) {
          if (!verifyInputItem(childNode, idInfo)) return false;
        }
      }
    } else if (nodeType) return verifyInputItem(node, idInfo);
    return true;
  }

  var idInfo = null;
  if (getRoot() == item) {
    idInfo = new Array(new Object(), new Object());
  }
  selectXMLForm();
  if (!verifyElement(item, idInfo)) return false;
  if (idInfo) {
    var missing = '';
    var errorCount = 0;
    for (var x in idInfo[1]) {
      missing += doubleQuote(x) + ' ';
    errorCount++;
    }
    if (errorCount) {
      alert('ID ' + missing + (errorCount>1 ? 'are' : 'is') + ' missing.');
    return false;
    }
  }
  return true; 
}

function getRealName(input) {
  return input.name.split(',')[0];
}
  
function collectEnclosedInput(item) {
  function appendCollectionToArray(a, c) { // would concat work?
    for (var i=0;i<c.length;i++) {
      a.push(c[i]);
    }
    return a;
  }
  
  var result = new Array();
  var inputs = item.getElementsByTagName('input');
  var first = null;
  for (var i=0;i< inputs.length;i++) {
    var elm = inputs[i];
    if (elm.type == 'hidden') continue;
    // if radio button, use the checked one as input
    // the problem is none in family is checked, we would pick first in family
    if (elm.type == 'radio') {
      if (first) {
      if (first.parentNode != elm.parentNode) {
        if (!first.checked) result.push(first); // none checked, use first
        first = elm;
      } 
    } else 
      first = elm;
    if (elm.checked) 
      first = elm 
    else 
      continue;
    }
    result.push(elm);
  }
  if (first && !first.checked) result.push(first); // none checked, use first
  appendCollectionToArray( result, item.getElementsByTagName('textarea'));
  appendCollectionToArray( result, item.getElementsByTagName('select'));
  return result;
}
  
function getInputOfItem(item) {
  var list = item.getElementsByTagName('input');
  if (list.length) {
    var radio = null;
    for (var i=0;i<list.length;i++) {
      var node = list[i];
      if (node.type == 'radio') {
        if (node.checked) return node;
        if (!radio) radio = node;
      } else if (node.type != 'hidden') {
        return node;
      }
    }
    if (radio) return radio;
  }
  list = item.getElementsByTagName('textarea');
  if (list.length) return list[0];
  list = item.getElementsByTagName('select');
  if (list.length) return list[0];
  return null;
}
  
function getItemOfInput(input) {
  return input.parentNode.parentNode;
}


function getInputValue(input) {
  if (input.tagName == 'INPUT' && input.type == 'radio' && !input.checked) return '';
  return input.value;
}

function collectValue(item) {
  function pushUnique(a, v) {
    if ((v != '') && indexOfItemInArray(a, v) == -1) a.push(v);
    return a;
  }
    
  function addToArray(x) {
    if (xmlType(x) > XMLSimpleContent) return true;
    var input = getInputOfItem(x);
    if (input.tagName == 'SELECT') {
      // for select we take every value in the options
      var options = input.options;
      for (var i=0;i< options.length;i++) {
        pushUnique( result, options[i].value);
      }
    } else if (input.tagName == 'INPUT' && input.type == 'radio') {
      var list = x.getElementsByTagName('input');
      for (var j=0;j<list.length;j++) {
        input = list[j];
        if (input.type == 'radio') pushUnique( result, input.value);
      }
    } else {
      pushUnique( result, input.value);
    }
    return true;
  }
  
  var enumeration = getItemMetaData(item, '#em');
  if (enumeration) {
     return enumeration;
  }
  var result = new Array();
  checkEachElement('', item, addToArray);
  return result;
}
  
function clearExtraFields(input) {
  var parent = input.parentNode;
  var nodeList = parent.childNodes;
  for (var i=nodeList.length-1;i>=0;i--) {
    var node = nodeList[i];
    if (node.tagName == 'LABEL') continue;
    //Cheng Yi, do not delete hidden tag, needed for html to xml
    if (node.tagName== 'INPUT' && node.type=='hidden' && node.name=='.tg') continue;
    parent.removeChild(node);
  }
  return parent;
}

function becomeTextareaElm(item) {
  function makeTextarea(x) {
    if (x.tagName != 'TEXTAREA') {
      var nilled = isNil(getItemOfInput(x));
      //Cheng Yi
      var htmlStr = '<textarea row="3" cols="30" onchange="textChanged(this);"></textarea>';
      var node = htmlToNode(htmlStr);
    if (nilled)  node.setAttribute('onchange', 'unnilIfChanged(this)');
    copyTextAttribute(x, node);
    var parent = clearExtraFields(x);
    parent.appendChild(htmlToNode('<br>'));
    parent.appendChild(node);
    if (nilled) nilElm(getItemOfInput(node));
    }
  }
    
  checkEachNode(collectEnclosedInput(item), makeTextarea);
  return true;
}
  
function becomeTextfieldElm(item) {
  function makeTextfield(x) {
    if (x.tagName == 'INPUT') {
      if (x.type == 'text') return true; // already text, no need to do it
    }
    var nilled = isNil(getItemOfInput(x));
    var htmlStr = '<input type="text" onchange="textChanged(this);"/>';
    var node = htmlToNode(htmlStr);
    if (nilled) node.setAttribute('onchange', 'unnilIfChanged(this)');
    copyTextAttribute(x, node);
    if (node.value.length > 20) node.size = 60;
    clearExtraFields(x).appendChild(node);
    if (nilled) nilElm(getItemOfInput(node));
  }
  checkEachNode(collectEnclosedInput(item), makeTextfield);
  return true;
}

//Cheng Yi, add <input type="hidden" value="attributeName" name=".tg">
function becomeSelectListElm(item, valueList) {
  if (xmlType(item) > XMLSimpleContent) return true;
  var nilled = isNil(item);
  var node = htmlToNode ('<select size="1" onchange="noUndefined(this);" onfocus="focusGained(this);"></select>');
  var input = getInputOfItem(item);
  var val = getInputValue(input);
  node.className = input.className;
  node.name = getRealName(input);
  var options = node.options;
  var index = indexOfItemInArray(valueList, val);
  if (index == -1) {
    options[0] = new Option('**Undefined**', '', false, false);
    index = 0;
  }
  for (var i=0;i<valueList.length;i++) {
    val = valueList[i];
    options[options.length] = new Option(val, val, false, false);
  }
  node.selectedIndex = index;
  clearExtraFields(input).appendChild(node);
  if (nilled) nilElm(item);
  return true;
}
  
function becomeRadioButtonElm(item, valueList) {
  if (xmlType(item) > XMLSimpleContent) return true;
   var nilled = isNil(item);
  var input = getInputOfItem(item);
  var index = indexOfItemInArray(valueList, getInputValue(input));
  var groupName = getRealName(input) + ',' + document.preference.radioIndex++;
  var cName = input.className;
  var parent = clearExtraFields(input);
  var total = 0;
  var i;
  for (i=0;i<valueList.length;i++) {
    total += valueList[i].length;
    if (total > 80) break;
  }
  for (i=0;i<valueList.length;i++) {
    if (total > 80) parent.appendChild(htmlToNode('<br>'));
    var node = htmlToNode ('<input type="radio" onchange="radioChanged(this);" onmousedown="radioClicked(this);">');
    parent.appendChild(node);
    node.className = cName;
    node.name = groupName;
    node.value = valueList[i];
    if (i == index) node.checked = true; // IE needs node to be in document before checked is set
    parent.appendChild(document.createTextNode(valueList[i]));
  }
  if (nilled) {
    nilElm(item);
  } else if (index < 0) {
    clearElm(item);
  }
  return true;
}

function menuChosen(selectObj) {
  var menuCommand = selectObj.value;
  selectObj.selectedIndex = 0;
  selectObj.blur();
  var multiple = false;
//  document.body.style.cursor = 'wait';
//document.styleSheets[0].cssRules[0].style.cursor = 'wait';
  if (menuCommand.charAt(0) != '-') {
    eval(menuCommand+"(" + multiple + ");");
  }
//document.styleSheets[0].cssRules[0].style.cursor = 'default';
//  document.body.style.cursor = 'default';
  return false;
}
  
function getDisplayName(name) {
  function adjustCase (word, state, toUpper) {
    if (state) {
      var firstChar = word.charAt(0);
      var theRest = word.substr(1);
      firstChar = toUpper ? firstChar.toUpperCase() : firstChar.toLowerCase();
      word = firstChar + theRest;
    }
    return word;
  }
  
  if (name.charAt(0) == '@') name = name.substr(1);
  if (document.preference.breaktag == 0) return name;
  var newName = name;
  var charLen = name.length;
  if (charLen > 1) {
    if (name.indexOf('_') >- 0) {
      newName = name.replace(/_/g, ' ');
    } else if (name.indexOf('\.') >- 0) {
      newName = name.replace(/\./g, ' ');
    } else if (name.indexOf('-') >- 0) {
      newName = name.replace(/-/g, ' ');
    } else {
      var useUpper = name.charAt(0).search(/[A-Z]/) == 0;
      var newState = useUpper ? 0 : 1;
      var curState;
      var wordBegin = 0;
      var newWord = '';
      newName = '';
      for (var i=1;i<charLen;i++) {
        curState = newState;
        var curChar = name.charAt(i);
        if (curChar.search(/[A-Z]/) == 0) {
          if (curState) {
            newWord = name.substr(wordBegin,i-wordBegin);
            wordBegin = i;
            newState = 0;
          }
        } else if (curChar.search(/[a-z]/) == 0) {
          if (curState) {
            if (curState == 2) {
              newWord = name.substr(wordBegin,i-wordBegin);
              wordBegin = i;
            }
          } else {
            newWord = name.substr(wordBegin,i-wordBegin-1);
            wordBegin = i-1;
          }
          newState = 1;
        } else {
          newState = 2;
        }
        if (newWord) {
          newName += adjustCase(newWord, curState, useUpper) + ' ';
          newWord = '';
        }
      }
      newName += adjustCase(name.substr(wordBegin), curState, useUpper);
    }
  }
  return newName;
}
  
function getPrototype( tagName ) {
  var htmlStr = getPrototypeHTML( tagName );
  return htmlToNode( htmlStr.replace(/!cn!/, '') );//change value to empty
}
  
function getPrototypeHTML( tagName ) {
  var elementType = tagName.charAt(0);
  if (elementType != '@') tagName = tagName.substr(1);
  var htmlStr = getElementPrototype(elementType);
  tagName = tagName.replace(/.*,/, '');
  htmlStr = htmlStr.replace(/!tn!/g, tagName);
  return htmlStr.replace(/!dn!/g, getDisplayName(tagName));//replace tagName and dispalyName
}

//Changed by Cheng Yi, add hidden .tg, use autoSizeTextArea for attribute and simple type.
function getElementPrototype( elementType ) {
  switch ( elementType) {
    case "#":
      return '<div class="XMLComplexContent" onclick="selectelm(this, event);" ondblclick="selectelm(this, event);"><fieldset class="!tn!"><legend>- !dn!</legend><input type="hidden" value="!tn!" name=".tg">!cn!<input type="hidden" value="/!tn!" name=".tg"></fieldset></div>';
    case "$":
        return '<div class="XMLSimpleContent" onclick="selectelm(this, event);" ondblclick="selectelm(this, event);">!cn!</div>';
    case "!":
    	return '<span class="XMLSimpleType" onclick="selectelm(this, event);" ondblclick="selectelm(this, event);"><span class="!tn!"><label>!dn!: </label><input type="hidden" value="#!tn!" name=".tg"><div class="cy-textarea-container" ><textarea oninput="textAreaAutoSize(this)" name="#!tn!" value="!cn!" onfocus="focusGained(this)" onchange="unnilIfChanged(this)"></textarea><div class="cy-textarea-size"></div></div></span><br></span>';
    case "@":
        return '<span class="XMLAttribute" onclick="selectelm(this, event);" ondblclick="selectelm(this, event);"> <span class="!tn!"> <label>!dn!: </label> <input type="hidden" value="!tn!" name=".tg"><div class="cy-textarea-container" ><textarea oninput="textAreaAutoSize(this)" name="!tn!" value="!cn!" onfocus="focusGained(this)" onchange="textChanged(this)"></textarea><div class="cy-textarea-size"></div></div></span><br></span>';
    }
  return null;
}
  
function getAnonymousHTML( value ) {
  return '<span class="XMLSimpleType" onclick="selectelm(this, event);" ondblclick="selectelm(this, event);"><span class="XMLAnonymous"><input type="text" class="XMLAnonymous" name="XMLAnonymous" value="' + value + '" onfocus="focusGained(this)"></span><br></span>';
}
    
function getElementDescription(item) {
  var elemDesc = new Object();
  elemDesc.item = item;
  elemDesc.itemName = getXMLElementName(item);
  var parent = getXMLParent(item);
  if (parent) {
    if (xmlType(parent) == XMLSimpleContent) {
      parent = (xmlType(item) == XMLAttribute) ? getChildByTagNames(parent, ['DIV','SPAN']) : getXMLParent(parent);
    }
    elemDesc.parentName = getXMLElementName(parent);
  } else {
    elemDesc.parentName = '';
  }
  return elemDesc;
}
  
function checkEachElement(confirmPrompt, proto, func, option) {
  function collectMatchingElement( item, elemDesc, matchingArray) {
  // we need to collect all the elements in an array first
  // we don't want to do it on the fly because the operation would affect the xml tree
    var thisItemDesc = getElementDescription(item);
    if ((item != elemDesc.item || confirmPrompt == '')
        && thisItemDesc.itemName == elemDesc.itemName && thisItemDesc.parentName == elemDesc.parentName) {
      matchingArray.push(item);
    }
    var thisType = xmlType(item);
    if (thisType > XMLSimpleType) {
      var container = item;
      if (thisType == XMLSimpleContent) {
        if (xmlType(elemDesc.item) != XMLAttribute) return; // only look for attribute inside complex type
      } else {
        container = getChildByTagNames(item, ['FIELDSET', 'SPAN', 'DIV']);
      }
      doToEachChild(container, collectMatchingElement, elemDesc, matchingArray);
    }
  }

  var elemDesc = getElementDescription(proto);
  var matchingArray = new Array();
  if (elemDesc) { // now do it to every matching element
    var thisItem = getChildByTagNames(document.xmlForm, ['DIV']);
    collectMatchingElement( thisItem, elemDesc, matchingArray);
  }
  // first do it to the prototype, if OK do to others
  var doIt = true;
  if (confirmPrompt) {
    var documentState = getChangeCount();
    if (func(proto, option) && matchingArray.length) {
      if (getChangeCount() != documentState) confirmPrompt += "\nThis cannot be undone later.";
      if (confirm(confirmPrompt)) {
        noUndo();
      } else
        doIt = false;
    } else
      doIt = false;
  }
  if (doIt) {
    for (var i=0;i<matchingArray.length;i++) {
      func(matchingArray[i], option);
    }
  }
}
  
function getSelection() {
  var item = xmlSelection;
  if (!item) item = focusField;
  return item;
}

function mustHaveSelection() {
  var item = getSelection();
  if (!item) alert('Nothing is selected.');
  return item;
}

function doToSelected(func, multiple, option) {
  var item = mustHaveSelection();
  if (!item) {
    return false;
  } else {
    if (multiple) {
      checkEachElement('Now do you want to repeat the operation on similar elements?', item, func, option)
    } else {
      func(item, option);
    }
    return true;
  }
}
  
function duplicateSelected(multiple) {
  doToSelected(duplicateElm, multiple);
}

function copySelected(multiple) {
  doToSelected(copyElm, multiple);
}

function cutSelected(multiple) {
  doToSelected(cutElm, multiple);
}

function pasteBeforeSelected(multiple) {
  doToSelected(pasteElm, multiple, 'before');
}

function pasteAfterSelected(multiple) {
  doToSelected(pasteElm, multiple, 'after');
}

function pasteIntoSelected(multiple) {
  doToSelected(pasteElm, multiple, 'into');
}

function pasteReplaceSelected(multiple) {
  doToSelected(pasteElm, multiple, 'replace');
}

function clearSelected(multiple) {
  doToSelected(clearElm, multiple);
}

function deleteSelected(multiple) {
  doToSelected(deleteElm, multiple);
}

function nilSelected(multiple) {
  doToSelected(nilElm, multiple);
}

function makeXML() {
  if (outputToWindow()) document.xmlForm.submit();
}

function expandSelected(multiple) {
  doToSelected(expandElm, multiple);
}

function collapseSelected(multiple) {
  doToSelected(collapseElm, multiple);
}

function verifySelected(multiple) {
  doToSelected(verifyElm, multiple);
}

function becomeTextarea(multiple) {
  doToSelected(becomeTextareaElm, multiple);
}

function becomeTextfield(multiple) {
  doToSelected(becomeTextfieldElm, multiple);
}

function becomeChooseOne(multiple, func, warnSize) {
  function longestItemSize(values) {
    var result = 0;
    for (var i=0;i<values.length;i++) {
      if (values[i].length > result) result = values[i].length;
    }
    return result;
  }
  
  var item = mustHaveSelection();
  if (!item) {
    return false;
  } else {
    switch (xmlType(item)) {
      case XMLAttribute:
      case XMLSimpleType:
        var values = collectValue(item);
        if (values.length == 0) {
          alert('There are no value to choose from');
          return false;
        }
        var extraProblem = '';
        var longest = longestItemSize(values);
        if (longest > 80) extraProblem = ' some with as many as ' + longest + ' characters, ';
        if (values.length > warnSize) {
          if (!confirm('There are ' + values.length + ' choices, ' + extraProblem + 'are you sure you want to do it?')) return false;
        } else if (extraProblem) {
          if (!confirm('We have long strings, ' + extraProblem + 'are you sure you want to do it?')) return false;
        }
        doToSelected(func, multiple, values);
        return true;
      default:
        alert('You need to do it to a single input field');
        return false;
    }
  }
}
  
function becomeSelectList(multiple) {
  return becomeChooseOne(multiple, becomeSelectListElm, 30);
}

function becomeRadioButton(multiple) {
  return becomeChooseOne(multiple, becomeRadioButtonElm, 10);
}

function showHTML(item) {
  alert(item.innerHTML);
}

function getTypeInfo(name, metaElm) {
  function getTypeInfoFromType(type, derived) {
    if (!type) return '';
    if (typeof(type) == 'string') {
      if (type.substr(0,1) == '#') {
        var predefined = 'the predefined type ' + doubleQuote(type.substr(1));
        if (derived) predefined = 'derived from ' + predefined;
        return predefined;
      }
      return getTypeInfoFromType( document.schema.datatype[type], derived );
    }
    if (typeof(type) == 'object') {
      var info;
      if (!derived) {
        info = type['#dc'];
        if (info) return info;
      }
      if (type['#ct'] || type['#pt'] || type['#em'] || type['#lc']) derived = true;
      if (type['#dt'] == '#list') {
        info = 'a list';
        var listInfo = getTypeInfoFromType(type['#lt'], false);
        if (listInfo) info += ' of (' + listInfo + ')';
      } else if (type['#dt'] == '#union') {
        info = 'a union of (';
        var unionList = type['#un'];
        for (var ui=0;ui<unionList.length;ui++) {
          var unionInfo = getTypeInfoFromType(unionList[ui], false);
          if (!unionInfo) unionInfo = 'some data type';
          if (ui != unionList.length-1) unionInfo += ', ';
          info += unionInfo;
        }
        info += ')';
      } else {
        info = getTypeInfoFromType(type['#dt'], derived);
      }
      if (!info) return '';
      if (derived && info.search(/^derived from/) != 0) info = 'derived from ' + info;
      return info;
    }
  }

  var info = getTypeInfoFromType(metaElm['#dt'], false);
   if (info) {
    if (metaElm['#ni']) info += '. ' + name + ' is nillable';
    return name + ' data type is ' + info;
  }
  var model = metaElm['#cm'];
  if (model) {
    if (model == '(<.*?>)*') return 'Any content is acceptable';
    var rst = humanReadableRegexp(name, model.replace(/<(?:[^>])*,/g, '<'));
    if (metaElm['#al']) rst += '. Child elements can be in any order';
    if (metaElm['#ni']) rst += '. ' + name + ' is nillable';
    return rst;
  } else if (model == '') {
    return 'Empty content model';
  } 
  return null;
}

function selectedInfo() {
  var item = getSelection();
  var name = item ? getErrorElementName(item) : '';
  var metaElm = item ? getMetaElement(item) : document.schema.content[''];
  var comment = metaElm ? metaElm['#dc'] : '';
  if (!comment) comment = '';
  if (item) {
    if (isNil(item)) {
      if (comment) comment += ".\n";
      comment += name + ' is nil, to edit it run the clear command first.';
    }
    if (item && metaElm) {
      var typeInfo = getTypeInfo(name, metaElm);
      if (typeInfo) {
        if (comment) 
          comment += ".\n" + typeInfo;
        else 
          comment = typeInfo;
      }
    }
  }
  if (!comment) return false;
  alert(comment + '.');
  selectXMLForm(item);
  return true;
}

function aboutSelected() {
  if (selectedInfo()) return true;
  alert('No help information is available');
  return true;
}
     
function helpSelected() {
  aboutSelected();
  event.returnValue = false;
  return false;
}

function sortSelectedKey() {
  function getKeyName(item) {
    var name = getXMLElementName(item);
    if (xmlType(item) == XMLAttribute) name = '@' + name;
    return name;
  }
    
  function findRun(candidates, start, name, prop) {
    var next = start;
    var result = null;
    while ((next = next[prop]) != null) {
      if (xmlType(next)) {
        if (getKeyName(next) == name)
          candidates.push(next)
        else
          break;
      }
    }
  }
    
  function getXMLValue(item) {
    if (xmlType(item) > XMLSimpleContent) return null;
    var input = getInputOfItem(item);
    return getInputValue(input);
  }
    
  var item = mustHaveSelection();
  if (!item) return false;
  var itemType = xmlType(item);
  var parent;
  var path = [getKeyName(item)];
  if (itemType == XMLSimpleType) { 
    parent = (isSimpleWithAttribute(item)) ? item.parentNode : parent = item;
  } else if (itemType == XMLAttribute) {
    parent = getXMLParent(item);
    path.push(getKeyName(parent));
  } else
    return alert('No key has been selected.');
  while (true) {
    if (isRoot(parent)) return alert('We cannot find anything to sort.');
    var parentType = xmlType(parent);
    var parentName = getKeyName(parent);
    var candidates = new Array();
    findRun(candidates, parent, parentName, 'previousSibling');
    candidates.reverse().push(parent);
    findRun(candidates, parent, parentName, 'nextSibling');
    if (candidates.length > 1) {
      if (confirm('Are you are trying to sort "' + getKeyName(parent) + '"?')) {
        var total = candidates.length;
        var before = candidates[total-1].nextSibling;
        var sortValues = new Array();
        var valueIndex = new Object();
        var missingValue = false;
        path.reverse();
        for (var i=0;i<total;i++) {
          var searchItem = candidates[i];
          var curItem = searchItem;
          for (var j=1;j<path.length;j++) {
            curItem = getNamedXMLChild(curItem, path[j]);
            if (!curItem) break; // cannot find key
          }
          val = null;
          if (curItem) val = getXMLValue(curItem);
          if (val == null) {
            missingValue = true;
            val = '';
          }
          sortValues.push(val);
          if (valueIndex[val])
            valueIndex[val].push(searchItem)
          else
            valueIndex[val] = [searchItem];
        }
        if (missingValue && !confirm('There are one or more elements without a key or whose key value is not a simple string, do you still want to sort?')) return false;
        sortValues.sort();
        var oldOrder = new Array();
        var indexArray = new Array();
        for (var i=0;i<total;i++) {
          oldOrder.push(candidates[i]);
          candidates[i] = valueIndex[sortValues[i]].shift();
        }
        for (var i=0;i<total;i++) {
          indexArray.push(indexOfItemInArray(candidates, oldOrder[i]));
        }
        rearrangeItems(candidates, before);
        toUndo(reorderItems, candidates[0], indexArray);
        return true;
    } else
      return false;
    } else {
      parent = getXMLParent(parent);
      path.push(getKeyName(parent));
    }
  }
}

function reorderItems(item, indexArray) {
  var newIndex = new Array();
  var oldOrder = new Array();
  var newOrder = new Array();
  for (var i=0;i<indexArray.length;i++) {
    oldOrder.push(item);
    item = getXMLNextSibling(item);
  }
  for (var i=0;i<indexArray.length;i++) {
    var j = indexArray[i];
    newOrder[i] = oldOrder[j];
    newIndex[j] = i;
  } 
  rearrangeItems(newOrder, item);
  toUndo(reorderItems, newOrder[0], newIndex);
}

function rearrangeItems(newOrdering, beyondRun) {
  var parent = newOrdering[0].parentNode;
  for (var i=newOrdering.length-1;i>=0;i--) { // reverse disturbs less if alreay in order
    var toMove = newOrdering[i];
    if (toMove.nextSibling != beyondRun) {
      parent.removeChild(toMove);
      parent.insertBefore(toMove, beyondRun);
    }
    beyondRun = toMove;
  }
}

function verifyRoot() {
  var hasSchema = false;
  for (var name in document.schema.content) {
    hasSchema = true;
    break;
  }
  if (hasSchema) verifyElm(getRoot());
}

function editorSetup() {
  document.undoState = new Object();
  document.undoState.changeCount = 0;
    document.preference = new Object();
  document.schema = new Object();
  document.schema.content = new Object();
  document.schema.datatype = new Object();
  if (setUpSchemaInfo) setUpSchemaInfo();
  if (setUpPreference) setUpPreference();
  //Cheng Yi
  //verifyRoot();
}

function verifyInputField(inputField, idInfo) {
  var xmlNode = inputField;
  while (!xmlType(xmlNode)) xmlNode = xmlNode.parentNode;
  return checkValidValue(new makeValueObj(inputField.value), getMetaElement(xmlNode), idInfo);
}

function showPreference() {
  function makeCheckbox(varName, displayName) {
    var rst = '<input type="checkbox" name="' + varName + '"';
    if (document.preference[varName]) rst += 'checked="checked"';
    rst += ' />' + displayName + '. <br>';
    return rst; 
  }
  
  var targetWindow = window.open('','Preference','width=400,height=160');
  var targetDoc = targetWindow.document;
  targetDoc.open('text/html');
  targetDoc.write('<title>XML Form Editor Preference</title>');
  targetDoc.write('<form>');
  targetDoc.write(makeCheckbox('checkSchema', 'Check to see if data conforms to schema'));
  targetDoc.write(makeCheckbox('obeySchema', 'Try to disallow data that does not follow the schema'));
  targetDoc.write('<input value="Set Preference" type="button" onclick="if (opener) opener.updatePreference(this.form);" /></form>');
  targetDoc.close();
  targetWindow.focus();
}

function updatePreference(form) {
  for (var i=0;i<form.elements.length;i++) {
    if (form.elements[i].type == 'checkbox') {
      document.preference[form.elements[i].name] = form.elements[i].checked;
    }
  }
}
  
function showSchema() {
  var targetWindow = window.open('','Schema','width=600,height=230');
  var targetDoc = targetWindow.document;
  targetDoc.open('text/html');
  targetDoc.write('<title>XML Form Editor Schema</title>');
  targetDoc.write('<form name="generator">');
  targetDoc.write('<textarea name="schema" rows="10" cols="70">' + markUpCompatiable(makeSchemaScript())+ '</textarea>');
  targetDoc.write('<input value="Set Schema" type="button" onclick="if (opener) {var rst=opener.updateSchemaScript(this.form.schema.value);if (rst) {alert(rst)} else {window.close()}}" /></form>');
  targetDoc.close();
  targetWindow.focus();
}
  
function updateSchemaScript(script) {
  var saveSchema = document.schema;
  document.schema = new Object();
  document.schema.content = new Object();
  document.schema.datatype = new Object();
  try {
    eval(script);
    return '';
  } catch(err) {
    document.schema = saveSchema;
    return err;
  }
}

function showProp(x) {
  var rst = '';
  for (var p in x) {
    var val = x[p];
    if (typeof(val) == 'object') {
      val = '{' + showProp(val) + '}';
    }
    rst += p + ':' + val + '\n';
  }
  return rst;
}
