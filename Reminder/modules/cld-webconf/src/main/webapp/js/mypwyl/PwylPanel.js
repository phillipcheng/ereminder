function PwylPanel() {
    this.slideRemoveVars = []
}
PwylPanel.prototype.Panel = function (elemId, anchorId) {
    this.anchor = document.getElementById(anchorId);
    this.panelElem = document.getElementById(elemId);
    this.isDisplayed = false;
    this.toggle = function () {
        if (this.isDisplayed) {
            this.hide()
        } else {
            var posAnchor = YAHOO.util.Dom.getXY(this.anchor);
            var toolbar = ppw.util.getFirstParentWithClass(this.anchor, "ppw_toolbar_content");
            var scrollY = ppw.editor.getScrollPosition().y;
            var relH = ppw.util.getOffsetHeight(this.anchor, toolbar);
            var absH = relH - ppw.editor.getScrollOffset(toolbar).y + scrollY;
            this.panelElem.style.top = (absH + this.anchor.offsetHeight) + "px";
            this.panelElem.style.left = posAnchor[0] + "px";
            this.panelElem.style.display = "block";
            if ((relH + this.anchor.offsetHeight + this.panelElem.offsetHeight) > YAHOO.util.Dom.getViewportHeight()) {
                if ((relH - this.panelElem.offsetHeight) < 0) {
                    this.panelElem.style.top = scrollY + "px"
                } else {
                    this.panelElem.style.top = (absH - this.panelElem.offsetHeight) + "px"
                }
                this.panelElem.style.left = posAnchor[0] + this.anchor.offsetWidth + "px"
            }
            if ((posAnchor[0] + this.anchor.offsetWidth + this.panelElem.offsetWidth) > YAHOO.util.Dom.getViewportWidth()) {
                this.panelElem.style.left = (posAnchor[0] - this.panelElem.offsetWidth) + "px"
            }
            this.isDisplayed = true
        }
    };
    this.hide = function () {
        this.panelElem.style.display = "none";
        this.isDisplayed = false
    }
};
PwylPanel.prototype.MessagePanel = function (panelId) {
    this.panelElem = document.getElementById(panelId);
    this.isDisplayed = false;
    this.toggle = function () {
        this.isDisplayed ? this.hide() : this.show()
    };
    this.show = function () {
        var lightbox = document.getElementById("ppw_lightbox");
        lightbox.style.left = "0";
        lightbox.style.width = YAHOO.util.Dom.getDocumentWidth() + "px";
        lightbox.style.height = YAHOO.util.Dom.getDocumentHeight() + "px";
        lightbox.style.display = "block";
        this.panelElem.style.display = "block";
        var panelLeft = ((YAHOO.util.Dom.getViewportWidth() - this.panelElem.offsetWidth) / 2) + ppw.editor.getScrollPosition().x;
        var panelTop = ((YAHOO.util.Dom.getViewportHeight() - this.panelElem.offsetHeight) / 2) + ppw.editor.getScrollPosition().y - 50;
        ppw.util.moveElement(this.panelElem, panelLeft, panelTop);
        this.isDisplayed = true
    };
    this.hide = function () {
        this.panelElem.style.display = "none";
        document.getElementById("ppw_lightbox").style.display = "none";
        this.isDisplayed = false;
        ppw.editor.changeCursorStyle("auto")
    }
};
PwylPanel.prototype.IFramePanel = function (url, attachPointId, panelId, regularPanel) {
    var messagePanel, ifr = false;
    this.displaying = false;
    this.toggle = function () {
        this.displaying ? this.hide() : this.show()
    }, this.show = function () {
        ifr = document.createElement("iframe");
        ifr.src = url;
        document.getElementById(attachPointId).appendChild(ifr);
        ifr.style.height = (YAHOO.util.Dom.getViewportHeight() * 0.7) + "px";
        if (regularPanel) {
            messagePanel = new ppw.panel.Panel(panelId, panelId + "_link")
        } else {
            messagePanel = new ppw.panel.MessagePanel(panelId)
        }
        messagePanel.toggle();
        this.displaying = true
    }, this.hide = function () {
        ifr.parentNode.removeChild(ifr);
        messagePanel.hide();
        messagePanel = null;
        this.displaying = false
    }
};
PwylPanel.prototype.slideRemove = function (elem) {
    elem.style.overflow = "hidden";
    var l = ppw.panel.slideRemoveVars.length;
    ppw.panel.slideRemoveVars[l] = {};
    ppw.panel.slideRemoveVars[l].elem = elem;
    ppw.panel.slideRemoveVars[l].id = setInterval("ppw.panel._slideRemoveIncrement(" + l + ")", 20)
};
PwylPanel.prototype._slideRemoveIncrement = function (l) {
    var inc = 1;
    var elem = ppw.panel.slideRemoveVars[l].elem;
    if (!elem.style.height) {
        elem.style.height = (elem.offsetHeight - ppw.util.getStyle(elem, "paddingTop") - ppw.util.getStyle(elem, "paddingBottom") - ppw.util.getStyle(elem, "borderTopWidth") - ppw.util.getStyle(elem, "borderBottomWidth")) + "px"
    }
    if (ppw.util.getStyle(elem, "paddingBottom") >= inc) {
        elem.style.paddingBottom = (ppw.util.getStyle(elem, "paddingBottom") - inc) + "px"
    } else {
        if (ppw.util.getStyle(elem, "height") >= inc) {
            elem.style.paddingBottom = "0px";
            elem.style.height = (ppw.util.getStyle(elem, "height") - inc) + "px"
        } else {
            if (ppw.util.getStyle(elem, "paddingTop") >= inc) {
                elem.style.height = "0px";
                elem.style.paddingTop = (ppw.util.getStyle(elem, "paddingTop") - inc) + "px"
            } else {
                clearInterval(ppw.panel.slideRemoveVars[l].id);
                elem.parentNode.removeChild(elem);
                ppw.panel.slideRemoveVars[l] = null
            }
        }
    }
};

