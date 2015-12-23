function PwylUI() {
    this.saveVars = [];
    this.proToolbar = null
}
PwylUI.prototype.instantiateUI = function () {
    var buttons = [];
    buttons.concatArray(YAHOO.util.Dom.getElementsByClassName("ppw_fancy_button", null, ppw.util.toolbar));
    buttons.concatArray(YAHOO.util.Dom.getElementsByClassName("ppw_double_button", null, ppw.util.toolbar));
    for (var i = 0; i < buttons.length; i++) {
        var button = buttons[i];
        button.onmouseover = ppw.ui.buttonMouseOver;
        button.onmouseout = ppw.ui.buttonMouseOut;
        button.onmousedown = ppw.ui.buttonMouseDown;
        button.onmouseup = ppw.ui.buttonMouseUp
    }
    var textFields = YAHOO.util.Dom.getElementsByClassName("ppw_toolbar_input_field", "input", ppw.util.toolbar);
    for (var i = 0; i < textFields.length; i++) {
        textFields[i].onfocus = function () {
            ppw.editor.enableKeyboardShortcuts = false
        };
        textFields[i].onblur = function () {
            ppw.editor.enableKeyboardShortcuts = true
        }
    }
    new Image().src = ppw.props.home + "editor/img/button_hover1.jpeg";
    this.addPagePanel = new ppw.panel.Panel("ppw_add_page", "ppw_add_page_link");
    this.newPagePanel = new ppw.panel.Panel("ppw_new_page", "ppw_new_page_link");
    this.saveAsPanel = new ppw.panel.Panel("ppw_save_as", "ppw_save_as_link");
    this.helpPanel = new ppw.panel.Panel("ppw_help", "ppw_help_link");
    this.undoListPanel = new ppw.panel.Panel("ppw_undo_list", "ppw_undo_select_button");
    this.pageHelpPanel = new ppw.panel.IFramePanel(ppw.props.home + "help/page", "ppw_page_help_bd", "ppw_page_help", true);
    this.ecofontHelpPanel = new ppw.panel.MessagePanel("ppw_ecofont_help");
    this.saveChangeSetPanel = new ppw.panel.MessagePanel("ppw_save_change_set");
    this.accountRequiredPanel = ppw.bookmarklet ? new ppw.panel.MessagePanel("ppw_bookmark_login_form") : new ppw.panel.IFramePanel(ppw.props.home + "user_sessions/authorize_frame?source=" + (ppw.bookmarklet ? "bookmarklet" : "toolbar"), "ppw_account_required_bd", "ppw_account_required");
    this.overQuotaPanel = new ppw.panel.IFramePanel(ppw.props.home + "subscriptions/new?source=" + (ppw.bookmarklet ? "bookmarklet" : "toolbar"), "ppw_over_quota_bd", "ppw_over_quota");
    if (ppw.util.onProEditPage()) {
        this.createPageHelpPanel = new ppw.panel.IFramePanel(ppw.props.home + "help/page_from_clips", "ppw_create_page_help_bd", "ppw_create_page_help", true)
    }
    this.completionMessagePanel = new ppw.panel.MessagePanel("ppw_completion_message_panel");
    this.completionMessagePanel.showMessage = function (messageId) {
        var elems = YAHOO.util.Dom.getElementsByClassName("ppw_completion_message_panel_message", "div", "ppw_completion_message_panel");
        for (var i = 0; i < elems.length; i++) {
            elems[i].style.display = "none"
        }
        YAHOO.util.Dom.get(messageId).style.display = "block";
        ppw.ui.completionMessagePanel.show()
    };
    draggableMenu = new YAHOO.util.DD("ppw_selection_commands");
    draggableMenu.setHandleElId("ppw_selection_commands_drag")
};
PwylUI.prototype.buttonMouseOver = function () {
    YAHOO.util.Dom.addClass(this, "ppw_button_onhover")
};
PwylUI.prototype.buttonMouseOut = function () {
    YAHOO.util.Dom.removeClass(this, "ppw_button_onhover");
    YAHOO.util.Dom.removeClass(this, "ppw_button_onclick")
};
PwylUI.prototype.buttonMouseDown = function () {
    YAHOO.util.Dom.addClass(this, "ppw_button_onclick")
};
PwylUI.prototype.buttonMouseUp = function () {
    YAHOO.util.Dom.removeClass(this, "ppw_button_onclick")
};
PwylUI.prototype.addPage = function () {
    ppw.ui.addPageBusy();
    ppw.commands.addPage(document.getElementById("ppw_add_page_url_form_field").value.strip())
};
PwylUI.prototype.printPage = function () {
    ppw.ui.completionMessagePanel.showMessage("ppw_completion_message_panel_print");
    window.print();
    try {
        ppw.savings.recordSavings()
    } catch (e) {}
};
PwylUI.prototype.addPageBusy = function () {
    document.getElementById("ppw_add_page_url_form_submit").blur();
    document.getElementById("ppw_add_page_url_form_submit").style.display = "none";
    document.getElementById("ppw_add_page_busy_icon").style.display = "inline"
};
PwylUI.prototype.addPageNormal = function () {
    ppw.ui.addPagePanel.hide();
    ppw.ui.addPagePanel.isVisible = false;
    document.getElementById("ppw_add_page_busy_icon").style.display = "none";
    document.getElementById("ppw_add_page_url_form_submit").style.display = "inline"
};
PwylUI.prototype.setToolbarCloseButton = function () {
    document.getElementById("ppw_close_toolbar").style.display = "block"
};
PwylUI.prototype.updateHelpMenuForMac = function () {
    if (ppw.util.os == "mac") {
        YAHOO.util.Dom.getElementsByClassName("ppw_help_ctrl_z", "span", null, function (el) {
            el.innerHTML = "&#8984; Z"
        });
        YAHOO.util.Dom.getElementsByClassName("ppw_help_ctrl_y", "span", null, function (el) {
            el.innerHTML = "&#8984; Y"
        });
        YAHOO.util.Dom.getElementsByClassName("ppw_help_del", "span", null, function (el) {
            el.innerHTML = "R"
        })
    }
};
PwylUI.prototype.setPageWidth = function () {
    if (ppw.util.onProEditPage()) {
        var proToolbar = document.getElementById("ppw_pro_toolbar_content");
        proToolbar.style.left = (YAHOO.util.Dom.getViewportWidth() - 210 - 2) + "px"
    }
    var clipsList = document.getElementById("ppw_clips");
    if (clipsList) {
        clipsList.style.height = (YAHOO.util.Dom.getViewportHeight() - YAHOO.util.Dom.getXY(clipsList)[1]) + "px"
    }
    var topW = YAHOO.util.Dom.getViewportWidth() - 210 - 2 - 0;
    if (ppw.util.onProEditPage()) {
        topW = topW - 210 - 2
    }
    ppw.util.top.style.width = topW + "px"
};
PwylUI.prototype.setBackgroundColor = function () {
    var bgcolor = YAHOO.util.Dom.getStyle(document.body, "backgroundColor");
    ppw.util.pageTop.style.backgroundColor = bgcolor
};
PwylUI.prototype.setOverflow = function () {
    if (YAHOO.util.Dom.getStyle(document.body, "overflow") == "hidden") {
        document.body.style.overflow = "visible"
    }
};
PwylUI.prototype.addFontFace = function () {
    if (ppw.bookmarklet) {
        return
    }
    var cssElem = document.createElement("link");
    cssElem.setAttribute("rel", "stylesheet");
    cssElem.setAttribute("type", "text/css");
    cssElem.setAttribute("pwyl", "true");
    cssElem.setAttribute("href", ppw.props.home + "editor/css/toolbar_all_" + (YAHOO.env.ua.ie ? "ie" : "w3c") + ".css");
    document.getElementsByTagName("head")[0].appendChild(cssElem);
    var opt = document.createElement("option");
    opt.text = "Vera Sans Ecofont";
    opt.value = "Spranq eco sans";
    if (!YAHOO.env.ua.webkit) {
        opt.style.fontFamily = "'Spranq eco sans'"
    }
    var selectM = document.getElementById("ppw_select_font_dropdown");
    try {
        selectM.add(opt, null)
    } catch (ex) {
        selectM.add(opt)
    }
};
PwylUI.prototype.trimUrl = function (idOfUrl) {
    var elem = document.getElementById(idOfUrl);
    elem.value = elem.value.strip();
    return true
};
PwylUI.prototype.removeFocus = function () {
    var inputs = ppw.util.top.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
        try {
            inputs[i].blur()
        } catch (e) {}
    }
};
PwylUI.prototype.setEnableUndoRedo = function (hasCommandsOnStack, hasUndoCommandsOnStack) {
    var undo = document.getElementById("ppw_undo_button");
    var undoSelect = document.getElementById("ppw_undo_select_button");
    var redo = document.getElementById("ppw_redo_button");
    var saveChangeSetLink = document.getElementById("ppw_save_to_changeset_link");
    var saveChangeSetWrapper = document.getElementById("ppw_save_to_changeset_link_wrapper");
    var changeSetSelection = document.getElementById("ppw_select_change_set_dropdown");
    if (hasCommandsOnStack) {
        undo.disabled = false;
        undoSelect.disabled = false;
        saveChangeSetLink.href = "javascript:ppw.auth.requiresAuthentication(ppw.ui.showSaveChangeSetForm)";
        saveChangeSetWrapper.className = saveChangeSetWrapper.className.replace(/disabled/g, "");
        changeSetSelection.disabled = true
    } else {
        undo.onmouseout();
        undo.disabled = true;
        undoSelect.onmouseout();
        undoSelect.disabled = true;
        saveChangeSetLink.href = "javascript:void(0)";
        if (saveChangeSetWrapper.className.indexOf("disabled") == -1) {
            saveChangeSetWrapper.className += " disabled"
        }
        changeSetSelection.disabled = false
    }
    if (hasUndoCommandsOnStack) {
        redo.disabled = false
    } else {
        redo.onmouseout();
        redo.disabled = true
    }
};
PwylUI.prototype.enableOrDisableButton = function (button_id, enable) {
    var button = document.getElementById(button_id);
    button.onmouseout();
    button.disabled = !enable
};
PwylUI.prototype.toggleButtons = function (to_check_button_id, to_uncheck_button_id) {
    ppw.util.$(to_uncheck_button_id).checked = "";
    ppw.util.$(to_check_button_id).checked = "checked"
};
PwylUI.prototype.disableRemoveBackground = function () {
    var hide_button = document.getElementById("ppw_background_hide");
    hide_button.onclick = null;
    hide_button.checked = "checked";
    var show_button = document.getElementById("ppw_background_show");
    show_button.checked = ""
};
PwylUI.prototype.enableRemoveBackground = function () {
    var hide_button = document.getElementById("ppw_background_hide");
    hide_button.onclick = function (event) {
        ppw.util.cursorWrapper("ppw.commands.removeBackground()")
    };
    hide_button.checked = "";
    var show_button = document.getElementById("ppw_background_show");
    show_button.checked = "checked"
};
PwylUI.prototype.disableRemoveImages = function () {
    var hide_button = document.getElementById("ppw_images_hide");
    hide_button.onclick = null;
    hide_button.checked = "checked";
    var show_button = document.getElementById("ppw_images_show");
    show_button.checked = ""
};
PwylUI.prototype.enableRemoveImages = function () {
    var hide_button = document.getElementById("ppw_images_hide");
    hide_button.onclick = function (event) {
        ppw.util.cursorWrapper("ppw.commands.removeImages()")
    };
    hide_button.checked = "";
    var show_button = document.getElementById("ppw_images_show");
    show_button.checked = "checked"
};
PwylUI.prototype.updateAddPageForm = function () {
    var applyChanges = document.getElementById("ppw_add_page_apply_changes");
    var applyChangesCheckbox = document.getElementById("ppw_add_page_apply_changes_checkbox");
    var url = document.getElementById("ppw_add_page_url_form_field").value;
    var urlHost = "";
    try {
        urlHost = new ppw.server.URLManager(url).host
    } catch (e) {}
    if (url && (ppw.server.pageUrl.host == urlHost)) {
        applyChanges.style.display = "inline";
        applyChangesCheckbox.disabled = false
    } else {
        applyChanges.style.display = "none";
        applyChangesCheckbox.disabled = true
    }
};
PwylUI.prototype.closeAllPanels = function () {
    var panels = [ppw.ui.addPagePanel, ppw.ui.newPagePanel, ppw.ui.saveAsPanel, ppw.ui.helpPanel, ppw.ui.undoListPanel, ppw.ui.pageHelpPanel, ppw.ui.ecofontHelpPanel, ppw.ui.changeSetHelpPanel, ppw.ui.accountRequiredPanel, ppw.ui.overQuotaPanel, ppw.ui.createPageHelpPanel, ppw.ui.completionMessagePanel];
    for (var i = 0; i < panels.length; i++) {
        if (panels[i] && panels[i].isDisplayed) {
            panels[i].hide()
        }
    }
};
PwylUI.prototype.addToUndoList = function (command) {
    var undoChangeLink = document.createElement("a");
    var changeIndex = ppw.changeset.doStack.length - 1;
    undoChangeLink.href = "javascript:ppw.changeset.undo(" + changeIndex + ")";
    undoChangeLink.target = "_self";
    undoChangeLink.innerHTML = command.name;
    var undoList = document.getElementById("ppw_undo_list");
    if (undoList.childNodes.length == 0) {
        undoList.appendChild(undoChangeLink)
    } else {
        undoList.insertBefore(undoChangeLink, undoList.firstChild)
    }
};
PwylUI.prototype.removeFromUndoList = function (changeId) {
    if (changeId) {
        changeId = ppw.changeset.doStack.length - changeId
    }
    changeId = changeId || 0;
    var undoList = document.getElementById("ppw_undo_list");
    var undoListElements = ppw.util.toArray(undoList.getElementsByTagName("a"));
    undoList.removeChild(undoListElements[changeId])
};
PwylUI.prototype.addToChangeList = function (command) {
    var newChange = document.createElement("li");
    var newChangeWrapper = document.createElement("span");
    newChangeWrapper.innerHTML = command.name;
    newChange.appendChild(newChangeWrapper);
    if (ppw.props.editChangeSet || ppw.props.editPrintLayout) {
        var changesList = document.getElementById("ppw_edit_change_set_changes");
        changesList.appendChild(newChange)
    }
};
PwylUI.prototype.removeFromChangeList = function (changeId) {
    var changesList = document.getElementById("ppw_edit_change_set_changes");
    var changesListElements = ppw.util.toArray(changesList.getElementsByTagName("li"));
    changeId = changeId || (changesListElements.length - 1);
    changesList.removeChild(changesListElements[changeId])
};
PwylUI.prototype.initializeProAccount = function () {
    if (ppw.props.editChangeSet) {
        var changeSetId = ppw.props.editChangeSet;
        ppw.acs.applyChangeSetForEditing(changeSetId)
    } else {
        if (ppw.props.editPrintLayout && typeof (ppw.props.editPrintLayout) == "number") {
            ppw.acs.applyChangeSetForEditing(ppw.props.editPrintLayout)
        } else {
            if (ppw.props.pageFromClips) {
                var clips = YAHOO.util.Dom.getElementsByClassName("ppw_clip", "div");
                for (i = 0; i < clips.length; i++) {
                    var clip = clips[i];
                    clip.onmousedown = ppw.pro.startDragOnClone
                }
            } else {
                if (ppw.props.proAccountId) {
                    ppw.acs.getChangeSetsForUrl()
                } else {
                    ppw.ui.hideChangeSetDropdown()
                }
            }
        }
    }
    if (ppw.props.proAccountId) {
        var logoLink = document.getElementById("ppw_logo_link");
        logoLink.href = logoLink.href + "dashboard"
    }
};
PwylUI.prototype.hideChangeSetDropdown = function () {
    var applyChangeSet = document.getElementById("ppw_apply_change_set");
    applyChangeSet.style.display = "none"
};
PwylUI.prototype.showSaveChangeSetForm = function () {
    if (ppw.props.proAccountId) {
        if (ppw.acs.appliedChangeSet) {
            ppw.acs.saveChangeSet(ppw.acs.appliedChangeSet)
        } else {
            var changeSetName = document.getElementById("ppw_change_set_name");
            if (changeSetName.value.length == 0) {
                var title = document.title.replace("PrintWhatYouLike on ", "");
                if (ppw.server.newChangeSetNum) {
                    title += " " + ppw.server.newChangeSetNum
                }
                changeSetName.value = title
            }
            ppw.ui.saveChangeSetPanel.toggle()
        }
    } else {
        ppw.ui.accountRequiredPanel.toggle()
    }
};
PwylUI.prototype.checkSaveChangeSetForm = function () {
    var changeSetName = document.getElementById((ppw.props.editChangeSet ? "ppw_edit_change_set_name" : "ppw_change_set_name"));
    if (changeSetName.value.length == 0) {
        alert("Please enter the name of the change set.");
        return false
    }
    var key = null,
        changeSetAutoApply = null,
        autoApply = "0";
    if (ppw.props.editChangeSet) {
        key = document.getElementById("ppw_edit_change_set_key").value;
        changeSetAutoApply = document.getElementById("ppw_edit_change_set_auto_apply_yes")
    } else {
        changeSetAutoApply = document.getElementById("ppw_change_set_auto_apply");
        ppw.ui.saveChangeSetPanel.toggle()
    }
    if (changeSetAutoApply.checked) {
        autoApply = "1"
    }
    ppw.acs.saveChangeSet(key, changeSetName.value, autoApply);
    return false
};
PwylUI.prototype.addInfoMessage = function (messageStr, alignRight) {
    var m = document.createElement("div");
    m.className = "ppw_info_message";
    m.innerHTML = messageStr;
    document.getElementById("ppw_info_panel").appendChild(m);
    if (alignRight) {
        var infoPanel = document.getElementById("ppw_info_panel");
        infoPanel.style.left = (YAHOO.util.Dom.getViewportWidth() - infoPanel.offsetWidth - 20) + "px";
        m.style.cssFloat = "right";
        m.style.styleFloat = "right"
    }
    var l = ppw.ui.saveVars.length;
    ppw.ui.saveVars[l] = m;
    return l
};
PwylUI.prototype.clearInfoMessage = function (l, fast) {
    if (fast) {
        ppw.util.removeElement(ppw.ui.saveVars[l])
    } else {
        ppw.panel.slideRemove(ppw.ui.saveVars[l])
    }
};
PwylUI.prototype.setSelfDestructingInfoMessage = function (msg, timeout) {
    if (!timeout) {
        timeout = 3000
    }
    var l = ppw.ui.addInfoMessage(msg);
    setTimeout("ppw.ui.clearInfoMessage(" + l + ");", timeout)
};
PwylUI.prototype.displayBookmarkletMessage = function () {
    var numVisits = YAHOO.util.Cookie.get("numVisits", Number);
    if (numVisits == null) {
        YAHOO.util.Cookie.set("numVisits", "1", {
            expires: new Date("January 1, 2025")
        })
    } else {
        numVisits += 1;
        if (numVisits == 3) {
            var bkmtMessage = document.getElementById("ppw_try_bkmt_message");
            bkmtMessage.style.display = ""
        }
        YAHOO.util.Cookie.set("numVisits", numVisits.toString(), {
            expires: new Date("January 1, 2025")
        })
    }
};
PwylUI.prototype.hideBookmarkletMessage = function () {
    var bkmtMessage = document.getElementById("ppw_try_bkmt_message");
    bkmtMessage.style.display = "none"
};

