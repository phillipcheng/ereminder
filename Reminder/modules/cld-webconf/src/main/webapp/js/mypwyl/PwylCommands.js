function PwylCommand() {
    this.name = "";
    this.doFunctions = [];
    this.undoFunctions = [];
    this.selectedElementPaths = [];
    this.args = [];
    this.undoable = true;
    this.addPage = false
}
PwylCommand.prototype.doAction = function (undoable) {
    this._executeAll(this.doFunctions)
};
PwylCommand.prototype.undoAction = function () {
    this._executeAll(this.undoFunctions)
};
PwylCommand.prototype._executeAll = function (functions) {
    for (var i = 0; i < functions.length; i++) {
        if ((this.args.length - 1) < i) {
            this.args[i] = []
        }
        var vars = functions[i].split(".");
        window[vars[0]][vars[1]][vars[2]](this, this.args[i])
    }
};
PwylCommand.prototype.setDoFunction = function () {
    for (var i = 0; i < arguments.length; i++) {
        this.doFunctions.push(arguments[i])
    }
};
PwylCommand.prototype.setUndoFunction = function () {
    for (var i = 0; i < arguments.length; i++) {
        this.undoFunctions.push(arguments[i])
    }
};
PwylCommand.prototype.stashSelectedElements = function () {
    ppw.editor.fixTop();
    var elems = ppw.util.clearSelectedElements();
    for (var i = 0; i < elems.length; i++) {
        this.selectedElementPaths[i] = ppw.paths.getPath(elems[i])
    }
};
PwylCommand.prototype.executeOnSelectedElements = function (functionCallback) {
    for (var i = 0; i < this.selectedElementPaths.length; i++) {
        var elem = ppw.paths.getElem(this.selectedElementPaths[i]);
        if (elem) {
            functionCallback(elem)
        }
    }
};
PwylCommand.prototype.serialize = function () {
    return YAHOO.lang.JSON.stringify(this)
};
PwylCommand.deserialize = function (str) {
    var command = new PwylCommand();
    try {
        var obj = YAHOO.lang.JSON.parse(str);
        YAHOO.lang.augmentObject(command, obj, true)
    } catch (e) {
        if (window.console) {
            console.log("error deserializing: " + e)
        }
    }
    return command
};

function PwylMultiCommand() {
    this.name = "";
    this.commands = [];
    this.undoable = true;
    this.addPage = false;
    this.afterDoAction = null;
    this.afterUndoAction = null
}
PwylMultiCommand.prototype.doAction = function () {
    ppw.changeset.executeCommandsSet(this.commands, this.afterDoAction, this.addPage)
};
PwylMultiCommand.prototype.undoAction = function () {
    for (var i = this.commands.length - 1; i >= 0; i--) {
        this.commands[i].undoAction()
    }
    if (this.afterUndoAction) {
        this.afterUndoAction()
    }
};
PwylMultiCommand.prototype.setCommand = function (cmd, selectedElems) {
    this.commands.push(cmd)
};

function PwylCommands() {}
PwylCommands.prototype._doChangeProperty = function (command, args) {
    element = ppw.paths.getElem(args.element_id);
    if (args.includeSubelements) {
        ppw.util.depthFirstRecursion(element, function (elem) {
            for (var i = 0; i < args.properties.length; i++) {
                ppw.util.applyProperty(elem, args.properties[i].name, args.properties[i].value, false, true)
            }
        }, [], true)
    } else {
        for (var i = 0; i < args.properties.length; i++) {
            ppw.util.applyProperty(element, args.properties[i].name, args.properties[i].value, false, true)
        }
    }
};
PwylCommands.prototype._undoChangeProperty = function (command, args) {
    element = ppw.paths.getElem(args.element_id);
    if (args.includeSubelements) {
        ppw.util.depthFirstRecursion(element, function (elem) {
            for (var i = 0; i < args.properties.length; i++) {
                ppw.util.undoApplyProperty(elem, args.properties[i].name)
            }
        }, [], true)
    } else {
        for (var i = 0; i < args.properties.length; i++) {
            ppw.util.undoApplyProperty(element, args.properties[i].name)
        }
    }
};
PwylCommands.prototype.changeProperty = function (element, name, value, includeSubelements, command) {
    ppw.commands.changeMultipleProperties(element, [{
        name: name,
        value: value
    }], includeSubelements, command)
};
PwylCommands.prototype.changeMultipleProperties = function (element, properties, includeSubelements, command) {
    element_id = ppw.paths.getPath(element);
    command.args.push({
        element_id: element_id,
        properties: properties,
        includeSubelements: includeSubelements
    });
    command.setDoFunction("ppw.commands._doChangeProperty");
    command.setUndoFunction("ppw.commands._undoChangeProperty")
};
PwylCommands.prototype._doChangePropertyOnSelectedElements = function (command, args) {
    command.executeOnSelectedElements(function (elem) {
        ppw.util.applyProperty(elem, args.propertyName, args.value)
    })
};
PwylCommands.prototype._undoChangePropertyOnSelectedElements = function (command, args) {
    command.executeOnSelectedElements(function (elem) {
        ppw.util.undoApplyProperty(elem, args.propertyName)
    })
};
PwylCommands.prototype.changePropertyOnSelectedElements = function (propertyName, value, command) {
    if (value) {
        value = value.replace(/'/g, "\\'")
    }
    command.args.push({
        propertyName: propertyName,
        value: value
    });
    command.setDoFunction("ppw.commands._doChangePropertyOnSelectedElements");
    command.setUndoFunction("ppw.commands._undoChangePropertyOnSelectedElements")
};
PwylCommands.prototype._doChangePropertyOnTags = function (command, args) {
    ppw.util.executeOnAllElementsOfTag(args.tags, function (elem) {
        ppw.util.applyProperty(elem, args.propertyName, args.value)
    })
};
PwylCommands.prototype._undoChangePropertyOnTags = function (command, args) {
    ppw.util.executeOnAllElementsOfTag(args.tags, function (elem) {
        ppw.util.undoApplyProperty(elem, args.propertyName)
    })
};
PwylCommands.prototype.changePropertyOnTags = function (tags, propertyName, value, command) {
    if (value) {
        value = value.replace(/'/g, "\\'")
    }
    command.args.push({
        tags: tags,
        propertyName: propertyName,
        value: value
    });
    command.setDoFunction("ppw.commands._doChangePropertyOnTags");
    command.setUndoFunction("ppw.commands._undoChangePropertyOnTags")
};
PwylCommands.prototype.changeFontSize = function (amount) {
    var command = new PwylCommand();
    command.name = (amount > 0 ? "Increase" : "Decrease") + " Font Size";
    command.args.push({
        amount: amount
    });
    command.setDoFunction("ppw.commands._doChangeFontSize");
    command.setUndoFunction("ppw.commands._undoChangeFontSize");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doChangeFontSize = function (currCommand, args) {
    ppw.util.depthFirstRecursion(ppw.util.top, function (elem) {
        var newFontSize = ppw.commands.incrementFontSize(YAHOO.util.Dom.getStyle(elem, "fontSize"), args.amount);
        ppw.util.applyProperty(elem, "fontSize", newFontSize, false, true)
    }, [], true)
};
PwylCommands.prototype._undoChangeFontSize = function (currCommand, args) {
    ppw.util.getElementAndSubelements(ppw.util.top).each(function (elem) {
        ppw.util.undoApplyProperty(elem, "fontSize")
    })
};
PwylCommands.prototype.incrementFontSize = function (fontSize, fontChange) {
    var size = 12;
    var units = "px";
    var results = /^(\d*)([a-z]*)$/i.exec(fontSize);
    if (results && results.length >= 2) {
        size = parseInt(results[1])
    }
    if (results && results.length >= 3) {
        units = results[2]
    }
    return (size + fontChange) + units
};
PwylCommands.prototype.changeFontType = function () {
    var select = document.getElementById("ppw_select_font_dropdown");
    var fontType = select.options[select.selectedIndex].value;
    if (fontType) {
        fontType = fontType.replace(/'/g, "\\'")
    }
    if (fontType == "Spranq eco sans" && !ppw.editor.hasEcoFontInstalled()) {
        ppw.ui.ecofontHelpPanel.toggle();
        return
    }
    var command = new PwylCommand();
    command.name = "Change Font Type to " + fontType;
    ppw.commands.changeProperty(ppw.util.pageTop, "fontFamily", fontType, true, command);
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doRemoveBackground = function () {
    YAHOO.util.Dom.addClass(ppw.util.top, "ppw_clear_bg");
    YAHOO.util.Dom.addClass(document.body, "ppw_remove_bg");
    YAHOO.util.Dom.addClass(document.getElementsByTagName("html")[0], "ppw_remove_bg");
    if (!ppw.print_button) {
        ppw.ui.disableRemoveBackground()
    }
};
PwylCommands.prototype._undoRemoveBackground = function () {
    YAHOO.util.Dom.removeClass(document.getElementsByTagName("html")[0], "ppw_remove_bg");
    YAHOO.util.Dom.removeClass(document.body, "ppw_remove_bg");
    YAHOO.util.Dom.removeClass(ppw.util.top, "ppw_clear_bg");
    if (!ppw.print_button) {
        ppw.ui.enableRemoveBackground()
    }
};
PwylCommands.prototype.removeBackground = function () {
    var command = new PwylCommand();
    command.name = "Remove Background";
    command.setDoFunction("ppw.commands._doRemoveBackground");
    command.setUndoFunction("ppw.commands._undoRemoveBackground");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doRemoveImages = function () {
    YAHOO.util.Dom.addClass(ppw.util.top, "ppw_remove_images");
    YAHOO.util.Dom.addClass(document.body, "ppw_remove_images_body");
    if (!ppw.print_button) {
        ppw.ui.disableRemoveImages()
    }
};
PwylCommands.prototype._undoRemoveImages = function () {
    YAHOO.util.Dom.removeClass(ppw.util.top, "ppw_remove_images");
    YAHOO.util.Dom.removeClass(document.body, "ppw_remove_images_body");
    if (!ppw.print_button) {
        ppw.ui.enableRemoveImages()
    }
};
PwylCommands.prototype.removeImages = function () {
    var command = new PwylCommand();
    command.name = "Remove Images";
    command.setDoFunction("ppw.commands._doRemoveImages");
    command.setUndoFunction("ppw.commands._undoRemoveImages");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doRemoveMargin = function () {
    YAHOO.util.Dom.addClass(ppw.util.top, "ppw_remove_margin");
    ppw.ui.toggleButtons("ppw_margins_hide", "ppw_margins_show")
};
PwylCommands.prototype._undoRemoveMargin = function () {
    YAHOO.util.Dom.removeClass(ppw.util.top, "ppw_remove_margin");
    ppw.ui.toggleButtons("ppw_margins_show", "ppw_margins_hide")
};
PwylCommands.prototype.removeMargin = function () {
    var command = new PwylCommand();
    command.name = "Remove Margin";
    command.setDoFunction("ppw.commands._doRemoveMargin");
    command.setUndoFunction("ppw.commands._undoRemoveMargin");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype.showStyle = function (styleId) {
    var command, i, styleCmd;
    switch (styleId) {
        case 1:
            styleCmd = "Remove Background";
            break;
        case 2:
            styleCmd = "Remove Images";
            break;
        case 3:
            styleCmd = "Remove Margin";
            break
    }
    for (i = 0; i < ppw.changeset.doStack.length; i++) {
        command = ppw.changeset.doStack[i];
        if (command.name == styleCmd) {
            ppw.changeset.undo(i, true);
            return
        }
    }
};
PwylCommands.prototype.remove = function () {
    var command = new PwylCommand();
    command.name = "Remove Selection";
    command.stashSelectedElements();
    ppw.commands.changePropertyOnSelectedElements("display", "none", command);
    ppw.changeset.doAction(command)
};

//Cheng Yi 
PwylCommands.prototype.selectXPath = function () {
	if (ppw.editor.selectedElems.length>0){
		var target = ppw.editor.selectedElems[ppw.editor.selectedElems.length-1];
		var xpath = getElementXPath(target);
	    var toFillXPath = document.getElementById("xmlForm").getAttribute("selectedXPath");
		var toFillXPathInput = getElementByXpath(toFillXPath);
		if (toFillXPathInput!=null){
			toFillXPathInput.value=xpath;
			textAreaAutoSize(toFillXPathInput);
		}
	}
};

//Cheng Yi 
PwylCommands.prototype.goUrl = function () {
	if (ppw.editor.selectedElems.length>0){
		var target = ppw.editor.selectedElems[ppw.editor.selectedElems.length-1];
		var url = getAbsUrl(target);
		if (url){
			setReloadUrlAndSubmit(url, false);
		}else{
			alert("url is nil");
		}
	}
};

//Cheng Yi 
PwylCommands.prototype.goUrlJS = function () {
	if (ppw.editor.selectedElems.length>0){
		var target = ppw.editor.selectedElems[ppw.editor.selectedElems.length-1];
		var url = getAbsUrl(target);
		if (url){
			setReloadUrlAndSubmit(url, true);
		}else{
			alert("url is nil");
		}
	}
};

PwylCommands.prototype._doIsolate = function (currCommand, args) {
    args.isolatedElems = [];
    currCommand.executeOnSelectedElements(function (currElem) {
        args.isolatedElems.push(currElem)
    });
    ppw.commands.isolateElems(args.isolatedElems)
};
PwylCommands.prototype.isolateElems = function (elems) {
    if (ppw.commands._containsPageTop(elems)) {
        return
    }
    for (var i = 0; i < elems.length; i++) {
        var currElem = elems[i];
        if (!currElem.parentNode) {
            continue
        }
        var currElemBgColor = ppw.util.getBackgroundColor(currElem);
        var currElemStyleRules = ppw.cssutil.getStyleAttributes(currElem);
        var origParent = currElem.parentNode;
        var origPos = ppw.util.toArray(currElem.parentNode.childNodes).indexOf(currElem);
        currElem = currElem.parentNode.removeChild(currElem);
        var modifiedCurrElem = ppw.commands.cleanElement(currElem);
        modifiedCurrElem.origElem = currElem;
        modifiedCurrElem.origParent = origParent;
        modifiedCurrElem.origPos = origPos;
        ppw.cssutil.applyStyleAttributesToElement(modifiedCurrElem, currElemStyleRules);
        ppw.cssutil.removePositionProperties(modifiedCurrElem);
        ppw.util.applyProperty(modifiedCurrElem, "backgroundColor", currElemBgColor);
        elems[i] = modifiedCurrElem
    }
    ppw.util.toArray(ppw.util.pageTop.childNodes).each(function (child) {
        ppw.util.makeInvisible(child)
    });
    elems.each(function (elem) {
        var base = ppw.paths.getBase(elem.origParent);
        var div = document.createElement("div");
        div.id = "ppw_isolate_" + ppw.paths.countIsolatedElems(base);
        div.className = "ppw_isolate";
        base.appendChild(div);
        div.appendChild(elem)
    });
    window.scroll(0, 0)
};
PwylCommands.prototype._undoIsolate = function (currCommand, args) {
    ppw.commands.unIsolateElems(args.isolatedElems)
};
PwylCommands.prototype.unIsolateElems = function (elems) {
    if (ppw.commands._containsPageTop(elems)) {
        return
    }
    for (var i = elems.length; i > 0; i--) {
        var currElem = elems[i - 1];
        if (!currElem.parentNode) {
            continue
        }
        currElem.parentNode.parentNode.removeChild(currElem.parentNode);
        ppw.cssutil.undoRemovePositionProperties(currElem);
        ppw.util.undoApplyProperty(currElem, "backgroundColor");
        if (currElem.origParent.childNodes.length == 0 || currElem.origPos >= currElem.origParent.childNodes.length) {
            currElem.origParent.appendChild(currElem.origElem)
        } else {
            var followingElem = currElem.origParent.childNodes[currElem.origPos];
            currElem.origParent.insertBefore(currElem.origElem, followingElem)
        }
    }
    ppw.util.toArray(ppw.util.pageTop.childNodes).each(function (child) {
        ppw.util.makeVisible(child)
    })
};
PwylCommands.prototype.cleanElement = function (elem) {
    var tagName = elem.tagName.toLowerCase();
    if (tagName == "tr" || tagName == "tfoot" || tagName == "thead") {
        var table = document.createElement("table");
        var tbody = document.createElement("tbody");
        table.appendChild(tbody);
        tbody.appendChild(elem);
        return table
    }
    if (tagName == "td" || tagName == "th") {
        var table = document.createElement("table");
        var tbody = document.createElement("tbody");
        var tr = document.createElement("tr");
        table.appendChild(tbody);
        tbody.appendChild(tr);
        tr.appendChild(elem);
        return table
    }
    return elem
};
PwylCommands.prototype._containsPageTop = function (elems) {
    for (var i = 0; i < elems.length; i++) {
        if (elems[i] == ppw.util.pageTop) {
            return true
        }
    }
    return false
};
PwylCommands.prototype.isolate = function () {
    if (ppw.editor.selectedElems <= 0) {
        ppw.init.afterDo();
        return false
    }
    var command = new PwylCommand();
    command.name = "Isolate";
    ppw.editor.selectedElems = ppw.util.orderPageElements(ppw.editor.selectedElems);
    command.stashSelectedElements();
    command.setDoFunction("ppw.commands._doIsolate");
    command.setUndoFunction("ppw.commands._undoIsolate");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doWiden = function (command, args) {
    command.executeOnSelectedElements(function (elem) {
        ppw.util.applyToElementAndParents(elem, function (elem2) {
            ppw.util.applyProperty(elem2, "width", "100%");
            if (elem2.getAttribute("width")) {
                ppw.util.applyProperty(elem2, "width", "100%", true)
            }
            var ml = YAHOO.util.Dom.getStyle(elem2, "marginLeft"),
                mr = YAHOO.util.Dom.getStyle(elem2, "marginRight"),
                pl = YAHOO.util.Dom.getStyle(elem2, "paddingLeft"),
                pr = YAHOO.util.Dom.getStyle(elem2, "paddingRight");
            if (ml != "auto" && !ml.startsWith("0")) {
                ppw.util.applyProperty(elem2, "marginLeft", "0")
            }
            if (mr != "auto" && !mr.startsWith("0")) {
                ppw.util.applyProperty(elem2, "marginRight", "0")
            }
            if (pl != "auto" && !pl.startsWith("0")) {
                ppw.util.applyProperty(elem2, "paddingLeft", "0")
            }
            if (pr != "auto" && !pr.startsWith("0")) {
                ppw.util.applyProperty(elem2, "paddingRight", "0")
            }
            if (YAHOO.util.Dom.getStyle(elem2, "left") != "auto") {
                ppw.util.applyProperty(elem2, "left", "0", false, true)
            }
        })
    })
};
PwylCommands.prototype._undoWiden = function (command, args) {
    command.executeOnSelectedElements(function (elem) {
        ppw.util.applyToElementAndParents(elem, function (elem2) {
            ppw.util.undoApplyProperty(elem2, "width");
            ppw.util.undoApplyProperty(elem2, "width", true);
            ppw.util.undoApplyProperty(elem2, "marginLeft");
            ppw.util.undoApplyProperty(elem2, "marginRight");
            ppw.util.undoApplyProperty(elem2, "paddingLeft");
            ppw.util.undoApplyProperty(elem2, "paddingRight");
            ppw.util.undoApplyProperty(elem2, "left")
        })
    })
};
PwylCommands.prototype.widen = function () {
    var command = new PwylCommand();
    command.name = "Widen";
    command.stashSelectedElements();
    command.setDoFunction("ppw.commands._doWiden");
    command.setUndoFunction("ppw.commands._undoWiden");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype.insideSelectedAreas = function (x, y, command) {
    command.executeOnSelectedElements(function (curr) {
        var pos = YAHOO.util.Dom.getXY(curr);
        if ((pos[0] - 15) <= x && x <= (pos[0] + curr.offsetWidth + 15) && (pos[1] - 15) <= y && y <= (pos[1] + curr.offsetHeight + 15)) {
            return true
        }
    });
    return false
};
PwylCommands.prototype.startResizeMode = function () {
    if (ppw.editor.selectedElems <= 0) {
        ppw.init.afterDo();
        return false
    }
    var command = new PwylCommand();
    command.name = "Resize";
    command.stashSelectedElements();
    var resizeObjs = [];
    command.useLightbox = (YAHOO.env.ua.gecko || YAHOO.env.ua.opera || YAHOO.env.ua.webkit || (YAHOO.env.ua.ie >= 8));
    var lightbox = document.getElementById("ppw_lightbox");
    command.executeOnSelectedElements(function (curr) {
        ppw.util.saveCurrentValueOfProperty(curr, "width");
        ppw.util.saveCurrentValueOfProperty(curr, "height");
        if (command.useLightbox) {
            ppw.util.applyProperty(curr, "backgroundColor", ppw.util.getBackgroundColor(curr));
            ppw.util.applyProperty(curr, "zIndex", "11000")
        }
        resizeObjs.push(new YAHOO.util.Resize(curr, {
            handles: "all",
            knobHandles: true
        }))
    });
    if (command.useLightbox) {
        lightbox.style.left = "210px";
        lightbox.style.width = (ppw.util.top.offsetWidth + 2) + "px";
        lightbox.style.height = YAHOO.util.Dom.getDocumentHeight() + "px";
        lightbox.style.display = "block"
    }
    ppw.editor.unInitializeHandlers(true);
    (command.useLightbox ? lightbox : ppw.util.top).onclick = function (event) {
        event = ppw.util.captureEvent(event);
        ppw.util.noBubble(event);
        var m = ppw.editor.getMousePosition(event);
        if (!ppw.commands.insideSelectedAreas(m.x, m.y, command)) {
            ppw.commands.endResizeMode(command, resizeObjs)
        }
        return false
    };
    return false
};
PwylCommands.prototype.endResizeMode = function (command, resizeObjs) {
    for (var i = 0; i < resizeObjs.length; i++) {
        resizeObjs[i].destroy()
    }
    ppw.commands.resize(command);
    command.executeOnSelectedElements(function (curr) {
        ppw.util.undoApplyProperty(curr, "backgroundColor");
        ppw.util.undoApplyProperty(curr, "zIndex")
    });
    if (command.useLightbox) {
        document.getElementById("ppw_lightbox").style.display = "none"
    }
    ppw.editor.initializeHandlers()
};
PwylCommands.prototype.resize = function (command) {
    command.executeOnSelectedElements(function (curr) {
        var resizedWidth = curr.style.width;
        var resizedHeight = curr.style.height;
        ppw.editor.fixTop();
        var resizedWidthAmt = parseFloat(resizedWidth.replace("px", ""));
        var resizedHeightAmt = parseFloat(resizedHeight.replace("px", ""));
        ppw.util.undoApplyProperty(curr, "width");
        ppw.util.undoApplyProperty(curr, "height");
        var origWidth = curr.offsetWidth;
        var origHeight = curr.offsetHeight;
        var widthPercent = 1;
        var heightPercent = 1;
        if (origWidth > 0) {
            widthPercent = resizedWidthAmt / origWidth
        }
        if (origHeight > 0) {
            heightPercent = resizedHeightAmt / origHeight
        }
        var element_id = ppw.paths.getPath(curr);
        command.args.push({
            element_id: element_id,
            widthPercent: widthPercent,
            heightPercent: heightPercent
        });
        command.setDoFunction("ppw.commands._doResizeElement");
        command.setUndoFunction("ppw.commands._undoResizeElement")
    });
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doResizeElement = function (command, args) {
    var element = ppw.paths.getElem(args.element_id);
    var newWidth = args.widthPercent * element.offsetWidth + "px";
    var newHeight = args.heightPercent * element.offsetHeight + "px";
    ppw.util.applyProperty(element, "width", newWidth, false, true);
    ppw.util.applyProperty(element, "height", newHeight, false, true)
};
PwylCommands.prototype._undoResizeElement = function (command, args) {
    var element = ppw.paths.getElem(args.element_id);
    ppw.util.undoApplyProperty(element, "width");
    ppw.util.undoApplyProperty(element, "height")
};
PwylCommands.prototype.auto = function () {
    var command = new PwylCommand();
    command.name = "Auto Format";
    command.setDoFunction("ppw.auto.scrub");
    command.setUndoFunction("ppw.auto.unscrub");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype.addPage = function (newPageUrl) {
    var applyChangesCheckbox = document.getElementById("ppw_add_page_apply_changes_checkbox");
    var applyChanges = (applyChangesCheckbox.checked && newPageUrl && (ppw.server.pageUrl.host == new ppw.server.URLManager(newPageUrl).host));
    var command = new PwylCommand();
    command.name = "Add Page";
    command.args.push({
        url: newPageUrl,
        id: ppw.paths.getNewPageId(),
        applyChanges: applyChanges,
        origHeight: ppw.ui.orig_doc_height
    });
    command.setDoFunction("ppw.commands._doAddPage");
    command.setUndoFunction("ppw.commands._undoAddPage");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doAddPage = function (command, args) {
    ppw.ui.before_add_page_height = YAHOO.util.Dom.getDocumentHeight();
    var requestedUrl;
    try {
        requestedUrl = new ppw.server.URLManager(args.url)
    } catch (e) {
        alert("hmmmm. " + url + " does not seem to be a valid url. Are you sure you entered it correctly?");
        return
    }
    if (ppw.bookmarklet) {
        if (ppw.server.url.host == requestedUrl.host) {
            ppw.loader.requestIFrame(requestedUrl.url, "ppw.commands._addPage", false, args.id, args.applyChanges)
        } else {
            ppw.loader.requestPageUsingJS(requestedUrl.url, ppw.commands._addPageJsBkmt, args.id, args.applyChanges)
        }
    } else {
        ppw.loader.requestIFrame(args.url, "ppw.commands._addPage", true, args.id, args.applyChanges)
    }
};
PwylCommands.prototype._addPage = function (ifr, pageBodyId, applyChanges) {
    var hr = document.createElement("hr");
    hr.id = pageBodyId + "_hr";
    hr.className = "ppw_page_divider";
    ppw.util.pageTop.appendChild(hr);
    var newPage = ppw.paths.getNewPage(pageBodyId);
    ppw.util.pageTop.appendChild(newPage);
    var customHeadAttributes = {
        addpage: pageBodyId
    };
    var copyOptions = {
        dontCopyJs: true,
        cloneElements: false,
        dontCopyPwylElements: false,
        applyTemplate: applyChanges,
        customHeadAttributes: customHeadAttributes,
        dontCopyIdAttribute: true
    };
    ppw.loader.copyPage(document.getElementsByTagName("head")[0], newPage, ifr.head, ifr.body, copyOptions);
    ppw.init.afterPageLoad();
    var nextPage = pgzp().buildPage(pgzp().doc.body, ifr.destUrl);
    pgzp().pages.push(nextPage);
    nextPage.nextLink = pgzp().getNextLink(nextPage.page);
    var new_page_height = YAHOO.util.Dom.getDocumentHeight();
    var add_page_height = new_page_height - ppw.ui.before_add_page_height;
    ppw.ui.orig_doc_height += (new_page_height - ppw.ui.before_add_page_height);
    ppw.changeset.resumeExecutingCommandSetIfRequired()
};
PwylCommands.prototype._addPageJsBkmt = function (src, args) {
    var ifr = ppw.loader.requestInPageIFrame(src);
    ppw.commands._addPage(ifr, args.pageBodyId, args.applyChanges)
};
PwylCommands.prototype._undoAddPage = function (command, args) {
    ppw.util.removeElement(args.id + "_hr");
    var addPageBody = document.getElementById(args.id);
    addPageBody.parentNode.removeChild(addPageBody);
    var head = document.getElementsByTagName("head")[0];
    var elementsToRemove = [];
    for (var i = 0; i < head.childNodes.length; i++) {
        var node = head.childNodes[i];
        if (node.nodeType == Node.ELEMENT_NODE) {
            var addPageId = node.getAttribute("addpage");
            if (addPageId && addPageId == args.id) {
                elementsToRemove.push(node)
            }
        }
    }
    for (var i = 0; i < elementsToRemove.length; i++) {
        var el = elementsToRemove[i];
        el.parentNode.removeChild(el)
    }
    ppw.ui.orig_doc_height = args.origHeight
};
PwylCommands.prototype.addNextPage = function () {
    var currPage = pgzp().pages.length - 1;
    var newPageUrl = pgzp().pages[currPage].nextLink.url;
    pgzp().url_list.push(newPageUrl);
    var command = new PwylCommand();
    command.name = "Add Next Page";
    command.args.push({
        url: newPageUrl,
        id: ppw.paths.getNewPageId(),
        applyChanges: true,
        origHeight: ppw.ui.orig_doc_height
    });
    command.setDoFunction("ppw.commands._doAddNextPage");
    command.setUndoFunction("ppw.commands._undoAddNextPage");
    ppw.changeset.doAction(command)
};
PwylCommands.prototype._doAddNextPage = function (command, args) {
    ppw.commands._doAddPage(command, args)
};
PwylCommands.prototype._undoAddNextPage = function (command, args) {
    pgzp().pages.pop();
    pgzp().url_list.pop();
    ppw.commands._undoAddPage(command, args)
};
PwylCommands.prototype.applySelectedChangeSet = function () {
    var changeSetDropdown = document.getElementById("ppw_select_change_set_dropdown");
    var changeSetId = changeSetDropdown.options[changeSetDropdown.selectedIndex].value;
    if (changeSetId == "Apply Change Set") {
        ppw.init.afterDo()
    } else {
        ppw.acs.applyChangeSet(changeSetId)
    }
};
PwylCommands.prototype.saveClip = function () {
    var totalClipsSaved = ppw.props.clipsSaved + ppw.editor.selectedElems.length;
    if (ppw.props.enforceQuotas) {
        ppw.props.clipsSaved = totalClipsSaved
    }
    var params = {
        user_id: ppw.props.proAccountId,
        name: document.title.replace("PrintWhatYouLike on ", ""),
        url: ppw.props.pageUrl,
        num_clips: ppw.editor.selectedElems.length
    };
    for (var i = 0; i < ppw.editor.selectedElems.length; i++) {
        params["html" + i] = ppw.util.getHtmlClip(ppw.editor.selectedElems[i])
    }
    var onComplete = function (response) {
        var msg = ppw.editor.selectedElems.length + " clip" + (ppw.editor.selectedElems.length > 1 ? "s" : "") + " saved";
        ppw.ui.setSelfDestructingInfoMessage(msg)
    };
    ppw.server.ajaxCall("clips", params, onComplete, "post", {
        forceMultipart: true
    })
};
