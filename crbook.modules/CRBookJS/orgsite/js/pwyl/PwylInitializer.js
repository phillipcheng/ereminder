function PwylInitializer() {}
PwylInitializer.prototype.stopToolbar = function () {
    ppw.init.onEditorUnload();
    ppw.loader.removePwylFrame(document)
};
PwylInitializer.prototype.onEditorLoad = function () {
    ppw.ads.initializeAnalytics();
    ppw.ads.initializeToolbarAd();
    ppw.util.initializeEditorComponents();
    ppw.editor.initializeEditorComponents();
    ppw.drag.initializeDragSelect();
    ppw.ui.instantiateUI();
    document.getElementById("ppw_add_page_url_form_field").value = ppw.server.pageUrl.url;
    document.getElementById("ppw_new_page_url_form_field").value = ppw.server.pageUrl.url;
    ppw.ui.updateHelpMenuForMac();
    ppw.ui.addFontFace();
    ppw.ui.setPageWidth();
    ppw.ui.initializeProAccount();
    _pgzpInitPwyl()
};
PwylInitializer.prototype.onEditorUnload = function () {
    ppw.editor.unInitializeHandlers();
    ppw.editor.applyOnMouseOverToAllCoverElements(null);
    ppw.cssutil.restoreCssHoverRules()
};
PwylInitializer.prototype.afterPageLoad = function () {
    ppw.editor.initializeHandlers();
    ppw.cssutil.removeCssHoverRules();
    ppw.ui.setOverflow();
    ppw.ui.setBackgroundColor();
    ppw.editor.initializeCoverBoxes();
    ppw.editor.fixCssMediaTypes(document.getElementsByName("head")[0]);
    ppw.ui.addPageNormal();
    if (ppw.ui.orig_doc_height == null) {
        ppw.ui.orig_doc_height = YAHOO.util.Dom.getDocumentHeight();
        ppw.ui.before_add_page_height = ppw.ui.orig_doc_height
    }
    if (!ppw.props.editChangeSet && !ppw.props.pageFromClips) {
        YAHOO.util.Event.addListener(window, "beforeunload", ppw.savings.recordSavings)
    }
    if (ppw.props.proAccountId && !ppw.props.editChangeSet && !ppw.props.pageFromClips) {
        setTimeout("ppw.acs.autoApplyChangeSet()", 200)
    }
    var printButton = parent.document.getElementById("printer_layouts_new");
    if (!ppw.bookmarklet && (printButton == null) && !ppw.props.editChangeSet && !ppw.props.pageFromClips) {
        ppw.ui.displayBookmarkletMessage()
    }
};
PwylInitializer.prototype.afterRedraw = function () {
    ppw.editor.redrawHoverBox();
    ppw.editor.redrawSelections()
};
PwylInitializer.prototype.beforeAction = function () {
    ppw.editor.clearHover()
};
PwylInitializer.prototype.afterAction = function () {
    ppw.ui.setEnableUndoRedo((ppw.changeset.doStack.length > 0), (ppw.changeset.undoStack.length > 0));
    ppw.editor.changeCursorStyle("auto");
    ppw.ui.setPageWidth();
    ppw.init.afterRedraw()
};
PwylInitializer.prototype.beforeDo = function () {
    if (!ppw.print_button) {
        ppw.init.beforeAction()
    }
};
PwylInitializer.prototype.afterDo = function () {
    if (!ppw.print_button) {
        ppw.init.afterAction()
    }
};
PwylInitializer.prototype.beforeUndo = function () {
    if (!ppw.print_button) {
        ppw.init.beforeAction()
    }
};
PwylInitializer.prototype.afterUndo = function () {
    if (!ppw.print_button) {
        ppw.init.afterAction()
    }
};
PwylInitializer.prototype.onSelect = function () {};
PwylInitializer.prototype.onUnselect = function () {};