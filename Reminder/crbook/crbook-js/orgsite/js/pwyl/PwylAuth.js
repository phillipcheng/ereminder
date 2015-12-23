function PwylAuth() {}
PwylAuth.prototype.requiresAuthentication = function (callback) {
    ppw.ui.saveAsPanel.hide();
    if (!ppw.props.proAccountId) {
        ppw.auth.stashedFunction = callback;
        ppw.ui.accountRequiredPanel.show()
    } else {
        callback()
    }
};
PwylAuth.prototype.afterAuthenticationContinueExecution = function () {
    ppw.ui.accountRequiredPanel.hide();
    if (ppw.auth.stashedFunction) {
        setTimeout("ppw.auth._afterAuthenticationContinueExecution()", 0)
    }
};
PwylAuth.prototype._afterAuthenticationContinueExecution = function () {
    ppw.auth.stashedFunction();
    ppw.auth.stashedFunction = null
};
PwylAuth.prototype.releaseQuotas = function () {
    ppw.props.enforceQuotas = false
};
PwylAuth.prototype.attemptLogin = function () {
    ppw.auth.showWaitMsg();
    params = {
        "user_session[email]": YAHOO.util.Dom.get("user_session_email").value,
            "user_session[password]": YAHOO.util.Dom.get("user_session_password").value,
        source: "bookmarklet"
    };
    var onComplete = function (response, params) {
        response ? ppw.auth.successfulLogin(params) : ppw.auth.unsuccessfulLogin(params)
    };
    ppw.server.ajaxCall("/user_sessions/bookmarklet_login", params, onComplete, "get")
};
PwylAuth.prototype.successfulLogin = function (params) {
    ppw.props.proAccountId = params.pro_id;
    YAHOO.util.Dom.get("ppw_bookmarklet_login_new").style.display = "none";
    YAHOO.util.Dom.get("ppw_bookmarklet_login_success").style.display = "block";
    YAHOO.util.Dom.get("ppw_bookmarklet_login_success_full_name").innerHTML = "Welcome " + params.full_name + "!"
};
PwylAuth.prototype.unsuccessfulLogin = function (params) {
    ppw.auth.stopWaitMsg();
    YAHOO.util.Dom.get("ppw_bookmarklet_login_failure").style.display = "block"
};
PwylAuth.prototype.showWaitMsg = function () {
    YAHOO.util.Dom.get("ppw_bookmarklet_login_new_buttons").style.display = "none";
    YAHOO.util.Dom.get("ppw_bookmarklet_login_new_wait").style.display = "block"
};
PwylAuth.prototype.stopWaitMsg = function () {
    YAHOO.util.Dom.get("ppw_bookmarklet_login_new_wait").style.display = "none";
    YAHOO.util.Dom.get("ppw_bookmarklet_login_new_buttons").style.display = "block"
};

