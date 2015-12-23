
// Copyright 2004-05 by Edmund K. Lai

function makeKnownElement(name, metaInfo) {
  var elementStack = new Array();
  if (!metaInfo) metaInfo = guessMetaInfo(name);
  if (!metaInfo) return null;
  return makeElementsWithMetaInfo(elementStack, name, metaInfo);
}
  
function makeElementsWithMetaInfo(elementStack, name, metaInfo) {
  for (var i=0;i<elementStack.length;i++) {
    if (elementStack[i] == name) return null; // avoid inifnite recursion
  }
  // TBD : we need a context fo find the right element and schema
  var schema;
  if (metaInfo) schema = metaInfo['#cm'];
  if (!schema) schema = getSchema(name);
  if (schema) schema = expandSchema(schema);
  elementStack.push(name);
  var result = null;
  if (schema != undefined) {
    if (schema.indexOf('<#string>') >= 0  || schema.indexOf('<.*?>') >= 0) {
      result = makeElementWithAttribute(elementStack, name, schema, metaInfo);
    } else {
      result = makeComplexContent(elementStack, name, schema, metaInfo);
    }
  } else {
    result = getPrototype( '!' + name ); // just a simple element
  }
  elementStack.pop();
  return result; 
}

function makeChildAndAttribute(elementStack, parentName, container, before, schema, metaInfo ) {
  var results = schema.match(/<.*?>/g);
  if (!results) return;
  var node;
  for (var i=0;i<results.length;i++) {
    var tag = results[i].substr(1,results[i].length-2);
    if (tag.charAt(0) == '@') { // it is an attribute
      node = getPrototype(tag);
      var defValue = getMetaDataByPath([parentName,tag], '#df');
      if (defValue) {
        // we know it is input is either text field or area
        getInputOfItem(node).value = defValue;
      }
        var options = getMetaDataByPath([parentName,tag], '#em');
      if (options) {
        becomeSelectListElm(node, options);
      }
    } else if (tag == '#string') {
// alert('TBD: what should we do if tag is #string');
    } else {
      var childMetaInfo = metaInfo[tag];
      if (!childMetaInfo) {
        var fullpath = metaInfo['#ph'] ? metaInfo['#ph'] + tag : tag;
        childMetaInfo = document.schema.content[fullpath];
      }
      node = makeElementsWithMetaInfo(elementStack, tag, childMetaInfo);
    }
    if (node) container.insertBefore(node, before);
  }
}
  
function makeElementWithAttribute(elementStack, name, schema, metaInfo) {
  var container = getPrototype('$');
  var node = getPrototype('!' + name);
  if (node) {
     container.appendChild(node);
  } else {
    return null;
  }
  makeChildAndAttribute(elementStack, name, container, null, schema, metaInfo);
  return container; 
}
  
function makeComplexContent(elementStack, name, schema, metaInfo) {
  var container = getPrototype('#' + name);
  makeChildAndAttribute(elementStack, name, container.firstChild, container.firstChild.lastChild, schema, metaInfo); 
  return container; 
}

function expandSchema (schema) {
  function replaceRepeatIndex (match, tag, repeatIndex) {
    replaced = true;
    return repeatElements(tag, repeatIndex);
  }
  
  function replaceGroup (match, tag, repeatIndex, option) {
    if (tag.indexOf('|') != -1) {
      tag = pickFromChoice(tag);
    }
    if (repeatIndex) {
      tag = repeatElements(tag, repeatIndex);
    } else if (document.preference.isLargeSchema) {
  //      if (option == '?' || option == '*') tag = '';
      if (option == '?') tag = '';
    }
    replaced = true;
    return tag;
  }
  
  function pickFromChoice(str) {
//    str = str.replace(/^\|+/, '');
    str = str.replace(/\|{2,}/g, '|').replace(/^\|/, '').replace(/\|$/, '');
//    return str.split('|')[0];
    var choices = str.split('|');
    return choices[Math.round(Math.random()*(choices.length-1))];
  }
  
  function repeatElements(tag, repeatIndex) {
    var repeatCount = repeatIndex.split(',')[0];
    if (!repeatCount) repeatCount = 1;
    var seq = tag;
    for (;repeatCount>1;repeatCount--) seq += tag;
    return seq;
  }

  if (!schema) return schema;
  if (schema == '(<.*?>)*') return '';
  schema = schema.replace(/\(</g, '<').replace(/>\)/g, '>');  
  var replaced;
  // TBD : do we need another loop ?
  do {
    replaced = false;
    schema = schema.replace(/(<[^>]*?>)\)\{(.*?)/, replaceRepeatIndex); // <a>{} => <a><a>
  } while(replaced);
  if (document.preference.isLargeSchema) {
    schema = schema.replace(/<[^>]*?>[*?]/g, ''); // <a>* => . <a>? => 
    schema = schema.replace(/(<[^>]*?>)+?/g, "$1"); // <a>* => <a>. <a>+ => <a>. <a>? => <a>
  } else {
    schema = schema.replace(/(<[^>]*?>)[*+?]?/g, "$1"); // <a>* => <a>. <a>+ => <a>. <a>? => <a>
  }
  do {
    replaced = false;
    schema = schema.replace(/\(\|*\)/g, ''); // TBD : why do we need this ?
    schema = schema.replace(/\(([^\(^\)]*?)\)(?:\{(.*?)|([*+?]?))/, replaceGroup); // (<a><b>)* or (<a><b>){}
  } while (replaced);
  if (schema.indexOf('|') != -1) schema = pickFromChoice(schema); // TBD : is this necessary ?
  return schema;
}

function checkTagSequence(parent, elementList, nilled, exceptAttribute) {
  function schemaOrderSort(item1, item2) {
    return (schema.indexOf(item1) - schema.indexOf(item2));
  }

  var tagSequence = '';
  var parentType = xmlType(parent);
  var metaElm = getMetaElement(parent);
  if (nilled && !metaElm['#ni']) return getErrorElementName(parent) + ' is nil but it is not nillable';
  var schema;
  if (metaElm) schema = metaElm['#cm'];
  if (schema == undefined) {
    if (metaElm && metaElm['#dt'] == '#anyType') return null;
    return 'This is not a valid element under this context';
  }
  var regexp = schema.replace(/<(?:[^>])*,/g, '<');
  var attributeNames = getXMLAttributeNames(parent);
  if (attributeNames) {
    tagSequence = attributeNames.sort(schemaOrderSort).join('');
    if (exceptAttribute) {
      tagSequence = tagSequence.replace('<@' + exceptAttribute + '>', '');
    }
  }
  if (nilled) {
    regexp = regexp.replace(/(?:\(\(|\(<[^@]).*/, '');
  }
  if (elementList) {
    var elementArray = new Array();
    for (var i=0; i< elementList.length; i++) {
      elementArray.push('<' + getXMLElementName(elementList[i]) +'>');
    } 
    var metaElm = getMetaElement(parent);
    if (metaElm && metaElm['#al']) {
      elementArray.sort(schemaOrderSort);
    }
    tagSequence += elementArray.join('');
  } else if (parentType == XMLSimpleContent || parentType == XMLSimpleType) {
    tagSequence += '<#string>';
    if (!schema) schema = '<#string>';
    if (!regexp) regexp = '<#string>';
  }
  if (tagSequence.search('^' + regexp + '$') == -1) {
    var typeInfo = null;
//    var metaElm = getMetaElement(parent);
    var parentName = getErrorElementName(parent);
    if (metaElm) typeInfo = getTypeInfo(parentName, metaElm);
    tagSequence = tagSequence.replace(/<@(.*?)>/g, "<attribute $1>");
    tagSequence = tagSequence.replace(/></g, ', ').substr(1, tagSequence.length-2);
    var tagArray = tagSequence.split(', ');
    tagSequence = '';
    var prevItem = tagArray[0];
    var repeatCount = 1;
    for (var j=1;j<tagArray.length;j++) {
      if (tagArray[j] == prevItem) {
        repeatCount++;
      } else {
        if (prevItem.charAt(0) == '#') prevItem = 'a string';
        tagSequence += prevItem;
        if (repeatCount > 1) tagSequence += '(' + repeatCount + ' times)';
        tagSequence += ', ';
        prevItem = tagArray[j];
        repeatCount = 1;
      }
    }
    tagSequence += prevItem;
    if (repeatCount > 1) tagSequence += '(' + repeatCount + ' times)';
    var errorMsg = "Content model mismatch.\n" + parentName + " content is " + tagSequence + ".\n";
    if (typeInfo) return errorMsg + typeInfo;
    return errorMsg + humanReadableRegexp(parentName, regexp);
  }
  return null;
}

function okToProceed(prompt, parent, elementList, exceptAttribute) {
  if (!document.preference.checkSchema) return true;
  var error = checkTagSequence(parent, elementList, false, exceptAttribute);
  if (!error) return true;
  return overrideSchema(prompt + '. ' + error);
}

function overrideSchema(prompt) {
  var preference = document.preference;
  if (!preference.checkSchema) return true;
  if (preference.obeySchema) {
    alert(prompt + '. Operation is forbidden by the schema.');
    return false;
  }
  return (confirm(prompt + '. Do you still want to do it?'));
}

function updateSchema(element, schema) {
  if (document.preference.obeySchema) return false;
  var obj = new Object();
  obj['#cm'] = schema;
  setSchema(element, obj);
}

function guessMetaInfo(elementName) {
  if (document.schema.content[elementName]) return document.schema.content[elementName];
  var toMatch = ',' + elementName + '$';
  for (var name in document.schema.content) {
    if (name.search(toMatch) > 0) {
      return document.schema.content[name];
    }
  }
  return null;
}

function getSchema(elementName) {
  var schemaElm = document.schema.content[elementName];
  if (schemaElm) {
    return schemaElm['#cm'];
  }
  return null;
}

function setSchema(element, data) {
  document.schema.content[element] = data;
}

function setTypeDef(type, data) {
  document.schema.datatype[type] = data;
}

function makeSchemaScript() {
  function getValueStr(value) {
    var rst;
    switch (typeof(value)) {
      case 'string':
        return singleQuote(value);
      case 'object': // array or assoc list
        if (value.splice) { // it is an array
          rst = '[';
          for (var i=0;i<value.length;i++) {
            rst += getValueStr(value[i]) + ',';
          }
          return rst.substr(0,rst.length-1) + ']';
        } else { // it is an assoc list
          rst = '{';
          for (var key in value) {
            var valueStr = getValueStr(value[key]);
            if (key == '#pt') {
              valueStr = valueStr.replace(/\\/g, "\\\\");
            } else if (key == '#dc') {
              valueStr = valueStr.replace(/\n/g, "\\n");
            }
            rst += "'" + key + "':" + valueStr + ",";
          }
          return rst.substr(0,rst.length-1) + '}';
        }
      default:
        return value;
    }
  }

  var total = '';
  var value;
  var content = document.schema.content;
  for (var elm in content) {
    total += "setSchema('" + elm + "'," + getValueStr(content[elm]) + ");\n";
  }
  var types = document.schema.datatype;
  for (var type in types) {
    total += "setTypeDef('" + type + "'," + getValueStr(types[type]) + ");\n";
  }
  return total;
}

function showSchemaScript() {
  alert(makeSchemaScript());
}

function getMetaElementByPath(path) {
  var pathLevel = path.length;
  var metaData;
  for (var i=0;i<pathLevel;i++) {
    metaData = document.schema.content;
    for (var j=i;j<pathLevel;j++) {
      if (metaData['#ph']) { // if type has a special path, try it first
        var typeElement = metaData['#ph']+path[j];
        metaData = metaData[path[j]];
        if (!metaData) metaData = document.schema.content[typeElement];
      } else
        metaData = metaData[path[j]];
      if (!metaData) break;
    }
    if (metaData) return metaData;
  }
  return null;
}

function getMetaDataByPath(path, key) {
  var metaData = getMetaElementByPath(path);
  if (!metaData) return metaData;
  return metaData[key];
}

function getMetaElement(xmlNode) {
  var path = new Array();
  if (xmlType(xmlNode.parentNode) == XMLSimpleContent && xmlType(xmlNode) != XMLAttribute) {
    path.push('');
  }
  var testNode = xmlNode;
  while (testNode) {
    var name = getXMLElementName(testNode);
    if (xmlType(testNode) == XMLAttribute) name = '@' + name;
    path.push(name);
    testNode = getXMLParent(testNode);
  }
  path.reverse();
  var metaElm = getMetaElementByPath(path);
  if (metaElm && xmlType(xmlNode) == XMLSimpleType && metaElm['']) {
    metaElm = metaElm[''];
  }
  return metaElm;
}

function getItemMetaData(xmlNode, key) {
  var elm = getMetaElement(xmlNode);
  if (!elm) return elm;
  return elm[key];
}

function humanReadableRegexp(name, regexp) {
  regexp = regexp.replace(/<#string>/g, '<a string>');
  regexp = regexp.replace(/<@(.*?)>/g, "<attribute $1>");
  regexp = regexp.replace(/\)(.*?)\(/g, ")$1, (").replace(/\(</g, '').replace(/>\)/g, '');
  regexp = regexp.replace(/\|,/g, ' or');
  regexp = regexp.replace(/\?/g, '(optional)').replace(/\+/g, '(1 or more times)');
  regexp = regexp.replace(/\*/g, '(0 or more times)');
  while (true) {
    var matchRst = regexp.match(/{(.+?)(,?)(.*?)}/);
    if (!matchRst) break;
    var replacement = '(' + matchRst[1];
    if (matchRst[3]) {
      replacement += ' to ' + matchRst[3];
    } else if (matchRst[2]) {
      replacement += ' or more';
    }
    replacement += ' times)';
    regexp = regexp.replace(matchRst[0], replacement);
  }
  return name + ' has a content model of ' + regexp;
}