function PwylEditor() {
    this.selectedElems = [];
    this.isCntrlKeyPressed = false;
    this.enableKeyboardShortcuts = true;
    this.isCoverBoxOpen = false;
    this.hoverElementsActive = true;
    this.undisplayableElements = {
        map: 1,
        area: 1,
        tbody: 1
    };
    this.unmodifyableElements = ["iframe", "embed", "object", "applet", "input", "button", "select", "textarea"];
    this.currHoverElem;
    this.disableSelecting = false
}
PwylEditor.prototype.initializeEditorComponents = function () {
    if (!ppw.bookmarklet) {
        ppw.editor.fixTop()
    }
    ppw.editor.borderTop = document.getElementById("ppw_hover_border_top");
    ppw.editor.borderTop.onclick = ppw.editor.mouseClick;
    ppw.editor.borderBottom = document.getElementById("ppw_hover_border_bottom");
    ppw.editor.borderBottom.onclick = ppw.editor.mouseClick;
    ppw.editor.borderLeft = document.getElementById("ppw_hover_border_left");
    ppw.editor.borderLeft.onclick = ppw.editor.mouseClick;
    ppw.editor.borderRight = document.getElementById("ppw_hover_border_right");
    ppw.editor.borderRight.onclick = ppw.editor.mouseClick
};
PwylEditor.prototype.initializeHandlers = function () {
    ppw.editor.convertTextToElements();
    ppw.editor.clearBadAttributes(ppw.util.pageTop);
    ppw.editor.initializeHandlersOnElement(ppw.util.pageTop);
    document.onkeydown = this.keyDown;
    document.onkeyup = this.keyUp;
    ppw.util.toolbarContent.onmouseover = this.clearHover;
    ppw.util.top.onmouseover = this.clearHover
};
PwylEditor.prototype.unInitializeHandlers = function (preventDefaults) {
    document.onkeydown = null;
    document.onkeyup = null;
    ppw.editor.initializeHandlersOnElement(ppw.util.pageTop, true, preventDefaults);
    ppw.editor.clearBadAttributes(ppw.util.pageTop, true)
};
PwylEditor.prototype.convertTextToElements = function () {
    for (var i = 0; i < ppw.util.pageTop.childNodes.length; i++) {
        var child = ppw.util.pageTop.childNodes[i];
        if (child.nodeType == Node.TEXT_NODE) {
            var text = child.nodeValue.strip();
            if (text.length > 0) {
                var span = document.createElement("span");
                span.innerHTML = text;
                ppw.util.pageTop.replaceChild(span, child)
            }
        }
    }
};
PwylEditor.prototype.initializeHandlersOnElement = function (currElem, uninitialize, preventDefaults) {
    var mouseOverFunction = (uninitialize) ? null : this.mouseOver;
    var mouseClickFunction;
    if (uninitialize) {
        mouseClickFunction = (preventDefaults) ? this.mouseClickPreventDefaults : null
    } else {
        mouseClickFunction = this.mouseClick
    }
    if (!currElem) {
        return
    }
    var allElems = ppw.util.getElementAndSubelements(currElem);
    for (var i = 0; i < allElems.length; i++) {
        allElems[i].onmouseover = mouseOverFunction;
        allElems[i].onclick = mouseClickFunction
    }
};
PwylEditor.prototype.clearBadAttributes = function (start, restore) {
    var documentAttributesToClear = ["onkeydown", "onkeyup", "onkeypress", "onclick"];
    var i;
    var modifyAttribute = restore ? ppw.util.restoreAttribute : ppw.util.removeAttribute;
    for (i = 0; i < documentAttributesToClear.length; i++) {
        modifyAttribute(document, documentAttributesToClear[i])
    }
    var allElems = ppw.util.getElementAndSubelements(start);
    for (i = 0; i < allElems.length; i++) {
        modifyAttribute(allElems[i], "onmouseover");
        modifyAttribute(allElems[i], "onmouseout");
        modifyAttribute(allElems[i], "onmousedown");
        modifyAttribute(allElems[i], "onmouseup");
        modifyAttribute(allElems[i], "onclick")
    }
};
PwylEditor.prototype.fixTop = function () {
    var i, e;
    if (!ppw.editor.isSane()) {
        if (ppw.util.toolbar.parentNode != document.body) {
            document.body.appendChild(ppw.util.toolbar)
        }
        if (ppw.util.top.parentNode != document.body) {
            document.body.appendChild(ppw.util.top)
        }
        if (ppw.util.pageTop.parentNode != ppw.util.top) {
            ppw.util.top.appendChild(ppw.util.pageTop)
        }
        var toolbarPosition = null;
        var pageTopFirstChild = ppw.util.pageTop.firstChild;
        for (i = 0; i < document.body.childNodes.length; i++) {
            e = document.body.childNodes[i];
            if (e == ppw.util.toolbar) {
                toolbarPosition = i
            } else {
                if (e != ppw.util.top) {
                    if (toolbarPosition == null || i < toolbarPosition) {
                        ppw.util.pageTop.insertBefore(e, pageTopFirstChild)
                    } else {
                        ppw.util.pageTop.appendChild(e)
                    }
                    i--
                }
            }
        }
        for (i = 0; i < ppw.util.top.childNodes.length; i++) {
            e = ppw.util.top.childNodes[i];
            if (e != ppw.util.pageTop) {
                ppw.util.pageTop.appendChild(e);
                i--
            }
        }
    }
};
PwylEditor.prototype.isSane = function () {
    for (var i = 0; i < document.body.childNodes.length; i++) {
        var e = document.body.childNodes[i];
        if (e.nodeType == Node.ELEMENT_NODE && e != ppw.util.toolbar && e != ppw.util.top) {
            return false
        }
    }
    if (ppw.util.getChildElements(ppw.util.top).length > 1) {
        return false
    }
    return true
};
PwylEditor.prototype.getValidElement = function (elem) {
    if (elem.orig_layer) {
        return ppw.editor.getValidElement(elem.orig_layer)
    }
    if (!ppw.util.isParent(elem, ppw.util.pageTop)) {
        return null
    }
    if (ppw.editor.undisplayableElements[elem.tagName.toLowerCase()] || ppw.editor.hasCoverSpan(elem) || elem.offsetWidth == 0 || elem.offsetHeight == 0) {
        return ppw.editor.getValidElement(elem.parentNode)
    }
    return elem
};
PwylEditor.prototype.inside = function (innerElem, outerElem, innerElemPos, outerElemPos) {
    innerElemPos = innerElemPos ? innerElemPos : YAHOO.util.Dom.getXY(innerElem);
    outerElemPos = outerElemPos ? outerElemPos : YAHOO.util.Dom.getXY(outerElem);
    return outerElemPos[0] <= innerElemPos[0] && ((outerElemPos[0] + outerElem.offsetWidth) >= (innerElemPos[0] + innerElem.offsetWidth)) && outerElemPos[1] <= innerElemPos[1] && ((outerElemPos[1] + outerElem.offsetHeight) >= (innerElemPos[1] + innerElem.offsetHeight))
};
PwylEditor.prototype.outside = function (elemA, elemB, elemAPos, elemBPos) {
    elemAPos = elemAPos ? elemAPos : YAHOO.util.Dom.getXY(elemA);
    elemBPos = elemBPos ? elemBPos : YAHOO.util.Dom.getXY(elemB);
    var narrower, narrowerPos, wider, widerPos, shorter, shorterPos, taller, tallerPos;
    if (elemA.offsetWidth <= elemB.offsetWidth) {
        narrower = elemA, narrowerPos = elemAPos, wider = elemB, widerPos = elemBPos
    } else {
        narrower = elemB, narrowerPos = elemBPos, wider = elemA, widerPos = elemAPos
    }
    if (elemA.offsetHeight <= elemB.offsetHeight) {
        shorter = elemA, shorterPos = elemAPos, taller = elemB, tallerPos = elemBPos
    } else {
        shorter = elemB, shorterPos = elemBPos, taller = elemA, tallerPos = elemAPos
    }
    var a = (!ppw.editor.between(widerPos[0], widerPos[0] + wider.offsetWidth, narrowerPos[0]) && !ppw.editor.between(widerPos[0], widerPos[0] + wider.offsetWidth, narrowerPos[0] + narrower.offsetWidth)) || (!ppw.editor.between(tallerPos[1], tallerPos[1] + taller.offsetHeight, shorterPos[1]) && !ppw.editor.between(tallerPos[1], tallerPos[1] + taller.offsetHeight, shorterPos[1] + shorter.offsetHeight));
    return a
};
PwylEditor.prototype.between = function (a, b, y) {
    var x1, x2;
    if (a <= b) {
        x1 = a, x2 = b
    } else {
        x1 = b, x2 = a
    }
    return x1 <= y && y <= x2
};
PwylEditor.prototype.hasCoverSpan = function (elem) {
    if (ppw.editor.countChildren(elem.parentNode) != 1 || elem == ppw.util.pageTop || elem.parentNode == ppw.util.top) {
        return false
    }
    return ppw.editor.inside(elem, elem.parentNode) && ((elem.parentNode.offsetWidth - elem.offsetWidth) <= 5) && ((elem.parentNode.offsetHeight - elem.offsetHeight) <= 5)
};
PwylEditor.prototype.countChildren = function (p) {
    var count = 0;
    for (var i = 0; i < p.childNodes.length; i++) {
        if (p.childNodes[i].nodeType == Node.ELEMENT_NODE || (p.childNodes[i].nodeType == Node.TEXT_NODE && p.childNodes[i].nodeValue.strip().length > 0)) {
            count++
        }
    }
    return count
};
PwylEditor.prototype.mouseOver = function (event) {
    ppw.util.noBubble(event);
    if (!this.isCoverBox) {
        ppw.editor.clearCoverBoxes()
    }
    ppw.editor.hoverElement(ppw.editor.getValidElement(this))
};
PwylEditor.prototype.hoverElement = function (elem) {
    if (!ppw.editor.hoverElementsActive) {
        return
    }
    if (ppw.editor.currHoverElem == elem) {
        return false
    }
    ppw.editor.currHoverElem = elem;
    ppw.editor.placeHoverBorder(elem)
};
PwylEditor.prototype.placeHoverBorder = function (elem) {
    var pos = YAHOO.util.Dom.getXY(elem);
    pos.x = pos[0];
    pos.y = pos[1];
    ppw.editor.setHoverBorderVisibility(false);
    ppw.util.moveElement(ppw.editor.borderTop, pos.x, (elem.offsetHeight <= 4 ? pos.y - 2 : pos.y));
    ppw.editor.borderTop.style.width = elem.offsetWidth + "px";
    ppw.util.moveElement(ppw.editor.borderBottom, pos.x, (elem.offsetHeight <= 4 ? pos.y : pos.y - 2) + elem.offsetHeight);
    ppw.editor.borderBottom.style.width = elem.offsetWidth + "px";
    ppw.util.moveElement(ppw.editor.borderLeft, (elem.offsetWidth <= 4 ? pos.x - 2 : pos.x), pos.y);
    ppw.editor.borderLeft.style.height = elem.offsetHeight + "px";
    ppw.util.moveElement(ppw.editor.borderRight, (elem.offsetWidth <= 4 ? pos.x : pos.x - 2) + elem.offsetWidth, pos.y);
    ppw.editor.borderRight.style.height = elem.offsetHeight + "px";
    ppw.editor.borderTop.orig_layer = elem;
    ppw.editor.borderBottom.orig_layer = elem;
    ppw.editor.borderLeft.orig_layer = elem;
    ppw.editor.borderRight.orig_layer = elem;
    ppw.editor.setHoverBorderVisibility(true)
};
PwylEditor.prototype.setHoverBorderVisibility = function (visible) {
    var css = visible ? "visible" : "hidden";
    ppw.editor.borderTop.style.visibility = css;
    ppw.editor.borderBottom.style.visibility = css;
    ppw.editor.borderLeft.style.visibility = css;
    ppw.editor.borderRight.style.visibility = css
};
PwylEditor.prototype.clearHover = function (event) {
    ppw.editor.setHoverBorderVisibility(false);
    ppw.editor.currHoverElem = null
};
PwylEditor.prototype.mouseClickPreventDefaults = function (event) {
    event = ppw.util.captureEvent(event);
    ppw.util.noBubble(event);
    return false
};
PwylEditor.prototype.mouseClick = function (event) {
    event = ppw.util.captureEvent(event);
    ppw.util.noBubble(event);
    if (ppw.editor.disableSelecting) {
        ppw.editor.disableSelecting = false;
        return false
    }
    var elem = ppw.editor.getValidElement(this);
    if (elem == null) {
        return false
    }
    if (elem.isSelected) {
        ppw.editor.unSelect(elem, false, event)
    } else {
        if (p = ppw.util.hasSelectedParent(elem)) {
            ppw.editor.unSelect(p, false, event)
        } else {
            ppw.editor.select(elem, event)
        }
    }
    return false
};
PwylEditor.prototype.keyDown = function (event) {
    if (!ppw.editor.enableKeyboardShortcuts) {
        return
    }
    event = ppw.util.captureEvent(event);
    switch (event.keyCode) {
        case 90:
            if (ppw.editor.isCntrlKeyPressed) {
                ppw.changeset.undo()
            } else {
                ppw.commands.startResizeMode()
            }
            break;
        case 89:
            if (ppw.editor.isCntrlKeyPressed) {
                ppw.changeset.redo()
            }//Cheng Yi
            //return false;
            break;
        case 77:
            ppw.editor.expandSelection();
            break;
        case 76:
            ppw.editor.narrowSelection();
            break;
        case 67:
            ppw.util.clearSelectedElements();
            break;
        case 27:
            ppw.util.clearSelectedElements();
            ppw.ui.closeAllPanels();
            break;
        case 46:
            ppw.util.cursorWrapper("ppw.commands.remove()");
            break;
        case 38:
            if (ppw.editor.isCntrlKeyPressed) {
                ppw.util.cursorWrapper("ppw.commands.changeFontSize(1)")
            }
            return false;
            break;
        case 40:
            if (ppw.editor.isCntrlKeyPressed) {
                ppw.util.cursorWrapper("ppw.commands.changeFontSize(-1)")
            }
            return false;
            break;
        case 17:
        case 224:
        case 91:
            ppw.editor.isCntrlKeyPressed = true;
            break
    }
};
PwylEditor.prototype.keyUp = function (event) {
    event = ppw.util.captureEvent(event);
    switch (event.keyCode) {
        case 17:
        case 224:
        case 91:
            ppw.editor.isCntrlKeyPressed = false;
            break
    }
};
PwylEditor.prototype.select = function (elem, event) {
    elem = ppw.editor.getValidElement(elem);
    var unselect = [];
    if (elem == null || elem.isSelected) {
        return
    }
    for (var i = 0; i < ppw.editor.selectedElems.length; i++) {
        var selectedElem = ppw.editor.selectedElems[i];
        if (ppw.util.isParent(selectedElem, elem)) {
            unselect.push(selectedElem)
        }
    }
    for (var i = 0; i < unselect.length; i++) {
        ppw.editor.unSelect(unselect[i], true)
    }
    ppw.editor.placeSelectBox(ppw.editor.generateSelectBox(elem, "ppw_select_box", "select_box"), 0);
    ppw.editor.placeSelectBox(ppw.editor.generateSelectBox(elem, "ppw_select_box_border", "select_box_border"), 1);
    elem.isSelected = true;
    this.selectedElems.push(elem);
    if (event) {
        ppw.editor.positionSelectionCommands(event)
    }
    ppw.editor.setSelectLessButtonVisibility();
    ppw.editor.clearCoverBoxes();
    ppw.init.onSelect()
};
PwylEditor.prototype.unSelect = function (elem, ignoreUnselectEvent, event) {
    YAHOO.util.Dom.removeClass(elem, "ppw_select");
    if (elem.select_box) {
        elem.select_box.parentNode.removeChild(elem.select_box)
    }
    if (elem.select_box_border) {
        elem.select_box_border.parentNode.removeChild(elem.select_box_border)
    }
    elem.isSelected = false;
    this.selectedElems.remove(elem);
    if (this.selectedElems.length > 0) {
        if (event) {
            ppw.editor.positionSelectionCommands(event)
        }
    } else {
        ppw.editor.hideSelectionCommands()
    }
    ppw.editor.setSelectLessButtonVisibility();
    if (YAHOO.env.ua.ie && ppw.editor.currHoverElem) {
        ppw.editor.currHoverElem.onmouseover()
    }
    if (ignoreUnselectEvent === undefined || ignoreUnselectEvent === false) {
        ppw.init.onUnselect()
    }
};
PwylEditor.prototype.generateSelectBox = function (elem, className, attributeName) {
    var div = document.createElement("div");
    div.className = className;
    ppw.util.toolbar.appendChild(div);
    elem[attributeName] = div;
    div.orig_layer = elem;
    div.onmouseover = this.mouseOver;
    div.onclick = this.mouseClick;
    return div
};
PwylEditor.prototype.placeSelectBox = function (box, borderWidth) {
    var elem = box.orig_layer;
    var pos = YAHOO.util.Dom.getXY(elem);
    var w = elem.offsetWidth - (borderWidth * 2);
    w = (w > 0 ? w : 0);
    box.style.width = w + "px";
    var h = elem.offsetHeight - (borderWidth * 2);
    h = (h > 0 ? h : 0);
    box.style.height = h + "px";
    ppw.util.moveElement(box, pos[0], pos[1])
};
PwylEditor.prototype.expandSelection = function () {
    if (ppw.editor.selectedElems.length > 0) {
        var elem = ppw.editor.selectedElems[ppw.editor.selectedElems.length - 1];
        var parent = ppw.editor.getValidElement(elem.parentNode);
        if (parent == null) {
            return
        }
        parent.expand_orig_selection = elem;
        parent.expand_selected_siblings = [];
        for (var i = 0; i < ppw.editor.selectedElems.length; i++) {
            if (ppw.editor.selectedElems[i] != elem && ppw.util.isParent(ppw.editor.selectedElems[i], parent)) {
                parent.expand_selected_siblings.push(ppw.editor.selectedElems[i])
            }
        }
        ppw.editor.clearHover();
        ppw.editor.select(parent, null);
        ppw.editor.showSelectionCommands()
    }
};
PwylEditor.prototype.narrowSelection = function () {
    if (ppw.editor.selectedElems.length > 0) {
        var elem = ppw.editor.selectedElems[ppw.editor.selectedElems.length - 1];
        var origElem = elem.expand_orig_selection;
        if (origElem) {
            ppw.editor.unSelect(elem, true);
            for (var i = 0; elem.expand_selected_siblings && i < elem.expand_selected_siblings.length; i++) {
                ppw.editor.select(elem.expand_selected_siblings[i], null, true)
            }
            ppw.editor.select(origElem, null);
            ppw.editor.showSelectionCommands();
            elem.expand_orig_selection = null;
            elem.expand_selected_siblings = null
        }
    }
};
PwylEditor.prototype.setSelectLessButtonVisibility = function () {
    if (this.selectedElems.length > 0 && this.selectedElems[this.selectedElems.length - 1].expand_orig_selection) {
        var selectLess = document.getElementById("ppw_narrow_link");
        selectLess.style.display = "inline";
        var menu = document.getElementById("ppw_selection_commands");
        var s = ppw.editor.getScrollPosition();
        var left = parseInt(menu.style.left);
        if ((left + menu.offsetWidth) >= (YAHOO.util.Dom.getViewportWidth() + s.x)) {
            left -= selectLess.offsetWidth + 15;
            menu.style.left = left + "px"
        }
    } else {
        document.getElementById("ppw_narrow_link").style.display = "none"
    }
};
PwylEditor.prototype.initializeCoverBoxes = function () {
    ppw.editor.applyOnMouseOverToAllCoverElements(this.coverElement);
    var div = document.getElementById("ppw_cover_div");
    div.isCoverBox = true;
    div.onmouseover = ppw.editor.mouseOver;
    div.onclick = ppw.editor.mouseClick
};
PwylEditor.prototype.applyOnMouseOverToAllCoverElements = function (onMouseOver) {
    var elemsToCover = [];
    for (var i = 0; i < ppw.editor.unmodifyableElements.length; i++) {
        elemsToCover.concatArray(ppw.util.top.getElementsByTagName(ppw.editor.unmodifyableElements[i]))
    }
    for (var i = 0; i < elemsToCover.length; i++) {
        elemsToCover[i].onmouseover = onMouseOver
    }
};
PwylEditor.prototype.coverElement = function (event) {
    ppw.util.noBubble(event);
    var elem = ppw.editor.getValidElement(this);
    ppw.editor.isCoverBoxOpen = true;
    ppw.editor.placeCoverBox(ppw.editor.getCoverBox(elem));
    ppw.editor.hoverElement(elem)
};
PwylEditor.prototype.getCoverBox = function (origElem) {
    var div = document.getElementById("ppw_cover_div");
    div.orig_layer = origElem;
    return div
};
PwylEditor.prototype.placeCoverBox = function (box) {
    var origElem = box.orig_layer;
    var pos = ppw.util.toArray(YAHOO.util.Dom.getXY(origElem));
    box.style.width = origElem.offsetWidth + "px";
    box.style.height = origElem.offsetHeight + "px";
    if (!pos[0]) {
        pos[0] = 0
    }
    if (!pos[1]) {
        pos[1] = 0
    }
    ppw.util.moveElement(box, pos[0], pos[1]);
    box.style.visibility = "visible"
};
PwylEditor.prototype.clearCoverBoxes = function () {
    if (ppw.editor.isCoverBoxOpen) {
        ppw.editor.isCoverBoxOpen = false;
        document.getElementById("ppw_cover_div").style.visibility = "hidden"
    }
};
PwylEditor.prototype.positionSelectionCommands = function (event) {
    this.hideSelectionCommands();
    var offset = 2;
    var m = ppw.editor.getMousePosition(event);
    var s = ppw.editor.getScrollPosition();
    var menu = document.getElementById("ppw_selection_commands");
    var top = m.y + offset,
        left = m.x + offset;
    if ((top + menu.offsetHeight) > (YAHOO.util.Dom.getViewportHeight() + s.y)) {
        top = top - menu.offsetHeight - offset
    }
    if ((left + menu.offsetWidth) > (YAHOO.util.Dom.getViewportWidth() + s.x)) {
        left = YAHOO.util.Dom.getViewportWidth() + s.x - menu.offsetWidth - 2
    }
    if (left < 212) {
        left = 212
    }
    menu.style.left = left + "px";
    menu.style.top = top + "px";
    menu.style.visibility = "visible";
    return false
};
PwylEditor.prototype.hideSelectionCommands = function () {
    document.getElementById("ppw_selection_commands").style.visibility = "hidden"
};
PwylEditor.prototype.showSelectionCommands = function () {
    document.getElementById("ppw_selection_commands").style.visibility = "visible"
};
PwylEditor.prototype.getMousePosition = function (event) {
    var s = ppw.editor.getScrollPosition(event);
    return {
        x: (event.clientX + s.x),
        y: (event.clientY + s.y)
    }
};
PwylEditor.prototype.getScrollPosition = function () {
    var x = 0;
    var y = 0;
    if (typeof (window.pageYOffset) == "number") {
        x = window.pageXOffset;
        y = window.pageYOffset
    } else {
        if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
            x = document.documentElement.scrollLeft;
            y = document.documentElement.scrollTop
        } else {
            if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
                x = document.body.scrollLeft;
                y = document.body.scrollTop
            }
        }
    }
    return {
        x: x,
        y: y
    }
};
PwylEditor.prototype.getScrollOffset = function (elem) {
    return {
        x: elem.scrollLeft,
        y: elem.scrollTop
    }
};
PwylEditor.prototype.redrawHoverBox = function () {
    if (ppw.editor.currHoverElem) {
        ppw.editor.placeHoverBorder(ppw.editor.currHoverElem)
    }
};
PwylEditor.prototype.redrawSelections = function () {
    for (var i = 0; i < this.selectedElems.length; i++) {
        ppw.editor.placeSelectBox(this.selectedElems[i].select_box, 0);
        ppw.editor.placeSelectBox(this.selectedElems[i].select_box_border, 1)
    }
};
PwylEditor.prototype.changeCursorStyle = function (style) {
    YAHOO.util.Dom.setStyle(document.body, "cursor", style);
    YAHOO.util.Dom.setStyle(ppw.util.toolbar, "cursor", style);
    var elems = ppw.util.toolbar.getElementsByTagName("button");
    for (var i = 0; i < elems.length; i++) {
        YAHOO.util.Dom.setStyle(elems[i], "cursor", style)
    }
};
PwylEditor.prototype.arePopupsEnabled = function () {
    var newWin = window.open("", "pwyl_test_window", "height=1,width=1,left=0,top=0,location=no,menubar=no,toolbar=no,scrollbars=no");
    self.focus();
    if (newWin) {
        newWin.close();
        return true
    } else {
        alert("A pop-up blocker is preventing this feature from working. Please disble your pop-up blocker for this site.");
        return false
    }
};
PwylEditor.prototype.hasEcoFontInstalled = function () {
    var d = document.createElement("DIV"),
        s = document.createElement("SPAN");
    d.appendChild(s);
    d.style.fontFamily = "serif";
    s.style.fontFamily = "serif";
    s.style.fontSize = "20px";
    s.innerHTML = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    ppw.util.toolbar.appendChild(d);
    var defaultWidth = s.offsetWidth,
        defaultHeight = s.offsetHeight;
    s.style.fontFamily = "Spranq eco sans";
    var fontW = s.offsetWidth,
        fontH = s.offsetHeight;
    ppw.util.toolbar.removeChild(d);
    return (fontW != defaultWidth || fontH != defaultHeight)
};
PwylEditor.prototype.saveToHtml = function () {
    if (!ppw.print_button) {
        ppw.ui.saveAsPanel.hide()
    }
    ppw.ui.completionMessagePanel.showMessage("ppw_completion_message_panel_html");
    var encoding = ppw.util.getRelevantDocEncoding();
    var ifr = ppw.loader.requestBlankInPageIFrame(null, encoding);
    ppw.loader.copyPage(ifr.head, ifr.body, document.getElementsByTagName("head")[0], document.body, {
        dontCopyJs: true,
        cloneElements: true,
        dontCopyPwylElements: true
    });
    ppw.loader.removePwylFrame(ifr.doc);
    ppw.editor.removeElementsByCssValue("display", "none", ifr.body);
    ppw.editor.postSrc(ifr.doc, ppw.props.home + "print/savehtml", false, false, encoding)
};
PwylEditor.prototype.saveToPdf = function () {
    if (!ppw.print_button) {
        ppw.ui.saveAsPanel.hide()
    }
    if (!ppw.print_button) {
        ppw.ui.completionMessagePanel.showMessage("ppw_completion_message_panel_pdf")
    }
    var encoding = ppw.util.getRelevantDocEncoding();
    var ifr = ppw.loader.requestBlankInPageIFrame(null, encoding);
    ppw.loader.copyPage(ifr.head, ifr.body, document.getElementsByTagName("head")[0], document.body, {
        dontCopyJs: true,
        cloneElements: true,
        dontCopyPwylElements: true
    });
    ppw.loader.removePwylFrame(ifr.doc);
    ppw.loader.addSpacerDiv(ifr.doc);
    ppw.editor.removeElementsByCssValue("display", "none", ifr.body);
    ifr.doc.getElementById("ppw_spacer").style.margin = ".3in";
    ppw.editor.postSrc(ifr.doc, ppw.props.home + "html_to_pdf", false, false, encoding)
};
PwylEditor.prototype.postSrc = function (srcDoc, destUrl, openNewWindow, checkPopupsBlocked, srcDocEncoding) {
    var form = document.getElementById("ppw_save_page_form");
    if (openNewWindow) {
        if (checkPopupsBlocked && !ppw.editor.arePopupsEnabled()) {
            return false
        }
        form.target = "_blank"
    } else {
        form.target = "_self"
    }
    var html = srcDoc.getElementsByTagName("html")[0].outerHTML ? srcDoc.getElementsByTagName("html")[0].outerHTML : "<html>" + srcDoc.getElementsByTagName("html")[0].innerHTML + "</html>";
    form.action = destUrl;
    document.getElementById("ppw_save_page_url").value = ppw.props.pageUrl;
    document.getElementById("ppw_save_page_field").value = html;
    srcDocEncoding = srcDocEncoding ? srcDocEncoding : "";
    document.getElementById("ppw_save_page_encoding").value = srcDocEncoding;
    form.submit()
};
PwylEditor.prototype.copySrcToNewWindow = function (srcDoc) {
    var width = Math.floor(YAHOO.util.Dom.getViewportWidth() * 0.5);
    var height = Math.floor(YAHOO.util.Dom.getViewportHeight() * 0.5);
    var locLeft = Math.floor((YAHOO.util.Dom.getViewportWidth() - width) / 2);
    var locTop = Math.floor((YAHOO.util.Dom.getViewportHeight() - height) / 2);
    var newWin = window.open("", "pwyl_save_html", "height=" + height + ",width=" + width + ",left=" + locLeft + ",top=" + locTop + ",location=yes,menubar=yes,toolbar=yes,scrollbars=yes");
    if (!newWin) {
        alert("A pop-up blocker is preventing this feature from working. Please disble your pop-up blocker for this site.");
        return
    }
    var html = srcDoc.getElementsByTagName("html")[0].outerHTML ? srcDoc.getElementsByTagName("html")[0].outerHTML : "<html>" + srcDoc.getElementsByTagName("html")[0].innerHTML + "</html>";
    var newDoc = newWin.document;
    newDoc.open();
    newDoc.write(html);
    newDoc.close()
};
PwylEditor.prototype.powerOffEditor = function (doc) {
    if (!doc) {
        doc = document
    }
};
PwylEditor.prototype.removeElementsByCssValue = function (cssRule, cssValue, rootNode) {
    var matchingElems = [];
    var callback = function (currElem) {
        if (YAHOO.util.Dom.getStyle(currElem, cssRule) == cssValue) {
            matchingElems.push(currElem);
            return false
        }
        return true
    };
    ppw.util.topDownDepthFirstRecursion(rootNode, callback);
    for (var i = 0; i < matchingElems.length; i++) {
        matchingElems[i].parentNode.removeChild(matchingElems[i])
    }
};
PwylEditor.prototype.fixCssMediaTypes = function (header) {
    var i;
    var cssElems = ppw.editor.getCssTagsOfMedia(header, "screen");
    for (i = 0; i < cssElems.length; i++) {
        cssElems[i].media = "all"
    }
    cssElems = ppw.editor.getCssTagsOfMedia(header, "print");
    for (i = 0; i < cssElems.length; i++) {
        if (cssElems[i].parentNode) {
            cssElems[i].parentNode.removeChild(cssElems[i])
        }
    }
};
PwylEditor.prototype.getCssTagsOfMedia = function (header, media) {
    var elems = [],
        i, e;
    var results = YAHOO.util.Selector.query("link[rel][type][media]", header);
    for (i = 0; i < results.length; i++) {
        e = results[i];
        if (!e.getAttribute("pwyl") && e.rel.toLowerCase() == "stylesheet" && e.type.toLowerCase() == "text/css" && e.media.toLowerCase() == media) {
            elems.push(e)
        }
    }
    results = YAHOO.util.Selector.query("style[media]", header);
    for (i = 0; i < results.length; i++) {
        if (results[0]["media"].toLowerCase() == media) {
            elems.push(results[0])
        }
    }
    return elems
};

