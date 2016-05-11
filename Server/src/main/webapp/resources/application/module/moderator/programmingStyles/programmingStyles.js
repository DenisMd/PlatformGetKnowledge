model.controller("programmingStylesCtrl", function ($scope,applicationService,className,$mdDialog) {

    function programmingStylesList() {
        $scope.selectorData.list = [];
        applicationService.list($scope , "editorStyles",className.programmingStyles,function(item){
            $scope.selectorData.list.push(item);
        });
    }

    $scope.selectorData = {
        list        : [],
        tableName   :   "ps_title",
        filters      : [
            {
                title : "name",
                type  : "text",
                field : "name",
                default : true
            }
        ],
        headerNames : [
            {
                name : "id",
                orderBy : true
            },
            {
                name : "name",
                title : "name",
                orderBy : true
            }
        ],
        selectItemCallback : function (item) {
            $scope.currentEStyle = item;
        },
        actions : [
            {
                icon : "fa-plus",
                color : "#46BE28",
                tooltip : "ps_create",
                actionCallback : function (ev){
                    $scope.showDialog(ev,$scope,"createPS.html",function(answer){
                        applicationService.create($scope,"", className.programmingStyles,answer,function(result){
                            $scope.showToast($scope.getResultMessage(result));
                            programmingStylesList();
                        });
                    });
                }
            }
        ],
        deleteOptions : {
            deleteCallback : function (ev,item) {
                $scope.showConfirmDialog(
                    ev,
                    $scope.translate("ps_delete") + " " + item.name,
                    null,
                    'Delete pl',
                    $scope.translate("delete"),
                    $scope.translate("cancel"),
                    function () {
                        applicationService.remove($scope,"",className.programmingStyles,$scope.currentEStyle.id,function (result) {
                            $scope.showToast($scope.getResultMessage(result));
                            programmingStylesList();
                            $scope.currentEStyle = null;
                        });
                    }
                )
            },
            deleteTitle : "ps_delete"
        }
    };

    programmingStylesList();

    $scope.updatePLanguage = function() {
        applicationService.update($scope,"",className.programmingStyles,$scope.currentEStyle,function(result){
            $scope.showToast($scope.getResultMessage(result));
        });
    };

});