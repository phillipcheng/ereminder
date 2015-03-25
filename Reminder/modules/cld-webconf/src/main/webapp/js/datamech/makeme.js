
// Copyright 2004-05 by Edmund K. Lai

function outputToWindow(x) {
  if (document.preference.scriptLoc == 'client') {
    outputToNewWindow(document.xmlForm);
    return false;
  }
  if (!isMoz  && !isSafari && !isKonqueror) return true;
  if (confirm('Mozilla bug 204784 can cause our CGI script to fail. The bug will be fixed in a coming Mozilla release. Safari has the same problem. In the mean time you can continue but the result is likely to be wrong if you made any insertion/deletion. The work around is to take a snap shot after you are done with your editing, and submit from the new snap shot.')) return true;
  return false;
}

function writeNode(targetDoc, node) {
  var i;
  var tag = node.tagName.toLowerCase();
  targetDoc.write('<' + tag);
  var attributes = node.attributes;
//  if (node.hasAttributes()) { // not supported by IE
  if (attributes.length) {
    if (tag == 'input') {
      if (node.type == 'text') {
        if (isIE) {
          // IE don't considered type and value specified
          targetDoc.write(' type="text"');
        } else
          node.setAttribute('value', markUpCompatiable(node.value));
      } else if (node.type == 'radio') {
          if (node.checked)
            node.setAttribute('checked', 'checked')
          else
            node.removeAttribute('checked');
      }
      if (isIE) {
        // IE don't considered type and value specified
        targetDoc.write(' value="' + markUpCompatiable(node.value) + '"');
      }
    } else if (tag == 'textarea')
      node.setAttribute('value', markUpCompatiable(node.value))
    else if (tag == 'select') {
      var options = node.getElementsByTagName('option');
      for (i=0;i< options.length;i++) {
        if (i == node.selectedIndex)
          options[i].setAttribute('selected', 'selected')
        else
          options[i].removeAttribute('selected');
      }
    } else if (isIE && tag == 'body') {
      targetDoc.write(' onload="editorSetup();"');
    } else if (isIE) {
      if (node.style.display) targetDoc.write(' style="display: ' + node.style.display + ';"');
    }
    if (isIE && tag == 'script' && node.innerHTML) {
      targetDoc.write('>');
      return writeSchema(node, targetDoc);
    }
    for (i=0;i<attributes.length;i++) {
      var attribute = attributes[i];
      if (attribute.specified) targetDoc.write(' ' + attribute.name + '="' + markUpCompatiable(attribute.value) + '"');
    }
  }
  var childNodes = node.childNodes;
//  if (node.hasChildNodes()) { // not supported by IE
  if (childNodes.length) {
    if (tag == 'form' || tag == 'select')
      targetDoc.writeln('>')
    else
      targetDoc.write('>');
    if (tag == 'script' && node.id == 'schemaScript' && !document.preference.publicSchema) return writeSchema(node, targetDoc);
    for (i=0;i< childNodes.length;i++) {
      var childNode = childNodes[i];
      if (childNode.nodeType == 1)
        writeNode(targetDoc, childNode)
      else
        targetDoc.write(childNode.nodeValue);
    }
    targetDoc.write('</' + tag + '>');  
    if (tag == 'span' || tag == 'div' || tag == 'option' || tag == 'select' ) targetDoc.write("\n");
  } else if (tag == 'script') {
    targetDoc.write('>');
    targetDoc.writeln('<' + '/script>'); // for some reason broswer don't like script slash
  } else if (tag == 'title') {
    targetDoc.write('>' + document.title + '</title>');
  } else if (tag == 'style') {
    if (isIE)
      targetDoc.writeln('>' + document.styleSheets[0].cssText + '<' + '/style' + '>')
    else
      targetDoc.writeln('><' + '/style' + '>');
  } else
    targetDoc.write(' /' + '>');
}

function writeSchema(node, targetDoc) {
  var scriptContent = node.innerHTML;
  var beginOffset = scriptContent.indexOf('// begin preference') + 20;
  var endOffset = scriptContent.indexOf('// end preference');
  if (!document.preference.publicSchema) {
    scriptContent = scriptContent.substr(0,beginOffset) + makePreferenceScript() + scriptContent.substr(endOffset);
    beginOffset = scriptContent.indexOf('// begin schema') + 16;
    endOffset = scriptContent.indexOf('// end schema');
    scriptContent = scriptContent.substr(0,beginOffset) + makeSchemaScript() + scriptContent.substr(endOffset);
  }
  targetDoc.writeln(scriptContent + '<' + '/script' + '>');
  return true;
}
  
function snapShot() {
  if (isSafari && !confirm('This does not work with Safari 1.2.1, proceed only if you want to try it on a later version of Safari')) return false;
  selectXMLForm(); // deselect it because we don't want the selection bg color
  var targetWindow = window.open();
  var targetDoc = targetWindow.document;
  targetDoc.open('text/html');
  var fakeDoc;
  var useInnerHTML = false; // use innerHTML is much simplier, but hangs when element was duplicated
  if (isIE && !useInnerHTML) {
    // for IE it would be faster to write to a buffer first
    fakeDoc = new Object();
    fakeDoc.dataString = new String('');
    fakeDoc.write = fakeWrite;
    fakeDoc.writeln = fakeWriteln;
  } else
    fakeDoc = targetDoc;
  fakeDoc.writeln('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">');
  fakeDoc.writeln("<html>");
  if (isIE  && useInnerHTML) {
    var docContent = document.getElementsByTagName('html')[0].innerHTML;
    var beginOffset = docContent.indexOf('// begin preference') + 20;
    var endOffset = docContent.indexOf('// end preference');
    targetDoc.write( docContent.substr(0,beginOffset) );
    targetDoc.write( makePreferenceScript() );
    beginOffset = docContent.indexOf('// begin schema') + 16;
    targetDoc.write( docContent.substr(endOffset,beginOffset-endOffset) );
    docContent = docContent.substr(beginOffset);
    targetDoc.write( makeSchemaScript() );
    endOffset = docContent.indexOf('// end schema');
    docContent = docContent.substr(endOffset);
    targetDoc.writeln(docContent);
  } else { 
    // opera doesnot support innerHTML, and Moz innerHTML does not have latest value in form elements
    // so we have to write it out ourself
    writeNode( fakeDoc, document.getElementsByTagName('head')[0] );
    writeNode( fakeDoc, document.getElementsByTagName('body')[0] );
  }
  fakeDoc.writeln("</html>");
  if (isIE && !useInnerHTML)
    targetDoc.write(fakeDoc.dataString.toString());
  targetDoc.close();
}

function fakeWrite(x) {
  this.dataString = this.dataString.concat(x);
}

function fakeWriteln(x) {
  this.dataString = this.dataString.concat(x, "\n");
}

