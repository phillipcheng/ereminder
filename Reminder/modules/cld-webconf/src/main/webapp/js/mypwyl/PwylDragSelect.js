
function PwylDragSelect() {
    this.mouseDown = false;
    this.isStartOfDrag = true;
    this.startCoords = {
        x: 0,
        y: 0
    };
    this.moveCoords = {
        x: 0,
        y: 0
    };
    this.selectBox;
    this.isProcessing = false
}
PwylDragSelect.prototype.initializeDragSelect = function () {
    if (document.body.attachEvent) {
        ppw.util.top.attachEvent("onmousedown", ppw.drag.onMouseDown);
        document.body.attachEvent("onmouseup", ppw.drag.onMouseUp)
    } else {
        ppw.util.top.onmousedown = this.onMouseDown;
        document.onmouseup = this.onMouseUp
    }
    this.selectBox = document.getElementById("ppw_hover_border_drag");
    if (typeof ppw.util.top.onselectstart != "undefined") {
        ppw.util.top.onselectstart = function () {
            return false
        }
    } else {
        if (typeof ppw.util.top.style.MozUserSelect != "undefined") {
            ppw.util.top.style.MozUserSelect = "none"
        }
    }
};
PwylDragSelect.prototype.onMouseDown = function (event) {
    event = ppw.util.captureEvent(event);
    if (ppw.drag.isPointerButton(event)) {
        ppw.drag.mouseDown = true;
        ppw.drag.startCoords = ppw.editor.getMousePosition(event);
        if (document.body.attachEvent) {
            document.body.attachEvent("onmousemove", ppw.drag.onMouseMove)
        } else {
            document.onmousemove = ppw.drag.onMouseMove
        }
        return false
    }
};
PwylDragSelect.prototype.onMouseMove = function (event) {
    if (ppw.drag.mouseDown) {
        event = ppw.util.captureEvent(event);
        var currCoords = ppw.editor.getMousePosition(event);
        if (Math.abs(currCoords.x - ppw.drag.startCoords.x) > 5 || Math.abs(currCoords.y - ppw.drag.startCoords.y) > 5) {
            if (currCoords.x < 212) {
                currCoords.x = 212
            }
            if (currCoords.y < 0) {
                currCoords.y = 0
            }
            var rightEdge = Math.max(ppw.util.pageTop.offsetWidth, YAHOO.util.Dom.getViewportWidth());
            var bottomEdge = YAHOO.util.Dom.getViewportHeight() + ppw.editor.getScrollPosition().y - 4;
            if (currCoords.x > rightEdge) {
                currCoords.x = rightEdge - 4
            }
            if (currCoords.y > bottomEdge) {
                currCoords.y = bottomEdge - 4
            }
            if (ppw.drag.isStartOfDrag) {
                ppw.drag.isStartOfDrag = false;
                ppw.editor.clearHover(event);
                ppw.editor.hoverElementsActive = false;
                ppw.drag.selectBox.style.display = "block"
            }
            if (!ppw.drag.isProcessing && (Math.abs(currCoords.x - ppw.drag.moveCoords.x) > 10 || Math.abs(currCoords.y - ppw.drag.moveCoords.y) > 10)) {
                ppw.drag.moveCoords = currCoords;
                ppw.drag.isProcessing = true;
                setTimeout("ppw.drag.selectElementsInSelectionBox();", 0)
            }
            ppw.drag.checkForScrolling(currCoords, {
                x: event.clientX,
                y: event.clientY
            });
            ppw.util.moveElement(ppw.drag.selectBox, Math.min(ppw.drag.startCoords.x, currCoords.x), Math.min(ppw.drag.startCoords.y, currCoords.y));
            ppw.drag.selectBox.style.width = Math.abs(currCoords.x - ppw.drag.startCoords.x) + "px";
            ppw.drag.selectBox.style.height = Math.abs(currCoords.y - ppw.drag.startCoords.y) + "px";
            return false
        }
    }
};
PwylDragSelect.prototype.onMouseUp = function (event) {
    event = ppw.util.captureEvent(event);
    var currCoords = ppw.editor.getMousePosition(event);
    if (ppw.drag.mouseDown && ppw.drag.isPointerButton(event)) {
        ppw.drag.mouseDown = false;
        ppw.drag.isStartOfDrag = true;
        ppw.editor.hoverElementsActive = true;
        ppw.drag.scrollAmt = null;
        if (document.body.detachEvent) {
            document.body.detachEvent("onmousemove", ppw.drag.onMouseMove)
        } else {
            document.onmousemove = null
        }
        if (Math.abs(currCoords.x - ppw.drag.startCoords.x) > 5 || Math.abs(currCoords.y - ppw.drag.startCoords.y) > 5) {
            ppw.drag.selectBox.style.display = "none";
            ppw.drag.selectBox.style.width = 0;
            ppw.drag.selectBox.style.height = 0;
            for (var i = 0; i < ppw.editor.selectedElems.length; i++) {
                ppw.editor.selectedElems[i].isDragSelect = null
            }
            ppw.editor.disableSelecting = true;
            setTimeout("ppw.editor.disableSelecting=false", 100)
        }
    }
};
PwylDragSelect.prototype.selectElementsInSelectionBox = function () {
    var i, curr;
    var selectedElems = ppw.drag.getElementsInSelectionBox();
    for (i = 0; i < ppw.editor.selectedElems.length; i++) {
        curr = ppw.editor.selectedElems[i];
        if (curr.isDragSelect && !selectedElems.contains(curr)) {
            ppw.editor.unSelect(curr);
            curr.isDragSelect = null;
            i--
        }
    }
    for (i = 0; i < selectedElems.length; i++) {
        selectedElems[i].isDragSelect = true;
        if (!selectedElems[i].isSelected) {
            ppw.editor.select(selectedElems[i])
        }
    }
    ppw.drag.isProcessing = false
};
PwylDragSelect.prototype.getElementsInSelectionBox = function () {
    var selectedElems = [];
    var addIfSelected = function (currElem) {
        var currElemPos = YAHOO.util.Dom.getXY(currElem);
        if (currElem.offsetHeight != 0 && ppw.editor.outside(ppw.drag.selectBox, currElem, selectBoxPos, currElemPos)) {
            return false
        }
        if (ppw.drag.inSelectBox(currElem, currElemPos, selectBoxPos) && ppw.drag.isSelectable(currElem)) {
            selectedElems.push(currElem);
            return false
        }
        return true
    };
    var selectBoxPos = YAHOO.util.Dom.getXY(ppw.drag.selectBox);
    ppw.util.topDownDepthFirstRecursion(ppw.util.pageTop, addIfSelected, null, true);
    return selectedElems
};
PwylDragSelect.prototype.isSelectable = function (elem) {
    return elem.offsetWidth > 0 && elem.offsetHeight > 0
};
PwylDragSelect.prototype.hasCornerInSelectionBox = function (elem) {
    var posA = YAHOO.util.Dom.getXY(elem);
    var pos = {
        x: posA[0],
        y: posA[1]
    };
    var sbPosA = YAHOO.util.Dom.getXY(ppw.drag.selectBox);
    var sbPos = {
        x: sbPosA[0],
        y: sbPosA[1]
    };
    return ppw.drag.isPointInSelectionBox(pos, sbPos) || ppw.drag.isPointInSelectionBox({
        x: pos.x,
        y: (pos.y + elem.offsetHeight)
    }, sbPos) || ppw.drag.isPointInSelectionBox({
        x: (pos.x + elem.offsetWidth),
        y: pos.y
    }, sbPos) || ppw.drag.isPointInSelectionBox({
        x: (pos.x + elem.offsetWidth),
        y: (pos.y + elem.offsetHeight)
    }, sbPos)
};
PwylDragSelect.prototype.isPointInSelectionBox = function (pointPos, sbPos) {
    var leeway = 0;
    return (sbPos.x - leeway) <= pointPos.x && pointPos.x <= (sbPos.x + ppw.drag.selectBox.offsetWidth + leeway) && (sbPos.y - leeway) <= pointPos.y && pointPos.y <= (sbPos.y + ppw.drag.selectBox.offsetHeight + leeway)
};
PwylDragSelect.prototype.inSelectBox = function (currElem, currElemPos, selectBoxPos) {
    return ppw.editor.inside(currElem, ppw.drag.selectBox, currElemPos, selectBoxPos) || ppw.drag.hasEdgeCrossing(currElem, ppw.drag.selectBox, currElemPos, selectBoxPos)
};
PwylDragSelect.prototype.hasEdgeCrossing = function (currElem, selectBox, currElemPos, selectBoxPos) {
    var ceul = {
        x: currElemPos[0],
        y: currElemPos[1]
    };
    var ceur = {
        x: (currElemPos[0] + currElem.offsetWidth),
        y: currElemPos[1]
    };
    var cebl = {
        x: currElemPos[0],
        y: (currElemPos[1] + currElem.offsetHeight)
    };
    var cebr = {
        x: (currElemPos[0] + currElem.offsetWidth),
        y: (currElemPos[1] + currElem.offsetHeight)
    };
    var sbul = {
        x: selectBoxPos[0],
        y: selectBoxPos[1]
    };
    var sbur = {
        x: (selectBoxPos[0] + selectBox.offsetWidth),
        y: selectBoxPos[1]
    };
    var sbbl = {
        x: selectBoxPos[0],
        y: (selectBoxPos[1] + selectBox.offsetHeight)
    };
    var sbbr = {
        x: (selectBoxPos[0] + selectBox.offsetWidth),
        y: (selectBoxPos[1] + selectBox.offsetHeight)
    };
    var edgeCrossings = 0;
    if (ppw.drag.doEdgesCross([ceul, ceur], [sbul, sbbl])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([ceul, ceur], [sbur, sbbr])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([cebl, cebr], [sbul, sbbl])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([cebl, cebr], [sbur, sbbr])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([ceul, cebl], [sbul, sbur])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([ceul, cebl], [sbbl, sbbr])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([ceur, cebr], [sbul, sbur])) {
        edgeCrossings++
    }
    if (ppw.drag.doEdgesCross([ceur, cebr], [sbbl, sbbr])) {
        edgeCrossings++
    }
    return edgeCrossings >= 3
};
PwylDragSelect.prototype.doEdgesCross = function (a, b) {
    return ppw.drag.doEdgesCrossOneDimension([a[0].x, a[1].x], [b[0].x, b[1].x]) && ppw.drag.doEdgesCrossOneDimension([a[0].y, a[1].y], [b[0].y, b[1].y])
};
PwylDragSelect.prototype.doEdgesCrossOneDimension = function (a, b) {
    var longer, shorter;
    a = (a[0] <= a[1]) ? a : [a[1], a[0]];
    b = (b[0] <= b[1]) ? b : [b[1], b[0]];
    if ((a[1] - a[0]) >= (b[1] - b[0])) {
        longer = a, shorter = b
    } else {
        longer = b, shorter = a
    }
    return (longer[0] <= shorter[0] && shorter[0] <= longer[1]) || (longer[0] <= shorter[1] && shorter[1] <= longer[1])
};
PwylDragSelect.prototype.insideSelectionBox = function (elem) {
    var posA = YAHOO.util.Dom.getXY(elem);
    var pos = {
        x: posA[0],
        y: posA[1]
    };
    var sbPosA = YAHOO.util.Dom.getXY(ppw.drag.selectBox);
    var sbPos = {
        x: sbPosA[0],
        y: sbPosA[1]
    };
    return ppw.drag.isPointInSelectionBox(pos, sbPos) && ppw.drag.isPointInSelectionBox({
        x: pos.x,
        y: (pos.y + elem.offsetHeight)
    }, sbPos) && ppw.drag.isPointInSelectionBox({
        x: (pos.x + elem.offsetWidth),
        y: pos.y
    }, sbPos) && ppw.drag.isPointInSelectionBox({
        x: (pos.x + elem.offsetWidth),
        y: (pos.y + elem.offsetHeight)
    }, sbPos)
};
PwylDragSelect.prototype.checkForScrolling = function (currCoords, mouseCoords) {
    var scrollW = 0,
        scrollH = 0,
        scrollAmt = 10;
    if (mouseCoords.y <= 20 && currCoords.y > 20) {
        scrollH = scrollAmt * -1
    }
    if ((YAHOO.util.Dom.getViewportHeight() - mouseCoords.y) <= 20 && (ppw.util.pageTop.offsetHeight - currCoords.y) > 20) {
        scrollH = scrollAmt
    }
    if ((mouseCoords.x < (212 + 20)) && (currCoords.x > (212 + 20))) {
        scrollW = scrollAmt * -1
    }
    if ((YAHOO.util.Dom.getViewportWidth() - mouseCoords.x) <= 20 && (ppw.util.pageTop.offsetWidth - currCoords.x) > 20) {
        scrollW = scrollAmt
    }
    if (scrollW != 0 || scrollH != 0) {
        ppw.drag.scrollAmt = {
            w: scrollW,
            h: scrollH
        };
        ppw.drag.scroll()
    } else {
        ppw.drag.scrollAmt = null
    }
};
PwylDragSelect.prototype.scroll = function () {
    if (!ppw.drag.scrollAmt) {
        return
    }
    window.scrollBy(ppw.drag.scrollAmt.w, ppw.drag.scrollAmt.h);
    if (ppw.drag.scrollAmt.h > 0) {
        ppw.drag.increaseSelectBoxSize(ppw.drag.scrollAmt.h, "height")
    }
    if (ppw.drag.scrollAmt.h < 0) {
        ppw.drag.increaseSelectBoxSize(ppw.drag.scrollAmt.h, "height");
        var selectBoxPos = YAHOO.util.Dom.getXY(ppw.drag.selectBox);
        ppw.util.moveElement(ppw.drag.selectBox, selectBoxPos[0], selectBoxPos[1] - Math.abs(ppw.drag.scrollAmt.h))
    }
    if (ppw.drag.scrollAmt.w > 0) {
        ppw.drag.increaseSelectBoxSize(ppw.drag.scrollAmt.w, "width")
    }
    if (ppw.drag.scrollAmt.w < 0) {
        ppw.drag.increaseSelectBoxSize(ppw.drag.scrollAmt.w, "width");
        var selectBoxPos = YAHOO.util.Dom.getXY(ppw.drag.selectBox);
        ppw.util.moveElement(ppw.drag.selectBox, selectBoxPos[0] - Math.abs(ppw.drag.scrollAmt.w), selectBoxPos[1])
    }
    setTimeout("ppw.drag.scroll()", 100);
    if (ppw.drag.selectBoxAtPageEdge()) {
        ppw.drag.scrollAmt = null
    }
};
PwylDragSelect.prototype.increaseSelectBoxSize = function (amount, dimension) {
    var newL = parseInt(ppw.util.getStyle(ppw.drag.selectBox, dimension), 10) + Math.abs(amount);
    ppw.drag.selectBox.style[dimension] = newL + "px"
};
PwylDragSelect.prototype.selectBoxAtPageEdge = function () {
    var sbPos = YAHOO.util.Dom.getXY(ppw.drag.selectBox);
    var atEdge = sbPos[0] <= 212 || (sbPos[0] + ppw.drag.selectBox.offsetWidth) >= ppw.util.pageTop.offsetWidth || sbPos[1] <= 0 || (sbPos[1] + ppw.drag.selectBox.offsetHeight) >= ppw.util.pageTop.offsetHeight;
    return atEdge
};
PwylDragSelect.prototype.isPointerButton = function (event) {
    return (event.which ? event.which : event.button) == 1
};
