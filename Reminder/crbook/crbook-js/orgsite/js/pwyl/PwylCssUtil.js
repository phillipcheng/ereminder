
function PwylCssUtil() {
    this.defaultBrowserCssRules;
    this.defaultElements = {};
    this.dontCopyTheseStyles = ["length", "cursor", "position", "top", "bottom", "left", "right", "outline", "outlineWidth"];
    this.dimensionStyles = ["height", "width"];
    this.positionStyles = ["marginTop", "marginRight", "marginBottom", "marginLeft", "overflow"];
    this.defaultIfr = null;
    this.removedCssRules = []
}
PwylCssUtil.prototype.copyStyleAttributes = function (dest, src, includeBrowserDefaults, includeDimensions, includePositions) {
    var styles = ppw.cssutil.getStyleAttributes(src, includeBrowserDefaults, includeDimensions, includePositions);
    ppw.cssutil.applyStyleAttributesToElement(dest, styles)
};
PwylCssUtil.prototype.applyStyleAttributesToElement = function (dest, styles) {
    var key, val;
    for (key in styles) {
        val = styles[key];
        if (typeof (val) == "number") {
            val = val + ""
        }
        if (val && typeof (val) == "string") {
            if (val.charAt(0) == '"' || val.charAt(0) == "'") {
                val = val.replace(/^["'](.*?)["']$/g, "$1")
            }
            YAHOO.util.Dom.setStyle(dest, key, val)
        }
    }
};
PwylCssUtil.prototype.getStyleAttributes = function (src, includeBrowserDefaults, includeDimensions, includePositions) {
    var dest = {}, cs;
    if (document.defaultView) {
        cs = document.defaultView.getComputedStyle(src, null);
        ppw.cssutil._copyStylesFromCSSStyleDeclaration(cs, dest)
    }
    if (src.currentStyle) {
        ppw.cssutil._copyStylesFromCSSCurrentStyleDeclaration(src.currentStyle, dest)
    }
    ppw.cssutil._removeBadProps(dest, src, includeBrowserDefaults, includeDimensions, includePositions);
    if (dest.direction != undefined) {
        dest.unicodeBidi = "embed"
    }
    return dest
};
PwylCssUtil.prototype._copyStylesFromCSSStyleDeclaration = function (styleList, dest) {
    if (!styleList) {
        return
    }
    var i = 0,
        key, val;
    while (i < styleList.length) {
        try {
            key = styleList.item(i);
            val = styleList.getPropertyValue(key);
            if (val && (typeof (val) == "string" || typeof (val) == "number")) {
                dest[key] = val
            }
        } catch (ex) {}
        i++
    }
};
PwylCssUtil.prototype._copyStylesFromCSSCurrentStyleDeclaration = function (styleList, dest) {
    if (!styleList) {
        return
    }
    var i = 0,
        key, val;
    for (key in styleList) {
        try {
            val = styleList[key];
            if (val && (typeof (val) == "string" || typeof (val) == "number")) {
                dest[key] = val
            }
        } catch (ex) {}
    }
};
PwylCssUtil.prototype._removeBadProps = function (dest, src, includeBrowserDefaults, includeDimensions, includePositions) {
    var removeValues = function (ar) {
        for (var i = 0; i < ar.length; i++) {
            var key = ar[i];
            if (key in dest) {
                delete dest[key]
            }
        }
    };
    removeValues(ppw.cssutil.dontCopyTheseStyles);
    if (!includeDimensions) {
        removeValues(ppw.cssutil.dimensionStyles)
    }
    if (!includePositions) {
        removeValues(ppw.cssutil.positionStyles)
    }
    if (!includeBrowserDefaults) {
        try {
            for (var j in dest) {
                if (ppw.cssutil.isDefaultBrowserValue(src, j, dest[j])) {
                    delete dest[j]
                }
            }
        } catch (ex) {}
    }
};
PwylCssUtil.prototype._isCopyProp = function (key) {
    return ppw.cssutil.dontCopyTheseStyles[key] != 1
};
PwylCssUtil.prototype.isDefaultBrowserValue = function (elem, cssName, cssValue) {
    if (typeof (cssValue) != "string" && typeof (cssValue) != "number") {
        return false
    }
    if (!ppw.cssutil.defaultIfr) {
        ppw.cssutil.defaultIfr = ppw.loader.requestBlankInPageIFrame(ppw.util.toolbar)
    }
    var defaultDoc = ppw.cssutil.defaultIfr.doc;
    var currDefault = ppw.cssutil.defaultElements[elem.tagName];
    if (!currDefault) {
        var tmpElem = defaultDoc.createElement(elem.tagName);
        try {
            tmpElem.appendChild(defaultDoc.createTextNode("hi!"))
        } catch (ex) {}
        defaultDoc.body.appendChild(tmpElem);
        ppw.cssutil.defaultElements[elem.tagName] = tmpElem;
        currDefault = tmpElem
    }
    var defaultValue = YAHOO.util.Dom.getStyle(currDefault, cssName);
    return ppw.cssutil._isUndefined(defaultValue) || defaultValue == cssValue
};
PwylCssUtil.prototype._isUndefined = function (value) {
    return (value + "") == "undefined"
};
PwylCssUtil.prototype.positionProperties = {
    position: "static",
    top: "0px",
    bottom: "0px",
    left: "0px",
    right: "0px",
    marginLeft: "0px",
    marginRight: "0px",
    marginTop: "0px",
    marginBottom: "0px",
    overflow: "visible"
};
PwylCssUtil.prototype.removePositionProperties = function (elem) {
    var value, i, good_value;
    try {
        for (i in ppw.cssutil.positionProperties) {
            value = YAHOO.util.Dom.getStyle(elem, i);
            good_value = ppw.cssutil.positionProperties[i];
            if (value && value != good_value && !ppw.cssutil.isDefaultBrowserValue(elem, i, value)) {
                ppw.util.applyProperty(elem, i, good_value, false, true)
            }
        }
    } catch (ex) {}
};
PwylCssUtil.prototype.undoRemovePositionProperties = function (elem) {
    for (var i in ppw.cssutil.positionProperties) {
        ppw.util.undoApplyProperty(elem, i)
    }
};
PwylCssUtil.prototype.removeCssHoverRules = function () {
    var i, currStyleSheet, currRules, j, cssOwner;
    var isHoverRule = function (selector) {
        var results;
        if (selector.search(/\:hover/i) > -1) {
            results = selector.match(/,/g);
            var numSelectors = (results ? results.length : 0) + 1;
            results = selector.match(/\:/g);
            var numPseudoClasses = results ? results.length : 0;
            if (numSelectors == numPseudoClasses) {
                return true
            }
        }
        return false
    };
    for (i = 0; i < document.styleSheets.length; i++) {
        try {
            currStyleSheet = document.styleSheets[i];
            cssOwner = currStyleSheet.ownerNode ? currStyleSheet.ownerNode : currStyleSheet.owningElement;
            if (!cssOwner.getAttribute("pwyl") && (!currStyleSheet.href || !currStyleSheet.href.startsWith("http") || ppw.server.pageUrl.hostName == new ppw.server.URLManager(currStyleSheet.href).hostName) && (!cssOwner.media || cssOwner.media.toLowerCase() == "screen" || cssOwner.media.toLowerCase() == "all")) {
                currRules = currStyleSheet.cssRules ? currStyleSheet.cssRules : currStyleSheet.rules;
                for (j = 0; j < currRules.length; j++) {
                    if (isHoverRule(currRules[j].selectorText)) {
                        if (currStyleSheet.deleteRule) {
                            ppw.cssutil.removedCssRules.push({
                                cssIndex: i,
                                index: j,
                                cssText: currRules[j].cssText
                            });
                            currStyleSheet.deleteRule(j)
                        } else {
                            var cssText = currRules[j].style.cssText ? currRules[j].style.cssText : "";
                            ppw.cssutil.removedCssRules.push({
                                cssIndex: i,
                                selector: currRules[j].selectorText,
                                style: cssText
                            });
                            currStyleSheet.removeRule(j)
                        }
                    }
                }
            }
        } catch (ex) {}
    }
};
PwylCssUtil.prototype.restoreCssHoverRules = function () {
    var currRule, currCss;
    while (ppw.cssutil.removedCssRules.length > 0) {
        currRule = ppw.cssutil.removedCssRules.pop();
        currCss = document.styleSheets[currRule.cssIndex];
        try {
            if (currCss.insertRule) {
                currCss.insertRule(currRule.cssText, currRule.index)
            } else {
                if (currRule.selector && currRule.style) {
                    currCss.addRule(currRule.selector, currRule.style)
                }
            }
        } catch (ex) {}
    }
};
