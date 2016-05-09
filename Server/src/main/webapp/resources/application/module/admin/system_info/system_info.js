model.controller("system_infoCtrl", function ($scope, applicationService, className) {

    applicationService.read($scope,"settings",className.settings,1);

    $scope.treeViewListData = {
        dataList : [],
        topField : "name",
        topSubItems : "actionInfos",
        subItemFieldTitle : "info",
        subItemFieldSubItems : "mandatoryFields",
        hideDisabled : true,
        callback : function(item) {

        }
    };

    applicationService.list($scope,"systemServices",className.systemServices , function (item) {
        for (var i=0; i < item.actionInfos.length; i++) {
            var actionItem = item.actionInfos[i];
            actionItem.info = actionItem.type + " : " + actionItem.name;
            for (var j=0; j < actionItem.mandatoryFields.length;  j++) {
                if (actionItem.mandatoryFields[j] === "") {
                    actionItem.mandatoryFields.splice(j,1);
                    continue;
                }

                actionItem.mandatoryFields[j] = {
                    info : actionItem.mandatoryFields[j]
                }
            }
        }
        $scope.treeViewListData.dataList.push(item);
    });
});