function PwylUtil() {
    this.topId = "ppw_page_top";
    this.pageTopId = "ppw_page_body";
    this.toolbarId = "ppw_toolbar";
    this.elemsExcludedFromDomPath = {
        script: 1
    };
    this.os = "win"
}
PwylUtil.prototype.initializeEditorComponents = function () {
    this.top = document.getElementById(this.topId);
    this.pageTop = document.getElementById(this.pageTopId);
    this.toolbar = document.getElementById(this.toolbarId);
    this.toolbarContent = document.getElementById("ppw_toolbar_content");
    if (navigator.appVersion.indexOf("Win") > -1) {
        this.os = "win"
    }
    if (navigator.appVersion.indexOf("Mac") > -1) {
        this.os = "mac"
    }
    if (navigator.appVersion.indexOf("X11") > -1 || navigator.appVersion.indexOf("Linux") > -1) {
        this.os = "linix"
    }
};
PwylUtil.prototype.toArray = function (nodeList) {
    var a = [];
    if (!nodeList) {
        return a
    }
    for (var i = 0; i < nodeList.length; i++) {
        a.push(nodeList[i])
    }
    return a
};
PwylUtil.prototype.getPosition = function (elem) {
    var leftX = 0;
    var leftY = 0;
    if (elem.offsetParent) {
        while (elem.offsetParent) {
            leftX += elem.offsetLeft;
            leftY += elem.offsetTop;
            elem = elem.offsetParent
        }
    } else {
        if (elem.x) {
            leftX += elem.x;
            leftY += elem.y
        }
    }
    return {
        x: leftX,
        y: leftY
    }
};
PwylUtil.prototype.moveElement = function (elem, x, y) {
    if (YAHOO.env.ua.gecko >= 1.9) {
        elem.style.left = x + "px";
        elem.style.top = y + "px"
    } else {
        YAHOO.util.Dom.setXY(elem, [x, y])
    }
};
PwylUtil.prototype.getOffsetHeight = function (elem, parent) {
    var currOffset = 0,
        e = elem;
    while (e != null && e != parent && e != document.body) {
        currOffset += e.offsetTop;
        e = e.offsetParent
    }
    return currOffset
};
PwylUtil.prototype.getFirstParentWithClass = function (elem, parentClass) {
    var e = elem;
    while (e != document.body) {
        if (YAHOO.util.Dom.hasClass(e, parentClass)) {
            return e
        }
        e = e.offsetParent
    }
    return null
};
PwylUtil.prototype.getElementAndSubelements = function (idOrElem, includeSubelements) {
    var elem = (typeof (idOrElem) == "string") ? ppw.paths.get(idOrElem) : idOrElem;
    var elems = [elem];
    if (elem.nodeType == Node.ELEMENT_NODE && (includeSubelements == undefined || includeSubelements)) {
        elems.concatArray(elem.getElementsByTagName("*"))
    }
    return elems
};
PwylUtil.prototype.areSiblings = function (node1, node2) {
    return node1 && node2 && ppw.util.toArray(node1.parentNode.childNodes).contains(node2)
};
PwylUtil.prototype.toString = function (obj) {
    if (typeof (obj) == "object") {
        var str = "{\n";
        for (var key in obj) {
            str += "\n" + key + "= " + obj[key]
        }
        return str + "\n}"
    }
    return obj + ""
};
PwylUtil.prototype.getChildElements = function (elem) {
    var children = [];
    for (var i = 0; i < elem.childNodes.length; i++) {
        if (elem.childNodes[i].nodeType == Node.ELEMENT_NODE) {
            children.push(elem.childNodes[i])
        }
    }
    return children
};
PwylUtil.prototype.depthFirstRecursion = function (rootNode, callback, args, onlyVisible, cloneNode) {
    for (var i = 0; i < rootNode.childNodes.length; i++) {
        if (rootNode.childNodes[i].nodeType == Node.ELEMENT_NODE) {
            if (onlyVisible && (YAHOO.util.Dom.getStyle(rootNode.childNodes[i], "display") == "none" || YAHOO.util.Dom.getStyle(rootNode.childNodes[i], "visible") == "hidden")) {
                continue
            }
            ppw.util.depthFirstRecursion(rootNode.childNodes[i], callback, args, onlyVisible, (cloneNode ? cloneNode.childNodes[i] : null))
        }
    }
    callback(rootNode, args, cloneNode)
};
PwylUtil.prototype.topDownDepthFirstRecursion = function (root, callback, cloneNode, onlyVisible) {
    if (callback(root, cloneNode)) {
        ppw.util._topDownDepthFirstRecursion(root, callback, cloneNode, onlyVisible)
    }
};
PwylUtil.prototype._topDownDepthFirstRecursion = function (root, callback, cloneNode, onlyVisible) {
    var currElem, currClone, i;
    for (i = 0; i < root.childNodes.length; i++) {
        currElem = root.childNodes[i];
        if (cloneNode) {
            currClone = cloneNode.childNodes[i]
        }
        if (currElem.nodeType == Node.ELEMENT_NODE) {
            if (onlyVisible && (YAHOO.util.Dom.getStyle(currElem, "display") == "none" || YAHOO.util.Dom.getStyle(currElem, "visible") == "hidden")) {
                continue
            }
            if (callback(currElem, currClone)) {
                ppw.util._topDownDepthFirstRecursion(currElem, callback, currClone)
            }
        }
    }
};
PwylUtil.prototype.getAttribute = function (node, attrName) {
    if (node.nodeType != Node.ELEMENT_NODE) {
        return null
    }
    if (node[attrName]) {
        return node[attrName]
    }
    for (var i = 0; i < node.attributes.length; i++) {
        var currAttr = node.attributes[i];
        if (currAttr.specified && currAttr.name == attrName) {
            return currAttr.value
        }
    }
    return null
};
PwylUtil.prototype.removeElementsOfType = function (tagName, root) {
    if (!root) {
        root = document
    }
    var elems = root.getElementsByTagName(tagName);
    while (elems.length > 0) {
        elems[0].parentNode.removeChild(elems[0])
    }
};
PwylUtil.prototype.generateUniqueId = function () {
    return new Date().getTime() + "" + Math.round(Math.random() * 10000000000)
};
PwylUtil.prototype.isNonEmptyText = function (e) {
    return e.nodeType == 3 && e.nodeValue.strip().length > 0
};
PwylUtil.prototype.removeElement = function (elem) {
    if (typeof (elem) == "string") {
        elem = document.getElementById(elem)
    }
    elem.parentNode.removeChild(elem)
};
PwylUtil.prototype.filter = function (elems, callback) {
    var filteredElems = [];
    for (var i = 0; i < elems.length; i++) {
        if (callback(elems[i])) {
            filteredElems.push(elems[i])
        }
    }
    return filteredElems
};
PwylUtil.prototype.$ = function (id) {
    return document.getElementById(id)
};
PwylUtil.prototype.makeInvisible = function (object) {
    switch (object.nodeType) {
        case Node.ELEMENT_NODE:
            ppw.util.applyProperty(object, "display", "none");
            break;
        case Node.TEXT_NODE:
            if (object.nodeType == Node.TEXT_NODE && object.nodeValue.strip().length > 0) {
                var span = document.createElement("span");
                span.innerHTML = object.nodeValue.strip();
                object.parentNode.replaceChild(span, object);
                ppw.util.applyProperty(span, "display", "none")
            }
            break
    }
};
PwylUtil.prototype.makeVisible = function (object) {
    if (object.nodeType == Node.ELEMENT_NODE) {
        ppw.util.undoApplyProperty(object, "display")
    }
};
PwylUtil.prototype.isVisible = function (elem) {
    return YAHOO.util.Dom.getStyle(elem, "display") != "none"
};
PwylUtil.prototype.applyProperty = function (elem, propertyName, value, isAttribute, saveCurrentValue) {
    if (!elem || elem.nodeType != Node.ELEMENT_NODE) {
        return
    }
    if (saveCurrentValue == undefined || saveCurrentValue) {
        ppw.util.saveCurrentValueOfProperty(elem, propertyName, isAttribute)
    }
    if (isAttribute) {
        elem[propertyName] = value
    } else {
        elem.style[propertyName] = value
    }
};
PwylUtil.prototype.removeAttribute = function (elem, propertyName) {
    if (elem.nodeType != Node.ELEMENT_NODE || !elem.getAttribute(propertyName)) {
        return
    }
    ppw.util.saveCurrentValueOfProperty(elem, propertyName, true);
    elem.removeAttribute(propertyName)
};
PwylUtil.prototype.restoreAttribute = function (elem, propertyName) {
    ppw.util.undoApplyProperty(elem, propertyName, true)
};
PwylUtil.prototype.saveCurrentValueOfProperty = function (elem, propertyName, isAttribute) {
    var currValueOfProperty = isAttribute ? elem.getAttribute(propertyName) : YAHOO.util.Dom.getStyle(elem, propertyName);
    var oldPropName = "old_" + (isAttribute ? "attrib" : "style") + "_" + propertyName;
    if (!elem[oldPropName]) {
        elem[oldPropName] = []
    }
    elem[oldPropName].push(currValueOfProperty + "")
};
PwylUtil.prototype.undoApplyProperty = function (elem, propertyName, isAttribute) {
    if (!elem || elem.nodeType != Node.ELEMENT_NODE) {
        return
    }
    var oldPropName = "old_" + (isAttribute ? "attrib" : "style") + "_" + propertyName;
    var value = (elem[oldPropName] && elem[oldPropName].length > 0) ? elem[oldPropName].pop() : "";
    if (value) {
        if (isAttribute) {
            elem.setAttribute(propertyName, value)
        } else {
            elem.style[propertyName] = value
        }
    }
};
PwylUtil.prototype.applyToElementAndParents = function (elem, callback) {
    if (elem == ppw.util.top) {
        return
    }
    if (elem.nodeType == Node.ELEMENT_NODE) {
        callback(elem)
    }
    ppw.util.applyToElementAndParents(elem.parentNode, callback)
};
PwylUtil.prototype.isParent = function (child, parent) {
    if (child == ppw.util.top || child == document) {
        return false
    }
    if (child == parent) {
        return true
    }
    return ppw.util.isParent(child.parentNode, parent)
};
PwylUtil.prototype.executeOnAllElementsOfTag = function (tags, functionCallback) {
    for (var i = 0; i < tags.length; i++) {
        var elements = ppw.util.top.getElementsByTagName(tags[i]);
        for (var j = 0; j < elements.length; j++) {
            functionCallback(elements[j])
        }
    }
};
PwylUtil.prototype.clearSelectedElements = function () {
    var selected = [];
    var currElem;
    while (currElem = ppw.editor.selectedElems.pop()) {
        ppw.editor.unSelect(currElem);
        selected.push(currElem)
    }
    return selected.reverse()
};
PwylUtil.prototype.hasSelectedParent = function (elem) {
    if (elem.isSelected) {
        return elem
    } else {
        if (elem.parentNode == ppw.util.top || elem.parentNode == document.body) {
            return false
        } else {
            return ppw.util.hasSelectedParent(elem.parentNode)
        }
    }
};
PwylUtil.prototype.hasParent = function (elem, parents) {
    if (elem == ppw.util.top || elem == document.body) {
        return false
    }
    if (parents.contains(elem)) {
        return true
    } else {
        return ppw.util.hasParent(elem, parents)
    }
};
PwylUtil.prototype.firstSubelement = function (node, args) {
    for (var i = 0; i < args.length; i++) {
        if (node == args[i]) {
            return args[i]
        }
    }
    for (var i = 0; i < node.childNodes.length; i++) {
        var result = ppw.util.firstSubelement(node.childNodes[i], args);
        if (result) {
            return result
        }
    }
    return false
};
PwylUtil.prototype.selectAllSubelementsBetween = function (rootElement, startElement, lastElement, callback, params) {
    var o = new Object();
    o.firstReached = false;
    o.lastReached = false;
    o.first = startElement;
    o.last = lastElement;
    o.callback = callback;
    if (startElement == null) {
        o.firstReached = true
    }
    if (lastElement == null) {
        o.lastReached = false
    }
    o.selectSubelements = function (node) {
        for (var i = 0; i < node.childNodes.length; i++) {
            o.selectSubelements(node.childNodes[i])
        }
        if (!o.firstReached && (o.first != null && node == o.first)) {
            o.firstReached = true
        } else {
            if (o.firstReached && !o.lastReached) {
                if (o.last != null && node == o.last) {
                    o.lastReached = true
                } else {
                    callback(node, params)
                }
            } else {
                if (o.firstReached && o.lastReached) {
                    return
                }
            }
        }
    };
    o.selectSubelements(rootElement)
};
PwylUtil.prototype.getFirstCommonParent = function (first, second) {
    var getParentsArray = function (node) {
        var parents = [];
        while (node != ppw.util.top) {
            node = node.parentNode;
            parents.push(node)
        }
        return parents
    };
    return getParentsArray(first).firstMatch(getParentsArray(second))
};
PwylUtil.prototype.getElementContainingNode = function (elements, node) {
    for (var i = 0; i < elements.length; i++) {
        if (node == elements[i]) {
            return elements[i]
        }
    }
    if (node.parentNode == ppw.util.top) {
        return null
    }
    return ppw.util.getElementContainingNode(elements, node.parentNode)
};
PwylUtil.prototype.executeOnSubelements = function (node, callback, params) {
    for (var i = 0; i < node.childNodes.length; i++) {
        ppw.util.executeOnSubelements(node.childNodes[i], callback, params)
    }
    callback(node, params)
};
PwylUtil.prototype.orderPageElements = function (elems) {
    var initArgs = {
        elems: elems,
        sortedElems: []
    };
    var checkElem = function (e, args) {
        if (args.elems.contains(e)) {
            args.sortedElems.push(e)
        }
    };
    ppw.util.depthFirstRecursion(ppw.util.pageTop, checkElem, initArgs);
    return initArgs.sortedElems
};
PwylUtil.prototype.getPageTitle = function () {
    return document.title.replace("PrintWhatYouLike on ", "")
};
PwylUtil.prototype.getHtmlClip = function (orig_elem) {
    var elem = ppw.util.inlineCss(orig_elem);
    elem = ppw.commands.cleanElement(elem);
    elem.style.backgroundColor = ppw.util.getBackgroundColor(orig_elem);
    ppw.util.makeRelativeUrlsAbsolute(elem);
    return ppw.util.getOuterHtml(elem)
};
PwylUtil.prototype.inlineCss = function (elem) {
    var clone = elem.cloneNode(true);
    var cloneElemsToRemove = [];
    var includeDimensions = elem.offsetWidth < 500;
    var callback = function (e, cloneE) {
        if (YAHOO.util.Dom.getStyle(e, "display") == "none" || YAHOO.util.Dom.getStyle(e, "visible") == "hidden") {
            cloneElemsToRemove.push(cloneE);
            return false
        } else {
            ppw.cssutil.copyStyleAttributes(cloneE, e, false, includeDimensions);
            return true
        }
    };
    ppw.util.topDownDepthFirstRecursion(elem, callback, clone);
    for (var i = 0; i < cloneElemsToRemove.length; i++) {
        cloneElemsToRemove[i].parentNode.removeChild(cloneElemsToRemove[i])
    }
    return clone
};
PwylUtil.prototype.makeRelativeUrlsAbsolute = function (elem) {
    var imgs = elem.getElementsByTagName("img");
    for (var i = 0; i < imgs.length; i++) {
        if (imgs[i].src != imgs[i].getAttribute("src")) {
            imgs[i].setAttribute("src", imgs[i].src)
        }
    }
};
PwylUtil.prototype.getOuterHtml = function (elem) {
    var div = document.createElement("div");
    div.appendChild(elem);
    return div.innerHTML
};
PwylUtil.prototype.noBubble = function (event) {
    event = this.captureEvent(event);
    if (event) {
        event.cancelBubble = true;
        if (event.stopPropagation) {
            event.stopPropagation()
        }
    }
    return event
};
PwylUtil.prototype.captureEvent = function (event) {
    if (!event) {
        event = window.event
    }
    return event
};
PwylUtil.prototype.cursorWrapper = function (func) {
    ppw.editor.changeCursorStyle("progress");
    if (typeof (func) == "string") {
        setTimeout(func, 0)
    } else {
        func()
    }
};
PwylUtil.prototype.getStyle = function (elem, style) {
    var st = YAHOO.util.Dom.getStyle(elem, style);
    if (st) {
        st = st.replace("px", "")
    }
    return st
};
PwylUtil.prototype.getInheritedProperty = function (elem, property, defaultValues, ifNotFound) {
    var value;
    var valueMatchesDefaults = function () {
        if (!value) {
            return true
        }
        for (var i = 0; i < defaultValues.length; i++) {
            if (value == defaultValues[i]) {
                return true
            }
        }
        return false
    };
    while (elem != document) {
        value = ppw.util.getStyle(elem, property);
        if (!valueMatchesDefaults()) {
            return value
        }
        elem = elem.parentNode
    }
    return ifNotFound
};
PwylUtil.prototype.getBackgroundColor = function (elem) {
    return ppw.util.getInheritedProperty(elem, "backgroundColor", ["transparent", "rgba(0, 0, 0, 0)"], "white")
};
PwylUtil.prototype.sleep = function (milliseconds) {
    var start = new Date().getTime();
    for (var i = 0; i < 10000000; i++) {
        if ((new Date().getTime() - start) > milliseconds) {
            break
        }
    }
};
PwylUtil.prototype.getDocEncoding = function (doc) {
    doc = doc ? doc : document;
    if (doc.inputEncoding) {
        return doc.inputEncoding
    }
    if (doc.characterSet) {
        return doc.characterSet
    }
    if (doc.charset) {
        return doc.charset
    }
    if (doc.defaultCharset) {
        return doc.defaultCharset
    }
    return null
};
PwylUtil.prototype.getRelevantDocEncoding = function (doc) {
    var parentEncoding, encoding = null;
    if (ppw.bookmarklet) {
        parentEncoding = ppw.util.getDocEncoding();
        if (parentEncoding && parentEncoding.toLowerCase() != "utf-8") {
            encoding = parentEncoding
        }
    }
    return encoding
};
PwylUtil.prototype.onProEditPage = function () {
    return document.getElementById("ppw_pro_toolbar_content") != null
};