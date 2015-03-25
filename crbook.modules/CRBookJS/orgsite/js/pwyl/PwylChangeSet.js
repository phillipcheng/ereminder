function PwylChangeSet() {
    this.doStack = [];
    this.undoStack = [];
    this.resumeCommands;
    this.resumeCallback
}
PwylChangeSet.prototype.doAction = function (command) {
    ppw.init.beforeDo();
    if (command.addPage) {
        switch (command.name) {
            case "Increase Font Size":
            case "Decrease Font Size":
                break;
            default:
                command.doAction()
        }
    } else {
        ppw.editor.fixTop();
        command.doAction()
    }
    if (command.undoable) {
        this.doStack.push(command);
        if (!ppw.print_button) {
            ppw.ui.addToUndoList(command)
        }
        if (ppw.props.editChangeSet) {
            ppw.ui.addToChangeList(command)
        }
    }
    ppw.init.afterDo()
};
PwylChangeSet.prototype.undo = function (change, specificCommand) {
    if (this.doStack.length > 0) {
        ppw.editor.changeCursorStyle("progress");
        if (change == undefined) {
            change = this.doStack.length - 1
        }
        if (!ppw.print_button) {
            ppw.ui.undoListPanel.hide()
        }
        setTimeout("ppw.changeset._undo(" + change + ", " + specificCommand + ")", 0)
    }
};
PwylChangeSet.prototype._undo = function (changeId, specificCommand) {
    ppw.init.beforeUndo();
    if (specificCommand) {
        var command = this.doStack[changeId];
        this.doStack.splice(changeId, 1);
        command.undoAction();
        this.undoStack.push(command);
        if (!ppw.print_button) {
            ppw.changeset.updateChangesLists(changeId)
        }
    } else {
        var numChanges = this.doStack.length - 1;
        for (var i = numChanges; i >= changeId; i--) {
            var command = this.doStack[i];
            this.doStack.splice(i, 1);
            command.undoAction();
            this.undoStack.push(command);
            if (!ppw.print_button) {
                ppw.changeset.updateChangesLists()
            }
        }
    }
    if (ppw.print_button && this.doStack.length == 0) {
        ppw.print_button.showPrintButton()
    }
    ppw.init.afterUndo();
    ppw.editor.changeCursorStyle("auto")
};
PwylChangeSet.prototype.updateChangesLists = function (changeId) {
    ppw.ui.removeFromUndoList(changeId);
    if (ppw.props.editChangeSet) {
        ppw.ui.removeFromChangeList(changeId)
    }
};
PwylChangeSet.prototype.redo = function () {
    if (this.undoStack.length > 0) {
        ppw.editor.changeCursorStyle("progress");
        setTimeout("ppw.changeset._redo()", 0)
    }
};
PwylChangeSet.prototype._redo = function () {
    this.doAction(this.undoStack.pop())
};
PwylChangeSet.prototype.executeUndoAll = function () {
    if (this.doStack.length > 0) {
        ppw.editor.changeCursorStyle("progress");
        setTimeout("ppw.changeset.undoAll()", 0)
    }
};
PwylChangeSet.prototype.undoAll = function () {
    while (this.doStack.length > 0) {
        ppw.changeset._undo()
    }
};
PwylChangeSet.prototype.executeCommandsSet = function (commands, onComplete, addPage) {
    var i = 0,
        currCmd;
    for (i = 0; i < commands.length; i++) {
        currCmd = commands[i];
        currCmd.addPage = addPage;
        if (currCmd.name == "Add Page" || currCmd.name == "Add Next Page") {
            ppw.changeset.resumeCommands = commands.slice(i + 1);
            ppw.changeset.resumeCallback = onComplete;
            ppw.changeset.doAction(currCmd);
            break
        }
        ppw.changeset.doAction(currCmd)
    }
    if (i == commands.length && onComplete) {
        onComplete()
    }
};
PwylChangeSet.prototype.resumeExecutingCommandSetIfRequired = function () {
    if (ppw.changeset.resumeCommands) {
        var cmds = ppw.changeset.resumeCommands;
        var callback = ppw.changeset.resumeCallback;
        ppw.changeset.resumeCommands = null;
        ppw.changeset.resumeCallback = null;
        ppw.changeset.executeCommandsSet(cmds, callback)
    }
};
PwylChangeSet.prototype.getExistingCommandsToApplyToAddedPage = function () {
    var baseCmds = [],
        newCmds = [];
    if (ppw.changeset.doStack.length < 1) {
        return []
    }
    var currCmd = ppw.changeset.doStack[ppw.changeset.doStack.length - 1];
    if (currCmd.name == "Add Page" || currCmd.name == "Add Next Page") {
        baseCmds = ppw.changeset.doStack
    } else {
        if (currCmd.name == "Apply Change Set") {
            baseCmds = currCmd.commands.slice(0, (currCmd.commands.length - ppw.changeset.resumeCommands.length - 1))
        }
    }
    newCmds = ppw.util.filter(baseCmds, function (cmd) {
        return (cmd.name != "Add Page" && cmd.name != "Add Next Page")
    });
    return newCmds
};
PwylChangeSet.prototype.buildChangeSetMultiCommand = function (name, commands, isApplyChanges) {
    var multi = new PwylMultiCommand();
    multi.name = name;
    multi.undoable = !isApplyChanges;
    multi.addPage = isApplyChanges;
    if (!commands) {
        commands = []
    }
    for (var i = 0; i < commands.length; i++) {
        commands[i].undoable = false;
        if (isApplyChanges) {
            commands[i].addPage = true
        }
        multi.setCommand(commands[i])
    }
    return multi
};
PwylChangeSet.prototype.executeInNewPageContext = function (newPage, callback) {
    var realTopId = ppw.util.topId;
    var realTop = ppw.util.top;
    var realPageTopId = ppw.util.pageTopId;
    var realPageTop = ppw.util.pageTop;
    ppw.util.topId = newPage.parentNode.id;
    ppw.util.top = newPage.parentNode;
    if (!newPage.id) {
        newPage.id = "ppw_apply_changes_" + ppw.util.generateUniqueId()
    }
    ppw.util.pageTopId = newPage.id;
    ppw.util.pageTop = newPage;
    ppw.paths.overrideBase = newPage;
    try {
        callback()
    } catch (ex) {
        if (window.console) {
            console.log("Error executing command: " + ex.message)
        }
    }
    ppw.util.topId = realTopId;
    ppw.util.top = realTop;
    ppw.util.pageTopId = realPageTopId;
    ppw.util.pageTop = realPageTop;
    ppw.paths.overrideBase = null
};

