
function pwylInitialize() {
    window.ppw = new Object();
    window.ppw.util = new PwylUtil();
    window.ppw.props = new PwylProperties();
    window.ppw.cssutil = new PwylCssUtil();
    window.ppw.commands = new PwylCommands();
    window.ppw.multicommand = new PwylMultiCommand();
    window.ppw.editor = new PwylEditor();
    window.ppw.drag = new PwylDragSelect();
    window.ppw.changeset = new PwylChangeSet();
    window.ppw.server = new PwylServer();
    window.ppw.ui = new PwylUI();
    window.ppw.panel = new PwylPanel();
    window.ppw.loader = new PwylLoader();
    window.ppw.auto = new PwylAuto();
    window.ppw.paths = new PwylPaths();
    window.ppw.init = new PwylInitializer();
    window.ppw.auth = new PwylAuth();
    window.ppw.acs = new PwylApplyChangeSet();
    window.ppw.ads = new PwylExtApis();
    window.ppw.savings = new PwylSavings();
    if (window.ppw.props.pageFromClips) {
        window.ppw.pro = new PwylPro()
    }
}

function pwylInitializeWithEditor() {
    pwylInitialize();
    ppw.init.onEditorLoad();
    ppw.init.afterPageLoad()
}

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

function PwylLoadJx() {
    return {
        http: false,
        format: "text",
        callback: function (data) {},
        handler: false,
        error: false,
        opt: new Object(),
        getHTTPObject: function () {
            var http = false;
            if (typeof ActiveXObject != "undefined") {
                try {
                    http = new ActiveXObject("Msxml2.XMLHTTP")
                } catch (e) {
                    try {
                        http = new ActiveXObject("Microsoft.XMLHTTP")
                    } catch (E) {
                        http = false
                    }
                }
            } else {
                if (window.XMLHttpRequest) {
                    try {
                        http = new XMLHttpRequest()
                    } catch (e) {
                        http = false
                    }
                }
            }
            return http
        },
        load: function (url, callback, format, method, opt) {
            var http = this.init();
            if (!http || !url) {
                return
            }
            if (http.overrideMimeType) {
                http.overrideMimeType("text/xml")
            }
            if (!method) {
                method = "GET"
            }
            if (!format) {
                format = "text"
            }
            if (!opt) {
                opt = {}
            }
            format = format.toLowerCase();
            method = method.toUpperCase();
            var now = "uid=" + new Date().getTime();
            url += (url.indexOf("?") + 1) ? "&" : "?";
            url += now;
            var parameters = null;
            if (method == "POST") {
                var parts = url.split("?");
                url = parts[0];
                parameters = parts[1]
            }
            http.open(method, url, true);
            if (method == "POST") {
                http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                http.setRequestHeader("Content-length", parameters.length);
                http.setRequestHeader("Connection", "close")
            }
            var ths = this;
            if (opt.handler) {
                http.onreadystatechange = function () {
                    opt.handler(http)
                }
            } else {
                http.onreadystatechange = function () {
                    if (http.readyState == 4) {
                        if (http.status == 200) {
                            var result = "";
                            if (http.responseText) {
                                result = http.responseText
                            }
                            if (format.charAt(0) == "j") {
                                result = result.replace(/[\n\r]/g, "");
                                result = eval("(" + result + ")")
                            } else {
                                if (format.charAt(0) == "x") {
                                    result = http.responseXML
                                }
                            }
                            if (callback) {
                                callback(result)
                            }
                        } else {
                            if (opt.loadingIndicator) {
                                document.getElementsByTagName("body")[0].removeChild(opt.loadingIndicator)
                            }
                            if (opt.loading) {
                                document.getElementById(opt.loading).style.display = "none"
                            }
                            if (error) {
                                error(http.status)
                            }
                        }
                    }
                }
            }
            http.send(parameters)
        },
        bind: function (user_options) {
            var opt = {
                url: "",
                onSuccess: false,
                onError: false,
                format: "text",
                method: "GET",
                update: "",
                loading: "",
                loadingIndicator: ""
            };
            for (var key in opt) {
                if (user_options[key]) {
                    opt[key] = user_options[key]
                }
            }
            if (!opt.url) {
                return
            }
            var div = false;
            if (opt.loadingIndicator) {
                div = document.createElement("div");
                div.setAttribute("style", "position:absolute;top:0px;left:0px;");
                div.setAttribute("class", "loading-indicator");
                div.innerHTML = opt.loadingIndicator;
                document.getElementsByTagName("body")[0].appendChild(div);
                this.opt.loadingIndicator = div
            }
            if (opt.loading) {
                document.getElementById(opt.loading).style.display = "block"
            }
            this.load(opt.url, function (data) {
                if (opt.onSuccess) {
                    opt.onSuccess(data)
                }
                if (opt.update) {
                    document.getElementById(opt.update).innerHTML = data
                }
                if (div) {
                    document.getElementsByTagName("body")[0].removeChild(div)
                }
                if (opt.loading) {
                    document.getElementById(opt.loading).style.display = "none"
                }
            }, opt.format, opt.method, opt)
        },
        init: function () {
            return this.getHTTPObject()
        }
    }
};

Array.prototype.each = function (callback) {
    for (var i = 0; i < this.length; i++) {
        callback(this[i])
    }
};
Array.prototype.remove = function (object) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == object) {
            this.splice(i, 1);
            return object
        }
    }
    return false
};
Array.prototype.indexOf = function (object) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == object) {
            return i
        }
    }
    return -1
};
Array.prototype.contains = function (object) {
    return this.indexOf(object) > -1
};
Array.prototype.firstMatch = function (toArray) {
    for (var i = 0; i < this.length; i++) {
        for (var j = 0; j < toArray.length; j++) {
            if (this[i] == toArray[j]) {
                return toArray[j]
            }
        }
    }
};
Array.prototype.between = function (start, end) {
    var reachedStart = false;
    var betweenNodes = [];
    for (var i = 0; i < this.length; i++) {
        if (!reachedStart) {
            if (start == this[i]) {
                reachedStart = true
            }
        } else {
            if (this[i] == end) {
                return betweenNodes
            } else {
                betweenNodes.push(this[i])
            }
        }
    }
    return betweenNodes
};
Array.prototype.concatArray = function () {
    for (var i = 0; i < arguments.length; i++) {
        for (var j = 0; j < arguments[i].length; j++) {
            this.push(arguments[i][j])
        }
    }
};
String.prototype.camelize = function () {
    var parts = this.split("-"),
        len = parts.length;
    if (len == 1) {
        return parts[0]
    }
    var camelized = this.charAt(0) == "-" ? parts[0].charAt(0).toUpperCase() + parts[0].substring(1) : parts[0];
    for (var i = 1; i < len; i++) {
        camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1)
    }
    return camelized
};
String.prototype.startsWith = function (pattern) {
    return this.indexOf(pattern) === 0
};
String.prototype.strip = function () {
    return this.replace(/^\s+/, "").replace(/\s+$/, "")
};
if (!window.console) {
    window.console = {
        log: function (str) {}
    }
}
if (!window.Node) {
    window.Node = new Object();
    Node.ELEMENT_NODE = 1;
    Node.ATTRIBUTE_NODE = 2;
    Node.TEXT_NODE = 3;
    Node.CDATA_SECTION_NODE = 4;
    Node.ENTITY_REFERENCE_NODE = 5;
    Node.ENTITY_NODE = 6;
    Node.PROCESSING_INSTRUCTION_NODE = 7;
    Node.COMMENT_NODE = 8;
    Node.DOCUMENT_NODE = 9;
    Node.DOCUMENT_TYPE_NODE = 10;
    Node.DOCUMENT_FRAGMENT_NODE = 11;
    Node.NOTATION_NODE = 12
};

function PwylSavings() {}
PwylSavings.prototype.recordSavings = function () {
    var modified_doc_height = YAHOO.util.Dom.getDocumentHeight();
    var params = {
        key: ppw.server.id,
        original_height: ppw.ui.orig_doc_height,
        modified_height: modified_doc_height
    };
    if (ppw.props.proAccountId) {
        params.user_id = ppw.props.proAccountId
    }
    ppw.server.ajaxCall("/savings/save", params, null, "get", {
        handler: function (http) {}
    });
    ppw.util.sleep(100)
};