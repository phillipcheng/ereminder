function PwylLoader() {
    this.addPage = false
}
PwylLoader.prototype.requestInPageIFrame = function (html, attachPoint) {
    var ifr = document.createElement("iframe");
    ifr.style.width = "1px";
    ifr.style.height = "1px";
    ifr.id = "id" + new Date().getMilliseconds();
    if (!attachPoint) {
        attachPoint = document.getElementById("ppw_iframe_dock")
    }
    attachPoint.appendChild(ifr);
    var ifrObj = ppw.loader.getIFrame(ifr);
    ifrObj.doc.open();
    ifrObj.doc.write(html);
    ifrObj.doc.close();
    return ppw.loader.getIFrame(ifr.id)
};
PwylLoader.prototype.requestBlankInPageIFrame = function (attachPoint, encoding) {
    encoding = encoding ? encoding : "UTF-8";
    var html = '<html><head><meta http-equiv="Content-Type" content="text/html;charset=' + encoding + '"/><base target="_blank" href="' + ppw.props.pageBaseUrl + '"/></head><body></body></html>';
    return ppw.loader.requestInPageIFrame(html, attachPoint)
};
PwylLoader.prototype.requestIFrame = function (requestedUrl, onLoadCallback, useProxy, pageBodyId, applyChanges) {
    var ifr;
    var ifrId = "id" + new Date().getMilliseconds();
    var ifrspan = document.createElement("span");
    ifrspan.innerHTML = '<iframe id="' + ifrId + '" onload="ppw.loader.loadIFrame(\'' + ifrId + "','" + pageBodyId + "'," + applyChanges + ')" onloadcallback="' + onLoadCallback + '"></iframe>';
    ifr = ifrspan.childNodes[0];
    ifr.destUrl = requestedUrl;
    var ifrSrc = useProxy ? (ppw.props.home + "get_page?url=" + new ppw.server.URLManager(requestedUrl).encodedUrl) : requestedUrl;
    if (ifrSrc == document.URL) {
        ifrSrc = ifrSrc + ((ifrSrc.indexOf("?") == -1) ? "?" : "&") + "uid=" + ifrId
    }
    ifr.src = ifrSrc;
    document.getElementById("ppw_iframe_dock").appendChild(ifr);
    return false
};
PwylLoader.prototype.loadIFrame = function (ifrId, pageBodyId, applyChanges) {
    var ifr = document.getElementById(ifrId);
    var callback = ppw.util.getAttribute(ifr, "onloadcallback");
    if (callback) {
        var vars = callback.split(".");
        window[vars[0]][vars[1]][vars[2]](ppw.loader.getIFrame(ifr), pageBodyId, applyChanges)
    }
};
PwylLoader.prototype.getIFrame = function (ifrOrId) {
    var ifrObj = new Object();
    var ifr = ppw.paths.get(ifrOrId);
    ifrObj.iframe = ifr;
    ifrObj.destUrl = ifr.destUrl;
    if (ifr.contentDocument) {
        ifrObj.win = ifr.contentWindow;
        ifrObj.doc = ifr.contentDocument
    } else {
        if (ifr.contentWindow) {
            ifrObj.win = ifr.contentWindow;
            ifrObj.doc = ifrObj.win.document
        }
    }
    try {
        if (ifrObj.doc.getElementsByTagName("head")) {
            ifrObj.head = ifrObj.doc.getElementsByTagName("head")[0]
        }
        if (ifrObj.doc.getElementsByTagName("body")) {
            ifrObj.body = ifrObj.doc.getElementsByTagName("body")[0]
        }
    } catch (ex) {}
    return ifrObj
};
PwylLoader.prototype.requestPageUsingJS = function (url, callback, pageBodyId, applyChanges) {
    var params = {
        url: new ppw.server.URLManager(url).encodedUrl,
        js: "true",
        pageBodyId: pageBodyId,
        applyChanges: applyChanges
    };
    ppw.server.getViaJs("get_page", params, callback)
};
PwylLoader.prototype.copyPage = function (destHead, destBody, srcHead, srcBody, options) {
    ppw.loader._copyOptions = (options) ? options : {};
    try {
        if (!ppw.loader.canHandleHeaderInnerHtml()) {
            srcHead = ppw.loader.unbreakIEHead(srcHead, destHead.ownerDocument);
            srcBody = ppw.loader.unbreakIEBody(srcBody, destBody.ownerDocument);
            if (ppw.loader._copyOptions.cloneElements) {
                ppw.loader._copyOptions.cloneElements = false
            }
        }
        ppw.loader.copyPageElements(destHead, srcHead, ppw.loader.getProperHeaderElement, ppw.loader._copyOptions.customHeadAttributes);
        ppw.loader.copyPageElements(destBody, srcBody, ppw.loader.getProperElement);
        if (ppw.loader._copyOptions.applyTemplate) {
            ppw.loader.applyCurrentChangesToNewPage(destBody)
        }
        ppw.loader.copyAttributes(destBody, srcBody)
    } catch (ex) {
        if (window.console) {
            console.error(ex.message)
        }
    }
};
PwylLoader.prototype.copyPageElements = function (dest, src, filter, customAttributes) {
    var i = 0;
    var firstToCopy = 0;
    var removeJs = function (elem) {
        var js = elem.getElementsByTagName("script");
        while (js.length > 0) {
            ppw.util.removeElement(js[0])
        }
    };
    var getNextElem = function () {
        if (ppw.loader._copyOptions.cloneElements) {
            if (i < src.childNodes.length) {
                if (ppw.loader._copyOptions.copyExcludesAttrib && src.childNodes[i][ppw.loader._copyOptions.copyExcludesAttrib]) {
                    i++;
                    return getNextElem
                }
                var cloned = src.childNodes[i].cloneNode(true);
                if (cloned.nodeType == Node.ELEMENT_NODE && ppw.loader._copyOptions.dontCopyJs) {
                    removeJs(cloned)
                }
                return cloned
            } else {
                return null
            }
        } else {
            if (src.childNodes.length > firstToCopy) {
                if (ppw.loader._copyOptions.copyExcludesAttrib && src.childNodes[firstToCopy][ppw.loader._copyOptions.copyExcludesAttrib]) {
                    firstToCopy++;
                    return getNextElem()
                }
                var elem = src.removeChild(src.childNodes[firstToCopy]);
                if (elem.nodeType == Node.ELEMENT_NODE && ppw.loader._copyOptions.dontCopyJs) {
                    removeJs(elem)
                }
                return elem
            } else {
                return null
            }
        }
    };
    var currElem = getNextElem();
    while (currElem) {
        try {
            currElem = filter(currElem, dest);
            if (currElem) {
                if (customAttributes) {
                    for (var key in customAttributes) {
                        currElem.setAttribute(key, customAttributes[key])
                    }
                }
                dest.appendChild(currElem)
            }
        } catch (ex) {
            if (window.console) {
                console.error("exception: " + ex.message)
            }
        }
        i++;
        currElem = getNextElem()
    }
};
PwylLoader.prototype.getProperElement = function (elem) {
    if (!elem) {
        return null
    }
    var bannedElementIds = {
        _yuiResizeMonitor: 1
    };
    if (elem.nodeType == Node.ELEMENT_NODE) {
        var tag = elem.tagName.toLowerCase();
        if ((ppw.loader._copyOptions.dontCopyJs && tag == "script") || (ppw.loader._copyOptions.dontCopyPwylElements && ppw.util.getAttribute(elem, "pwyl")) || (bannedElementIds[elem.id])) {
            return null
        }
        return elem
    }
    if (elem.nodeType == Node.TEXT_NODE && elem.nodeValue && elem.nodeValue.strip().length > 0) {
        var span = document.createElement("span");
        span.appendChild(elem);
        return span
    }
    return null
};
PwylLoader.prototype.getProperHeaderElement = function (elem, dest) {
    var elem = ppw.loader.getProperElement(elem);
    if (!elem) {
        return null
    }
    if (ppw.loader.isAllowableHeaderElem(elem)) {
        return elem
    }
    return null
};
PwylLoader.prototype.isAllowableHeaderElem = function (elem) {
    if (!elem.tagName) {
        return false
    }
    var tag = elem.tagName.toLowerCase();
    return tag == "script" || tag == "style" || tag == "title" || (tag == "link" && elem.rel && elem.rel.toLowerCase() == "stylesheet")
};
PwylLoader.prototype.copyAttributes = function (dest, src) {
    var attrs = src.attributes;
    for (var i = 0; i < attrs.length; i++) {
        if (attrs[i].specified) {
            if (attrs[i].name.toLowerCase() == "id" && ppw.loader._copyOptions.dontCopyIdAttribute) {
                continue
            }
            dest.setAttribute(attrs[i].name, attrs[i].value)
        }
    }
};
PwylLoader.prototype.unbreakIEHead = function (head, destDoc) {
    var newHead = destDoc.createElement("span");
    for (var i = 0; i < head.childNodes.length; i++) {
        var srcElem = head.childNodes[i];
        if (srcElem.nodeType == Node.ELEMENT_NODE) {
            var srcTagName = srcElem.tagName.toLowerCase();
            if (srcTagName == "script" && ppw.loader._copyOptions.dontCopyJs) {
                continue
            }
            var destElem = destDoc.createElement(srcTagName);
            ppw.loader.copyAttributes(destElem, srcElem);
            if (srcElem.innerHTML) {
                var currHtml = ppw.loader.removeCDataTags(ppw.loader.removeAtRules(srcElem.innerHTML));
                if (srcTagName == "style") {
                    if (!ppw.util.getAttribute(destElem, "type")) {
                        destElem.setAttribute("type", "text/css")
                    }
                    destElem.styleSheet.cssText = currHtml
                } else {
                    if (srcTagName == "script") {
                        destElem.text = currHtml
                    } else {
                        destElem.innerHTML = currHtml
                    }
                }
            }
            newHead.appendChild(destElem)
        } else {
            if (srcElem.nodeType == Node.TEXT_NODE) {
                newHead.appendChild(destDoc.createTextNode(srcElem.nodeValue))
            }
        }
    }
    return newHead
};
PwylLoader.prototype.unbreakIEBody = function (srcBody, destDoc) {
    var tmpBody = destDoc.createElement("span");
    tmpBody.innerHTML = ppw.loader.removeCDataTags(ppw.loader.removeAtRules(srcBody.innerHTML));
    ppw.loader.copyAttributes(tmpBody, srcBody);
    return tmpBody
};
PwylLoader.prototype.removeCDataTags = function (str) {
    return str.replace("<![CDATA[", "").replace("]]>", "")
};
PwylLoader.prototype.removeAtRules = function (str) {
    return str.replace(/@\w+\s+\{[^\}]*\}/g, "").replace(/@\w+\s+[\s\S]+?;/g, "")
};
PwylLoader.prototype.canHandleHeaderInnerHtml = function () {
    var span = document.createElement("span");
    span.innerHTML = "<meta />";
    return span.childNodes.length > 0
};
PwylLoader.prototype.applyCurrentChangesToNewPage = function (elemToModify) {
    var multiCommand = ppw.changeset.buildChangeSetMultiCommand("Add Page: Apply Changes", ppw.changeset.getExistingCommandsToApplyToAddedPage(), true);
    ppw.changeset.executeInNewPageContext(elemToModify, function () {
        ppw.changeset.doAction(multiCommand)
    });
    return elemToModify
};
PwylLoader.prototype.removePwylFrame = function (doc) {
    var destBody = doc.body;
    var destTop = doc.getElementById(ppw.util.topId);
    var destPageTop = doc.getElementById(ppw.util.pageTopId);
    var destToolbar = doc.getElementById(ppw.util.toolbarId);
    while (destPageTop.childNodes.length > 0) {
        destBody.appendChild(destPageTop.childNodes[0])
    }
    ppw.loader.copyCssStyle(destBody, destTop);
    destBody.removeChild(destTop);
    if (destToolbar) {
        destBody.removeChild(destToolbar)
    }
};
PwylLoader.prototype.addSpacerDiv = function (doc) {
    var destBody = doc.body;
    var spacer = doc.createElement("div");
    spacer.id = "ppw_spacer";
    while (destBody.childNodes.length > 0) {
        spacer.appendChild(destBody.childNodes[0])
    }
    destBody.appendChild(spacer)
};
PwylLoader.prototype.copyCssStyle = function (dest, src, removeOld) {
    var styles = ["ppw_clear_bg", "ppw_remove_images", "ppw_remove_margin"];
    for (var i = 0; i < styles.length; i++) {
        if (YAHOO.util.Dom.hasClass(src, styles[i])) {
            YAHOO.util.Dom.addClass(dest, styles[i]);
            if (removeOld) {
                YAHOO.util.Dom.removeClass(src, styles[i])
            }
        }
    }
};

