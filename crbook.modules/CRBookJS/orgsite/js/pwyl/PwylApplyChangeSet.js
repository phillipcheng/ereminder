function PwylApplyChangeSet() {
    this.appliedChangeSet = null;
    this.newChangeSetNum = null
}
PwylApplyChangeSet.prototype.saveChangeSet = function (key, name, auto_apply) {
    var params = {};
    params.key = key || ppw.server.id;
    params.user_id = ppw.props.proAccountId;
    params.url = ppw.props.pageUrl;
    if (name) {
        params.name = name
    }
    if (auto_apply) {
        params.auto_apply = auto_apply
    }
    if (ppw.props.enforceQuotas) {
        ppw.props.changeSetsSaved++
    }
    params = ppw.acs.serializeCommandObjects(params);
    ppw.ui.completionMessagePanel.showMessage("ppw_completion_message_panel_changeset");
    ppw.server.ajaxCall("/change_sets/save", params, null, "post", {
        forceMultipart: true
    })
};
PwylApplyChangeSet.prototype.serializeCommandObjects = function (params) {
    var changes = ppw.changeset.doStack;
    params.num_changes = changes.length;
    var i = 0,
        j = 0,
        change;
    for (i = 0; i < changes.length; i++) {
        change = changes[i];
        if (change instanceof PwylCommand) {
            change.undoable = true;
            params["change" + j] = encodeURIComponent(change.serialize());
            j++
        } else {
            if (change instanceof PwylMultiCommand) {
                for (var k = 0; k < change.commands.length; k++) {
                    var command = change.commands[k];
                    command.undoable = true;
                    params["change" + j] = encodeURIComponent(command.serialize());
                    j++
                }
                params.num_changes += change.commands.length - 1
            }
        }
    }
    return params
};
PwylApplyChangeSet.prototype.getChangeSetsForUrl = function () {
    var _addChangeSets = function (response) {
        if (response) {
            var changeSetDropdown = document.getElementById("ppw_select_change_set_dropdown");
            var rawChangeSets = response.split(ppw.server.htmlDelimiter);
            var pageTitle = ppw.util.getPageTitle();
            var regexp = new RegExp(pageTitle + "( ?[0-9]+)?");
            var maxChangeSetNum = 0;
            for (var i = 0; i < rawChangeSets.length; i++) {
                var rawChangeSet = rawChangeSets[i];
                var changeSet = eval("(" + rawChangeSet + ")");
                var option = document.createElement("option");
                option.text = changeSet.name;
                option.value = changeSet.id;
                try {
                    changeSetDropdown.add(option, null)
                } catch (ex) {
                    changeSetDropdown.add(option)
                }
                var result = changeSet.name.match(regexp);
                if (result != null) {
                    var changeSetNum = 0;
                    if (result[1] == undefined) {
                        changeSetNum = 1
                    } else {
                        changeSetNum = parseInt(result[1])
                    }
                    if (changeSetNum > maxChangeSetNum) {
                        maxChangeSetNum = changeSetNum
                    }
                }
            }
            if (maxChangeSetNum > 0) {
                ppw.acs.newChangeSetNum = maxChangeSetNum + 1
            }
            if (rawChangeSets.length == 0) {
                ppw.ui.hideChangeSetDropdown()
            }
        } else {
            ppw.ui.hideChangeSetDropdown()
        }
    };
    var params = {
        user_id: ppw.props.proAccountId,
        url: ppw.server.pageUrl.encodedUrl
    };
    ppw.server.ajaxCall("/change_sets/get_for_url", params, _addChangeSets)
};
PwylApplyChangeSet.prototype.getChangeSet = function (callback, changeSetId) {
    ppw.server.ajaxCall("/change_sets/" + changeSetId + "/get", {}, callback)
};
PwylApplyChangeSet.prototype.extractCommandsFromResponse = function (response) {
    var cmds = [],
        cmdsStr, i, cmd;
    if (response) {
        cmdsStr = response.split(ppw.server.htmlDelimiter);
        if (cmdsStr.length > 0) {
            for (i = 0; i < cmdsStr.length; i++) {
                cmd = PwylCommand.deserialize(decodeURIComponent(cmdsStr[i]));
                cmds.push(cmd)
            }
        }
    }
    return cmds
};
PwylApplyChangeSet.prototype.applyChangeSetFromResponse = function (response) {
    if (response) {
        ppw.changeset.undoStack = [];
        var commands = ppw.acs.extractCommandsFromResponse(response);
        var multiCommand = ppw.changeset.buildChangeSetMultiCommand("Apply Change Set", commands, false);
        multiCommand.changeSetId = ppw.acs.appliedChangeSet;
        multiCommand.afterDoAction = function () {
            if (!ppw.print_button) {
                var changeSetSelection = document.getElementById("ppw_select_change_set_dropdown");
                changeSetSelection.disabled = true;
                ppw.ui.setSelfDestructingInfoMessage("Change set applied.");
                ppw.acs.appliedChangeSet = multiCommand.changeSetId
            }
        };
        multiCommand.afterUndoAction = function () {
            ppw.acs.appliedChangeSet = null
        };
        ppw.changeset.doAction(multiCommand)
    }
};
PwylApplyChangeSet.prototype.applyChangeSet = function (changeSetId) {
    ppw.acs.getChangeSet(ppw.acs.applyChangeSetFromResponse, changeSetId);
    this.appliedChangeSet = changeSetId
};
PwylApplyChangeSet.prototype.applyChangeSetForEditing = function (changeSetId) {
    var _applyChangeSet = function (response) {
        ppw.changeset.executeCommandsSet(ppw.acs.extractCommandsFromResponse(response))
    };
    ppw.acs.getChangeSet(_applyChangeSet, changeSetId)
};
PwylApplyChangeSet.prototype.autoApplyChangeSet = function () {
    var _applyChangeSet = function (response) {
        if (response && response.length > 0) {
            ppw.acs.applyChangeSet(response)
        }
    };
    var params = {
        user_id: ppw.props.proAccountId,
        url: ppw.server.pageUrl.encodedUrl
    };
    ppw.server.ajaxCall("/change_sets/get_auto_for_url", params, _applyChangeSet)
};

