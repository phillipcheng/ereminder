// Copyright 2004-05 by Edmund K. Lai

function askForXMLtag(promptStr) {
  var errorStr = '';
  while (true) {
    var promptResult = prompt(promptStr + errorStr, '');
    if (promptResult == null) return null;
    var valueObj = new makeValueObj(promptResult);
    if (!checkValidType(valueObj, null, '#Name')) {
    return valueObj.value;
    } else {
      errorStr = "\n" + '"' + promptResult +'" is not a valid name, try again.';
    }
  }
}
  
function askForXMLnames(promptStr) {
  var errorStr = '';
  var str = '';
  while (true) {
    var promptResult = prompt(promptStr+errorStr, str);
    if (promptResult == null) {
      return null;
    } else {
      if (promptResult == '') return [];
      var rst = checkValidList(new makeValueObj(promptResult), null, '#Name');
          if (typeof(rst) == 'string') {
            errorStr = "\n" + rst + ' not valid XML name, try again';
        str = promptResult;
      } else {
        return rst;
      }
    }
  }
}

function pickNamesFromList(lst, promptStr) {
  if (lst.length == 0) return [];
  var errorStr = '';
  while (true) {
    var promptResult = prompt(promptStr+errorStr, lst.join(' '));
    if (promptResult == null) return null;
    if (promptResult == '') return [];
    var rst = promptResult.split(' ');
    errorStr = '';
    for (var i=rst.length-1;i>=0;i--) {
      var name = rst[i];
      if (name.charAt[0] == ' ') {
        rst.splice(i,1);
      } else {
        var inList = false;
        for (var j=0;j<lst.length;j++) {
          if (name == lst[j]) {
            inList = true;
            break;
          }
        }
        if (!inList) {
          if (errorStr) {
            errorStr = name;
          } else {
            errorStr += ' ' + name;
          }
        }
      }
      if (!errorStr) return rst;
      errorStr = "\n" + errorStr + ' not in the list, try again';
    }
  }
}
  
function collectComplexContentInfo(element) {
  var complexStuff = [element];
  var nonComplex = new Array();
  var unknown = new Array();
  var tbdList = new Array(); // list of elements to be written to schema
  var attributeList = new Array();
  var newElement, attributes, attributesArray;
  while (complexStuff.length) {
    while (complexStuff.length) {
      newElement = complexStuff.shift();
      attributes = askAboutAttributes(newElement);
      if (attributes == null) return false;
      if (attributes) attributeList = attributeList.concat(attributes);
      var children = askForXMLnames('Enter all the child elements of the element "' + newElement + "\"\n" +
                      "The child names should be separated by a space\n" +
                      "Leave the field empty if there is no child element\n" +
                      'Hit cancel button only if you do not want to stop creating the new element');
      if (children == null) return false;
      tbdList.push(Array(newElement, makeRegSchema(attributes, children)));
      var found = false;
      for (var i=0;i<children.length;i++) {
        var childElement = children[i];
        var childType = existsInElementsMenu(childElement);
        if (childType && childType.charAt(0) != '@') break; // already in the schema
        for (var j=0;j<tbdList.length;j++) {
          if (childElement == tbdList[j][0]) {
            found = true;
            break; // will be in the schema soon
          }
        }
        if (!found) {
          for (var n=0;j<unknown.length;n++) {
            if (childElement == unknown[n]) {
              found = true;
              break; // we already know that we need to find out more
            }
          }
        }
        if (!found) unknown.push(childElement); // first time we encounter this, put it in unknow list
      }
    }
    var complexChildren = pickNamesFromList(unknown, "We do not know about the following elements\n" +
          "Please return those that are complex element with attributes and child elements but no text value\n" +
        "Hit cancel button only if you don't cancel the whole operation");
    if (complexChildren == null) return false;
    if (complexChildren.length) {
      subtractFromArray(unknown, complexChildren); // now we know they are complex content, remove from unknown
      complexStuff = complexStuff.concat(complexChildren); // and add them to the complex content list
    }
    if (unknown.length) {
      nonComplex = nonComplex.concat(unknown); // the rest go to the non complex list
    }
    unknown = []; // unknown has be divded up by complex or non-complex, so nothing left
  }
  // now find out which of the non-complex have attributes
  var hasAttributes = pickNamesFromList(nonComplex, "The following elements have text value and non child elements\n"         + "Please return those that have attributes\n" +
          "Hit cancel button only if you don't cancel the whole operation");
  if (hasAttributes == null) return false;
  if (hasAttributes.length) {
    subtractFromArray(nonComplex, hasAttributes); // only thing left in noncomplex should be simple type
    while (hasAttributes.length) {
      newElement = hasAttributes.shift();
      attributes = askAboutAttributes(newElement);
      if (attributes == null) return false;
      if (attributes.length) attributeList = attributeList.concat(attributes);
      tbdList.push(Array(newElement, makeRegSchema(attributes,['#string'])));
    }
  }
  // now put tbd list up to the menu and schema
  while (tbdList.length) {
    var schemaArray = tbdList.shift();
    newElement = schemaArray[0];
    updateSchema(newElement, schemaArray[1]);
    var placeBefore = schemaArray[1].indexOf('<#string>') < 0 ? 'XMLSimpleContent' : 'XMLAttribute';
    addToElementsMenu('#' + newElement, placeBefore);
  }
  while (nonComplex.length) {
    newElement = nonComplex.shift();
    addToElementsMenu('!' + newElement, 'XMLAttribute');
  }
  while (attributeList.length) {
    newElement = attributeList.shift();
    addToElementsMenu('@' + newElement);
  }
  return true;
}

function makeRegSchema(attributes, children) {
  var rst = '';
  if (attributes.length) rst = '(<@' + attributes.join('>)(<@') + '>)';
  if (children.length) rst += '(<' + children.join('>)(<') + '>)';
  return rst;
}
  
function askAboutAttributes(element) {
  return askForXMLnames('Enter all the attributes of the element "' + element + "\"\n" +
                  "The attributs names should be separated by a space\n" +
                "Leave the field empty if there is no attribute\n" +
                'Hit cancel button only if you do not want to stop creating the new element');
}

function existsInElementsMenu(name) {
  var select = document.menuForm.elementsMenu;
  var options = select.options;
  var i;
  for (i=0;i<options.length;i++) {
    var optionValue = options[i].value;
    if (name == optionValue.substr(1)) return optionValue; // already there, no need to add
  }
  return null;
}
  
function addToElementsMenu(optionValue, before) {
  var elementType = optionValue.charAt(0);
  var elementName = optionValue.substr(1);
  if (elementType == '!' && !document.schema.content[elementName]) { 
    setSchema(elementName, {'#dt':'#string'});  
  } else if (elementType == '@' && !document.schema.content[optionValue]) {
    setSchema(optionValue, {'#dt':'#string'});  
  }
  var select = document.menuForm.elementsMenu;
  var where = before ? indexOfValueInOptions(select.options,before)-1: -1;
  addOptionToSelect(select, elementName, optionValue, where);
}

function makeElementChosen(selectObj) {
  function followSchema() {
    if (document.preference.obeySchema) {
      alert('You cannot make anything that is not already defined in the schema');
      return true;
    }
    return false;
  }
    
  var menuCommand = selectObj.value;
  selectObj.selectedIndex = 0;
  selectObj.blur();
  if (menuCommand == 'XMLAnonymous') {
    document.xmlForm.scrap = htmlToNode(getAnonymousHTML( '' ));
  } else if (menuCommand == 'XMLAttribute') {
    if (followSchema()) return false;
    var newAttrib = askForXMLtag('Please enter the name of the attribute that you want to create');
    if (newAttrib) {
      document.xmlForm.scrap = getPrototype('@'+newAttrib);
      addToElementsMenu('@'+newAttrib);
    }
  } else if (menuCommand == 'XMLSimpleContent' || menuCommand == 'XMLComplexContent') {
    if (followSchema()) return false;
    var newElement = askForXMLtag('Please enter the name of the element that you want to create');
    if (newElement) {
      var sameAs = existsInElementsMenu(newElement);
      if (sameAs && sameAs.charAt(0) == '@') {
        sameAs = null; // it is OK to have name as attribute
      }
      if (sameAs) {
        newElement = sameAs;
      } else {
        if (menuCommand == 'XMLSimpleContent') { 
        var attributes = askAboutAttributes(newElement);
        if (attributes == null) return;
        if (attributes.length == 0) {
          newElement = '!' + newElement;
        } else {
          updateSchema(newElement, makeRegSchema(attributes, ['#string']));
          newElement = '#' + newElement;
          while (attributes.length) {
            addToElementsMenu('@' + attributes.shift());
          }
        }
        addToElementsMenu(newElement, 'XMLAttribute');
      } else { // complex content, we need child elements
        if (!collectComplexContentInfo(newElement)) return false;
        newElement = '#' + newElement;
      }
    }
    document.xmlForm.scrap = makeElementFromMenu(newElement);
    }
  } else {
    document.xmlForm.scrap = makeElementFromMenu(menuCommand);
  }
  return false;
}
  
function makeElementFromMenu(menuValue) {
  var elementType = menuValue.charAt(0);
  if (elementType != '#') {
    return getPrototype(menuValue);
  } else {
    return makeKnownElement(menuValue.substr(1));
  }
}
