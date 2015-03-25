function PwylAuto() {
    this.badTags = {
        form: 1,
        object: 1,
        embed: 1,
        iframe: 1
    };
    this.ignoreTags = {
        h1: 1,
        br: 1
    }
}
PwylAuto.prototype.scrub = function (currCommand, args) {
    args.content = ppw.auto.getTitles();
    var mainContent = ppw.auto.getMainContentBlock();
    if (mainContent) {
        args.content.push(mainContent)
    }
    ppw.commands.isolateElems(args.content);
    args.cruft = ppw.auto.getCruft(mainContent);
    for (var i = 0; i < args.cruft.length; i++) {
        ppw.util.applyProperty(args.cruft[i], "display", "none")
    }
    ppw.auto.cleanAfterScrub();
    ppw.commands._doRemoveBackground();
    if (!ppw.print_button) {
        ppw.ui.enableOrDisableButton("ppw_auto_link", false)
    }
};
PwylAuto.prototype.unscrub = function (currCommand, args) {
    ppw.commands._undoRemoveBackground();
    for (var i = 0; i < args.cruft.length; i++) {
        ppw.util.undoApplyProperty(args.cruft[i], "display")
    }
    ppw.commands.unIsolateElems(args.content);
    if (!ppw.print_button) {
        ppw.ui.enableOrDisableButton("ppw_auto_link", true)
    }
};
PwylAuto.prototype.getMainContentBlock = function () {
    var allElemScores = [],
        tmpPs = [],
        i, currP, mainElem = null;
    ppw.auto.depthFirstRecursion(ppw.util.pageTop, function (elem) {
        elem.score = ppw.auto.scoreElement(elem);
        if (elem.score) {
            elem.setAttribute("cscore", elem.score.compositeScore);
            if (elem.score.compositeScore > 1 && !elem.score.isInline) {
                allElemScores.push(elem.score)
            }
        }
    });
    allElemScores.sort(function (a, b) {
        return b.compositeScore - a.compositeScore
    });
    for (i = 0; i < allElemScores.length; i++) {
        currP = allElemScores[i].elem.parentNode;
        if (!currP.subsScore) {
            currP.subsScore = 0;
            tmpPs.push(currP)
        }
        currP.subsScore++;
        if (currP.subsScore >= 3) {
            mainElem = currP;
            break
        }
    }
    for (i = 0; i < tmpPs.length; i++) {
        tmpPs[i].subsScore = null
    }
    if (!mainElem && allElemScores.length > 0) {
        mainElem = allElemScores[0].elem
    }
    return mainElem
};
PwylAuto.prototype.getCruft = function (elem) {
    var e, cruft = [];
    for (var i = 0; i < elem.childNodes.length; i++) {
        e = elem.childNodes[i];
        if (e.nodeType != 1 || ppw.auto.ignoreTags[e.tagName.toLowerCase()] == 1) {
            continue
        }
        if (e.getAttribute("cscore") < 1 || ppw.auto.badTags[e.tagName.toLowerCase()] == 1) {
            cruft.push(e)
        }
    }
    return cruft
};
PwylAuto.prototype.cleanAfterScrub = function (e) {
    ppw.auto.depthFirstRecursion(ppw.util.pageTop, function (elem) {
        if (elem.score) {
            elem.score = null
        }
        if (elem.getAttribute("cscore")) {
            elem.removeAttribute("cscore")
        }
    })
};
PwylAuto.prototype.scoreElement = function (elem) {
    var score = {
        visibleChars: 0,
        invisibleChars: 0,
        numChildren: 0,
        compositeScore: 0,
        elem: elem,
        isInline: false
    };
    var e, count, hasTextNodes = false,
        elemStyle = YAHOO.util.Dom.getStyle(elem, "display");
    if (elemStyle == "none") {
        return null
    }
    for (var i = 0; i < elem.childNodes.length; i++) {
        e = elem.childNodes[i];
        if (ppw.util.isNonEmptyText(e)) {
            score.visibleChars += e.nodeValue.strip().length;
            hasTextNodes = true
        } else {
            if (e.nodeType == 1 && e.score && !e.score.isInline) {
                score.visibleChars += e.score.visibleChars;
                score.invisibleChars += e.score.invisibleChars;
                score.numChildren += e.score.numChildren + 1
            }
        }
    }
    score.invisibleChars += ppw.auto.getOuterHtmlTagLength(elem);
    if (hasTextNodes) {
        score.numChildren++
    }
    if (ppw.auto.isInline(elem, elemStyle)) {
        score.invisibleChars = 0;
        score.isInline = true
    }
    score.compositeScore = score.invisibleChars == 0 ? score.visibleChars : (score.visibleChars / score.invisibleChars);
    return score
};
PwylAuto.prototype.getTitles = function () {
    return ppw.util.toArray(ppw.util.top.getElementsByTagName("h1"))
};
PwylAuto.prototype.depthFirstRecursion = function (rootNode, callback, notFirst) {
    for (var i = 0; i < rootNode.childNodes.length; i++) {
        if (rootNode.childNodes[i].nodeType == 1 && rootNode.childNodes[i].tagName.toUpperCase() != "IFRAME") {
            ppw.auto.depthFirstRecursion(rootNode.childNodes[i], callback, true)
        }
    }
    if (notFirst) {
        callback(rootNode)
    }
};
PwylAuto.prototype.checkAdjacent = function (elem, forward) {
    var next = forward ? elem.nextSibling : elem.previousSibling;
    var trimmed;
    while (next != null) {
        if (next.nodeType != 3) {
            return false
        }
        trimmed = next.nodeValue.strip();
        if (trimmed.length > 0) {
            var touchingChar = forward ? trimmed.slice(0, 1) : trimmed.slice(trimmed.length - 1, trimmed.length);
            return touchingChar.match(/[\w\!\?"'()\-\.,\:]/) != null
        }
        next = forward ? next.nextSibling : next.previousSibling
    }
    return false
};
PwylAuto.prototype.isInline = function (elem, elemStyle) {
    elemStyle = elemStyle || YAHOO.util.Dom.getStyle(elem, "display");
    if (elem.nodeType != 1 || elemStyle != "inline") {
        return false
    }
    return ppw.auto.checkAdjacent(elem, false) || ppw.auto.checkAdjacent(elem, true)
};
PwylAuto.prototype.getElemName = function (elem) {
    return elem.tagName + "." + elem.id + "." + elem.className
};
PwylAuto.prototype.getOuterHtmlTagLength = function (elem) {
    var len = 0;
    var attrs = elem.attributes;
    for (var i = 0; i < attrs.length; i++) {
        if (attrs[i].specified) {
            len += attrs[i].name.length + attrs[i].value.length + 2
        }
    }
    len += ((elem.tagName.length + 2) * 2) + 1;
    return len
};

