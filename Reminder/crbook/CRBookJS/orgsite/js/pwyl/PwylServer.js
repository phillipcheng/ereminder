function PwylServer() {
    this.id = ppw.util.generateUniqueId();
    this.url = new this.URLManager(document.URL);
    this.pageUrl = new this.URLManager(ppw.props.pageUrl);
    this.jx = PwylLoadJx();
    this.htmlDelimiter = "--- 941587982 244932647 867248554 ---";
    this.waitingCallbacks = {}
}
PwylServer.prototype.ajaxCall = function (relUrl, params, onSuccess, method, opt) {
    if (!method) {
        method = "get"
    }
    if (ppw.bookmarklet || ppw.print_button) {
        if (method == "get") {
            ppw.server.getViaJs(relUrl, params, onSuccess)
        } else {
            ppw.server.postViaForm(relUrl, params, onSuccess)
        }
    } else {
        if (opt && opt.forceMultipart) {
            ppw.server.postViaForm(relUrl, params, onSuccess)
        } else {
            ppw.server.jx.load(ppw.server.getAbsUrl(relUrl, params), onSuccess, "text", method, opt)
        }
    }
};
PwylServer.prototype.postViaForm = function (relUrl, params, callback, defaultResponse) {
    var url = ppw.server.getAbsUrl(relUrl);
    if (!params) {
        params = {}
    }
    var formHtml = "<form id='ppw_save_data_form' method='post' target='ppw_hidden_frame' enctype='multipart/form-data' charset='utf-8' autocomplete='off'></form>						 <iframe name='ppw_hidden_frame' id='ppw_hidden_frame' onload='ppw.server._postViaForm()'></iframe>";
    if (document.getElementById("ppw_save_data_form_iframe")) {
        ppw.util.removeElement("ppw_save_data_form_iframe")
    }
    var div = document.createElement("div");
    div.id = "ppw_save_data_form_iframe";
    div.innerHTML = formHtml;
    document.getElementById("ppw_dock").appendChild(div);
    if (!ppw.bookmarklet) {
        params.callbackId = ppw.server.waitlistCallback(callback)
    }
    var i, field, form = document.getElementById("ppw_save_data_form");
    form.action = url;
    for (var key in params) {
        field = document.createElement("input");
        field.type = "hidden";
        field.name = key;
        field.value = params[key];
        form.appendChild(field)
    }
    form.submit();
    if (ppw.bookmarklet) {
        callback(defaultResponse)
    }
};
PwylServer.prototype._postViaForm = function () {
    if (ppw.bookmarklet) {
        return
    }
    var ifr = ppw.loader.getIFrame("ppw_hidden_frame");
    var params;
    if (ifr.win.response_loaded) {
        ifr.win.response_loaded = false;
        params = ifr.win.response_args;
        if (params.callbackId) {
            var callback = ppw.server.retrieveCallback(params.callbackId);
            if (callback) {
                callback(params.response, params)
            }
        }
    }
};
PwylServer.prototype.getViaJs = function (relUrl, params, callback) {
    var id = ppw.server.waitlistCallback(callback);
    if (!params) {
        params = {}
    }
    params.callbackId = id;
    params.js = 1;
    var js = document.createElement("script");
    js.setAttribute("type", "text/javascript");
    js.setAttribute("src", ppw.server.getAbsUrl(relUrl, params));
    js.setAttribute("pwyl", "true");
    document.getElementsByTagName("head")[0].appendChild(js)
};
PwylServer.prototype._handleJsResponse = function (id, response, args) {
    if (response) {
        response = response.replace(/&lt;/g, "<");
        response = response.replace(/&gt;/g, ">")
    }
    var callback = ppw.server.retrieveCallback(id);
    if (callback) {
        callback(response, args)
    }
};
PwylServer.prototype.getAbsUrl = function (relUrl, params) {
    if (relUrl.charAt(0) == "/") {
        relUrl = relUrl.slice(1)
    }
    var absUrl = ppw.props.home + relUrl;
    var paramStr = ppw.server.parametersToString(params);
    if (paramStr.length > 0) {
        absUrl += (absUrl.indexOf("?") > -1) ? "&" : "?";
        absUrl += paramStr
    }
    return absUrl
};
PwylServer.prototype.parametersToString = function (params) {
    var isFirst = true;
    var paramsStr = "";
    for (var key in params) {
        if (isFirst) {
            isFirst = false
        } else {
            paramsStr += "&"
        }
        paramsStr += key + "=" + params[key]
    }
    return paramsStr
};
PwylServer.prototype.waitlistCallback = function (callback) {
    var curr = new Date().getTime();
    ppw.server.waitingCallbacks[curr] = callback;
    return curr
};
PwylServer.prototype.retrieveCallback = function (id) {
    return ppw.server.waitingCallbacks[id]
};
PwylServer.prototype.URLManager = function (url) {
    this.url = url;
    var hna = this.url.match(/^(http\:\/\/)?([\S]+?)(\/.*)?$/i);
    this.hostName = hna[hna.length - 2];
    this.host = "http://" + this.hostName;
    this.parameters = [];
    this.encodedUrl = "";
    if (this.url.indexOf("?") >= 0) {
        var results = this.url.match(/^.*\?(.*)$/i);
        var pairs = results[1].split("&");
        for (var i = 0; i < pairs.length; i++) {
            var keyValue = pairs[i].split("=");
            this.parameters[keyValue[0]] = keyValue[1]
        }
    }
    this.encodedUrl = encodeURIComponent(this.url)
};

