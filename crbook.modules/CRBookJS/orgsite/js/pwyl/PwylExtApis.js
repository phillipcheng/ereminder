function PwylExtApis() {
    this.is_local = (ppw.props.home.indexOf("localhost") >= 0)
}
PwylExtApis.prototype.initializeToolbarAd = function (messageId) {
    if (ppw.ads.is_local || ppw.bookmarklet || ppw.print_button) {
        var ads = document.getElementsByClassName("ppw_toolbar_adver");
        for (var i = 0; i < ads.length; i++) {
            ads[i].style.display = "none"
        }
        return
    }
    var anchor = document.getElementById("ppw_ad_toolbar_anchor");
    if (anchor.childNodes.length > 0) {
        return
    }
    var ad_src = "chitika_toolbar.html";
    var ad_width = "200";
    var ad_height = "200";
    anchor.innerHTML = "<iframe id='ppw_ad_toolbar_container' src='" + ppw.props.home + "editor/ads/" + ad_src + "' style='width: " + ad_width + "px; height: " + ad_height + "px; margin: 0 0 0 3px;' class='ppw_ad_iframe' scrolling='no' frameBorder='0'></iframe>"
};
PwylExtApis.prototype.initializeAnalytics = function () {
    if (ppw.bookmarklet || ppw.ads.is_local) {
        return
    }
    var editor_src;
    if (ppw.bookmarklet) {
        editor_src = "bookmarklet"
    } else {
        if (ppw.print_button) {
            editor_src = "print_button"
        } else {
            editor_src = "editor"
        }
    }
    var _gaq = _gaq || [];
    _gaq.push(["_setAccount", "UA-5329881-1"]);
    _gaq.push(["_setCustomVar", 1, "pwyl_src", editor_src, 3]);
    _gaq.push(["_trackPageview"]);
    (function () {
        var ga = document.createElement("script");
        ga.type = "text/javascript";
        ga.async = true;
        ga.src = ("https:" == document.location.protocol ? "https://ssl" : "http://www") + ".google-analytics.com/ga.js";
        (document.getElementsByTagName("head")[0] || document.getElementsByTagName("body")[0]).appendChild(ga)
    })()
};