function PwylPaths() {
    this.numPages = 1;
    this.overrideBase = null
}
PwylPaths.prototype.getPath = function (elem, alt) {
    return YAHOO.lang.JSON.stringify(ppw.paths.getPathArray(elem))
};
PwylPaths.prototype.getPathArray = function (elem, alt) {
    var fragments = [];
    var currElem, currFrag;
    var base = ppw.paths.getBase(elem);
    currElem = elem;
    while (currElem != base) {
        currFrag = ppw.paths._getPathFragment(currElem, alt);
        fragments.push(currFrag);
        if (ppw.paths._isAbsoluteFragment(currFrag)) {
            break
        }
        currElem = currElem.parentNode;
        alt = false
    }
    fragments.push(ppw.paths.getBasePathFragment(base));
    fragments.reverse();
    return fragments
};
PwylPaths.prototype._getPathFragment = function (elem, alt) {
    var frag = {};
    var par, i, currChild, currTagCount, isUnique = false;
    frag.tag = elem.tagName.toLowerCase();
    if (elem.id && ppw.paths._isIdValid(elem) && alt != true) {
        frag.id = elem.id;
        if (!isUnique) {
            frag.alt = ppw.paths.getPathArray(elem, true)
        }
        isUnique = true
    }
    if (!isUnique) {
        par = elem.parentNode, currTagCount = 0;
        for (i = 0; i < par.childNodes.length; i++) {
            currChild = par.childNodes[i];
            if (currChild == elem) {
                frag.child = currTagCount;
                break
            } else {
                if (currChild.tagName == elem.tagName) {
                    currTagCount++
                }
            }
        }
    }
    return frag
};
PwylPaths.prototype._isAbsoluteFragment = function (fragment) {
    return ("id" in fragment)
};
PwylPaths.prototype._isIdValid = function (elem) {
    var elemById = document.getElementById(elem.id);
    return (elemById == elem)
};
PwylPaths.prototype.getBasePathFragment = function (base) {
    return {
        tag: base.tagName.toLowerCase(),
        id: base.id
    }
};
PwylPaths.prototype.getElem = function (path) {
    var fragments = YAHOO.lang.JSON.parse(path);
    return ppw.paths.getElemArray(fragments)
};
PwylPaths.prototype.getElemArray = function (fragments) {
    if (fragments.length == 0) {
        return null
    }
    var parentElem = ppw.paths._getBaseElemFromFragment(fragments[0]);
    for (var i = 1; i < fragments.length; i++) {
        if (parentElem == null) {
            return parentElem
        }
        parentElem = ppw.paths._getElemFromFragment(fragments[i], parentElem)
    }
    return parentElem
};
PwylPaths.prototype._getElemFromFragment = function (fragment, root) {
    if (fragment.id) {
        var tmpE = ppw.paths.getElementById(fragment.id, fragment.tag, root);
        if (tmpE == null && fragment.alt) {
            tmpE = ppw.paths.getElemArray(fragment.alt)
        }
        if (tmpE == null && fragment.id.toLowerCase().startsWith("post-")) {
            tmpE = ppw.paths.getPostId(fragment.id, fragment.tag, root)
        }
        return tmpE
    }
    var tagCount = 0,
        currChild;
    for (var i = 0; i < root.childNodes.length; i++) {
        currChild = root.childNodes[i];
        if (currChild.nodeType == Node.ELEMENT_NODE && currChild.tagName.toLowerCase() == fragment.tag.toLowerCase()) {
            if (tagCount == parseInt(fragment.child, 10)) {
                return currChild
            } else {
                tagCount++
            }
        }
    }
    return null
};
PwylPaths.prototype._getBaseElemFromFragment = function (fragment) {
    if (ppw.paths.overrideBase) {
        return ppw.paths.overrideBase
    } else {
        return document.getElementById(fragment.id)
    }
};
PwylPaths.prototype.getElementById = function (id, tag, root) {
    var elem = document.getElementById(id);
    if (elem && ppw.util.isParent(elem, root)) {
        return elem
    }
    var elems = root.getElementsByTagName(tag.toLowerCase());
    for (var i = 0; i < elems.length; i++) {
        var curr = elems[i];
        if (elems[i].id == id) {
            return elems[i]
        }
    }
    return null
};
PwylPaths.prototype.getPostId = function (id, tag, root) {
    var isPost = function (elem) {
        return (elem.id && elem.id.match(/post-\d+/gi) != null)
    };
    return YAHOO.util.Dom.getElementBy(isPost, tag, root)
};
PwylPaths.prototype.get = function (idOrElem) {
    if (typeof (idOrElem) == "string") {
        return document.getElementById(idOrElem)
    }
    return idOrElem
};
PwylPaths.prototype.getNewPage = function (baseId) {
    var newBase = document.createElement("div");
    newBase.id = (baseId ? baseId : ppw.paths.getNewPageId());
    newBase.className = "ppw_base";
    return newBase
};
PwylPaths.prototype.getNewPageId = function () {
    ppw.paths.numPages++;
    return "ppw_page_" + ppw.paths.numPages
};
PwylPaths.prototype.getBase = function (elem) {
    if (ppw.paths.overrideBase) {
        return ppw.paths.overrideBase
    }
    while (elem != ppw.util.top) {
        if (YAHOO.util.Dom.hasClass(elem, "ppw_base")) {
            return elem
        } else {
            elem = elem.parentNode
        }
    }
    return ppw.util.pageTop
};
PwylPaths.prototype.countIsolatedElems = function (base) {
    var count = 0;
    for (var i = 0; i < base.childNodes.length; i++) {
        if (base.childNodes[i].nodeType == Node.ELEMENT_NODE && YAHOO.util.Dom.hasClass(base.childNodes[i], "ppw_isolate")) {
            count++
        }
    }
    return count
};